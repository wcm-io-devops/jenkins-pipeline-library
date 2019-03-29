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

import java.util.regex.Matcher

/**
 * Returns all jobs from the given jenkins instance which matches the given regex
 *
 * @param remote Map containing the definition for the remote, example
 *
 *    [
 *      regex: 'regularExpression',
 *      baseUrl: 'https://jenkins.example.org',
 *      credentialId: 'org.example.jenkins.credentials',
 *      depth: 3
 *    ]
 *
 * @return List of jobs of the Jenkins instance that match to the regex
 */
List<Job> findJobsByNameRegex(Map remote) {
  Logger log = new Logger("jenkinsApi.findJobByRegexName")
  log.debug("remote", remote)

  String regex = remote.regex
  String baseUrl = remote.baseUrl
  String credentialsId = remote.credentialsId
  Integer depth = remote.depth ?: 3

  String treeQuery = ""
  for (Integer i = 0; i < depth; i++) {
    if (treeQuery != "") {
      treeQuery = ",$treeQuery"
    }
    treeQuery = "jobs[name,url$treeQuery]"
  }
  String apiUrl = "$baseUrl/api/json?tree=$treeQuery"

  def response = httpRequest(acceptType: 'APPLICATION_JSON', authentication: credentialsId, timeout: 30, url: apiUrl, consoleLogResponseBody: false, validResponseCodes: '200')
  Map responseJson = readJSON(text: response.getContent())

  Job jenkinsRoot = new Job(responseJson)
  List<Job> allJobs = jenkinsRoot.flatten()
  List<Job> matchedJobs = []
  Matcher matcher

  for (Job job in allJobs) {
    matcher = job.name =~ regex
    if (matcher) {
      matchedJobs.push(job)
    }
  }

  // reset to null to avoid not serializable issues
  matcher = null

  return matchedJobs
}
