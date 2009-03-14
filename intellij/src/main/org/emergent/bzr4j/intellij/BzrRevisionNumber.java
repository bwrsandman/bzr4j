package org.emergent.bzr4j.intellij;

import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import org.emergent.bzr4j.core.BazaarRevision;
import org.emergent.bzr4j.utils.NaturalOrderComparator;

public final class BzrRevisionNumber implements VcsRevisionNumber
{
    private BazaarRevision m_rev;

    public BzrRevisionNumber( BazaarRevision rev )
    {
        m_rev = rev;
    }

    public BazaarRevision getBazaarRevision()
    {
        return m_rev;
    }

    public String asString()
    {
        return m_rev.toString();
    }

    public int compareTo( VcsRevisionNumber o )
    {
        return NaturalOrderComparator.compareObjects( asString(), o.asString() );
    }
}
