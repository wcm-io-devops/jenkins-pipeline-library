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
 * Source retriever for the used JenkinsPipelineUnit testing framework which allows to use git submodules as library
 * resource
 *
 * @see <a href="https://github.com/lesfurets/JenkinsPipelineUnit">JenkinsPipelineUnit</a>
 */
class SubmoduleSourceRetriever implements SourceRetriever {

  String sourceURL

  SubmoduleSourceRetriever(String sourceURL) {
    this.sourceURL = sourceURL
  }

  @Override
  List<URL> retrieve(String repository, String branch, String targetPath) {
    def sourceDir = new File(sourceURL).toPath().resolve("$targetPath/$branch").toFile()
    if (sourceDir.exists()) {
      return [sourceDir.toURI().toURL()]
    }
    throw new IllegalStateException("Directory $sourceDir.path does not exists")
  }

  static SubmoduleSourceRetriever submoduleSourceRetriever(String source) {
    new SubmoduleSourceRetriever(source)
  }
}
