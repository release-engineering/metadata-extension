/*
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
package org.goots.maven.extensions;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.apache.maven.rtinfo.RuntimeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

public class MetadataInjection
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final String fileName = "build.metadata";

    private final Properties metadata = new Properties( );

    private final RuntimeInformation runtime;


    @Inject
    public MetadataInjection ( RuntimeInformation runtime )
    {
        this.runtime = runtime;
    }

    public void createMetadata( MavenSession session ) throws IOException
    {
        recordMetadata( session, Utils.getGITProperties( session.getExecutionRootDirectory()));

        for ( MavenProject project : session.getProjects())
        {
            logger.debug ("Examining {} with base dir {} packaging {} and final name {}", project.getId(),
                         project.getBasedir(), project.getPackaging() , project.getBuild().getFinalName() );

            if ( ! project.getPackaging().equals( "pom" ) )
            {
                File target = calcMetadataLocation( project );
                target.getParentFile().mkdirs();

                try ( OutputStream outputStream = new FileOutputStream( target ) )
                {
                    metadata.store( outputStream, "Written on by Metadata-Extension " + Utils.getManifestInformation() );
                }
            }
        }
    }

    private void recordMetadata( MavenSession session, Map<String, String> gitProperties )
    {
        metadata.setProperty( "build.maven.execution.cmdline", calcCommandLine( session.getSystemProperties() ) );
        metadata.setProperty( "build.maven.version", runtime.getMavenVersion() );
        metadata.setProperty( "build.java.version", session.getSystemProperties().getProperty( "java.version" ) );

        metadata.putAll( gitProperties );

    }

    private String calcCommandLine( final Properties executionProperties )
    {
        String commandLine = executionProperties.getProperty( "sun.java.command" );
        if ( commandLine != null )
        {
            commandLine = commandLine.replace( "org.codehaus.plexus.classworlds.launcher.Launcher", "" ).trim();
        }
        return commandLine;
    }

    private File calcMetadataLocation( MavenProject project)
    {
        final Build build = project.getBuild();
        final File jarFile = new File( build.getOutputDirectory(), "META-INF/" + fileName );
        final File targetDir = new File( build.getDirectory() );
        final File deploymentUnitFile = new File( targetDir, build.getFinalName() + "/META-INF/" + fileName );

        File result;

        switch ( project.getPackaging() )
        {
            case "war":
            case "ear":
            case "sar":
            case "rar":
            case "par":
            {
                result = deploymentUnitFile;
                break;
            }
            case "bundle":
            case "ejb":
            case "maven-plugin":
            case "atlassian-plugin":
            case "hpi":
            case "maven-archetype":
            case "jar":
            {
                result = jarFile;
                break;
            }
            default:
            {
                result = new File ( targetDir, fileName);
                break;
            }
        }
        return result;
    }
}
