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
package vars.sshAgentWrapper.jobs

import io.wcm.tooling.jenkins.pipeline.shell.ScpCommandBuilderImpl
import io.wcm.tooling.jenkins.pipeline.ssh.SSHTarget
import org.jenkinsci.plugins.workflow.cps.DSL

import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*

/**
 * Runs the transferScp step with ssh credential auto lookup (key + username)
 *
 * @return The script
 * @see vars.setScmUrl.SetScmUrlIT
 */
def execute() {

  List sshTargets = [
      new SSHTarget("host1.domain.tld"),
      new SSHTarget("host2.domain.tld"),
      new SSHTarget("host3.domain.tld")
  ]

  sshAgentWrapper(sshTargets) {
    sh "echo 'multiple ssh targets'"
  }

  return sshTargets
}


return this
