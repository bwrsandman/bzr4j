/*
 * Copyright (c) 2010 Emergent.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.emergent.bzr4j.core.xmloutput;

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
