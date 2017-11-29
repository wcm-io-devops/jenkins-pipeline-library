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
import io.wcm.tooling.jenkins.pipeline.model.Tool
import io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants
import io.wcm.tooling.jenkins.pipeline.utils.logging.Logger

/**
 * Main function to setup tools. Takes a list of
 *
 *  [ name: 'STRING', type: 'Tool', envVar: 'STRING' ]
 *
 * This step automatically adds the tool path to the PATH environment so it will be available for later usage
 *
 * @param config The config containing the tools to be setup inside tools node
 */
void call(Map config) {
    Logger log = new Logger(this)
    List<Map> toolsConfig = (List<Map>) config[ConfigConstants.TOOLS] ? config[ConfigConstants.TOOLS] : []

    for (Map toolConfig in toolsConfig) {
        doSetupTool(toolConfig, log)
    }
}

/**
 * Setups a tool based on the provided configuration. The path of the tool is automatically added to the PATH
 * environment for later usage.
 *
 * @param toolConfig A map with this structure: [ name: 'STRING', type: 'STRING', envVar: 'STRING' ]
 * @param log The logger instance from the main function
 */
void doSetupTool(Map toolConfig, Logger log) {
    // retrieve the configuration variables
    String toolName = toolConfig[ConfigConstants.TOOL_NAME]
    Tool toolType = (Tool) toolConfig[ConfigConstants.TOOL_TYPE]
    String toolEnvVar = toolConfig[ConfigConstants.TOOL_ENVVAR]

    // when no environment variable was provided do auto detection by using Tool enum
    if (toolEnvVar == null && toolType != null) {
        toolEnvVar = toolType.getEnvVar()
    }

    log.debug("Setting up '$toolName' with type '$toolType' to environment variable '$toolEnvVar'")

    // call the Jenkins pipeline method to install the tool and get back the path
    String retrievedTool = "${tool toolName}"

    // check of tool was retrieved correctly, otherwise abort
    if (retrievedTool == null || retrievedTool == "") {
        error("Tool '$toolName' not found, aborting")
    }

    // if environment variable is present, set the environment variable to the path of the tool
    if (toolEnvVar != null) {
        env.setProperty(toolEnvVar, retrievedTool)
        log.info "set environment var '$toolEnvVar' to: '${env.getProperty(toolEnvVar)}'"
    }

    // add the tool path to the PATH variable for later usage
    env.setProperty("PATH", "${retrievedTool}/bin:${env.PATH}")
    log.info "set environment var PATH to: ${env.PATH}"
}
