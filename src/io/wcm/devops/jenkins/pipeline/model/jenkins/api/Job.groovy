package io.wcm.devops.jenkins.pipeline.model.jenkins.api

import io.wcm.devops.jenkins.pipeline.utils.logging.Logger

/**
 * Job Model for Jenkins REST API.
 */
class Job implements Serializable {

  Logger log = new Logger(this)

  private static final long serialVersionUID = 1L

  String name

  String url

  String _class

  List <Job> jobs

  Map data

  Job(Map data) {
    this.data = data
    this.url = data.url ?: null
    this.name = data.name ?: null
    this._class = data._class ?: null
    this.jobs = []
    List rawJobs = data.jobs ?: []
    for (Map rawJobData in rawJobs) {
      Job job = new Job(rawJobData)
      this.jobs.push(job)
    }
  }

  List<Job> flatten() {
    List<Job> ret = [this]
    for (Job job in jobs) {
      ret += job.flatten()
    }
    return ret
  }
}
