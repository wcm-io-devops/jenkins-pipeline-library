/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2022 wcm.io DevOps
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

import static io.wcm.testing.jenkins.pipeline.StepConstants.CHECK_STYLE
import static io.wcm.testing.jenkins.pipeline.StepConstants.FIND_BUGS
import static io.wcm.testing.jenkins.pipeline.StepConstants.JUNIT_PARSER
import static io.wcm.testing.jenkins.pipeline.StepConstants.PMD_PARSER
import static io.wcm.testing.jenkins.pipeline.StepConstants.RECORD_ISSUES
import static io.wcm.testing.jenkins.pipeline.StepConstants.TASK_SCANNER

class WarningsNGPluginMock {

  WarningsNGPluginMock(LibraryIntegrationTestContext context) {
    context.getPipelineTestHelper().registerAllowedMethod(RECORD_ISSUES, [LinkedHashMap.class], { LinkedHashMap map -> context.getStepRecorder().record(RECORD_ISSUES, map) })

    context.getPipelineTestHelper().registerAllowedMethod(FIND_BUGS, [LinkedHashMap.class], {
      LinkedHashMap map ->
        context.getStepRecorder().record(FIND_BUGS, map)
        return FIND_BUGS
    })
    context.getPipelineTestHelper().registerAllowedMethod(PMD_PARSER, [LinkedHashMap.class], {
      LinkedHashMap map ->
        context.getStepRecorder().record(PMD_PARSER, map)
        return PMD_PARSER
    })
    context.getPipelineTestHelper().registerAllowedMethod(TASK_SCANNER, [LinkedHashMap.class], {
      LinkedHashMap map ->
        context.getStepRecorder().record(TASK_SCANNER, map)
        return TASK_SCANNER
    })
    context.getPipelineTestHelper().registerAllowedMethod(CHECK_STYLE, [LinkedHashMap.class], {
      LinkedHashMap map ->
        context.getStepRecorder().record(CHECK_STYLE, map)
        return CHECK_STYLE
    })
    context.getPipelineTestHelper().registerAllowedMethod(JUNIT_PARSER, [LinkedHashMap.class], {
      LinkedHashMap map ->
        context.getStepRecorder().record(JUNIT_PARSER, map)
        return JUNIT_PARSER
    })
  }

}
