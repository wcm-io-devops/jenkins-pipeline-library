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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import net.sf.json.JSON
import net.sf.json.JSONObject

// @formatter:off
/**
 * Parses an incoming json object into Credential objects
 *
 * Expected json file format:
 * [
 *      {
 *          "pattern": "subdomain\.domain\.tld[:/]group1",
 *          "id": "Id of the credential in the jenkins instance",
 *          "comment": "Comment for the credential"
 *      },
 *      { .. }
 * ]
 *
 * @see Credential
 */
// @formatter:on
class CredentialParser implements Serializable {

  private static final long serialVersionUID = 1L

  Logger log = new Logger(this)

  /**
   * Parses a json object containing a list of credential objects into a list of Credential
   * Only valid Credential objects are added to the returned list
   *
   * @param jsonContent The json content loaded via JsonLibraryResource
   * @return The parsed list of valid Credential objects
   */
  @NonCPS
  @SuppressFBWarnings('SE_NO_SERIALVERSIONID')
  List<Credential> parse(JSON jsonContent) {
    Credential credential = null
    List<Credential> parsedCredentials = []
    // Walk through entries, try to parse them as Credential object and add it to the returned list
    jsonContent.each { JSONObject entry ->
      String comment = entry.comment ?: null
      String id = entry.id ?: null
      String pattern = entry.pattern ?: null
      String username = entry.username ?: null
      credential = new Credential(pattern, id, comment, username)
      log.trace("parsed credential file: ", credential)
      if (credential.isValid()) {
        parsedCredentials.push(credential)
      } else {
        log.debug("credential is invalid because id and/or pattern is missing")
      }
      log.trace("entry: ", entry)
    }

    return parsedCredentials
  }
}
