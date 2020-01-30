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

class AnsiblePlaybookIT extends LibraryIntegrationTestBase {

  @Override
  void setUp() throws Exception {
    super.setUp()
  }

  @Test
  void shouldRunAnsibleWithMinimalConfiguration() {
    loadAndExecuteScript("vars/ansible/jobs/ansibleExecPlaybookMinimalTestJob.groovy")
    Map actualPlaybookCall = StepRecorderAssert.assertOnce(StepConstants.ANSIBLE_PLAYBOOK)

    Map expectedPlayBookCall = [
      colorized         : true,
      extras            : '--extra-vars \'{}\'',
      forks             : 5,
      installation      : 'ansible-1.0.0',
      inventory         : 'ansible-inventory',
      limit             : null,
      playbook          : 'ansible-playbook-path',
      skippedTags       : null,
      startAtTask       : null,
      sudo              : false,
      sudoUser          : null,
      tags              : null,
      credentialsId     : null,
      vaultCredentialsId: null,
    ]

    Assert.assertEquals(expectedPlayBookCall, actualPlaybookCall)
  }

  @Test
  void shouldRunAnsibleWithCustomConfiguration() {
    loadAndExecuteScript("vars/ansible/jobs/ansibleExecPlaybookCustomConfigurationTestJob.groovy")
    Map actualPlaybookCall = StepRecorderAssert.assertOnce(StepConstants.ANSIBLE_PLAYBOOK)

    Map expectedPlayBookCall = [
      colorized         : false,
      extras            : '-v --extra-vars \'{"string":"value","boolean":true,"integer":1,"list":[1,2,3,4]}\'',
      forks             : 10,
      installation      : 'ansible-installation-variant2',
      inventory         : 'ansible-inventory-variant2',
      limit             : "ansible-limit-variant2",
      playbook          : 'ansible-playbook-variant2',
      skippedTags       : "ansible-tags-variant2",
      startAtTask       : "ansible-start-at-task-variant2",
      sudo              : true,
      sudoUser          : "ansible-sudo-user-variant2",
      tags              : "ansible-tags-variant2",
      credentialsId     : "ansible-credentials-variant2",
      vaultCredentialsId: "ansible-vault-credentials-id2",
    ]

    Assert.assertEquals(expectedPlayBookCall, actualPlaybookCall)
  }

  @Test
  void shouldInjectBuildParams() {
    this.getJobPropertiesMock().setParams([choiceParam: "choice1", boolParam: true, stringParam: "text"])
    loadAndExecuteScript("vars/ansible/jobs/ansibleExecPlaybookInjectParamsTestJob.groovy")
    Map actualPlaybookCall = StepRecorderAssert.assertOnce(StepConstants.ANSIBLE_PLAYBOOK)


    Map expectedPlayBookCall = [
      colorized         : true,
      extras            : '--extra-vars \'{"param":"value","choiceParam":"choice1","boolParam":true,"stringParam":"text"}\'',
      forks             : 5,
      installation      : 'ansible-inject-params-installation',
      inventory         : 'ansible-inject-params-inventory',
      limit             : null,
      playbook          : 'ansible-inject-params-playbook',
      skippedTags       : null,
      startAtTask       : null,
      sudo              : false,
      sudoUser          : null,
      tags              : null,
      credentialsId     : null,
      vaultCredentialsId: null
    ]

    Assert.assertEquals(expectedPlayBookCall, actualPlaybookCall)
  }

}
