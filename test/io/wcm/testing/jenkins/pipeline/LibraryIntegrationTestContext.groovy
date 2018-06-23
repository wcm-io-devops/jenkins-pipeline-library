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
import hudson.model.Run
import io.wcm.devops.jenkins.pipeline.environment.EnvironmentConstants
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorder
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert

import static org.mockito.Mockito.mock

/**
 * Provides context for integration tests
 */
class LibraryIntegrationTestContext {

  public final static String WORKSPACE_PATH = "/path/to/workspace"
  public final static String WORKSPACE_TMP_PATH = WORKSPACE_PATH.concat("@tmp/")

  /**
   * Reference to PipelineTestHelper
   */
  PipelineTestHelper pipelineTestHelper

  /**
   * Utility for recording executed steps
   */
  StepRecorder stepRecorder

  /**
   * Environment
   */
  EnvActionImplMock envVars

  /**
   * Reference to DSL mock object
   */
  DSLMock dslMock

  /**
   * Reference to DSL mock object
   */
  RunWrapperMock runWrapperMock

  /**
   * Reference to DSL mock object
   */
  Binding binding

  LibraryIntegrationTestContext(PipelineTestHelper helper, Binding binding) {
    this.pipelineTestHelper = helper
    this.binding = binding

    // initialize the step recorder
    stepRecorder = new StepRecorder()
    StepRecorderAssert.init(stepRecorder)

    envVars = new EnvActionImplMock(stepRecorder)
    envVars.setProperty("PATH", "/usr/bin")

    // initialize the RunWrapper Mock
    runWrapperMock = new RunWrapperMock(mock(Run))

    // initialize the DSL Mock
    this.dslMock = new DSLMock()

    // give the dslMock the refernce to the pipeline helper to allow access to registered libraries
    dslMock.setHelper(helper)

    // set binding for steps and assign it the the DSL cpsScriptMock
    binding.setVariable("steps", this.dslMock.getMock())

    // set the environment variables
    binding.setVariable('env', envVars)

    // set the workspace
    binding.setVariable(EnvironmentConstants.WORKSPACE, WORKSPACE_PATH)

    // set the currentBuild to the RunWrapper cpsScriptMock
    this.binding.setVariable("currentBuild", runWrapperMock)

    this.dslMock = dslMock
  }
}
