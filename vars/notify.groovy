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
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import io.wcm.devops.jenkins.pipeline.utils.maps.MapMergeMode
import io.wcm.devops.jenkins.pipeline.utils.maps.MapUtils

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

/**
 * Sends a MQTT notification using the MQTT Notification Plugin
 *
 * @param config The configuration for the step
 */
void mqtt(Map config = [:]) {

  Logger log = new Logger("notify.mqtt")

  String defaultMqttMessage = """\
    JOB_DISPLAY_URL: '${env.getProperty("JOB_DISPLAY_URL")}'
    RUN_CHANGES_DISPLAY_URL: '${env.getProperty("RUN_CHANGES_DISPLAY_URL")}'
    BUILD_RESULT: '${currentBuild.result}'
    JOB_NAME: '${env.getProperty("JOB_NAME")}'
    BUILD_NUMBER: '${env.getProperty("BUILD_NUMBER")}'"""

  Map defaultConfig = [
    (NOTIFY_MQTT): [
      (MAP_MERGE_MODE)            : (MapMergeMode.REPLACE),
      (NOTIFY_MQTT_BROKER)        : null,
      (NOTIFY_MQTT_ENABLED)       : true,
      (NOTIFY_MQTT_CREDENTIALS_ID): '',
      (NOTIFY_MQTT_MESSAGE)       : defaultMqttMessage,
      (NOTIFY_MQTT_QOS)           : "0",
      (NOTIFY_MQTT_RETAIN)        : false,
      (NOTIFY_MQTT_TOPIC)         : "jenkins/${env.getProperty('JOB_NAME')}",
    ]
  ]

  config = MapUtils.merge(defaultConfig, config)

  Map mqttConfig = config[NOTIFY_MQTT]
  Boolean mqttEnabled = mqttConfig[NOTIFY_MQTT_ENABLED]

  if (!mqttEnabled) {
    log.info("mqtt notification is disabled.")
  }

  String broker = mqttConfig[NOTIFY_MQTT_BROKER]

  if (broker.isEmpty()) {
    String msg = "broker (NOTIFY_MQTT_BROKER) needs to be defined"
    log.fatal(msg)
    error(msg)
  }

  String credentialId = mqttConfig[NOTIFY_MQTT_CREDENTIALS_ID]
  String topic = mqttConfig[NOTIFY_MQTT_TOPIC]
  String message = mqttConfig[NOTIFY_MQTT_MESSAGE]
  qos = mqttConfig[NOTIFY_MQTT_QOS]
  Boolean retainMessage = mqttConfig[NOTIFY_MQTT_RETAIN]

  mqttNotification(brokerUrl: broker, credentialsId: credentialId, message: message, qos: qos, retainMessage: retainMessage, topic: topic)
}
