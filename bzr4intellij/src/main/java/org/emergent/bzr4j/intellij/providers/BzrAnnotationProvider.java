package org.emergent.bzr4j.intellij.providers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.annotate.AnnotationListener;
import com.intellij.openapi.vcs.annotate.AnnotationProvider;
import com.intellij.openapi.vcs.annotate.AnnotationSourceSwitcher;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.annotate.LineAnnotationAspect;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vfs.VirtualFile;
import org.emergent.bzr4j.commandline.syntax.IAnnotateOptions;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.BazaarRevision;
import org.emergent.bzr4j.core.IBazaarAnnotation;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.intellij.BzrContentRevision;
import org.emergent.bzr4j.intellij.BzrFileRevision;
import org.emergent.bzr4j.intellij.BzrRevisionNumber;
import org.emergent.bzr4j.intellij.BzrVcs;
import org.emergent.bzr4j.intellij.config.BzrVcsSettings;
import org.emergent.bzr4j.intellij.utils.IJUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BzrAnnotationProvider implements AnnotationProvider
{
    private final Project m_project;

    public BzrAnnotationProvider( @NotNull Project project )
    {
        m_project = project;
    }

    public FileAnnotation annotate( VirtualFile file ) throws VcsException
    {
        return annotate( file, null );
    }

    public FileAnnotation annotate( VirtualFile file, VcsFileRevision revision ) throws VcsException
    {
        try
        {
            BzrVcs vcs = BzrVcs.getInstance( m_project );
            BzrRevisionNumber revno = revision != null
                    ? (BzrRevisionNumber)revision.getRevisionNumber()
                    : (BzrRevisionNumber)vcs.getDiffProvider().getCurrentRevision( file );

            IBazaarAnnotation annotation = IJUtil.createBzrClient().annotate( IJUtil.toFile( file ),
                    IAnnotateOptions.REVISION.setArgument( String.valueOf( revno ) ) );

            BzrContentRevision thing = new BzrContentRevision( vcs, IJUtil.toFilePath( file ), revno );

            return new BzrFileAnnotation( annotation, vcs, file, thing.getContent() );
        }
        catch ( BazaarException e )
        {
            throw IJUtil.notYetHandled( e );
        }
    }

    public boolean isAnnotationValid( VcsFileRevision rev )
    {
        return true;
    }

    public class BzrFileAnnotation implements FileAnnotation
    {
        private IBazaarAnnotation annotation;

        private final BzrVcs bzr;

        private final VirtualFile myFile;

        private final List<AnnotationListener> myListeners = new ArrayList<AnnotationListener>();

        private String m_content;

        private final LineAnnotationAspect DATE_ASPECT = new LineAnnotationAspect()
        {
            public String getValue( int lineNumber )
            {
                String retval = "";
                if ( annotation.getNumberOfLines() > lineNumber && lineNumber >= 0 )
                {
                    retval = annotation.getDate( lineNumber );
                }
                return retval;
            }

            public String getTooltipText( int lineNumber )
            {
                return null; // todo implement
            }
        };

        private final LineAnnotationAspect REVISION_ASPECT = new RevisionAnnotationAspect();

        private final LineAnnotationAspect AUTHOR_ASPECT = new LineAnnotationAspect()
        {
            public String getValue( int lineNumber )
            {
                String retval = "";
                if ( annotation.getNumberOfLines() > lineNumber && lineNumber >= 0 )
                {
                    retval = annotation.getAuthor( lineNumber );
                    int atIdx = retval.indexOf( '@' );
                    if (atIdx > 0 && BzrVcsSettings.getInstance( m_project ).isAnnotationTrimmingEnabled())
                    {
                        retval = retval.substring( 0, atIdx );
                    }
                }
                return retval;
            }

            public String getTooltipText( int lineNumber )
            {
                return null; // todo implement
            }
        };

//  private final SvnEntriesListener myListener = new SvnEntriesListener() {
//    public void onEntriesChanged(VirtualFile directory) {
//      final AnnotationListener[] listeners = myListeners.toArray(new AnnotationListener[myListeners.size()]);
//      for (int i = 0; i < listeners.length; i++) {
//        listeners[i].onAnnotationChanged();
//      }
//    }
//  };

        public BzrFileAnnotation( IBazaarAnnotation annotation, final BzrVcs vcs,
                final VirtualFile file, String content )
        {
            this.annotation = annotation;

            bzr = vcs;
            myFile = file;
            m_content = content;
        }

        public void addListener( AnnotationListener listener )
        {
            myListeners.add( listener );
        }

        public void removeListener( AnnotationListener listener )
        {
            myListeners.remove( listener );
        }

        public void dispose()
        {
//    myVcs.getSvnEntriesFileListener().removeListener(myListener);
        }

        public LineAnnotationAspect[] getAspects()
        {
            return new LineAnnotationAspect[]{REVISION_ASPECT, DATE_ASPECT, AUTHOR_ASPECT};
        }

        public String getToolTip( final int lineNumber )
        {
//    if (myLineInfos.size() <= lineNumber || lineNumber < 0) {
//      return "";
//    }
//    final LineInfo info = myLineInfos.get(lineNumber);
//    BzrFileRevision svnRevision = myRevisionMap.get(info.getRevision());
//    if (svnRevision != null) {
//      return "Revision " + info.getRevision() + ": " + svnRevision.getCommitMessage();
//    }
            return "";
        }

        public String getAnnotatedContent()
        {
            return m_content;
        }

        public VcsRevisionNumber getLineRevisionNumber( final int lineNumber )
        {
            if ( annotation.getNumberOfLines() <= lineNumber || lineNumber < 0 )
            {
                return null;
            }
            BazaarRevision bazRev =
                    BazaarRevision.getRevision( annotation.getRevision( lineNumber ) );
            return new BzrRevisionNumber( bazRev );
        }

        public List<VcsFileRevision> getRevisions()
        {
            try
            {
                File file = new File( myFile.getPath() );
                List<IBazaarLogMessage> messages = IJUtil.createBzrClient().log( file );
                List<VcsFileRevision> result = new ArrayList<VcsFileRevision>();
                for ( IBazaarLogMessage message : messages )
                {
                    result.add( new BzrFileRevision( bzr, file, message ) );
                }
                return result;
            }
            catch ( BazaarException e )
            {
                throw new RuntimeException( e );
            }
        }

        public VcsRevisionNumber originalRevision( int lineNumber )
        {
            return null; // todo implement
        }

        public AnnotationSourceSwitcher getAnnotationSourceSwitcher()
        {
            return null; // todo implement
        }

        private class RevisionAnnotationAspect
                implements LineAnnotationAspect /*, EditorGutterAction */
        {
            public String getValue( int lineNumber )
            {
                String retval = "";
                if ( annotation.getNumberOfLines() > lineNumber && lineNumber >= 0 )
                {
                    retval = annotation.getRevision( lineNumber );
                }
                return retval;
            }

            public String getTooltipText( int lineNumber )
            {
                return null; // todo implement
            }

//    public Cursor getCursor(final int lineNum) {
//      return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
//    }
//
//    public void doAction(int lineNum) {
//      if (lineNum >= 0 && lineNum < myLineInfos.size()) {
//        final LineInfo info = myLineInfos.get(lineNum);
//        BzrFileRevision svnRevision = myRevisionMap.get(info.getRevision());
//        if (svnRevision != null) {
//          ShowAllSubmittedFilesAction.showSubmittedFiles(myVcs.getProject(), svnRevision, myFile);
//        }
//      }
//    }
        }
    }

}
