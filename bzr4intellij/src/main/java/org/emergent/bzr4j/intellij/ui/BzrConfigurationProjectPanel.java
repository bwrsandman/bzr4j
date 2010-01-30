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
package org.emergent.bzr4j.intellij.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.emergent.bzr4j.intellij.BzrProjectSettings;

import javax.swing.*;
import java.awt.*;

public class BzrConfigurationProjectPanel {

  private JPanel panel;

  private JCheckBox checkIncomingCbx;

  private JCheckBox checkOutgoingCbx;

  private final BzrProjectSettings projectSettings;

  public BzrConfigurationProjectPanel(BzrProjectSettings projectSettings) {
    this.projectSettings = projectSettings;
    loadSettings();
  }

  public boolean isModified() {
    return checkIncomingCbx.isSelected() != projectSettings.isCheckIncoming()
        || checkOutgoingCbx.isSelected() != projectSettings.isCheckOutgoing();
  }

  public void saveSettings() {
    projectSettings.setCheckIncoming(checkIncomingCbx.isSelected());
    projectSettings.setCheckOutgoing(checkOutgoingCbx.isSelected());
  }

  public void loadSettings() {
    checkIncomingCbx.setSelected(projectSettings.isCheckIncoming());
    checkOutgoingCbx.setSelected(projectSettings.isCheckOutgoing());
  }

  public JPanel getPanel() {
    return panel;
  }

  {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /** Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    panel = new JPanel();
    panel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    final Spacer spacer1 = new Spacer();
    panel.add(spacer1,
        new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1,
            GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
    panel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
        false));
    panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Changesets"));
    checkIncomingCbx = new JCheckBox();
    checkIncomingCbx.setSelected(true);
    checkIncomingCbx.setText("Check for incoming changesets");
    checkIncomingCbx.setMnemonic('I');
    checkIncomingCbx.setDisplayedMnemonicIndex(10);
    panel1.add(checkIncomingCbx,
        new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer2 = new Spacer();
    panel1.add(spacer2,
        new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1,
            GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    checkOutgoingCbx = new JCheckBox();
    checkOutgoingCbx.setSelected(true);
    checkOutgoingCbx.setText("Check for outgoing changesets");
    checkOutgoingCbx.setMnemonic('O');
    checkOutgoingCbx.setDisplayedMnemonicIndex(10);
    panel1.add(checkOutgoingCbx,
        new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
  }

  /** @noinspection ALL */
  public JComponent $$$getRootComponent$$$() {
    return panel;
  }
}