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
package vars.notify.mqtt

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import io.wcm.testing.jenkins.pipeline.StepConstants
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

class NotifyMqttIT extends LibraryIntegrationTestBase {

  @Override
  void setUp() throws Exception {
    super.setUp()
    this.setEnv("JOB_DISPLAY_URL", "MOCKED_JOB_DISPLAY_URL")
    this.setEnv("RUN_CHANGES_DISPLAY_URL", "MOCKED_RUN_CHANGES_DISPLAY_URL")
    this.setEnv("JOB_NAME", "MOCKED_JOB_NAME")
    this.setEnv("JOB_BASE_NAME", "MOCKED_JOB_BASE_NAME")
    this.setEnv("BUILD_NUMBER", "456")
    this.setEnv("BUILD_URL", "MOCKED_BUILD_URL")
    this.setEnv("JENKINS_URL", "MOCKED_JENKINS_URL")
    this.setEnv("JENKINS_URL", "MOCKED_JENKINS_URL")
    this.updateBuildStatus("MOCKED_BUILD_RESULT")
  }

  @Test
  void shouldNotifyMqttWithCustomConfiguration() {
    loadAndExecuteScript("vars/notify/mqtt/jobs/notifyMqttCustomJob.groovy")

    Map mqttNotificationCall = StepRecorderAssert.assertOnce(StepConstants.MQTT_NOTIFICATION)
    Assert.assertEquals([
      brokerUrl    : "tcp://custom-broker:1883",
      credentialsId: "custom-credential-id",
      message      : "custom-message",
      qos          : "1",
      retainMessage: true,
      topic        : "custom-topic"
    ], mqttNotificationCall)
  }

  @Test
  void shouldNotifyMqttWithDefaultConfiguration() {
    loadAndExecuteScript("vars/notify/mqtt/jobs/notifyMqttDefaultsJob.groovy")

    Map mqttNotificationCall = StepRecorderAssert.assertOnce(StepConstants.MQTT_NOTIFICATION)

    this.assertCorrectMessage((String) mqttNotificationCall['message'])

    Assert.assertEquals("defaultbroker", mqttNotificationCall['brokerUrl'])
    Assert.assertEquals("", mqttNotificationCall['credentialsId'])
    Assert.assertEquals("1", mqttNotificationCall['qos'])
    Assert.assertEquals(false, mqttNotificationCall['retainMessage'])
    Assert.assertEquals("jenkins/MOCKED_JOB_NAME", mqttNotificationCall['topic'])
  }

  @Test
  void shouldNotifyMqttWithSpecificYamlConfiguration() {
    this.setEnv("JOB_NAME", "team-a/project-1-compile")
    loadAndExecuteScript("vars/notify/mqtt/jobs/notifyMqttDefaultsJob.groovy")
    Map mqttNotificationCall = StepRecorderAssert.assertOnce(StepConstants.MQTT_NOTIFICATION)

    this.assertCorrectMessage((String) mqttNotificationCall['message'], "team-a/project-1-compile")

    Assert.assertEquals("team-a-broker", mqttNotificationCall['brokerUrl'])
    Assert.assertEquals("team-a-broker-credential-id", mqttNotificationCall['credentialsId'])
    Assert.assertEquals("2", mqttNotificationCall['qos'])
    Assert.assertEquals(true, mqttNotificationCall['retainMessage'])
    Assert.assertEquals("jenkins/team-a/project-1-compile", mqttNotificationCall['topic'])
  }

  void assertCorrectMessage(actualMessage, String expectedJobName = "MOCKED_JOB_NAME") {
    Assert.assertThat(actualMessage, CoreMatchers.containsString("BUILD_NUMBER: 456"))
    Assert.assertThat(actualMessage, CoreMatchers.containsString("BUILD_RESULT: 'FAILURE'"))
    Assert.assertThat(actualMessage, CoreMatchers.containsString("BUILD_RESULT_COLOR: '#f0372e'"))
    Assert.assertThat(actualMessage, CoreMatchers.containsString("BUILD_URL: 'MOCKED_BUILD_URL'"))
    Assert.assertThat(actualMessage, CoreMatchers.containsString("JENKINS_URL: 'MOCKED_JENKINS_URL'"))
    Assert.assertThat(actualMessage, CoreMatchers.containsString("JOB_BASE_NAME: 'MOCKED_JOB_BASE_NAME'"))
    Assert.assertThat(actualMessage, CoreMatchers.containsString("JOB_DISPLAY_URL: 'MOCKED_JOB_DISPLAY_URL'"))
    Assert.assertThat(actualMessage, CoreMatchers.containsString("JOB_NAME: '${expectedJobName}'"))
    Assert.assertThat(actualMessage, CoreMatchers.containsString("RUN_CHANGES_DISPLAY_URL: 'MOCKED_RUN_CHANGES_DISPLAY_URL'"))
  }

}
