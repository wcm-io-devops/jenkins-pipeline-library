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
package io.wcm.testing.jenkins.pipeline

import com.lesfurets.jenkins.unit.PipelineTestHelper
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorder

import static io.wcm.testing.jenkins.pipeline.StepConstants.BOOLEAN_PARAM
import static io.wcm.testing.jenkins.pipeline.StepConstants.BUILD_DISCARDER
import static io.wcm.testing.jenkins.pipeline.StepConstants.CHOICE
import static io.wcm.testing.jenkins.pipeline.StepConstants.CRON
import static io.wcm.testing.jenkins.pipeline.StepConstants.DISABLE_CONCURRENT_BUILDS
import static io.wcm.testing.jenkins.pipeline.StepConstants.DISABLE_CONCURRENT_BUILDS
import static io.wcm.testing.jenkins.pipeline.StepConstants.LOG_ROTATOR
import static io.wcm.testing.jenkins.pipeline.StepConstants.PARAMETERS
import static io.wcm.testing.jenkins.pipeline.StepConstants.PARAMETERS
import static io.wcm.testing.jenkins.pipeline.StepConstants.PIPELINE_TRIGGERS
import static io.wcm.testing.jenkins.pipeline.StepConstants.PIPELINE_TRIGGERS
import static io.wcm.testing.jenkins.pipeline.StepConstants.POLLSCM
import static io.wcm.testing.jenkins.pipeline.StepConstants.STRING
import static io.wcm.testing.jenkins.pipeline.StepConstants.TEXT
import static io.wcm.testing.jenkins.pipeline.StepConstants.UPSTREAM

class JobPropertiesMock {

  /**
   * Reference to PipelineTestHelper
   */
  protected PipelineTestHelper helper

  /**
   * Utility for recording executed steps
   */
  protected StepRecorder stepRecorder

  /**
   * Reference to the binding
   */
  protected Binding binding

  /**
   * Current build parameters
   */
  protected Map params

  JobPropertiesMock(PipelineTestHelper helper, StepRecorder stepRecorder, Binding binding) {
    this.helper = helper
    this.stepRecorder = stepRecorder
    this.binding = binding

    // set build parameters
    params = [:]
    binding.setVariable('params', params)

    helper.registerAllowedMethod(BOOLEAN_PARAM, [Map.class], booleanParamCallback)
    helper.registerAllowedMethod(BUILD_DISCARDER, [Object.class], { Map incomingCall -> stepRecorder.record(BUILD_DISCARDER, incomingCall) })

    helper.registerAllowedMethod(CHOICE, [Map.class], choiceParamCallback)
    helper.registerAllowedMethod(CRON, [String.class], cronCallback)

    helper.registerAllowedMethod(DISABLE_CONCURRENT_BUILDS, [], {
      stepRecorder.record(DISABLE_CONCURRENT_BUILDS, null)
    })

    helper.registerAllowedMethod(LOG_ROTATOR, [Map.class], {
      Map incomingCall ->
        stepRecorder.record(LOG_ROTATOR, incomingCall)
        return [(LOG_ROTATOR): incomingCall]
    })

    helper.registerAllowedMethod(PARAMETERS, [List.class], { List incomingCall -> stepRecorder.record(PARAMETERS, incomingCall) })
    helper.registerAllowedMethod(PIPELINE_TRIGGERS, [List.class], { List incomingCall -> stepRecorder.record(PIPELINE_TRIGGERS, incomingCall) })
    helper.registerAllowedMethod(POLLSCM, [String.class], pollSCMCallback)

    helper.registerAllowedMethod(STRING, [Map.class], stringParamCallback)

    helper.registerAllowedMethod(TEXT, [Map.class], textParamCallback)

    helper.registerAllowedMethod(UPSTREAM, [Map.class], upstreamCallback)
  }

  /**
   * Mock for boolean parameter
   */
  def booleanParamCallback = {
    Map config ->
      stepRecorder.record(BOOLEAN_PARAM, config)
      return "booleanParam($config)"
  }

  /**
   * Mock for choice parameter
   */
  def choiceParamCallback = {
    Map config ->
      stepRecorder.record(CHOICE, config)
      return "choice($config)"
  }

  /**
   * Mock for string parameter
   */
  def stringParamCallback = {
    Map config ->
      stepRecorder.record(STRING, config)
      return "string($config)"
  }

  /**
   * Mock for text parameter
   */
  def textParamCallback = {
    Map config ->
      stepRecorder.record(TEXT, config)
      return "text($config)"
  }

  /**
   * Callback for pollscm pipeline trigger
   */
  def pollSCMCallback = {
    String config ->
      stepRecorder.record(POLLSCM, config)
      return "pollSCM($config)"
  }

  /**
   * Callback for cron pipeline trigger
   */
  def cronCallback = {
    String config ->
      stepRecorder.record(CRON, config)
      return "cron($config)"
  }

  /**
   * Callback for upstream pipeline trigger
   */
  def upstreamCallback = {
    Map config ->
      stepRecorder.record(UPSTREAM, config)
      return "upstream($config)"
  }

  /**
   * @return The current build parameters
   */
  Map getParams() {
    return params
  }

  /**
   * Sets the current build parameters
   *
   * @param params
   */
  void setParams(Map params) {
    this.params = params
    this.binding.setVariable("params", params)
  }

}
