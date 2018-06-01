# managedScripts

The managedScripts allows you to execute managed scripts that are
provided by
* The ConfigFileProvider plugin or
* as pipeline resource

# Table of contents

* [`execJenkinsShellScript`](#execjenkinsshellscriptstring-scriptid-commandbuilder-commandbuilder-null-returnstdout--false-returnstatus--false)
* [`execPipelineShellScript`](#execpipelineshellscriptstring-scriptpath-commandbuilder-commandbuilder-null-returnstdout--false-returnstatus--false)
* [Related classes](#related-classes)

## `execJenkinsShellScript(String scriptId, CommandBuilder commandBuilder null, returnStdout = false, returnStatus = false)`

This step will execute the Jenkins script with the id `scriptId`. The
shell command is build using the provided command builder.

If you want to get the stdout from the execution set `returnStdout` to
`true`. If the status code is needed set `returnStatus` to `true`.

:exclamation: You can only set `returnStdout` or `returnStatus`. If both
are set the `returnStdout` setting will be used.

## `execPipelineShellScript(String scriptPath, CommandBuilder commandBuilder null, returnStdout = false, returnStatus = false)`

This step will execute a managed shell script from the pipeline-library
with path `scriptPath`. The shell command is build using the provided
command builder.

If you want to get the stdout from the execution set `returnStdout` to
`true`. If the status code is needed set `returnStatus` to `true`.

:exclamation: You can only set `returnStdout` or `returnStatus`. If both
are set the `returnStdout` setting will be used.

## Related classes
* [`CommandBuilder`](../src/io/wcm/devops/jenkins/pipeline/shell/CommandBuilder.groovy)

