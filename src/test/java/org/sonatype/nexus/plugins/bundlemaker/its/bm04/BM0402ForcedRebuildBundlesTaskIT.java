/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.bundlemaker.its.bm04;

import static org.testng.Assert.assertTrue;

import java.io.File;

import org.sonatype.nexus.plugins.bundlemaker.its.BundleMakerIT;
import org.testng.annotations.Test;

public class BM0402ForcedRebuildBundlesTaskIT
    extends BundleMakerIT
{

    public BM0402ForcedRebuildBundlesTaskIT()
    {
        super( "bm04" );
    }

    /**
     * Deploy jars before bundle maker capability is enabled, and check recipe/bundle after rebuild task. Verify that a
     * forced rebuild task recreates them.
     */
    @Test
    public void test()
        throws Exception
    {
        // capability is not eagerly so bundle is not created when jars are deployed
        createCapability();

        deployArtifacts( getTestResourceAsFile( "artifacts/jars" ) );

        runTask( false );

        File recipe = storageRecipeFor( "commons-logging", "commons-logging", "1.1.1" );
        assertTrue( recipe.exists(), "Recipe " + recipe.getPath() + "created" );
        final long lastModified = recipe.lastModified();

        // make a pause so we do not regenerate recipe in same second
        Thread.sleep( 1000 );

        runTask( true );

        recipe = storageRecipeFor( "commons-logging", "commons-logging", "1.1.1" );
        assertTrue( recipe.exists(), "Recipe " + recipe.getPath() + "created" );

        assertTrue( recipe.lastModified() > lastModified, "Recipe was recreated" );

        assertStorageRecipeFor( "commons-logging", "commons-logging", "1.1.1" ).matches(
            getTestResourceAsFile( "manifests/bm0401/commons-logging-1.1.1.osgi.properties" ) );

        assertStorageBundleFor( "commons-logging", "commons-logging", "1.1.1" ).matches(
            getTestResourceAsFile( "manifests/bm0401/commons-logging-1.1.1-osgi.jar.properties" ) );
    }

}
