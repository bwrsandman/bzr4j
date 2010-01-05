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

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.emergent.bzr4j.intellij.ui.BzrConfigurationIdePanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BzrIdeConfigurable implements ApplicationComponent, Configurable {

  private final BzrConfigurationIdePanel panel;

  public BzrIdeConfigurable(BzrGlobalSettings globalSettings) {
    panel = new BzrConfigurationIdePanel(globalSettings);
  }

  @Nls
  public String getDisplayName() {
    Object[] params = new Object[] { };
    return BzrVcsMessages.message("bzr4intellij.bazaar", params);
  }

  public Icon getIcon() {
    return BzrVcs.BAZAAR_ICON;
  }

  public String getHelpTopic() {
    return null;
  }

  public JComponent createComponent() {
    return panel.getBasePanel();
  }

  public boolean isModified() {
    return panel.isModified();
  }

  public void apply() throws ConfigurationException {
    panel.validate();
    panel.saveSettings();
  }

  public void reset() {
    panel.loadSettings();
  }

  public void disposeUIResources() {
  }

  @NotNull
  public String getComponentName() {
    return getClass().getName();
  }

  public void initComponent() {
  }

  public void disposeComponent() {
  }
}
