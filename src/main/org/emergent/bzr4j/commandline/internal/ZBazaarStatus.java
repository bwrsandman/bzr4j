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
package org.emergent.bzr4j.commandline.internal;

import org.emergent.bzr4j.core.BazaarStatus;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.IBazaarStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Woodworth
 */
public class ZBazaarStatus extends BazaarStatus
{
    public ZBazaarStatus( List<BazaarStatusKind> statuses, File path, File branchRoot )
    {
        super( statuses, path, branchRoot );
    }

    public static IBazaarStatus parseLine( File rootFile, final String line )
    {
        if ( line.length() < 5 ) return null;
        if ( line.startsWith( "working tree is out of date" ) ) return null;

        char mod1 = line.charAt( 0 );
        char mod2 = line.charAt( 1 );
        char mod3 = line.charAt( 2 );

        String path = line.substring( 3 ).trim();
        String oldpath = null;

        if ( mod1 == 'R' )
        {
            int ptrIdx = path.indexOf( "=>" );
            oldpath = path.substring( 0, ptrIdx ).trim();
            path = path.substring( ptrIdx + "=>".length() ).trim();
        }

        File pathFile = new File( rootFile, path );

        ArrayList<BazaarStatusKind> kinds = new ArrayList<BazaarStatusKind>();
        if (!Character.isWhitespace( mod1 ))
            kinds.add( BazaarStatusKind.fromFlag( mod1 ) );
        if (!Character.isWhitespace( mod2 ))
            kinds.add( BazaarStatusKind.fromFlag( mod2 ) );
        if (!Character.isWhitespace( mod3 ))
            kinds.add( BazaarStatusKind.fromFlag( mod3 ) );

        ZBazaarStatus retval = new ZBazaarStatus( kinds, pathFile, rootFile );

        retval.m_previousFile = new File( rootFile, oldpath );

        return retval;
    }
}
