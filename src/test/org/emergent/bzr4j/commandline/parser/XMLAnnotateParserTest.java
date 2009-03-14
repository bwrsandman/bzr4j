/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.emergent.bzr4j.core.IBazaarAnnotation;
import org.emergent.bzr4j.commandline.CommandLineAnnotation;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.testUtils.ParserTest;

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
        Assert.assertNotNull( ann );
        Assert.assertTrue( ann.getNumberOfLines() == 46 );
    }
}
