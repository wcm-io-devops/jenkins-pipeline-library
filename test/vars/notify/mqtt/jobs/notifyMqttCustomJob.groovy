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
package vars.notify.mqtt.jobs

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

/**
 * Runs notify.mqtt step with default configuration
 *
 * @return The script
 */
def execute() {
  Map config = [
    (NOTIFY_MQTT) : [
      (NOTIFY_MQTT_BROKER)        : "tcp://custom-broker:1883",
      (NOTIFY_MQTT_CREDENTIALS_ID): 'custom-credential-id',
      (NOTIFY_MQTT_TOPIC)         : "custom-topic",
      (NOTIFY_MQTT_MESSAGE)       : "custom-message",
      (NOTIFY_MQTT_QOS)           : "1",
      (NOTIFY_MQTT_RETAIN)        : true,
    ]
  ]
  notify.mqtt(config)
}

return this
