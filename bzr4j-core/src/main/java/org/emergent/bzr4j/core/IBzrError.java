/**
 *
 */
package org.emergent.bzr4j.core;

import java.util.Map;

/**
 * Interface to represent a bazaar error
 *
 * TODO: this should be integrated in some way to the BazaarException
 *
 * @author Guillermo Gonzalez <guillo.gonzo AT gmail DOT com>
 *
 */
public interface IBzrError {

  public String getType();

  public Map<String, String> getDict();

  public String getMessage();

}
