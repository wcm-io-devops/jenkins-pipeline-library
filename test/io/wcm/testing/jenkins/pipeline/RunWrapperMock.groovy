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
package io.wcm.testing.jenkins.pipeline

import hudson.model.Run

class RunWrapperMock {

  Run run

  public String result = null

  RunWrapperMock previousBuild = null

  String getDisplayName() {
    return displayName
  }

  String getResult() {
    return result
  }

  void setResult(String result) {
    this.result = result
  }

  void setDisplayName(String displayName) {
    this.displayName = displayName
  }
  String displayName = ""

  RunWrapperMock(Run run) {
    this.run = run
    result = null
  }

  Object getPreviousBuild() {
    return previousBuild
  }

  void setPreviousBuild(RunWrapperMock runWrapper) {
    previousBuild = runWrapper
  }

  Object getRawBuild() {
    return run
  }

  void setPreviousBuildResult(String result) {
    if (this.previousBuild == null) {
      this.previousBuild = new RunWrapperMock(null)
    }
    this.previousBuild.setResult(result)
  }
}
