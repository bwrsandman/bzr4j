/**
 *
 */
package org.emergent.bzr4j.core.commandline.parser;

import org.emergent.bzr4j.core.commandline.parser.CommandLinePlugin;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IPlugin;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

/**
 * I'm a parser for the output of: 'bzr plugins --xml' command. <br>
 *
 * @author Guillermo Gonzalez <guillo.gonzo AT gmail DOT com>
 *
 */
class XMLPluginParser extends XmlAbstractParser {

  private static final String PLUGINS = "plugins"; //$NON-NLS-1$

  private static final String PLUGIN = "plugin"; //$NON-NLS-1$

  private final static String NAME = "name";

  private final static String VERSION = "version";

  private final static String PATH = "path";

  private final static String DOC = "doc";

  private Set<IPlugin> plugins;

  public Set<IPlugin> parse(String xml) throws BazaarException {
    plugins = new HashSet<IPlugin>();
    try {
      XMLInputFactory factory = XMLInputFactory.newInstance();
      parser = factory.createXMLStreamReader(new StringReader(xml));
      parser.nextTag();
      parser.require(XMLStreamConstants.START_ELEMENT, null, PLUGINS);
      while (parser.nextTag() == XMLStreamConstants.START_ELEMENT) {
        parser.require(XMLStreamConstants.START_ELEMENT, null, PLUGIN);
        parser.nextTag();
        parser.require(XMLStreamConstants.START_ELEMENT, null, NAME);
        String name = parser.getElementText();
        parser.nextTag();
        parser.require(XMLStreamConstants.START_ELEMENT, null, VERSION);
        String version = parser.getElementText();
        parser.nextTag();
        parser.require(XMLStreamConstants.START_ELEMENT, null, PATH);
        String path = parser.getElementText();
        parser.nextTag();
        String doc = null;
        try {
          parser.require(XMLStreamConstants.START_ELEMENT, null, DOC);
          doc = parser.getElementText();
          parser.nextTag();
        }
        catch (XMLStreamException e) {}
        plugins.add(new CommandLinePlugin(doc != null ? doc.trim() : null,
            name != null ? name.trim() : null, path != null ? path.trim() : null,
            version != null ? version.trim() : null));
      }
//			parser.require(KXmlParser.END_TAG, null, PLUGINS);
    }
    catch (XMLStreamException e) {
      throw new BazaarException(e);
    }
    return plugins;
  }

}
