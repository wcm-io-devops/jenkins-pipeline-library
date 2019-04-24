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

package io.wcm.devops.jenkins.pipeline.tools.ansible

import com.cloudbees.groovy.cps.NonCPS
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

/**
 * Utility class for a loaded requirements YAML file.
 * Provides parsing into Role objects and transforming them into checkout configurations.
 */
class RoleRequirements implements Serializable {

  private static final long serialVersionUID = 1L

  List<Role> _roles = []

  Logger log = new Logger(this)

  List ymlContent

  boolean _parsed = false

  /**
   * @param ymlContent The loaded YAML content from a requirements YAML file
   */
  RoleRequirements(List ymlContent) {
    this.ymlContent = ymlContent
  }

  /**
   * parses the ymlContent into Role objects
   *
   * @see Role
   */
  @NonCPS
  void parse() {
    if (_parsed == true) {
      return
    }
    for (Map requirement in this.ymlContent) {
      String src = requirement.src ?: null
      String scm = requirement.scm ?: null
      String name = requirement.name ?: null
      String version = requirement.version ?: null

      // use name when src is null
      src = src != null ? src : name

      Role role = new Role(src)
      if (scm != null) role.setScm(scm)
      if (name != null) role.setName(name)
      if (version != null) role.setVersion(version)

      if (role.isValid()) {
        _roles.push(role)
      }
    }

    this._parsed = true
  }

  /**
   * Getter function for roles
   * @return
   */
  @NonCPS
  List<Role> getRoles() {
    this.parse()
    return this._roles
  }

  /**
   * Transforms the parsed ansible roles into checkout configurations which can be used with the checkoutScm step
   * @return A list of checkout configurations for scmCheckout
   */
  @NonCPS
  List<Map> getCheckoutConfigs() {
    List ret = []
    for (Role role in this.getRoles()) {
      log.debug("getCheckoutConfigs role: " + role.getSrc())
      if (role.isScmRole()) {
        log.debug("getCheckoutConfigs role is scmRole!")
        Map scmConfig = [
          (SCM): [
            (SCM_URL)       : role.getSrc(),
            (SCM_BRANCHES)  : [[name: role.getVersion()]],
            (SCM_EXTENSIONS): [
              [$class: 'LocalBranch'],
              [$class: 'RelativeTargetDirectory', relativeTargetDir: role.getName()],
              [$class: 'ScmName', name: role.getName()]
            ]
          ]
        ]
        ret.push(scmConfig)
      }
    }
    return ret
  }
}
