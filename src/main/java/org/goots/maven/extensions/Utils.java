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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Manifest;

class Utils
{
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

    public static Map<String, String> getGITProperties( String executionRootDirectory ) throws IOException
    {
        Map<String, String> map = new HashMap<>();

        Repository repository = new FileRepositoryBuilder().setWorkTree( new File( executionRootDirectory)).readEnvironment().findGitDir().setMustExist( true).build();

        ObjectId head = repository.resolve( "HEAD");
        if (head == null) {
            throw new IllegalStateException("No such revision: HEAD");
        }

        map.put( "git.branch", StringUtils.defaultString( repository.getBranch(), "") );

        String commitId = head.name();
        map.put("git.commit.id", commitId);

        String commitIdAbbrev = repository.newObjectReader().abbreviate(head).name();
        map.put("git.commit.id.abbrev", commitIdAbbrev);

        return map;
    }
}
