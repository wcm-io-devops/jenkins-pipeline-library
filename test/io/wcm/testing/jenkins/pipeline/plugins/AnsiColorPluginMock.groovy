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
package io.wcm.testing.jenkins.pipeline.plugins

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestContext

import static io.wcm.testing.jenkins.pipeline.StepConstants.ANSI_COLOR

class AnsiColorPluginMock {

  LibraryIntegrationTestContext context

  AnsiColorPluginMock(LibraryIntegrationTestContext context) {
    this.context = context

    // add callbacks for DSL functions and pass them to the step recorder if necessary
    context.getPipelineTestHelper().registerAllowedMethod(ANSI_COLOR, [String.class, Closure.class], ansiColorCallback)
  }

  /**
   * Mocks the 'configFileProvider' step. For each ManagedFile the environment variable is set to a dummy filepath
   */
  def ansiColorCallback = {
    String colorMode, Closure body ->
      context.getStepRecorder().record(ANSI_COLOR, colorMode)
      this.context.getEnvVars().setProperty('TERM', colorMode)
      body.run()
      this.context.getEnvVars().setProperty('TERM', null)
  }

}
