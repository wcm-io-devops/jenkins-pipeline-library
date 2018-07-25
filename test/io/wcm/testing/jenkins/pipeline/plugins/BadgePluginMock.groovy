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

import static io.wcm.testing.jenkins.pipeline.StepConstants.*

/**
 * Mocks the badge plugin
 */
class BadgePluginMock {

  LibraryIntegrationTestContext context

  BadgePluginMock(LibraryIntegrationTestContext context) {
    this.context = context

    context.getPipelineTestHelper().registerAllowedMethod(ADD_BADGE, [String.class, String.class], addBadgeCallBack )
    context.getPipelineTestHelper().registerAllowedMethod(ADD_BADGE, [String.class, String.class, String.class], addBadgeCallBack )
    context.getPipelineTestHelper().registerAllowedMethod(ADD_BADGE, [String.class, String.class, String.class], addBadgeCallBack)
    context.getPipelineTestHelper().registerAllowedMethod(ADD_BADGE, [String.class, String.class, String.class, String.class], addBadgeCallBack)

    context.getPipelineTestHelper().registerAllowedMethod(ADD_ERROR_BADGE, [String.class], addErrorBadgeCallback)
    context.getPipelineTestHelper().registerAllowedMethod(ADD_ERROR_BADGE, [String.class,String.class], addErrorBadgeCallback)
    context.getPipelineTestHelper().registerAllowedMethod(ADD_ERROR_BADGE, [String.class,String.class,String.class], addErrorBadgeCallback)

    context.getPipelineTestHelper().registerAllowedMethod(ADD_HTML_BADGE, [String.class], addHtmlBadgeCallback)
    context.getPipelineTestHelper().registerAllowedMethod(ADD_HTML_BADGE, [String.class,String.class], addHtmlBadgeCallback)

    context.getPipelineTestHelper().registerAllowedMethod(ADD_INFO_BADGE, [String.class], addInfoBadgeCallback)
    context.getPipelineTestHelper().registerAllowedMethod(ADD_INFO_BADGE, [String.class,String.class], addInfoBadgeCallback)
    context.getPipelineTestHelper().registerAllowedMethod(ADD_INFO_BADGE, [String.class,String.class,String.class], addInfoBadgeCallback)

    context.getPipelineTestHelper().registerAllowedMethod(ADD_SHORT_TEXT, [String.class], addShortTextCallback)
    context.getPipelineTestHelper().registerAllowedMethod(ADD_SHORT_TEXT, [String.class,String.class], addShortTextCallback)
    context.getPipelineTestHelper().registerAllowedMethod(ADD_SHORT_TEXT, [String.class,String.class,Integer.class], addShortTextCallback)
    context.getPipelineTestHelper().registerAllowedMethod(ADD_SHORT_TEXT, [String.class,String.class,Integer.class,String.class], addShortTextCallback)
    context.getPipelineTestHelper().registerAllowedMethod(ADD_SHORT_TEXT, [String.class,String.class,Integer.class,String.class,String.class], addShortTextCallback)
    context.getPipelineTestHelper().registerAllowedMethod(ADD_SHORT_TEXT, [String.class,String.class,Integer.class,String.class,String.class,String.class], addShortTextCallback)

    context.getPipelineTestHelper().registerAllowedMethod(ADD_WARNING_BADGE, [String.class], addWarningBadgeCallback)
    context.getPipelineTestHelper().registerAllowedMethod(ADD_WARNING_BADGE, [String.class,String.class], addWarningBadgeCallback)
    context.getPipelineTestHelper().registerAllowedMethod(ADD_WARNING_BADGE, [String.class,String.class,String.class], addWarningBadgeCallback)

    context.getPipelineTestHelper().registerAllowedMethod(REMOVE_BADGES, [], removeBadgesCallback)
    context.getPipelineTestHelper().registerAllowedMethod(REMOVE_BADGES, [String.class], removeBadgesCallback)

    context.getPipelineTestHelper().registerAllowedMethod(REMOVE_HTML_BADGES, [], removeHtmlBadgesCallback)
    context.getPipelineTestHelper().registerAllowedMethod(REMOVE_HTML_BADGES, [String.class], removeHtmlBadgesCallback)

    context.getPipelineTestHelper().registerAllowedMethod(CREATE_SUMMARY, [String.class], createSummaryCallback)
    context.getPipelineTestHelper().registerAllowedMethod(CREATE_SUMMARY, [String.class,String.class], createSummaryCallback)
    context.getPipelineTestHelper().registerAllowedMethod(CREATE_SUMMARY, [String.class,String.class,String.class], createSummaryCallback)
  }

