/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2019 wcm.io DevOps
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
package vars.jenkinsRestApi

import io.wcm.devops.jenkins.pipeline.model.jenkins.api.Job
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Assert
import org.junit.Test

import static org.junit.Assert.assertEquals

class JenkinsRestApiIT extends LibraryIntegrationTestBase {

  @Test
  void shouldFindAllJobs() {
    File mockedResponse = this.context.getDslMock().locateTestResource("jenkinsRestApi/findJobsByNameRegex-sample001.json")
    this.httpRequestPluginMock.mockResponse(mockedResponse.getText("UTF-8"), 200)

    //this.httpRequestPluginMock.mock
    List<Job> actualJobs = loadAndExecuteScript("vars/jenkinsRestApi/jobs/shouldFindAllJobsTestJob.groovy")
    Assert.assertEquals(5, actualJobs.size())

    assertEquals(null, actualJobs[0].getName())
    assertEquals(null, actualJobs[0].getUrl())
    assertEquals("hudson.model.Hudson", actualJobs[0].get_class())

    assertEquals("job001", actualJobs[1].getName())
    assertEquals("https://jenkins.example.org/job/job001/", actualJobs[1].getUrl())
    assertEquals("hudson.model.FreeStyleProject", actualJobs[1].get_class())

    assertEquals("folder", actualJobs[2].getName())
    assertEquals("https://jenkins.example.org/job/folder/", actualJobs[2].getUrl())
    assertEquals("com.cloudbees.hudson.plugins.folder.Folder", actualJobs[2].get_class())

    assertEquals("job002", actualJobs[3].getName())
    assertEquals("https://jenkins.example.org/job/folder/job/job002/", actualJobs[3].getUrl())
    assertEquals("org.jenkinsci.plugins.workflow.job.WorkflowJob", actualJobs[3].get_class())

    assertEquals("job003", actualJobs[4].getName())
    assertEquals("https://jenkins.example.org/job/folder/job/job003/", actualJobs[4].getUrl())
    assertEquals("hudson.model.FreeStyleProject", actualJobs[4].get_class())
  }

  @Test
  void shouldFindOneJob() {
    File mockedResponse = this.context.getDslMock().locateTestResource("jenkinsRestApi/findJobsByNameRegex-sample001.json")
    this.httpRequestPluginMock.mockResponse(mockedResponse.getText("UTF-8"), 200)

    //this.httpRequestPluginMock.mock
    List<Job> actualJobs = loadAndExecuteScript("vars/jenkinsRestApi/jobs/shouldFindOneJobTestJob.groovy")
    Assert.assertEquals(1, actualJobs.size())

    assertEquals("job001", actualJobs[0].getName())
    assertEquals("https://jenkins.example.org/job/job001/", actualJobs[0].getUrl())
    assertEquals("hudson.model.FreeStyleProject", actualJobs[0].get_class())
  }

  @Test
  void shouldFindNoJob() {
    File mockedResponse = this.context.getDslMock().locateTestResource("jenkinsRestApi/findJobsByNameRegex-sample001.json")
    this.httpRequestPluginMock.mockResponse(mockedResponse.getText("UTF-8"), 200)

    //this.httpRequestPluginMock.mock
    List<Job> actualJobs = loadAndExecuteScript("vars/jenkinsRestApi/jobs/shouldFindNoJobTestJob.groovy")
    Assert.assertEquals(0, actualJobs.size())
  }
}
