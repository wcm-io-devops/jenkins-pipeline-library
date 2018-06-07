# conditionalStage

The `conditionalStage` step allows you, as it says, to execute steps
based on a condition when using the imperative pipeline.

When the condition not evaluated to true the stage is skipped.

The step uses the
[`Utils`](https://github.com/jenkinsci/pipeline-model-definition-plugin/blob/master/pipeline-model-definition/src/main/groovy/org/jenkinsci/plugins/pipeline/modeldefinition/Utils.groovy)
from the pipeline-model-definition plugin to mark the stage as skipped.

Sadly this call needs administrative script approval to work.

#### `conditionalStage(String stageName, Boolean condition, Boolean throwException = true, Closure body)`

Creates a stage with the name provided with `stageName`.

When the `condition` is evaluated to `true` the body will be executed.

When the `condition` is evaluated to `false` the body will not be
executed and the stage is marked as skipped in the stages view.

The optional parameter `throwException` controls the exception throw
behavior. Set this parameter to false if you don't want to break the
build when the call to the pipeline-model-definition `Utils` fails with
a `RejectedAccessException` and you have no possibility to approve the
rejected signature.

**Example**
```groovy
Boolean executeStage = true

conditionalStage("stage", executeStage) {
  echo("hello world")
} 
```
