# maven

This part of the pipeline library provides utility functions for maven.

# Table of contents

* [`purgeSnapshots(Map config)`](#purgesnapshotsmap-config)
* [`purgeSnapshots(String repositoryPath = null, Boolean dryRun = false, LogLevel logLevel = null)`](#purgesnapshotsstring-repositorypath--null-boolean-dryrun--false-loglevel-loglevel--null)

## `purgeSnapshots(Map config)`

This function is a map based adapter function for [`purgeSnapshots`](#purgesnapshotsstring-repositorypath--null-boolean-dryrun--false-loglevel-loglevel--null)

### Configuration options

Complete list of all configuration options.

All configuration options must be inside the `MAVEN_PURGE_SNAPSHOTS`
([`ConfigConstants.MAVEN_PURGE_SNAPSHOTS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy))
map element to be evaluated and used by the function.

```groovy
import io.wcm.devops.jenkins.pipeline.utils.logging.LogLevel
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

maven.purgeSnapshots(
        (MAVEN_PURGE_SNAPSHOTS): [
            (MAVEN_PURGE_SNAPSHOTS_REPO_PATH) : null,
            (MAVEN_PURGE_SNAPSHOTS_DRY_RUN) : false,
            (MAVEN_PURGE_SNAPSHOTS_LOG_LEVEL) : LogLevel.INFO,
        ]
    )

```

#### `repoPath` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.MAVEN_PURGE_SNAPSHOTS_REPO_PATH`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

The path to the maven repository.

#### `dryRun` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.MAVEN_PURGE_SNAPSHOTS_DRY_RUN`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`false`|

Controls if the snapshot purging is executed in test mode.

#### `logLevel` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.MAVEN_PURGE_SNAPSHOTS_LOG_LEVEL`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`null`|

The log level of the managed shell script.

## `purgeSnapshots(String repositoryPath = null, Boolean dryRun = false, LogLevel logLevel = null)`

This function uses the managed pipeline shell script [`purge-snapshots.sh`](../resources/jenkinsPipelineLibrary/managedScripts/shell/maven/purge-snapshots.sh)

This scrips searches for SNAPSHOT artifacts in the specified maven repository and deletes them.
Normally this script searches under `$HOME/.m2/repository` but this can be adjusted by setting a `repositoryPath`.
When `dryRun` is set to `true` the script will only simulate the purge.
With the parameter `logLevel` the verbosity of the managed shell script can be adjusted.

## Related
* [`managedScripts`](managedScripts.md)
