package org.emergent.bzr4j.intellij.providers;

import com.intellij.openapi.vcs.ChangeListColumn;
import com.intellij.openapi.vcs.CommittedChangesProvider;
import com.intellij.openapi.vcs.DefaultRepositoryLocation;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.RepositoryLocation;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.committed.DecoratorManager;
import com.intellij.openapi.vcs.changes.committed.VcsCommittedListsZipper;
import com.intellij.openapi.vcs.changes.committed.VcsCommittedViewAuxiliary;
import com.intellij.openapi.vcs.versionBrowser.ChangeBrowserSettings;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeListImpl;
import com.intellij.openapi.vcs.versionBrowser.StandardVersionFilterComponent;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.intellij.BzrVcs;
import org.emergent.bzr4j.intellij.utils.IJUtil;
import org.emergent.bzr4j.utils.BzrUtil;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BzrCommittedChangesProvider
        implements CommittedChangesProvider<CommittedChangeList, ChangeBrowserSettings>
{
    private BzrVcs m_vcs;

    public BzrCommittedChangesProvider( BzrVcs bzrVcs )
    {
        m_vcs = bzrVcs;
    }

    public ChangeBrowserSettings createDefaultSettings()
    {
        return new ChangeBrowserSettings();
    }

    public StandardVersionFilterComponent<ChangeBrowserSettings> createFilterUI(
            boolean showDateFilter )
    {
        return new StandardVersionFilterComponent<ChangeBrowserSettings>( showDateFilter )
        {
            public JComponent getComponent()
            {
                return new JPanel();
            }
        };
    }

    public RepositoryLocation getLocationFor( FilePath root )
    {
        return getLocationFor( root, null );
    }

    public RepositoryLocation getLocationFor( FilePath root, String repositoryPath )
    {
        try
        {
            return new DefaultRepositoryLocation(
                    BzrUtil.getRootBranch( new File( root.getPath() ) ).getPath() );
        }
        catch ( Exception e )
        {
            throw IJUtil.notYetHandled( e );
        }
    }

    public List<CommittedChangeList> getCommittedChanges( ChangeBrowserSettings settings,
            RepositoryLocation location,
            int maxCount ) throws VcsException
    {
        try
        {
            File root = new File( location.toPresentableString() );
            m_vcs.getBzrClient().setWorkDir( root );
            List<IBazaarLogMessage> messages = m_vcs.getBzrClient().log( root );

            List<CommittedChangeList> revisions =
                    new ArrayList<CommittedChangeList>( messages.size() );
            for ( IBazaarLogMessage msg : messages )
            {
                revisions.add( new CommittedChangeListImpl( "name?", msg.getMessage(),
                        msg.getCommiter(),
                        0, msg.getDate(), null
                ) );
            }

            return revisions;

        }
        catch ( BazaarException e )
        {
            throw IJUtil.notYetHandled( e );
        }
    }

    public ChangeListColumn[] getColumns()
    {
        return new ChangeListColumn[]{ChangeListColumn.DATE, ChangeListColumn.DESCRIPTION,
                ChangeListColumn.NAME, ChangeListColumn.NUMBER};
    }

    public VcsCommittedListsZipper getZipper()
    {
        return null;
    }

    public VcsCommittedViewAuxiliary createActions( DecoratorManager manager,
            RepositoryLocation location )
    {
        return null;
    }

    public int getUnlimitedCountValue()
    {
        return 0;
    }
}
