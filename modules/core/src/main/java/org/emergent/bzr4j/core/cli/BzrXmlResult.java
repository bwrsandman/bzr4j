/*
 * Copyright (c) 2010 Patrick Woodworth
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.emergent.bzr4j.core.cli;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Patrick Woodworth
 */
public class BzrXmlResult extends BzrAbstractResult {

  private static final SAXParserFactory sm_saxParserFactory = SAXParserFactory.newInstance();

  static {
    try {
      sm_saxParserFactory.setFeature("http://apache.org/xml/features/allow-java-encodings",true);
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXNotRecognizedException e) {
      e.printStackTrace();
    } catch (SAXNotSupportedException e) {
      e.printStackTrace();
    }
  }

  private final DefaultHandler m_handler;

  public BzrXmlResult(DefaultHandler handler) {
    m_handler = handler;
  }

  public static BzrXmlResult createBzrXmlResult(DefaultHandler handler) {
    return new BzrXmlResult(handler);
  }

  Thread startOutRelay(final InputStream is) {
    Thread readingThread = new Thread(new Runnable() {
      public void run() {
        try {
          SAXParser parser = sm_saxParserFactory.newSAXParser();
          parser.parse(new BufferedInputStream(is), m_handler);
        } catch (ParserConfigurationException e) {
          logRelayException(e);
        } catch (SAXException e) {
          logRelayException(e);
        } catch (IOException e) {
          logRelayException(e);
        } finally {
          if (is != null) try { is.close(); } catch (Exception ignored) { }
        }
      }
    });
    readingThread.start();
    return readingThread;
  }
}
