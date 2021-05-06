/*
 * Copyright © 2020 Cask Data, Inc.
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

package io.cdap.cdap.internal.app.dispatcher;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import io.cdap.cdap.common.conf.CConfiguration;
import io.cdap.cdap.common.conf.Constants;
import io.cdap.cdap.common.conf.SConfiguration;
import io.cdap.cdap.common.discovery.ResolvingDiscoverable;
import io.cdap.cdap.common.discovery.URIScheme;
import io.cdap.cdap.common.http.CommonNettyHttpServiceBuilder;
import io.cdap.cdap.common.security.HttpsEnabler;
import io.cdap.http.NettyHttpService;
import org.apache.twill.common.Cancellable;
import org.apache.twill.discovery.DiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * A scheduled service that periodically poll for new preview request and execute it.
 */
public class TaskWorkerService extends AbstractIdleService {

  private static final Logger LOG = LoggerFactory.getLogger(TaskWorkerService.class);

  private final CConfiguration cConf;
  private final SConfiguration sConf;
  private final DiscoveryService discoveryService;
  private NettyHttpService httpService;
  private Cancellable cancelDiscovery;
  private InetSocketAddress bindAddress;

  @Inject
  TaskWorkerService(CConfiguration cConf,
                    SConfiguration sConf,
                    DiscoveryService discoveryService) {
    this.cConf = cConf;
    this.sConf = sConf;
    this.discoveryService = discoveryService;
  }

  @Override
  protected void startUp() throws Exception {
    LOG.debug("Starting TaskWorker service");
    launchHttpServer();
    LOG.debug("Starting TaskWorker service has completed");
  }

  @Override
  protected void shutDown() throws Exception {
    LOG.debug("Shutting down TaskWorker service");
    shutdownHttpServer();
    LOG.debug("Shutting down TaskWorker service has completed");
  }

  private void launchHttpServer() throws Exception {
    LOG.info("Starting TaskWorker http server");
    NettyHttpService.Builder builder = new CommonNettyHttpServiceBuilder(cConf, Constants.Service.TASK_WORKER)
      .setHost(cConf.get(Constants.TaskWorker.ADDRESS))
      .setPort(cConf.getInt(Constants.TaskWorker.PORT))
      .setExecThreadPoolSize(cConf.getInt(Constants.TaskWorker.EXEC_THREADS))
      .setBossThreadPoolSize(cConf.getInt(Constants.TaskWorker.BOSS_THREADS))
      .setWorkerThreadPoolSize(cConf.getInt(Constants.TaskWorker.WORKER_THREADS))
      .setHttpHandlers(new TaskWorkerHttpHandlerInternal(this.cConf));

    if (cConf.getBoolean(Constants.Security.SSL.INTERNAL_ENABLED)) {
      new HttpsEnabler().configureKeyStore(cConf, sConf).enable(builder);
    }
    httpService = builder.build();

    httpService.start();
    bindAddress = httpService.getBindAddress();
    cancelDiscovery = discoveryService.register(
      ResolvingDiscoverable.of(URIScheme.createDiscoverable(Constants.Service.TASK_WORKER, httpService)));

    LOG.info("Starting TaskWorker http server has completed on {}", bindAddress);
  }

  private void shutdownHttpServer() throws Exception {
    cancelDiscovery.cancel();
    httpService.stop();
  }
}
