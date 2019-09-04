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

package vars.ansible

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestContext
import io.wcm.testing.jenkins.pipeline.StepConstants
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert
import org.junit.Assert
import org.junit.Test

class AnsibleIT extends LibraryIntegrationTestBase {

  @Override
  void setUp() throws Exception {
    super.setUp()
  }

  @Test
  void shouldWrapInstallation() {
    loadAndExecuteScript("vars/ansible/jobs/ansibleWrapInstallationTestJob.groovy")

    Map toolCall = StepRecorderAssert.assertOnce(StepConstants.TOOL)
    String shellCall = StepRecorderAssert.assertOnce(StepConstants.SH)
    String withEnvCall = StepRecorderAssert.assertOnce(StepConstants.WITH_ENV)

    Assert.assertEquals([
      name: "ansible-installation",
      type: "org.jenkinsci.plugins.ansible.AnsibleInstallation"
    ], toolCall)

    String expectedAnsibleInstallation = LibraryIntegrationTestContext.TOOL_ANSIBLE_PREFIX + LibraryIntegrationTestContext.TOOL_ANSIBLE

    Assert.assertEquals("[PATH=${expectedAnsibleInstallation}:/usr/bin]".toString(), withEnvCall)
    Assert.assertEquals("ansible --version", shellCall)

  }

}
