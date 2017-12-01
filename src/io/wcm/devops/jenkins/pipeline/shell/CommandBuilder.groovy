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

package io.wcm.devops.jenkins.pipeline.shell

import com.cloudbees.groovy.cps.NonCPS
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings

/**
 * Interface all command builders have to implement
 */
interface CommandBuilder {

  /**
   * Adds an argument to the list
   *
   * @param argument The argument to add
   * @return The current instance
   */
  @NonCPS
  CommandBuilder addArgument(String argument)

  /**
   * Adds a path argument.
   * The provided argument will be escaped for shell usage before adding to arguments list.
   *
   * @param argument The path argument to add
   * @return The current instance
   */
  @NonCPS
  CommandBuilder addPathArgument(String argument)

  /**
   * Adds a path argument with argument name and value e.g. --path /some/path
   * The provided argument will be escaped for shell usage before adding to arguments list.
   *
   * @param argumentName The name of the argument
   * @param value The value of the argument
   * @return The current instance
   */
  @NonCPS
  CommandBuilder addPathArgument(String argumentName, String value)

  /**
   * Adds a path argument with argument name and value e.g. --prop value
   *
   * @param argumentName The name of the argument
   * @param argumentValue The value of the argument
   * @return The current instance
   */
  @NonCPS
  CommandBuilder addArgument(String argumentName, String argumentValue)

  /**
   * Builds the command line by joining all provided arguments using space
   *
   * @return The command line that can be called by the 'sh' step
   */
  @NonCPS
  String build()

  /**
   * Adapter function for arguments provided as string
   *
   * @param arguments The argument String to be added
   * @return The current instance
   */
  @NonCPS
  CommandBuilder addArguments(String arguments)

  /**
   * Adds a list of arguments
   *
   * @param arguments A List of String containing 0-n arguments to be added
   * @return The current instance
   */
  @NonCPS
  @SuppressFBWarnings('SE_NO_SERIALVERSIONID')
  CommandBuilder addArguments(List<String> arguments)

  /**
   * Resets all arguments in the command builder
   *
   * @return The current instance
   */
  @NonCPS
  CommandBuilder reset()

  /**
   * Sets the executable
   * @param executable
   * @return The current instance
   */
  @NonCPS
  CommandBuilder setExecutable(String executable)

}
