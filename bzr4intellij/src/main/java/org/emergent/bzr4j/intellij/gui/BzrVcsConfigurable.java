package org.emergent.bzr4j.intellij.gui;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.emergent.bzr4j.intellij.config.BzrVcsSettings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Patrik Beno
 */
public class BzrVcsConfigurable implements Configurable
{
    private final BzrVcsSettings settings;

    private BzrConfigurationPanel panel;

    private Project project;

    public BzrVcsConfigurable( Project project )
    {
        this.project = project;
        this.settings = BzrVcsSettings.getInstance( project );
    }

    @Nls
    public String getDisplayName()
    {
        return "Bazaar";
    }

    @Nullable
    public Icon getIcon()
    {
        return null;
    }

    @Nullable
    @NonNls
    public String getHelpTopic()
    {
        return null;
    }

    public JComponent createComponent()
    {
        panel = new BzrConfigurationPanel( project );
        panel.load( settings );
        return panel.getPanel();
    }

    public boolean isModified()
    {
        return panel.isModified( settings );
    }

    public void apply() throws ConfigurationException
    {
        panel.save( settings );
    }

    public void reset()
    {
        panel.load( settings );
    }

    public void disposeUIResources()
    {
        panel = null;
    }
}
