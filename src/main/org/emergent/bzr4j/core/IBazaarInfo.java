/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

import java.util.Date;

/**
 * This will contain all known locations and formats associated to the
 * tree, branch or repository. Also statistical information is included.
 *
 * Branches and working trees will also report any missing revisions.
 *
 * @author Guillermo Gonzalez
 */
public interface IBazaarInfo
{

    // info
    String getLayout();

    String[] getFormats();

    // locations
    Locations getLocations();

    RelatedBranches getRelatedBranches();

    // formats
    String getControlFormat();

    String getWorkingTreeFormat();

    String getBranchFormat();

    String getRepositoryFormat();

    // working tree stats
    WorkingTreeStats getWorkingTreeStats();

    // branch history
    BranchHistory getBranchHistory();

    // repo stats
    RepositoryStats getRepositoryStats();

    interface RelatedBranches
    {
        String getPublicBranch();

        String getPushBranch();

        String getParentBranch();

        String getSubmitBranch();
    }

    interface WorkingTreeStats
    {
        Integer getUnchanged();

        Integer getModified();

        Integer getAdded();

        Integer getRemoved();

        Integer getRenamed();

        Integer getUnknown();

        Integer getIgnored();

        Integer getVersionedSubdirectories();
    }

    interface BranchHistory
    {
        Integer getRevisions();

        Integer getCommitters();

        Integer getDaysOld();

        Date getFirstRevisionDate();

        Date getLatestRevisionDate();
    }

    interface RepositoryStats
    {
        Integer getRevisionCount();

        Long getSize();
    }

    interface Locations
    {
        String getLightCheckoutRoot();

        String getRepositoryCheckoutRoot();

        String getCheckoutRoot();

        String getCheckoutOfBranch();

        String getSharedRepository();

        String getRepository();

        String getRepositoryBranch();

        String getBranchRoot();

        String getBoundToBranch();
    }
}
