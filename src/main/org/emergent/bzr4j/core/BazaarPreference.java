package org.emergent.bzr4j.core;

public enum BazaarPreference
{

    EXECUTABLE( "bzr.executable" ),
    BZR_EMAIL( "bzr.email" ),
    BZR_PLUGIN_PATH( "bzr.pluginPath" ),
    BZR_HOME( "bzr.home" ),
    BZR_PROGRESS_BAR( "bzr.progressBar" ),
    BZR_SSH( "bzr.ssh" ),
    BZR_REMOTE_PATH( "bzr.remotePath" ),
    BZR_EDITOR( "bzr.editor" ),
    BZR_HOSTKEYS( "bzr.hostkeys" ),
    BZR_XMLRPC_PORT( "bzr.xmlRpcPort" ),
    TEST_USER_NAME( "bzr.user.name" ),
    TEST_USER_MAIL( "bzr.user.mail" ),
    TEST_CLIENT_TYPE( "bzr.clientType" );

    private final String value;

    private BazaarPreference( final String value )
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return value;
    }

    public String getValue()
    {
        return value;
    }
}
