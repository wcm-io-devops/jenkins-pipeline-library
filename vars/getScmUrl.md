# getScmUrl(Map config = [:], Boolean jobNameFallback = false)

The `getScmUrl` is a utility step which will return the url of the current SCM.

The step tries to retrieve the scm url from the config object first and
does then a fallback to the environment variables `SCM_URL` and then `GIT_URL`.

If even the `SCM_URL` and `GIT_URL` variables are not set the `JOB_NAME` environment
variable is used when `jobNameFallback` is enabled.

This step is used for example by:
* [`execMaven`](execMaven.md)
* [`execNpm`](execNpm.md)


