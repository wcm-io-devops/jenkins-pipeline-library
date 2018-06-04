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
package io.wcm.devops.jenkins.pipeline.utils

import com.cloudbees.groovy.cps.NonCPS
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.wcm.devops.jenkins.pipeline.model.PatternMatchable
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger

import java.util.regex.Matcher

/**
 * Utility function to match incoming strings (scm urls) against a list of PatternMatchable objects.
 * Used to get necessary ManagedFile or Credential Objects for an URL (scm url)
 *
 * @see PatternMatchable
 * @see io.wcm.devops.jenkins.pipeline.credentials.Credential
 * @see io.wcm.devops.jenkins.pipeline.managedfiles.ManagedFile
 */
class PatternMatcher implements Serializable {

  Logger log = new Logger(this)

  /**
   * Returns the best match for the searchValue out of a list of PatternMatchable list.
   * As score the length of the match is used. The more characters match the better the score.
   *
   * @param searchValue The String to match against the patterns of the proviced items
   * @param items A list of PatternMatchable items in which the algorithm is searching for the best match
   * @return The match with the best score (length of match)
   */
  @NonCPS
  @SuppressFBWarnings('SE_NO_SERIALVERSIONID')
  PatternMatchable getBestMatch(String searchValue, List<PatternMatchable> items) {
    log.debug("getBestPatternMatch '$searchValue'")
    PatternMatchable result = null
    int matchScore = 0
    // Walk through list and match each pattern of the PatternMatchable against the searchvalue
    items.each {
      item ->
        log.debug("try to match file: " + item + " with pattern " + item.getPattern())
        Matcher matcher = searchValue =~ item.getPattern()
        // check if there is a match
        if (matcher) {
          String group = matcher[0]
          // check if matcher has a group and if the matched length/score is better as the last found match
          if (group && (group.length() > matchScore)) {
            matchScore = group.length()
            log.trace("match found with score $matchScore")
            result = item
          }
        }
    }
    return result
  }

}
