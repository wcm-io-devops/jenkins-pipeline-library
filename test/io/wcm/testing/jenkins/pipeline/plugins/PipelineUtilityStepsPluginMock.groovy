/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2018 wcm.io DevOps
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
package io.wcm.testing.jenkins.pipeline.plugins

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestContext
import org.apache.maven.model.Model
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.apache.tools.ant.DirectoryScanner
import org.jenkinsci.plugins.pipeline.utility.steps.fs.FileWrapper

import java.nio.file.Path

import static io.wcm.testing.jenkins.pipeline.StepConstants.FIND_FILES
import static io.wcm.testing.jenkins.pipeline.StepConstants.READ_JSON
import static io.wcm.testing.jenkins.pipeline.StepConstants.READ_MAVEN_POM
import static io.wcm.testing.jenkins.pipeline.StepConstants.READ_YAML

class PipelineUtilityStepsPluginMock {

  LibraryIntegrationTestContext context

  PipelineUtilityStepsPluginMock(LibraryIntegrationTestContext context) {
    this.context = context

    this.context.getPipelineTestHelper().registerAllowedMethod(FIND_FILES, [Map.class], {
      Map params ->
        this.context.getStepRecorder().record(FIND_FILES, params)
        return this.findFiles(params['glob'])
    })
    this.context.getPipelineTestHelper().registerAllowedMethod(FIND_FILES, [], {
      this.context.getStepRecorder().record(FIND_FILES, null)
      return this.findFiles()
    })

    this.context.getPipelineTestHelper().registerAllowedMethod(READ_JSON, [Map.class], readJSONCallback)
    this.context.getPipelineTestHelper().registerAllowedMethod(READ_MAVEN_POM, [Map.class], readMavenPomCallback)
    this.context.getPipelineTestHelper().registerAllowedMethod(READ_YAML, [Map.class], readYamlCallback)
  }

  /**
   * Mocks the 'readYaml' step
   *
   * @see <a href="https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Utility+Steps+Plugin">Pipeline Utility Steps Plugin</a>
   *
   * return The Maven model
   */
  def readYamlCallback = {
    Map incomingCommand ->
      String file = incomingCommand.file
      String text = incomingCommand.text
      return this.context.getDslMock().readYaml(file, text)
  }

  /**
   * Mocks the 'readJSON' step
   *
   * @see <a href="https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Utility+Steps+Plugin">Pipeline Utility Steps Plugin</a>
   *
   * return The file/text as json
   */
  def readJSONCallback = {
    Map incomingCommand ->
      String file = incomingCommand.file
      String text = incomingCommand.text
      return this.context.getDslMock().readJSON(file, text)
  }

  /**
   * Mocks the 'readMavenPom' step
   * Emulates the readMavenPom step of the Pipeline Utility Steps Plugin
   *
   * @see <a href="https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Utility+Steps+Plugin">Pipeline Utility Steps Plugin</a>
   *
   * return The Maven model
   */
  def readMavenPomCallback = {
    Map incomingCommand ->
      String path = incomingCommand.file
      File file = this.context.getDslMock().locateTestResource(path)
      InputStream inputStream = new FileInputStream(file)
      Model ret = new MavenXpp3Reader().read(inputStream)
      inputStream.close()
      return ret
  }

  /**
   * Mocks findFiles from pipeline-utility-steps plugin
   *
   * @param glob (optional) Ant style pattern of file paths that should match. If this property is set all descendants of the current working directory will be searched for a match and returned, if it is omitted only the direct descendants of the directory will be returned.
   * @return Returns a list of found files
   */
  FileWrapper[] findFiles(String glob = null) {
    if (glob == null) {
      glob = "*"
    }
    String[] includes = [glob]
    String[] excludes = ["**/target/**/*"]
    DirectoryScanner ds = new DirectoryScanner()
    File baseDir = new File("").getAbsoluteFile()
    ds.setBasedir(baseDir)
    ds.setIncludes(includes)
    ds.setExcludes(excludes)
    ds.scan()

    String[] files = ds.getIncludedFiles()
    FileWrapper[] ret = new FileWrapper[files.length]
    for (int i = 0; i < files.size(); i++) {
      Path path = baseDir.toPath().resolve(files[i])
      File file = path.toFile()
      String name = file.getName()
      ret[i] = new FileWrapper(name, file.toString(), file.isDirectory(), file.length(), file.lastModified())
    }

    return ret
  }
}
