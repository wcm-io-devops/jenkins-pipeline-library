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
import io.wcm.devops.jenkins.pipeline.environment.EnvironmentConstants
import io.wcm.devops.jenkins.pipeline.environment.EnvironmentUtils
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

  // lookup credentials or use the targetCredentialIds when not null
  srcCredentialIds = _lookupRepositoryCredentials(srcRepo, srcCredentialIds, log)

  // execute bare clone or fetch
  CommandBuilder cloneCommandBuilder = new GitCommandBuilderImpl(this.steps)

  Boolean repoExists = fileExists(srcRepo.getProject())

  _withGitCredentials(srcRepo, srcCredentialIds, log) {
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

        if (this.getFetchOrigin(remotes) != srcRepo.getUrl()) {
          log.fatal("Unable to verify that remote fetch target is pointing to '${srcRepo.getUrl()}'! Found remotes: \n$remotes")
          error("Unable to verify that remote fetch target is pointing to '${srcRepo.getUrl()}'! Found remotes: \n$remotes")
        }

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

  // lookup credentials or use the targetCredentialIds when not null
  targetCredentialIds = _lookupRepositoryCredentials(targetRepo, targetCredentialIds, log)
  _withGitCredentials(targetRepo, targetCredentialIds, log) {
    dir(srcRepoPath) {

      // change remote
      CommandBuilder setRemoteCommandBuilder = new GitCommandBuilderImpl(this.steps)
      setRemoteCommandBuilder.addArguments(["remote", "set-url", "--push origin", targetRepo.getUrl()])
      sh(setRemoteCommandBuilder.build())

      String remotes = this._getRemotes()

      if (this.getFetchOrigin(remotes) == targetRepo.getUrl()) {
        log.fatal("Unable to verify that remote fetch target is NOT pointing to '${targetRepo.getUrl()}'! Found remotes: \n$remotes")
        error("Unable to verify that remote fetch target is NOT pointing to '${targetRepo.getUrl()}'! Found remotes: \n$remotes")
      }

      if (this.getPushOrigin(remotes) != targetRepo.getUrl()) {
        log.fatal("Unable to verify that remote push target is pointing to '${targetRepo.getUrl()}'! Found remotes: \n$remotes")
        error("Unable to verify that remote push target is pointing to '${targetRepo.getUrl()}'! Found remotes: \n$remotes")
      }

      // push to target url
      CommandBuilder pushCommand = new GitCommandBuilderImpl(this.steps)
      pushCommand.addArguments(["push", "--mirror"])

      sh(pushCommand.build())
    }
  }
}

/**
 * Utility function to get the remotes of the target server
 * @return The result of the git remove -v call
 */
String _getRemotes() {
  // verify that remote push target is correct
  CommandBuilder getRemotePushTarget = new GitCommandBuilderImpl(this.steps)
  getRemotePushTarget.addArguments(["remote", "-v",])
  return sh(script: getRemotePushTarget.build(), returnStdout: true).trim()
}

/**
 * Utility function to retrieve the fetch origin
 * @param remotes String containing the remotes, when null the remotes will be automatically retrieved via _getRemotes
 *
 * @return The fetch origin, null when not found
 */
String getFetchOrigin(String remotes = null) {
  String ret = null
  if (remotes == null) {
    remotes = _getRemotes()
  }
  Matcher fetchMatcher = remotes =~ /origin\s+(.*)\s+\(fetch\)/
  if (fetchMatcher) {
    ret = fetchMatcher[0][1]
  }
  fetchMatcher == null
  return ret
}

/**
 * Utility function to retrieve the push origin
 * @param remotes String containing the remotes, when null the remotes will be automatically retrieved via _getRemotes
 *
 * @return The push origin, null when not found
 */
String getPushOrigin(String remotes = null) {
  String ret = null
  if (remotes == null) {
    remotes = _getRemotes()
  }
  Matcher pushMatcher = remotes =~ /origin\s+(.+)\s+\(push\)/
  if (pushMatcher) {
    ret = pushMatcher[0][1]
  }
  pushMatcher = null
  return ret
}

/**
 * Internal function providing GIT credentials for steps
 *
 * @param repo The repository to provide the credentials for
 * @param credentialIds The list of credentials that should be provided
 * @param log The logger instance
 * @param body The body that should be executed with the GIT credentials
 */
