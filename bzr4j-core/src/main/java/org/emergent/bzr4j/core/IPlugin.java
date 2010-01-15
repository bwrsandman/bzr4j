/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

/**
 * This represents a bazaar plugin. <br>
 * It contains all the metadata provided by bzr about it plugins<br>
 *
 * @author Guillermo Gonzalez <guillo.gonzo AT gmail DOT com>
 *
 */
public interface IPlugin {

  String getName();

  String[] getVersion();

  String getDescription();

  String getPath();

}
