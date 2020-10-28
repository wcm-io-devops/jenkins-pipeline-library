[![Build Status](https://travis-ci.org/wcm-io-devops/jenkins-pipeline-library.svg?branch=master)](https://travis-ci.org/wcm-io-devops/jenkins-pipeline-library)
[![Code Coverage](https://codecov.io/gh/wcm-io-devops/jenkins-pipeline-library/branch/master/graph/badge.svg)](https://codecov.io/gh/wcm-io-devops/jenkins-pipeline-library)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm.devops.jenkins/io.wcm.devops.jenkins.pipeline-library/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm.devops.jenkins/io.wcm.devops.jenkins.pipeline-library)
[![Sonatype Snapshots](https://img.shields.io/nexus/s/https/oss.sonatype.org/io.wcm.devops.jenkins/io.wcm.devops.jenkins.pipeline-library.svg)](https://oss.sonatype.org/content/repositories/snapshots/io/wcm/devops/jenkins/io.wcm.devops.jenkins.pipeline-library/)

# Jenkins Pipeline Library

Since Jenkins Pipeline has reached a certain state of production scripted
pipelines are the way to go.

But: Not everything known from the UI is available in Pipeline and
configuring and writing scripts is not so easy for the normal developer.

The target of this library is to take out some complexity (and yes
adding some too) of the pipeline creation and to bring back some known
functionality (for example `GIT_BRANCH` and `SCM_URL` environment
variables, mail notification on still unstable etc.)

Want to see an example? Have look at
[Usage examples](docs/usage-examples.md)

# Table of contents
* [Key concepts](#key-concepts)
* [Requirements](#requirements)
* [Steps](#steps)
* [Utilities](#utilities)
* [Credential and managed file auto lookup](#credential-and-managed-file-auto-lookup)
* [Support for command line execution](#support-for-command-line-execution)
* [Setup your environment to use the pipeline library](#setup-your-environment-to-use-the-pipeline-library)
* [Building/Testing](#buildingtesting)
    * [Building with maven](#building-with-maven)
* [Changes / Version History](#changes--version-history)

## Key concepts

The pipeline library was developed with a focus to ease Java and Maven
build processes within companies which have a more or less similiar
project structure e.g.
* Maven/Java
* local Artifact Server (like Sonatype Nexus or Artifactory)
* GIT

The assumption is that in these environments

* Jenkins has a dedicated user account to checkout code (or one per project)
* the artifact server caches public artifacts and acts as a internal
  artifact server

:question: So why configure maven repositories and scm credentials in
every pipeline?

So the key concepts of the pipeline enable you to
* Auto provide credentials (no worries, only Jenkins credential ids, not
  the credential itself) (see [Credentials](docs/credentials.md))
* Auto provide maven settings (see [ManagedFiles](docs/managed-files.md))
* configure each job the same way (see [ConfigStructure](docs/config-structure.md))
* log and see the things you are interested in (see [Logging](docs/logging.md))

to builds.

Running this pipeline library will result in more structured and easier
to maintain pipeline scripts.

Configured properly this library enables you to checkout scm
with these lines of code:

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
checkoutScm( (SCM) : [
        (SCM_URL) : "git@domain.tld/group/project.git",
    ]
)
```

Or running maven with local and global maven settings with these lines
of code:

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
execMaven( 
    (SCM) : [
        (SCM_URL) : "git@domain.tld/group/project.git",
    ],
    (MAVEN): [
        (MAVEN_GOALS) : [ "clean", "install" ]
    ]
)
```

:question: Looking for an example on how a pipeline script looks like
when using Pipeline? Have a look at
[Usage examples](docs/usage-examples.md)

:bulb: Have a look at the [setup tutorial](docs/tutorial-setup.md) to
start using Pipeline Library.

## Requirements

Have a look at [requirements](docs/requirements.md) to get the library running.

## Steps

The pipeline library comes with the following steps:

* [ansible](vars/ansible.md)
  * [`ansible.checkoutRoles`](vars/ansible.md#checkoutrolesmap-config)
  * [`ansible.execPlaybook`](vars/ansible.md#execplaybookmap-config)
  * [`ansible.getGalaxyRoleInfo`](vars/ansible.md#getgalaxyroleinforole-role)
  * [`ansible.installRoles`](vars/ansible.md#installrolesmap-config)
* [`checkoutScm`](vars/checkoutScm.md)
* [`conditionalStage`](vars/conditionalStage.md)
* [credentials](vars/credentials.md)
    *  [`lookupHttpCredential`](vars/credentials.md#lookuphttpcredentialstring-uri)
    *  [`lookupScmCredential`](vars/credentials.md#lookupscmcredentialstring-uri)
    *  [`lookupSshCredential`](vars/credentials.md#lookupsshcredentialstring-uri)
* [`execManagedShellScript`](vars/execManagedShellScript.md)
* [`execMaven`](vars/execMaven.md)
* [`execMavenRelease`](vars/execMavenRelease.md)
* [`execNpm`](vars/execNpm.md)
* [genericConfig](vars/genericConfig.md)
  * [`load`](vars/genericConfig.md#object-loadpath-searchvalue-resultkey--null)
* [`getScmUrl`](vars/getScmUrl.md)
* [gitTools](vars/gitTools.md)
  * [`mirrorRepository`](vars/gitTools.md#mirrorrepositorystring-srcurl-string-targeturl-liststring-srccredentialids--null-liststring-targetcredentialids--null)
  * [`mirrorRepositoryToRemote`](vars/gitTools.md#mirrorrepositorytoremotestring-srcrepopath-gitrepository-targetrepo-liststring-targetcredentialids--null)
  * [`mirrorRepositoryToWorkspace`](vars/gitTools.md#mirrorrepositorytoworkspacegitrepository-srcrepo-liststring-srccredentialids--null)
  * [`getFetchOrigin`](vars/gitTools.md#string-getfetchoriginstring-remotes--null)
  * [`getPushOrigin`](vars/gitTools.md#string-getpushoriginstring-remotes--null)
  * [`getParentBranch`](vars/gitTools.md#string-getparentbranch)
* [im (instant messaging)](vars/im.md)
  * [`im.mattermost()`](vars/im.md#immattermost)
  * [`im.teams()`](vars/im.md#imteams)
* [jenkinsRestApi](vars/jenkinsRestApi.md)
  * [`jenkinsRestApi.findJobsByNameRegex`](vars/jenkinsRestApi.md#list-job-findjobsbynameregexmap-remote)
* [managedScripts](vars/managedScripts.md)
    * [`managedScripts.execJenkinsShellScript`](vars/managedScripts.md#execjenkinsshellscriptstring-scriptid-commandbuilder-commandbuilder-null-returnstdout--false-returnstatus--false)
    * [`managedScripts.execPipelineShellScript`](vars/managedScripts.md#execpipelineshellscriptstring-scriptpath-commandbuilder-commandbuilder-null-returnstdout--false-returnstatus--false)
* [maven](vars/maven.md)
    * [`purgeSnapshots`](vars/maven.md#purgesnapshotsmap-config)
* [notify](vars/notify.md)
  * [`notify.mail`](vars/notify.md#notifymailmap-config)
  * [`notify.mattermost`](vars/notify.md#notifymattermostmap-config)
  * [`notify.mqtt`](vars/notify.md#notifymqttmap-config)
  * [`notify.teams`](vars/notify.md#notifyteamsmap-config)
* [`notifyMail`](vars/notifyMail.md) :warning: deprecated, use
  [`notify.mail`](vars/notify.md#notifymailmap-config) instead
* [`setBuildName`](vars/setBuildName.md)
* [`setGitBranch`](vars/setGitBranch.md)
* [`setScmUrl`](vars/setScmUrl.md)
* [`setupTools`](vars/setupTools.md)
* [`sshAgentWrapper`](vars/sshAgentWrapper.md)
* [`transferScp`](vars/transferScp.md)
* [wrap](vars/wrap.md)
    * [`wrap.color`](vars/wrap.md) :warning: deprecated, use [`wrappers`](vars/wrappers.md) instead.
* [wrappers](vars/wrappers.md)
    * [`wrappers.color`](vars/wrappers.md#colormap-config-closure-body)

## Utilities
* [Build Parameter Factory](src/io/wcm/devops/jenkins/pipeline/job/BuildParameterFactory.groovy)
* [Integration Testing](vars/integrationTestUtils.md)
* [Logging](docs/logging.md)
    * [`Logger`](src/io/wcm/devops/jenkins/pipeline/utils/logging/Logger.groovy)
    * [`LogLevel`](src/io/wcm/devops/jenkins/pipeline/utils/logging/LogLevel.groovy)
* [Configuration map merging](docs/config-map-merging.md)
  * [`MapUtils`](src/io/wcm/devops/jenkins/pipeline/utils/maps/MapUtils.groovy)
  * [`MapMergeMode`](src/io/wcm/devops/jenkins/pipeline/utils/maps/MapMergeMode.groovy)

## Credential and managed file auto lookup

* [Credentials](docs/credentials.md)
    *  [`Credential`](src/io/wcm/devops/jenkins/pipeline/credentials/Credential.groovy)
    *  [`CredentialParser`](src/io/wcm/devops/jenkins/pipeline/credentials/CredentialParser.groovy)
    *  [`CredentialConstants`](src/io/wcm/devops/jenkins/pipeline/credentials/CredentialConstants.groovy)
* [Generic Configuration](docs/generic-config.md)
* [ManagedFiles](docs/managed-files.md)
    * [`ManagedFile`](src/io/wcm/devops/jenkins/pipeline/managedfiles/ManagedFile.groovy)
    * [`ManagedFileParser`](src/io/wcm/devops/jenkins/pipeline/managedfiles/ManagedFileParser.groovy)
    * [`ManagedFileConstants`](src/io/wcm/devops/jenkins/pipeline/managedfiles/ManagedFileConstants.groovy)
* [PatternMatching](docs/pattern-matching.md)
    * [`PatternMatchable`](src/io/wcm/devops/jenkins/pipeline/model/PatternMatchable.groovy)
    * [`PatternMatcher`](src/io/wcm/devops/jenkins/pipeline/utils/PatternMatcher.groovy)

## Support for command line execution

* [`CommandBuilder`](src/io/wcm/devops/jenkins/pipeline/shell/CommandBuilderImpl.groovy)
* [`MavenCommandBuilder`](src/io/wcm/devops/jenkins/pipeline/shell/MavenCommandBuilderImpl.groovy)

## Setup your environment to use the pipeline library

Please refer to [SetupTutorial](docs/tutorial-setup.md) for detailed
instruction on how to setup the library in your environment.

## Building/Testing

The library uses two approaches for testing.

The class parts are tested by unit testing using JUnit/Surefire. All
unit tests have the naming format `*Test.groovy` and are located below
`test/io`.

The step parts are tested by using
[Jenkins Pipeline Unit](https://github.com/lesfurets/JenkinsPipelineUnit)
with jUnit/Failsafe. All integration tests have the naming format
`*IT.groovy` and are located below `test/vars`.

### Building with maven

    mvn clean install

### Add license file headers

    mvn license:update-file-header

## Changes / Version History

Please have a look at the [Releases](https://github.com/wcm-io-devops/jenkins-pipeline-library/releases)
