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

import static io.wcm.testing.jenkins.pipeline.StepConstants.CONFIGFILEPROVIDER
import static io.wcm.testing.jenkins.pipeline.StepConstants.WITH_ENV

class BasicStepsMock {

/**
 * Reference to PipelineTestHelper
 */
  protected PipelineTestHelper helper

  /**
   * Utility for recording executed steps
   */
  protected StepRecorder stepRecorder

  /**
   * Environment
   */
  protected EnvActionImplMock envVars

  BasicStepsMock(PipelineTestHelper helper, StepRecorder stepRecorder, EnvActionImplMock envVars) {
    this.helper = helper
    this.stepRecorder = stepRecorder
    this.envVars = envVars

    helper.registerAllowedMethod(WITH_ENV, [List.class, Closure.class], withEnvCallback)
  }

  def withEnvCallback = {
    List vars, Closure body ->
      List<String> modifiedEnvVars = []
      for (String var in vars) {
        List varParts = var.split("=")
        String varName = varParts[0]
        String varValue = varParts[1]
        modifiedEnvVars.push(varName)
        this.envVars.setProperty(varName, varValue)
      }
      body.call()
      // set environment variables
      for (String modifiedEnvVar in modifiedEnvVars) {
        this.envVars.setProperty(modifiedEnvVar, null)
      }
  }
}
