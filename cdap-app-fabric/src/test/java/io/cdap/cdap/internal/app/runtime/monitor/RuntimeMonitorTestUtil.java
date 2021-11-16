/*
 * Copyright © 2014 Cask Data, Inc.
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

package io.cdap.cdap.internal.app.runtime.monitor;

import io.cdap.cdap.api.common.Bytes;
import io.cdap.cdap.common.app.RunIds;
import io.cdap.cdap.internal.app.store.RunRecordDetail;
import io.cdap.cdap.proto.id.NamespaceId;
import io.cdap.cdap.proto.id.ProgramRunId;
import org.apache.twill.api.RunId;

import java.util.concurrent.TimeUnit;

/**
 * Utility class for runtime monitor tests
 */
public final class RuntimeMonitorTestUtil {

  /**
   * setup and return mock program properties on runrecord builder but use passed namespaceId and runId
   */
  public static RunRecordDetail getMockRunRecordMeta(ProgramRunId programRunId) {
    RunRecordDetail.Builder runRecordBuilder = RunRecordDetail.builder();
    runRecordBuilder.setArtifactId(NamespaceId.DEFAULT.artifact("testArtifact", "1.0").toApiArtifactId());
    runRecordBuilder.setPrincipal("userA");
    RunId runId = RunIds.generate();
    runRecordBuilder.setProgramRunId(programRunId);
    runRecordBuilder.setSourceId(Bytes.toBytes("sourceId"));
    runRecordBuilder.setStartTime(RunIds.getTime(runId, TimeUnit.SECONDS));
    return runRecordBuilder.build();
  }

}
