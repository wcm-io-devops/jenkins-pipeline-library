# notify

This party of the pipeline provide useful notification steps.

# Table of contents

* [notify.mqtt(Map config)](#notifymqttmap-config)
  * [Configuration options](#configuration-options)
    * [`broker` (mandatory)](#broker-mandatory)
    * [`enabled` (optional)](#enabled-optional)
    * [`credentialsId` (optional)](#credentialsid-optional)
    * [`message` (optional)](#message-optional)
    * [`qos` (optional)](#qos-optional)
    * [`retain` (optional)](#retain-optional)
    * [`topic` (optional)](#topic-optional)


## `notify.mqtt(Map config)`

The `notify.mqtt` step is basically a wrapper for the [MQTT Notification
Plugin](https://plugins.jenkins.io/mqtt-notification-plugin).

### Configuration options

Complete list of all configuration options.

All configuration options must be inside the `notifyMqtt`
([`ConfigConstants.NOTIFY_MQTT`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy))
map element to be evaluated and used by the step.

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

String defaultMqttMessage = """\
  JOB_DISPLAY_URL: '${env.getProperty("JOB_DISPLAY_URL")}'
  RUN_CHANGES_DISPLAY_URL: '${env.getProperty("RUN_CHANGES_DISPLAY_URL")}'
  BUILD_RESULT: '${currentBuild.result}'
  JOB_NAME: '${env.getProperty("JOB_NAME")}'
  BUILD_NUMBER: '${env.getProperty("BUILD_NUMBER")}'"""

notify.mqtt( 
  (NOTIFY_MQTT) : [
    (NOTIFY_MQTT_BROKER)        : null,
    (NOTIFY_MQTT_CREDENTIALS_ID): '',
    (NOTIFY_MQTT_ENABLED)       : true,
    (NOTIFY_MQTT_MESSAGE)       : defaultMqttMessage,
    (NOTIFY_MQTT_QOS)           : "0",
    (NOTIFY_MQTT_RETAIN)        : false,
    (NOTIFY_MQTT_TOPIC)         : "jenkins/${env.getProperty('JOB_NAME')}",
  ]
)
```

The minimal configuration is as follows:

```groovy
Map config = [
  (NOTIFY_MQTT) : [
    (NOTIFY_MQTT_BROKER) : "tcp://localhost:1883"
  ]
]
notify.mqtt(config)
```

### `broker` (mandatory)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MQTT_BROKER`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

This setting is mandatory.
***Example:***
```groovy
Map config = [
  (NOTIFY_MQTT) : [
    (NOTIFY_MQTT_BROKER) : "tcp://localhost:1883"
  ]
]
```

### `credentialsId` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MQTT_CREDENTIALS_ID`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`''`|

Specifies the username/password credentials to use.

### `enabled` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MQTT_ENABLED`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`true`|

Enables/Disables the notifications.

### `message` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MQTT_MESSAGE`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default| see below|

Specifies the MQTT message to send, default:

```groovy
String defaultMqttMessage = """\
  JOB_DISPLAY_URL: '${env.getProperty("JOB_DISPLAY_URL")}'
  RUN_CHANGES_DISPLAY_URL: '${env.getProperty("RUN_CHANGES_DISPLAY_URL")}'
  BUILD_RESULT: '${currentBuild.result}'
  JOB_NAME: '${env.getProperty("JOB_NAME")}'
  BUILD_NUMBER: '${env.getProperty("BUILD_NUMBER")}'"""
```

### `qos` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MQTT_QOS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|'0'|

Specifies the MQTT qos to use.

### `retain` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MQTT_RETAIN`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|'false'|

Sets the message retain option.

### `topic` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MQTT_TOPIC`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`jenkins/${env.getProperty('JOB_NAME')}`|

Specifies the MQTT topic to send.