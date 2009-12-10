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
package org.emergent.bzr4j.intellij.utils;

import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;

import java.awt.*;

import org.emergent.bzr4j.intellij.BzrBundle;

/**
 * @author Patrick Woodworth
 */
public class BzrErrorReportSubmitter extends ErrorReportSubmitter
{
    @Override
    public String getReportActionText()
    {
        return BzrBundle.message("report.error.to.plugin.vendor");
    }

    @Override
    public SubmittedReportInfo submit( IdeaLoggingEvent[] events, Component parentComponent )
    {
        SubmittedReportInfo retval = new SubmittedReportInfo(null,
                "short text that UI interface pointing to the issue should have.",
                SubmittedReportInfo.SubmissionStatus.NEW_ISSUE);
        return retval;
    }
}
