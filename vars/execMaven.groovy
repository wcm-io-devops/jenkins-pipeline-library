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
import hudson.AbortException
import io.wcm.devops.jenkins.pipeline.environment.EnvironmentConstants
import io.wcm.devops.jenkins.pipeline.managedfiles.ManagedFile
import io.wcm.devops.jenkins.pipeline.managedfiles.ManagedFileConstants
import io.wcm.devops.jenkins.pipeline.managedfiles.ManagedFileParser
import io.wcm.devops.jenkins.pipeline.model.PatternMatchable
import io.wcm.devops.jenkins.pipeline.shell.MavenCommandBuilderImpl
import io.wcm.devops.jenkins.pipeline.utils.ConfigConstants
import io.wcm.devops.jenkins.pipeline.utils.PatternMatcher
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import io.wcm.devops.jenkins.pipeline.utils.resources.JsonLibraryResource
import net.sf.json.JSON
import org.jenkinsci.plugins.workflow.cps.DSL

/**
 * Executes maven with the given configuration options inside the "maven" element.
 * This step implements
 *  - auto lookup for global maven settings
 *  - auto lookup for local maven settings
 *  - auto lookup for NPMRC and NPM_CONFIG_USERCONFIG
 *  - auto lookup for BUNDLE_CONFIG
 *
 * @param config Configuration options for the step
 */
void call(Map config = null) {
    config = config ?: [:]
    Logger log = new Logger(this)

    // retrieve the configuration and set defaults

    // retrieve scm url via utility step
    String scmUrl = getScmUrl(config)

    // initialize the command builder
    MavenCommandBuilderImpl commandBuilder = new MavenCommandBuilderImpl((DSL) steps, params)
    commandBuilder.applyConfig(config)

    // initialize the configuration files
    List configFiles = []

    // retrieve global settingsId
    if (commandBuilder.getGlobalSettingsId() == null) {
        // use autolookup for maven global settingsId
        ManagedFile mavenGlobalSettingsManagedFile = autoLookupMavenSettings(ManagedFileConstants.GLOBAL_MAVEN_SETTINGS_PATH, scmUrl, log)
        if (mavenGlobalSettingsManagedFile) {
            commandBuilder.setGlobalSettingsId(mavenGlobalSettingsManagedFile.getId())
        }
        log.info("mavenGlobalSettings was null, result from autolookup: ", commandBuilder.getGlobalSettingsId())
    }

    // retrieve settingsId
    if (commandBuilder.getSettingsId() == null) {
        // use autolookup for maven global settingsId
        ManagedFile mavenSettingsManagedFile = autoLookupMavenSettings(ManagedFileConstants.MAVEN_SETTINGS_PATH, scmUrl, log)
        if (mavenSettingsManagedFile) {
            commandBuilder.setSettingsId(mavenSettingsManagedFile.getId())
        }
        log.info("mavenSettings was null, result from autolookup: ", commandBuilder.getSettingsId())
    }

    // check if global maven settingsId were found during auto lookup and add them
    if (commandBuilder.getGlobalSettingsId() != null) {
        configFiles.push(configFile(fileId: commandBuilder.getGlobalSettingsId(), targetLocation: '', variable: ManagedFileConstants.GLOBAL_MAVEN__SETTINGS_ENV))
    }

    // check if local maven settingsId were found during auto lookup and add them
    if (commandBuilder.getSettingsId() != null) {
        configFiles.push(configFile(fileId: commandBuilder.getSettingsId(), targetLocation: "", variable: ManagedFileConstants.MAVEN_SETTING_ENV))
    }

    // add config file for NPM_CONFIG_USERCONFIG if defined
    addManagedFile(log, scmUrl, ManagedFileConstants.NPM_CONFIG_USERCONFIG_PATH, ManagedFileConstants.NPM_CONFIG_USERCONFIG_ENV, configFiles)
    // add config file for NPMRC if defined
    addManagedFile(log, scmUrl, ManagedFileConstants.NPMRC_PATH, ManagedFileConstants.NPMRC_ENV, configFiles)
    // add config file for ruby
    addManagedFile(log, scmUrl, ManagedFileConstants.BUNDLE_CONFIG_PATH, ManagedFileConstants.BUNDLE_CONFIG_ENV, configFiles)

    configFileProvider(configFiles) {
        // add global settingsId
        if (commandBuilder.getGlobalSettingsId() != null) {
            String path = env.getProperty(ManagedFileConstants.GLOBAL_MAVEN__SETTINGS_ENV)
            commandBuilder.setGlobalSettings(path)
        }

        // add local settingsId
        if (commandBuilder.getSettingsId() != null) {
            String path = env.getProperty(ManagedFileConstants.MAVEN_SETTING_ENV)
            commandBuilder.setSettings(path)
        }

        // build the command line
        command = commandBuilder.build()
        log.info("executing maven with: $command")

        // execute the maven command
        sh(command)
    }
}