  /**
   * Callback for addBadge step
   */
  def addBadgeCallBack = {
    ...a ->
      Map recordData = [
        icon: getArgAt(a,0),
        text: getArgAt(a,1),
        id: getArgAt(a,2),
        link: getArgAt(a,3),
      ]
      context.getStepRecorder().record(ADD_BADGE, recordData)
  }

  /**
   * Callback for addErrorBadge step
   */
  def addErrorBadgeCallback = {
    ...a ->
      Map recordData = [
        text: getArgAt(a,0),
        id: getArgAt(a,1),
        link: getArgAt(a,2),
      ]
      context.getStepRecorder().record(ADD_ERROR_BADGE, recordData)
  }

  /**
   * Callback for addHtmlBadge step
   */
  def addHtmlBadgeCallback = {
    ...a ->
      Map recordData = [
        html: getArgAt(a,0),
        id: getArgAt(a,1),
      ]
      context.getStepRecorder().record(ADD_HTML_BADGE, recordData)
  }

  /**
   * Callback for addInfoBadge step
   */
  def addInfoBadgeCallback = {
    ...a ->
      Map recordData = [
        text: getArgAt(a,0),
        id: getArgAt(a,1),
        link: getArgAt(a,2),
      ]
      context.getStepRecorder().record(ADD_INFO_BADGE, recordData)
  }

  /**
   * Callback for addInfoBadge step
   */
  def addShortTextCallback = {
    ...a ->
      Map recordData = [
        text: getArgAt(a,0),
        background: getArgAt(a,1),
        border: getArgAt(a,2),
        borderColor: getArgAt(a,3),
        color: getArgAt(a,4),
        link: getArgAt(a,5),
      ]
      context.getStepRecorder().record(ADD_SHORT_TEXT, recordData)
  }

  /**
   * Callback for addInfoBadge step
   */
  def addWarningBadgeCallback = {
    ...a ->
      Map recordData = [
        text: getArgAt(a,0),
        id: getArgAt(a,1),
        link: getArgAt(a,2),
      ]
      context.getStepRecorder().record(ADD_WARNING_BADGE, recordData)
  }

  /**
   * Callback for removeBadges Step
   */
  def removeBadgesCallback = {
    ...a ->
      Map recordData = [
        id: getArgAt(a,0),
      ]
      context.getStepRecorder().record(REMOVE_BADGES, recordData)
  }

  /**
   * Callback for removeBadges Step
   */
  def removeHtmlBadgesCallback = {
    ...a ->
      Map recordData = [
        id: getArgAt(a,0),
      ]
      context.getStepRecorder().record(REMOVE_HTML_BADGES, recordData)
  }

  /**
   * Callback for addInfoBadge step
   */
  def createSummaryCallback = {
    ...a ->
      Map recordData = [
        icon: getArgAt(a,0),
        id: getArgAt(a,1),
        text: getArgAt(a,2),
      ]
      context.getStepRecorder().record(CREATE_SUMMARY, recordData)
  }

  /**
   * Utility function to get an argument from dynamic arguments
   *
   * @param args The object containing the arguments
   * @param index The index of the argument that should be parsed
   * @param defaultValue
   * @return The found arg or defaultValue when arg is not present
   */
  protected Object getArgAt(Object args, Integer index, defaultValue = null) {
    return (args.length > index ? args.getAt(index) : null)
  }
}
