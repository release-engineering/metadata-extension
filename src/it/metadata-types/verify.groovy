/**
 * Copyright (C) 2012 Red Hat, Inc. (jcasey@redhat.com)
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

def failed = true
def plugin = null
assert pom.build.plugins.children().size() == 0

def zips = [ new File ( basedir, "/jar/target/metadata-types-jar-1.jar" ),
             new File ( basedir, "/bundle/target/metadata-types-bundle-1.jar" ),
             new File ( basedir, "/war/target/metadata-types-war-1.war" ),
             new File ( basedir, "/ear/target/metadata-types-ear-1.ear" )
]

for ( File target : zips )
{
    System.out.println ("Using " + target)
    def zipFile = new java.util.zip.ZipFile(target)
    failed = true
    zipFile.entries().each {
        println zipFile.getInputStream(it).text
        if ( zipFile.getInputStream(it).text.contains("Written by Metadata-Extension") )
        {
            failed = false
        }
    }
    if ( failed == true ) System.out.println ("### Failed with " + target)
    assert failed == false
}
