# integrationTestUtils

Due to the constantly changing sandbox and CPS implementation
integration tests were introduced in version 0.9.

The target is to offer test stuff to just test if the Classes and utils
work after a update of the Jenkins and the pipeline plugins

To enable the resuse of some of the integration test functionalities
some of the test utils were moved to this file.

These utils are used by:
* [integration-tests](../jenkinsfiles/integration-tests.groovy)

## Related classes
* [`IntegrationTestHelper`](../src/io/wcm/devops/jenkins/pipeline/utils/IntegrationTestHelper.groovy)


