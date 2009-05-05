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
package org.emergent.bzr4j.intellij.action;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import org.emergent.bzr4j.intellij.BzrVcs;
import org.emergent.bzr4j.intellij.providers.BzrChangeProvider;
import org.emergent.bzr4j.utils.LogUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Patrick Woodworth
 */
public class ToggleScanningAction extends AnAction
{
    private static final LogUtil sm_logger = LogUtil.getLogger( ToggleScanningAction.class );

    public void actionPerformed( AnActionEvent event )
    {
        if ( sm_logger.isDebugEnabled() )
        {
            sm_logger.debug( "enter: actionPerformed(id='" + ActionManager.getInstance().getId( this ) + "')" );
        }
        final DataContext dataContext = event.getDataContext();
        final Project project = PlatformDataKeys.PROJECT.getData( dataContext );

        final BzrVcs vcs = BzrVcs.getInstance( project );
        BzrChangeProvider changeProvider = vcs.getChangeProvider();
        boolean newVal = !changeProvider.isScanningEnabled();
        changeProvider.setScanningEnabled( newVal );
        fireNotification( project, "Bzr scanning " + (newVal ? "enabled!" : "disabled!") );
    }

    public void update( AnActionEvent e )
    {
        super.update( e );

        Presentation presentation = e.getPresentation();
        final DataContext dataContext = e.getDataContext();

        Project project = PlatformDataKeys.PROJECT.getData( dataContext );
        if ( project == null )
        {
            presentation.setEnabled( false );
            presentation.setVisible( false );
            return;
        }

        presentation.setEnabled( true );
        presentation.setVisible( true );
    }

    private void fireNotification( Project project, String message )
    {
        final WindowManager windowManager = WindowManager.getInstance();
        if (windowManager != null)
        {
            final StatusBar statusBar = windowManager.getStatusBar( project );
            if (statusBar != null)
            {
                statusBar.fireNotificationPopup( new JLabel(message), Color.LIGHT_GRAY );
            }
        }

    }
}
