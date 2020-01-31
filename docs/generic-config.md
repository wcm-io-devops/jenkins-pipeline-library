# Generic Configuration

The initial release for the pipeline library already contained the
[pattern-matching](pattern-matching.md) mechanism which is able to
provide scm-url related settings for

* credentials
* maven settings
* etc.

Over the time the configuration via json and the strict one-to-one
relation ship between a pattern and configuration lead to some
frustration and administrative overhead.

Also the SCM_URL approach did not fit in all scenarios. So we introduced
the **F**ully-**Q**ualified **J**ob **N**ame (FQJN) approach

That's why the `GenericConfig` was introduced.

# Table of contents

* [Format](#format)
  * [Multi-pattern](#multi-pattern)
  * [Single-pattern (legacy)](#single-pattern-legacy)
* [Placement](#placement)
* [Pattern matching](#pattern-matching)
  * [SCM_URL based pattern matching](#scm_url-based-pattern-matching)
  * [FQJN based pattern matching](#fqjn-based-pattern-matching)
* [FQJN pattern tipps](#fqjn-pattern-tipps)
* [Working with GenericConfigs](#working-with-genericconfigs)
  * [Example: Load a generic config and match it against the FQJN](#example-load-a-generic-config-and-match-it-against-the-fully-qualified-job-name)

# Format

The generic configuration is based upon a yaml format and allows you to
specify 1-N patterns for the [pattern-matching](pattern-matching.md)
mechanism.

## Multi-pattern

```yaml
- patterns:
    - multi-pattern1
    - multi-pattern2
  id: "multi-pattern-id"
  config:
    key: value
    # ...
```

## Single-pattern (legacy)

This structure was kept due to legacy issues. The Multi-pattern variant
should be preferred.

```yaml
- pattern: single-pattern
  id: "single-pattern-id"
  config:
    key: value
```

# Placement

These yaml files can be placed in the `resources` folder of your
pipeline-library configuration SCM Project.

E.g. `resources/jenkins-pipeline-library/config/notify/mattermost.yaml`

# Pattern matching

The general concept of the pattern matching is documented in
[PatternMatching](pattern-matching.md).

In this pipeline library we use two sources for the pattern, which are
documented in the following sections.

## SCM_URL based pattern matching

In this variant we are using SCM_URL to find an appropriate
configuration item from a list.

This mechanism is for example used in:

* [`checkoutScm`](../vars/checkoutScm.md)
* [`sshAgentWrapper`](../vars/sshAgentWrapper.md)

**Example**

    String scmUrl = getScmUrl()
    Map yamlConfig = genericConfig.load('resources/jenkins-pipeline-library/config/notify/mattermost.yaml', getScmUrl(), 'notifyMattermost')


## FQJN based pattern matching

In this variant we are using a combination of the Jenkins `JOB_NAME` and
the `GIT_BRANCH` to find an appropriate configuration item from a list.

For example the `JOB_NAME` is `folder/subfolder/my_compile_job` and the
`GIT_BRANCH` is `origin/feature/my-branch` then the **FQJN** is
`folder/subfolder/my_compile_job@feature/my-branch`.

Please note that we are stripping off the `origin/` from the
`GIT_BRANCH`.

:bulb: It is highly recommended to use the
[`checkoutScm`](../vars/checkoutScm.md) step from the pipeline library,
because this step is taking care of setting the correct `GIT_BRANCH`!

This mechanism is for example used in:

* [`notify.mqtt`](../vars/notify.md#notifymqttmap-config)
* [`notify.mattermost`](../vars/notify.md#notifymattermostmap-config)

# FQJN pattern tipps

When using FQJN as pattern matching search value you for example include
only jobs that are building the master branch:

    patterns:
      - ^folder\/subfolder\/.*@master

You can also exclude jobs that are building feature branches:

    patterns:
       - ^folder\/subfolder\/.*@(?!feature)

# Working with GenericConfigs

:bulb: For your convenience a step is available:
[genericConfig](../vars/genericConfig.md).

## Example: Load a generic config and match it against the **F**ully-**Q**ualified **J**ob **N**ame

```groovy
import io.wcm.devops.jenkins.pipeline.config.GenericConfigUtils

GenericConfigUtils genericConfigUtils = new GenericConfigUtils(this)
String search = genericConfigUtils.getFQJN()
Map yamlConfig = genericConfig.load('resources/jenkins-pipeline-library/config/notify/mattermost.yaml', getScmUrl(), 'notifyMattermost')
```