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

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class MetadataEventSpy extends AbstractEventSpy
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final String DISABLE_METADATA_EXTENSION = "metadata.extension.disable";

    private final MetadataInjection injector;

    @Inject
    public MetadataEventSpy(MetadataInjection injector)
    {
        this.injector = injector;
    }

    @Override
    public void onEvent( Object event ) throws Exception
    {
        if ( isEventSpyDisabled() )
        {
            return;
        }

        try
        {
            if ( event instanceof ExecutionEvent )
            {
                final ExecutionEvent ee = (ExecutionEvent) event;
                final ExecutionEvent.Type type = ee.getType();

                // Rather than using SessionStarted or ProjectDiscoveryStarted we will use MojoStarted. This is
                // because if the user has activated the clean lifecycle that would wipe away any metadata creation.
                // Therefore we will inject at the start of the validate lifecycle, just after clean is finished.
                //
                // Note that ee.getMojoExecution is only valid in the MojoStarted phase.
                if ( type == ExecutionEvent.Type.MojoStarted )
                {
                    if ( ee.getMojoExecution().getLifecyclePhase().equals( "validate" ) )
                    {
                        if ( ee.getSession() != null )
                        {
                            logger.info( "Activating metadata extension" );
                            logger.debug( "Activating metadata extension {} ", Utils.getManifestInformation() );

                            injector.createMetadata( ee.getSession() );
                        }
                        else
                        {
                            logger.error( "Null session ; unable to continue" );
                        }
                    }

                }
            }
        }
        // Catch any runtime exceptions and mark them to fail the build as well.
        catch ( final Throwable e )
        {
            // TODO: Correctly fail the build if the extension fails...
            logger.error( "Extension failure", e );
            throw e;
        }
    }


    private boolean isEventSpyDisabled(){
        return "true".equalsIgnoreCase(System.getProperty(DISABLE_METADATA_EXTENSION)) ||
                "true".equalsIgnoreCase(System.getenv(DISABLE_METADATA_EXTENSION));
     }
}
