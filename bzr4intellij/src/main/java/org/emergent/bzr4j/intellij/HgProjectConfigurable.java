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

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.emergent.bzr4j.intellij.ui.HgConfigurationProjectPanel;

import javax.swing.Icon;
import javax.swing.JComponent;

public class HgProjectConfigurable implements Configurable {

  private final HgConfigurationProjectPanel hgConfigurationProjectPanel;

  public HgProjectConfigurable(HgProjectSettings projectSettings) {
    hgConfigurationProjectPanel = new HgConfigurationProjectPanel(projectSettings);
  }

  @Nls
  public String getDisplayName() {
    return HgVcsMessages.message("hg4idea.mercurial");
  }

  public Icon getIcon() {
    return HgVcs.MERCURIAL_ICON;
  }

  public String getHelpTopic() {
    return null;
  }

  public JComponent createComponent() {
    return hgConfigurationProjectPanel.getPanel();
  }

  public boolean isModified() {
    return hgConfigurationProjectPanel.isModified();
  }

  public void apply() throws ConfigurationException {
    hgConfigurationProjectPanel.saveSettings();
  }

  public void reset() {
    hgConfigurationProjectPanel.loadSettings();
  }

  public void disposeUIResources() {
  }

}
