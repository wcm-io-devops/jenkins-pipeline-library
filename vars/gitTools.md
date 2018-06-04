# gitTools

This part of the pipeline provides utility steps for common GIT operations.

# Table of contents
* [`mirrorRepository`](#mirrorrepositorystring-srcurl-string-targeturl-liststring-srccredentialids--null-liststring-targetcredentialids--null)
* [`mirrorRepositoryToRemote`](#mirrorrepositorytoremotestring-srcrepopath-gitrepository-targetrepo-liststring-targetcredentialids--null)
* [`mirrorRepositoryToWorkspace`](#mirrorrepositorytoworkspacegitrepository-srcrepo-liststring-srccredentialids--null)

## `mirrorRepository(String srcUrl, String targetUrl, List<String> srcCredentialIds = null, List<String> targetCredentialIds = null)`

This step is a combination of:
* [`mirrorRepositoryToWorkspace(...)`](#mirrorrepositorytoworkspacegitrepository-srcrepo-liststring-srccredentialids--null)
* [`mirrorRepositoryToRemote(...)`](#mirrorrepositorytoremotestring-srcrepopath-gitrepository-targetrepo-liststring-targetcredentialids--null)

This steps mirrors a GIT repository from `srcUrl` to `targetUrl`
When no `srcCredentialIds` or `targetCredentialIds` are provided a auto lookup of the ssh credentials will be performed.

## `mirrorRepositoryToRemote(String srcRepoPath, GitRepository targetRepo, List<String> targetCredentialIds = null)`

Mirrors a local bare cloned repository form `$WORKSPACE/$srcRepoPath` to the  `targetRepo`.
When no `targetCredentialIds` are provided a auto lookup of the ssh credentials will be performed.

## `mirrorRepositoryToWorkspace(GitRepository srcRepo, List<String> srcCredentialIds = null)`

Mirrors the `srcRepo` to the current workspace.
When no `srcCredentialIds` are provided a auto lookup of the ssh credentials will be performed.

