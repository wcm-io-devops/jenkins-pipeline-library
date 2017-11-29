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
package io.wcm.testing.jenkins.pipeline.global.lib

import com.lesfurets.jenkins.unit.global.lib.SourceRetriever

/**
 * Source retriever for used JenkinsPipelineUnit testing framework which allows to use the current project as library resource.
 *
 * @see <a href="https://github.com/lesfurets/JenkinsPipelineUnit">JenkinsPipelineUnit</a>
 */
class SelfSourceRetriever implements SourceRetriever {

  String sourceURL

  SelfSourceRetriever(String sourceURL) {
    this.sourceURL = sourceURL
  }

  /**
   * Returns the current workspace as part of the source to be used
   *
   * @param repository Not used since the current workspace is the repo
   * @param branch Not used since the current workspace is the repo
   * @param targetPath Not used since the current workspace is the repo
   * @return The current workspace path as the only entry in the list
   */
  @Override
  List<URL> retrieve(String repository, String branch, String targetPath) {
    File sourceDir = new File(this.sourceURL)
    if (sourceDir.exists()) {
      return [sourceDir.toURI().toURL()]
    }
    throw new IllegalStateException("Directory $sourceDir.path does not exists")
  }

  static SelfSourceRetriever localSourceRetriever(String source) {
    new SelfSourceRetriever(source)
  }
}
