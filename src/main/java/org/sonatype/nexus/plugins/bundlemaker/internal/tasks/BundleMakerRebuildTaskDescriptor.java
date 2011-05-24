package org.sonatype.nexus.plugins.bundlemaker.internal.tasks;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

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

    public static final String REPO_OR_GROUP_FIELD_ID = "repositoryId";

    public static final String RESOURCE_STORE_PATH_FIELD_ID = "resourceStorePath";

    private final RepoOrGroupComboFormField repoField = new RepoOrGroupComboFormField( REPO_OR_GROUP_FIELD_ID,
        FormField.MANDATORY );

    private final StringTextFormField resourceStorePathField = new StringTextFormField( RESOURCE_STORE_PATH_FIELD_ID,
        "Repository path",
        "Enter a repository path to run the task in recursively (ie. \"/\" for root or \"/org/apache\")",
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
        return Arrays.<FormField> asList( repoField, resourceStorePathField );
    }

}
