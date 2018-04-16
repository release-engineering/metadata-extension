/**
 * Copyright (C) 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
def pomFile = new File( basedir, 'pom.xml' )
System.out.println( "Slurping POM: ${pomFile.getAbsolutePath()}" )

def pom = new XmlSlurper().parse( pomFile )
System.out.println( "POM Version: ${pom.version.text()}" )

// Verify logging
def buildLog = new File( basedir, 'build.log' )
def message = 0
buildLog.eachLine {
   if (it.contains( "BUILD FAILURE")) {
      message++
   }
   if (it.contains( "Failed to record metadata")) {
      message++
   }
}
assert message == 3

// Verify metadata exists
def metaFile = new File( basedir, 'target/classes/META-INF/build.metadata' )
assert ! metaFile.exists()
