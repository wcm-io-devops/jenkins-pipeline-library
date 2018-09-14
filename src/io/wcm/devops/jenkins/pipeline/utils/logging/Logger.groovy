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
package io.wcm.devops.jenkins.pipeline.utils.logging

import com.cloudbees.groovy.cps.NonCPS
import io.wcm.devops.jenkins.pipeline.utils.ConfigConstants
import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException
import org.jenkinsci.plugins.workflow.cps.DSL

/**
 * Logging functionality for pipeline scripts.
 */
class Logger implements Serializable {

  private static final long serialVersionUID = 1L

  /**
   * Reference to the dsl/script object
   */
  static DSL dsl

  /**
   * Reference to the CpsScript/WorkflowScript
   */
  static Script script

  /**
   * The log level
   */
  static LogLevel level = LogLevel.fromInteger(2)

  /**
   * The name of the logger
   */
  public String name = ""

  /**
   * Flag if the logger is initialized
   */
  public static Boolean initialized = false

  /**
   * @param name The name of the logger
   */
  Logger(String name = "") {
    this.name = name
  }

  /**
   * @param logScope The object the logger is for. The name of the logger is autodetected.
   */
  Logger(Object logScope) {
    if (logScope instanceof Object) {
      this.name = getClassName(logScope)
      if (this.name == null) {
        this.name = "$logScope"
      }
    }
  }

  /**
   * Initializes the logger with DSL/steps object and LogLevel
   *
   * @param dsl The DSL object of the current pipeline script (available via this.steps in pipeline scripts)
   * @param logLvl The log level to use during execution of the pipeline script
   * @deprecated
   */
  @NonCPS
  static void init(DSL dsl, LogLevel logLvl = LogLevel.INFO) {
    if (logLvl == null) logLvl = LogLevel.INFO
    level = logLvl
    if (Logger.initialized == true) {
      return
    }
    this.dsl = dsl
    initialized = true
    Logger tmpLogger = new Logger('Logger')
    tmpLogger.deprecated('Logger.init(DSL dsl, logLevel)','Logger.init(Script script, logLevel)')
  }

  /**
   * Initializes the logger with DSL/steps object and configuration map
   *
   * @param dsl The DSL object of the current pipeline script (available via this.steps in pipeline scripts)
   * @param map The configuration object of the pipeline
   * @deprecated
   */
  @NonCPS
  static void init(DSL dsl, Map map) {
    LogLevel lvl
    if (map) {
      lvl = map[ConfigConstants.LOGLEVEL] ?: LogLevel.INFO
    } else {
      lvl = LogLevel.INFO
    }
    init(dsl, lvl)
  }

  /**
   * Initializes the logger with DSL/steps object and loglevel as string
   *
   * @param dsl The DSL object of the current pipeline script (available via this.steps in pipeline scripts)
   * @param sLevel the log level as string
   * @deprecated
   */
  @NonCPS
  static void init(DSL dsl, String sLevel) {
    if (sLevel == null) sLevel = LogLevel.INFO
    init(dsl, LogLevel.fromString(sLevel))
  }

  /**
   * Initializes the logger with DSL/steps object and loglevel as integer
   *
   * @param dsl The DSL object of the current pipeline script (available via this.steps in pipeline scripts)
   * @param iLevel the log level as integer
   *
   * @deprecated
   */
  @NonCPS
  static void init(DSL dsl, Integer iLevel) {
    if (iLevel == null) iLevel = LogLevel.INFO.getLevel()
    init(dsl, LogLevel.fromInteger(iLevel))
  }

  /**
   * Initializes the logger with CpsScript object and LogLevel
   *
   * @param script CpsScript object of the current pipeline script (available via this in pipeline scripts)
   * @param map The configuration object of the pipeline
   */
  @NonCPS
  static void init(Script script, LogLevel logLvl = LogLevel.INFO) {
    if (logLvl == null) logLvl = LogLevel.INFO
    level = logLvl
    if (Logger.initialized == true) {
      return
    }
    this.script = script
    this.dsl = (DSL) script.steps
    initialized = true
  }

  /**
   * Initializes the logger with CpsScript object and configuration map
   *
   * @param script CpsScript object of the current pipeline script (available via this in pipeline scripts)
   * @param map The configuration object of the pipeline
   */
  @NonCPS
  static void init(Script script, Map map) {
    LogLevel lvl
    if (map) {
      lvl = map[ConfigConstants.LOGLEVEL] ?: LogLevel.INFO
    } else {
      lvl = LogLevel.INFO
    }
    init(script, lvl)
  }

  /**
   * Initializes the logger with CpsScript object and loglevel as string
   *
   * @param script CpsScript object of the current pipeline script (available via this in pipeline scripts)
   * @param sLevel the log level as string
   */
  @NonCPS
  static void init(Script script, String sLevel) {
    if (sLevel == null) sLevel = LogLevel.INFO
    init(script, LogLevel.fromString(sLevel))
  }

  /**
   * Initializes the logger with DSL/steps object and loglevel as integer
   *
   * @param script CpsScript object of the current pipeline script (available via this in pipeline scripts)
   * @param iLevel the log level as integer
   *
   */
  @NonCPS
  static void init(Script script, Integer iLevel) {
    if (iLevel == null) iLevel = LogLevel.INFO.getLevel()
    init(script, LogLevel.fromInteger(iLevel))
  }

  /**
   * Logs a trace message followed by object dump
   *
   * @param message The message to be logged
   * @param object The object to be dumped
   */
  @NonCPS
  void trace(String message, Object object) {
    log(LogLevel.TRACE, message, object)
  }

  /**
   * Logs a info message followed by object dump
   *
   * @param message The message to be logged
   * @param object The object to be dumped
   */
  @NonCPS
  void info(String message, Object object) {
    log(LogLevel.INFO, message, object)
  }

