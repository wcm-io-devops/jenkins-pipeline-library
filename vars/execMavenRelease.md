# execMavenRelease

This step can be used to automate releases using Jenkins.

Basically the `execMavenRelease` step calls the
[`execMaven`](execMaven.md) step with the goals `release:prepare` and
`release:perform`, but with a little bit of magic around it.

# Table of contents
* [Prerequisites](#prerequisites)
    * [SCM (Git+SSH)](#scm-gitssh)
        * [Supported branches](#supported-branches)
        * [Git configuration](#git-configuration)
    * [maven-release-plugin version >= 2.5.3](#maven-release-plugin-version--253)
* [Workflow](#workflow)
* [Configuration options](#configuration-options)

## Prerequisites

### SCM (Git+SSH)

The step only allows releases via git+ssh! Release via http(s) is
currently not supported.

#### Supported branches

You can use this step only from the `master` branch! The step will fail
for any other branch!

#### Git configuration

1. Create/configure a user with the appropriate rights got the branch (master)
2. Create/configure a ssh key pair for this user and configure that key
   in Jenkins
3. Configure the ssh credential auto lookup (see
   [`sshAgentWrapper`](sshAgentWrapper.md) for details)

### maven-release-plugin version >= 2.5.3

You need at least the version 2.5.3 of the maven release plugin.
Versions prior 2.5.3 had some problems with committing to git.

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <artifactId>maven-release-plugin</artifactId>
                <version>2.5.3</version>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

## Workflow

The step performs some steps to ensure that everything is configured and
used correctly to save you some time deleting wrong scm tags and
artifacts from your artifact manager

1. Check SCM
   1. Check if scm url is a git ssh url
   (`git@someserver:group/project.git`)
   2. Check if scm branch is `master` .
   3. fail if there is any error
2. Check plugin versions
   1. Generating the effective pom by using the execMaven step with the
      goals `help:effective-pom` with output to
      `effective-pom.tmp`
   2. Read the effective pom and checking for the correct
      maven-release-plugin version
   3. fail if there is any error
3. Wrap the execMaven step by using the
   [`sshAgentWrapper`](sshAgentWrapper.md) step
3. Call `execMaven` with `release:prepare release:perform`

## Configuration options

The `execMavenRelease` step has no dedicated configuration options. Have
a look at the configuration options for the
[`execMaven` configuration options](execMaven.md#configuration-options) for more information

## Related classes
* [`ComparableVersion`](../src/io/wcm/devops/jenkins/pipeline/versioning/ComparableVersion.groovy)
