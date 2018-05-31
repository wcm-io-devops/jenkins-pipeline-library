/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io DevOps
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
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import io.wcm.devops.jenkins.pipeline.utils.resources.LibraryResource
import org.jenkinsci.plugins.workflow.cps.DSL

/**
 * Executes a managed shell script from the ConfigFileProvider Plugin
 *
 * @param scriptId The id of the script to execute
 * @param commandBuilder The CommandBuilder used for building the command line
 * @param returnStdout When set to true the stdout will be returned
 * @param returnStatus When set to true the status code will be returned
 * @return stdout, status code or sh step result, depending on the selection
 */
Object execJenkinsShellScript(String scriptId, CommandBuilder commandBuilder, returnStdout = false, returnStatus = false) {
  Logger log = new Logger('execJenkinsShellScript')
  log.debug("scriptId", scriptId)
  String tmpScriptPath = '.jenkinsShellScript_' + scriptId

  Object ret = null

  // get the managed file via the configFileProvider step
  configFileProvider([configFile(fileId: scriptId, targetLocation: tmpScriptPath)]) {
    ret = _execShellScript(log, tmpScriptPath, commandBuilder, returnStdout, returnStatus)
  }
  return ret
}

/**
 * Executes a managed shell script from the pipeline library
 *
 * @param scriptPath The path to the script
 * @param commandBuilder The CommandBuilder used for building the command line
 * @param returnStdout When set to true the stdout will be returned
 * @param returnStatus When set to true the status code will be returned
 * @return stdout, status code or sh step result, depending on the selection
 */
Object execPipelineShellScript(String scriptPath, CommandBuilder commandBuilder, returnStdout = false, returnStatus = false) {
  Logger log = new Logger('execPipelineShellScript')
  String tmpScriptPath = '.libraryShellScript_' + scriptPath.replace('/','___')
  log.info("provide pipelin shell script from '$scriptPath' to '$tmpScriptPath'")
  LibraryResource pipelineScriptResource = new LibraryResource(this.steps, scriptPath)
  String scriptContent = pipelineScriptResource.load()
  writeFile(encoding: 'UTF-8', file: tmpScriptPath, text: scriptContent)
  return _execShellScript(log, tmpScriptPath, commandBuilder, returnStdout, returnStatus)
}

/**
 * Internal function to execute a managed shell script.
 *
 * @param log The Logger instance
 * @param scriptPath Path to the script that should be executed
 * @param commandBuilder The CommandBuilder used for building the command line
 * @param returnStdout When set to true the stdout will be returned
 * @param returnStatus When set to true the status code will be returned
 * @return stdout, status code or sh step result, depending on the selection
 */
Object _execShellScript(Logger log, String scriptPath, CommandBuilder commandBuilder, returnStdout = false, returnStatus = false) {
  log.debug("scriptPath: '$scriptPath', returnStdout: $returnStdout, returnStatus: $returnStatus")
  if (returnStatus == true && returnStdout == true) {
    log.warn("returnStatus and returnStdout are set to true, only one parameter is allowed to be true, using returnStdout")
  }
  // mark script as executable
  CommandBuilderImpl chmodBuilder = new CommandBuilderImpl((DSL) steps, "chmod")
  chmodBuilder.addArgument("+x")
  chmodBuilder.addPathArgument(scriptPath)
  String chmodCommand = chmodBuilder.build()
  sh(chmodCommand)

  // build shell command for executing managed script
  commandBuilder.setExecutable("./$scriptPath")
  String command = commandBuilder.build()

  // execute the command
  log.info("Executing command: $command")

  Object ret = null

  if (returnStdout == true) {
    ret = sh(returnStdout: true, script: command).trim()
  } else if (returnStatus == true) {
    ret = sh(returnStatus: true, script: command)
  } else {
    ret = sh(script: command)
  }
  return ret
}
