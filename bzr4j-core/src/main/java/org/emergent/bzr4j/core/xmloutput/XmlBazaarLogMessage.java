/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.core.xmloutput;

import org.emergent.bzr4j.core.BazaarRevision;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.core.utils.BazaarRuntimeException;
import org.emergent.bzr4j.core.utils.StringUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Guillermo Gonzalez
 *
 * TODO: need revision
 */
class XmlBazaarLogMessage implements IBazaarLogMessage {

  private final String timestamp;

  private final List<IBazaarStatus> files;

  private final String commiter;

  private final String author;

  private final String revno;

  private final String nick;

  private final String message;

  private final List<IBazaarLogMessage> merged;

  private final String revisionId;

  private final List<String> parents;

  /**
   *
   * @param revno
   * @param commiter
   * @param author
   * @param nick
   * @param timestamp
   * @param message
   * @param resourceStatus
   * @param merged
   * @param revisionId
   * @param parents
   */
  private XmlBazaarLogMessage(
      String revno,
      String commiter,
      String author,
      String nick,
      String timestamp,
      String message,
      List<IBazaarStatus> resourceStatus,
      List<IBazaarLogMessage> merged,
      String revisionId,
      List<String> parents) {
    this.revno = revno;
    this.commiter = commiter;
    this.nick = nick;
    this.timestamp = timestamp;
    this.message = message;
    this.files = Collections.unmodifiableList(
        resourceStatus == null ? Collections.EMPTY_LIST : resourceStatus);
    this.merged =
        Collections.unmodifiableList(merged == null ? Collections.EMPTY_LIST : merged);
    this.author = author;
    this.revisionId = revisionId;
    this.parents =
        Collections.unmodifiableList(parents == null ? Collections.EMPTY_LIST : parents);
  }

  public String getBranchNick() {
    return nick;
  }

  public String getCommiter() {
    return commiter;
  }

  public String getMessage() {
    return message;
  }

  public BazaarRevision getRevision() {
    return BazaarRevision.getRevision("revno:", revno);
  }

  public String getTimeStamp() {
    return timestamp;
  }

  public Date getDate() {
    try {
      // todo FIXME: investigate why the call to parse died if the timeStamp
      // conatins the day (three letters)
      // ¿maybe a locale issue?
      return StringUtil.parseLogDate(timestamp);
    } catch (ParseException e) {
      throw new BazaarRuntimeException(e);
    }
  }

  public List<IBazaarStatus> getAffectedFiles() {
    return files;
  }

  public List<IBazaarLogMessage> getMerged() {
    return merged;
  }

  public List<IBazaarStatus> getAffectedFiles(boolean includeMerges) {
    if (includeMerges) {
      List<IBazaarStatus> all = new ArrayList<IBazaarStatus>();
      all.addAll(files);
      for (IBazaarLogMessage log : getMerged()) {
        for (IBazaarStatus status : log.getAffectedFiles()) {
          if (!all.contains(status)) {
            all.add(status);
          }
        }
      }
      return all;
    } else {
      return getAffectedFiles();
    }
  }

  public String getAuthor() {
    if (author == null)
      return getCommiter();
    return author;
  }

  public List<String> getParents() {
    return parents;
  }

  public String getRevisionId() {
    return revisionId;
  }

  public int hasMerge() {
    final List<IBazaarLogMessage> merges = getMerged();
    if (merges != null) {
      return merges.size();
    } else {
      return 0;
    }
  }

  public static class Builder {

    private String m_revno;
    private String m_commiter;
    private String m_author;
    private String m_nick;
    private String m_timestamp;
    private String m_message;
    private List<IBazaarStatus> m_resourceStatus;
    private List<IBazaarLogMessage> m_merged;
    private String m_revisionId;
    private List<String> m_parents;

    public Builder setRevno(String revno) {
      m_revno = revno;
      return this;
    }

    public Builder setCommiter(String commiter) {
      m_commiter = commiter;
      return this;
    }

    public Builder setAuthor(String author) {
      m_author = author;
      return this;
    }

    public Builder setNick(String nick) {
      m_nick = nick;
      return this;
    }

    public Builder setTimestamp(String timestamp) {
      m_timestamp = timestamp;
      return this;
    }

    public Builder setMessage(String message) {
      m_message = message;
      return this;
    }

//    public Builder addResourceStatus(IBazaarStatus... resourceStatus) {
//      m_resourceStatus.addAll(Arrays.asList(resourceStatus));
//      return this;
//    }

    public Builder setResourceStatus(List<IBazaarStatus> resourceStatus) {
      m_resourceStatus = resourceStatus;
      return this;
    }

//    public Builder addMerged(IBazaarLogMessage... merged) {
//      m_merged.addAll(Arrays.asList(merged));
//      return this;
//    }

    public Builder setMerged(List<IBazaarLogMessage> merged) {
      m_merged = merged;
      return this;
    }

    public Builder setRevisionId(String revisionId) {
      m_revisionId = revisionId;
      return this;
    }

//    public Builder addParents(String... parents) {
//      m_parents.addAll(Arrays.asList(parents));
//      return this;
//    }

    public Builder setParents(List<String> parents) {
      m_parents = parents;
      return this;
    }

    public IBazaarLogMessage createBazaarLogMessage() {
      return new XmlBazaarLogMessage(m_revno, m_commiter, m_author, m_nick, m_timestamp, m_message, m_resourceStatus,
          m_merged, m_revisionId, m_parents);
    }
  }
}
