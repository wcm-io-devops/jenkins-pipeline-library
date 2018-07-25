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

class AnsibleIT extends LibraryIntegrationTestBase {

  @Override
  void setUp() throws Exception {
    super.setUp()
    helper.registerAllowedMethod(StepConstants.SH, [Map.class], shellMapCallback)
  }

  @Test
  public void shouldCheckoutRequirements() {
    loadAndExecuteScript("vars/ansible/jobs/ansibleCheckoutRequirementsTestJob.groovy")
    List checkoutCalls = StepRecorderAssert.assertStepCalls(StepConstants.CHECKOUT, 4)

    Map expectedCheckoutCall0 = [
        '$class'                           : "GitSCM",
        "branches"                         : [
            ["name": "*/master"]
        ],
        "doGenerateSubmoduleConfigurations": false,
        "extensions"                       : [
            [$class: 'LocalBranch'],
            [$class: 'RelativeTargetDirectory', relativeTargetDir: 'williamyeh.oracle-java'],
            [$class: 'ScmName', name: 'williamyeh.oracle-java']
        ],
        "submoduleCfg"                     : [],
        "userRemoteConfigs"                : [
            [url: "https://github.com/William-Yeh/ansible-oracle-java.git"]
        ]
    ]

    Map expectedCheckoutCall1 = [
        '$class'                           : "GitSCM",
        "branches"                         : [
            ["name": "*/v3.5.2"]
        ],
        "doGenerateSubmoduleConfigurations": false,
        "extensions"                       : [
            [$class: 'LocalBranch'],
            [$class: 'RelativeTargetDirectory', relativeTargetDir: 'tecris.maven'],
            [$class: 'ScmName', name: 'tecris.maven']
        ],
        "submoduleCfg"                     : [],
        "userRemoteConfigs"                : [
            [url: "https://github.com/tecris/ansible-maven.git"]
        ]
    ]

    Map expectedCheckoutCall2 = [
        '$class'                           : "GitSCM",
        "branches"                         : [
            ["name": "*/master"]
        ],
        "doGenerateSubmoduleConfigurations": false,
        "extensions"                       : [
            [$class: 'LocalBranch'],
            [$class: 'RelativeTargetDirectory', relativeTargetDir: 'aem-cms'],
            [$class: 'ScmName', name: 'aem-cms']
        ],
        "submoduleCfg"                     : [],
        "userRemoteConfigs"                : [
            [url: "https://github.com/wcm-io-devops/ansible-aem-cms.git"]
        ]
    ]

    Map expectedCheckoutCall3 = [
        '$class'                           : "GitSCM",
        "branches"                         : [
            ["name": "*/develop"]
        ],
        "doGenerateSubmoduleConfigurations": false,
        "extensions"                       : [
            [$class: 'LocalBranch'],
            [$class: 'RelativeTargetDirectory', relativeTargetDir: 'aem-service'],
            [$class: 'ScmName', name: 'aem-service']
        ],
        "submoduleCfg"                     : [],
        "userRemoteConfigs"                : [
            [url: "https://github.com/wcm-io-devops/ansible-aem-service.git"]
        ]
    ]

    Assert.assertEquals(expectedCheckoutCall0, checkoutCalls.get(0))
    Assert.assertEquals(expectedCheckoutCall1, checkoutCalls.get(1))
    Assert.assertEquals(expectedCheckoutCall2, checkoutCalls.get(2))
    Assert.assertEquals(expectedCheckoutCall3, checkoutCalls.get(3))
  }

  @Test
  public void shouldRunAnsibleWithMinimalConfiguration() {
    loadAndExecuteScript("vars/ansible/jobs/ansibleExecPlaybookMinimalTestJob.groovy")
    Map actualPlaybookCall = StepRecorderAssert.assertOnce(StepConstants.ANSIBLE_PLAYBOOK)

    Map expectedPlayBookCall = [
        colorized    : true,
        extras       : '--extra-vars \'{}\'',
        forks        : 5,
        installation : 'ansible-1.0.0',
        inventory    : 'ansible-inventory',
        limit        : null,
        playbook     : 'ansible-playbook-path',
        skippedTags  : null,
        startAtTask  : null,
        sudo         : false,
        sudoUser     : null,
        tags         : null,
        credentialsId: null,
    ]

    Assert.assertEquals(expectedPlayBookCall, actualPlaybookCall)
  }

