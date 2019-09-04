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
import hudson.AbortException
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorder

import static io.wcm.testing.jenkins.pipeline.StepConstants.CHECKOUT
import static io.wcm.testing.jenkins.pipeline.StepConstants.CHECKOUT
import static io.wcm.testing.jenkins.pipeline.StepConstants.CONFIGFILEPROVIDER
import static io.wcm.testing.jenkins.pipeline.StepConstants.DIR
import static io.wcm.testing.jenkins.pipeline.StepConstants.ERROR
import static io.wcm.testing.jenkins.pipeline.StepConstants.ERROR
import static io.wcm.testing.jenkins.pipeline.StepConstants.FILE_EXISTS
import static io.wcm.testing.jenkins.pipeline.StepConstants.SLEEP
import static io.wcm.testing.jenkins.pipeline.StepConstants.STASH
import static io.wcm.testing.jenkins.pipeline.StepConstants.STASH
import static io.wcm.testing.jenkins.pipeline.StepConstants.STEP
import static io.wcm.testing.jenkins.pipeline.StepConstants.STEP
import static io.wcm.testing.jenkins.pipeline.StepConstants.TIMEOUT
import static io.wcm.testing.jenkins.pipeline.StepConstants.TOOL
import static io.wcm.testing.jenkins.pipeline.StepConstants.UNSTASH
import static io.wcm.testing.jenkins.pipeline.StepConstants.UNSTASH
import static io.wcm.testing.jenkins.pipeline.StepConstants.WITH_ENV
import static io.wcm.testing.jenkins.pipeline.StepConstants.WRITE_FILE
import static io.wcm.testing.jenkins.pipeline.StepConstants.WRITE_FILE

class BasicStepsMock {



  LibraryIntegrationTestContext context

  BasicStepsMock(LibraryIntegrationTestContext context) {
    this.context = context

    this.context.getPipelineTestHelper().registerAllowedMethod(CHECKOUT, [Map.class], { LinkedHashMap incomingCall -> this.context.getStepRecorder().record(CHECKOUT, incomingCall) })

    this.context.getPipelineTestHelper().registerAllowedMethod(DIR, [String.class, Closure.class], dirCallback)

    this.context.getPipelineTestHelper().registerAllowedMethod(ERROR, [String.class], { String incomingCall ->
      context.getStepRecorder().record(ERROR, incomingCall)
      throw new AbortException(incomingCall)
    })

    this.context.getPipelineTestHelper().registerAllowedMethod(FILE_EXISTS, [String.class], fileExistsCallback)

    this.context.getPipelineTestHelper().registerAllowedMethod(SLEEP, [LinkedHashMap.class], { LinkedHashMap incomingCall -> context.getStepRecorder().record(SLEEP, incomingCall) })
    this.context.getPipelineTestHelper().registerAllowedMethod(STEP, [Map.class], { LinkedHashMap incomingCall -> context.getStepRecorder().record(STEP, incomingCall) })
    this.context.getPipelineTestHelper().registerAllowedMethod(STASH, [Map.class], { Map incomingCall -> context.getStepRecorder().record(STASH, incomingCall) })

    this.context.getPipelineTestHelper().registerAllowedMethod(TIMEOUT, [Map.class, Closure.class], timeoutCallback)
    this.context.getPipelineTestHelper().registerAllowedMethod(TOOL, [String.class], toolCallback)
    this.context.getPipelineTestHelper().registerAllowedMethod(TOOL, [Map.class], toolMapCallback)

    this.context.getPipelineTestHelper().registerAllowedMethod(UNSTASH, [Map.class], { Map incomingCall -> context.getStepRecorder().record(UNSTASH, incomingCall) })

    this.context.getPipelineTestHelper().registerAllowedMethod(WITH_ENV, [List.class, Closure.class], withEnvCallback)
    this.context.getPipelineTestHelper().registerAllowedMethod(WRITE_FILE, [Map.class], { Map incomingCall -> context.getStepRecorder().record(WRITE_FILE, incomingCall) })
  }

  def withEnvCallback = {
    List vars, Closure body ->
      List<String> modifiedEnvVars = []
      for (String var in vars) {
        List varParts = var.split("=")
        String varName = varParts[0]
        String varValue = varParts[1]
        modifiedEnvVars.push(varName)
        this.context.getEnvVars().setProperty(varName, varValue)
      }
      body.call()
      // set environment variables
      for (String modifiedEnvVar in modifiedEnvVars) {
        this.context.getEnvVars().setProperty(modifiedEnvVar, null)
      }
  }

  /**
   * Callback for dir step
   */
  def dirCallback = {
    String dir, Closure body ->
      context.getStepRecorder().record(DIR, dir)
      body.run()
  }

  /**
   * Mocks the 'fileExists' step
   *
   * @return true when file exists, false when file does not exist
   */
  def fileExistsCallback = {
    String path ->
      try {
        File file = this.context.getDslMock().locateTestResource(path)
        return file.exists()
      } catch (AbortException ex) {
        return false
      }
  }

  /**
   * Callback for timeout step
   */
  def timeoutCallback = {
    Map params, Closure body ->
      context.getStepRecorder().record(TIMEOUT, params)
      body.run()
  }

  /**
   * Mocks the 'tool' step
   */
  def toolCallback = { String tool ->
    return this.toolMapCallback(name: tool)
  }

  /**
   * Mocks the 'tool' step with named parameters
   */
  def toolMapCallback = { Map toolCfg ->
    String name = toolCfg.name
    String type = toolCfg.type

    if (type != null) {
      context.getStepRecorder().record(TOOL, toolCfg)
    } else {
      context.getStepRecorder().record(TOOL, name)
    }

    switch (name) {
      case LibraryIntegrationTestContext.TOOL_MAVEN:
        return LibraryIntegrationTestContext.TOOL_MAVEN_PREFIX.concat(name)
      case LibraryIntegrationTestContext.TOOL_JDK:
        return LibraryIntegrationTestContext.TOOL_JDK_PREFIX.concat(name)
    }
    return ""
  }
}
