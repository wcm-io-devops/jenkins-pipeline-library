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
package io.wcm.devops.jenkins.pipeline.managedfiles

import com.cloudbees.groovy.cps.NonCPS
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import net.sf.json.JSON
import net.sf.json.JSONObject

// @formatter:off
/**
 * Parses an incoming json object into ManagedFile objects
 *
 * Expected json file format:
 * [
 *      {
 *          "pattern": "subdomain\.domain\.tld[:/]group1",
 *          "id": "Id of the file in the jenkins instance",
 *          "name": "The name of the managed file",
 *          "comment": "The comment of the managed file"
 *      },
 *      { .. }
 * ]
 *
 * @see ManagedFile
 */
// @formatter:on
class ManagedFileParser implements Serializable {

  private static final long serialVersionUID = 1L

  Logger log = new Logger(this)

  /**
   * Parses a json object containing a list of ManagedFile objects into a list of ManagedFile
   * Only valid ManagedFile objects are added to the returned list
   *
   * @param jsonContent The json content loaded via JsonLibraryResource
   * @return The parsed list of valid ManagedFile objects
   */
  @NonCPS
  @SuppressFBWarnings('SE_NO_SERIALVERSIONID')
  List<ManagedFile> parse(JSON jsonContent) {
    ManagedFile managedFile = null
    List<ManagedFile> parsedFiles = []

    jsonContent.each { JSONObject entry ->
      String name = entry.name ?: null
      String comment = entry.comment ?: null
      String id = entry.id ?: null
      String pattern = entry.pattern ?: null
      managedFile = new ManagedFile(pattern, id, name, comment)
      log.trace("parsed managed file: ", managedFile)
      if (managedFile.isValid()) {
        parsedFiles.push(managedFile)
      } else {
        log.debug("managed file is invalid because id and/or pattern is missing")
      }
      log.trace("entry: ", entry)
    }

    return parsedFiles
  }

}
