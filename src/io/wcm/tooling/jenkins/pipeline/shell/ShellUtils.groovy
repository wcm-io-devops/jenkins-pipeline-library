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

/**
 * Utilities for the shell
 */
class ShellUtils implements Serializable {

  private static final long serialVersionUID = 1L

  /**
   * List of all character that must be escaped for shell usage
   */
  static escapeCharacters = [
      "\\",
      " ",
      '"',
      "'",
      "!",
      "#",
      "\$",
      "&",
      "(",
      ")",
      ",",
      ";",
      "<",
      ">",
      "?",
      "[",
      "]",
      "^",
      "`",
      "{",
      "}",
      "|"
  ]

  /**
   * Escapes an incoming path by removing quotes and escaping it for shell
   *
   * @param path The path to be escaped
   * @return the escaped path
   */
  @NonCPS
  static String escapePath(String path) {
    if (path == null) return null
    // qualified reference due to GroovySandBox
    path = ShellUtils.trimDoubleQuote(path)
    path = ShellUtils.trimSingleQuote(path)
    path = ShellUtils.escapeShellCharacters(path)
    return path
  }

  /**
   * Removes one double quote at beginning and one double quote at the end
   *
   * @param str The string to be trimmed
   * @return the String with removed first and last double quote (when present)
   */
  @NonCPS
  static String trimDoubleQuote(String str) {
    // TODO: implement functionality by using only one regular expression
    // remove beginning double quote
    def matcher = str =~ '^"?(.*)'
    str = matcher ? matcher[0][1] : str
    // remove ending double quote
    matcher = str =~ '^(.*)"$'
    str = matcher ? matcher[0][1] : str
    return str
  }

  /**
   * Removes one single quote at beginning and one single quote at the end
   *
   * @param str The string to be trimmed
   * @return the String with removed first and last double quote (when present)
   */
  @NonCPS
  static String trimSingleQuote(String str) {
    // TODO: implement functionality by using only one regular expression
    def matcher = str =~ "^'?(.*)"
    str = matcher ? matcher[0][1] : str
    matcher = str =~ "^(.*)'\$"
    str = matcher ? matcher[0][1] : str
    return str
  }

  /**
   * Escapes characters for shell usage
   *
   * @param str The string to be escaped for use in shell
   * @return str The escaped String
   */
  @NonCPS
  static String escapeShellCharacters(String str) {
    for (String escapeCharacter : escapeCharacters) {
      str = str.replace(escapeCharacter, "\\$escapeCharacter")
    }
    return str
  }
}
