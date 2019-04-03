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

package io.cdap.cdap.api.data.batch;

import io.cdap.cdap.api.dataset.DataSetException;

/**
 * This interface is implemented by a dataset if at the end of a batch job (MapReduce, Spark, ...)
 * the output needs to be committed, or rolled back, depending on success of the job. This is
 * similar to what Hadoop's OutputCommitter does, however, this is dataset-centric and does not
 * assume any dependencies on Hadoop.
 */
public interface DatasetOutputCommitter {

  /**
   * Called if the job completed successfully.
   */
  void onSuccess() throws DataSetException;

  /**
   * Called if the job failed.
   */
  void onFailure() throws DataSetException;
}
