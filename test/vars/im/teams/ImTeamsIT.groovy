/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2020 wcm.io DevOps
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
package vars.im.teams

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import io.wcm.testing.jenkins.pipeline.StepConstants
import org.junit.Assert
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.StepConstants.OFFICE365_CONNECTOR_SEND
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertNone
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOnce

class ImTeamsIT extends LibraryIntegrationTestBase {

  @Override
  void setUp() throws Exception {
    super.setUp()
    this.getBinding().setVariable("TEAMS_WEBHOOK_URL", "https://MOCKED_TEAMS_WEBHOOK_URL")
  }

  @Test
  void shouldUseDefaults() {
    Map expectedTeamsCall = [
        "message"   : "customMessageDefaults",
        "webhookUrl": "https://MOCKED_TEAMS_WEBHOOK_URL",
        "color"     : "customColorDefaults",
      ]

    loadAndExecuteScript("vars/im/teams/jobs/imTeamsDefaultsJob.groovy")
    assertNone(StepConstants.WITH_CREDENTIALS)
    Map actualTeamsCall = assertOnce(OFFICE365_CONNECTOR_SEND)

    Assert.assertEquals(expectedTeamsCall, actualTeamsCall)
  }

  @Test
  void shouldUseCustomWebhookUrl() {
    Map expectedTeamsCall = [
      message    : "customMessage",
      webhookUrl : "https://customWebhookUrl",
      color      : "customColor",
    ]

    loadAndExecuteScript("vars/im/teams/jobs/imTeamsCustomWebhookUrlJob.groovy")
    assertNone(StepConstants.WITH_CREDENTIALS)
    Map actualTeamsCall = assertOnce(OFFICE365_CONNECTOR_SEND)

    Assert.assertEquals(expectedTeamsCall, actualTeamsCall)
  }

  @Test
  void shouldUseCustomWebhookUrlCredential() {
    Map expectedTeamsCall = [
      message   : "customMessage",
      webhookUrl: "https://MOCKED_TEAMS_WEBHOOK_URL",
      color     : "customColor",
    ]

    Map expectedStringCredentialsCall = [
      "credentialsId": "custom-webhookUrl-credential-id",
      "variable": "TEAMS_WEBHOOK_URL"
    ]

    loadAndExecuteScript("vars/im/teams/jobs/imTeamsCustomWebhookUrlCredentialJob.groovy")
    Map actualStringCredentialCall = assertOnce(StepConstants.STRING)
    Map actualTeamsCall = assertOnce(OFFICE365_CONNECTOR_SEND)

    Assert.assertEquals(expectedTeamsCall, actualTeamsCall)
    Assert.assertEquals(expectedStringCredentialsCall, actualStringCredentialCall)
  }

  @Test
  void shouldUseConfigValues() {
    Map expectedTeamsCall = [
      "message"   : "configMessage",
      "webhookUrl": "https://MOCKED_TEAMS_WEBHOOK_URL",
      "color"     : "configColor",
    ]

    Map expectedStringCredentialCall = [
      "credentialsId" : "configWebhookUrl",
      "variable" : "TEAMS_WEBHOOK_URL"
    ]

    loadAndExecuteScript("vars/im/teams/jobs/imTeamsConfigJob.groovy")
    Map actualStringCredentialCall = assertOnce(StepConstants.STRING)
    Map actualTeamsCall = assertOnce(OFFICE365_CONNECTOR_SEND)

    Assert.assertEquals(expectedTeamsCall, actualTeamsCall)
    Assert.assertEquals(expectedStringCredentialCall, actualStringCredentialCall)
  }

}
