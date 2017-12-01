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

/**
 * Helper for integration tests to capture test results
 */
class IntegrationTestHelper implements Serializable {

  private static final long serialVersionUID = 1L

  private static Map allResults = [:]

  private static String currentPackage = null

  /**
   * Adds a new package to the test results
   *
   * @param packageName The name of the package
   */
  @NonCPS
  public static void addTestPackage(String packageName) {
    currentPackage = packageName
    getCurrentPackageResults()
  }

  /**
   * Adds a test result to the current package
   *
   * @param result The test result
   */
  @NonCPS
  public static void addTestResult(Map result) {
    getCurrentPackageResults().push(result)
  }

  /**
   * Helper to initialize and return the test result list for the current package
   *
   * @return The result list for the current package
   */
  @NonCPS
  public static List getCurrentPackageResults() {
    if (allResults[currentPackage] == null) {
      allResults[currentPackage] = []
    }
    return (List) allResults[currentPackage]
  }

  /**
   * Getter for the test results
   *
   * @return The results
   */
  @NonCPS
  public static Map getResults() {
    return allResults
  }

  /**
   * Resets the test results
   */
  @NonCPS
  public static void reset() {
    allResults = [:]
    currentPackage = null
  }

}
