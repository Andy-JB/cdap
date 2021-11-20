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

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import io.cdap.cdap.api.dataset.lib.CloseableIterator;
import io.cdap.cdap.spi.data.StructuredRow;
import io.cdap.cdap.spi.data.StructuredTable;
import io.cdap.cdap.spi.data.table.field.Field;
import io.cdap.cdap.spi.data.table.field.Fields;
import io.cdap.cdap.spi.data.table.field.Range;
import io.cdap.cdap.spi.data.transaction.TransactionRunner;
import io.cdap.cdap.spi.data.transaction.TransactionRunners;
import io.cdap.cdap.store.StoreDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Store for tethering data.
 */
public class TetherStore {
  private static final Gson GSON = new GsonBuilder().create();

  private final TransactionRunner transactionRunner;

  @Inject
  TetherStore(TransactionRunner transactionRunner) {
    this.transactionRunner = transactionRunner;
  }

  /**
   * Adds a peer.
   *
   * @param peerInfo peer information
   * @throws IOException if inserting into the table fails
   */
  public void addPeer(PeerInfo peerInfo) {
    TransactionRunners.run(transactionRunner, context -> {
      StructuredTable tetherTable = context.getTable(StoreDefinition.TetherStore.TETHER);
      Collection<Field<?>> fields = new ArrayList<>();
      fields.add(Fields.stringField(StoreDefinition.TetherStore.PEER_NAME_FIELD, peerInfo.getName()));
      fields.add(Fields.stringField(StoreDefinition.TetherStore.PEER_URI_FIELD, peerInfo.getEndpoint()));
      fields.add(Fields.stringField(StoreDefinition.TetherStore.TETHER_STATE_FIELD,
                                    peerInfo.getTetherStatus().toString()));
      fields.add(Fields.longField(StoreDefinition.TetherStore.LAST_CONNECTION_TIME_FIELD, 0L));
      fields.add(Fields.stringField(StoreDefinition.TetherStore.PEER_METADATA_FIELD,
                                    GSON.toJson(peerInfo.getMetadata())));
      tetherTable.upsert(fields);
    });
  }

  /**
   * Updates tether status and last connection time for a peer.
   *
   * @param peerName name of the peer
   * @param tetherStatus status of tether with the peer
   * @throws IOException if updating the table fails
   */
    public void updatePeerStatusAndTimestamp(String peerName, TetherStatus tetherStatus) {
    TransactionRunners.run(transactionRunner, context -> {
      Collection<Field<?>> fields = new ArrayList<>();
      fields.add(Fields.stringField(StoreDefinition.TetherStore.PEER_NAME_FIELD, peerName));
      fields.add(Fields.stringField(StoreDefinition.TetherStore.TETHER_STATE_FIELD, tetherStatus.toString()));
      fields.add(Fields.longField(StoreDefinition.TetherStore.LAST_CONNECTION_TIME_FIELD, System.currentTimeMillis()));
      StructuredTable tetherTable = context.getTable(StoreDefinition.TetherStore.TETHER);
      tetherTable.update(fields);
    });
  }

  /**
   * Updates tether status for a peer.
   *
   * @param peerName name of the peer
   * @param tetherStatus status of tether with the peer
   * @throws IOException if updating the table fails
   */
  public void updatePeerStatus(String peerName, TetherStatus tetherStatus) {
    TransactionRunners.run(transactionRunner, context -> {
      Collection<Field<?>> fields = new ArrayList<>();
      fields.add(Fields.stringField(StoreDefinition.TetherStore.PEER_NAME_FIELD, peerName));
      fields.add(Fields.stringField(StoreDefinition.TetherStore.TETHER_STATE_FIELD, tetherStatus.toString()));
      StructuredTable tetherTable = context.getTable(StoreDefinition.TetherStore.TETHER);
      tetherTable.update(fields);
    });
  }

  /**
   * Updates the last connection timestamp for the peer.
   *
   * @param peerName name of the peer
   * @throws IOException if updating the table fails
   */
  public void updatePeerTimestamp(String peerName) {
    TransactionRunners.run(transactionRunner, context -> {
      Collection<Field<?>> fields = new ArrayList<>();
      fields.add(Fields.stringField(StoreDefinition.TetherStore.PEER_NAME_FIELD, peerName));
      fields.add(Fields.longField(StoreDefinition.TetherStore.LAST_CONNECTION_TIME_FIELD, System.currentTimeMillis()));
      StructuredTable tetherTable = context.getTable(StoreDefinition.TetherStore.TETHER);
      tetherTable.update(fields);
    });
  }

