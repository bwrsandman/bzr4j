/*
 * Copyright 2010 Tripwire, Inc. All Rights Reserved.
 */
package org.emergent.bzr4j.core.commandline.parser;

import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.IBazaarStatus;

import java.util.List;
import java.util.Set;

/**
 * @author Patrick Woodworth
 */
public interface XmlStatusResult {

  Set<IBazaarStatus> getStatusSet();

  List<IBazaarLogMessage> getPendingMerges();
}
