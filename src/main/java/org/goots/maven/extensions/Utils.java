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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.Manifest;

class Utils
{
   private static final Logger logger = LoggerFactory.getLogger( Utils.class );

   /**
     * Retrieves the SHA this was built with.
     *
     * @return the GIT sha of this codebase.
     */
    public static String getManifestInformation() throws IOException
    {
        String result = "";

        final Enumeration<URL> resources = Utils.class.getClassLoader().getResources( "META-INF/MANIFEST.MF" );

        while ( resources.hasMoreElements() )
        {
            final URL jarUrl = resources.nextElement();

            if ( jarUrl.getFile().contains( "metadata-extension-" ) )
            {
                final Manifest manifest = new Manifest( jarUrl.openStream() );

                result = manifest.getMainAttributes().getValue( "Implementation-Version" );
                result += " ( SHA: " + manifest.getMainAttributes().getValue( "Scm-Revision" ) + " ) ";
                break;
            }
        }

        return result;
    }

    public static Map<String, String> getGITProperties( String executionRootDirectory )
    {
        Map<String, String> map = new TreeMap<>();

        try
        {
            // TODO: Handle closing/cleanup
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder.readEnvironment()
                                           .findGitDir(new File (executionRootDirectory ))
                                           .setMustExist( true )
                                           .build();

            ObjectId head = repository.resolve( "HEAD" );
            if ( head == null )
            {
                throw new IOException( "No such revision: HEAD" );
            }

            map.put( "build.scmRevision.branch", StringUtils.defaultString( repository.getBranch(), "" ) );

            String commitId = head.name();
            map.put( "build.scmRevision.id", commitId );

            String commitIdAbbrev = repository.newObjectReader().abbreviate( head ).name();
            map.put( "build.scmRevision.id.abbrev", commitIdAbbrev );
        }
        catch (IOException e)
        {
            logger.error( "Caught exception examining GIT repository ", e );
        }
        return map;
    }
}
