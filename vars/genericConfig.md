# genericConfig

The genericConfig steps are utility steps for the generic config
mechanism.

# Table of contents

* [`Object load(path, searchValue, resultKey = null`)](#object-loadpath-searchvalue-resultkey--null)
  * [Example 1: No resultKey wrapping](#example-1-no-resultkey-wrapping)
  * [Example 2: resultKey wrapping](#example-2-resultkey-wrapping)

# `Object load(path, searchValue, resultKey = null)`

This step will load a generic configuration yaml from the provided
`path`. The yaml will then be parsed using the `GenericConfigParser`.

With the [pattern-matching](../docs/pattern-matching.md) mechanism the
step will then retrieve the best matching `GenericConfig` for the
`searchValue`.

When a `resultKey` is provided the result will be wrapped into the
`resultKey`.

## Example 1: No resultKey wrapping

In this example the content of the `config` section of the matched
GenericConfig ([mattermost.yaml](../test/resources/jenkins-pipeline-library/config/notify/mattermost.yaml)) is returned without modification.

```groovy
import io.wcm.devops.jenkins.pipeline.config.GenericConfigConstants 
Map yamlConfig = genericConfig.load('jenkins-pipeline-library/config/notify/mattermost.yaml', 'git@git-ssh.domain.tld:/team-a/project1')

// result:
yamlConfig = [
  "endpointCredentialId" : "default.credential.id",
  "channel" : "team-a-jenkins-build-notifications"
]
```

## Example 2: resultKey wrapping

In this example the content of the `config` section of the matched
GenericConfig
([mattermost.yaml](../test/resources/jenkins-pipeline-library/config/notify/mattermost.yaml))
is placed inside the `notifyMattermost` key.

```groovy
import io.wcm.devops.jenkins.pipeline.config.GenericConfigConstants 
Map yamlConfig = genericConfig.load('jenkins-pipeline-library/config/notify/mattermost.yaml', 'git@git-ssh.domain.tld:/team-a/project1', "notifyMattermost")

// result:
yamlConfig = [
  "notifyMattermost" : [
    "endpointCredentialId" : "default.credential.id",
    "channel" : "team-a-jenkins-build-notifications"
  ]
]
```