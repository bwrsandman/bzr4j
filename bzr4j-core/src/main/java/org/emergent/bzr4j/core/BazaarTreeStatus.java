package org.emergent.bzr4j.core;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Guillermo Gonzalez <guillo.gonzo@gmail.com>
 *
 */
public class BazaarTreeStatus {

  private final Set<IBazaarStatus> status;

  private final List<IBazaarLogMessage> pendingMerges;

  private IBazaarStatus[] statusArray;

  public BazaarTreeStatus(final Set<IBazaarStatus> status, final List<IBazaarLogMessage> pendingMerges) {
    super();
    this.status = status;
    this.pendingMerges = pendingMerges;
  }

  public BazaarTreeStatus() {
    this.status = Collections.EMPTY_SET;
    this.pendingMerges = Collections.EMPTY_LIST;
  }

  public Set<IBazaarStatus> getStatus() {
    return status;
  }

  public IBazaarStatus[] getStatusAsArray() {
    if (statusArray == null) {
      statusArray = getStatus().toArray(new IBazaarStatus[0]);
    }
    return statusArray;
  }

  public List<IBazaarLogMessage> getPendingMerges() {
    return pendingMerges;
  }

}