  /**
   * Logs a debug message followed by object dump
   *
   * @param message The message to be logged
   * @param object The object to be dumped
   */
  @NonCPS
  void debug(String message, Object object) {
    log(LogLevel.DEBUG, message, object)
  }

  /**
   * Logs warn message followed by object dump
   *
   * @param message The message to be logged
   * @param object The object to be dumped
   */
  @NonCPS
  void warn(String message, Object object) {
    log(LogLevel.WARN, message, object)
  }

  /**
   * Logs a error message followed by object dump
   *
   * @param message The message to be logged
   * @param object The object to be dumped
   */
  @NonCPS
  void error(String message, Object object) {
    log(LogLevel.ERROR, message, object)
  }

  /**
   * Logs a fatal message followed by object dump
   *
   * @param message The message to be logged
   * @param object The object to be dumped
   */
  @NonCPS
  void fatal(String message, Object object) {
    log(LogLevel.FATAL, message, object)
  }

  /**
   * Logs a trace message
   *
   * @param message The message to be logged
   */
  @NonCPS
  void trace(String message) {
    log(LogLevel.TRACE, message)
  }

  /**
   * Logs a trace message
   *
   * @param message The message to be logged
   */
  @NonCPS
  void info(String message) {
    log(LogLevel.INFO, message)
  }

  /**
   * Logs a debug message
   *
   * @param message The message to be logged
   */
  @NonCPS
  void debug(String message) {
    log(LogLevel.DEBUG, message)
  }

  /**
   * Logs a warn message
   *
   * @param message The message to be logged
   */
  @NonCPS
  void warn(String message) {
    log(LogLevel.WARN, message)
  }

  /**
   * Logs a error message
   *
   * @param message The message to be logged
   * @param object The object to be dumped
   */
  @NonCPS
  void error(String message) {
    log(LogLevel.ERROR, message)
  }

  /**
   * Logs a deprecation message
   *
   * @param message The message to be logged
   */
  @NonCPS
  void deprecated(String message) {
    try {
      Logger.dsl.addWarningBadge(message)
    } catch (Throwable ex) {
      // no badge plugin available
    }
    log(LogLevel.DEPRECATED, message)
  }

  /**
   * Logs a deprecation message with deprecated and replacement
   *
   * @param deprecatedItem The item that is depcrecated
   * @param newItem The replacement (if exist)
   */
  @NonCPS
  void deprecated(String deprecatedItem, String newItem) {
    String message = "The step/function/class '$deprecatedItem' is marked as depecreated and will be removed in future releases. " +
      "Please use '$newItem' instead."
    deprecated(message)
  }

  /**
   * Logs a fatal message
   *
   * @param message The message to be logged
   * @param object The object to be dumped
   */
  @NonCPS
  void fatal(String message) {
    log(LogLevel.FATAL, message)
  }

  /**
   * Helper function for logging/dumping a object at the given log level
   *
   * @param logLevel the loglevel to be used
   * @param message The message to be logged
   * @param object The object to be dumped
   */
  @NonCPS
  void log(LogLevel logLevel, String message, Object object) {
    if (doLog(logLevel)) {
      def objectName = getClassName(object)
      if (objectName != null) {
        objectName = "($objectName) "
      } else {
        objectName = ""
      }

      def objectString = object.toString()
      String msg = "$name : $message -> $objectName$objectString"
      writeLogMsg(logLevel, msg)
    }
  }

  /**
   * Helper function for logging at the given log level
   *
   * @param logLevel the loglevel to be used
   * @param message The message to be logged
   */
  @NonCPS
  void log(LogLevel logLevel, String message) {
    if (doLog(logLevel)) {
      String msg = "$name : $message"
      writeLogMsg(logLevel, msg)
    }
  }

  /**
   * Utility function for writing to the jenkins console
   *
   * @param logLevel the loglevel to be used
   * @param msg The message to be logged
   */
  @NonCPS
  private static void writeLogMsg(LogLevel logLevel, String msg) {
    String lvlString = "[${logLevel.toString()}]"

    lvlString = wrapColor(logLevel.getColorCode(), lvlString)

    if (dsl != null) {
      dsl.echo("$lvlString $msg")
    }
  }

  /**
   * Wraps a string with color codes when terminal is available
   * @param logLevel
   * @param str
   * @return
   */
  @NonCPS
  private static String wrapColor(String colorCode, String str) {
    String ret = str
    if (hasTermEnv()) {
      ret = "\u001B[${colorCode}m${str}\u001B[0m"
    }
    return ret
  }

  /**
   * Helper function to detect if a term environment is available
   * @return
   */
  @NonCPS
  private static Boolean hasTermEnv() {
    String termEnv = null
    if (script != null) {
      try {
        termEnv = script.env.TERM
      } catch (Exception ex) {

      }
    }
    return termEnv != null
  }

  /**
   * Utiltiy function to determine if the given logLevel is active
   *
   * @param logLevel
   * @return true , when the loglevel should be displayed, false when the loglevel is disabled
   */
  @NonCPS
  private static boolean doLog(LogLevel logLevel) {
    if (logLevel.getLevel() >= level.getLevel()) {
      return true
    }
    return false
  }

  /**
   * Helper function to get the name of the object
   * @param object
   * @return
   */
  @NonCPS
  private static String getClassName(Object object) {
    String objectName = null
    // try to retrieve as much information as possible about the class
    try {
      Class objectClass = object.getClass()
      objectName = objectClass.getName().toString()
      objectName = objectClass.getCanonicalName().toString()
    } catch (RejectedAccessException e) {
      // do nothing
    }

    return objectName
  }
}