  /**
   * Deletes a peer
   *
   * @param peerName name of the peer
   * @throws IOException if deleting the table fails
   */
  public void deletePeer(String peerName) {
    TransactionRunners.run(transactionRunner, context -> {
      StructuredTable capabilityTable = context.getTable(StoreDefinition.TetherStore.TETHER);
      capabilityTable
        .delete(Collections.singleton(Fields.stringField(StoreDefinition.TetherStore.PEER_NAME_FIELD, peerName)));
    });
  }

  /**
   * Get information about all tethered peers
   *
   * @return information about status of tethered peer
   * @throws IOException if reading from the database fails
   */
  public List<PeerInfo> getPeers() throws IOException {
    List<PeerInfo> peers = new ArrayList<>();
    return TransactionRunners.run(transactionRunner, context -> {
      StructuredTable tetherTable = context
        .getTable(StoreDefinition.TetherStore.TETHER);
      CloseableIterator<StructuredRow> iterator = tetherTable
        .scan(Range.all(), Integer.MAX_VALUE);
      iterator.forEachRemaining(row -> {
        String peerName = row.getString(StoreDefinition.TetherStore.PEER_NAME_FIELD);
        String endpoint = row.getString(StoreDefinition.TetherStore.PEER_URI_FIELD);
        TetherStatus tetherStatus = TetherStatus.valueOf(row.getString(StoreDefinition.TetherStore.TETHER_STATE_FIELD));
        PeerMetadata peerMetadata = GSON.fromJson(row.getString(StoreDefinition.TetherStore.PEER_METADATA_FIELD),
                                                  PeerMetadata.class);
        long lastConnectionTime = row.getLong(StoreDefinition.TetherStore.LAST_CONNECTION_TIME_FIELD);
        peers.add(new PeerInfo(peerName, endpoint, tetherStatus, peerMetadata, lastConnectionTime));
      });
      return peers;
    });
  }

  /**
   * Get information about a peer
   *
   * @return information about status of a peer
   * @throws IOException if reading from the database fails
   * @throws PeerNotFoundException if the peer is not found
   */
  public PeerInfo getPeer(String peerName) {
    return TransactionRunners.run(transactionRunner, context -> {
      StructuredTable tetherTable = context
        .getTable(StoreDefinition.TetherStore.TETHER);
      Range range =  Range.singleton(
        ImmutableList.of(Fields.stringField(StoreDefinition.TetherStore.PEER_NAME_FIELD, peerName)));
      try (CloseableIterator<StructuredRow> iterator = tetherTable.scan(range, Integer.MAX_VALUE)) {
        if (!iterator.hasNext()) {
          throw new PeerNotFoundException(peerName);
        }
        StructuredRow row = iterator.next();
        String endpoint = row.getString(StoreDefinition.TetherStore.PEER_URI_FIELD);
        TetherStatus tetherStatus = TetherStatus.valueOf(row.getString(StoreDefinition.TetherStore.TETHER_STATE_FIELD));
        PeerMetadata peerMetadata = GSON.fromJson(row.getString(StoreDefinition.TetherStore.PEER_METADATA_FIELD),
                                                  PeerMetadata.class);
        long lastConnectionTime = row.getLong(StoreDefinition.TetherStore.LAST_CONNECTION_TIME_FIELD);
        return new PeerInfo(peerName, endpoint, tetherStatus, peerMetadata, lastConnectionTime);

      }
    });
  }

  /**
   * Get tether status for a peer
   *
   * @param peerName name of the peer
   * @return tether status
   * @throws IOException if reading from the database fails
   * @throws PeerNotFoundException if the peer is not found
   */
  public TetherStatus getTetherStatus(String peerName) {
    return TransactionRunners.run(transactionRunner, context -> {
      StructuredTable tetherTable = context
        .getTable(StoreDefinition.TetherStore.TETHER);
      Range range =  Range.singleton(
        ImmutableList.of(Fields.stringField(StoreDefinition.TetherStore.PEER_NAME_FIELD, peerName)));
      try (CloseableIterator<StructuredRow> iterator = tetherTable.scan(range, Integer.MAX_VALUE)) {
        if (!iterator.hasNext()) {
          throw new PeerNotFoundException(peerName);
        }
        return TetherStatus.valueOf(iterator.next().getString(StoreDefinition.TetherStore.TETHER_STATE_FIELD));
      }
    });
  }
}
