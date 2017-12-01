# ManagedFiles

The pipeline library supports the loading of Jenkins managed files
references from json files by using the [Pipeline Utility Steps Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Utility+Steps+Plugin)

These references can be used to auto lookup ManageFile ids based on
patterns. This can be useful to provide maven settings based on the `scm` url.

Based on the rules for writing libraries these json files must be places
below the resources folder.

:bulb: The library only works with references/ids so your config files
remain safe in the Jenkins instance.

:bulb: See also [`execMaven`](../vars/execMaven.md) step

:bulb: See
[Extending with Shared Libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/)
for more information

# Table of contents
* [JSON Format](#json-format)
* [Using Credentials](#using-credentials)
* [Step examples](#step-examples)

* [Related classes](credentials.md#related-classes)

## JSON Format

In order to parse the files correctly they must be in the following format:

```json
[
  {
    "pattern": "git\.yourcompany\.tld\/group1\/project1",
    "id": "group1-project1-managed-file",
    "name": "Local maven settings group1/project1",
    "comment": "Deploy maven setttings for project1 from group1 for nexus.yourcompany.tld"
  },
  {
      "pattern": "github\.com\/wcm-io",
      "id": "wcm-io-maven-global-settings",
      "name": "global maven settings wcm-io",
      "comment": "Global maven settings to build wcm-io projects"
  }
]
```

The properties `pattern` and `id` are mandatory, the `comment` and `name` properties
are optional and can be omitted.

## Using managed files

In order to use managed files inside your pipeline script you have to
* load
* parse and
* search for a managed file based on a pattern

:bulb: The pattern is treated as regular expression

The Example is based on the `execMaven` step.
This step loads a json and matches the incoming scm url against the entries to find matching settings ids to provide for the maven `shell` call.

```groovy
import io.wcm.devops.jenkins.pipeline.managedfiles.ManagedFile 

ManagedFile autoLookupMavenSettings(String jsonPath, String scmUrl) {
    // load and parse the json
    JsonLibraryResource jsonLibraryResource = new JsonLibraryResource(steps, jsonPath)
    JSON managedFilesJson = jsonLibraryResource.load()
    ManagedFileParser parser = new ManagedFileParser()
    List<PatternMatchable> managedFiles = parser.parse(managedFilesJson)
    
    // match the scmUrl against the parsed mangedFiles and get the best match
    PatternMatcher matcher = new PatternMatcher()
    return (ManagedFile) matcher.getBestMatch(scmUrl, managedFiles)
}

void getGlobalMavenSettings() {
    ManagedFile managedFile = autoLookupMavenSettings('resources/managedfiles/maven/global-settings.json', 'git@git.yourcompany.tld:group1/project1')
    echo "Managed file id: '${managedFile.id}'" 
}
```
The result in this example would be a output in the log like:

    Managed file id: 'group1-project1-managed-file'

:bulb: Refer to [PatternMatching](https://github.com/wcm-io-devops/jenkins-pipeline-library/blob/master/docs/pattern-matching.md) for more
information on how the `getBestMatch` algorithm works

### Step examples

#### configFileProvider
```groovy
// load and parse the json
JsonLibraryResource jsonLibraryResource = new JsonLibraryResource(steps, 'resources/path/to/config.json')
JSON managedFilesJson = jsonLibraryResource.load()

ManagedFileParser parser = new ManagedFileParser()
List<PatternMatchable> managedFiles = parser.parse(managedFilesJson)

// match the scmUrl against the parsed mangedFiles and get the best match
PatternMatcher matcher = new PatternMatcher()
ManagedFile managedFile = matcher.getBestMatch('git@git.yourcompany.tld:group1/project1', managedFiles)

List configFiles = []
if (managedFile) {
    configFiles.push(configFile(fileId: managedFile.getId(), targetLocation: "", variable: 'MY_VARIABLE'))
}

configFileProvider(configFiles) {
    // some block
}
```
