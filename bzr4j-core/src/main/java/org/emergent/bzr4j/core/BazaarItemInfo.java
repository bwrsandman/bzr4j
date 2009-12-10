package org.emergent.bzr4j.core;

public class BazaarItemInfo implements IBazaarItemInfo
{

    private String id;

    private String path;

    private BazaarItemKind kind;

    private BazaarStatusKind statusKind;

    public BazaarItemInfo( BazaarItemKind kind, String id, String path )
    {
        this.kind = kind;
        this.id = id;
        this.path = path;
    }

    public BazaarItemInfo( BazaarItemKind kind, String id, String path, BazaarStatusKind type )
    {
        this( kind, id, path );
        this.statusKind = type;
    }

    public String getId()
    {
        return id;
    }

    public BazaarItemKind getKind()
    {
        return kind;
    }

    public String getPath()
    {
        return path;
    }

    public BazaarStatusKind getStatusKind()
    {
        return statusKind;
    }

}
