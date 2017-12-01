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
import io.wcm.devops.jenkins.pipeline.versioning.ComparableVersion

/**
 * Utility class for detecting type of variables since instanceof is forbidden in groovy pipeline sandbox.
 * This utiltiy uses simple methods with type overloading to simply return true or false.
 */
class TypeUtils implements Serializable {

  private static final long serialVersionUID = 1L

  /**
   * Utility function to return false for all non Map objects
   *
   * @param object Any other object that is not of type Map
   * @return false
   */
  @NonCPS
  Boolean isMap(Object object) {
    return false
  }

  /**
   * Utility function to return true for all Map objects
   *
   * @param object Map object
   * @return true
   */
  @NonCPS
  Boolean isMap(Map object) {
    return true
  }

  /**
   * Utility function to return false for all non List objects
   *
   * @param object Any other object that is not of type List
   * @return false
   */
  @NonCPS
  Boolean isList(Object object) {
    return false
  }

  /**
   * Utility function to return true for all List objects
   *
   * @param object List object
   * @return true
   */
  @NonCPS
  Boolean isList(List object) {
    return true
  }

  /**
   * Utility function to return false for all non ListItem objects
   *
   * @param object Comparable Version object
   * @return true
   */
  @NonCPS
  Boolean isComparableVersion(ComparableVersion object) {
    return true
  }

  /**
   * Utility function to return false for all non ComparableVersion objects
   *
   * @param object Any other object that is not of type ComparableVersion
   * @return false
   */
  @NonCPS
  Boolean isComparableVersion(Object object) {
    return false
  }
}
