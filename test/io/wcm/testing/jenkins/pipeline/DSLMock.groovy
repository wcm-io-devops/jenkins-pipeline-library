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

import com.lesfurets.jenkins.unit.PipelineTestHelper
import com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration
import hudson.AbortException
import hudson.FilePath
import net.sf.json.JSON
import net.sf.json.JSONSerializer
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.NotImplementedException
import org.jenkinsci.plugins.pipeline.utility.steps.conf.ReadYamlStep
import org.jenkinsci.plugins.pipeline.utility.steps.json.ReadJSONStep
import org.jenkinsci.plugins.pipeline.utility.steps.shaded.org.yaml.snakeyaml.Yaml
import org.jenkinsci.plugins.pipeline.utility.steps.shaded.org.yaml.snakeyaml.constructor.SafeConstructor
import org.jenkinsci.plugins.pipeline.utility.steps.shaded.org.yaml.snakeyaml.reader.UnicodeReader
import org.jenkinsci.plugins.workflow.cps.DSL
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import static org.apache.commons.lang.StringUtils.isBlank
import static org.apache.commons.lang.StringUtils.isNotBlank
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Mock for the Jenkins Pipeline DSL Object
 * Used during unit-testing of the pipeline library to provide the surrounding pipeline environment
 */
class DSLMock {

  /**
   * Storage for all executed 'echo' step arguments
   */
  protected List<String> logMessages

  /**
   * The mocked DSL object
   */
  protected DSL mock

  /**
   * Map for providing mocked resources
   */
  protected Map<String, String> mockedResources

  /**
   * Reference to the PipelineTestHelper from the JenkinsPipelineUnit unit
   * @see <a href="https://github.com/lesfurets/JenkinsPipelineUnit">JenkinsPipelineUnit</a>
   */
  protected PipelineTestHelper helper

  DSLMock() {
    // cpsScriptMock the DSL object
    this.mock = mock(DSL.class)

    // initialize the log messages
    logMessages = []

    // initialize the mocked resources
    mockedResources = new TreeMap<String, String>()

    // cpsScriptMock libraryResource
    when(mock.invokeMethod(eq("libraryResource"), any())).then(new Answer<String>() {
      @Override
      String answer(InvocationOnMock invocationOnMock) throws Throwable {
        Object[] args = invocationOnMock.getArguments()
        String resourcePath = args[1][0].toString()
        // search in all SourceRetrievers for the the resource with the given path
        File foundResource = locateTestResource(resourcePath)
        return foundResource.getText("UTF-8")
      }
    })

    // cpsScriptMock the 'error' step
    when(mock.invokeMethod(eq("error"), any())).then(new Answer<String>() {
      @Override
      String answer(InvocationOnMock invocationOnMock) throws Throwable {
        throw new AbortException((String) invocationOnMock.getArguments()[1][0])
      }
    })

    // cpsScriptMock readJSON method from pipeline utility steps plugin, see: https://github.com/jenkinsci/pipeline-utility-steps-plugin
    // TODO: use real implementation of plugin here
    when(mock.invokeMethod(eq("readJSON"), any())).then(new Answer<JSON>() {
      @Override
      JSON answer(InvocationOnMock invocationOnMock) throws Throwable {
        Object[] args = invocationOnMock.getArguments()
        def functionArgs = args[1][0]

        return readJSON(functionArgs.file, functionArgs.text)
      }
    })

    when(mock.invokeMethod(eq("readYaml"), any())).then(new Answer<Object>() {
      @Override
      Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        Object[] args = invocationOnMock.getArguments()
        def functionArgs = args[1][0]

        return readYaml(functionArgs.file, functionArgs.text)
      }
    })

