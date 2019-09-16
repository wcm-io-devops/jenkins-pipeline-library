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
import io.wcm.testing.jenkins.pipeline.StepConstants
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert
import net.sf.json.JSONObject
import org.junit.Assert
import org.junit.Test

class AnsibleInstallRolesIT extends LibraryIntegrationTestBase {

  @Override
  void setUp() throws Exception {
    super.setUp()
  }

  @Test
  void shouldInstallRolesDefault() {

    loadAndExecuteScript("vars/ansible/jobs/ansibleInstallRolesDefaultTestJob.groovy")
    Map toolCall = StepRecorderAssert.assertOnce(StepConstants.TOOL)
    String shellCall = StepRecorderAssert.assertOnce(StepConstants.SH)

    Assert.assertEquals([
      name: "ansible-installation",
      type: "org.jenkinsci.plugins.ansible.AnsibleInstallation"
    ],toolCall)
    Assert.assertEquals("ansible-galaxy install -r tools/ansible/requirements.yml",shellCall)
  }

  @Test
  void shouldInstallRolesCustom() {

    loadAndExecuteScript("vars/ansible/jobs/ansibleInstallRolesCustomTestJob.groovy")
    Map toolCall = StepRecorderAssert.assertOnce(StepConstants.TOOL)
    String shellCall = StepRecorderAssert.assertOnce(StepConstants.SH)

    Assert.assertEquals([
      name: "ansible-installation",
      type: "org.jenkinsci.plugins.ansible.AnsibleInstallation"
    ],toolCall)
    Assert.assertEquals("ansible-galaxy install -r tools/ansible/requirements.yml --force",shellCall)
  }

}
