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
package io.wcm.devops.jenkins.pipeline.credentials

import com.cloudbees.groovy.cps.NonCPS
import io.wcm.devops.jenkins.pipeline.model.PatternMatchable

/**
 * Model for Jenkins credentials
 */
class Credential extends PatternMatchable implements Serializable {

  private static final long serialVersionUID = 1L

  String comment

  String userName

  /**
   * @param pattern The pattern which will be matched against the scm url
   * @param id The id of the credential stored in the Jenkins instance
   * @param comment Additional comment for debug purposes
   * @param user user name to use, used during sshagent steps
   */
  Credential(String pattern, String id, String comment = null, String userName = null) {
    super(pattern, id)
    this.comment = comment
    this.userName = userName
  }

  @NonCPS
  boolean isValid() {
    return (pattern != null && id != null)
  }

}
