# Instant Messaging (im)

This part of the pipeline provide steps for instant messaging.

# Table of contents

* [`im.mattermost()`](#immattermost)
  * [Arguments](#arguments)
    * [channel](#channel)
    * [endpointOrCredentialId](#endpointorcredentialid)
  * [Generic Configuration support](#generic-configuration-support)
  * [Examples](#examples)

# `im.mattermost()`

The `im.mattermost` step uses the
[Mattermost Notification Plugin](https://plugins.jenkins.io/mattermost)
to send instant messages to a mattermost instance using mattermost
webhooks.

Complete signature: `void mattermost(String message, String text = null, String color = null, String channel = null, String icon = null, String endpointOrCredentialId, failOnError = false)`

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