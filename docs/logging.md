# Logging

The pipeline library provides an own [`Logger`](../src/io/wcm/tooling/jenkins/pipeline/utils/logging/Logger.groovy)
since pipeline provides only `echo` step at the moment and is quite communicative.

At the end the
[`Logger`](../src/io/wcm/tooling/jenkins/pipeline/utils/logging/Logger.groovy)
also uses the echo step but it filters out messages you don't want to
see the whole time.

## Table of contents

* [Initialization](#initialization)
* [Features](#features)
    *[Colorized output](#colorized-output)
* [LogLevels](#loglevels)
* [Examples](#examples)
  * [Example 1: Do a trace logging](#example-1-do-a-trace-logging)
  * [Example 2: Do a warning logging and hide loglevel below](#example-2-do-a-warning-logging-and-hide-loglevel-below)
  * [Example 3: Log an object](#example-3-log-an-object)
* [Configuration options](#configuration-options)
    * [`logLevel` (optional)](#loglevel-optional)
* [Related classes](#related-classes)

## Initialization

In order to work properly the Logger has to be initialized once at the
beginning of your pipeline script:

```groovy
// do the import
import io.wcm.tooling.jenkins.pipeline.utils.logging.LogLevel
import io.wcm.tooling.jenkins.pipeline.utils.logging.Logger

// initialize the logger with WorkflowScript reference (this 
Logger.init(this, [ logLevel: LogLevel.INFO ])
```

The logger needs a reference to the `DSL` instance which is the `steps`
object in a pipeline script

## Features

### Colorized output

Since version 0.11 of the pipeline library the Logger supports xterm color output.
The text remains black but the log level part like `[WARN]` will echoed using colors.

The colors used are from the 88/256 colors table.
:bulb: For more information have a look at [https://misc.flogisoft.com/bash/tip_colors_and_formatting](https://misc.flogisoft.com/bash/tip_colors_and_formatting#colors1)

The colorized output is enabled automatically when the ansiColor wrapper is used.
:bulb: You can use logs within and without the ansiColor plugin in the same project.
The logger detects the wrapper by checking for the `TERM` environment variable.

## LogLevels

The Logger supports the following LogLevels (from priority low to high, log level / color code)

|Name|Level (`int`)|Color|
|---|---|---|
|`ALL`|`0`|`0`|
|`TRACE`|`2`|`8`|
|`DEBUG`|`3`|`12`|
|`INFO`|`4`|`0`|
|`WARN`|`5`|`202`|
|`ERROR`|`6`|`5`|
|`FATAL`|`7`|`9`|
|`NONE`|`Integer.MAX_VALUE`|`0`|

If you want to show only `INFO` and above (e.g. `WARN`, `ERROR` or
`FATAL` set the log level to `LogLevel.INFO`.

If you want the logger to be as communicative as the pipeline is set the
`LogLevel` to `ALL`.

When you don't want to see any log message at all either do not
initialize the `Logger` or set the `LogLevel` to `NONE`

## Examples

### Example 1: Do a trace logging

```groovy
// do the import
import io.wcm.tooling.jenkins.pipeline.utils.logging.LogLevel
import io.wcm.tooling.jenkins.pipeline.utils.logging.Logger

// initialize the logger 
Logger.init(this, [ logLevel: LogLevel.TRACE ])
Logger log = new Logger(this)

log.trace("I am a trace log message")
```

Output:

    [INFO] [ScriptName] : I am a trace log message


### Example 2: Do a warning logging and hide loglevel below

```groovy
// do the import
import io.wcm.tooling.jenkins.pipeline.utils.logging.LogLevel
import io.wcm.tooling.jenkins.pipeline.utils.logging.Logger
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*

// initialize the logger 
Logger.init(this, [ (LOGLEVEL) : LogLevel.WARN] )
Logger log = new Logger(this)

log.trace("I am a trace log message")
log.warn("I am a warn log message")
```

Output:

    [WARN] [ScriptName] : I am a warn log message

### Example 3: Log an object

:bulb: Logging an object is limited when running in untrusted mode. The
`Logger` may fail to the the class name of the object to be logged, but
the `String` representation (`toString()`) should always work.

```groovy
// do the import
import io.wcm.tooling.jenkins.pipeline.utils.logging.LogLevel
import io.wcm.tooling.jenkins.pipeline.utils.logging.Logger
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*

Map config = [ (LOGLEVEL) : LogLevel.DEBUG ]

// initialize the logger 
Logger.init(this, config)
Logger log = new Logger(this)

log.debug("This is the config: ", config)
```

Output:

    [DEBUG] [ScriptName] : This is the config -> (LinkedHashMap) [logLevel:LogLevel.DEBUG] 

### Example 4: Colorized log output

This example will output all log levels with theis colors.

```groovy
// do the import
import io.wcm.tooling.jenkins.pipeline.utils.logging.LogLevel
import io.wcm.tooling.jenkins.pipeline.utils.logging.Logger
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*

Map config = [ (LOGLEVEL) : LogLevel.TRACE ]

// initialize the logger 
Logger.init(this, config)
Logger log = new Logger(this)

ansiColor('xterm') {
    log.trace("trace logging")
    log.debug("debug logging")
    log.info("info logging")
    log.warn("warn logging")
    log.error("error logging")
    log.fatal("fatal logging")    
}

```

## Configuration options

The logger has currently only one configuration option which must be at
the root level of the config to be evaluated.

### `logLevel` (optional)
|||
|---|---|
|Type|`String` or `LogLevel`|
|Default|`LogLevel.info`|

The log level for the logger

## Related classes
* [Logger](../src/io/wcm/tooling/jenkins/pipeline/utils/logging/Logger.groovy)
* [LogLevel](../src/io/wcm/tooling/jenkins/pipeline/utils/logging/LogLevel.groovy)
