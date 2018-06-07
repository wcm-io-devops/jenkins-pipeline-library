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

import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import org.jenkinsci.plugins.pipeline.modeldefinition.Utils
import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException

/**
 * Conditionally executes a stage and marks it as skipped if supported
 *
 * @param stageName The name of the stage
 * @param condition The condition
 * @param throwException Controls if the RejectedAccessException will be thrown
 * @param body The stage body
 */
void call(String stageName, Boolean condition, Boolean throwException = true, Closure body) {
  Logger log = new Logger("conditionalStage")
  stage(stageName) {
    def config = [:]
    body.resolveStrategy = Closure.OWNER_FIRST
    body.delegate = config

    if (condition) {
      log.debug("condition evaluated to true, executing stage '$stageName'")
      body()
    } else {
      log.debug("condition evaluated to false, skipping stage ''$stageName''")
      try {
        Utils.markStageSkippedForConditional(stageName)
      } catch (RejectedAccessException ex) {
        log.warn("The stage '$stageName' was skipped, but the the Jenkins sandbox does not allow to mark the stage as skipped. You can approve this signature below ${JENKINS_URL}scriptApproval.")
        if (throwException) {
          throw ex
        }
      }
    }
  }
}
