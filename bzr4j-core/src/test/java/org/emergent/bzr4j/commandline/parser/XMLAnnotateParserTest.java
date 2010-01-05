/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import org.emergent.bzr4j.commandline.CommandLineAnnotation;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IBazaarAnnotation;
import org.emergent.bzr4j.testUtils.ParserTest;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * @author Guillermo Gonzalez
 */
public class XMLAnnotateParserTest extends ParserTest
{

    @Test
    public void testSimpleAnnotation() throws BazaarException, IOException
    {
        IBazaarAnnotation ann =
                CommandLineAnnotation.getAnnotationFromXml( getContentsFrom( annotationFile ) );
        org.testng.Assert.assertNotNull( ann );
        org.testng.Assert.assertTrue( ann.getNumberOfLines() == 46 );
    }
}
