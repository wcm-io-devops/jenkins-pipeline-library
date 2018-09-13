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


import io.wcm.devops.jenkins.pipeline.utils.IntegrationTestHelper
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger

/**
 * Assert utility function
 *
 * @param expected Expected object/value
 * @param actual Actual object/value
 * @param msg Additional message that will be logged
 */
void assertEquals(expected, actual, String msg = null) {
    if (expected != actual) {
        if (msg != null) {
            error("Assertion error -> expected: '$expected', got '$actual'. Message: '$msg'")
        } else {
            error("Assertion error -> expected: '$expected', got '$actual'.")
        }
    }
}

/**
 * Assert utility function
 *
 * @param expected Expected object/value
 * @param actual Actual object/value
 * @param msg Additional message that will be logged
 */
void assertNotEquals(expected, actual, String msg = null) {
    if (expected == actual) {
        if (msg != null) {
            error("Assertion error -> expected: '$expected', got '$actual'. Message: '$msg'")
        } else {
            error("Assertion error -> expected: '$expected', got '$actual'.")
        }
    }
}

/**
 * Assert utility function function
 *
 * @param actual Actual object/value
 * @param msg Additional message that will be logged
 */
void assertNull(actual, String msg = null) {
    this.assertEquals(null, actual, msg)
}

/**
 * Assert utility function function
 *
 * @param actual Actual object/value
 * @param msg Additional message that will be logged
 */
void assertTrue(actual, String msg = null) {
    this.assertEquals(true, actual, msg)
}

/**
 * Assert utility function function
 *
 * @param actual Actual object/value
 * @param msg Additional message that will be logged
 */
void assertFalse(actual, String msg = null) {
    this.assertEquals(false, actual, msg)
}

/**
 * Logs the test result
 *
 * @param results The test results as map
 */
void logTestResults(Map results) {
    Logger log = new Logger(this)
    List lines = []
    String separator = "##############################################################"

    lines.push("")
    lines.push(separator)
    lines.push("package test results")
    lines.push(separator)
    Integer maxLength = 0
    results.each {
        String k, List v ->
            v.each {
                Map item ->
                    if (item.name.length() > maxLength) {
                        maxLength = item.name.length();
                    }
            }
    }
    results.each {
        String k, List v ->
            lines.push("Results for package: '$k'")

            v.each {
                Map item ->
                    String result = "SUCCESS"
                    if (item.exception != null) {
                        result = "FAILURE"
                    }
                    lines.push(" - ${item.name.padRight(maxLength)} : ${result}")
            }
    }
    log.info(lines.join('\n'))
}

/**
 * Processes the test results and fails when an error is found
 *
 * @param results The test results as map
 */
void processFailedTests(Map results) {
    Logger log = new Logger(this)
    List lines = []
    Integer maxLength = 0
    List failureItems = []
    // check if there are any errors:
    results.each {
        String k, List v ->
            v.each {
                Map item ->
                    if (item.exception != null) {
                        maxLength = item.name.length();
                        failureItems.push(item)
                    }
            }
    }

    // return when there are no test failures
    if (failureItems.size() == 0) {
        log.info("no test failures found")
        return
    }

    String separator = "##############################################################"

    lines.push("")
    lines.push(separator)
    lines.push("package test failures")
    lines.push(separator)

    failureItems.each {
        Map item ->
            lines.push(" - ${item.name.padRight(maxLength)} exception: ${item.exception}")
    }
    String message = lines.join('\n')
    log.fatal(message)
    error(message)
}

/**
 * Utility function for running a test
 *
 * @param className The name of the Class to run the test for
 * @param testClosure Contains the test code
 */
void runTest(String className, Closure testClosure) {
    Map result = [
        "name"     : className,
        "exception": null
    ]
    try {
        testClosure()
    } catch (Exception ex) {
        result.exception = ex
    }
    IntegrationTestHelper.addTestResult(result)
}

/**
 * Wrapper function for reporting the test results for a package
 *
 * @param results The results object
 * @param packageName The name of the package used for display in Stage View
 * @param packageTestClosure The closure containing the code to be executed
 */
void runTestsOnPackage(String packageName, Closure packageTestClosure) {
    // reset package test results
    IntegrationTestHelper.addTestPackage(packageName)
    String displayName = packageName.replace("io.wcm.devops.jenkins.pipeline.", "")
    stage(displayName) {
        packageTestClosure()
    }
}
