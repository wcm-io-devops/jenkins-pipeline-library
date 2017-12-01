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
package io.wcm.devops.jenkins.pipeline.utils.resources

import com.cloudbees.groovy.cps.NonCPS
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import org.jenkinsci.plugins.workflow.cps.DSL

/**
 * Utility function for loading library resources
 */
class LibraryResource implements Serializable {

  private static final long serialVersionUID = 1L

  String file = null
  String content = null
  DSL dsl

  Logger log = new Logger(this)

  /**
   * @param dsl The DSL object of the current pipeline script (available via this.steps in pipeline scripts)
   * @param file path to the file
   */
  LibraryResource(DSL dsl, String file) {
    this.file = file
    this.dsl = dsl
  }

  /**
   * Loads the file and returns the content as String
   *
   * @return The content of the loaded library resource
   */
  @NonCPS
  String load() {
    log.trace("loading $file", this)
    if (content != null) {
      return content
    }
    try {
      content = this.dsl.libraryResource(file)
      log.trace("content of $file: ${content}")
      return content
    } catch (Exception ex) {
      log.fatal("Error loading $file from project pipeline library, error ${ex}")
      throw ex
    }
  }
}
