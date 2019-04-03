/*
 * Copyright © 2016 Cask Data, Inc.
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

package io.cdap.cdap.gateway.handlers.log;

import io.cdap.cdap.logging.gateway.handlers.LogData;
import io.cdap.cdap.logging.read.LogOffset;
import com.google.common.base.Objects;

/**
 * Test Log object that contains {@link LogData} and {@link LogOffset}.
 */
class LogDataOffset extends OffsetLine {
  private final LogData log;

  LogDataOffset(LogData log, LogOffset offset) {
    super(offset);
    this.log = log;
  }

  public LogData getLog() {
    return log;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("log", log)
      .add("offset", getOffset())
      .toString();
  }
}
