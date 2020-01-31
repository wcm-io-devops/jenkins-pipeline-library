/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2020 wcm.io DevOps
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
package io.wcm.devops.jenkins.pipeline.config

import io.wcm.devops.jenkins.pipeline.environment.EnvironmentConstants
import io.wcm.devops.jenkins.pipeline.environment.EnvironmentUtils
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger

class GenericConfigUtils implements Serializable {

  private static final long serialVersionUID = 1L

  transient Script script

  GroovyObjectSupport envActionImpl

  EnvironmentUtils envUtils

  Logger log = new Logger(this)

  GenericConfigUtils(Script script) {
    this.script = script
    this.envActionImpl = script.env
    envUtils = new EnvironmentUtils(script)
  }

  /**
   * Utility function for getting the Fully-Qualified Job Name
   * which consists out of JOB_URL@GIT_BRANCH
   *
   * @return The fully qualified job name
   */
  String getFQJN() {
    String ret = envActionImpl.getProperty(EnvironmentConstants.JOB_NAME)

    if (envUtils.hasEnvVar(EnvironmentConstants.GIT_BRANCH, false)) {
      String gitBranch = this.envActionImpl.getProperty(EnvironmentConstants.GIT_BRANCH)
      gitBranch = gitBranch.replace("origin/", "")
      ret = "${ret}@${gitBranch}"
    }

    return ret
  }

}
