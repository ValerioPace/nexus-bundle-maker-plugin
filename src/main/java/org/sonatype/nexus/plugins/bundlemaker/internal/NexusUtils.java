package org.sonatype.nexus.plugins.bundlemaker.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.index.artifact.Gav;
import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.item.DefaultStorageFileItem;
import org.sonatype.nexus.proxy.item.PreparedContentLocator;
import org.sonatype.nexus.proxy.item.RepositoryItemUid;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.storage.local.fs.DefaultFSLocalRepositoryStorage;

class NexusUtils
{

    private NexusUtils()
    {
    }

    static File retrieveFile( final Repository repository, final String path )
        throws LocalStorageException
    {
        final ResourceStoreRequest request = new ResourceStoreRequest( path );
        final File content =
            ( (DefaultFSLocalRepositoryStorage) repository.getLocalStorage() ).getFileFromBase( repository, request );
        return content;
    }

    static File safeRetrieveFile( final Repository repository, final String path )
    {
        try
        {
            return retrieveFile( repository, path );
        }
        catch ( final LocalStorageException e )
        {
            return null;
        }
    }

    static StorageItem retrieveItem( final MavenRepository repository, final Gav gav )
        throws Exception
    {
        return retrieveItem( repository, repository.getGavCalculator().gavToPath( gav ) );
    }

    static StorageItem safeRetrieveItem( final MavenRepository repository, final Gav gav )
    {
        try
        {
            return retrieveItem( repository, gav );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    static StorageItem retrieveItem( final Repository repository, final String path )
        throws Exception
    {
        final ResourceStoreRequest request = new ResourceStoreRequest( path );
        final StorageItem item = repository.retrieveItem( request );
        return item;
    }

    static StorageItem retrieveItemBypassingChecks( final Repository repository, final String path )
        throws Exception
    {
        final ResourceStoreRequest request = new ResourceStoreRequest( path );
        final StorageItem item = repository.retrieveItem( false, request );
        return item;
    }

    static StorageItem safeRetrieveItem( final Repository repository, final String path )
    {
        try
        {
            return retrieveItem( repository, path );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    static StorageItem safeRetrieveItemBypassingChecks( final Repository repository, final String path )
    {
        try
        {
            return retrieveItemBypassingChecks( repository, path );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    public static void storeItem( final Repository repository, final ResourceStoreRequest request,
                                  final InputStream in, final String mimeType, final Map<String, String> userAttributes )
        throws Exception
    {
        final DefaultStorageFileItem fItem =
            new DefaultStorageFileItem( repository, request, true, true, new PreparedContentLocator( in, mimeType ) );

        if ( userAttributes != null )
        {
            fItem.getAttributes().putAll( userAttributes );
        }

        repository.storeItem( false, fItem );
    }

    static File localStorageOfRepositoryAsFile( final Repository repository )
        throws LocalStorageException
    {
        if ( repository.getLocalUrl() != null
            && repository.getLocalStorage() instanceof DefaultFSLocalRepositoryStorage )
        {
            final File baseDir =
                ( (DefaultFSLocalRepositoryStorage) repository.getLocalStorage() ).getBaseDir( repository,
                    new ResourceStoreRequest( RepositoryItemUid.PATH_ROOT ) );
            return baseDir;
        }

        throw new LocalStorageException( String.format( "Repository [%s] does not have an local storage",
            repository.getId() ) );
    }

    static String getRelativePath( final File fromFile, final File toFile )
    {
        final String[] fromSegments = getReversePathSegments( fromFile );
        final String[] toSegments = getReversePathSegments( toFile );

        String relativePath = "";
        int i = fromSegments.length - 1;
        int j = toSegments.length - 1;

        // first eliminate common root
        while ( ( i >= 0 ) && ( j >= 0 ) && ( fromSegments[i].equals( toSegments[j] ) ) )
        {
            i--;
            j--;
        }

        for ( ; i >= 0; i-- )
        {
            relativePath += ".." + File.separator;
        }

        for ( ; j >= 1; j-- )
        {
            relativePath += toSegments[j] + File.separator;
        }

        relativePath += toSegments[j];

        return relativePath;
    }

    private static String[] getReversePathSegments( final File file )
    {
        final List<String> paths = new ArrayList<String>();

        File segment;
        try
        {
            segment = file.getCanonicalFile();
            while ( segment != null )
            {
                paths.add( segment.getName() );
                segment = segment.getParentFile();
            }
        }
        catch ( final IOException e )
        {
            return null;
        }
        return paths.toArray( new String[paths.size()] );
    }

}