### Maven Metadata Extension

This is a Maven Extension which can create basic metadata for builds. It is compatible with Maven 3.1.1 and above.

It is inspired by https://github.com/release-engineering/buildmetadata-maven-plugin and http://www.smartics.eu/buildmetadata-maven-plugin

# Installation
Place in the Maven installation lib/ext directory or for Maven 3.3.1 above using Core Extensions ( https://maven.apache.org/docs/3.3.1/release-notes.html )

# Usage
Once the extension is installed it will automatically create a `build.metadata` in the target build directory (or appropriate subdirectory thereof) so that it is packaged within the artifact.

An example of the metadata file is:

```
#Written by Metadata-Extension 1.0-SNAPSHOT ( SHA: e094a650 )
#Wed May 09 10:07:27 BST 2018
build.java.version=1.8.0_151
build.maven.execution.cmdline=clean install
build.maven.version=3.3.9
build.scmRevision.branch=master
build.scmRevision.id=aac67e7267c5cb9ceed7b23313848a6d642cc925
build.scmRevision.id.abbrev=aac67e7
```

To disable the extension set `metadata.extension.disable` to true.
