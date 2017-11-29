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


package io.wcm.tooling.jenkins.pipeline.ssh

import com.cloudbees.groovy.cps.NonCPS
import io.wcm.tooling.jenkins.pipeline.credentials.Credential
import io.wcm.tooling.jenkins.pipeline.credentials.CredentialAware

/**
 * Value object for ssh targets
 */
class SSHTarget implements Serializable, CredentialAware {

  private static final long serialVersionUID = 1L

  String host

  Credential _credential = null

  /**
   * The host to connect to
   * @param host
   */
  SSHTarget(String host) {
    this.host = host
  }

  /**
   * Used to set the username based on a Credential found by auto lookup
   *
   * @param credential The credential object to use the username from (if set)
   */
  @Override
  @NonCPS
  void setCredential(Credential credential) {
    this._credential = credential
  }

  /**
   * Getter function for credentials
   *
   * @return The stored credentials
   */
  @Override
  @NonCPS
  Credential getCredential() {
    return this._credential
  }
}
