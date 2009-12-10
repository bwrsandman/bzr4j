// Copyright 2009 Victor Iacoban
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software distributed under
// the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific language governing permissions and
// limitations under the License.
package org.emergent.bzr4j.intellij;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.emergent.bzr4j.core.BazaarClientPreferences;
import org.emergent.bzr4j.core.BazaarPreference;

@State(
  name = "bzr4intellij.settings",
  storages = @Storage(id = "bzr4intellij.settings", file = "$OPTIONS$/bzr4intellij.xml")
)
public class HgGlobalSettings implements PersistentStateComponent<HgGlobalSettings> {

  private static final String DEFAULT_EXECUTABLE = "bzr";
  private static final int FIVE_MINUTES = 300;

  private String m_bzrExecutable = DEFAULT_EXECUTABLE;

  public String getBzrExecutable() {
    return m_bzrExecutable;
  }

  public void setBzrExecutable(String bzrExecutable ) {
    m_bzrExecutable = bzrExecutable;
    BazaarClientPreferences.getInstance().set( BazaarPreference.EXECUTABLE, bzrExecutable );
  }

  public boolean isAutodetectBzr() {
    return DEFAULT_EXECUTABLE.equals( m_bzrExecutable );
  }

  public void enableAutodetectBzr() {
    setBzrExecutable( DEFAULT_EXECUTABLE );
  }

  public int getIncomingCheckIntervalSeconds() {
    return FIVE_MINUTES;
  }

  public HgGlobalSettings getState() {
    return this;
  }

  public void loadState(HgGlobalSettings state) {
    m_bzrExecutable = state.m_bzrExecutable;
  }

}
