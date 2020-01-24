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


import groovy.json.JsonOutput
import io.wcm.devops.jenkins.pipeline.shell.CommandBuilder
import io.wcm.devops.jenkins.pipeline.shell.CommandBuilderImpl
import io.wcm.devops.jenkins.pipeline.tools.ansible.Role
import io.wcm.devops.jenkins.pipeline.tools.ansible.RoleRequirements
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import io.wcm.devops.jenkins.pipeline.utils.maps.MapUtils

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

/**
 * Adapter function for using config map
 *
 * @param config The pipeline configuration object
 */
void checkoutRoles(Map config) {
    Map ansibleCfg = config[ANSIBLE] ?: null
    checkoutRoles(ansibleCfg[ANSIBLE_GALAXY_ROLE_FILE] ?: null)
}

/**
 * Legacy Adapter function
 * @param requirementsYmlPath
 *
 * @deprecated
 */
void checkoutRequirements(String requirementsYmlPath) {
    Logger log = new Logger("ansible:checkoutRequirements -> ")
    log.deprecated("ansible.checkoutRequirements", "ansible.checkoutRoles")
    checkoutRoles(requirementsYmlPath)
}

/**
 * Checks out ansible galaxy requirements based upon a provided
 * path to a requirements YAML file.
 *
 * The scm uris for ansible galaxy roles will be looked up by using
 * getGalaxyRoleInfo
 *
 */
void checkoutRoles(String galaxyRoleFile) {
    Logger log = new Logger("ansible:checkoutRoles -> ")
    log.debug("loading yml '$galaxyRoleFile'")
    List ymlContent = readYaml(file: galaxyRoleFile)
    log.debug("create requirements object")
    RoleRequirements roleRequirements = new RoleRequirements(ymlContent)

    // try to find github urls for ansible galaxy roles
    List<Role> roles = roleRequirements.getRoles()
    for (role in roles) {
        if (role.isGalaxyRole()) {
            Object roleApiInfo = getGalaxyRoleInfo(role)
            if (roleApiInfo) {
                log.debug("building scm url")
                String githubUser = roleApiInfo['github_user']
                String githubRepo = roleApiInfo['github_repo']

                String scmUrl = "https://github.com/${githubUser}/${githubRepo}.git"

                // set values into role for checkout
                role.setScm(Role.SCM_GIT)
                role.setSrc(scmUrl)
            }
        }
    }

    List checkoutScmConfigs = roleRequirements.getCheckoutConfigs()
    log.debug("checkoutConfigs: " + checkoutScmConfigs)
    for (Map checkoutConfig in checkoutScmConfigs) {
        checkoutScm(checkoutConfig)
    }
}

/**
 * Executes a ansible playbook with the given configuration.
 * Please refer to the documentation for details about the configuration options
 * 
 * @param config The configuration used to execute the playbook
 */
void execPlaybook(Map config) {
    Logger log = new Logger("ansible:execPlaybook -> ")

    Map ansibleCfg = config[ANSIBLE] ?: null

    if (ansibleCfg == null) {
        log.fatal("provided ansible configuration is null, make sure to configure properly.")
        error("provided ansible configuration is null, make sure to configure properly.")
    }

    Boolean colorized = ansibleCfg[ANSIBLE_COLORIZED] != null ? ansibleCfg[ANSIBLE_COLORIZED] : true

    String installation = ansibleCfg[ANSIBLE_INSTALLATION] ?: null
    Integer forks = ansibleCfg[ANSIBLE_FORKS] ?: 5
    String limit = ansibleCfg[ANSIBLE_LIMIT] ?: null
    String playbook = ansibleCfg[ANSIBLE_PLAYBOOK] ?: null
    String credentialsId = ansibleCfg[ANSIBLE_CREDENTIALS_ID] ?: null
    String inventory = ansibleCfg[ANSIBLE_INVENTORY] ?: null
    String skippedTags = ansibleCfg[ANSIBLE_SKIPPED_TAGS] ?: null
    String startAtTask = ansibleCfg[ANSIBLE_START_AT_TASK] ?: null
    Boolean sudo = ansibleCfg[ANSIBLE_SUDO] != null ? ansibleCfg[ANSIBLE_SUDO] : false
    String sudoUser = ansibleCfg[ANSIBLE_SUDO_USER] ?: null
    String tags = ansibleCfg[ANSIBLE_TAGS] ?: null

    List extraParameters = (List) ansibleCfg[ANSIBLE_EXTRA_PARAMETERS] ?: []
    Map extraVars = (Map) ansibleCfg[ANSIBLE_EXTRA_VARS] ?: [:]
    Boolean injectParams = ansibleCfg[ANSIBLE_INJECT_PARAMS] != null ? ansibleCfg[ANSIBLE_INJECT_PARAMS] : false

    if (playbook == null) {
        log.warn("no ansible playbook defined, skipping")
        return
    }

    // create copies
    Map internalExtraVars = MapUtils.merge(extraVars)
    List internalExtraParameters = []
    for (extraParameter in extraParameters) {
        internalExtraParameters.push(extraParameter)
    }

    log.trace("debug: extraParameters.size: ${extraParameters.size()}")
    log.trace("debug: extraVars.size: ${extraVars.size()}")

    if (injectParams == true) {
        log.info("injecting build parameters as extra vars into playbook")
        params.each { String k, Object v ->
            log.debug("adding key '$k' with value '$v'")
            internalExtraVars[k] = v
        }
    }
    String extraVarsJson = JsonOutput.toJson(internalExtraVars)
    // add extra vars to extraparameters
    internalExtraParameters.push("--extra-vars '${extraVarsJson}'")

    // build extras string
    String extras = internalExtraParameters.join(' ')

    log.trace("Calling ansiblePlaybook with:")
    log.trace("colorized: $colorized")
    log.trace("extras: $extras")
    log.trace("forks: $forks")
    log.trace("installation: $installation")
    log.trace("inventory: $inventory")
    log.trace("limit: $limit")
    log.trace("playbook: $playbook")
    log.trace("skippedTags: $skippedTags")
    log.trace("startAtTask: $startAtTask")
    log.trace("sudo: $sudo")
    log.trace("sudoUser: $sudoUser")
    log.trace("tags: $tags")
    log.trace("credentialsId: $credentialsId")

    _ansibleWrapper {
        ansiblePlaybook(
          colorized: colorized,
          extras: extras,
          forks: forks,
          installation: installation,
          inventory: inventory,
          limit: limit,
          playbook: playbook,
          skippedTags: skippedTags,
          startAtTask: startAtTask,
          sudo: sudo,
          sudoUser: sudoUser,
          tags: tags,
          credentialsId: credentialsId,
        )
    }
}

