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

/**
 * Constants for credentials
 */
class CredentialConstants implements Serializable {

  private static final long serialVersionUID = 1L

  final static SCM_CREDENTIALS_PATH = "credentials/scm/credentials.json"

  final static SSH_CREDENTIALS_PATH = "credentials/ssh/credentials.json"

  final static HTTP_CREDENTIALS_PATH = "credentials/http/credentials.json"

}
