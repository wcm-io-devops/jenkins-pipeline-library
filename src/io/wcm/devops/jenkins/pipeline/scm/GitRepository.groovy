/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2018 wcm.io DevOps
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
package io.wcm.devops.jenkins.pipeline.scm

import com.cloudbees.groovy.cps.NonCPS
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger

import java.util.regex.Matcher

class GitRepository {

  public static final PROTOCOL_SSH = "ssh"
  public static final PROTOCOL_HTTP = "http"
  public static final PROTOCOL_HTTPS = "https"

  Logger log = new Logger(this)

  String url = null
  String group = null
  String project = null
  String projectName = null
  String protocol = null
  String server = null
  Script script = null

  GitRepository(Script script, String url) {
    this.script = script
    this.url = url
    Matcher matcher = url =~ /(https?:\\/\\/|(?:ssh:\\/\\/)?\w+@)((?:[\w.\-_]+)(?::\d+)?)(?:\\/|:)((?:[\w-_\\/]*))\\/(.*)/
    if (matcher) {
      List matches = matcher[0]

      // parse protocol
      String protocolMatch = matches[0]
      if (protocolMatch.matches(/^(ssh:\/\/)?git@.*/)) {
        this.protocol = PROTOCOL_SSH
      } else if (protocolMatch.matches(/^https:\/\/.*$/)) {
        this.protocol = PROTOCOL_HTTPS
      } else if (protocolMatch.matches(/^http:\/\/.*$/)) {
        this.protocol = PROTOCOL_HTTP
      }

      this.server = matches[2]
      this.group = matches[3]
      this.project = matches[4]
      this.projectName = project.replace(".git", "")
    } else {
      this.script.steps.error("Error during parsing provided url: '$url'")
    }
    log.debug("url", url)
    log.debug("group", group)
    log.debug("project", project)
    log.debug("projectName", projectName)
    log.debug("protocol", protocol)
    log.debug("server", server)
    log.debug("matcher", matcher)
    matcher = null
  }

  @NonCPS
  Boolean isSsh() {
    return this.protocol == PROTOCOL_SSH
  }

  @NonCPS
  Boolean isHttp() {
    return this.protocol == PROTOCOL_HTTP
  }

  @NonCPS
  Boolean isHttps() {
    return this.protocol == PROTOCOL_HTTPS
  }

  @NonCPS
  Boolean isValid() {
    return (
      this.url != null &&
        this.projectName != null &&
        this.protocol != null &&
        this.project != null &&
        this.server != null &&
        this.group != null)
  }
}
