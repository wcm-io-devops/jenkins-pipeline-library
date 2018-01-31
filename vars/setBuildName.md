# setBuildName

This step will create a versionNumberString and set it as display name
for the build.

At the moment there are no configuration options for this step

:bulb: If you want to make the branch name appear in your build call the
[`setGitBranch`](setGitBranch.groovy) step before calling this step

:exclamation: This step requires the [Jenkins Version Number Plugin](https://wiki.jenkins.io/display/JENKINS/Version+Number+Plugin).

## `GIT_BRANCH` environment variable available

When the `GIT_BRANCH` environment variable is present this will be used format:

`#${BUILD_NUMBER}_${GIT_BRANCH}`

## `GIT_BRANCH` environment variable not available

`#${BUILD_NUMBER}`
