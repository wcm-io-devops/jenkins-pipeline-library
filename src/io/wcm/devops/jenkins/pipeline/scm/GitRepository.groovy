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

/**
 * Model for a GIT repository
 */
class GitRepository {

  public static final PROTOCOL_SSH = "ssh"
  public static final PROTOCOL_HTTP = "http"
  public static final PROTOCOL_HTTPS = "https"

  Logger log = new Logger(this)

  /**
   * The repository url
   */
  String url = null

  /**
   * The GIT group, like 'wcm-io-devops'
   */
  String group = null

  /**
   * The project, like 'jenkins-pipeline-library.git'
   */
  String project = null

  /**
   * The project name, like 'jenkins-pipeline-library'
   */
  String projectName = null

  /**
   * The protocol, either ssh, http or https
   */
  String protocol = null

  /**
   * The parsedProtocolString
   */
  String protocolPrefix = null

  /**
   * The server, like 'github.com'
   */
  String server = null

  /**
   * Stores a reference to the pipeline script
   */
  Script script = null

  /**
   * Stores the username
   */
  String username = null

  /**
   *
   * @param script Reference to the pipeline script
   * @param url The GIT repository url to parse
   */
  GitRepository(Script script, String url) {
    this.script = script
    this.url = url
    Matcher matcher = url =~ /(https?:\\/\\/|(?:ssh:\\/\\/)?)(?:([^@\/]+)@)?((?:[\w.\-_]+)(?::\d+)?)(?:\\/|:)((?:[\w-_\\/]*))\\/(.*)/
    if (matcher) {
      List matches = matcher[0]

      // parse protocol
      String protocolMatch = matches[0]
      if (protocolMatch.matches(/^(ssh:\/\/)?[^@\/]*@.*/)) {
        this.protocol = PROTOCOL_SSH
      } else if (protocolMatch.matches(/^https:\/\/.*$/)) {
        this.protocol = PROTOCOL_HTTPS
      } else if (protocolMatch.matches(/^http:\/\/.*$/)) {
        this.protocol = PROTOCOL_HTTP
      }

      this.protocolPrefix = matches[1] != "" ? matches[1] : null

      this.username = matches[2]
      this.server = matches[3]
      this.group = matches[4]
      this.project = matches[5]
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

  /**
   * @return true when protocol is SSH
   */
  @NonCPS
  Boolean isSsh() {
    return this.protocol == PROTOCOL_SSH
  }

  /**
   * @return true when protocol is HTTP
   */
  @NonCPS
  Boolean isHttp() {
    return this.protocol == PROTOCOL_HTTP
  }

  /**
   * @return true when protocol is HTTPS
   */
  @NonCPS
  Boolean isHttps() {
    return this.protocol == PROTOCOL_HTTPS
  }

  /**
   * @returns The url composed out of the parsed parts
   */
  @NonCPS
  String getUrl() {
    String ret = ""
    // add a protocol prefix if present (e.g. ssh://, http:// or https://
    if (protocolPrefix != null) {
      ret += "${protocolPrefix}"
    }
    if (isHttp() || isHttps()) {
      // add a username if present
      if (username != null) {
        ret += "${username}@"
      }
      // add the server, group and project
      ret += "${server}/${group}/${project}"
    } else {
      ret += "${username}@${server}"
      // check if server has a port configuration and add only a slash, otherwise a double colon
      if (server.contains(":")) {
        ret += "/"
      } else {
        ret += ":"
      }
      ret += "${group}/${project}"
    }
    return ret
  }
/**
   * @return true when parsed GIT repository is valid
   */
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
