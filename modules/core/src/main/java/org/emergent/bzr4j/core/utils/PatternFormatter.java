/**
 * Copyright [2007] [Rohit B. Rai]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.emergent.bzr4j.core.utils;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/**
 * TODO PatternFormatter class is not used.
 * @author Rohit B. Rai
 * @author Patrick Woodworth
 */
public class PatternFormatter extends Formatter {

  java.util.logging.SimpleFormatter sh;

  /**
   *


   *        The Log Formatter will use the following formatting tokens
   *
   *          LoggerName %LOGGER%
   *          Level %LEVEL%
   *          Time %TIME%
   *          Message %MESSAGE%
   *          SourceClassName %SOURCECLASS%
   *          SourceMethodName %SOURCEMETHOD%
   *          Exception Message %EXCEPTION%
   *          ExceptionStackTrace %STACKTRACE%
   *


   *
   * The default log format is "[%LOGGER% - %LEVEL%] %TIME%: %MESSAGE%"
   * And exception format is [%LOGGER% - %LEVEL%] %TIME% %MESSAGE% \n
   * Exception: %EXCEPTION% \n %STACKTRACE% Apart from this the time
   * format may be specified in satand java time format in the timeFormat
   * variable The default time format is "dd-MMM-yyy; HH:mm:ss"
   */

  private String logPattern;

  private String exceptionPattern;

  private String timeFormat;

  private MessageFormat logMessageFormat;

  private MessageFormat exceptionMessageFormat;

  private DateFormat dateFormat;

  public PatternFormatter() {
    LogManager manager = LogManager.getLogManager();
    String cname = getClass().getName();

    timeFormat = manager.getProperty(cname + ".timeFormat");

    if (timeFormat == null) {
      timeFormat = "dd-MMM-yyy; HH:mm:ss";
    }
    setTimeFormat(timeFormat);

    logPattern = manager.getProperty(cname + ".logPattern");
    if (logPattern == null) {
      logPattern = "[{0} - {1}] {2}: {3} \n";
    }
    setLogPattern(logPattern);

    exceptionPattern = manager.getProperty(cname + ".exceptionPattern");
    if (exceptionPattern == null) {
      exceptionPattern = "[{0} - {1}] {2} {3} \nException in {4}: {6} \n{7} ";
    }
    setExceptionPattern(exceptionPattern);

    logMessageFormat = new MessageFormat(logPattern);
    exceptionMessageFormat = new MessageFormat(exceptionPattern);

    dateFormat = new SimpleDateFormat(timeFormat); // Locale.US seems to be not necessary here
  }

  public void setTimeFormat(String timeFormat) {
    this.timeFormat = timeFormat;
    dateFormat = new SimpleDateFormat(timeFormat); // Locale.US seems to be not necessary here
  }

  public void setLogPattern(String logFormat) {
    logFormat = logFormat.replace("%LOGGER%", "{0}");
    logFormat = logFormat.replace("%LEVEL%", "{1}");
    logFormat = logFormat.replace("%TIME%", "{2}");
    logFormat = logFormat.replace("%MESSAGE%", "{3}");
    logFormat = logFormat.replace("%SOURCECLASS%", "{4}");
    logFormat = logFormat.replace("%SOURCEMETHOD%", "{5}");

    this.logPattern = logFormat;

    logMessageFormat = new MessageFormat(logPattern);
  }

  public void setExceptionPattern(String exceptionFormat) {
    exceptionFormat = exceptionFormat.replace("%LOGGER%", "{0}");
    exceptionFormat = exceptionFormat.replace("%LEVEL%", "{1}");
    exceptionFormat = exceptionFormat.replace("%TIME%", "{2}");
    exceptionFormat = exceptionFormat.replace("%MESSAGE%", "{3}");
    exceptionFormat = exceptionFormat.replace("%SOURCECLASS%", "{4}");
    exceptionFormat = exceptionFormat.replace("%SOURCEMETHOD%", "{5}");
    exceptionFormat = exceptionFormat.replace("%EXCEPTION%", "{6}");
    exceptionFormat = exceptionFormat.replace("%STACKTRACE%", "{7}");

    this.exceptionPattern = exceptionFormat;

    exceptionMessageFormat = new MessageFormat(logPattern);
  }

  @Override
  public String format(LogRecord record) {
    Date time = new Date(record.getMillis());
    String formattedTime = dateFormat.format(time);

    String logMessage = "";

    if (record.getThrown() == null) {
      Object[] log =
          { record.getLoggerName(), String.format("%7.7s", record.getLevel()), formattedTime, record.getMessage(),
              record.getSourceClassName(), record.getSourceMethodName() };

      logMessage = logMessageFormat.format(log);
    } else {
      String stack = getStackLayout(record.getThrown(), "");

      Object[] log =
          { record.getLoggerName(), String.format("%7.7s", record.getLevel()), formattedTime, record.getMessage(),
              record.getSourceClassName(), record.getSourceMethodName(),
              record.getThrown().getMessage(), stack };

      logMessage = exceptionMessageFormat.format(log);
    }
    return logMessage;
  }

  private String getStackLayout(Throwable t, String indenter) {
    indenter = indenter + " ";

    StackTraceElement[] ste = t.getStackTrace();
    String stack = indenter + ste[0].toString();
    for (int i = 1; i < ste.length; i++) {
      stack = stack + "\n" + indenter + ste[i];
    }

    String innerStack = "";
    if (t.getCause() != null) {
      innerStack = indenter + "Caused by: " + t.getCause().getMessage() + "\n";
      innerStack = innerStack + getStackLayout(t.getCause(), indenter);
    }
    stack = stack + "\n" + innerStack;

    return stack;
  }

  public String getExceptionPattern() {
    return exceptionPattern;
  }

  public String getLogPattern() {
    return logPattern;
  }

  public String getTimeFormat() {
    return timeFormat;
  }

}
