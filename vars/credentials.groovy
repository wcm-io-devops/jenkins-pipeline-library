/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2018 wcm.io DevOps
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

import io.wcm.devops.jenkins.pipeline.credentials.Credential
import io.wcm.devops.jenkins.pipeline.credentials.CredentialConstants
import io.wcm.devops.jenkins.pipeline.credentials.CredentialParser
import io.wcm.devops.jenkins.pipeline.utils.PatternMatcher
import io.wcm.devops.jenkins.pipeline.utils.resources.JsonLibraryResource
import net.sf.json.JSON
import org.jenkinsci.plugins.workflow.cps.DSL

/**
 * Tries to retrieve credentials for the given scmUrl by using configurations provided in
 * resources/credentials/scm/credentials.json
 *
 * @param scmUrl The url of the repository
 * @return The found Credential object or null when no credential object was found during auto lookup
 * @see io.wcm.devops.jenkins.pipeline.credentials.Credential
 * @see io.wcm.devops.jenkins.pipeline.credentials.CredentialParser
 * @see io.wcm.devops.jenkins.pipeline.utils.resources.JsonLibraryResource
 * @see io.wcm.devops.jenkins.pipeline.credentials.CredentialConstants
 */
Credential lookupScmCredential(String scmUrl) {
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

/**
 * Tries to retrieve credentials for the given host by using configurations provided in
 * resources/credentials/ssh/credentials.json
 *
 * @param host The host to connect to
 * @return The found Credential object or null when no credential object was found during auto lookup
 * @see io.wcm.devops.jenkins.pipeline.credentials.Credential
 * @see io.wcm.devops.jenkins.pipeline.credentials.CredentialParser
 * @see JsonLibraryResource
 * @see io.wcm.devops.jenkins.pipeline.credentials.CredentialConstants
 */
Credential lookupSshCredential(String host) {
  // load the json
  JsonLibraryResource jsonRes = new JsonLibraryResource((DSL) this.steps, CredentialConstants.SSH_CREDENTIALS_PATH)
  JSON credentialJson = jsonRes.load()
  // parse the credentials
  CredentialParser parser = new CredentialParser()
  List<Credential> credentials = parser.parse(credentialJson)
  // try to find matching credential and return the credential
  PatternMatcher matcher = new PatternMatcher()
  return (Credential) matcher.getBestMatch(host, credentials)
}
