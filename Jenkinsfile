@Library('pipeline-library') pipelineLibrary
@Library('pv-pipeline-library') pvPipelineLibrary


import io.wcm.devops.jenkins.pipeline.ssh.SSHTarget

import static de.provision.devops.jenkins.pipeline.utils.ConfigConstants.*
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

// See:
// https://github.com/pro-vision/jenkins-pv-pipeline-library
// https://github.com/pro-vision/jenkins-pv-pipeline-library/blob/master/docs/config-structure.md
// Also have a look at https://github.com/wcm-io-devops/jenkins-pipeline-library for further configuration options

List triggers = defaults.getTriggers()
triggers.push(githubPush())

Map config = [
  (BUILD_WRAPPER): [
    (BUILD_WRAPPER_SSH_TARGETS): [new SSHTarget("ssh-wcm.io")]
  ],
  (PROPERTIES) : [
    (PROPERTIES_PIPELINE_TRIGGERS): triggers
  ],
  (STAGE_COMPILE): [
    (MAVEN): [
      (MAVEN_GOALS): ["clean", "deploy"],
    ]
  ]
]

routeDefaultJenkinsFile(config)
