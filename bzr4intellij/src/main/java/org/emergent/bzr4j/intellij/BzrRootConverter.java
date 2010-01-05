/*
 * Copyright (c) 2009 Emergent.org
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
package org.emergent.bzr4j.intellij;

import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.HashSet;

import java.util.ArrayList;
import java.util.List;

/**
 * The converter for the VCS roots
 * @author Patrick Woodworth
 */
public class BzrRootConverter implements AbstractVcs.RootsConvertor {

  /** The static instance */
  public static final BzrRootConverter INSTANCE = new BzrRootConverter();

  /** {@inheritDoc} */
  public List<VirtualFile> convertRoots(List<VirtualFile> result) {
    ArrayList<VirtualFile> roots = new ArrayList<VirtualFile>();
    HashSet<VirtualFile> listed = new HashSet<VirtualFile>();
    for (VirtualFile f : result) {
      VirtualFile r = BzrUtil.bzrRootOrNull(f);
      BzrVcs.LOG.debug(String.format("RootConversion: \"%s\" => \"%s\"", f, r));
      if (r != null && listed.add(r)) {
        roots.add(r);
      }
    }
    return roots;
  }
}
