# Instant Messaging (im)

This part of the pipeline provide steps for instant messaging.

# Table of contents

* [`im.mattermost()`](#immattermost)
  * [Arguments](#arguments)
    * [`channel`](#channel)
    * [`endpointOrCredentialId`](#endpointorcredentialid)
    * [`config`](#config)
       * [Configuration options](#configuration-options)
  * [Generic Configuration support](#generic-configuration-support)
  * [Examples](#examples)
* [`im.teams()`](#imteams)
  * [Arguments](#arguments)
    * [`webhookUrlOrCredentialId`](#webhookurlorcredentialid)
    * [`config`](#config)
       * [Configuration options](#configuration-options)
  * [Generic Configuration support](#generic-configuration-support)
  * [Examples](#examples)

# `im.mattermost()`

The `im.mattermost` step uses the
[Mattermost Notification Plugin](https://plugins.jenkins.io/mattermost)
to send instant messages to a mattermost instance using mattermost
webhooks.

Signatures:
* `void mattermost(String message, String text = null, String
color = null, String channel = null, String icon = null, String
endpointOrCredentialId, failOnError = false)`
* `void mattermost(Map config)`

## Arguments

Please refer to the step documentation for details as `im.mattermost` is
mostly forwarding the arguments:
https://jenkins.io/doc/pipeline/steps/mattermost/

### `channel`

When `channel` is `null` (default) then an auto-lookup for the channel
using the
[Generic Configuration support](#generic-configuration-support) is
performed.

### `endpointOrCredentialId`

When `endpointOrCredentialId` is `null` (default) then an auto-lookup for the channel
using the
[Generic Configuration support](#generic-configuration-support) is
performed.

When `endpointOrCredentialId` starts with `http://` or `https://` the
value of `endpointOrCredentialId` is used as mattermost endpoint.

When non of the above conditions matches the value of
`endpointOrCredentialId` is used as credential id to retrieve the
endpoint from the Jenkins credential storage

### `config`

If you want you can also use the step with a config map.

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

Map config = [
  (NOTIFY_MATTERMOST): [
    (NOTIFY_MATTERMOST_CHANNEL)               : null,
    (NOTIFY_MATTERMOST_ENDPOINT): null,
    (NOTIFY_MATTERMOST_ENDPOINT_CREDENTIAL_ID): null,
    (NOTIFY_MATTERMOST_ICON)                  : null,
    (NOTIFY_MATTERMOST_COLOR)                 : null,
    (NOTIFY_MATTERMOST_TEXT)                  : null,
    (NOTIFY_MATTERMOST_MESSAGE)               : null,
    (NOTIFY_MATTERMOST_FAIL_ON_ERROR)         : false,
  ]
]

im.mattermost(config)
```
#### Configuration options

Complete list of all configuration options.

All configuration options must be inside the `NOTIFY_MATTERMOST`
([`ConfigConstants.NOTIFY_MATTERMOST`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy))
map element to be evaluated and used by the step.

You have to provide at least a `NOTIFY_MATTERMOST_CHANNEL` and either a
`NOTIFY_MATTERMOST_ENDPOINT` or a credential containing the endpoint by
using `NOTIFY_MATTERMOST_ENDPOINT_CREDENTIAL_ID`.

##### `channel`
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_CHANNEL`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

The channel to post messages to.

##### `color` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_COLOR`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`Result.getColor()`|

The color for the message. When using the defaults the color is retrieved from the parsed build result object.
See [Result.groovy](../src/io/wcm/devops/jenkins/pipeline/model/Result.groovy) for the color definition.

##### `endpoint`
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

##### `endpointCredentialId`
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_ENDPOINT_CREDENTIAL_ID`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

Specifies a secret text (String) credential to use as the Mattermost
endpoint. Will not be used when `endpoint`/`NOTIFY_MATTERMOST_ENDPOINT`
is configured.

##### `failOnError`  (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_FAIL_ON_ERROR`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`false`|

Controls if the step will fail when there are issues during sending the
message.

##### `icon` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_ICON`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

The icon to use for the message. Refer to
[Mattermost Notification Plugin documentation](https://jenkins.io/doc/pipeline/steps/mattermost/)
for more information.

##### `message` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_MESSAGE`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`"${triggerHelper.getTrigger()} - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"`|

The message of the mattermost notification. Refer to
[Mattermost Notification Plugin documentation](https://jenkins.io/doc/pipeline/steps/mattermost/)
for more information.

##### `text` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MATTERMOST_TEXT`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

Optional text. Refer to
[Mattermost Notification Plugin](https://jenkins.io/doc/pipeline/steps/mattermost/).

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

## Examples

```groovy
// message only 
im.mattermost("message")
// message and text
im.mattermost("message","text")
// message, text, channel and icon
im.mattermost("message","text","#00FF00","wcm-io-channel", "https://www.mattermost.org/wp-content/uploads/2016/04/icon.png")
```

# `im.teams()`

The `im.teams` step uses the
[Office365 Connector plugin](https://plugins.jenkins.io/Office-365-Connector/)
to send instant messages to a Microsoft Teams instance using webhooks.

Signatures:
* `void teams(String message = null, String webhookUrlOrCredentialId = null, String color = null)`
* `void teams(Map config)`

## Arguments

Please refer to the step documentation for details as `im.teams` is
mostly forwarding the arguments:
https://jenkins.io/doc/pipeline/steps/Office-365-Connector/

### `webhookUrlOrCredentialId`

When `webhookUrlOrCredentialId` is `null` (default) then an auto-lookup for the webhook URL
using the
[Generic Configuration support](#generic-configuration-support) is
performed.

When `webhookUrlOrCredentialId` starts with `http://` or `https://`, the
value of `webhookUrlOrCredentialId` is used as MS Teams webhook URL.

When none of the above conditions match the value of `webhookUrlOrCredentialId`, 
it is used as credential ID to retrieve the endpoint from the Jenkins credential storage.

### `config`

If you want you can also use the step with a config map.

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

Map config = [
  (NOTIFY_TEAMS): [
    (NOTIFY_TEAMS_MESSAGE)                    : null,
    (NOTIFY_TEAMS_WEBHOOK_URL)                : null,
    (NOTIFY_TEAMS_COLOR)                      : null,
  ]
]

im.teams(config)
```

#### Configuration options

Complete list of all configuration options.

All configuration options must be inside the `NOTIFY_TEAMS`
([`ConfigConstants.NOTIFY_TEAMS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy))
map element to be evaluated and used by the step.

You have to provide at least a `NOTIFY_TEAMS_WEBHOOK_URL`.

### `webhookUrl`
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_TEAMS_WEBHOOK_URL`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

The URL of the webhook that Jenkins needs to send notifications to MS Teams. You will obtain this URL while setting up 
the Jenkins connector in your MS Teams channel. For more information, refer to 
[Microsoft's documentation](https://techcommunity.microsoft.com/t5/microsoft-teams-blog/stay-up-to-date-on-your-build-activities-with-jenkins/ba-p/467440).
When `endpointOrCredentialId` is `null` (default) then an auto-lookup for the channel
using the
[Generic Configuration support](#generic-configuration-support) is
performed.
##### `webhookUrlCredentialId`
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_TEAMS_WEBHOOK_URL_CREDENTIAL_ID`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

Specifies a secret text (String) credential to use as the MS Teams webhook URL.
Will not be used when `endpoint`/`NOTIFY_TEAMS_WEBHOOK_URL`
is configured.

### `message` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_TEAMS_MESSAGE`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

The message of the MS Teams notification. This defaults to `null` since the plugin already provides a pretty detailed
message by default.

### `color` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_TEAMS_COLOR`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`Result.getColor()`|

The color for the message. When using the defaults the color is retrieved from the parsed build result object.
See [Result.groovy](../src/io/wcm/devops/jenkins/pipeline/model/Result.groovy) for the color definition.

## Generic Configuration support

This step supports the [Generic Configuration](../docs/generic-config.md)
mechanism for loading and applying a FQJN based auto-lookup for the
appropriate configuration options.

:bulb: FQJN = **F**ully-**Q**ualified **J**ob **N**ame =
`${JOB_NAME}@${GIT_BRANCH}`

:bulb: This method of configuration is recommended!

When using this mechanism the step expects a YAML pipeline resource with
the path `resources/jenkins-pipeline-library/notify/teams.yaml`.

:bulb: An example for this `teams.yaml` is here: [`teams.yaml`](../test/resources/jenkins-pipeline-library/config/notify/teams.yaml)

## Examples

```groovy
// message only 
im.teams("message")
// message with custom color
im.teams("message", color = "ffffff")
```
