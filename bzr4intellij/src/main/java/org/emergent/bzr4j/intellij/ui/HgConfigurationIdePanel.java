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

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.lang.StringUtils;
import org.emergent.bzr4j.intellij.HgVcsMessages;
import org.emergent.bzr4j.intellij.HgGlobalSettings;
import org.emergent.bzr4j.intellij.command.HgVersionCommand;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

public class HgConfigurationIdePanel
{
    private JRadioButton autoRadioButton;

    private JRadioButton selectRadioButton;

    private TextFieldWithBrowseButton pathSelector;

    private JPanel basePanel;

    private final HgGlobalSettings globalSettings;

    public HgConfigurationIdePanel( HgGlobalSettings globalSettings )
    {
        this.globalSettings = globalSettings;
        loadSettings();

        String title = HgVcsMessages.message( "hg4idea.configuration.title" );
        String description = HgVcsMessages.message( "hg4idea.configuration.description" );

        pathSelector.addBrowseFolderListener(
                title, description, null,
                new FileChooserDescriptor( true, false, false, false, false, false )
        );

        final ActionListener listener = new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                pathSelector.setEnabled( selectRadioButton.isSelected() );
            }
        };

        selectRadioButton.addActionListener( listener );
        autoRadioButton.addActionListener( listener );
    }

    public boolean isModified()
    {
        if (selectRadioButton.isSelected())
        {
            return !pathSelector.getText().equals( globalSettings.getBzrExecutable() );
        }
        return autoRadioButton.isSelected() != globalSettings.isAutodetectBzr();
    }

    public JPanel getBasePanel()
    {
        return basePanel;
    }

    public void validate() throws ConfigurationException
    {
        HgVersionCommand command = new HgVersionCommand();
        if (!command.isValid( globalSettings.getBzrExecutable() ))
        {
            throw new ConfigurationException(
                    HgVcsMessages.message(
                            "hg4idea.configuration.executable.error",
                            globalSettings.getBzrExecutable()
                    )
            );
        }
    }

    public void saveSettings()
    {
        if (autoRadioButton.isSelected())
        {
            globalSettings.enableAutodetectBzr();
        }
        else
        {
            globalSettings.setBzrExecutable( pathSelector.getText() );
        }
    }

    public void loadSettings()
    {
        boolean isAutodetectHg = globalSettings.isAutodetectBzr();
        autoRadioButton.setSelected( isAutodetectHg );
        selectRadioButton.setSelected( !isAutodetectHg );
        pathSelector.setEnabled( !isAutodetectHg );
        if (isAutodetectHg)
        {
            pathSelector.setText( StringUtils.EMPTY );
        }
        else
        {
            pathSelector.setText( globalSettings.getBzrExecutable() );
        }
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
        basePanel = new JPanel();
        basePanel.setLayout( new GridLayoutManager( 2, 1, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        final JPanel panel1 = new JPanel();
        panel1.setLayout( new GridLayoutManager( 2, 2, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        basePanel.add( panel1,
                new GridConstraints( 0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null,
                        0, false ) );
        panel1.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                ResourceBundle.getBundle( "org/emergent/bzr4j/intellij/HgVcsMessages" ).getString(
                        "bzr4intellij.path_to_executable.text" ) ) );
        autoRadioButton = new JRadioButton();
        autoRadioButton.setSelected( true );
        this.$$$loadButtonText$$$( autoRadioButton,
                ResourceBundle.getBundle( "org/emergent/bzr4j/intellij/HgVcsMessages" ).getString(
                        "bzr4intellij.autodetect.text" ) );
        panel1.add( autoRadioButton,
                new GridConstraints( 0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false ) );
        selectRadioButton = new JRadioButton();
        selectRadioButton.setEnabled( true );
        selectRadioButton.setText( "Specify executable path" );
        selectRadioButton.setMnemonic( 'S' );
        selectRadioButton.setDisplayedMnemonicIndex( 0 );
        panel1.add( selectRadioButton,
                new GridConstraints( 1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false ) );
        pathSelector = new TextFieldWithBrowseButton();
        pathSelector.setEnabled( false );
        panel1.add( pathSelector,
                new GridConstraints( 1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        new Dimension( 100, -1 ), null, null, 0, false ) );
        final Spacer spacer1 = new Spacer();
        basePanel.add( spacer1,
                new GridConstraints( 1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1,
                        GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false ) );
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add( autoRadioButton );
        buttonGroup.add( selectRadioButton );
    }

    /** @noinspection ALL */
    private void $$$loadButtonText$$$( AbstractButton component, String text )
    {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++)
        {
            if (text.charAt( i ) == '&')
            {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt( i ) != '&')
                {
                    haveMnemonic = true;
                    mnemonic = text.charAt( i );
                    mnemonicIndex = result.length();
                }
            }
            result.append( text.charAt( i ) );
        }
        component.setText( result.toString() );
        if (haveMnemonic)
        {
            component.setMnemonic( mnemonic );
            component.setDisplayedMnemonicIndex( mnemonicIndex );
        }
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$()
    { return basePanel; }
}
