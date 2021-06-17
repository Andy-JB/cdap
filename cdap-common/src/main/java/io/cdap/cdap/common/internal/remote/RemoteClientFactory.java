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

package io.cdap.cdap.common.internal.remote;

import com.google.common.annotations.VisibleForTesting;
import io.cdap.cdap.security.spi.authentication.AuthenticationContext;
import io.cdap.common.http.HttpRequestConfig;
import org.apache.twill.discovery.DiscoveryServiceClient;

import javax.inject.Inject;

/**
 * A factory to create {@link RemoteClient}.
 */
public class RemoteClientFactory {

  private final DiscoveryServiceClient discoveryClient;
  private final AuthenticationContext authenticationContext;

  @Inject @VisibleForTesting
  public RemoteClientFactory(DiscoveryServiceClient discoveryClient, AuthenticationContext authenticationContext) {
    this.discoveryClient = discoveryClient;
    this.authenticationContext = authenticationContext;
  }

  public RemoteClient createRemoteClient(String discoverableServiceName,
                                         HttpRequestConfig httpRequestConfig, String basePath) {
    return new RemoteClient(authenticationContext, discoveryClient, discoverableServiceName, httpRequestConfig,
                            basePath);
  }
}