/**
 *
 */
package org.emergent.bzr4j.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.core.IDelta;

/**
 * @author Guillermo Gonzalez <guillo.gonzo AT gmail DOT com>
 *
 */
public class Delta implements IDelta
{

    private final List<IBazaarLogMessage> logs;

    private List<IBazaarStatus> affectedFiles;

    public Delta( IBazaarLogMessage[] logs )
    {
        super();
        this.logs = Arrays.asList( logs );
    }

    public Delta( List<IBazaarLogMessage> logs )
    {
        super();
        this.logs = logs;
    }

    public List<IBazaarStatus> getAffectedFiles()
    {
        if ( affectedFiles == null )
        {
            Set<IBazaarStatus> all = new HashSet<IBazaarStatus>( logs.size() );
            for ( IBazaarLogMessage log : logs )
            {
                all.addAll( log.getAffectedFiles( true ) );
            }
            affectedFiles = new ArrayList<IBazaarStatus>( all.size() );
            affectedFiles.addAll( all );
        }
        return affectedFiles;
    }

    public List<IBazaarLogMessage> getLogs()
    {
        return logs;
    }

}