void _withGitCredentials(GitRepository repo, List credentialIds, Logger log, Closure body) {
  if (repo.isSsh()) {
    sshagent(credentialIds) {
      body()
    }
  } else if (repo.isHttp() || repo.isHttps()) {
    if (credentialIds.size() >= 1) {
      withCredentials([usernamePassword(credentialsId: credentialIds[0], passwordVariable: '_GIT_PASSWORD', usernameVariable: '_GIT_USERNAME')]) {
        String originalUsername = repo.getUsername()
        String sshAskPassPath = "${WORKSPACE}/.gitaskpass.sh"
        try {
          // set the username to the repository to make sure username is used during git operations
          repo.setUsername(env.getProperty("_GIT_USERNAME"))
          String gitPassword = env.getProperty("_GIT_PASSWORD")
          // create script for git askpass
          sh("echo '#!/bin/bash\necho \"$gitPassword\"' > '${sshAskPassPath}' && chmod 700 '${sshAskPassPath}'")
          withEnv(["GIT_ASKPASS=$sshAskPassPath"]) {
            body()
          }
        } finally {
          // reset username
          repo.setUsername(originalUsername)
          sh("rm -f '$sshAskPassPath'")
        }
      }
    } else {
      // no credentials found, execute without providing credentials
      body()
    }
  }
}

/**
 * Utility function to retrieve ssh or http/https credentials for a repository.
 * When the parameter credentialIds is null a autolookup is performed.
 * Otherwise the passed credentialIds will be returned
 *
 * @param repo The repository to lookup the credentials for
 * @param credentialIds When not empty the
 * @return A list of credential ids for a repository
 */
List<String> _lookupRepositoryCredentials(GitRepository repo, List credentialIds, Logger log) {
  // do credential auto lookup for clone operation
  if (credentialIds == null) {
    credentialIds = []
    if (repo.isSsh()) {
      log.debug("no credentialIds passed, repo is using ssh, try ssh credential auto lookup")
      Credential repoSshCredential = credentials.lookupSshCredential(repo.getUrl())
      if (repoSshCredential != null) {
        log.debug("using '${repoSshCredential.getComment()}' with id '${repoSshCredential.getId()}' for repo")
        credentialIds.push(repoSshCredential.getId())
      }
    } else if (repo.isHttps() || repo.isHttp()) {
      log.debug("no credentialIds passed, repo is using https, try http/https credential auto lookup")
      Credential repoHttpsCredential = credentials.lookupHttpCredential(repo.getUrl())
      if (repoHttpsCredential != null) {
        log.debug("using '${repoHttpsCredential.getComment()}' with id '${repoHttpsCredential.getId()}' for repo")
        credentialIds.push(repoHttpsCredential.getId())
      }
    }
  }
  return credentialIds
}

/**
 * Looks up the parent branch e.g. for feature branch merge operations.
 *
 * @return The name of the detected parent branch
 */
String getParentBranch() {
  Logger log = new Logger("getParentBranch")
  Integer branchResultCode = -1

  log.info("check if git repo has a remote branch named: 'origin/develop'")
  branchResultCode = sh(script: "git branch --list --remote | grep origin/develop", returnStatus: true)
  log.debug("git command result code for origin/develop:", branchResultCode)

  // origin/develop branch was found
  if (branchResultCode == 0) {
    return "origin/develop"
  }

  log.info("check if git repo has a remote branch named: 'origin/master'")
  branchResultCode = sh(script: "git branch --list --remote | grep origin/master", returnStatus: true)
  log.debug("git command result code for origin/master:", branchResultCode)

  // origin/develop branch was found
  if (branchResultCode == 0) {
    return "origin/master"
  }

  log.info("unable to detect parent branch, returning", null)

  return null
}

/**
 * Detects and returns the branch name.
 * The result is also stored in the environment variable EnvironmentConstants.GIT_BRANCH
 * when EnvironmentConstants.GIT_BRANCH was not set before
 *
 * @return The name of the git branch or the short commit hash
 */
String getBranch() {
  Logger log = new Logger("gitTools.getBranch")
  log.debug("try to retrieve branch from environment vars.")

  EnvironmentUtils envUtils = new EnvironmentUtils(this)
  List<String> branchNameEnvs = [
    EnvironmentConstants.GIT_BRANCH,
    EnvironmentConstants.GIT_LOCAL_BRANCH,
    EnvironmentConstants.BRANCH_NAME,
  ]
  String result = envUtils.getFirstFound(branchNameEnvs)

  // when result is still null get the commit hash
  try {
    if (result == null) {
      log.info("unable to determine git branch name via environment variables, using commit hash instead")
      gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim().take(6)
      result = gitCommit
    }
  }
  catch (Exception ex) {
    log.warn("unable to determine git branch, set to empty string")
    result = ""
  }

  envUtils.setEnvWhenEmpty(EnvironmentConstants.GIT_BRANCH, result)

  return result
}