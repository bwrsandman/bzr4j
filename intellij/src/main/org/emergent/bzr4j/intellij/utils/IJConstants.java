package org.emergent.bzr4j.intellij.utils;

import org.emergent.bzr4j.utils.BzrConstants;

/**
 * @author Patrick Woodworth
 */
public class IJConstants
{
    public static final boolean FOO_ACTION_ENABLED = 
            Boolean.getBoolean( "bzr4j.foo_action_enabled" );

    public static final boolean ENABLE_STATUS_TARGET_OPTIMIZATION =
            BzrConstants.getBoolean( "bzr4j.intellij.enable_status_target_optimization", false );
}