/**
 * Provides the configured ansible tool for the closure/body
 *
 * @param config The configuration for the ansible tool
 * @param body The closure you want to execute
 */
void withInstallation(Map config, Closure body) {
    Logger log = new Logger("withInstallation")
    Map ansibleCfg = config[ANSIBLE] ?: null

    String ansibleInstallation = ansibleCfg[ANSIBLE_INSTALLATION] ?: null

    def ansibleToolPath = tool(name: ansibleInstallation, type: 'org.jenkinsci.plugins.ansible.AnsibleInstallation')

    withEnv(["PATH=${ansibleToolPath}:${env.PATH}"]) {
        body()
    }
}

/**
 * Installs ansible requirements
 *
 * @param config The configuration used to install the roles
 */
void installRoles(Map config) {
    Logger log = new Logger("installRoles")
    Map ansibleCfg = config[ANSIBLE] ?: null

    String requirementsPath = ansibleCfg[ANSIBLE_GALAXY_ROLE_FILE] ?: null
    Boolean requirementsForce = ansibleCfg[ANSIBLE_GALAXY_FORCE] != null ? ansibleCfg[ANSIBLE_GALAXY_FORCE] : false

    this.withInstallation(config) {
        CommandBuilder commandBuilder = new CommandBuilderImpl(this.steps, "ansible-galaxy")
        commandBuilder.addArgument("install")
        commandBuilder.addPathArgument("-r", requirementsPath)
        if (requirementsForce) {
            commandBuilder.addArgument("--force")
        }
        log.debug("command", commandBuilder.build())
        retry(3) {
            sh(commandBuilder.build())
        }
    }

}

void _ansibleWrapper(Closure body) {
    withEnv(['PYTHONUNBUFFERED=1']) {
        body()
    }
}

/**
 * Calls the ansible galaxy API for role information
 *
 * @param role The role for which the ansible galaxy API info should be retrieved
 * @return The API result or null when any error occurred
 */
Object getGalaxyRoleInfo(Role role) {
    Logger log = new Logger("ansible:getGalaxyRoleInfo -> ")
    if (!role.isGalaxyRole()) {
        log.debug("Role with name: " + role.getName() + " is not a galaxy role")
        return null
    }
    log.info("Getting role info for ${role.getName()} (namespace: '${role.getNamespace()}', role name: '${role.getRoleName()}')")

    String roleApiUrl = "https://galaxy.ansible.com/api/v1/roles/?owner__username=${role.getNamespace()}&name=${role.getRoleName()}"

    def response = httpRequest(acceptType: 'APPLICATION_JSON', timeout: 30, url: roleApiUrl, consoleLogResponseBody: false, validResponseCodes: '200', quiet: true)
    Map apiResultJson = (Map) readJSON(text: response.getContent())

    Integer size = apiResultJson.results.size()
    // we expect only one result here because username and role should only give one result
    if (size != 1) {
        log.warn("Expected one role result for ${role.getName()}, but found: $size")
        return null
    }

    return apiResultJson.results[0]
}
