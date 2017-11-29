# notifyMail

With jenkins pipeline the sending of mail notification lost some
functionality. For example the
* Still Failing
* Still Unstable and
* Fixed

results are no longer available (at the moment)

The `notifyMail` step brings back parts of this convenience.

# Table of contents
* [Examples](#examples)
  * [Default triggers with attached log and to-recipients](#default-triggers-with-attached-log-and-to-recipients)
  * [Send only on first failure all participating developers](#send-only-on-first-failure-all-participating-developers)
  * [Custom Subject](#custom-subject)
* [Configuration Options](#configuration-options)
  * [attachLog](#attachlog-optional)
  * [attachmentsPattern](#attachmentspattern-optional)
  * [body](#body-optional)
  * [compressLog](#compresslog-optional)
  * [mimeType](#mimetype-optional)
  * [onAbort](#onabort-optional)
  * [onFailure](#onfailure-optional)
  * [onStillFailing](#onstillfailing-optional)
  * [onFixed](#onfixed-optional)
  * [onSuccess](#onsuccess-optional)
  * [onUnstable](#onunstable-optional)
  * [onStillUnstable](#onstillunstable-optional)
  * [recipientProviders](#recipientproviders-optional)
  * [subject](#subject-optional)
  * [to](#to-optional)
* [Related classes](checkoutScm.md#related-classes)

## Examples

### Default triggers with attached log and to-recipients

``` groovy
notifyMail(
    notify : [
        attachLog: true,        
        compressLog : false,
        to: "recipient1@domain.tld,recipient2@domain.tld"
    ]
)
```

### Send only on first failure all participating developers

``` groovy
notifyMail(
    notify : [
        attachLog: true,        
        compressLog : false,
        onAbort : false,
        onFailure : true,
        onStillFailing : false,
        onFixed : false,        
        onSuccess : false,    
        onUnstable : false,
        onStillUnstable : false,        
        recipientProviders: [[$class: 'DevelopersRecipientProvider']]
    ]
)
```

### Custom Subject

``` groovy
notifyMail(
    notify : [
        'Custom notification for ${PROJECT_NAME} with status: ${NOTIFICATION_TRIGGER}',
    ]
)
```

:exclamation: Make sure to use single quotes here because environment
variables would otherwise be directly evaluated!

## Configuration options

Complete list of all configuration options.

All configuration options must be inside the `notify` ([`ConfigConstants.NOTIFY`](https://github.com/wcm-io-devops/jenkins-pipeline-library/blob/master/src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)) map element to be
evaluated and used by the step.

```groovy
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*

notifyMail(
    (NOTIFY) : [
        (NOTIFY_ATTACH_LOG): false,
        (NOTIFY_ATTACHMENTS_PATTERN): '',
        (NOTIFY_BODY): null,
        (NOTIFY_COMPRESS_LOG): false,
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
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_ATTACH_LOG`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`false`|

Controls if the log should be attached to the mail.

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)


### `attachmentsPattern` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_ATTACHMENTS_PATTERN`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`, comma separated list of ANT patterns|
|Default|`''`|

The pattern(s) for the attachments which should be send along with the email.

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)

### `body` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_BODY`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`${DEFAULT_CONTENT}`|

The body of the mail. The pipeline script assumes that you have a configured email template in place so default values is used `${DEFAULT_CONTENT}`

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)

### `compressLog` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_COMPRESS_LOG`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`false`|

When set to `true` the log is attached to the mail as compressed zip.

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)

### `mimeType` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_MIME_TYPE`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

The mimeType of the mail. The pipeline script assumes that you have
configured the mimeType in the "Extended E-mail Notification" section of
your Jenkins instance.

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)

### `onAbort` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_ON_ABORT`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`false`|

When set to `true` a notification is send when the job is aborted.

### `onFailure` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_ON_FAILURE`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`true`|

When set to `true` a notification is send when the job swiches to
failure (first failure)

:exclamation: For controlling the behavior when a job failes more than
one time in a row see [onStillFailing](#onstillfailing-optional)

### `onStillFailing` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_ON_STILL_FAILING`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`true`|

When set to `true` a notification is send when the job failed and the
previous build failed.

### `onFixed` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_ON_FIXED`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`true`|

When set to `true` a notification is send when the job status switches
from a non successful to successful.

### `onSuccess` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_ON_SUCCESS`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`false`|

When set to `true` a notification is send every time a job is
successful.

### `onUnstable` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_ON_UNSTABLE`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`true`|

When set to `true` a notification is send when the job switches to unstable.

:exclamation: For controlling the behavior when a job is unstable more than
one time in a row see [onStillUnstable](#onstillunstable-optional)

### `onStillUnstable` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_ON_STILL_UNSTABLE`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`true`|

### `recipientProviders` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_RECIPIENT_PROVIDERS`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`List` of `Map` with `RecipientProvider` classes|
|Default|`[[$class: 'CulpritsRecipientProvider'],[$class: 'DevelopersRecipientProvider'],[$class: 'FailingTestSuspectsRecipientProvider'], [$class: 'FirstFailingBuildSuspectsRecipientProvider'],[$class: 'RequesterRecipientProvider'][$class: 'UpstreamComitterRecipientProvider']]`|

The list of recipient providers used to determine who should receive a
notification. Per default all recipent providers (except `ListProvider`)
are used.

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)

### `subject` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_SUBJECT`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`${PROJECT_NAME} - Build # ${BUILD_NUMBER} - ${NOTIFICATION_TRIGGER}`|

The subject for the mail.

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)

### `to` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NOTIFY_TO`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`, comma separated list of email adresses|
|Default|`null`|

Recipients that should always get a notification. This list has to be a
comma separated String of mail adresses.

:bulb: See [Email Extension Plugin](https://jenkins.io/doc/pipeline/steps/email-ext/)

## Related classes
* [`NotificationTriggerHelper`](../src/io/wcm/tooling/jenkins/pipeline/utils/NotificationTriggerHelper.groovy)
