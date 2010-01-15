package org.emergent.bzr4j.core;

public final class BazaarRevisionRange implements IBazaarRevisionSpec {

  private BazaarRevision start;

  private BazaarRevision end;

  public static BazaarRevisionRange getRange(BazaarRevision start, BazaarRevision end) {
    return new BazaarRevisionRange(start, end);
  }

  protected BazaarRevisionRange() {
  }

  private BazaarRevisionRange(BazaarRevision start, BazaarRevision end) {
    this.start = start;
    this.end = end;
  }

  public BazaarRevision getStart() {
    return start;
  }

  public BazaarRevision getEnd() {
    return end;
  }

  @Override
  public String toString() {
    return String.format("%1$s..%2$s",
        start == null ? "" : start.toString(),
        end == null ? "" : end.toString());
  }

}
