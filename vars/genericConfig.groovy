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
import io.wcm.devops.jenkins.pipeline.config.GenericConfig
import io.wcm.devops.jenkins.pipeline.config.GenericConfigConstants
import io.wcm.devops.jenkins.pipeline.config.GenericConfigParser
import io.wcm.devops.jenkins.pipeline.utils.PatternMatcher
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import io.wcm.devops.jenkins.pipeline.utils.resources.YamlLibraryResource

Object load(path, searchValue, resultKey) {
  Logger log = new Logger("genericConfig.load")
  GenericConfigParser configParser = new GenericConfigParser()
  PatternMatcher patternMatcher = new PatternMatcher()
  Map result = [:]
  try {
    YamlLibraryResource configResource = new YamlLibraryResource(this, path)
    Object yamlContent = configResource.load()
    log.info("yamlContent", yamlContent)
    List<GenericConfig> genericConfigs = configParser.parse(yamlContent)
    log.info("genericConfigs", genericConfigs)
    GenericConfig matchedConfig = patternMatcher.getBestMatch(searchValue, genericConfigs)
    log.info("matchedConfig", matchedConfig)
    if (matchedConfig) {
      result = [
        (resultKey): matchedConfig.getConfig()
      ]
    }
  } catch (Exception ex) {
    log.warn("Unable to load mattermost config from ${path}. If you want to use mattermost notifications read the documentation. TODO")
  }
  return result
}
