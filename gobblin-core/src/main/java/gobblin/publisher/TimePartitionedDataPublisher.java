/* (c) 2015 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 */

package gobblin.publisher;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gobblin.configuration.State;
import gobblin.configuration.WorkUnitState;
import gobblin.util.HadoopUtils;


/**
 * For time partition jobs, writer output directory is
 * $GOBBLIN_WORK_DIR/task-output/{extractId}/{tableName}/{partitionPath},
 * where partition path is the time bucket, e.g., 2015/04/08/15.
 *
 * Publisher output directory is $GOBBLIN_WORK_DIR/job-output/{tableName}/{partitionPath}
 *
 * @author ziliu
 */
public class TimePartitionedDataPublisher extends BaseDataPublisher {

  private static final Logger LOG = LoggerFactory.getLogger(TimePartitionedDataPublisher.class);

  public TimePartitionedDataPublisher(State state) {
    super(state);
  }

  /**
   * This method needs to be overridden for TimePartitionedDataPublisher, since the output folder structure
   * contains timestamp, we have to move the files recursively.
   *
   * For example, move {writerOutput}/2015/04/08/15/output.avro to {publisherOutput}/2015/04/08/15/output.avro
   */
  @Override
  protected void addWriterOutputToExistingDir(Path writerOutput, Path publisherOutput, WorkUnitState workUnitState,
      int branchId) throws FileNotFoundException, IOException {

    for (FileStatus status : HadoopUtils.listStatusRecursive(this.fss.get(branchId), writerOutput)) {
      String filePathStr = status.getPath().toString();
      String pathSuffix =
          filePathStr.substring(filePathStr.indexOf(writerOutput.toString()) + writerOutput.toString().length() + 1);
      Path outputPath = new Path(publisherOutput, pathSuffix);

      if (!this.fss.get(branchId).exists(outputPath.getParent())) {
        this.fss.get(branchId).mkdirs(outputPath.getParent());
      }

      if (this.fss.get(branchId).rename(status.getPath(), outputPath)) {
        LOG.info(String.format("Moved %s to %s", status.getPath(), outputPath));
      } else {
        throw new IOException("Failed to move from " + status.getPath() + " to " + outputPath);
      }
    }
  }
}
