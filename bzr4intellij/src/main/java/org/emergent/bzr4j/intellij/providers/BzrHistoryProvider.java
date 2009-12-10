package org.emergent.bzr4j.intellij.providers;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.history.FileHistoryPanel;
import com.intellij.openapi.vcs.history.HistoryAsTreeProvider;
import com.intellij.openapi.vcs.history.VcsDependentHistoryComponents;
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
import org.emergent.bzr4j.intellij.utils.IJUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BzrHistoryProvider implements VcsHistoryProvider
{
    private final Project m_project;

    public BzrHistoryProvider( @NotNull Project project )
    {
        m_project = project;
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

            BazaarRevision revno = IJUtil.createBzrClient().revno( file );

            final File bzrRoot = IJUtil.root( file );

            IJUtil.createBzrClient().setWorkDir( bzrRoot );
            List<IBazaarLogMessage> messages = IJUtil.createBzrClient().log( file );

            List<VcsFileRevision> revisions = new ArrayList<VcsFileRevision>();
            for ( IBazaarLogMessage message : messages )
            {
                revisions.add( new BzrFileRevision( m_project, file, message ) );
            }

            return new VcsHistorySession(revisions,new BzrRevisionNumber( revno )) {
                protected VcsRevisionNumber calcCurrentRevisionNumber()
                {
                    try
                    {
                        return new BzrRevisionNumber( IJUtil.createBzrClient().revno( file ) );
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

    public VcsDependentHistoryComponents getUICustomization( VcsHistorySession session,
            JComponent forShortcutRegistration )
    {
        return VcsDependentHistoryComponents.createOnlyColumns( getRevisionColumns() );
    }
}
