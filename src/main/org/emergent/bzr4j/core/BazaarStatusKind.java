/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

/**
 * @author Adrian Wilkins
 * @author Patrick Woodworth
 */
public enum BazaarStatusKind
{

    NONE( Category.NONE, ' ', "none" ), VERSIONED( Category.VERSIONED, '+',
        "versioned" ), UNVERSIONED( Category.VERSIONED, '-', "unversioned" ), RENAMED(
        Category.VERSIONED, 'R', "renamed" ), UNKNOWN( Category.VERSIONED, '?',
        "unknown" ), HAS_CONFLICTS( Category.VERSIONED, 'C', "conflicts" ), PENDING_MERGE(
        Category.VERSIONED, 'P', "pending merge" ), IGNORED( Category.VERSIONED, 'I',
        "ignored" ), CREATED( Category.CONTENT, 'N', "created" ), DELETED( Category.CONTENT, 'D',
        "deleted" ), KIND_CHANGED( Category.CONTENT, 'K', "kind changed" ), MODIFIED(
        Category.CONTENT, 'M', "modified" ), UNCHANGED( Category.CONTENT, 'S',
        "unchanged" ), X_BIT_CHANGED( Category.EXECUTABLE, '*', "exe bit changed" );

    public enum Category
    {
        VERSIONED,
        CONTENT,
        EXECUTABLE,
        NONE
    }

    private final char flag;

    private final String name;

    private final Category category;

    private BazaarStatusKind( Category category, char flag, String name )
    {
        this.flag = flag;
        this.name = name;
        this.category = category;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public char toChar()
    {
        return flag;
    }

    public Category getCategory()
    {
        return category;
    }

    public static BazaarStatusKind fromString( String name )
    {
        for ( BazaarStatusKind item : BazaarStatusKind.values() )
        {
            if ( item.toString().equals( name ) )
                return item;
        }
        throw new EnumConstantNotPresentException( BazaarStatusKind.class, name );
    }

    public static BazaarStatusKind fromFlag( char flag )
    {
        for ( BazaarStatusKind item : BazaarStatusKind.values() )
        {
            if ( item.toChar() == flag )
                return item;
        }
        throw new EnumConstantNotPresentException( BazaarStatusKind.class, "flag: " + flag );
    }
}
