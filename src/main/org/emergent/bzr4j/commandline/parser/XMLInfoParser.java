/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import org.emergent.bzr4j.commandline.CommandLineInfo;
import org.emergent.bzr4j.commandline.CommandLineInfo.CmdLineBranchHistory;
import org.emergent.bzr4j.commandline.CommandLineInfo.CmdLineLocations;
import org.emergent.bzr4j.commandline.CommandLineInfo.CmdLineRelatedBranches;
import org.emergent.bzr4j.commandline.CommandLineInfo.CmdLineRepositoryStats;
import org.emergent.bzr4j.commandline.CommandLineInfo.CmdLineWorkingTreeStats;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IBazaarInfo;
import org.emergent.bzr4j.utils.StringUtil;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Guillermo Gonzalez
 *
 */
public class XMLInfoParser extends XMLParser
{
    private static final String INFO = "info";

    private static final String LAYOUT = "layout";

    private static final String FORMATS = "formats";

    private static final String FORMAT = "format";

    // <location>
    private static final String LOCATION = "location";

    private static final String LOCATION_LIGHT_CHECKOUT_ROOT = "light_checkout_root";

    private static final String LOCATION_REPOSITORY_CHECKOUT_ROOT = "repository_checkout_root";

    private static final String LOCATION_CHECKOUT_ROOT = "checkout_root";

    private static final String LOCATION_CHECKOUT_OF_BRANCH = "checkout_of_branch";

    private static final String LOCATION_SHARED_REPOSITORY = "shared_repository";

    private static final String LOCATION_REPOSITORY = "repository";

    private static final String LOCATION_REPOSITORY_BRANCH = "repository_branch";

    private static final String LOCATION_BRANCH_ROOT = "branch_root";

    private static final String LOCATION_BOUND_TO_BRANCH = "bound_to_branch";

    // </location>
    // <related_branches>
    private static final String RELATED_BRANCHES = "related_branches";

    private static final String PUBLIC_BRANCH = "public_branch";

    private static final String PUSH_BRANCH = "push_branch";

    private static final String PARENT_BRANCH = "parent_branch";

    private static final String SUBMIT_BRANCH = "submit_branch";

    // </related_branches>
    // <format>
    private static final String CONTROL = "control";

    private static final String WORKING_TREE = "working_tree";

    private static final String BRANCH = "branch";

    private static final String REPOSITORY = "repository";

    // <workingtree_stats>
    private static final String WORKING_TREE_STATS = "working_tree_stats";

    private static final String UNCHANGED = "unchanged";

    private static final String MODIFIED = "modified";

    private static final String ADDED = "added";

    private static final String REMOVED = "removed";

    private static final String RENAMED = "renamed";

    private static final String UNKNOWN = "unknown";

    private static final String IGNORED = "ignored";

    private static final String VERSIONED_SUBDIRECTORIES = "versioned_subdirectories";
    // </workingtree_stats>

    private static final String BRANCH_HISTORY = "branch_history";

    private static final String REVISIONS = "revisions";

    private static final String COMMITTERS = "committers";

    private static final String DAYS_OLD = "days_old";

    private static final String FIRST_REVISION = "first_revision";

    private static final String LATEST_REVISION = "latest_revision";

    private static final String REPOSITORY_STATS = "repository_stats";

    private static final String SIZE = "size";

    public IBazaarInfo parse( String xml ) throws BazaarException
    {
        parser = new KXmlParser();
        try
        {
            parser.setInput( new StringReader( xml ) );
            int eventType = parser.getEventType();
            CommandLineInfo info = null;
            while ( eventType != XmlPullParser.END_DOCUMENT )
            {
                if ( eventType == XmlPullParser.START_TAG && INFO.equals( parser.getName() ) )
                {
                    info = parseInfo();
                }
                eventType = parser.next();
            }
            return info;
        }
        catch ( XmlPullParserException e )
        {
            throw new BazaarException( e );
        }
        catch ( ParseException e )
        {
            throw new BazaarException( e );
        }
        catch ( IOException e )
        {
            throw new BazaarException( e );
        }
    }

