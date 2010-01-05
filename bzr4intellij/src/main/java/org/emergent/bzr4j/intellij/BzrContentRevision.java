package org.emergent.bzr4j.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.emergent.bzr4j.intellij.command.BzrCatCommand;
import org.jetbrains.annotations.NotNull;

/**
 * @author Patrick Woodworth
 */
public class BzrContentRevision implements ContentRevision {

  private final Project project;

  private final BzrFile hgFile;

  private final BzrRevisionNumber revisionNumber;

  private FilePath filePath;
  private String content;

  public BzrContentRevision(Project project, FilePath file, BzrRevisionNumber revision) {
    this.project = project;
    VirtualFile vcsRoot = VcsUtil.getVcsRootFor(project, filePath);
    assert vcsRoot != null;
    hgFile = new BzrFile(vcsRoot, file);
    revisionNumber = revision;
  }

  public BzrContentRevision(Project project, BzrFile file, @NotNull BzrRevisionNumber revision) {
    this.project = project;
    hgFile = file;
    revisionNumber = revision;
  }

//    @Nullable
//    public String getContent() throws VcsException {
//        if (content != null) {
//            return content;
//        }
//
//        try {
//            File ioFile = new File(m_file.getPath());
//            IJUtil.createBzrClient().setWorkDir(IJUtil.root(ioFile));
//            InputStream stream = IJUtil.createBzrClient().cat(ioFile, m_revision.getBazaarRevision());
//            content = new String(FileUtil.adaptiveLoadBytes(stream));
//            return content;
//        }
//        catch (BazaarException e) {
//            throw IJUtil.notYetHandled(e);
//        }
//        catch (IOException e) {
//            throw IJUtil.notYetHandled(e);
//        }
//
//    }

  public String getContent() throws VcsException {
    if (StringUtils.isBlank(content)) {
      content = new BzrCatCommand(project).execute(hgFile, revisionNumber, getFile().getCharset());
    }
    return content;
  }

  @NotNull
  public FilePath getFile() {
    if (filePath == null) {
      filePath = hgFile.toFilePath();
    }
    return filePath;
  }

  @NotNull
  public BzrRevisionNumber getRevisionNumber() {
    return revisionNumber;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(hgFile)
        .append(revisionNumber)
        .toHashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }
    if (!(object instanceof BzrContentRevision)) {
      return false;
    }
    BzrContentRevision that = (BzrContentRevision)object;
    return new EqualsBuilder()
        .append(hgFile, that.hgFile)
        .append(revisionNumber, that.revisionNumber)
        .isEquals();
  }
}
