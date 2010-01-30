/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.core.xmloutput;

import org.emergent.bzr4j.core.IBazaarPlugin;

/**
 * Implementation of {@link org.emergent.bzr4j.core.IBazaarPlugin} for the commandline adapter.<br>
 *
 * @author Guillermo Gonzalez <guillo.gonzo AT gmail DOT com>
 *
 */
class XmlBazaarPlugin implements IBazaarPlugin {

  private String description;

  private String name;

  private String path;

  private String[] version;

  /**
   * Full Constructor.<br>
   *
   * @param description
   * @param name
   * @param path
   * @param version
   */
  public XmlBazaarPlugin(String description, String name, String path, String version) {
    super();
    this.description = description;
    this.name = name;
    this.path = path;
    if (version != null)
      this.version = version.split("\\.");
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  public String[] getVersion() {
    return version;
  }

}
