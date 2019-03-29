/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2019 wcm.io DevOps
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.devops.jenkins.pipeline.model.jenkins.api

import io.wcm.devops.jenkins.pipeline.utils.logging.Logger

/**
 * Job Model for Jenkins REST API.
 */
class Job implements Serializable {

  Logger log = new Logger(this)

  private static final long serialVersionUID = 1L

  String name

  String url

  String _class

  List <Job> jobs

  Map data

  Job(Map data) {
    this.data = data
    this.url = data.url ?: null
    this.name = data.name ?: null
    this._class = data._class ?: null
    this.jobs = []
    List rawJobs = data.jobs ?: []
    for (Map rawJobData in rawJobs) {
      Job job = new Job(rawJobData)
      this.jobs.push(job)
    }
  }

  List<Job> flatten() {
    List<Job> ret = [this]
    for (Job job in jobs) {
      ret += job.flatten()
    }
    return ret
  }
}