  @Test
  public void shouldRunAnsibleWithCustomConfiguration() {
    loadAndExecuteScript("vars/ansible/jobs/ansibleExecPlaybookCustomConfigurationTestJob.groovy")
    Map actualPlaybookCall = StepRecorderAssert.assertOnce(StepConstants.ANSIBLE_PLAYBOOK)

    Map expectedPlayBookCall = [
        colorized    : false,
        extras       : '-v --extra-vars \'{"string":"value","boolean":true,"integer":1,"list":[1,2,3,4]}\'',
        forks        : 10,
        installation : 'ansible-installation-variant2',
        inventory    : 'ansible-inventory-variant2',
        limit        : "ansible-limit-variant2",
        playbook     : 'ansible-playbook-variant2',
        skippedTags  : "ansible-tags-variant2",
        startAtTask  : "ansible-start-at-task-variant2",
        sudo         : true,
        sudoUser     : "ansible-sudo-user-variant2",
        tags         : "ansible-tags-variant2",
        credentialsId: "ansible-credentials-variant2",
    ]

    Assert.assertEquals(expectedPlayBookCall, actualPlaybookCall)
  }

  @Test
  public void shouldInjectBuildParams() {
    this.getJobPropertiesMock().setParams([choiceParam: "choice1", boolParam: true, stringParam: "text"])
    loadAndExecuteScript("vars/ansible/jobs/ansibleExecPlaybookInjectParamsTestJob.groovy")
    Map actualPlaybookCall = StepRecorderAssert.assertOnce(StepConstants.ANSIBLE_PLAYBOOK)


    Map expectedPlayBookCall = [
        colorized    : true,
        extras       : '--extra-vars \'{"param":"value","choiceParam":"choice1","boolParam":true,"stringParam":"text"}\'',
        forks        : 5,
        installation : 'ansible-inject-params-installation',
        inventory    : 'ansible-inject-params-inventory',
        limit        : null,
        playbook     : 'ansible-inject-params-playbook',
        skippedTags  : null,
        startAtTask  : null,
        sudo         : false,
        sudoUser     : null,
        tags         : null,
        credentialsId: null,
    ]

    Assert.assertEquals(expectedPlayBookCall, actualPlaybookCall)
  }

  @Test
  public void shouldGetGalaxyRoleInfo() {
    JSONObject result = loadAndExecuteScript("vars/ansible/jobs/ansibleGetGalaxyRoleInfoTestJob.groovy")
    Assert.assertEquals("William-Yeh", result["github_user"])
    Assert.assertEquals("ansible-oracle-java", result["github_repo"])
  }

  @Test
  public void shouldNotGetGalaxyRoleInfo() {
    JSONObject result = loadAndExecuteScript("vars/ansible/jobs/ansibleGetGalaxyRoleInfoWithErrorsTestJob.groovy")
    Assert.assertNull(result)
  }

  // cpsScriptMock the curl command
  def shellMapCallback = { Map incomingCommand ->
    context.getStepRecorder().record(StepConstants.SH, incomingCommand)
    Boolean returnStdout = incomingCommand.returnStdout ?: false
    String script = incomingCommand.script ?: ""
    // return default values for several commands
    if (returnStdout) {
      switch (script) {
        case "curl --silent 'https://galaxy.ansible.com/api/v1/roles/?owner__username=williamyeh&name=oracle-java'":
          File mockedResponse = this.context.getDslMock().locateTestResource("tools/ansible/williamyeh.oracle-java.json")
          return mockedResponse.getText("UTF-8")
          break
        case "curl --silent 'https://galaxy.ansible.com/api/v1/roles/?owner__username=tecris&name=maven'":
          File mockedResponse = this.context.getDslMock().locateTestResource("tools/ansible/tecris.maven.json")
          return mockedResponse.getText("UTF-8")
          break
        default: throw new Exception()
      }
    }
  }


}
