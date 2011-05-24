package com.sonatype.nexus.plugins.bundlemaker.bm02;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;

import org.testng.annotations.Test;

import com.sonatype.nexus.plugins.bundlemaker.BundleMakerProxyIT;

public class BM0204ProxyAlreadyABundleNotEagerlyIT
    extends BundleMakerProxyIT
{

    @Test
    public void proxyAlreadyABundleNotEagerly()
        throws Exception
    {
        createCapability();

        final File recipe =
            new File( new File( nexusWorkDir ), "storage/" + getTestRepositoryId()
                + "/org/ops4j/base/ops4j-base-lang/1.2.3/ops4j-base-lang-1.2.3.osgi" );
        assertFalse( recipe.exists(), "Recipe " + recipe.getPath() + " created" );

        final File bundle =
            new File( new File( nexusWorkDir ), "storage/" + getTestRepositoryId()
                + "/org/ops4j/base/ops4j-base-lang/1.2.3/ops4j-base-lang-1.2.3-osgi.jar" );
        assertFalse( bundle.exists(), "Bundle " + bundle.getPath() + " created" );

        final File downloadDir = new File( "target/downloads/" + this.getClass().getSimpleName() );

        try
        {
            downloadArtifact( "org.ops4j.base", "ops4j-base-lang", "1.2.3", "osgi", null, downloadDir.getPath() );
            fail( "Expected a FileNotFoundException" );
        }
        catch ( final FileNotFoundException expected )
        {
        }

        try
        {
            downloadArtifact( "org.ops4j.base", "ops4j-base-lang", "1.2.3", "jar", "osgi", downloadDir.getPath() );
            fail( "Expected a FileNotFoundException" );
        }
        catch ( final FileNotFoundException expected )
        {
        }
    }
}
