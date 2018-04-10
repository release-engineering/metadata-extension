### Maven Metadata Extension

This is a Maven Extension which can create basic metadata for builds. It is compatible with Maven 3.1.1 and above.

It is inspired by https://github.com/release-engineering/buildmetadata-maven-plugin and http://www.smartics.eu/buildmetadata-maven-plugin

# Installation
Place in the Maven installation lib/ext directory or for Maven 3.3.1 above using Core Extensions ( https://maven.apache.org/docs/3.3.1/release-notes.html )

# Usage
Once the extension is installed it will automatically create a `build.metadata` in the target build directory (or appropriate subdirectory thereof) so that it is packaged within the artifact.

To disable the extension set `metadata.extension.disable` to true.
