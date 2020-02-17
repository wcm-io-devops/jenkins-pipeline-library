# notify

This party of the pipeline provide useful notification steps.

With jenkins pipeline the sending of notifications lost some
functionality. For example the
* Still Failing
* Still Unstable and
* Fixed

results are no longer available (at the moment)

The `notify.mail` and `notify.mattermost` steps bring back parts of this convenience.

# Table of contents

* [Build result specific configuration](#build-result-specific-configuration)
* [notify.mail](#notifymattermostmap-config)
  * [Examples](#examples)
  * [Default triggers with attached log and to-recipients](#default-triggers-with-attached-log-and-to-recipients)
  * [Send only on first failure all participating developers](#send-only-on-first-failure-all-participating-developers)
  * [Custom Subject](#custom-subject)
* [notify.mattermost](#notifymattermostmap-config)
* [notify.mqtt](#notifymqttmap-config)

# Build result specific configuration

The `notify.mail` and the `notify.mattermost` step support build result
specific configurations.

With the configuration options for the build status, like

* `ConfigConstants.NOTIFY_ON_SUCCESS`
* `ConfigConstants.NOTIFY_ON_FAILURE`

you have the following configuration options. Simply enable it by
setting the configuration option to `true`, or disable it by setting the
value to `false`.

In more complex scenarios you can specify build result specific
configurations like:

``` groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

notifyMail(
    (NOTIFY) : [
        (NOTIFY_ON_FAILURE) : [
          (NOTIFY_TO) : "build-failure@example.com",
          (NOTIFY_ATTACH_LOG) : true,
        ],
        (NOTIFY_ON_FIXED) : [
          (NOTIFY_TO) : "build-fixed@example.com",
        ]
    ]
)
```

So you are able to configure for each build result custom options. You
can use each non build result specific configuration options again in
these configs maps (because placing a `(NOTIFY_ON_FAILURE)` inside a
`(NOTIFY_ON_FAILURE)` wouldn't make sence).

:bulb: Please be aware that the build result specific
configuration is merged with the "root" configuration! This especially
affects the `ConfigConstants.NOTIFY_RECIPIENT_PROVIDERS` since this is a
list.

# `notify.mail(Map config)`

## Examples

### Default triggers with attached log and to-recipients

``` groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

notifyMail(
    (NOTIFY) : [
        (NOTIFY_ATTACH_LOG): true,
        (NOTIFY_COMPRESS_LOG) : false,
        (NOTIFY_TO): "recipient1@domain.tld,recipient2@domain.tld"
    ]
)
```

### Send only on first failure all participating developers

``` groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

notifyMail(
    (NOTIFY) : [
        (NOTIFY_ATTACH_LOG): true,
        (NOTIFY_COMPRESS_LOG) : false,
        (NOTIFY_ON_ABORT) : false,
        (NOTIFY_ON_FAILURE) : true,
        (NOTIFY_ON_STILL_FAILING) : false,
        (NOTIFY_ON_FIXED) : false,
        (NOTIFY_ON_SUCCESS) : false,
        (NOTIFY_ON_UNSTABLE) : false,
        (NOTIFY_ON_STILL_UNSTABLE) : false,
        (NOTIFY_RECIPIENT_PROVIDERS): [[$class: 'DevelopersRecipientProvider']]
    ]
)
```

### Custom Subject

``` groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

notifyMail(
    (NOTIFY) : [
        (NOTIFY_SUBJECT): 'Custom notification for ${PROJECT_NAME} with status: ${NOTIFICATION_TRIGGER}',
    ]
)
```

:exclamation: Make sure to use single quotes here because environment
variables would otherwise be directly evaluated!

## Generic Configuration support

This step supports the [Generic Configuration](../docs/generic-config.md)
mechanism for loading and applying a FQJN based auto-lookup for the
appropriate configuration options.

:bulb: FQJN = **F**ully-**Q**ualified **J**ob **N**ame =
`${JOB_NAME}@${GIT_BRANCH}`

:bulb: This method of configuration is recommended!

When using this mechanism the step expects a YAML pipeline resource with
the path `resources/jenkins-pipeline-library/notify/mattermost.yaml`.

:bulb: An example for this `mail.yaml` is here:
[`mail.yaml`](../test/resources/jenkins-pipeline-library/config/notify/mail.yaml)

## Configuration options

Complete list of all configuration options.

All configuration options must be inside the `notify` ([`ConfigConstants.NOTIFY`](https://github.com/wcm-io-devops/jenkins-pipeline-library/blob/master/src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)) map element to be
evaluated and used by the step.

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

notifyMail(
    (NOTIFY) : [
        (NOTIFY_ATTACH_LOG): false,
        (NOTIFY_ATTACHMENTS_PATTERN): '',
        (NOTIFY_BODY): null,
        (NOTIFY_COMPRESS_LOG): false,
        (NOTIFY_ENABLED): true,
        (NOTIFY_MIME_TYPE): null,
        (NOTIFY_ON_ABORT): true,
        (NOTIFY_ON_FAILURE): true,
        (NOTIFY_ON_STILL_FAILING): true,
        (NOTIFY_ON_FIXED): true,        
        (NOTIFY_ON_SUCCESS): false,    
        (NOTIFY_ON_UNSTABLE): true,
        (NOTIFY_ON_STILL_UNSTABLE): true,
        (NOTIFY_RECIPIENT_PROVIDERS) : null, 
        (NOTIFY_SUBJECT): '${PROJECT_NAME} - Build # ${BUILD_NUMBER} - ${NOTIFICATION_TRIGGER}',
        (NOTIFY_TO): "recipient@domain.tld"
    ]
)
```

### `attachLog` (optional)
|                                                                                                                     ||
|:---------|:----------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_ATTACH_LOG`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `Boolean`                                                                                                 |
| Default  | `false`                                                                                                   |

Controls if the log should be attached to the mail.

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)


### `attachmentsPattern` (optional)
|                                                                                                                              ||
|:---------|:-------------------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_ATTACHMENTS_PATTERN`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `String`, comma separated list of ANT patterns                                                                     |
| Default  | `''`                                                                                                               |

The pattern(s) for the attachments which should be send along with the email.

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)

### `body` (optional)
|                                                                                                               ||
|:---------|:----------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_BODY`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `String`                                                                                            |
| Default  | `${DEFAULT_CONTENT}`                                                                                |

The body of the mail. The pipeline script assumes that you have a configured email template in place so default values is used `${DEFAULT_CONTENT}`

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)

### `compressLog` (optional)
|                                                                                                                       ||
|:---------|:------------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_COMPRESS_LOG`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `Boolean`                                                                                                   |
| Default  | `false`                                                                                                     |

When set to `true` the log is attached to the mail as compressed zip.

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)

### `enabled` (optional)
|                                                                                                                  ||
|:---------|:-------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_ENABLED`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `Boolean`                                                                                              |
| Default  | `true`                                                                                                 |

Disables the notifications when set to `false`

### `mimeType` (optional)
|                                                                                                                    ||
|:---------|:---------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_MIME_TYPE`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `String`                                                                                                 |
| Default  | `null`                                                                                                   |

The mimeType of the mail. The pipeline script assumes that you have
configured the mimeType in the "Extended E-mail Notification" section of
your Jenkins instance.

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)

### `onAbort` (optional)
|                                                                                                                   ||
|:---------|:--------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_ON_ABORT`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `Boolean` or `Map` with custom configuration                                                            |
| Default  | `false`                                                                                                 |

When set to `true` a notification is send when the job is aborted.

### `onFailure` (optional)
|                                                                                                                     ||
|:---------|:----------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_ON_FAILURE`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `Boolean` or `Map` with custom configuration                                                              |
| Default  | `true`                                                                                                    |

When set to `true` a notification is send when the job swiches to
failure (first failure)

:exclamation: For controlling the behavior when a job failes more than
one time in a row see [onStillFailing](#onstillfailing-optional)

### `onStillFailing` (optional)
|                                                                                                                           ||
|:---------|:----------------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_ON_STILL_FAILING`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `Boolean` or `Map` with custom configuration                                                                    |
| Default  | `true`                                                                                                          |

When set to `true` a notification is send when the job failed and the
previous build failed.

### `onFixed` (optional)
|                                                                                                                   ||
|:---------|:--------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_ON_FIXED`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `Boolean` or `Map` with custom configuration                                                            |
| Default  | `true`                                                                                                  |

When set to `true` a notification is send when the job status switches
from a non successful to successful.

### `onSuccess` (optional)
|                                                                                                                     ||
|:---------|:----------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_ON_SUCCESS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `Boolean` or `Map` with custom configuration                                                              |
| Default  | `false`                                                                                                   |

When set to `true` a notification is send every time a job is
successful.

### `onUnstable` (optional)
|                                                                                                                      ||
|:---------|:-----------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_ON_UNSTABLE`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `Boolean` or `Map` with custom configuration                                                               |
| Default  | `true`                                                                                                     |

When set to `true` a notification is send when the job switches to unstable.

:exclamation: For controlling the behavior when a job is unstable more than
one time in a row see [onStillUnstable](#onstillunstable-optional)

### `onStillUnstable` (optional)
|                                                                                                                            ||
|:---------|:-----------------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_ON_STILL_UNSTABLE`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `Boolean` or `Map` with custom configuration                                                                     |
| Default  | `true`                                                                                                           |

### `recipientProviders` (optional)
|                                                                                                                                                                                                                                                                                          ||
|:---------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_RECIPIENT_PROVIDERS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)                                                                                                                                                             |
| Type     | `List` of `Map` with `RecipientProvider` classes                                                                                                                                                                                                                               |
| Default  | `[[$class: 'CulpritsRecipientProvider'],[$class: 'DevelopersRecipientProvider'],[$class: 'FailingTestSuspectsRecipientProvider'], [$class: 'FirstFailingBuildSuspectsRecipientProvider'],[$class: 'RequesterRecipientProvider'][$class: 'UpstreamComitterRecipientProvider']]` |

The list of recipient providers used to determine who should receive a
notification. Per default all recipent providers (except `ListProvider`)
are used.

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)

### `subject` (optional)
|                                                                                                                  ||
|:---------|:-------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_SUBJECT`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `String`                                                                                               |
| Default  | `${PROJECT_NAME} - Build # ${BUILD_NUMBER} - ${NOTIFICATION_TRIGGER}`                                  |

The subject for the mail.

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)

### `to` (optional)
|                                                                                                             ||
|:---------|:--------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.NOTIFY_TO`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `String`, comma separated list of email adresses                                                  |
| Default  | `null`                                                                                            |

Recipients that should always get a notification. This list has to be a
comma separated String of mail adresses.

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)

# `notify.mattermost(Map config)`

The `notify.mattermost` step uses the
[Mattermost Notification Plugin](https://plugins.jenkins.io/mattermost)
to send build notifications to a mattermost instance using mattermost
webhooks.

## Generic Configuration support

This step supports the [Generic Configuration](../docs/generic-config.md)
mechanism for loading and applying a FQJN based auto-lookup for the
appropriate configuration options.

:bulb: FQJN = **F**ully-**Q**ualified **J**ob **N**ame =
`${JOB_NAME}@${GIT_BRANCH}`

:bulb: This method of configuration is recommended!

When using this mechanism the step expects a YAML pipeline resource with
the path `resources/jenkins-pipeline-library/notify/mattermost.yaml`.

:bulb: An example for this `mattermost.yaml` is here: [`mattermost.yaml`](../test/resources/jenkins-pipeline-library/config/notify/mattermost.yaml)

## Configuration options

Complete list of all configuration options.

All configuration options must be inside the `NOTIFY_MATTERMOST`
([`ConfigConstants.NOTIFY_MATTERMOST`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy))
map element to be evaluated and used by the step.

You have to provide at least a `NOTIFY_MATTERMOST_CHANNEL` and either a
`NOTIFY_MATTERMOST_ENDPOINT` or a credential containing the endpoint by
using `NOTIFY_MATTERMOST_ENDPOINT_CREDENTIAL_ID`.

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

NotificationTriggerHelper triggerHelper = this.getTriggerHelper()

String defaultMattermostMessage = "**${triggerHelper.getTrigger()}** - <${env.BUILD_URL}|${env.JOB_NAME} #${env.BUILD_NUMBER}>\n  <${env.BUILD_URL}console|open console>"
String defaultColor = triggerHelper.getTrigger().getColor()

notify.mattermost( 
  (NOTIFY_MATTERMOST): [
      (NOTIFY_MATTERMOST_ENABLED)               : true,
      (NOTIFY_MATTERMOST_CHANNEL)               : null,
      (NOTIFY_MATTERMOST_ENDPOINT)              : null,
      (NOTIFY_MATTERMOST_ENDPOINT_CREDENTIAL_ID): null,
      (NOTIFY_MATTERMOST_ICON)                  : null,
      (NOTIFY_MATTERMOST_COLOR)                 : defaultColor,
      (NOTIFY_MATTERMOST_TEXT)                  : null,
      (NOTIFY_MATTERMOST_MESSAGE)               : defaultMattermostMessage,
      (NOTIFY_MATTERMOST_FAIL_ON_ERROR)         : false,
      (NOTIFY_ON_ABORT)                         : false,
      (NOTIFY_ON_FAILURE)                       : true,
      (NOTIFY_ON_STILL_FAILING)                 : true,
      (NOTIFY_ON_FIXED)                         : true,
      (NOTIFY_ON_SUCCESS)                       : false,
      (NOTIFY_ON_UNSTABLE)                      : true,
      (NOTIFY_ON_STILL_UNSTABLE)                : true
    ]
)
```

### `enabled` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_ENABLED`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`true`|

Enables / disables mattermost notifications.

### `channel`
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_CHANNEL`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

The channel to post messages to.

### `color` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_COLOR`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`Result.getColor()`|

The color for the message. When using the defaults the color is retrieved from the parsed build result object.
See [Result.groovy](../src/io/wcm/devops/jenkins/pipeline/model/Result.groovy) for the color definition.

### `endpoint`
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_ENDPOINT`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

Configures the mattermost endpoint (e.g. webhook) to use. Overwrites
`endpointCredentialId`/`ConfigConstants.NOTIFY_MATTERMOST_ENDPOINT_CREDENTIAL_ID`
when set. Refer to
[Mattermost Notification Plugin documentation](https://jenkins.io/doc/pipeline/steps/mattermost/)
for more information.

### `endpointCredentialId`
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_ENDPOINT_CREDENTIAL_ID`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

Specifies a secret text (String) credential to use as the Mattermost
endpoint. Will not be used when `endpoint`/`NOTIFY_MATTERMOST_ENDPOINT`
is configured.

### `failOnError`  (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_FAIL_ON_ERROR`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`false`|

Controls if the step will fail when there are issues during sending the
message.

### `icon` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_ICON`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

The icon to use for the message. Refer to
[Mattermost Notification Plugin documentation](https://jenkins.io/doc/pipeline/steps/mattermost/)
for more information.

### `message` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_MESSAGE`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`"${triggerHelper.getTrigger()} - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"`|

The message of the mattermost notification. Refer to
[Mattermost Notification Plugin documentation](https://jenkins.io/doc/pipeline/steps/mattermost/)
for more information.

### `text` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_TEXT`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

Optional text. Refer to
[Mattermost Notification Plugin](https://jenkins.io/doc/pipeline/steps/mattermost/).

# `notify.mqtt(Map config)`

The `notify.mqtt` step is basically a wrapper for the [MQTT Notification
Plugin](https://plugins.jenkins.io/mqtt-notification-plugin).

## Extreme feedback device support

This step is designed to send a mqtt default message that is compatible
with
[wcm_io_devops.jenkins_xfd](https://github.com/wcm-io-devops/ansible-jenkins-xfd)
which displays the build status using hardware from Cleware.

## Generic Configuration support

This step supports the [Generic Configuration](../docs/generic-config.md)
mechanism for loading and applying `SCM_URL`/`JOB_NAME` based
auto-lookup for the appropriate configuration options.

:bulb: This method of configuration is recommended!

When using this mechanism the step expects a YAML pipeline resource with
the path `resources/jenkins-pipeline-library/notify/mqtt.yaml`.

:bulb: An example for this `mqtt.yaml` is here:
[`mqtt.yaml`](../test/resources/jenkins-pipeline-library/config/notify/mqtt.yaml)

## Configuration options

Complete list of all configuration options.

All configuration options must be inside the `notifyMqtt`
([`ConfigConstants.NOTIFY_MQTT`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy))
map element to be evaluated and used by the step.

```groovy
import io.wcm.devops.jenkins.pipeline.utils.NotificationTriggerHelper

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

NotificationTriggerHelper triggerHelper = this.getTriggerHelper()
Result buildResult = triggerHelper.getTrigger()

Integer timestamp = Integer.parseInt(sh(script: "echo \$(date +%s)", returnStdout: true).trim())

String defaultMqttMessage = """\
  BUILD_NUMBER: ${Integer.parseInt(env.getProperty("BUILD_NUMBER"))}
  BUILD_RESULT: '${buildResult.toString()}'
  BUILD_RESULT_COLOR: '${buildResult.getColor()}'
  BUILD_URL: '${env.getProperty("BUILD_URL")}'
  JENKINS_URL: '${env.getProperty("JENKINS_URL")}'
  JOB_BASE_NAME: '${env.getProperty("JOB_BASE_NAME")}'
  JOB_DISPLAY_URL: '${env.getProperty("JOB_DISPLAY_URL")}'
  JOB_NAME: '${env.getProperty("JOB_NAME")}'
  RUN_CHANGES_DISPLAY_URL: '${env.getProperty("RUN_CHANGES_DISPLAY_URL")}'
  TIMESTAMP: ${timestamp}"""

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