    /**
     * @throws IOException
     * @throws XmlPullParserException
     * @throws ParseException
     *
     */
    private CommandLineInfo parseInfo() throws XmlPullParserException, IOException, ParseException
    {
        int eventType = parser.next();
        String layout = null;
        String workingTreeFormat = null;
        String branchFormat = null;
        String repositoryFormat = null;
        String controlFormat = null;
        List<String> formats = Collections.EMPTY_LIST;
        CmdLineLocations locations = null;
        CmdLineRelatedBranches relatedBranches = null;
        CmdLineWorkingTreeStats workingTreeStats = null;
        CmdLineBranchHistory branchHistory = null;
        CmdLineRepositoryStats repositoryStats = null;
        while ( eventType != XmlPullParser.END_DOCUMENT || (eventType == XmlPullParser.END_TAG
                && INFO.equals( parser.getName() )) )
        {
            if ( eventType == XmlPullParser.START_TAG && LAYOUT.equals( parser.getName() ) )
            {
                layout = parser.nextText();
            }
            else if ( eventType == XmlPullParser.START_TAG && FORMATS.equals( parser.getName() ) )
            {
                formats = parseFormats();
            }
            else if ( eventType == XmlPullParser.START_TAG && LOCATION.equals( parser.getName() ) )
            {
                locations = parseLocations();
            }
            else if ( eventType == XmlPullParser.START_TAG && RELATED_BRANCHES
                    .equals( parser.getName() ) )
            {
                relatedBranches = parseRelatedBranches();
            }
            else if ( eventType == XmlPullParser.START_TAG && FORMAT.equals( parser.getName() ) )
            {
                int formatEventType = parser.next();
                while ( formatEventType != XmlPullParser.END_DOCUMENT || (
                        formatEventType == XmlPullParser.END_TAG && FORMAT
                                .equals( parser.getName() )) )
                {
                    if ( formatEventType == XmlPullParser.START_TAG && CONTROL
                            .equals( parser.getName() ) )
                    {
                        controlFormat = StringUtil.nullSafeTrim( parser.nextText() );
                    }
                    else if ( formatEventType == XmlPullParser.START_TAG && WORKING_TREE
                            .equals( parser.getName() ) )
                    {
                        workingTreeFormat = StringUtil.nullSafeTrim( parser.nextText() );
                    }
                    else if ( formatEventType == XmlPullParser.START_TAG && BRANCH
                            .equals( parser.getName() ) )
                    {
                        branchFormat = StringUtil.nullSafeTrim( parser.nextText() );
                    }
                    else if ( formatEventType == XmlPullParser.START_TAG && REPOSITORY
                            .equals( parser.getName() ) )
                    {
                        repositoryFormat = StringUtil.nullSafeTrim( parser.nextText() );
                    }
                    else if ( formatEventType == XmlPullParser.END_TAG && FORMAT
                            .equals( parser.getName() ) )
                    {
                        break;
                    }
                    formatEventType = parser.next();
                }
            }
            else if ( eventType == XmlPullParser.START_TAG && WORKING_TREE_STATS
                    .equals( parser.getName() ) )
            {
                workingTreeStats = parseWorkingTreeStats();
            }
            else if ( eventType == XmlPullParser.START_TAG && BRANCH_HISTORY
                    .equals( parser.getName() ) )
            {
                branchHistory = parseBranchHistory();
            }
            else if ( eventType == XmlPullParser.START_TAG && REPOSITORY_STATS
                    .equals( parser.getName() ) )
            {
                repositoryStats = parseRepositoryStats();
            }
            eventType = parser.next();
        }
        return new CommandLineInfo( layout, formats.toArray( new String[0] ), branchFormat,
                controlFormat, repositoryFormat, workingTreeFormat, locations, relatedBranches,
                workingTreeStats,
                branchHistory, repositoryStats );
    }

    private List<String> parseFormats() throws XmlPullParserException, IOException
    {
        int eventType = parser.next();
        final List<String> formats = new ArrayList<String>();
        while ( eventType != XmlPullParser.END_DOCUMENT || (eventType == XmlPullParser.END_TAG
                && FORMATS.equals( parser.getName() )) )
        {
            if ( eventType == XmlPullParser.START_TAG && FORMAT.equals( parser.getName() ) )
            {
                formats.add( StringUtil.nullSafeTrim( parser.nextText() ) );
            }
            else if ( eventType == XmlPullParser.END_TAG && FORMATS.equals( parser.getName() ) )
            {
                return formats;
            }
            eventType = parser.next();
        }
        return formats;
    }

