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
import io.wcm.tooling.jenkins.pipeline.credentials.Credential
import io.wcm.tooling.jenkins.pipeline.credentials.CredentialConstants
import io.wcm.tooling.jenkins.pipeline.credentials.CredentialParser
import io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants
import io.wcm.tooling.jenkins.pipeline.utils.PatternMatcher
import io.wcm.tooling.jenkins.pipeline.utils.logging.Logger
import io.wcm.tooling.jenkins.pipeline.utils.resources.JsonLibraryResource
import net.sf.json.JSON
import org.jenkinsci.plugins.workflow.cps.DSL

/**
 * Step which takes care about checking out source code from git. Other SCMs are currently not supported.
 *
 * Variant 1: When scm variable is present and config scm.useScmVar is set to true the configured SCM from the job
 * itself is used to checkout.
 *
 * Variant 2: When config scm.useScmVar is not set to true the value from config scm.url is used for checkout. In this
 * mode the step tries to auto lookup the Jenkins credentials from resources/credentials/scm/credentials.json based on
 * the url to checkout.
 *
 * See <a href="vars/checkoutScm.md">checkoutScm.md</a> for detailed documentation
 *
 * Defaults:
 *      branches: master and develop
 *      extensions: LocalBranch
 *
 * @param config configuration object
 */
def call(Map config) {
    Logger log = new Logger(this)

    // retrieve the configuration
    Map scmCfg = (Map) config[ConfigConstants.SCM] ?: [:]
    Boolean useScmVar = scmCfg[ConfigConstants.SCM_USE_SCM_VAR] ? scmCfg[ConfigConstants.SCM_USE_SCM_VAR] : false

    if (useScmVar && scm) {
        // checkout by using provided scm, used when Jenkinsfile is in the project repository
        log.info("Found configuration to use existing scm var, checking out with scm configuration from job")
        checkoutWithScmVar()
    } else {
        // checkout by using the provided scm configuration, used when Jenkinsfile is not in the project repository
        log.info("Checking out with provided configuration")
        checkoutWithConfiguration(scmCfg, log)
    }

    // try to retrieve the git branch and set it into environment variable GIT_BRANCH
    setGitBranch()
    // set the scm url to environment variable SCM_URL
    setScmUrl(config)
}

/**
 * Runs the checkout by using the provided scm variable
 *
 * @return result of checkout step
 */
def checkoutWithScmVar() {
    checkout scm
}

/**
 * Runs the checkout by using the provided scm configuration
 *
 * @param scmCfg The configuration used for checkout
 * @param log The logger instance
 * @return result of checkout step
 */
def checkoutWithConfiguration(Map scmCfg, Logger log) {
    // parse the configuration with defaults
    String credentialsId = scmCfg[ConfigConstants.SCM_CREDENTIALS_ID]
    String url = scmCfg[ConfigConstants.SCM_URL]
    List branches = (List) scmCfg[ConfigConstants.SCM_BRANCHES] ?: [[name: '*/master'], [name: '*/develop']]
    List submoduleCfg = (List) scmCfg[ConfigConstants.SCM_SUBMODULE_CONFIG] ?: []
    List extensions = (List) scmCfg[ConfigConstants.SCM_EXTENSIONS] ?: [[$class: 'LocalBranch']]
    Boolean doGenerateSubmoduleConfigurations = scmCfg[ConfigConstants.SCM_DO_GENERATE_SUBMODULE_CONFIGURATION] != null ? scmCfg[ConfigConstants.SCM_DO_GENERATE_SUBMODULE_CONFIGURATION] : false
    Map userRemoteConfig = (Map) scmCfg[ConfigConstants.SCM_USER_REMOTE_CONFIG] ?: [:]
    List userRemoteConfigs = (List) scmCfg[ConfigConstants.SCM_USER_REMOTE_CONFIGS] ?: []

    log.debug("url: ", url)
    log.debug("branches: ", branches)

    if (userRemoteConfigs.size() > 0) {
        // use userRemoteConfigs when provided
        log.info("userRemoteConfigs found in provided configuration, do not auto credential lookup", userRemoteConfigs)
    } else if (userRemoteConfig.size() > 0) {
        // use userRemoteConfig when provided
        userRemoteConfigs.push(userRemoteConfig)
    } else {
        // since url is necessary for this part fail when not present
        if (url == null) {
            log.fatal("No scm url provided, aborting")
            error("$this: No scm url provided, aborting")
        }

        // do credential auto lookup
        if (credentialsId == null) {
            log.debug("no credentials id passed, try auto lookup")
            Credential credential = autoLookupSCMCredentials(url)
            if (credential != null) {
                credentialsId = credential.getId()
            }
        }

        // add the url to the userRemoteConfigs
        userRemoteConfig.put("url", url)

        // only add credentials when provided/found
        if (credentialsId != null) {
            userRemoteConfig.put("credentialsId", credentialsId)
        }

        // prepare the userRemoteConfigs object
        log.info("checkoutScm from $url, with credentials: $credentialsId")
        userRemoteConfigs.add(userRemoteConfig)
    }

    // call the checkout step
    checkout(
            [
                    $class                           : 'GitSCM',
                    branches                         : branches,
                    doGenerateSubmoduleConfigurations: doGenerateSubmoduleConfigurations,
                    extensions                       : extensions,
                    submoduleCfg                     : submoduleCfg,
                    userRemoteConfigs                : userRemoteConfigs
            ]
    )
}

/**
 * Tries to retrieve credentials for the given scmUrl by using configurations provided in
 * resources/credentials/scm/credentials.json
 *
 * @param scmUrl The url of the repository
 * @return The found Credential object or null when no credential object was found during auto lookup
 * @see Credential
 * @see CredentialParser
 * @see JsonLibraryResource
 * @see CredentialConstants
 */
Credential autoLookupSCMCredentials(String scmUrl) {
    // load the json
    JsonLibraryResource jsonRes = new JsonLibraryResource((DSL) this.steps, CredentialConstants.SCM_CREDENTIALS_PATH)
    JSON credentialJson = jsonRes.load()
    // parse the credentials
    CredentialParser parser = new CredentialParser()
    List<Credential> credentials = parser.parse(credentialJson)
    // try to find matching credential and return the credential
    PatternMatcher matcher = new PatternMatcher()
    return (Credential) matcher.getBestMatch(scmUrl, credentials)
}
