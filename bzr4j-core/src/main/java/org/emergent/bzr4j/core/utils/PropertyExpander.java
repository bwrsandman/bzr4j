/*
 * Copyright (c) 2010 Emergent.org
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

package org.emergent.bzr4j.core.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Patrick Woodworth
 */
public class PropertyExpander {

  public static String START_TOKEN = "${";
  public static char END_TOKEN = '}';

  public static String expandProperty(String s) {
    return expandProperty(s, System.getProperties());
  }

  public static Properties expandInto(InputStream is, Properties props) throws IOException {
    Properties tmpProps = new Properties();
    tmpProps.load(is);
    Set keys = tmpProps.keySet();
    props.putAll(tmpProps);
    boolean modified = false;
    do {
      modified = false;
      for (Iterator iter = keys.iterator(); iter.hasNext();) {
        String key = (String)iter.next();
        String val = props.getProperty(key);
        String newval = expandProperty(val, props);
        if (!val.equals(newval)) {
          props.setProperty(key, newval);
          modified = true;
        }
      }
    } while (modified);
    return props;
  }

  public static Properties expandInto(File file, Properties props) throws IOException {
    InputStream is = null;
    try {
      is = new BufferedInputStream(new FileInputStream(file));
      return expandInto(is, props);
    } finally {
      if (is != null) try { is.close(); } catch (Exception e) { }
    }
  }

  /**
   * Static method to help expand specified property.
   */
  public static String expandProperty(String s, Map resolver) {
    /**
     * We are done if null.
     */
    if (s == null)
      return null;

    /**
     * Does the property have anything to expand?
     */
    int cur = s.indexOf(START_TOKEN, 0);
    if (cur == -1)
      return s;

    /**
     * Lets get to work...
     */
    StringBuffer stringbuffer = new StringBuffer(s.length());
    int end = s.length();
    int beg = 0;
    while (cur < end) {
      if (cur > beg) {
        stringbuffer.append(s.substring(beg, cur));
        beg = cur;
      }

      /**
       * Determine name to expand
       */
      int i;
      for (i = cur + START_TOKEN.length()
          ; i < end && s.charAt(i) != END_TOKEN
          ; i++
          )
        ;

      /**
       * Garbage or intent? If we are at the end, return the rest of
       * the string as is.
       */
      if (i == end) {
        stringbuffer.append(s.substring(cur, i));
        break;
      }

      /**
       * Extract name
       */
      String propToExpand = s.substring(cur + 2, i);

      /**
       * Lookup name
       */
      if (propToExpand.equals("/")) {
        /**
         * Special case for path separator.
         */
        stringbuffer.append(java.io.File.separatorChar);
      } else {
        /**
         * Find property in System env.
         */
        String expandValue = (String)resolver.get(propToExpand);
        if (expandValue == null) {
          stringbuffer.append(START_TOKEN);
          stringbuffer.append(propToExpand);
          stringbuffer.append(END_TOKEN);
        } else
          stringbuffer.append(expandValue);
      }

      /**
       * Anything else to expand?
       */
      beg = i + 1;
      cur = s.indexOf("${", beg);
      if (cur == -1) {
        if (beg < end)
          stringbuffer.append(s.substring(beg, end));
        // DONE!!
        break;
      }
    }

    /**
     * Return expanded property
     */
    return stringbuffer.toString();
  }

  public static void dumpProps(Properties props) {
    TreeMap mymap = new TreeMap();
    mymap.putAll(props);
    System.out.println("---- properties:begin ---- ");
    for (Iterator i = mymap.keySet().iterator(); i.hasNext();) {
      String key = (String)i.next();
      String val = (String)mymap.get(key);
      System.out.println(key + " : " + val);
    }
    System.out.println("---- properties:end ---- ");

  }
}
