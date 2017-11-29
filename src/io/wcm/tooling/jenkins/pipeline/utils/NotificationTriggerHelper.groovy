/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io DevOps
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
package io.wcm.tooling.jenkins.pipeline.utils

import com.cloudbees.groovy.cps.NonCPS
import hudson.model.Result as JenkinsResult
import io.wcm.tooling.jenkins.pipeline.model.Result

/**
 * Helper class to reimplement the notification options provided by the extmail plugin.
 * This helper makes it possible to detect still failing, still unstable and fixed within pipeline scripts
 */
class NotificationTriggerHelper implements Serializable {

  private static final long serialVersionUID = 1L

  final static String ENV_TRIGGER = "NOTIFICATION_TRIGGER"

  protected Result currentResult = null
  protected Result lastResult = null

  protected Boolean isAborted = false
  protected Boolean isFailure = false
  protected Boolean isUnstable = false
  protected Boolean isSuccess = false
  protected Boolean isStillFailing = false
  protected Boolean isStillUnstable = false
  protected Boolean isFixed = false

  NotificationTriggerHelper(JenkinsResult currentResultObject) {
    this(currentResultObject.toString(), null)
  }

  NotificationTriggerHelper(JenkinsResult currentResultObject, JenkinsResult lastResultObject) {
    this(currentResultObject.toString(), lastResultObject != null ? lastResultObject.toString() : null)
  }

  NotificationTriggerHelper(String currentBuildResult, String lastBuildResult) {
    // fix for declarative pipeline, when currentBuildResult is null the build is SUCCESS
    if (currentBuildResult == null) {
      currentBuildResult = JenkinsResult.SUCCESS.toString()
    }

    this.currentResult = Result.fromString(currentBuildResult)
    this.lastResult = Result.fromString(lastBuildResult)
  }

  /**
   * Checks the current and the previous build result and calculates the trigger for which to send the notification
   *
   * @see Result
   *
   * @return The trigger for the notification
   */
  @NonCPS
  protected Result calculateTrigger() {
    Result trigger
    // reset
    isAborted = false
    isFailure = false
    isUnstable = false
    isSuccess = false
    isStillFailing = false
    isStillUnstable = false
    isFixed = false

    // check if build is still failing
    if (currentResult == lastResult && currentResult == Result.FAILURE) {
      isStillFailing = true
      return Result.STILL_FAILING
    } // check if build is still unstable
    else if (currentResult == lastResult && currentResult == Result.UNSTABLE) {
      isStillUnstable = true
      return Result.STILL_UNSTABLE
    } // check if build is fixed
    else if (currentResult == Result.SUCCESS &&
        lastResult &&
        currentResult.isBetterThan(lastResult) &&
        lastResult.isBetterOrEqualTo(Result.FAILURE)) {
      isFixed = true
      return Result.FIXED
    } // per default set the trigger to the current result
    else {
      trigger = Result.fromString(currentResult.toString())
    }

    // check the result trigger and set the properties
    if (trigger == Result.FAILURE) (
        isFailure = true
    ) else if (trigger == Result.SUCCESS) {
      isSuccess = true
    } else if (trigger == Result.UNSTABLE) {
      isUnstable = true
    } else if (trigger == Result.ABORTED) {
      isAborted = true
    }
    return trigger
  }

  @NonCPS
  String replaceEnvVar(String str, String value) {
    if (str == null || value == null) return null
    return str.replaceAll('\\$\\{' + ENV_TRIGGER + '\\}', value)
  }

  @NonCPS
  Result getTrigger() {
    return calculateTrigger()
  }

  @NonCPS
  Boolean isAborted() {
    return isAborted
  }

  @NonCPS
  Boolean isFailure() {
    return isFailure
  }

  @NonCPS
  Boolean isUnstable() {
    return isUnstable
  }

  @NonCPS
  Boolean isSuccess() {
    return isSuccess
  }

  @NonCPS
  Boolean isStillFailing() {
    return isStillFailing
  }

  @NonCPS
  Boolean isStillUnstable() {
    return isStillUnstable
  }

  @NonCPS
  Boolean isFixed() {
    return isFixed
  }
}
