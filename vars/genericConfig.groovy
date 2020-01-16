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