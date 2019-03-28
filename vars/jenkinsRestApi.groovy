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
import io.wcm.devops.jenkins.pipeline.model.jenkins.api.Job
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import jenkins.plugins.http_request.ResponseContentSupplier

import java.util.regex.Matcher

/**
 * Returns all jobs from the given jenkins instance which matches the given regex
 *
 * @param regex The regex the jobs must match to
 * @param remoteHostUrl The url of the jenkins instance
 * @param credentialsId The credentials to use for connection
 * @param depth The maximum depth to search (default 3)
 * @return List of jobs of the Jenkins instance that match to the regex
 */
List <Job> findJobsByNameRegex(String regex, String remoteHostUrl, String credentialsId, Integer depth = 3) {
  Logger log = new Logger("jenkinsApi.findJobByRegexName")
  log.info("regex", regex)
  log.info("remoteHostUrl", remoteHostUrl)
  log.info("credentialsId", credentialsId)
  log.info("depth", depth)

  String treeQuery = ""
  for (Integer i = 0; i < depth; i++) {
    if (treeQuery != "") {
      treeQuery = ",$treeQuery"
    }
    treeQuery = "jobs[name,url$treeQuery]"
  }
  String apiUrl = "$remoteHostUrl/api/json?tree=$treeQuery"

  log.info("treeQuery: $treeQuery")
  ResponseContentSupplier response = httpRequest(acceptType: 'APPLICATION_JSON', authentication: credentialsId, timeout: 30, url: apiUrl, consoleLogResponseBody: false, validResponseCodes: '200')
  Map responseJson = readJSON(text: response.getContent())

  Job jenkinsRoot = new Job(responseJson)
  List <Jobs> allJobs = jenkinsRoot.flatten()
  List <Jobs> matchedJobs = []
  Matcher matcher

  for (Job job in allJobs) {
    matcher = job.name =~ regex
    log.debug("matching: '${job.name}'")
    if (matcher) {
      matchedJobs.push(job)
    }
  }

  // reset to null to avoid not serializable issues
  matcher = null

  return matchedJobs
}
