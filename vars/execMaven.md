# execMaven

Especially in company environments where you have your own artifact
managers like [Nexus](http://www.sonatype.org/nexus/) or
[Artifactory](https://www.jfrog.com/artifactory/) for caching and
storage you want to provide project based global and local settings.

To make this easier the `execMaven` step provides autolookup based on
*  [ManagedFiles](../docs/managed-files.md) and the
*  [PatternMatching](../docs/pattern-matching.md) algorithm

This can of course be done by wrapping the `sh` step inside a
`configFileProvider` step and define all necessary managed files here
but this can be quite anoying and it makes it difficult to maintain the
scripts and configurations in a large CI environment.

This step removes some complexity from your scripty providing automatically
* global maven settings
* local maven settings
* NPM configuration
* Ruby Bundler configuration

It also takes care about the command line building by transforming the
given configuration into a `sh` step call.

# Table of contents
* [Managed file auto lookup](#managed-file-auto-lookup)
    * [Global Maven Settings](#global-maven-settings)
        * [Example for global Maven settings auto lookup](#example-for-global-maven-settings-auto-lookup)
    * [Local Maven settings](#local-maven-settings)
        * [Example for local Maven settings auto lookup](#example-for-local-maven-settings-auto-lookup)
    * [NPM/node.js environment configuration](#npmnodejs-environment-configuration)
        * [Example for `NPM_CONFIG_USER_CONFIG` and `NPMRC` auto lookup](#example-for-npm-config-user-config-and-npmrc-auto-lookup)
    * [Bundler environment configuration](#bundler-environment-configuration)
        * [Example for `BUNDLE_CONFIG` auto lookup](#example-for-bundle-config-auto-lookup)
* [Examples](#examples)
    * [Example 1: All configuration options used](#example-1-all-configuration-options-used)
    * [Example 2: Simple maven call](#example-2-simple-maven-call)
    * [Example 3: Just maven](#example-3-just-maven)
    * [Example 4: Maven version](#example-4-maven-version)
* [Configuration options](#configuration-options)
    * [`arguments` (optional)](#arguments-optional)
    * [`defines` (optional)](#defines-optional)
    * [`executable` (optional)](#executable-optional)
    * [`globalSettings` (optional)](#globalsettings-optional)
    * [`goals` (optional)](#goals-optional)
    * [`pom` (optional)](#pom-optional)
    * [`profiles` (optional)](#profiles-optional)
    * [`settings` (optional)](#settings-optional)
* [Related classes](#related-classes)

## Managed file auto lookup

The managed file auto lookup is the core functionality of the
`execMaven` step. It reduces the amount of time that must be spend for
configuring maven builds in larger environments to a minimum.

### Global Maven Settings

If you provide a JSON file at this location
`resources/managedfiles/maven/global-settings.json` in the format
described in [ManagedFiles](../docs/managed-files.md) the step will
automatically try to lookup the global settings for the provided scm url
and use them

This step uses the best match by using the
[PatternMatcher](../src/io/wcm/devops/jenkins/pipeline/utils/PatternMatcher.groovy)
so the `ManagedFile` with the most matching characters will be used as
global setting.

When no global setting was found the command line parameter will be omitted.

#### Example for global Maven settings auto lookup

Given a company with a GIT server at `https://git.company.tld`, a global
maven setting with id `company-global-maven-setting` stored inside
Jenkins as ManagedFile and we assume that all projects should use this
per default.

When you setup your own pipeline library which uses the pipeline-library
all you have to do is to create
`resources/managedfiles/maven/global-settings.json` with this content:

```json
[
  {
    "pattern": "git.company.tld",
    "id": "company-global-maven-setting",
    "name": "Company global maven settings",
    "comment": "Global maven settings for nexus.company.tld"
  }
]

```

When you now execute the `execMaven` step with
```groovy
execMaven(
    scm : [ url: 'https://git.company.tld/group/project.git' ],
    maven : [ goals: ['clean', 'install'] ]
)
```

Maven will be executed with this commandline: `mvn clean
install --global-settings
'/path/to/temporary/managed-global-settings-file'`

### Local Maven settings

The local settings mechanism works the same way as the global maven
settings but with an other json file containing the definitions.

Local Maven settings are useful when you have project specific
credentials on your artifact server.

If you provide a JSON file at this location
`resources/managedfiles/maven/settings.json` in the format
described in [ManagedFiles](../docs/managed-files.md) the step will
automatically try to lookup the settings for the provided scm url
and use them

This step uses the best match by using the
[PatternMatcher](../src/io/wcm/devops/jenkins/pipeline/utils/PatternMatcher.groovy)
so the `ManagedFile` with the most matching characters will be used as
global setting.

When no local setting was found the command line parameter will be
omitted.

#### Example for local Maven settings auto lookup

Given a company with a GIT server at `https://git.company.tld`, a local
maven setting with id `group1-project1-local-maven-setting` stored
inside Jenkins as ManagedFile.

When you setup your own pipeline library which uses the pipeline-library
all you have to do is to create
`resources/managedfiles/maven/settings.json` with this content:

```json
[
  {
    "pattern": "git.company.tld/group1/project1",
    "id": "group1-project1-local-maven-setting",
    "name": "group1, project1 local maven msettings",
    "comment": "Local maven settings to deploy group1/project1 artifacts to nexus.company.tld"
  }
]

```

When you now execute the `execMaven` Step with
```groovy
execMaven(
    scm : [ url: 'https://git.company.tld/group1/project1.git' ],
    maven : [ goals: ['clean', 'install'] ]
)
```

Maven will be executed with this commandline: `mvn clean
install --settings
'/path/to/temporary/managed-group1-project1-settings-file'`

### NPM/node.js environment configuration

If you are using node.js/NPM to build frontend stuff within your maven
projects you can use the `execMaven` step to automatically provide
* managed configuration file to `NPM_CONFIG_USER_CONFIG` environment
  variable
* managed configuration file to `NPMRC` environment variable

#### Example for `NPM_CONFIG_USER_CONFIG` and `NPMRC` auto lookup

Given a company with a GIT server at `https://git.company.tld`, a npmrc
setting with id `group1-project1-npmrc` and a npm config with id
`group1-project1-npm-config` stored inside Jenkins as ManagedFile.

When you setup your own pipeline library which uses the pipeline-library
all you have to do is to create the file
`resources/managedfiles/npm/npmrc.json` with this content:

```json
[
  {
    "pattern": "git.company.tld/group1/project1",
    "id": "group1-project1-npmrc",
    "name": "group1, project1 npmrc",
    "comment": "npmrc for group1/project1"
  }
]

```

and a file `resources/managedfiles/npm/npm-config-userconfig.json` with
this content:

```json
[
  {
    "pattern": "git.company.tld/group1/project1",
    "id": "group1-project1-npm-config",
    "name": "group1, project1 npm config",
    "comment": "npmrc for group1/project1"
  }
]

```

When you now execute the `execMaven` Step with
```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
execMaven(
    (SCM) : [ (SCM_URL) : 'https://git.company.tld/group1/project1.git' ],
    (MAVEN) : [ (MAVEN_GOALS) : ['clean', 'install'] ]
)
```

Maven will be executed with this commandline: `mvn clean
install` and for the duration of the execution the managed files are
available in these environment variables
* `NPMRC`
* `NPM_CONFIG_USER_CONFIG`

### Bundler environment configuration

The `execMaven` is also able to provide npm bundler configuration as
environment variable `BUNDLE_CONFIG` during the execution of maven.

#### Example for `BUNDLE_CONFIG` auto lookup

Given a company with a GIT server at `https://git.company.tld` and a ruby
bundler setting with id `group1-project1-bundle-config` stored inside Jenkins as ManagedFile.

When you setup your own pipeline library which uses the pipeline-library
all you have to do is to create the file
`resources/managedfiles/ruby/bundle-config.json` with this content:

```json
[
  {
    "pattern": "git.company.tld/group1/project1",
    "id": "group1-project1-bundle-config",
    "name": "group1, project1 ruby bundler config",
    "comment": "ruby bundler config for group1/project1"
  }
]

```

When you now execute the `execMaven` Step with
```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
execMaven(
    (SCM) : [ (SCM_URL) : 'https://git.company.tld/group1/project1.git' ],
    (MAVEN) : [ (MAVEN_GOALS): ['clean', 'install'] ]
)
```

Maven will be executed with this commandline: `mvn clean
install` and for the duration of the execution the managed file is
available in this environment variable
* `BUNDLE_CONFIG`

## Build parameter injection

The UI version of the maven execution step supports the use of build
parameters as defines.

The `execMaven` also supports this functionality by simply enabling this functionality.

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
execMaven(
    (MAVEN): [        
        (MAVEN_GOALS)           : ["clean", "install"],
        (MAVEN_DEFINES)         : ["continuousIntegration": true, "flag": null],
        (MAVEN_INJECT_PARAMS)  : true,
        (MAVEN_ARGUMENTS)       : ["-B", "-U"]    
    ]
)
```

Enabling `MAVEN_INJECT_PARAMS` will add all existing builds parameters
from the global `params` Map object to the maven defines.

## Examples

### Example 1: All configuration options used

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
execMaven(
    (MAVEN): [
        (MAVEN_POM)             : "path/to/customPom1.xml",
        (MAVEN_GOALS)           : ["clean", "install"],
        (MAVEN_DEFINES)         : ["continuousIntegration": true, "flag": null],
        (MAVEN_GLOBAL_SETTINGS) : "global-settings-id",       
        (MAVEN_SETTINGS)        : "local-settings-id",
        (MAVEN_ARGUMENTS)       : ["-B", "-U"]    
    ]
)
```
The resulting `shell` command will look like:

    mvn -f path/to/customPom1.xml clean install -B -U
    -Dcontinuous-integration=true -Dflag --global-settings
    /path/to/job@tmp/config4417403508849619324tmp --settings
    /path/to/job@tmp/config838306283686660309tmp

:bulb: In this example the auto lookup for global and local maven
settings is omitted because `globalSettings` and `localSettings` were provided

### Example 2: Simple maven call

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
execMaven(
    (MAVEN) : [
        (MAVEN_GOALS) : ["clean", "install"]
    ]
)
```
Assuming that no global and local settings were provided for auto lookup
mechanism The resulting `shell` command will look like:

    mvn clean install

### Example 3: Just maven

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
execMaven(
    (MAVEN) : [:]
)
```
Assuming that no global and local settings were provided for auto lookup
mechanism The resulting `shell` command will look like:

    mvn

### Example 4: Maven version

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
execMaven(
    (MAVEM): [
        (MAVEN_ARGUMENTS) : ["--version"]
    ]
)
```
Assuming that no global and local settings were provided for auto lookup
mechanism The resulting `shell` command will look like:

    mvn --version

## Configuration options

Complete list of all configuration options.

All configuration options must be inside the `maven`
([`ConfigConstants.MAVEN`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy))
map element to be evaluated and used by the step.

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

execMaven( 
    (MAVEN) : [
        (MAVEN_ARGUMENTS): [ "-B", "-U" ],
        (MAVEN_DEFINES): ["name": "value", "flag": null],
        (MAVEN_EXECUTABLE): "/path/to/maven/bin",
        (MAVEN_GLOBAL_SETTINGS): "managed-file-id",
        (MAVEN_GOALS): ["goal1", "goal2"],
        (MAVEN_INJECT_PARAMS): false,
        (MAVEN_POM): "/path/to/pom.xml",
        (MAVEN_PROFILES): ["profile1", "profile2"],
        (MAVEN_SETTINGS): "managed-file-id",
    ]
)
```

### `arguments` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.MAVEN_ARGUMENTS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`List` of `String` or `String`|
|Default|`null`|

Additional arguments for maven. Can be a `List of `String` like

    [ "-B", "-U" ]

or a `String` like:

    "-B -U"

### `defines` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.MAVEN_DEFINES`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Map` or `String`|
|Default|`null`|

Defines for maven. Can be a `Map` like

    ["name": "value", "flag": null]

or a `String` like:

    "-Dname=value -Dflag"

### `executable` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.MAVEN_EXECUTABLE`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`mvn`|

Defines the command for maven.

You can specify the path to a custom mavn installation with this option like

    [ maven: [ executable: "/path/to/maven" ] ]

### `globalSettings` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.MAVEN_GLOBAL_SETTINGS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

When provided the auto lookup mechanism for global maven settings is omitted
and the step tries to retrieve a managed file with the provided value.

### `goals` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.MAVEN_GOALS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`List` of `String`, or `String`|
|Default|`null`|

The maven goals. Can be a `List of `String` like

    [ "goal1", "goal2" ]

or a `String` like:

    "goal1 goal2"

### `injectParams` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.MAVEN_INJECT_PARAMS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`false`|

When set to true the current build parameters are injected as defines to
the maven command line.

:bulb: The defines defined by `MAVEN_DEFINES` will not be overwritten
when using `MAVEN_INJECT_PARAMS`

### `pom` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.MAVEN_POM`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

Path to maven pom. When configuration is provided maven will be executed
without a path to a pom, so maven will look for a `pom.xml` in the
current working directory

### `profiles` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.MAVEN_PROFILES`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String` or `List<String>`|
|Default|`[]`|

Maven profiles to use.

### `settings` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.MAVEN_SETTINGS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

When provided the auto lookup mechanism for local maven settings is omitted
and the step tries to retrieve a managed file with the provided value.

## Related classes
* [`ManagedFile`](../src/io/wcm/devops/jenkins/pipeline/managedfiles/ManagedFile.groovy)
* [`ManagedFileParser`](../src/io/wcm/devops/jenkins/pipeline/managedfiles/ManagedFileParser.groovy)
* [`CommandBuilder`](../src/io/wcm/devops/jenkins/pipeline/shell/CommandBuilderImpl.groovy)
* [`MavenCommandBuilder`](../src/io/wcm/devops/jenkins/pipeline/shell/MavenCommandBuilderImpl.groovy)
* [`PatternMatcher`](../src/io/wcm/devops/jenkins/pipeline/utils/PatternMatcher.groovy)
