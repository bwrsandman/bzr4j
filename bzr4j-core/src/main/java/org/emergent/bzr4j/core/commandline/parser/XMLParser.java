package org.emergent.bzr4j.commandline.parser;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;

public abstract class XMLParser
{

    protected KXmlParser parser;

    protected File workDir;

    public XMLParser()
    {
        super();
    }

    protected boolean isEndTag( int eventType )
    {
        return XmlPullParser.END_TAG == eventType;
    }

}
