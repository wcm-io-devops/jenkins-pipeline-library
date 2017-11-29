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
package io.wcm.tooling.jenkins.pipeline.shell

import com.cloudbees.groovy.cps.NonCPS
import io.wcm.tooling.jenkins.pipeline.credentials.Credential
import io.wcm.tooling.jenkins.pipeline.credentials.CredentialAware
import io.wcm.tooling.jenkins.pipeline.utils.logging.Logger
import org.jenkinsci.plugins.workflow.cps.DSL

import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*

/**
 * Utility for building scp commands
 */
class ScpCommandBuilderImpl implements CommandBuilder, CredentialAware, ConfigAwareCommandBuilder, Serializable {

  private static final long serialVersionUID = 1L

  /**
   * Default executable
   */
  public static final String EXECUTABLE = "scp"

  /**
   * The host to connect to
   */
  String host = null

  /**
   * The user to use
   */
  String user = null

  /**
   * The scp destination path
   */
  String destinationPath = null

  /**
   * The source path on the local machine
   */
  String sourcePath = null

  /**
   * Logger instance
   */
  Logger log = new Logger(this)

  /**
   * Wrapped command builder since inheritance causes problems in Groovy Sandbox
   */
  CommandBuilderImpl commandBuilder

  /**
   * Credentials for SSH
   */
  Credential credentials

  /**
   * @param dsl The DSL object of the current pipeline script (available via this.steps in pipeline scripts)
   * @param executable The executable, default: 'scp'
   */
  ScpCommandBuilderImpl(DSL dsl, String executable = null) {
    commandBuilder = new CommandBuilderImpl(dsl, executable ?: EXECUTABLE)
    this.reset()
  }

  /**
   * Applies a given map configuration to the command builder
   *
   * @param config Map with configration values
   */
  @NonCPS
  ConfigAwareCommandBuilder applyConfig(Map config) {
    commandBuilder.executable = config[SCP_EXECUTABLE] ?: "scp"
    this.user = config[SCP_USER] ?: null
    this.host = config[SCP_HOST] ?: null
    this.sourcePath = config[SCP_SOURCE] ?: null
    this.destinationPath = config[SCP_DESTINATION] ?: null

    Integer port = (Integer) config[SCP_PORT] ?: 22
    Boolean recursive = config[SCP_RECURSIVE] ?: false
    List arguments = (List) config[SCP_ARGUMENTS] ?: []
    Boolean scpHostKeyCheck = config[SCP_HOST_KEY_CHECK] ?: false

    // add arguments
    this.addArguments(arguments)
    // add port
    this.addArgument("-P", port.toString())
    // add recursive if configured
    if (recursive) {
      this.addArgument("-r")
    }
    // check if ssh host key checking has to be disabled
    if (!scpHostKeyCheck) {
      this.addArgument("-o", "StrictHostKeyChecking=no")
      this.addArgument("-o", "UserKnownHostsFile=/dev/null")
    }
    return this
  }

  /**
   * Used to set the username based on a Credential found by auto lookup
   *
   * @param credential The credential object to use the username from (if set)
   */
  @NonCPS
  void setCredential(Credential credential) {
    this.credentials = credential
    if (this.user == null && this.credentials != null && credential.getUserName() != null) {
      this.user = credential.getUserName()
    }
  }

  @Override
  Credential getCredential() {
    return this.credentials
  }
/**
 * Builds the commandline by first calling the build function of superclass and then adding
 * - shell escaped source path
 * - user and host
 * - shell escaped destination path
 *
 * @return The scp command line
 */
  @NonCPS
  CommandBuilder addArgument(String argument) {
    commandBuilder.addArgument(argument)
    return this
  }

  /**
   * @see CommandBuilder#addPathArgument(java.lang.String)
   */
  @NonCPS
  CommandBuilder addPathArgument(String argument) {
    commandBuilder.addPathArgument(argument)
    return this
  }

  /**
   * @see CommandBuilder#addPathArgument(java.lang.String, java.lang.String)
   */
  @NonCPS
  CommandBuilder addPathArgument(String argumentName, String value) {
    commandBuilder.addPathArgument(argumentName, value)
    return this
  }

  /**
   * @see CommandBuilder#addArgument(java.lang.String, java.lang.String)
   */
  @NonCPS
  CommandBuilder addArgument(String argumentName, String argumentValue) {
    commandBuilder.addArgument(argumentName, argumentValue)
    return this
  }

  /**
   * Builds the command line for SCP by using the wrapped command builder and
   * adding the specific scp arguments.
   *
   * @see CommandBuilder#build()
   */
  @NonCPS
  String build() {
    String baseCommand = commandBuilder.build()
    if (host == null || destinationPath == null || sourcePath == null) {
      log.fatal("One of the mandatory properties is not set! (host: $host, destinationPath: $destinationPath, sourcePath: $sourcePath)")
      // exits and throws HudsonAbortException
      commandBuilder.dsl.error("One of the mandatory properties is not set! (host: $host, destinationPath: $destinationPath, sourcePath: $sourcePath)")
    }
    String escapedDestinationPath = ShellUtils.escapePath(destinationPath)
    String escapedSourcePath = ShellUtils.escapePath(sourcePath)

    // calculate destination
    // add user when defined
    String destination = user ? "$user@" : ""
    // add the host
    destination = "$destination$host:"
    // add the destination path surrounded by double quotes since it should be evaluated on target server
    destination = destination + "\"$escapedDestinationPath\""

    // append to the existing command and return
    return "$baseCommand $escapedSourcePath $destination"
  }

  @NonCPS
  CommandBuilder addArguments(String arguments) {
    commandBuilder.addArguments(arguments)
    return this
  }

  @NonCPS
  CommandBuilder addArguments(List<String> arguments) {
    commandBuilder.addArguments(arguments)
    return this
  }

  @Override
  @NonCPS
  CommandBuilder reset() {
    host = null
    user = null
    destinationPath = null
    sourcePath = null
    credentials = null
    commandBuilder.reset()
    return this
  }

  @Override
  @NonCPS
  CommandBuilder setExecutable(String executable) {
    commandBuilder.setExecutable(executable)
    return this
  }
}
