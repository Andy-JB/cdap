/*
 * Copyright © 2015 Cask Data, Inc.
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

package io.cdap.cdap.internal.app.runtime.artifact;

import io.cdap.cdap.common.ConflictException;
import io.cdap.cdap.common.id.Id;

/**
 * Thrown when there is a write conflict adding an artifact, such as when multiple writers are trying to
 * write the same artifact at the same time.
 */
public class WriteConflictException extends ConflictException {

  public WriteConflictException(Id.Artifact artifactId) {
    super(String.format("Write conflict while writing artifact %s.", artifactId));
  }

}
