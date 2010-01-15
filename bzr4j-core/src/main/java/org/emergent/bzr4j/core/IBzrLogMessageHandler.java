/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

import java.util.List;

/**
 * @author Guillermo Gonzalez <guillo.gonzo@gmail.com>
 *
 */
public interface IBzrLogMessageHandler {

  void handle(List<IBazaarLogMessage> logs);

}
