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

package org.emergent.bzr4j.core.xmloutput;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * @author Aliaksei Vasileuski
 * Unit Test for bug 534959
 */
public class XmlTimestampTest {

  @Test
  public void testIgnored() throws Exception {
    String timestamp = "Fri 2009-03-27 18:41:19 +0000";
    try {
      // set default locale to non-US to reproduce bug even in USA.
      Locale.setDefault(Locale.GERMAN);
      Date tstamp = XmlOutputHandler.parseBzrTimeStamp(timestamp);
      assertNotNull(tstamp);
    } catch (ParseException e) {
      assertTrue(false, "XmlOutputHandler.parseBzrTimeStamp (SimpleDateFormat.parse) thrown ParseException");
    }
  }

}
