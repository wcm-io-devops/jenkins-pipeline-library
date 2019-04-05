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
import static io.wcm.testing.jenkins.pipeline.StepConstants.PROPERTIES
import static io.wcm.testing.jenkins.pipeline.StepConstants.PROPERTIES
import static io.wcm.testing.jenkins.pipeline.StepConstants.STRING
import static io.wcm.testing.jenkins.pipeline.StepConstants.TEXT
import static io.wcm.testing.jenkins.pipeline.StepConstants.UPSTREAM

class JobPropertiesMock {

  LibraryIntegrationTestContext context

  /**
   * Current build parameters
   */
  protected Map params

  JobPropertiesMock(LibraryIntegrationTestContext context) {
    this.context = context

    // set build parameters
    params = [:]
    context.getBinding().setVariable('params', params)

    context.getPipelineTestHelper().registerAllowedMethod(PROPERTIES, [List.class], { List incomingCall -> context.getStepRecorder().record(PROPERTIES, incomingCall) })

    context.getPipelineTestHelper().registerAllowedMethod(BOOLEAN_PARAM, [Map.class], booleanParamCallback)
    context.getPipelineTestHelper().registerAllowedMethod(BUILD_DISCARDER, [Object.class], { Map incomingCall -> context.getStepRecorder().record(BUILD_DISCARDER, incomingCall) })

    context.getPipelineTestHelper().registerAllowedMethod(CHOICE, [Map.class], choiceParamCallback)
    context.getPipelineTestHelper().registerAllowedMethod(CRON, [String.class], cronCallback)

    context.getPipelineTestHelper().registerAllowedMethod(DISABLE_CONCURRENT_BUILDS, [], {
      context.getStepRecorder().record(DISABLE_CONCURRENT_BUILDS, null)
    })

    context.getPipelineTestHelper().registerAllowedMethod(LOG_ROTATOR, [Map.class], {
      Map incomingCall ->
        context.getStepRecorder().record(LOG_ROTATOR, incomingCall)
        return [(LOG_ROTATOR): incomingCall]
    })

    context.getPipelineTestHelper().registerAllowedMethod(PARAMETERS, [List.class], { List incomingCall -> context.getStepRecorder().record(PARAMETERS, incomingCall) })
    context.getPipelineTestHelper().registerAllowedMethod(PIPELINE_TRIGGERS, [List.class], { List incomingCall -> context.getStepRecorder().record(PIPELINE_TRIGGERS, incomingCall) })
    context.getPipelineTestHelper().registerAllowedMethod(POLLSCM, [String.class], pollSCMCallback)

    context.getPipelineTestHelper().registerAllowedMethod(STRING, [Map.class], stringParamCallback)

    context.getPipelineTestHelper().registerAllowedMethod(TEXT, [Map.class], textParamCallback)

    context.getPipelineTestHelper().registerAllowedMethod(UPSTREAM, [Map.class], upstreamCallback)
  }

  /**
   * Mock for boolean parameter
   */
  def booleanParamCallback = {
    Map config ->
      context.getStepRecorder().record(BOOLEAN_PARAM, config)
      return "booleanParam($config)"
  }

  /**
   * Mock for choice parameter
   */
  def choiceParamCallback = {
    Map config ->
      context.getStepRecorder().record(CHOICE, config)
      return "choice($config)"
  }

  /**
   * Mock for string parameter
   */
  def stringParamCallback = {
    Map config ->
      context.getStepRecorder().record(STRING, config)
      return "string($config)"
  }

  /**
   * Mock for text parameter
   */
  def textParamCallback = {
    Map config ->
      context.getStepRecorder().record(TEXT, config)
      return "text($config)"
  }

  /**
   * Callback for pollscm pipeline trigger
   */
  def pollSCMCallback = {
    String config ->
      context.getStepRecorder().record(POLLSCM, config)
      return "pollSCM($config)"
  }

  /**
   * Callback for cron pipeline trigger
   */
  def cronCallback = {
    String config ->
      context.getStepRecorder().record(CRON, config)
      return "cron($config)"
  }

  /**
   * Callback for upstream pipeline trigger
   */
  def upstreamCallback = {
    Map config ->
      context.getStepRecorder().record(UPSTREAM, config)
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
    this.context.getBinding().setVariable("params", params)
  }

}
