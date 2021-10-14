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

import java.util.List;
import java.util.Map;

/**
 * Tethering request that's sent to the client.
 */
public class TetherCreationRequest extends TetherRequestBase {
  // Server endpoint
  private final String endpoint;
  // CDAP namespaces
  private final List<String> namespaces;
  // Metadata associated with this tether
  private final Map<String, String> metadata;

  public TetherCreationRequest(String instance, String endpoint,
                               List<String> namespaces, Map<String, String> metadata) {
    super(instance);
    this.endpoint = endpoint;
    this.namespaces = namespaces;
    this.metadata = metadata;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public List<String> getNamespaces() {
    return namespaces;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

}
