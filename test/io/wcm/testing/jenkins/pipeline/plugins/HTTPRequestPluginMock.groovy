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
package io.wcm.testing.jenkins.pipeline.plugins

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestContext
import jenkins.plugins.http_request.*
import org.apache.http.client.fluent.Content
import org.mockito.MockitoAnnotations

import static io.wcm.testing.jenkins.pipeline.StepConstants.HTTP_REQUEST

class HTTPRequestPluginMock {

  LibraryIntegrationTestContext context

  List<HTTPRequestPluginResponseMock> responseMocks

  HTTPRequestPluginMock(LibraryIntegrationTestContext context) {
    this.context = context
    context.getPipelineTestHelper().registerAllowedMethod(HTTP_REQUEST, [Map.class], httpRequestCallback)
    responseMocks = new ArrayList<HTTPRequestPluginResponseMock>()
  }

  void mockResponse(String content, Integer statusCode) {
    responseMocks.push(new HTTPRequestPluginResponseMock(null, content, statusCode))
  }

  void mockResponse(Map expectedParams, String content, Integer statusCode) {
    responseMocks.push(new HTTPRequestPluginResponseMock(expectedParams, content, statusCode))
  }

  def httpRequestCallback = {
    Map incomingParameters ->
      this.context.getStepRecorder().record(HTTP_REQUEST, incomingParameters)

      // search for first matching response
      for (HTTPRequestPluginResponseMock responseMock in responseMocks) {
        if (responseMock.match(incomingParameters) ) {
          return responseMock.getResponseContentSupplierMock()
        }
      }
      return null
  }
}

class HTTPRequestPluginResponseMock {

  Map expectedParams

  ResponseContentSupplierMock responseContentSupplierMock

  HTTPRequestPluginResponseMock(Map expectedParams, String content, Integer statusCode) {
    this.expectedParams = expectedParams
    this.responseContentSupplierMock = new ResponseContentSupplierMock(content, statusCode)
  }

  /**
   * Checks if all expected params match to the incoming parameters
   * @param Map incomingParams The incoming parameters from the call
   * @return True when the expectedParams matched the incomingParams
   */
  Boolean match(Map incomingParams) {
    for (expectedParam in expectedParams) {
      if (incomingParams[expectedParam.key] != expectedParam.value) {
        return false
      }
    }
    return true
  }
}

