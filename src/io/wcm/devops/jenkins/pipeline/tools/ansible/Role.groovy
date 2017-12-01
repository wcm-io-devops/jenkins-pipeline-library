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

/**
 * Object for ansible roles
 */
class Role implements Serializable {

  private static final long serialVersionUID = 1L

  public static SCM_GIT = "git"

  String src = null

  String name = null

  String scm = null

  String version = "master"

  /**
   * The src, either a galaxy role name or a scm path
   *
   * @param src
   */
  Role(String src) {
    this.src = src
    this.name = src
  }

  @NonCPS
  public Boolean isValid() {
    return this.src != null
  }

  @NonCPS
  public Boolean isScmRole() {
    return this.scm == Role.SCM_GIT
  }

  @NonCPS
  public Boolean isGalaxyRole() {
    return this.scm == null
  }
}
