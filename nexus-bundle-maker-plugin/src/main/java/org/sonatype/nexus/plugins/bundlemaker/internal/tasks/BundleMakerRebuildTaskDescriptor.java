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
package org.sonatype.nexus.plugins.bundlemaker.internal.tasks;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import org.sonatype.nexus.formfields.CheckboxFormField;
import org.sonatype.nexus.formfields.FormField;
import org.sonatype.nexus.formfields.RepoOrGroupComboFormField;
import org.sonatype.nexus.formfields.StringTextFormField;
import org.sonatype.nexus.tasks.descriptors.AbstractScheduledTaskDescriptor;
import org.sonatype.nexus.tasks.descriptors.ScheduledTaskDescriptor;

@Named
public class BundleMakerRebuildTaskDescriptor
    extends AbstractScheduledTaskDescriptor
    implements ScheduledTaskDescriptor
{

    public static final String ID = "BundleMakerRebuildTask";

    public static final String REPOSITORY_FIELD_ID = "repositoryId";

    public static final String RESOURCE_STORE_PATH_FIELD_ID = "resourceStorePath";

    public static final String FORCED_REGENERATION_FIELD_ID = "forceRegeneration";

    private final RepoOrGroupComboFormField repoField = new RepoOrGroupComboFormField( REPOSITORY_FIELD_ID,
        FormField.MANDATORY );

    private final StringTextFormField resourceStorePathField = new StringTextFormField( RESOURCE_STORE_PATH_FIELD_ID,
        "Repository path",
        "Enter a repository path to run the task in recursively (ie. \"/\" for root or \"/org/apache\")",
        FormField.OPTIONAL );

    private final FormField forceRegeneration = new CheckboxFormField( FORCED_REGENERATION_FIELD_ID,
        "Force regeneration", "Check this if you wanna force regeneration of bundles even if is up to date",
        FormField.OPTIONAL );

    @Override
    public String getId()
    {
        return ID;
    }

    @Override
    public String getName()
    {
        return "Rebuild OSGi bundles";
    }

    @Override
    public List<FormField> formFields()
    {
        return Arrays.<FormField> asList( repoField, resourceStorePathField, forceRegeneration );
    }

}
