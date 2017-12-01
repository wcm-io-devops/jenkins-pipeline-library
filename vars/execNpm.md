# execNpm

The `execNpm` steps enables you to NPM builds within your pipeline
scripts. The step supports the managed file auto lookup mechanism so you
don't have to configure your own npm artifact manager in every job.

# Table of contents
* [Managed file auto lookup](#managed-file-auto-lookup)
    * [NPM/node.js environment configuration](#npmnodejs-environment-configuration)
        * [Example for `NPM_CONFIG_USER_CONFIG` and `NPMRC` auto lookup](#example-for-npm-config-user-config-and-npmrc-auto-lookup)
* [Examples]()
* [Configuration options](#configuration-options)
    * [`arguments` (optional)](#arguments-optional)
    * [`executable` (optional)](#executable-optional)
* [Related classes](#related-classes)

## Managed file auto lookup

The managed file auto lookup is the core functionality of the
`execNpm` step. It reduces the amount of time that must be spend for
configuring npm builds in larger environments to a minimum.

### NPM/node.js environment configuration

If you are using node.js/NPM to build frontend stuff within your ci
environment you can use the `execNpm` step to automatically provide
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

When you now execute the `execNpm` Step with
```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
execNpm(
    (SCM) : [ (SCM_URL) : 'https://git.company.tld/group1/project1.git' ],
    (NPM) : [ (NPM_ARGUMENTS): ['run', 'build'] ]
)
```

Npm will be executed with this commandline:

`npm run build --userconfig /path/to/user/config --globalconfig /
path/to/global/config`

## Examples



## Configuration options

Complete list of all configuration options.

All configuration options must be inside the `npm`
([`ConfigConstants.NPM`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy))
map element to be evaluated and used by the step.

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

execNpm( 
    (NPM) : [
        (NPM_ARGUMENTS): [ "run", "build" ],
        (NPM_EXECUTABLE): "npm",
    ]
)
```

### `arguments`
|||
|---|---|
|Constant|[`ConfigConstants.NPM_ARGUMENTS`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`List` of `String` or `String`|
|Default|`[]`|

The arguments which will be placed after the `npm` command.

```groovy
(NPM_ARGUMENTS) : [ "run", "build" ]
```

or a `String` like:

```groovy
(NPM_ARGUMENTS) : [ "run build" ]
```

### `executable` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.NPM_EXECUTABLE`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Map` or `String`|
|Default|`null`|

Defines the executable to use. Per default `execNpm` expects the executable `npm` to be in the `PATH`

## Related classes
* [`ManagedFile`](../src/io/wcm/tooling/jenkins/pipeline/managedfiles/ManagedFile.groovy)
* [`ManagedFileParser`](../src/io/wcm/tooling/jenkins/pipeline/managedfiles/ManagedFileParser.groovy)
* [`CommandBuilder`](../src/io/wcm/tooling/jenkins/pipeline/shell/CommandBuilderImpl.groovy)
* [`PatternMatcher`](../src/io/wcm/tooling/jenkins/pipeline/utils/PatternMatcher.groovy)
