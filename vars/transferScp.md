# transferScp

This step provides an easy way to transfer files via SCP.


# Table of contents
* [Features](#features)
    * [SSH Credential auto lookup](#ssh-credential-auto-lookup)
    * [Path escaping](#path-escaping)
* [Examples](#examples)
  * [Example 1: Transfer a single file](#example-1-transfer-a-single-file)
  * [Example 2: Transfer multiple files (recursively)](#example-2-transfer-multiple-files-recursively)
* [Configuration options](#configuration-options)
    * [`arguments` (optional)](#arguments-optional)
    * [`destination`](#destination)
    * [`executable` (Optional)](#executable-optional)
    * [`host`](#host)
    * [`hostKeyCheck` (Optional)](#hostkeycheck-optional)
    * [`port` (Optional)](#port-optional)
    * [`recursive` (Optional)](#recursive-optional)
    * [`source`](#source)
    * [`user` (Optional)](#user-optional)
* [Related classes](#related-classes)

## Features
### SSH Credential auto lookup

This step is using the [`sshAgentWrapper`](sshAgentWrapper.md) to wrap
the shell command.

So if you configured the ssh credentials then the key is automatically
provided.

### Path escaping
You don't have to take care about path escaping! This step uses the  
[ScpCommandBuilderImpl](../src/io/wcm/tooling/jenkins/pipeline/shell/ScpCommandBuilderImpl.groovy)
which used the
[ShellUtils](../src/io/wcm/tooling/jenkins/pipeline/shell/ShellUtils.groovy)
to escape the paths for you.

```groovy
  (SCP) : [
    (SCP_HOST)       : "somehost",
    (SCP_RECURSIVE)  : true,
    (SCP_SOURCE)     : "path to source/*",
    (SCP_DESTINATION): "/var/www/target path with spaces/",
  ]
```

Will be escaped into
* `SCP_SOURCE` = `path\ to\ source/*`
* `SCP_DESTINATION` = `/var/www/target\ path\ with\ spaces/`

## Examples

### Example 1: Transfer a single file
```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
Map config = [
   // configure scp transport
  (SCP) : [
    (SCP_HOST)       : "your-target-host",
    (SCP_SOURCE)     : "target/index.html",
    (SCP_DESTINATION): "/var/www/your-target-folder/",
  ]
]

transferScp(config)
```

### Example 2: Transfer multiple files (recursively)
```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
Map config = [
   // configure scp transport
  (SCP) : [
    (SCP_HOST)       : "your-target-host",
    (SCP_RECURSIVE)  : true,
    (SCP_SOURCE)     : "target/*",
    (SCP_DESTINATION): "/var/www/your-target-folder/",
  ]
]

transferScp(config)
```

## Configuration options

Complete list of all configuration options.

All configuration options must be inside the `scp` ([`ConfigConstants.SCP`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)) map element to be
evaluated and used by the step.

The `scp` element must be a `Map`.
```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

transferScp([
    (SCP): [
        [ 
            (SCP_ARGUMENTS): [],
            (SCP_DESTINATION): '/path/to/target/dir-or-file',
            (SCP_EXECUTABLE): 'scp', 
            (SCP_HOST): 'scp-target-host', 
            (SCP_HOST_KEY_CHECK): false,
            (SCP_PORT): 22,
            (SCP_RECURSIVE): true,
            (SCP_SOURCE): '/path/to/source/dir-or-file',
            (SCP_USER): "scp-user", 
        ],
        // more tool definitions
    ]
])
```

### `arguments` (optional)
|||
|---|---|
|Constant|[`ConfigConstants.SCP_ARGUMENTS`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`List` of `String`|
|Default|`[]`|

Additional arguments for SCP like `-v` for verbose output.

`(SCP_ARGUMENTS): [ "-v" ]`

### `destination`
|||
|---|---|
|Constant|[`ConfigConstants.SCP_DESTINATION`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

### `executable` (Optional)
|||
|---|---|
|Constant|[`ConfigConstants.SCP_EXECUTABLE`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`scp`|

Defines the executable to use. Per default `transferScp` expects the executable `scp` to be in the `PATH`

### `host`
|||
|---|---|
|Constant|[`ConfigConstants.SCP_HOST`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

The scp destination host.

### `hostKeyCheck` (Optional)
|||
|---|---|
|Constant|[`ConfigConstants.SCP_HOST_KEY_CHECK`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`false`

Controls the host key check behavior. Per default the host key checking is disabled.
When set to `false` (default) ssh arguments are automatically added to the command line.

`-o "StrictHostKeyChecking=no" -o "UserKnownHostsFile=/dev/null"`

### `port` (Optional)
|||
|---|---|
|Constant|[`ConfigConstants.SCP_PORT`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Integer`|
|Default|`22`|

### `recursive` (Optional)
|||
|---|---|
|Constant|[`ConfigConstants.SCP_RECURSIVE`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`false`|

When set to true the `-r` argument is added to the command line and SCP will transfer in recursive mode.

### `source`
|||
|---|---|
|Constant|[`ConfigConstants.SCP_SOURCE`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

Path to the source which should be transferred.

### `user` (Optional)
|||
|---|---|
|Constant|[`ConfigConstants.SCP_USER`](../src/io/wcm/tooling/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

The name of the user to use. Per default the user is determined during SSH credential auto lookup.

## Related classes:
* [`Credential`](../src/io/wcm/tooling/jenkins/pipeline/credentials/Credential.groovy)
* [`CredentialConstants`](../src/io/wcm/tooling/jenkins/pipeline/credentials/CredentialConstants.groovy)
* [`CredentialParser`](../src/io/wcm/tooling/jenkins/pipeline/credentials/CredentialParser.groovy)
* [`PatternMatcher`](../src/io/wcm/tooling/jenkins/pipeline/utils/PatternMatcher.groovy)
* [`ScpCommandBuilderImpl`](../src/io/wcm/tooling/jenkins/pipeline/shell/ScpCommandBuilderImpl.groovy)
* [`ShellUtils`](../src/io/wcm/tooling/jenkins/pipeline/shell/ShellUtils.groovy)