    private CmdLineLocations parseLocations() throws XmlPullParserException, IOException
    {
        int eventType = parser.next();
        String lightCheckoutRoot, repositoryCheckoutRoot, checkoutRoot, checkoutOfBranch;
        String sharedRepository, repository, repositoryBranch, branchRoot, boundToBranch;
        lightCheckoutRoot = repositoryCheckoutRoot = checkoutRoot = checkoutOfBranch = null;
        sharedRepository = repository = repositoryBranch = branchRoot = boundToBranch = null;
        while ( eventType != XmlPullParser.END_DOCUMENT || (eventType == XmlPullParser.END_TAG
                && LOCATION.equals( parser.getName() )) )
        {
            if ( eventType == XmlPullParser.START_TAG && LOCATION_BOUND_TO_BRANCH
                    .equals( parser.getName() ) )
            {
                boundToBranch = StringUtil.nullSafeTrim( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && LOCATION_BRANCH_ROOT
                    .equals( parser.getName() ) )
            {
                branchRoot = StringUtil.nullSafeTrim( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && LOCATION_CHECKOUT_OF_BRANCH
                    .equals( parser.getName() ) )
            {
                checkoutOfBranch = StringUtil.nullSafeTrim( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && LOCATION_CHECKOUT_ROOT
                    .equals( parser.getName() ) )
            {
                checkoutRoot = StringUtil.nullSafeTrim( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && LOCATION_LIGHT_CHECKOUT_ROOT
                    .equals( parser.getName() ) )
            {
                lightCheckoutRoot = StringUtil.nullSafeTrim( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && LOCATION_REPOSITORY
                    .equals( parser.getName() ) )
            {
                repository = StringUtil.nullSafeTrim( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && LOCATION_REPOSITORY_BRANCH
                    .equals( parser.getName() ) )
            {
                repositoryBranch = StringUtil.nullSafeTrim( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && LOCATION_REPOSITORY_CHECKOUT_ROOT
                    .equals( parser.getName() ) )
            {
                repositoryCheckoutRoot = StringUtil.nullSafeTrim( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && LOCATION_SHARED_REPOSITORY
                    .equals( parser.getName() ) )
            {
                sharedRepository = StringUtil.nullSafeTrim( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.END_TAG && LOCATION.equals( parser.getName() ) )
            {
                break;
            }
            eventType = parser.next();
        }
        return new CmdLineLocations( lightCheckoutRoot, repositoryCheckoutRoot,
                checkoutRoot, checkoutOfBranch, sharedRepository, repository,
                repositoryBranch, branchRoot, boundToBranch );
    }

    private CmdLineRelatedBranches parseRelatedBranches() throws XmlPullParserException, IOException
    {
        int eventType = parser.next();
        String publicBranch = null, push = null, parent = null, submit = null;
        while ( eventType != XmlPullParser.END_DOCUMENT || (eventType == XmlPullParser.END_TAG
                && RELATED_BRANCHES.equals( parser.getName() )) )
        {
            if ( eventType == XmlPullParser.START_TAG && PUBLIC_BRANCH.equals( parser.getName() ) )
            {
                publicBranch = StringUtil.nullSafeTrim( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && PUSH_BRANCH
                    .equals( parser.getName() ) )
            {
                push = StringUtil.nullSafeTrim( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && PARENT_BRANCH
                    .equals( parser.getName() ) )
            {
                parent = StringUtil.nullSafeTrim( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && SUBMIT_BRANCH
                    .equals( parser.getName() ) )
            {
                submit = StringUtil.nullSafeTrim( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.END_TAG && RELATED_BRANCHES
                    .equals( parser.getName() ) )
            {
                break;
            }
            eventType = parser.next();
        }
        return new CmdLineRelatedBranches( publicBranch, push, parent, submit );
    }

    private CmdLineWorkingTreeStats parseWorkingTreeStats()
            throws XmlPullParserException, IOException
    {
        int eventType = parser.next();
        Integer unchanged, modified, added, removed, renamed, unknown, ignored, versionedSubDirs;
        unchanged = modified = added =
                removed = renamed = unknown = ignored = versionedSubDirs = Integer.valueOf( 0 );
        while ( eventType != XmlPullParser.END_DOCUMENT || (eventType == XmlPullParser.END_TAG
                && WORKING_TREE_STATS.equals( parser.getName() )) )
        {
            if ( eventType == XmlPullParser.START_TAG && UNCHANGED.equals( parser.getName() ) )
            {
                unchanged = new Integer( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && MODIFIED.equals( parser.getName() ) )
            {
                modified = new Integer( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && ADDED.equals( parser.getName() ) )
            {
                added = new Integer( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && REMOVED.equals( parser.getName() ) )
            {
                removed = new Integer( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && RENAMED.equals( parser.getName() ) )
            {
                renamed = new Integer( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && UNKNOWN.equals( parser.getName() ) )
            {
                unknown = new Integer( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && IGNORED.equals( parser.getName() ) )
            {
                ignored = new Integer( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && VERSIONED_SUBDIRECTORIES
                    .equals( parser.getName() ) )
            {
                versionedSubDirs = new Integer( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.END_TAG && WORKING_TREE_STATS
                    .equals( parser.getName() ) )
            {
                break;
            }
            eventType = parser.next();
        }
        return new CmdLineWorkingTreeStats( added, ignored, modified, removed, renamed, unchanged,
                unknown, versionedSubDirs );
    }

    private CmdLineBranchHistory parseBranchHistory()
            throws XmlPullParserException, IOException, ParseException
    {
        int eventType = parser.next();
        Integer revisions, committers, days;
        revisions = committers = days = Integer.valueOf( 0 );
        Date firstRevDate = null, lastRevDate = null;
        while ( eventType != XmlPullParser.END_DOCUMENT || (eventType == XmlPullParser.END_TAG
                && BRANCH_HISTORY.equals( parser.getName() )) )
        {
            if ( eventType == XmlPullParser.START_TAG && REVISIONS.equals( parser.getName() ) )
            {
                revisions = new Integer( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && COMMITTERS
                    .equals( parser.getName() ) )
            {
                committers = new Integer( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && DAYS_OLD.equals( parser.getName() ) )
            {
                days = new Integer( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && FIRST_REVISION
                    .equals( parser.getName() ) )
            {
                final String timestamp = StringUtil.nullSafeTrim( parser.nextText() );
                firstRevDate = StringUtil.parseLogDate( timestamp );
            }
            else if ( eventType == XmlPullParser.START_TAG && LATEST_REVISION
                    .equals( parser.getName() ) )
            {
                final String timestamp = StringUtil.nullSafeTrim( parser.nextText() );
                lastRevDate = StringUtil.parseLogDate( timestamp );
            }
            else if ( eventType == XmlPullParser.END_TAG && BRANCH_HISTORY
                    .equals( parser.getName() ) )
            {
                break;
            }
            eventType = parser.next();
        }
        return new CmdLineBranchHistory( revisions, committers, days, firstRevDate, lastRevDate );
    }

    private CmdLineRepositoryStats parseRepositoryStats() throws XmlPullParserException, IOException
    {
        int eventType = parser.next();
        Integer revisions = null;
        Long size = null;
        while ( eventType != XmlPullParser.END_DOCUMENT || (eventType == XmlPullParser.END_TAG
                && REPOSITORY_STATS.equals( parser.getName() )) )
        {
            if ( eventType == XmlPullParser.START_TAG && REVISIONS.equals( parser.getName() ) )
            {
                revisions = new Integer( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.START_TAG && SIZE.equals( parser.getName() ) )
            {
                size = new Long( parser.nextText() );
            }
            else if ( eventType == XmlPullParser.END_TAG && REPOSITORY_STATS
                    .equals( parser.getName() ) )
            {
                break;
            }
            eventType = parser.next();
        }
        return new CmdLineRepositoryStats( revisions, size );
    }

}