    // cpsScriptMock the 'echo' step and store the arguments in the logMessages object
    when(mock.invokeMethod(eq("echo"), any())).then(new Answer<Void>() {
      @Override
      Void answer(InvocationOnMock invocationOnMock) throws Throwable {
        Object[] args = invocationOnMock.getArguments()
        String logStatement = args[1][0].toString()

        logMessages.push(logStatement)
        return null
      }
    })
  }

  JSON readJSON(String file = null, String text = null) {
    ReadJSONStep step = new ReadJSONStep()
    step.setFile((String) file)
    step.setText((String) text)

    if (isNotBlank(step.getFile()) && isNotBlank(step.getText())) {
      throw new IllegalArgumentException("At most one of file or text must be provided to readJSON.")
    }
    if (isBlank(step.getFile()) && isBlank(step.getText())) {
      throw new IllegalArgumentException("At least one of file or text needs to be provided to readJSON.")
    }

    JSON json = null
    if (!isBlank(step.getFile())) {
      // TODO: Implement readJSON from file
      throw new NotImplementedException("readJSON from file is currently not implemented")
    }
    if (!isBlank(step.getText())) {
      json = JSONSerializer.toJSON(step.getText().trim())
    }


    return json
  }

  Object readYaml(String file = null, String text = null) {
    String yamlText = ""
    ReadYamlStep step = new ReadYamlStep()
    step.setFile((String) file)
    step.setText((String) text)

    if (isNotBlank(step.getFile()) && isNotBlank(step.getText())) {
      throw new IllegalArgumentException("At most one of file or text must be provided to readYaml.")
    }
    if (isBlank(step.getFile()) && isBlank(step.getText())) {
      throw new IllegalArgumentException("At least one of file or text needs to be provided to readYaml.")
    }

    if (!isBlank(step.getFile())) {
      File ymlFile = locateTestResource(step.getFile())
      FilePath path = new FilePath(ymlFile)
      Reader reader = new UnicodeReader(path.read())
      yamlText = IOUtils.toString(reader)
    }
    if (!isBlank(step.getText())) {
      yamlText += System.getProperty("line.separator") + step.getText();
    }

    // Use SafeConstructor to limit objects to standard Java objects like List or Long
    Iterable<Object> yaml = new Yaml(new SafeConstructor()).loadAll(yamlText)

    List<Object> result = new LinkedList<Object>()
    for (Object data : yaml) {
      result.add(data)
    }

    // if only one YAML document, return it directly
    if (result.size() == 1) {
      return result.get(0)
    }

    return result
  }

  /**
   * Searches in all available library sources and in the current workspace for a resource with the given resourcePath
   * This function is used to locate test resources below ./test/resources or in registered Sources of the JenkinsPipelineUnit framework
   *
   * @param resourcePath The path of the resource to locate
   * @return Map of found resources with key = LibraryName, value = path to the found resource
   */
  Map<String, File> locateTestResources(String resourcePath) {
    Map<String, File> foundResources = new HashMap<>()
    // check if resource is mocked and return the mocked resource when found
    String mockedResourcePath = this.mockedResources.get(resourcePath)
    if (mockedResourcePath) {
      resourcePath = mockedResourcePath
      File mockedLibraryResource = new File("test/resources/".concat(resourcePath))
      if (mockedLibraryResource.exists()) {
        foundResources.put("test-resource", mockedLibraryResource)
        return foundResources
      }
    }

    // try to load resource from registered libraries
    if (helper) {
      helper.libraries.each {
        String libraryName, LibraryConfiguration libraryConfig ->
          List<URL> librarySources = libraryConfig.getRetriever().retrieve(libraryConfig.name, libraryConfig.defaultVersion, libraryConfig.targetPath)
          for (URL librarySource in librarySources) {
            File libraryResource = new File(librarySource.toURI()).toPath().resolve("test/resources/$resourcePath").toFile()
            if (libraryResource.exists()) {
              foundResources.put(libraryName, libraryResource)
            }
          }
      }
    }

    // lookup in local path when helper not present or no resource was found
    if (foundResources.size() == 0 && resourcePath != null) {
      File libraryResource = new File("test/resources/".concat(resourcePath))
      if (libraryResource.exists()) {
        foundResources.put("test-resource", libraryResource)
      }
    }

    return foundResources
  }

  /**
   * Utility function to locate a test resource with a given path.
   * This function emulates the AbortException when a resourcePath was found in more than one library which would
   * result in an Ambigious error during running in Jenkins environment since the Library loaded does not know which
   * of the resources is correct.
   *
   * @param resourcePath The path of the resource to locate
   * @return The found file
   * @throws AbortException Thrown when the resource was not found or is Ambigious (found more than one time)
   */
  File locateTestResource(String resourcePath) throws AbortException {
    Map<String, File> foundResources = locateTestResources(resourcePath)
    if (foundResources.size() == 1) {
      File fileResource = foundResources.entrySet().iterator().next().value
      if (fileResource.exists()) {
        return fileResource
      } else {
        // try to lookup from libraries
        System.out.println("Test resource does not exist " + resourcePath)
        throw new AbortException(String.format("No such library resource '%s' could be found.", resourcePath))
      }
    } else if (foundResources.size() > 1) {
      // check if resource is bijective
      throw new AbortException("Ambigious resouce $resourcePath. Found resource in these libraries: " + foundResources.toString())
    } else {
      System.out.println("Test resource does not exist " + resourcePath)
      throw new AbortException(String.format("No such library resource '%s' could be found.", resourcePath))
    }
  }

  /**
   * Sets the PipelineTestHelper
   * @param helper
   */
  void setHelper(PipelineTestHelper helper) {
    this.helper = helper
  }

  /**
   * Prints the recorded log messages to system out
   */
  void printLogMessages() {
    logMessages.each { msg ->
      System.out.println("MSG: " + msg)
    }
  }

  /**
   * Getter function for logMessages object
   *
   * @return the recorded logMessages
   */
  List<String> getLogMessages() {
    return logMessages
  }

  /**
   *
   * @param resourceName The path of the resource to be mocked
   * @param resourcePath The path of the mocked resource
   */
  void mockResource(String resourcePath, String mockedPath) {
    this.mockedResources.put(resourcePath, mockedPath)
  }

  /**
   * Getter function for the mocked DSL object
   *
   * @return The mocked DSL Object
   */
  DSL getMock() {
    return mock
  }
}
