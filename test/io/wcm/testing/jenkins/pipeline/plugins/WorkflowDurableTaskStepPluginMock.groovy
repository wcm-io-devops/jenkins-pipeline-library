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

import static io.wcm.testing.jenkins.pipeline.StepConstants.SH

class WorkflowDurableTaskStepPluginMock {

  LibraryIntegrationTestContext context

  WorkflowDurableTaskStepPluginMock(LibraryIntegrationTestContext context) {
    this.context = context
    context.getPipelineTestHelper().registerAllowedMethod(SH, [String.class], { String incomingCommand -> context.getStepRecorder().record(SH, incomingCommand) })
    context.getPipelineTestHelper().registerAllowedMethod(SH, [Map.class], shellMapCallback)
  }

  /**
   * Mocks the 'sh' step when executed with named arguments (Map)
   * Used to cpsScriptMock some shell commands executed during integration testing
   *
   * @return A dummy response depending on the incoming command
   */
  def shellMapCallback = { Map incomingCommand ->
    context.getStepRecorder().record(SH, incomingCommand)
    Boolean returnStdout = incomingCommand.returnStdout ?: false
    Boolean returnStatus = incomingCommand.returnStatus ?: false
    String script = incomingCommand.script ?: ""
    // return default values for several commands
    if (returnStdout) {
      switch (script) {
        case "git config remote.origin.url": return "http://remote.origin.url/group/project.git"
          break
        case "git rev-parse HEAD": return "0HFGC0"
          break
        case "git branch": return "* (detached from 0HFGC0)"
          break
        case "mvn -f path/to/returnStdout.xml": return "stdout from maven"
          break
        default: return ""
      }
    }
    else if (returnStatus) {
      switch (script) {
        case "mvn -f path/to/returnStatus.xml": return 42
          break
        default: return 0
      }
    }
  }

}
