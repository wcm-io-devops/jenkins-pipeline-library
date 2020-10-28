import io.wcm.devops.jenkins.pipeline.environment.EnvironmentConstants
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.ANSI_COLOR
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.ANSI_COLOR_XTERM

/**
 * Enables color output in Jenkins console by using the ansiColor step
 * Please refer to the documentation for details about the configuration options
 *
 * @param config The configuration options
 * @param body The closure to be executed
 *
 * @deprecated
 */
void color(Map config = [:], Closure body) {
    Logger log = new Logger(this)
    String ansiColorMap = (String) config[ANSI_COLOR] ?: ANSI_COLOR_XTERM

    String currentAnsiColorMap = env.getProperty(EnvironmentConstants.TERM)
    if (currentAnsiColorMap == ansiColorMap) {
        log.debug("Do not wrap with color scheme: '${ansiColorMap}' because wrapper with same color map is already active")
        // current ansi color map is new color map, do not wrap again
        body()
    } else {
        log.debug("Wrapping build with color scheme: '${ansiColorMap}'")
        ansiColor(ansiColorMap) {
            body()
        }
    }


}
