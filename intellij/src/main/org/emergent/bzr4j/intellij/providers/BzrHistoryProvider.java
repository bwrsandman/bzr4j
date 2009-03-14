package org.emergent.bzr4j.intellij.providers;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.history.FileHistoryPanel;
import com.intellij.openapi.vcs.history.HistoryAsTreeProvider;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.history.VcsHistoryProvider;
import com.intellij.openapi.vcs.history.VcsHistorySession;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.util.ui.ColumnInfo;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.BazaarRevision;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.intellij.BzrFileRevision;
import org.emergent.bzr4j.intellij.BzrRevisionNumber;
import org.emergent.bzr4j.intellij.BzrVcs;
import org.emergent.bzr4j.intellij.utils.IJUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BzrHistoryProvider implements VcsHistoryProvider
{

    private BzrVcs bzr;

    public BzrHistoryProvider( BzrVcs bzr )
    {
        this.bzr = bzr;
    }

    public ColumnInfo[] getRevisionColumns()
    {
        return new ColumnInfo[0];
    }

    public AnAction[] getAdditionalActions( FileHistoryPanel panel )
    {
        return new AnAction[0];
    }

    public boolean isDateOmittable()
    {
        return false;
    }

    @Nullable
    @NonNls
    public String getHelpId()
    {
        return null;
    }

    @Nullable
    public VcsHistorySession createSessionFor( FilePath filePath ) throws VcsException
    {
        try
        {
            final File file = filePath.getIOFile();

            BazaarRevision revno = bzr.getBzrClient().revno( file );

            final File bzrRoot = IJUtil.root( file );

            bzr.getBzrClient().setWorkDir( bzrRoot );
            List<IBazaarLogMessage> messages = bzr.getBzrClient().log( file );

            List<VcsFileRevision> revisions = new ArrayList<VcsFileRevision>();
            for ( IBazaarLogMessage message : messages )
            {
                revisions.add( new BzrFileRevision( bzr, file, message ) );
            }

            return new VcsHistorySession(revisions,new BzrRevisionNumber( revno )) {
                protected VcsRevisionNumber calcCurrentRevisionNumber()
                {
                    try
                    {
                        return new BzrRevisionNumber( bzr.getBzrClient().revno( file ) );
                    }
                    catch ( BazaarException e )
                    {
                        throw IJUtil.notYetHandled( e );
                    }
                }
            };
        }
        catch ( BazaarException e )
        {
            throw IJUtil.notYetHandled( e );
        }
    }

    @Nullable
    public HistoryAsTreeProvider getTreeHistoryProvider()
    {
        // todo: try to implement this feature cause bzr has tree history
        return null;
    }

    public ColumnInfo[] getRevisionColumns( VcsHistorySession session )
    {
        return new ColumnInfo[0];
    }

    public boolean supportsHistoryForDirectories()
    {
        return false;
    }
}
