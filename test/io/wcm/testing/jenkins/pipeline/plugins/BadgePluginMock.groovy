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

import com.lesfurets.jenkins.unit.PipelineTestHelper
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorder

import static io.wcm.testing.jenkins.pipeline.StepConstants.*

/**
 * Mocks the badge plugin
 */
class BadgePluginMock {

  /**
   * Reference to PipelineTestHelper
   */
  PipelineTestHelper helper

  /**
   * Utility for recording executed steps
   */
  protected StepRecorder stepRecorder

  BadgePluginMock(PipelineTestHelper helper, StepRecorder stepRecorder) {
    this.helper = helper
    this.stepRecorder = stepRecorder

    helper.registerAllowedMethod(ADD_BADGE, [String.class, String.class], addBadgeCallBack )
    helper.registerAllowedMethod(ADD_BADGE, [String.class, String.class, String.class], addBadgeCallBack )
    helper.registerAllowedMethod(ADD_BADGE, [String.class, String.class, String.class], addBadgeCallBack)
    helper.registerAllowedMethod(ADD_BADGE, [String.class, String.class, String.class, String.class], addBadgeCallBack)

    helper.registerAllowedMethod(ADD_ERROR_BADGE, [String.class], addErrorBadgeCallback)
    helper.registerAllowedMethod(ADD_ERROR_BADGE, [String.class,String.class], addErrorBadgeCallback)
    helper.registerAllowedMethod(ADD_ERROR_BADGE, [String.class,String.class,String.class], addErrorBadgeCallback)

    helper.registerAllowedMethod(ADD_HTML_BADGE, [String.class], addHtmlBadgeCallback)
    helper.registerAllowedMethod(ADD_HTML_BADGE, [String.class,String.class], addHtmlBadgeCallback)

    helper.registerAllowedMethod(ADD_INFO_BADGE, [String.class], addInfoBadgeCallback)
    helper.registerAllowedMethod(ADD_INFO_BADGE, [String.class,String.class], addInfoBadgeCallback)
    helper.registerAllowedMethod(ADD_INFO_BADGE, [String.class,String.class,String.class], addInfoBadgeCallback)

    helper.registerAllowedMethod(ADD_SHORT_TEXT, [String.class], addShortTextCallback)
    helper.registerAllowedMethod(ADD_SHORT_TEXT, [String.class,String.class], addShortTextCallback)
    helper.registerAllowedMethod(ADD_SHORT_TEXT, [String.class,String.class,Integer.class], addShortTextCallback)
    helper.registerAllowedMethod(ADD_SHORT_TEXT, [String.class,String.class,Integer.class,String.class], addShortTextCallback)
    helper.registerAllowedMethod(ADD_SHORT_TEXT, [String.class,String.class,Integer.class,String.class,String.class], addShortTextCallback)
    helper.registerAllowedMethod(ADD_SHORT_TEXT, [String.class,String.class,Integer.class,String.class,String.class,String.class], addShortTextCallback)

    helper.registerAllowedMethod(ADD_WARNING_BADGE, [String.class], addWarningBadgeCallback)
    helper.registerAllowedMethod(ADD_WARNING_BADGE, [String.class,String.class], addWarningBadgeCallback)
    helper.registerAllowedMethod(ADD_WARNING_BADGE, [String.class,String.class,String.class], addWarningBadgeCallback)

    helper.registerAllowedMethod(REMOVE_BADGES, [], removeBadgesCallback)
    helper.registerAllowedMethod(REMOVE_BADGES, [String.class], removeBadgesCallback)

    helper.registerAllowedMethod(REMOVE_HTML_BADGES, [], removeHtmlBadgesCallback)
    helper.registerAllowedMethod(REMOVE_HTML_BADGES, [String.class], removeHtmlBadgesCallback)

    helper.registerAllowedMethod(CREATE_SUMMARY, [String.class], createSummaryCallback)
    helper.registerAllowedMethod(CREATE_SUMMARY, [String.class,String.class], createSummaryCallback)
    helper.registerAllowedMethod(CREATE_SUMMARY, [String.class,String.class,String.class], createSummaryCallback)
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
      stepRecorder.record(ADD_BADGE, recordData)
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
      stepRecorder.record(ADD_ERROR_BADGE, recordData)
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
      stepRecorder.record(ADD_HTML_BADGE, recordData)
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
      stepRecorder.record(ADD_INFO_BADGE, recordData)
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
      stepRecorder.record(ADD_SHORT_TEXT, recordData)
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
      stepRecorder.record(ADD_WARNING_BADGE, recordData)
  }

  /**
   * Callback for removeBadges Step
   */
  def removeBadgesCallback = {
    ...a ->
      Map recordData = [
        id: getArgAt(a,0),
      ]
      stepRecorder.record(REMOVE_BADGES, recordData)
  }

  /**
   * Callback for removeBadges Step
   */
  def removeHtmlBadgesCallback = {
    ...a ->
      Map recordData = [
        id: getArgAt(a,0),
      ]
      stepRecorder.record(REMOVE_HTML_BADGES, recordData)
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
      stepRecorder.record(CREATE_SUMMARY, recordData)
  }
}
