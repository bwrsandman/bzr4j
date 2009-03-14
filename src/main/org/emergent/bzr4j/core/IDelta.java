/**
 *
 */
package org.emergent.bzr4j.core;

import java.util.List;

/**
 * @author Guillermo Gonzalez <guillo.gonzo AT gmail DOT com>
 *
 */
public interface IDelta
{

    public List<IBazaarStatus> getAffectedFiles();

    public List<IBazaarLogMessage> getLogs();

}
