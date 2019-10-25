# gitTools

This part of the pipeline provides utility steps for common GIT operations.

# Table of contents
* [`mirrorRepository`](#mirrorrepositorystring-srcurl-string-targeturl-liststring-srccredentialids--null-liststring-targetcredentialids--null)
* [`mirrorRepositoryToRemote`](#mirrorrepositorytoremotestring-srcrepopath-gitrepository-targetrepo-liststring-targetcredentialids--null)
* [`mirrorRepositoryToWorkspace`](#mirrorrepositorytoworkspacegitrepository-srcrepo-liststring-srccredentialids--null)
* [`getFetchOrigin`](#string-getfetchoriginstring-remotes--null)
* [`getPushOrigin`](#string-getpushoriginstring-remotes--null)
* [`getParentBranch`](#string-getparentbranch)

#### `mirrorRepository(String srcUrl, String targetUrl, List<String> srcCredentialIds = null, List<String> targetCredentialIds = null)`

This step is a combination of:
* [`mirrorRepositoryToWorkspace(...)`](#mirrorrepositorytoworkspacegitrepository-srcrepo-liststring-srccredentialids--null)
* [`mirrorRepositoryToRemote(...)`](#mirrorrepositorytoremotestring-srcrepopath-gitrepository-targetrepo-liststring-targetcredentialids--null)

This steps mirrors a GIT repository from `srcUrl` to `targetUrl` When no
`srcCredentialIds` or `targetCredentialIds` are provided a auto lookup
of the SSH or http(s) credentials will be performed.

#### `mirrorRepositoryToRemote(String srcRepoPath, GitRepository targetRepo, List<String> targetCredentialIds = null)`

Mirrors a local bare cloned repository form `$WORKSPACE/$srcRepoPath` to
the `targetRepo`. When no `targetCredentialIds` are provided a auto
lookup of the SSH or http(s) credentials will be performed.

#### `mirrorRepositoryToWorkspace(GitRepository srcRepo, List<String> srcCredentialIds = null)`

Mirrors the `srcRepo` to the current workspace.
When no `srcCredentialIds` are provided a auto lookup of the SSH or http(s) credentials will be performed.

#### `String getFetchOrigin(String remotes = null)`

Utility function to get the fetch origin from a git remote list (`git
remote -v`). When no `remotes` are provided the step will try to
retrieve them by using the internal function `_getRemotes` which
basically executes a `git remote -v`.

#### `String getPushOrigin(String remotes = null)`

Utility function to get the push origin from a git remote list (`git
remote -v`). When no `remotes` are provided the step will try to
retrieve them by using the internal function `_getRemotes` which
basically executes a `git remote -v`.

#### `String getParentBranch()`

Utility function to get the name of the parent branch. At the moment
this is limited to the following logic:

1.   Use `origin/develop` as default
2.   When there is no `origin/develop` branch in the remote branch list
     use `origin/master`
