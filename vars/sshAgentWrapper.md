# sshAgentWrapper

This step provides an easy way to enable ssh keyagent support for your
shell commands.

When you use this wrapper the step will try to automatically provide the
correct key for the given targetUrl.

# Table of contents
* [Features](#features)
    * [SSH Credential auto lookup](#ssh-credential-auto-lookup)
* [Examples](#examples)
  * [Example 1: Simple usage](#example-1-simple-usage)
  * [Example 2: Use a credential aware command builder](#example-2-use-a-credential-aware-command-builder)
* [Related classes](#related-classes)

## Features
### SSH Credential auto lookup

Especially in company environments where you may have one SSH account
for your testing environments the `scpTransfer` task makes your life a
lot easier by automatically setting up the ssh keyagent.

If you provide a JSON file at this location
`resources/credentials/ssh/credentials.json` in the format described in
[Credentials](https://github.com/wcm-io-devops/jenkins-pipeline-library/blob/master/docs/credentials.md) the step will
automatically try to lookup the SSH credentials for the target host and
uses the credentials for authentication.

This step uses the best match by using the
[PatternMatcher](https://github.com/wcm-io-devops/jenkins-pipeline-library/blob/master/src/io/wcm/tooling/jenkins/pipeline/utils/PatternMatcher.groovy)
so the SSH credentials with the most matching characters will be used
for the sshagent.

:bulb: At the moment only authentication via SSH keys stored in the
Jenkins instance are supported.

## Examples

### Example 1: Simple usage

```groovy

String targetHost = "testserver.yourcompany.de"

sshAgentWrapper(targetHost)
    sh "ssh testuser@testserver.yourcompany.de 'pwd'"
}
```

### Example 2: Use a credential aware command builder

In this example we are providing the
[`CredentialAware`](../src/io/wcm/tooling/jenkins/pipeline/credentials/CredentialAware.groovy)
[`ScpCommandBuilderImpl`](../src/io/wcm/tooling/jenkins/pipeline/shell/ScpCommandBuilderImpl.groovy).
During the ssh credential autolookup the found credentials are
automatically added to the `SSHTarget` object. With this information we are
able to use the username configured in the credential for building the command.

```groovy
import io.wcm.tooling.jenkins.pipeline.shell.ScpCommandBuilderImpl
import io.wcm.tooling.jenkins.pipeline.ssh.SSHTarget 

ScpCommandBuilderImpl commandBuilder = new ScpCommandBuilderImpl((DSL) this.steps)
commandBuilder.setHost("testserver.yourcompany.de")
commandBuilder.setSourcePath("/path/to/source")
commandBuilder.setDestinationPath("/path/to/destination/")
commandBuilder.addArgument("-r")

SSHTarget sshTarget = new SSHTarget(host)

// use the sshAgentWrapper for ssh credential auto lookup
sshAgentWrapper([sshTarget]) {
    commandBuilder.setCredential(sshTarget.getCredential())
    // build the command
    command = commandBuilder.build()                
    // execute the command
    sh(command)
}
```

### Example 3: Multipe SSH targets

```groovy
import io.wcm.tooling.jenkins.pipeline.ssh.SSHTarget

List<SSHTarget> sshTargets = [
    new SSHTarget("testserver1.yourcompany.de"),
    new SSHTarget("testserver2.yourcompany.de"),
]

sshAgentWrapper(sshTargets)
    sh "ssh testuser@testserver1.yourcompany.de 'pwd'"
    sh "ssh testuser@testserver2.yourcompany.de 'pwd'"
}
```

# Related classes
* [`Credential`](../src/io/wcm/tooling/jenkins/pipeline/credentials/Credential.groovy)
* [`CredentialAware`](../src/io/wcm/tooling/jenkins/pipeline/credentials/CredentialAware.groovy)
* [`CredentialConstants`](../src/io/wcm/tooling/jenkins/pipeline/credentials/CredentialConstants.groovy)
* [`CredentialParser`](../src/io/wcm/tooling/jenkins/pipeline/credentials/CredentialParser.groovy)
* [`PatternMatcher`](../src/io/wcm/tooling/jenkins/pipeline/utils/PatternMatcher.groovy)
* [`SSHTarget`](../src/io/wcm/tooling/jenkins/pipeline/ssh/SSHTarget.groovy)