/**
 * Searches for a matching maven setting for the scmUrl in the provided json
 *
 * @param jsonPath Path to the json conaining configurations for managed files
 * @param scmUrl The url of the used scm
 * @return A found Managed file, or null
 */
ManagedFile autoLookupMavenSettings(String jsonPath, String scmUrl, Logger log) {
    // load and parse the json
    JsonLibraryResource jsonLibraryResource = new JsonLibraryResource(steps, jsonPath)
    JSON managedFilesJson
    try {
        managedFilesJson = jsonLibraryResource.load()
    } catch (AbortException ex) {
        log.warn("Exception during loading '$jsonPath', it seems that you do not have a pipeline configuration present, skip parsing of managedfiles. " +
          "Refer to https://github.com/wcm-io-devops/jenkins-pipeline-library/blob/master/docs/tutorial-setup.md for proper setup.")
        return null
    }

    ManagedFileParser parser = new ManagedFileParser()
    List<PatternMatchable> managedFiles = parser.parse(managedFilesJson)
    // match the scmUrl against the parsed mangedFiles and get the best match
    PatternMatcher matcher = new PatternMatcher()
    return (ManagedFile) matcher.getBestMatch(scmUrl, managedFiles)
}

/**
 * Searches for a managed file in the json from jsonPath by using the scmUrl for matching and adds the file
 * to the provided configFiles object when a result was found.
 *
 * @param log Instance of the execMaven logger
 * @param scmUrl The scm url of the current job
 * @param jsonPath Path to the json containing configurations for managed files
 * @param envVar The environment variable where the configFileProvider should store the path in
 * @param configFiles List of config files where the found file has to be added
 */
void addManagedFile(Logger log, String scmUrl, String jsonPath, String envVar, List configFiles) {
    // load and parse the json
    JsonLibraryResource jsonLibraryResource = new JsonLibraryResource(steps, jsonPath)
    JSON managedFilesJson
    try {
        managedFilesJson = jsonLibraryResource.load()
    } catch (AbortException ex) {
        log.warn("Exception during loading '$jsonPath', it seems that you do not have a pipeline configuration present, skip parsing of managedfiles. " +
          "Refer to https://github.com/wcm-io-devops/jenkins-pipeline-library/blob/master/docs/tutorial-setup.md for proper setup.")
        return
    }

    ManagedFileParser parser = new ManagedFileParser()
    List<PatternMatchable> managedFiles = parser.parse(managedFilesJson)
    PatternMatcher matcher = new PatternMatcher()
    // match the scmUrl against the parsed mangedFiles and get the best match
    PatternMatchable managedFile = matcher.getBestMatch(scmUrl, managedFiles)
    // when a file was found add it to the configFiles
    if (managedFile) {
        log.info("Found managed file for env var '$envVar' with id: '${managedFile.id}', adding to provided config files")
        configFiles.push(configFile(fileId: managedFile.getId(), targetLocation: "", variable: envVar))
    }
}
