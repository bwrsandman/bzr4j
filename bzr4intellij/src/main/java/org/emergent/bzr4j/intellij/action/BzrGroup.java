package org.emergent.bzr4j.intellij.action;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.actions.StandardVcsGroup;
import org.emergent.bzr4j.intellij.BzrVcs;

public class BzrGroup extends StandardVcsGroup
{

    public AbstractVcs getVcs( Project project )
    {
        return ProjectLevelVcsManager.getInstance( project ).findVcsByName( BzrVcs.VCS_NAME );
    }

    @Override
    public String getVcsName( final Project project )
    {
        return BzrVcs.VCS_NAME;
    }
}
