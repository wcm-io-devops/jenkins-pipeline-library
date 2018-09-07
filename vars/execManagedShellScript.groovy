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

import io.wcm.devops.jenkins.pipeline.shell.CommandBuilderImpl
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger

/**
 * Adapter when called with list of arguments
 *
 * @param fileId The id of the managed files
 * @param args List of string arguments for the manages script
 * @return the output of the executed shell script
 */
String call(String fileId, List<String> args) {
    return this.call(fileId, args.join(" "))
}

/**
 * Executes a managed script identified by fileId with the given argLine.
 * Since managed shell scripts are not executable by default when provided by the configFileProvider
 * this step takes also care about the specific chmod command.
 *
 * @param fileId The id of the managed script
 * @param argLine The argument line for the managed script to be executed
 * @return the output of the executed shell script
 * @deprecated
 */
String call(String fileId, String argLine) {
    Logger log = new Logger(this)
    log.deprecated("execManagedShellScript","managedScripts.execJenkinsShellScript")

    return managedScripts.execJenkinsShellScript(fileId, new CommandBuilderImpl(this.steps).addArgument(argLine), true)
}
