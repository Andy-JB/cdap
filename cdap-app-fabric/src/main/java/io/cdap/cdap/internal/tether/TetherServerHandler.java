/*
 * Copyright © 2021 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.cdap.internal.tether;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.cdap.cdap.api.dataset.lib.CloseableIterator;
import io.cdap.cdap.api.messaging.Message;
import io.cdap.cdap.api.messaging.MessageFetcher;
import io.cdap.cdap.api.messaging.MessagePublisher;
import io.cdap.cdap.api.messaging.TopicAlreadyExistsException;
import io.cdap.cdap.api.messaging.TopicNotFoundException;
import io.cdap.cdap.common.BadRequestException;
import io.cdap.cdap.common.ForbiddenException;
import io.cdap.cdap.common.NotImplementedException;
import io.cdap.cdap.common.conf.CConfiguration;
import io.cdap.cdap.common.conf.Constants;
import io.cdap.cdap.messaging.MessagingService;
import io.cdap.cdap.messaging.TopicMetadata;
import io.cdap.cdap.messaging.context.MultiThreadMessagingContext;
import io.cdap.cdap.proto.id.NamespaceId;
import io.cdap.cdap.proto.id.TopicId;
import io.cdap.cdap.spi.data.transaction.TransactionRunner;
import io.cdap.http.AbstractHttpHandler;
import io.cdap.http.HandlerContext;
import io.cdap.http.HttpResponder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * {@link io.cdap.http.HttpHandler} to manage tethering server v3 REST APIs
 */
@Path(Constants.Gateway.API_VERSION_3)
public class TetherServerHandler extends AbstractHttpHandler {
  private static final Logger LOG = LoggerFactory.getLogger(TetherServerHandler.class);
  private static final Gson GSON = new GsonBuilder().create();
  static final String TETHERING_TOPIC_PREFIX = "tethering_";
  private final CConfiguration cConf;
  private final TetherStore store;
  private final MessagingService messagingService;
  private final MultiThreadMessagingContext messagingContext;
  private final TransactionRunner transactionRunner;

  @Inject
  TetherServerHandler(CConfiguration cConf, TetherStore store, MessagingService messagingService,
                      TransactionRunner transactionRunner) {
    this.cConf = cConf;
    this.store = store;
    this.messagingService = messagingService;
    this.messagingContext = new MultiThreadMessagingContext(messagingService);
    this.transactionRunner = transactionRunner;
  }

  @Override
  public void init(HandlerContext context) {
    super.init(context);
  }

  /**
   * Sends control commands to the client.
   */
  @GET
  @Path("/tethering/controlchannels/{peer}")
  public void connectControlChannel(FullHttpRequest request, HttpResponder responder, @PathParam("peer") String peer,
                                    @QueryParam("messageId") String messageId)
    throws IOException, NotImplementedException, PeerNotFoundException, ForbiddenException, BadRequestException {
    checkTetherServerEnabled();
    store.updatePeerTimestamp(peer);
    TetherStatus tetherStatus = store.getPeer(peer).getTetherStatus();
    if (tetherStatus == TetherStatus.PENDING) {
      throw new PeerNotFoundException(String.format("Peer %s not found", peer));
    } else if (tetherStatus == TetherStatus.REJECTED) {
      responder.sendStatus(HttpResponseStatus.FORBIDDEN);
      throw new ForbiddenException(String.format("Peer %s is not authorized", peer));
    }

    List<TetherControlMessage> commands = new ArrayList<>();
    MessageFetcher fetcher = messagingContext.getMessageFetcher();
    TopicId topic = new TopicId(NamespaceId.SYSTEM.getNamespace(), TETHERING_TOPIC_PREFIX + peer);
    String lastMessageId = messageId;
    try (CloseableIterator<Message> iterator =
           fetcher.fetch(topic.getNamespace(), topic.getTopic(), 1, messageId)) {
      while (iterator.hasNext()) {
        Message message = iterator.next();
        TetherControlMessage controlMessage = GSON.fromJson(message.getPayloadAsString(StandardCharsets.UTF_8),
                                                            TetherControlMessage.class);
        commands.add(controlMessage);
        lastMessageId = message.getId();
      }
    } catch (TopicNotFoundException e) {
      LOG.warn("Received control connection from peer {} that's not tethered", peer);
    } catch (IllegalArgumentException e) {
      throw new BadRequestException(String.format("Invalid message id %s", messageId));
    }

    if (commands.isEmpty()) {
      commands.add(new TetherControlMessage(TetherControlMessage.Type.KEEPALIVE));
    }
    TetherControlResponse tetherControlResponse = new TetherControlResponse(lastMessageId, commands);
    responder.sendJson(HttpResponseStatus.OK, GSON.toJson(tetherControlResponse));
  }

