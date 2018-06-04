/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2018 wcm.io DevOps
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import io.wcm.devops.jenkins.pipeline.credentials.Credential
import io.wcm.devops.jenkins.pipeline.scm.GitRepository
import io.wcm.devops.jenkins.pipeline.shell.CommandBuilder
import io.wcm.devops.jenkins.pipeline.shell.GitCommandBuilderImpl
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger

import java.util.regex.Matcher

/**
 * Mirrors a GIT repository from src to remote.
 * This step only supports SSH connections.
 *
 * @param srcUrl The url of the source repository
 * @param targetUrl The url of the target repository
 * @param srcCredentialIds List of credential ids for the ssh agent during clone/fetch (optional)
 * @param targetCredentialIds List of credential ids for the ssh agent during push (optional)
 */
void mirrorRepository(String srcUrl, String targetUrl, List<String> srcCredentialIds = null, List<String> targetCredentialIds = null) {
  Logger log = new Logger("mirrorRepository")
  log.info("Mirror repo from '$srcUrl' to '$targetUrl'")
  GitRepository srcRepo = new GitRepository(this, srcUrl)
  GitRepository targetRepo = new GitRepository(this, targetUrl)

  if (srcRepo.getServer() == targetRepo.getServer()) {
    log.fatal("Source and target server are identical! This setup is forbidden to avoid mistakes!")
    error("Source and target server are identical! This setup is forbidden to avoid mistakes!")
  }

  // mirror repository to local
  this.mirrorRepositoryToWorkspace(srcRepo, srcCredentialIds)

  // mirror repository to remote
  this.mirrorRepositoryToRemote(srcRepo.getProject(), targetRepo, targetCredentialIds)
}

/**
 * Mirrors a GIT repository to the current workspace
 *
 * @param srcRepo The source repository
 * @param srcCredentialIds List of credential ids for the ssh agent during clone/fetch (optional)
 */
void mirrorRepositoryToWorkspace(GitRepository srcRepo, List<String> srcCredentialIds = null) {
  Logger log = new Logger("mirrorRepositoryToWorkspace")
  if (!srcRepo.isValid()) {
    log.fatal("The provided source repository is invalid!")
    error("The provided source repository is invalid!")
  }

  if (!srcRepo.isSsh()) {
    log.fatal("Only ssh repositories are supported for the source repository!")
    error("Only ssh repositories are supported for the source repository!")
  }

  // do credential auto lookup for clone operation
  if (srcCredentialIds == null) {
    srcCredentialIds = []
    log.debug("no srcCredentialId passed, try ssh credential auto lookup")
    Credential srcCredential = credentials.lookupSshCredential(srcRepo.getUrl())
    if (srcCredential != null) {
      srcCredentialId = srcCredential.getId()
      log.debug("using '${srcCredential.getComment()}' with id '${srcCredential.getId()}' for source server")
      srcCredentialIds.push(srcCredentialId)
    }
  }

  // execute bare clone or fetch
  CommandBuilder cloneCommandBuilder = new GitCommandBuilderImpl(this.steps)
  sshagent(srcCredentialIds) {
    Boolean repoExists = fileExists(srcRepo.getProject())
    if (!repoExists) {
      log.info("no existing repository found, mirror external repository")
      cloneCommandBuilder.addArguments(["clone", "--mirror", srcRepo.getUrl(), srcRepo.getProject()])
      // execute clone or fetch command
      sh(cloneCommandBuilder.build())
    } else {
      // change to directory for fetch
      dir(srcRepo.getProject()) {
        // check remotes
        String remotes = this._getRemotes()

        // check that fetch origin is correct
        Matcher fetchMatcher = remotes =~ /origin\s+${srcRepo.getUrl()}\s+\(fetch\)/

        if (!fetchMatcher) {
          log.fatal("Unable to verify that remote fetch target is pointing to '${srcRepo.getUrl()}'!")
          error("Unable to verify that remote fetch target is pointing to '${srcRepo.getUrl()}'!")
        }

        // unset matcher vars because they are not serializable
        fetchMatcher = null

        log.info("existing mirror found in workspace, update using fetch")
        cloneCommandBuilder.addArguments(["fetch", "-p origin"])
        // execute clone or fetch command
        sh(cloneCommandBuilder.build())
      }
    }
  }
}

/**
 * Mirrors a local bare cloned repository to a target server
 *
 * @param srcRepoPath The path to the GIT repository in the current workspace
 * @param targetRepo The target repository
 * @param targetCredentialIds List of credential ids for the ssh agent during push (optional)
 */
void mirrorRepositoryToRemote(String srcRepoPath, GitRepository targetRepo, List<String> targetCredentialIds = null) {
  Logger log = new Logger("mirrorRepositoryToRemote")
  if (!targetRepo.isValid()) {
    log.fatal("The provided target repository is invalid!")
    error("The provided target repository is invalid!")
  }

  if (!targetRepo.isSsh()) {
    log.fatal("Only ssh repositories are supported for the target repository!")
    error("Only ssh repositories are supported for the target repository!")
  }

  // do credential auto lookup for clone operation
  if (targetCredentialIds == null) {
    targetCredentialIds = []
    log.debug("no targetCredentialId passed, try ssh credential auto lookup")
    Credential targetCredential = credentials.lookupSshCredential(targetRepo.getUrl())
    if (targetCredential != null) {
      targetCredentialId = targetCredential.getId()
      log.debug("using '${targetCredential.getComment()}' with id '${targetCredential.getId()}' for target server")
      targetCredentialIds.push(targetCredentialId)
    }
  }

  dir(srcRepoPath) {

    // change remote
    CommandBuilder setRemoteCommandBuilder = new GitCommandBuilderImpl(this.steps)
    setRemoteCommandBuilder.addArguments(["remote", "set-url", "--push origin", targetRepo.getUrl()])
    sh(setRemoteCommandBuilder.build())

    String remotes = this._getRemotes()

    // check that push origin is correct
    Matcher fetchMatcher = remotes =~ /origin\s+${targetRepo.getUrl()}\s+\(fetch\)/
    Matcher pushMatcher = remotes =~ /origin\s+${targetRepo.getUrl()}\s+\(push\)/

    if (fetchMatcher) {
      log.fatal("Unable to verify that remote fetch target is NOT pointing to '${targetRepo.getUrl()}'!")
      error("Unable to verify that remote fetch target is NOT pointing to '${targetRepo.getUrl()}'!")
    }

    if (!pushMatcher) {
      log.fatal("Unable to verify that remote push target is pointing to '${targetRepo.getUrl()}'!")
      error("Unable to verify that remote push target is pointing to '${targetRepo.getUrl()}'!")
    }

    // unset matcher vars because they are not serializable
    pushMatcher = null
    fetchMatcher = null

    // push to target url
    CommandBuilder pushCommand = new GitCommandBuilderImpl(this.steps)
    pushCommand.addArguments(["push", "--mirror"])

    sshagent(targetCredentialIds) {
      sh(pushCommand.build())
    }
  }
}

String _getRemotes() {
  // verify that remote push target is correct
  CommandBuilder getRemotePushTarget = new GitCommandBuilderImpl(this.steps)
  getRemotePushTarget.addArguments(["remote", "-v",])
  return sh(script: getRemotePushTarget.build(), returnStdout: true).trim()
}
