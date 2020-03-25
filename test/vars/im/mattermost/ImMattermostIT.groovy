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
package vars.im.mattermost

import hudson.model.Result
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import io.wcm.testing.jenkins.pipeline.StepConstants
import org.junit.Assert
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.StepConstants.MATTERMOST_SEND
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertNone
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOnce

class ImMattermostIT extends LibraryIntegrationTestBase {

  @Override
  void setUp() throws Exception {
    super.setUp()
    this.getBinding().setVariable("MATTERMOST_ENDPOINT", "https://MOCKED_MATTERMOST_ENDPOINT")
  }

  @Test
  void shouldUseDefaults() {
    Map expectedMattermostCall = [
      message    : "customMessageDefaults",
      text       : "customTextDefaults",
      color      : "customColorDefaults",
      channel    : "jenkins-build-notifications",
      icon       : null,
      endpoint   : "https://MOCKED_MATTERMOST_ENDPOINT",
      failOnError: false
    ]

    loadAndExecuteScript("vars/im/mattermost/jobs/imMattermostDefaultsJob.groovy")
    assertNone(StepConstants.WITH_CREDENTIALS)
    Map actualMattermostCall = assertOnce(MATTERMOST_SEND)

    Assert.assertEquals(expectedMattermostCall, actualMattermostCall)
  }

  @Test
  void shouldUseCustomEndpoint() {
    Map expectedMattermostCall = [
      message    : "customEndpointMessage",
      text       : "customEndpointText",
      color      : "customEndpointColor",
      channel    : "customEndpointChannel",
      icon       : "customEndpointIcon",
      endpoint   : "https://customEndpoint",
      failOnError: true
    ]

    loadAndExecuteScript("vars/im/mattermost/jobs/imMattermostCustomEndpointJob.groovy")
    assertNone(StepConstants.WITH_CREDENTIALS)
    Map actualMattermostCall = assertOnce(MATTERMOST_SEND)

    Assert.assertEquals(expectedMattermostCall, actualMattermostCall)
  }

  @Test
  void shouldUseCustomEndpointCredential() {
    Map expectedMattermostCall = [
      message    : "customCredentialIdMessage",
      text       : "customCredentialIdText",
      color      : "customCredentialIdColor",
      channel    : "customCredentialIdChannel",
      icon       : "customCredentialIdIcon",
      endpoint   : "https://MOCKED_MATTERMOST_ENDPOINT",
      failOnError: true
    ]

    Map expectedStringCredentialsCall = [
      "credentialsId" : "custom-endpoint-credential-id",
      "variable" : "MATTERMOST_ENDPOINT"
    ]

    loadAndExecuteScript("vars/im/mattermost/jobs/imMattermostCustomEndpointCredentialJob.groovy")
    Map actualStringCredentialCall = assertOnce(StepConstants.STRING)
    Map actualMattermostCall = assertOnce(MATTERMOST_SEND)

    Assert.assertEquals(expectedMattermostCall, actualMattermostCall)
    Assert.assertEquals(expectedStringCredentialsCall, actualStringCredentialCall)
  }

  @Test
  void shouldUseConfigValues() {
    Map expectedMattermostCall = [
      message    : "configMessage",
      text       : "configText",
      color      : "configColor",
      channel    : "configChannel",
      icon       : "configIcon",
      endpoint   : "https://MOCKED_MATTERMOST_ENDPOINT",
      failOnError: true
    ]

    Map expectedStringCredentialsCall = [
      "credentialsId" : "configEndpointCredentialId",
      "variable" : "MATTERMOST_ENDPOINT"
    ]

    loadAndExecuteScript("vars/im/mattermost/jobs/imMattermostConfigJob.groovy")
    Map actualStringCredentialCall = assertOnce(StepConstants.STRING)
    Map actualMattermostCall = assertOnce(MATTERMOST_SEND)

    Assert.assertEquals(expectedMattermostCall, actualMattermostCall)
    Assert.assertEquals(expectedStringCredentialsCall, actualStringCredentialCall)
  }

}