  /**
   * Creates a tether with a client.
   */
  @POST
  @Path("/tethering/connect")
  public void createTether(FullHttpRequest request, HttpResponder responder)
    throws NotImplementedException, IOException {
    checkTetherServerEnabled();

    String content = request.content().toString(StandardCharsets.UTF_8);
    TetherConnectionRequest tetherRequest = GSON.fromJson(content, TetherConnectionRequest.class);
    TopicId topicId = new TopicId(NamespaceId.SYSTEM.getNamespace(),
                                  TETHERING_TOPIC_PREFIX + tetherRequest.getPeer());
    try {
      messagingService.createTopic(new TopicMetadata(topicId, Collections.emptyMap()));
    } catch (TopicAlreadyExistsException e) {
      LOG.warn("Topic {} already exists", topicId);
    } catch (IOException e) {
      LOG.error("Failed to create topic {}", topicId, e);
      throw e;
    }

    try {
      store.getPeer(tetherRequest.getPeer());
      // Peer is already configured, treat this as a no-op.
      responder.sendStatus(HttpResponseStatus.OK);
      return;
    } catch (PeerNotFoundException ignored) {
    }

    // We don't need to keep track of the client metadata on the server side.
    PeerMetadata peerMetadata = new PeerMetadata(tetherRequest.getNamespaceAllocations(), Collections.emptyMap());
    // We don't store the peer endpoint on the server side because the connection is initiated by the client.
    PeerInfo peerInfo = new PeerInfo(tetherRequest.getPeer(), null, TetherStatus.PENDING, peerMetadata);
    try {
      store.addPeer(peerInfo);
    } catch (Exception e) {
      try {
        messagingService.deleteTopic(topicId);
      } catch (Exception ex) {
        e.addSuppressed(ex);
      }
      throw new IOException("Failed to create tether with peer " + tetherRequest.getPeer(), e);
    }
    responder.sendStatus(HttpResponseStatus.OK);
  }

  /**
   * Accepts/rejects the tethering request.
   */
  @POST
  @Path("/tethering/connections/{peer}")
  public void tetherAction(HttpRequest request, HttpResponder responder, @PathParam("peer") String peer,
                           @QueryParam("action") String action)
    throws NotImplementedException, BadRequestException, PeerNotFoundException, IOException {
    TetherStatus tetherStatus;
    switch (action) {
      case "accept":
        tetherStatus = TetherStatus.ACCEPTED;
        break;
      case "reject":
        tetherStatus = TetherStatus.REJECTED;
        break;
      default:
        throw new BadRequestException(String.format("Invalid action: %s", action));
    }
    updateTetherStatus(responder, peer, tetherStatus);
  }

  private void checkTetherServerEnabled() throws NotImplementedException {
    if (!cConf.getBoolean(Constants.Tether.TETHER_SERVER_ENABLE)) {
      throw new NotImplementedException("Tethering is not enabled");
    }
  }

  private void updateTetherStatus(HttpResponder responder, String peer, TetherStatus newStatus)
    throws NotImplementedException, PeerNotFoundException, IOException {
    checkTetherServerEnabled();
    PeerInfo peerInfo = store.getPeer(peer);
    if (peerInfo.getTetherStatus() == TetherStatus.PENDING) {
      store.updatePeerStatus(peerInfo.getName(), newStatus);
    } else {
      LOG.info("Cannot update tether state to {} as current state state is {}",
               newStatus, peerInfo.getTetherStatus());
    }
    responder.sendStatus(HttpResponseStatus.OK);
  }

  // Currently unused.
  // TODO: Send RUN_PIPELINE message to peer.
  private void publishMessage(TopicId topicId, @Nullable TetherControlMessage message)
    throws IOException, TopicNotFoundException {
    MessagePublisher publisher = messagingContext.getMessagePublisher();
    try {
      publisher.publish(topicId.getNamespace(), topicId.getTopic(), StandardCharsets.UTF_8,
                        GSON.toJson(message));
    } catch (IOException | TopicNotFoundException e) {
      LOG.error("Failed to publish to topic {}", topicId, e);
      throw e;
    }
  }
}
