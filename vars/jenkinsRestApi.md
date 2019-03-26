# jenkinsRestApi

Utility functions for Jenkins REST API.

# Table of contents

* [`findJobsByNameRegex`](#list-job-findjobsbynameregexstring-regex-string-remotehosturl-string-credentialsid-integer-depth--3)
* [Related classes](#related-classes)

## `List <Job> findJobsByNameRegex(String regex, String remoteHostUrl, String credentialsId, Integer depth = 3)`

This function will retrieve and return all Jobs from the Jenkins running
under `remoteHostUrl` and match the names against `regex`. For
authentication the `credentialsId` are required.

If you have the Cloudbees Folder Plugin installed the maximum depth can
be controlled with the `depth` parameter.

### Example

```groovy
import io.wcm.devops.jenkins.pipeline.model.jenkins.api.Job
List<Job> jobs = jenkinsRestApi.findJobsByNameRegex("example-.*-job-name", "https://jenkins.example.org", "org.example.jenkins.credentials")
```


## Related classes
* [`Job`](../src/io/wcm/devops/jenkins/pipeline/model/jenkins/api/Job.groovy)
