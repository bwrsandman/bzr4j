package org.emergent.bzr4j.utils;

import java.util.logging.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Patrick Woodworth
 */
public class IOUtil
{
    public static final String EOL = System.getProperty( "line.separator" );

    public static void close( InputStream closeable )
    {
        if (closeable != null) try { closeable.close(); } catch (Exception ignored) { }
    }

    public static void close( OutputStream closeable )
    {
        if (closeable != null) try { closeable.close(); } catch (Exception ignored) { }
    }

    public static void close( Reader closeable )
    {
        if (closeable != null) try { closeable.close(); } catch (Exception ignored) { }
    }

    public static void close( Writer closeable )
    {
        if (closeable != null) try { closeable.close(); } catch (Exception ignored) { }
    }

    /**
     * Replace all separators like / and \ to File.separatorChar
     *
     * @param filePath path to ve normilized
     * @return path with replaced separators
     */
    public static String deNormalizeSeparator( String filePath ) {
      final StringBuilder result = new StringBuilder(filePath.length());
      for (int i = 0; i < filePath.length(); i++) {
        final char c = filePath.charAt(i);
        result.append(c == '/' || c == '\\' ? File.separatorChar : c);
      }
      return result.toString();
    }

    public static String getCommonParent(final String firstUrl, final String secondUrl) {
      final StringBuffer result = new StringBuffer();

      StringBuffer nextPathElem = new StringBuffer();

      boolean addSeparator = false;

      int i = 0;
      for (; i < firstUrl.length() && i < secondUrl.length(); i++) {
        final char c1 = firstUrl.charAt(i);
        final char c2 = secondUrl.charAt(i);

        if (c1 != c2) {
          break;
        } else if (isFileSeparator(c1) || isFileSeparator(c2)) {
          if (addSeparator) {
            result.append("/");
          }

          if (nextPathElem.length() > 0) {
            result.append(nextPathElem.toString());
            nextPathElem = new StringBuffer();
            addSeparator = true;
          } else {
            if (addSeparator) {
              result.append("/");
              addSeparator = false;
            } else {
              addSeparator = true;
            }
          }

        } else {
          nextPathElem.append(c1);
        }
      }

      if (i == firstUrl.length() || i == secondUrl.length()) {
        if (nextPathElem.length() > 0) {
          if (result.length() > 0) {
            result.append("/");
          }
          result.append(nextPathElem.toString());
        }
      }


      return result.toString();
    }

    public static boolean isFileSeparator(final char c1) {
      return c1 == '\\' || c1 == '/';
    }

    public static File createTempDirectory(String prefix, String suffix) throws IOException
    {
        return createTempDirectory( prefix, suffix, new File( System.getProperty( "java.io.tmpdir" )) );
    }

    public static File createTempDirectory(final String prefix, final String suffix, final File in)
            throws IOException
    {
      final File tempFile = File.createTempFile(prefix, suffix, in);
      if (!tempFile.delete())
        throw new IOException( String.format( "Could not delete file '%s'", tempFile ) );
      if (!tempFile.mkdir())
          throw new IOException( String.format( "Could not create directory '%s'", tempFile ) );
      return tempFile;
    }

    public static void writeToFile( File file, byte[] bytes ) throws IOException
    {
        OutputStream os = null;
        try
        {
            os = new BufferedOutputStream( new FileOutputStream( file ) );
            os.write( bytes );
            os.flush();
        }
        finally
        {
            close( os );
        }
    }

    public static File toCanonical( File file )
    {
        try
        {
            return file.getCanonicalFile();
        }
        catch ( IOException e )
        {
            return file.getAbsoluteFile();
        }
    }
}
