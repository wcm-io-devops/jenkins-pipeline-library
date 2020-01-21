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

That's why the `GenericConfig` was introduced.

# Table of contents

* [Format](#format)
  * [Multi-pattern](#multi-pattern)
  * [Single-pattern (legacy)](#single-pattern-legacy)


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

# Working with GenericConfigs

:bulb: For your convenience a step is available:
[genericConfig](../vars/genericConfig.md).

## Example: Load a generic config and access it's values

```groovy
Map yamlConfig = genericConfig.load('resources/jenkins-pipeline-library/config/notify/mattermost.yaml', getScmUrl(), 'notifyMattermost')
```