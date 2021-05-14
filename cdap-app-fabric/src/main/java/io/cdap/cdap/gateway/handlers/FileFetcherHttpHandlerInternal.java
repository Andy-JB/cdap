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

package io.cdap.cdap.gateway.handlers;

import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.cdap.cdap.common.conf.Constants;
import io.cdap.cdap.common.io.Locations;
import io.cdap.http.AbstractHttpHandler;
import io.cdap.http.BodyProducer;
import io.cdap.http.HttpHandler;
import io.cdap.http.HttpResponder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.twill.filesystem.Location;
import org.apache.twill.filesystem.LocationFactory;
import org.mortbay.log.Log;

import java.io.InputStream;
import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;


/**
 * Internal {@link HttpHandler} for serving file downloading requests.
 * <p>
 * This is currently used by FileLocalizer to download required files from AppFabric
 * in order to launch TwillApplication.
 */
@Singleton
@Path(Constants.Gateway.INTERNAL_API_VERSION_3)
public class FileFetcherHttpHandlerInternal extends AbstractHttpHandler {
  private final LocationFactory locationFactory;

  @Inject
  FileFetcherHttpHandlerInternal(LocationFactory locationFactory) {
    this.locationFactory = locationFactory;
  }

  /**
   * Download the file specified by path based on current {@link LocationFactory}
   *
   * @param request {@link HttpRequest}
   * @param responder {@link HttpResponse}
   */
  @GET
  @Path("/location/**")
  public void download(HttpRequest request, HttpResponder responder) throws Exception {
    String prefix = String.format("%s/location/", Constants.Gateway.INTERNAL_API_VERSION_3);
    String path = request.uri().substring(prefix.length());
    Location location = Locations.getLocationFromAbsolutePath(locationFactory, path);

    if (!location.exists()) {
      responder.sendString(HttpResponseStatus.NOT_FOUND, String.format("File %s not found", path));
      return;
    }

    byte[] buf = new byte[64 * 1024];
    InputStream inputStream = location.getInputStream();
    try {
      responder.sendContent(HttpResponseStatus.OK, new BodyProducer() {
        @Override
        public ByteBuf nextChunk() throws Exception {
          int len = inputStream.read(buf);
          if (len == -1) {
            return Unpooled.EMPTY_BUFFER;
          }
          return Unpooled.copiedBuffer(buf, 0, len);
        }

        @Override
        public void finished() throws Exception {
          Closeables.closeQuietly(inputStream);
        }

        @Override
        public void handleError(@Nullable Throwable cause) {
          Log.warn("Error when sending chunks for path {}", path, cause);
          Closeables.closeQuietly(inputStream);
        }
      }, new DefaultHttpHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM));
    } catch (Exception e) {
      Log.warn("Exception when initiating stream download for path {}", path);
      Closeables.closeQuietly(inputStream);
      throw e;
    }
  }
}