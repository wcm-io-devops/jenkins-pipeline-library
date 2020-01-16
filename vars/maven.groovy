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


import io.wcm.devops.jenkins.pipeline.shell.CommandBuilder
import io.wcm.devops.jenkins.pipeline.shell.CommandBuilderImpl
import io.wcm.devops.jenkins.pipeline.utils.logging.LogLevel

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

/**
 * Adapter function to allow execution by providing a config map.
 *
 * @param config
 */
void purgeSnapshots(Map config) {
  Map purgeConfig = config[MAVEN_PURGE_SNAPSHOTS] ?: [:]
  String repoPath= purgeConfig[MAVEN_PURGE_SNAPSHOTS_REPO_PATH] ?: null
  Boolean dryRun = purgeConfig[MAVEN_PURGE_SNAPSHOTS_DRY_RUN] != null ? purgeConfig[MAVEN_PURGE_SNAPSHOTS_DRY_RUN] : false
  LogLevel logLevel = (LogLevel) purgeConfig[MAVEN_PURGE_SNAPSHOTS_LOG_LEVEL] ?: null
  purgeSnapshots(repoPath, dryRun, logLevel)
}

/**
 * Purges SNAPSHOT artifacts from the given repository
 *
 * @param repositoryPath The repository path (e.g. $HOME/.m2/repository), set to null to use default
 * @param dryRun Set to true if you only want to see what the step will do
 * @param logLevel The log level of the managed pipeline shell script
 */
void purgeSnapshots(String repositoryPath = null, Boolean dryRun = false, LogLevel logLevel = null) {
  CommandBuilder commandBuilder = new CommandBuilderImpl(this.steps)
  if (repositoryPath != null) {
    commandBuilder.addArgument("--repo='$repositoryPath'")
  }
  if (dryRun) {
    commandBuilder.addArgument("--dryrun")
  }
  if (logLevel) {
    commandBuilder.addArgument("--loglvl=${logLevel.level}")
  }
  managedScripts.execPipelineShellScript("jenkins-pipeline-library/managedScripts/shell/maven/purge-snapshots.sh", commandBuilder)
}
