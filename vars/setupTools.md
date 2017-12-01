# setupTools

Despite the fact that setting up tools is one of the more easier steps
in pipeline there is always the hassle with providing the path to the
tool for `sh` access.

This step takes care about this issue and ensures that the initialized
tool is also available in the `PATH` environment variable

# Table of contents
* [Examples](#examples)
  * [Setting up Maven and Jdk](#setting-up-maven-and-jdk)
  * [Setting up two Jdk's](#setting-up-two-jdks)
  * [Setting up tool without environment variable](#setting-up-tool-without-environment-variable)
* [Supported tools](#supported-tools)
* [Configuration options](#configuration-options)
    * [`envVar` (optional)](#envvar-optional)
    * [`name`](#name)
    * [`type` (optional)](#type-optional)
* [Related classes](#related-classes)

## Examples

### Setting up Maven and Jdk

``` groovy
``` groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
import io.wcm.tooling.jenkins.pipeline.model.Tool

setupTools(
    (TOOLS): [
        [ (TOOL_NAME): 'apache-maven3', (TOOL_TYPE): Tool.MAVEN ],
        [ (TOOL_NAME): 'jdk8', (TOOL_TYPE): Tool.JDK ]
    ]
)
```

After execution there will be two environment variables:
* `MAVEN_HOME=/path/to/maven/installation`
* `JAVA_HOME=/path/to/java/installation`

The `PATH` environment variable will be adjusted and also contains the
paths to the tools.

### Setting up two Jdk's

``` groovy
``` groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
import io.wcm.tooling.jenkins.pipeline.model.Tool

setupTools([
    (TOOLS): [
        [ (TOOL_NAME): 'jdk8', (TOOL_TYPE): Tool.JDK ],
        [ (TOOL_NAME): 'jdk7', (TOOL_TYPE): Tool.JDK, (TOOL_ENVVAR): 'JAVA_HOME7' ]
    ]
])
```

After execution there will be two environment variables:
* `JAVA_HOME=/path/to/java8/installation`
* `JAVA_HOME7=/path/to/java7/installation`

The `PATH` environment variable will be adjusted and also contains the
paths to the tools.

:bulb: Use the environment variables in this case for executing since
there now will be two java binaries in the paths

### Setting up tool without environment variable

``` groovy
``` groovy
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*
import io.wcm.devops.jenkins.pipeline.model.Tool
setupTools([
    (TOOLS): [
        [ (TOOL_NAME): 'jdk8' ]     
    ]
])
```

After execution there will be **no** specific environment variable for
the tool since no `type` and no `envVar` was specified

However, the `PATH` environment variable was adjusted and contains the
path to the tool.

## Supported tools

At the moment the following tools are supported for auto lookup the
environment variables.

* MAVEN (environment variable: `MAVEN_HOME`)
* JDK (environment variable: `JAVA_HOME`)
* ANSIBLE (environment variable: `ANSIBLE_HOME`)
* GIT (environment variable: `GIT_HOME`)
* GROOVY (environment variable: `GROOVY_HOME`)
* MSBUILD (environment variable: `MSBUILD_HOME`)
* ANT (environment variable: `ANT_HOME`)
* PYTHON (environment variable: `PYTHON_HOME`)
* DOCKER (environment variable: `DOCKER_HOME`)
* NODEJS (environment variable: `NPM_HOME`)

:bulb: See
[`Tool`](../src/io/wcm/tooling/jenkins/pipeline/model/Tool.groovy)

## Configuration options

Complete list of all configuration options.

All configuration options must be inside the `tools` ([`ConfigConstants.TOOLS`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)) map element to be
evaluated and used by the step.

The `tools` element must be a `List`.
```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

setupTools([
    (TOOLS): [
        [ 
            (TOOL_ENVVAR): 'the-name-of-the-environemt-variable',
            (TOOL_NAME): 'tool-name-defined-in-jenkins', 
            (TOOL_TYPE): "the-type-of-the.tool" 
        ],
        // more tool definitions
    ]
])
```

Each tool definition has three properties:

### `envVar` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.TOOL_ENVVAR`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String` The name of the environment variable where the path will be set after initialisation|
|Default|-|

If set this environment variable will be used to store the path the tool

### `name`
|||
|---|---|
|Constant|[`ConfigConstants.TOOL_NAME`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|-|

The name of the tool configured in the Jenkins instance

### `type` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.TOOL_TYPE`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|-|

The type of the tool. [Supported tools](#supported-tools)
If provided the environment variable will be automatically set based on the type.

## Related classes:
* [`Tool`](../src/io/wcm/tooling/jenkins/pipeline/model/Tool.groovy)
