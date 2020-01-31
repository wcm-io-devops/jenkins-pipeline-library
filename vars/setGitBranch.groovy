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
import io.wcm.devops.jenkins.pipeline.environment.EnvironmentConstants
import io.wcm.devops.jenkins.pipeline.environment.EnvironmentUtils
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * This step brings back the GIT_BRANCH variable by trying several methods to determine the current git branch.
 * The most reliably way is configure the scm checkout to use the "LocalBranch" extension so the step will be able to
 * read it via shell command.
 *
 * @deprecated migrated to gitTools.getBranch()
 */
String call() {
    Logger log = new Logger(this)
    log.deprecated("setGitBranch()", "gitTools.getBranch()")
    return gitTools.getBranch()
}
