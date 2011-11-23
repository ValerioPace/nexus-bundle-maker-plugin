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
import org.sonatype.nexus.proxy.item.DefaultStorageLinkItem;
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

    static void createLink( final Repository repository, final StorageItem item, final String path )
        throws Exception
    {
        final ResourceStoreRequest req = new ResourceStoreRequest( path );

        req.getRequestContext().putAll( item.getItemContext() );

        final DefaultStorageLinkItem link =
            new DefaultStorageLinkItem( repository, req, true, true, item.getRepositoryItemUid() );

        repository.storeItem( false, link );
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

    static void deleteItem( final Repository repository, final String path )
        throws Exception
    {
        final ResourceStoreRequest request = new ResourceStoreRequest( path );
        repository.deleteItem( request );
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

        return relativePath.replace( File.separator, "/" );
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