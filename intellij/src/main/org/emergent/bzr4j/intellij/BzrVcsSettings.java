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
package org.emergent.bzr4j.intellij;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.emergent.bzr4j.core.BazaarClientPreferences;
import org.emergent.bzr4j.core.BazaarPreference;

/**
 * @author Patrick Woodworth
 */
@State(name = "BzrApplicationSettings", storages = {
                @Storage(id = "BzrApplicationSettings", file = "$APP_CONFIG$/bzr4intellij.xml")})
public class BzrVcsSettings
        implements PersistentStateComponent<BzrVcsSettings>, ApplicationComponent
{
    private String m_executable = "bzr";

    private boolean m_annotationTrimmingEnabled = false;

    private boolean m_scanTargetOptimizationEnabled = false;

    private boolean m_extraLoggingEnabled = false;

    public static BzrVcsSettings getInstance()
    {
        return ServiceManager.getService( BzrVcsSettings.class );
    }

    public BzrVcsSettings getState()
    {
        return this;
    }

    public void loadState( BzrVcsSettings settings )
    {
        XmlSerializerUtil.copyBean( settings, this );
    }

    @NonNls
    @NotNull
    public String getComponentName()
    {
        return "Bazaar Configuration";
    }

    public void initComponent()
    {
    }

    public void disposeComponent()
    {
    }

    public String getExecutablePath()
    {
        return BazaarClientPreferences.getExecutablePath();
    }

    public String getBzrExecutable()
    {
        return m_executable;
    }

    public void setBzrExecutable( String bzrexe )
    {
        m_executable = bzrexe;
        BazaarClientPreferences.getInstance().set( BazaarPreference.EXECUTABLE, bzrexe );
    }

    public boolean isAnnotationTrimmingEnabled()
    {
        return m_annotationTrimmingEnabled;
    }

    public void setAnnotationTrimmingEnabled( boolean value )
    {
        m_annotationTrimmingEnabled = value;
    }

    public boolean isScanTargetOptimizationEnabled()
    {
        return m_scanTargetOptimizationEnabled;
    }

    public void setScanTargetOptimizationEnabled( boolean value )
    {
        m_scanTargetOptimizationEnabled = value;
    }

    public boolean isExtraLoggingEnabled()
    {
        return m_extraLoggingEnabled;
    }

    public void setExtraLoggingEnabled( boolean value )
    {
        m_extraLoggingEnabled = value;
    }
}
