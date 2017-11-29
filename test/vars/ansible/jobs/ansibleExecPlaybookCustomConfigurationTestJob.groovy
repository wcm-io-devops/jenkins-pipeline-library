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
package vars.ansible.jobs

import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*

/**
 * Runs execMaven step with path to custom maven executable
 *
 * @return The script
 * @see vars.execMaven.ExecMavenIT
 */
def execute() {

  Map config = [
      (ANSIBLE): [
          (ANSIBLE_COLORIZED)       : false,
          (ANSIBLE_EXTRA_PARAMETERS): ["-v"],
          (ANSIBLE_EXTRA_VARS)      : [
              "string" : "value",
              "boolean": true,
              "integer": 1,
              "list"   : [1, 2, 3, 4]
          ],
          (ANSIBLE_FORKS)           : 10,
          (ANSIBLE_INSTALLATION)    : "ansible-installation-variant2",
          (ANSIBLE_INVENTORY)       : "ansible-inventory-variant2",
          (ANSIBLE_LIMIT)           : "ansible-limit-variant2",
          (ANSIBLE_PLAYBOOK)        : "ansible-playbook-variant2",
          (ANSIBLE_CREDENTIALS_ID)  : "ansible-credentials-variant2",
          (ANSIBLE_SKIPPED_TAGS)    : "ansible-tags-variant2",
          (ANSIBLE_START_AT_TASK)   : "ansible-start-at-task-variant2",
          (ANSIBLE_SUDO)            : true,
          (ANSIBLE_SUDO_USER)       : "ansible-sudo-user-variant2",
          (ANSIBLE_TAGS)            : "ansible-tags-variant2",
          (ANSIBLE_INJECT_PARAMS)   : false
      ]
  ]

  ansible.execPlaybook(config)
}

return this
