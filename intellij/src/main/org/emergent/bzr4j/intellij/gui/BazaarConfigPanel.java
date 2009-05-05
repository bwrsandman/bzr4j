/*
 * Copyright (c) 2009 Patrick Woodworth.
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
package org.emergent.bzr4j.intellij.gui;

import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;

import org.emergent.bzr4j.intellij.BzrVcsSettings;

/**
 * @author Patrick Woodworth
 */
public class BazaarConfigPanel
{
    private JButton m_browseButton;

    private JPanel m_panel;

    private JTextField m_exePathField;

    public BazaarConfigPanel( Project project )
    {

    }

    public JComponent getPanel()
    {
        return m_panel;
    }

    public boolean isModified( BzrVcsSettings settings )
    {
        return false;
    }

    public void load( BzrVcsSettings settings )
    {
    }

    public void save( BzrVcsSettings settings )
    {
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
    private void $$$setupUI$$$()
    {
        m_panel = new JPanel();
        m_panel.setLayout( new GridLayoutManager( 2, 3, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        m_browseButton = new JButton();
        m_browseButton.setText( "Browse" );
        m_panel.add( m_browseButton, new GridConstraints( 0, 2, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false ) );
        final Spacer spacer1 = new Spacer();
        m_panel.add( spacer1, new GridConstraints( 1, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null,
                null, 0, false ) );
        m_exePathField = new JTextField();
        m_panel.add( m_exePathField, new GridConstraints( 0, 1, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension( 150, -1 ), null, 0,
                false ) );
        final JLabel label1 = new JLabel();
        label1.setText( "Executable path" );
        m_panel.add( label1, new GridConstraints( 0, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false ) );
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$()
    { return m_panel; }
}
