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
 * Utility function for loading JSON library files
 *
 * @see LibraryResource
 */
class YamlLibraryResource implements Serializable {

  private static final long serialVersionUID = 1L

  Logger log = new Logger(this)

  LibraryResource libraryResource

  DSL dsl

  String file

  /**
   * @param dsl The DSL object of the current pipeline script (available via this.steps in pipeline scripts)
   * @param file Path to the file
   */
  YamlLibraryResource(DSL dsl, String file) {
    this.dsl = dsl
    this.file = file
    libraryResource = new LibraryResource(dsl, file)
  }

  /**
   * Loads the resource file via LibraryResource and uses the Pipeline Utility step readYaml to parse the content into
   * a object
   *
   * @return The loaded yaml file as object
   */
  Object load() {
    def yamlString = libraryResource.load()
    try {
      Object yaml = dsl.readYaml(text: yamlString)
      log.trace("parsed yaml: ${yaml}")
      return yaml
    } catch (Exception ex) {
      log.debug("Error parsing '$file' from project pipeline library: ${ex}")
      throw ex
    }
  }
}
