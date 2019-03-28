# jenkinsRestApi

Utility functions for Jenkins REST API.

# Table of contents

* [`findJobsByNameRegex(Map remote)`](#list-job-findjobsbynameregexmap-remote)
  * [Input](#input)
  * [Example](#example)
* [Related classes](#related-classes)

## `List <Job> findJobsByNameRegex(Map remote)`

This function will retrieve and return all matching Jobs from a Jenkins
instance.

### Input

| Key           | Type                     | Description                                                                  |
|:--------------|:-------------------------|:-----------------------------------------------------------------------------|
| regex         | String, **Mandatory**    | The regular expression the job name must match to                            |
| baseUrl       | String, **Mandatory**    | The baseUrl of the remove jenkins instance                                   |
| credentialsId | String, **Mandatory**    | The id of the credentials to use for authentication on remote                |
| depth         | Integer, defaults to `3` | The maximum depth to retrieve when Cloudbees Folder Plugin is used on remote |

### Example

```groovy
import io.wcm.devops.jenkins.pipeline.model.jenkins.api.Job

Map remote = [
    regex: ".*",
    baseUrl: "https://jenkins.example.org",
    credentialsId: "org.example.jenkins.credentials",
    depth: 4
]

List<Job> foundJobs = jenkinsRestApi.findJobsByNameRegex(remote)
```

## Related classes
* [`Job`](../src/io/wcm/devops/jenkins/pipeline/model/jenkins/api/Job.groovy)
