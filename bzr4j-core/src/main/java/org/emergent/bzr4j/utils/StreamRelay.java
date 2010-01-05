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
package org.emergent.bzr4j.utils;

import org.emergent.bzr4j.debug.LogUtil;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Patrick Woodworth
 */
public class StreamRelay extends Thread
{
    private static final LogUtil LOG = LogUtil.getLogger( StreamRelay.class );

    private static final int BUFFER_SIZE = 2048;

    private InputStream m_is;

    private OutputStream m_os;

    public StreamRelay( String name, InputStream in, OutputStream out )
    {
        super( name );
        m_is = in;
        m_os = out;
        setDaemon( true );
    }

    public void run()
    {
        try
        {
            byte[] buf = new byte[BUFFER_SIZE];
            int count;
            while ( (count = m_is.read( buf, 0, BUFFER_SIZE )) >= 0 )
            {
                if (count == 0)
                {
                    Thread.yield();
                }
                else if ( m_os != null )
                {
                    m_os.write( buf, 0, count );
                    m_os.flush();
                }
            }
        }
        catch (Throwable e)
        {
            LOG.warn( "Exception in " + this.getName(), e );
        }
    }
}
