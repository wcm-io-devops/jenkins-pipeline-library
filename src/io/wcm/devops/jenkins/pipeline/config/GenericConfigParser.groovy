/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2020 wcm.io DevOps
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
package io.wcm.devops.jenkins.pipeline.config

import com.cloudbees.groovy.cps.NonCPS
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.wcm.devops.jenkins.pipeline.credentials.Credential
import io.wcm.devops.jenkins.pipeline.utils.TypeUtils
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import net.sf.json.JSON
import net.sf.json.JSONObject

class GenericConfigParser implements Serializable {
  private static final long serialVersionUID = 1L

  Logger log = new Logger(this)
  TypeUtils typeUtils = new TypeUtils()

  /**
   * Parses a yaml object containing a list of generic config objects a list of GenericConfig
   * Only valid GenericConfig objects are added to the returned list
   *
   * @param configContent The yaml content loaded via YamlLibraryResource
   * @return The parsed list of valid GenericConfig objects
   */
  @NonCPS
  @SuppressFBWarnings('SE_NO_SERIALVERSIONID')
  List<GenericConfig> parse(Object yamlContent) {
    List<GenericConfig> parsedItems = []
    // Walk through entries, try to parse them as GenericConfig object and add it to the returned list
    yamlContent.each { Object entry ->
      String id = entry.id ?: null
      String pattern = entry.pattern ?: null
      List patterns = entry.patterns ?: null
      Object config = entry.config ?: null
      log.info("test")
      if (typeUtils.isList(patterns)) {
        for (String patternItem in patterns) {
          parsedItems.push(new GenericConfig(patternItem, id, config))
        }
      } else if (pattern != null) {
        parsedItems.push(new GenericConfig(pattern, id, config))
      }
    }

    return parsedItems
  }
}
