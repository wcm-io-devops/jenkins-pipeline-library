/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io DevOps
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
import io.wcm.tooling.jenkins.pipeline.environment.EnvironmentConstants
import io.wcm.tooling.jenkins.pipeline.utils.logging.Logger
import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * This step brings back the GIT_BRANCH variable by trying several methods to determine the current git branch.
 * The most reliably way is configure the scm checkout to use the "LocalBranch" extension so the step will be able to
 * read it via shell command.
 */
String call() {
    Logger log = new Logger(this)

    String result = null

    // try to retrieve via existing GIT_BRANCH env var
    try {
        result = env.getProperty(EnvironmentConstants.GIT_BRANCH)
    } catch (e) {
        log.trace("Tried to retrieve GIT_BRANCH from environment variable GIT_BRANCH but got exception", e)
    }
    if (result == null) {
        // try to retrieve via existing BRANCH_NAME env var when running in multibranch pipeline builds
        try {
            result = env.getProperty(EnvironmentConstants.BRANCH_NAME)
        } catch (e) {
            log.trace("Tried to retrieve GIT_BRANCH from environment variable BRANCH_NAME but got exception", e)
        }
    }

    // if no result found try to retrieve it via git command line
    if (result == null) {
        log.debug("no git branch determined via log analysis, using fallback to shell")
        try {
            // call git branch command (make sure to enable "LocalBranch" extension during checkout to make this work
            String localGitBranchResult = sh(returnStdout: true, script: 'git branch').trim()
            // try to retieve pattern like "* develop"
            def matcher = (localGitBranchResult =~ /\*\s([^(].*)/)
            result = matcher ? matcher[0][1] : null
            // reset matcher since matcher is not serializable!
            matcher = null
        } catch (Exception ex) {
            // catch exception when checkout to subfolder
            // TODO: Add support for checking out into subfolder
            // setting empty result since following call will also fail
            result = ""
        }

        // when result is still null get the commit hash
        if (result == null) {
            gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
            result = gitCommit.take(6)
        }
    }

    env.setProperty(EnvironmentConstants.GIT_BRANCH, result)
    log.info "set environment var GIT_BRANCH to '${env.getProperty(EnvironmentConstants.GIT_BRANCH)}'"

    return result
}
