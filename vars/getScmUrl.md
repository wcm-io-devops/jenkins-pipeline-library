# getScmUrl

The `getScmUrl` is a utility step which will return the url of the current SCM.

The step tries to retrieve the scm url from the config object first and
does then a fallback to the `SCM_URL` environment variable.

If even the `SCM_URL` variable is not set the `JOB_NAME` environment variable is used.

This step is used for example by:
* [`execMaven`](execMaven.md)
* [`execNpm`](execNpm.md)


