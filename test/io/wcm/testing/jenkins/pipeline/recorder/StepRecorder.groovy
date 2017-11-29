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
package io.wcm.testing.jenkins.pipeline.recorder

import io.wcm.testing.jenkins.pipeline.StepConstants

/**
 * Helper for recording executed steps during unit and integration testing
 */
class StepRecorder {

  protected Map recordedSteps

  StepRecorder() {
    this.recordedSteps = [:]
  }

  /**
   * Records an executed step with the given name and the paramaters executed
   *
   * @param stepName The name of the step to record
   * @param value The arguments with with the step was called
   */
  void record(String stepName, Object value) {
    // create a new entry in the recordedSteps object if necessary
    List entryList = (List) recordedSteps[stepName] ?: []
    entryList.add(value)
    // add the step to the records
    recordedSteps.put(stepName, entryList)
  }

  /**
   * Returns the recorded items for the given stepName.
   * Since there is the possibility to use a general build step like "step([$class: 'AnalysisPublisher', ...)"
   * this function also looks into the general steps recorded and returns these calls also based on the "$class" property
   *
   * @param stepName The name of the step to search for
   * @return List of executed calls containing the arguments with which the step was called
   */
  List getRecordedSteps(String stepName) {
    List steps = (List) recordedSteps[stepName] ?: []

    // walk through generic steps to find generic steps like Jacoco and Analysis Publisher
    List genericSteps = (List) recordedSteps[StepConstants.STEP] ?: []
    genericSteps.each { step ->
      if (step instanceof Map) {
        String className = step['$class'] ?: ""
        if (className == stepName) {
          steps.add(step)
        }
      }
    }
    return steps
  }

  Map getRecordedSteps() {
    return recordedSteps
  }
}
