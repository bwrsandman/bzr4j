/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.core.commandline.parser;

import org.emergent.bzr4j.core.IBazaarInfo;

import java.util.Date;

/**
 * @author Guillermo Gonzalez
 *
 */
class CommandLineInfo implements IBazaarInfo {

  private final String layout;

  private final String[] formats;

  private final String branchFormat;

  private final String controlFormat;

  private final String repositoryFormat;

  private final String workingTreeFormat;

  private final CmdLineLocations locations;

  private final CmdLineRelatedBranches relatedBranches;

  private final CmdLineWorkingTreeStats workingTreeStats;

  private final CmdLineBranchHistory branchHistory;

  private final CmdLineRepositoryStats repositoryStats;

  public CommandLineInfo(final String layout, final String[] formats, final String branchFormat,
      final String controlFormat, final String repositoryFormat,
      final String workingTreeFormat,
      final CmdLineLocations locations, final CmdLineRelatedBranches relatedBranches,
      final CmdLineWorkingTreeStats workingTreeStats,
      final CmdLineBranchHistory branchHistory,
      final CmdLineRepositoryStats repositoryStats) {
    super();
    this.layout = layout;
    this.formats = formats;
    this.branchFormat = branchFormat;
    this.controlFormat = controlFormat;
    this.repositoryFormat = repositoryFormat;
    this.workingTreeFormat = workingTreeFormat;
    this.locations = locations;
    this.relatedBranches = relatedBranches;
    this.workingTreeStats = workingTreeStats;
    this.branchHistory = branchHistory;
    this.repositoryStats = repositoryStats;
  }

  public BranchHistory getBranchHistory() {
    return branchHistory;
  }

  public String[] getFormats() {
    return formats;
  }

  public String getBranchFormat() {
    return branchFormat;
  }

  public String getControlFormat() {
    return controlFormat;
  }

  public String getRepositoryFormat() {
    return repositoryFormat;
  }

  public String getWorkingTreeFormat() {
    return workingTreeFormat;
  }

  public String getLayout() {
    return layout;
  }

  public Locations getLocations() {
    return locations;
  }

  public RelatedBranches getRelatedBranches() {
    return relatedBranches;
  }

  public RepositoryStats getRepositoryStats() {
    return repositoryStats;
  }

  public WorkingTreeStats getWorkingTreeStats() {
    return workingTreeStats;
  }

  public static class CmdLineRelatedBranches implements RelatedBranches {

    private final String publicBranch;

    private final String pushBranch;

    private final String parentBranch;

    private final String submitBranch;

    public CmdLineRelatedBranches(String publicBranch, String pushBranch,
        String parentBranch, String submitBranch) {
      this.publicBranch = publicBranch;
      this.pushBranch = pushBranch;
      this.parentBranch = parentBranch;
      this.submitBranch = submitBranch;
    }

    public String getParentBranch() {
      return parentBranch;
    }

    public String getPublicBranch() {
      return publicBranch;
    }

    public String getPushBranch() {
      return pushBranch;
    }

    public String getSubmitBranch() {
      return submitBranch;
    }
  }

  public static class CmdLineWorkingTreeStats implements WorkingTreeStats {

    private final Integer added;

    private final Integer ignored;

    private final Integer modified;

    private final Integer removed;

    private final Integer renamed;

    private final Integer unchanged;

    private final Integer unknown;

    private final Integer versionedSubdirectories;

    public CmdLineWorkingTreeStats(Integer added, Integer ignored,
        Integer modified, Integer removed, Integer renamed,
        Integer unchanged, Integer unknown,
        Integer versionedSubdirectories) {
      this.added = added;
      this.ignored = ignored;
      this.modified = modified;
      this.removed = removed;
      this.renamed = renamed;
      this.unchanged = unchanged;
      this.unknown = unknown;
      this.versionedSubdirectories = versionedSubdirectories;
    }

    public Integer getAdded() {
      return added;
    }

    public Integer getIgnored() {
      return ignored;
    }

    public Integer getModified() {
      return modified;
    }

    public Integer getRemoved() {
      return removed;
    }

    public Integer getRenamed() {
      return renamed;
    }

    public Integer getUnchanged() {
      return unchanged;
    }

    public Integer getUnknown() {
      return unknown;
    }

    public Integer getVersionedSubdirectories() {
      return versionedSubdirectories;
    }
  }

  public static class CmdLineBranchHistory implements BranchHistory {

    private final Integer branchRevisions;

    private final Integer committers;

    private final Integer daysOld;

    private final Date firstRevisionDate;

    private final Date latestRevisionDate;

    public CmdLineBranchHistory(Integer branchRevisions,
        Integer committers, Integer daysOld, Date firstRevisionDate,
        Date latestRevisionDate) {
      super();
      this.branchRevisions = branchRevisions;
      this.committers = committers;
      this.daysOld = daysOld;
      this.firstRevisionDate = firstRevisionDate;
      this.latestRevisionDate = latestRevisionDate;
    }

    public Integer getRevisions() {
      return branchRevisions;
    }

    public Integer getCommitters() {
      return committers;
    }

    public Date getFirstRevisionDate() {
      return firstRevisionDate;
    }

    public Date getLatestRevisionDate() {
      return latestRevisionDate;
    }

    public Integer getDaysOld() {
      return daysOld;
    }
  }

  public static class CmdLineRepositoryStats implements RepositoryStats {

    private final Integer repositoryRevisions;

    private final Long repositorySize;

    public CmdLineRepositoryStats(Integer repositoryRevisions,
        Long repositorySize) {
      this.repositoryRevisions = repositoryRevisions;
      this.repositorySize = repositorySize;
    }

    public Integer getRevisionCount() {
      return repositoryRevisions;
    }

    public Long getSize() {
      return repositorySize;
    }

  }

  public static class CmdLineLocations implements Locations {

    private final String lightCheckoutRoot;

    private final String repositoryCheckoutRoot;

    private final String checkoutRoot;

    private final String checkoutOfBranch;

    private final String sharedRepository;

    private final String repository;

    private final String repositoryBranch;

    private final String branchRoot;

    private final String boundToBranch;

    public CmdLineLocations(String lightCheckoutRoot,
        String repositoryCheckoutRoot, String checkoutRoot,
        String checkoutOfBranch, String sharedRepository,
        String repository, String repositoryBranch, String branchRoot,
        String boundToBranch) {
      super();
      this.lightCheckoutRoot = lightCheckoutRoot;
      this.repositoryCheckoutRoot = repositoryCheckoutRoot;
      this.checkoutRoot = checkoutRoot;
      this.checkoutOfBranch = checkoutOfBranch;
      this.sharedRepository = sharedRepository;
      this.repository = repository;
      this.repositoryBranch = repositoryBranch;
      this.branchRoot = branchRoot;
      this.boundToBranch = boundToBranch;
    }

    public String getLightCheckoutRoot() {
      return lightCheckoutRoot;
    }

    public String getRepositoryCheckoutRoot() {
      return repositoryCheckoutRoot;
    }

    public String getCheckoutRoot() {
      return checkoutRoot;
    }

    public String getCheckoutOfBranch() {
      return checkoutOfBranch;
    }

    public String getSharedRepository() {
      return sharedRepository;
    }

    public String getRepository() {
      return repository;
    }

    public String getRepositoryBranch() {
      return repositoryBranch;
    }

    public String getBranchRoot() {
      return branchRoot;
    }

    public String getBoundToBranch() {
      return boundToBranch;
    }
  }
}
