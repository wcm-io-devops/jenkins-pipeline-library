# Config structure

You may be wondering why each step gets named config map passed like

```groovy
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*

checkoutScm( 
    (SCM): [
        (SCM_URL): 'git@git.yourcompany.tld:group/project.git'
        ]
)
```

or

```groovy
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*

execMaven(
    (MAVEN): [
        (MAVEN_GOALS): [ "clean", "install" ]
    ]
)
```

or

```groovy
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*

setupTools([
    (TOOLS): [
        [ (TOOL_NAME): 'apache-maven3', (TOOL_TYPE): Tool.MAVEN ],
        [ (TOOL_NAME): 'jdk8', (TOOL_TYPE): Tool.JDK ],
    ]
])
```

The reason why the pipeline-library was designed like this is the maintainability of your configuration.
With this structure you are able to keep your configuration in one place.

## Example

Based on the steps above you are able to create a pipeline job like this:

```groovy
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*

import io.wcm.tooling.jenkins.pipeline.model.Tool
import io.wcm.tooling.jenkins.pipeline.utils.logging.LogLevel
import io.wcm.tooling.jenkins.pipeline.utils.logging.Logger

Map config = [
    (SCM): [
        (SCM_URL): 'git@git.yourcompany.tld:group/project.git'
    ],
    (TOOLS): [
        [ (TOOL_NAME): 'apache-maven3', (TOOL_TYPE): Tool.MAVEN ],
        [ (TOOL_NAME): 'jdk8', (TOOL_TYPE): Tool.JDK ]
    ],
    (MAVEN): [
        (MAVEN_GOALS): [ "clean", "install" ]
    ],
    (LOGLEVEL) : LogLevel.INFO
]

// initialize the logger
Logger.init(steps, config)

node() {
    // setup the tools
    setupTools(config)
    // to the checkout
    checkoutScm(config)
    // execute maven
    execMaven(config)
}
```

So your pipeline will stay much cleaner by keeping configuration in one place

## :+1: Tipp

All configuration option have constants. It is strongly recommended to
use them!