package io.wcm.devops.jenkins.pipeline.model.jenkins.api

import static org.junit.Assert.*
import org.junit.Test

class JobTest {

  Job underTest

  @Test
  void shouldParseEmptyData() {
    underTest = new Job([:])
    assertEquals(null, underTest.getName())
    assertEquals(null, underTest.getUrl())
    assertEquals(null, underTest.get_class())
    assertEquals([], underTest.getJobs())
    assertEquals([:], underTest.getData())
  }

  @Test
  void shouldParseSimpleData() {
    Map expectedData = [
      _class:"expectedClass",
      name:"expectedName",
      url:"expectedUrl"
    ]
    underTest = new Job(expectedData)
    assertEquals("expectedName", underTest.getName())
    assertEquals("expectedUrl", underTest.getUrl())
    assertEquals("expectedClass", underTest.get_class())
    assertEquals([], underTest.getJobs())
    assertEquals(expectedData, underTest.getData())
  }

  @Test
  void shouldParseComplexData() {
    Map expectedData = [
      _class:"0_expectedClass",
      name:"0_expectedName",
      url:"0_expectedUrl",
      jobs: [
        [
          _class:"1.0_expectedClass",
          name:"1.0_expectedName",
          url:"1.0_expectedUrl",
          jobs: [
            [
              _class:"2.0_expectedClass",
              name:"2.0_expectedName",
              url:"2.0_expectedUrl",
            ],
            [
              _class:"2.1_expectedClass",
              name:"2.1_expectedName",
              url:"2.1_expectedUrl",
            ]
          ]
        ],
        [
          _class:"1.1_expectedClass",
          name:"1.1_expectedName",
          url:"1.1_expectedUrl",
        ]
      ]
    ]
    underTest = new Job(expectedData)
    List actualFlattenedJobs = underTest.flatten()
    assertEquals(5, actualFlattenedJobs.size())
    assertEquals(expectedData, underTest.getData())

    assertEquals("0_expectedName", actualFlattenedJobs[0].getName())
    assertEquals("0_expectedUrl", actualFlattenedJobs[0].getUrl())
    assertEquals("0_expectedClass", actualFlattenedJobs[0].get_class())

    assertEquals("1.0_expectedName", actualFlattenedJobs[1].getName())
    assertEquals("1.0_expectedUrl", actualFlattenedJobs[1].getUrl())
    assertEquals("1.0_expectedClass", actualFlattenedJobs[1].get_class())

    assertEquals("2.0_expectedName", actualFlattenedJobs[2].getName())
    assertEquals("2.0_expectedUrl", actualFlattenedJobs[2].getUrl())
    assertEquals("2.0_expectedClass", actualFlattenedJobs[2].get_class())

    assertEquals("2.1_expectedName", actualFlattenedJobs[3].getName())
    assertEquals("2.1_expectedUrl", actualFlattenedJobs[3].getUrl())
    assertEquals("2.1_expectedClass", actualFlattenedJobs[3].get_class())

    assertEquals("1.1_expectedName", actualFlattenedJobs[4].getName())
    assertEquals("1.1_expectedUrl", actualFlattenedJobs[4].getUrl())
    assertEquals("1.1_expectedClass", actualFlattenedJobs[4].get_class())
  }

}
