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
  void shouldCheckoutRequirements() {

    File role1MockedResponse = this.context.getDslMock().locateTestResource("tools/ansible/wcm_io_devops.jenkins_pipeline_library.json")
    this.httpRequestPluginMock.mockResponse([url: "https://galaxy.ansible.com/api/v1/roles/?owner__username=wcm_io_devops&name=jenkins_pipeline_library"] ,role1MockedResponse.getText("UTF-8"), 200)

    File role2MockedResponse = this.context.getDslMock().locateTestResource("tools/ansible/wcm_io_devops.jenkins_plugins.json")
    this.httpRequestPluginMock.mockResponse([url: "https://galaxy.ansible.com/api/v1/roles/?owner__username=wcm_io_devops&name=jenkins_plugins",] ,role2MockedResponse.getText("UTF-8"), 200)

    File role3MockedResponse = this.context.getDslMock().locateTestResource("tools/ansible/wcm_io_devops.jenkins_facts.json")
    this.httpRequestPluginMock.mockResponse([url: "https://galaxy.ansible.com/api/v1/roles/?owner__username=wcm_io_devops&name=jenkins_facts",] ,role3MockedResponse.getText("UTF-8"), 200)

    loadAndExecuteScript("vars/ansible/jobs/ansibleCheckoutRequirementsTestJob.groovy")
    List checkoutCalls = StepRecorderAssert.assertStepCalls(StepConstants.CHECKOUT, 5)

    Map expectedCheckoutCall0 = [
        '$class'                           : "GitSCM",
        "branches"                         : [
            ["name": "master"]
        ],
        "doGenerateSubmoduleConfigurations": false,
        "extensions"                       : [
            [$class: 'LocalBranch'],
            [$class: 'RelativeTargetDirectory', relativeTargetDir: 'wcm_io_devops.jenkins_pipeline_library'],
            [$class: 'ScmName', name: 'wcm_io_devops.jenkins_pipeline_library']
        ],
        "submoduleCfg"                     : [],
        "userRemoteConfigs"                : [
            [url: "https://github.com/wcm-io-devops/ansible-jenkins-pipeline-library.git"]
        ]
    ]

    Map expectedCheckoutCall1 = [
      '$class'                           : "GitSCM",
      "branches"                         : [
        ["name": "master"]
      ],
      "doGenerateSubmoduleConfigurations": false,
      "extensions"                       : [
        [$class: 'LocalBranch'],
        [$class: 'RelativeTargetDirectory', relativeTargetDir: 'wcm_io_devops.jenkins_facts'],
        [$class: 'ScmName', name: 'wcm_io_devops.jenkins_facts']
      ],
      "submoduleCfg"                     : [],
      "userRemoteConfigs"                : [
        [url: "https://github.com/wcm-io-devops/ansible-jenkins-facts.git"]
      ]
    ]

    Map expectedCheckoutCall2 = [
        '$class'                           : "GitSCM",
        "branches"                         : [
            ["name": "1.2.0"]
        ],
        "doGenerateSubmoduleConfigurations": false,
        "extensions"                       : [
            [$class: 'LocalBranch'],
            [$class: 'RelativeTargetDirectory', relativeTargetDir: 'wcm_io_devops.jenkins_plugins'],
            [$class: 'ScmName', name: 'wcm_io_devops.jenkins_plugins']
        ],
        "submoduleCfg"                     : [],
        "userRemoteConfigs"                : [
            [url: "https://github.com/wcm-io-devops/ansible-jenkins-plugins.git"]
        ]
    ]

    Map expectedCheckoutCall3 = [
        '$class'                           : "GitSCM",
        "branches"                         : [
            ["name": "master"]
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

    Map expectedCheckoutCall4 = [
        '$class'                           : "GitSCM",
        "branches"                         : [
            ["name": "develop"]
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
    Assert.assertEquals(expectedCheckoutCall4, checkoutCalls.get(4))
  }

  @Test
  void shouldRunAnsibleWithMinimalConfiguration() {
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
  void shouldRunAnsibleWithCustomConfiguration() {
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
  void shouldInjectBuildParams() {
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
  void shouldGetGalaxyRoleInfo() {
    File mockedResponse = this.context.getDslMock().locateTestResource("tools/ansible/wcm_io_devops.jenkins_pipeline_library.json")
    this.httpRequestPluginMock.mockResponse(mockedResponse.getText("UTF-8"), 200)

    JSONObject result = loadAndExecuteScript("vars/ansible/jobs/ansibleGetGalaxyRoleInfoTestJob.groovy")

    StepRecorderAssert.assertOnce(StepConstants.HTTP_REQUEST)
    Assert.assertEquals("wcm-io-devops", result["github_user"])
    Assert.assertEquals("ansible-jenkins-pipeline-library", result["github_repo"])
  }

  @Test
  void shouldNotGetGalaxyRoleInfo() {
    File mockedResponse = this.context.getDslMock().locateTestResource("tools/ansible/not.existingrole.json")
    this.httpRequestPluginMock.mockResponse(mockedResponse.getText("UTF-8"), 200)

    JSONObject result = loadAndExecuteScript("vars/ansible/jobs/ansibleGetGalaxyRoleInfoWithErrorsTestJob.groovy")

    StepRecorderAssert.assertOnce(StepConstants.HTTP_REQUEST)
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
        case "curl --silent 'https://galaxy.ansible.com/api/v1/roles/?owner__username=tecris&name=maven'":
          File mockedResponse = this.context.getDslMock().locateTestResource("tools/ansible/tecris.maven.json")
          return mockedResponse.getText("UTF-8")
          break
        default: throw new Exception()
      }
    }
  }


}
