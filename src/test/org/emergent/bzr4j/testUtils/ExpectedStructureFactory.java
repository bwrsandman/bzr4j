/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.testUtils;

/**
 *
 * @author Guillermo Gonzalez
 *
 * TODO
 */
public class ExpectedStructureFactory
{

    public static ExpectedWorkingTree getWorkingTree()
    {
        ExpectedWorkingTree wt = new ExpectedWorkingTree();
        putStructureInto( wt );
        return wt;
    }

    private static void putStructureInto( ExpectedStructure greek )
    {
        greek.addItem( "", null );
        greek.addItem( "file_in_root.txt",
                "This is the a file in the root." + System.getProperty( "line.separator" ) );
        greek.addItem( "A", null );
        greek.addItem( "A/B", null );
        greek.addItem( "A/B/C", null );
        greek.addItem( "A/B/D", null );
        greek.addItem( "A/file_in_A",
                "This is a file in the root of /A" + System.getProperty( "line.separator" ) );
        greek.addItem( "A/B/file_in_B",
                "This is a file in /A/B" + System.getProperty( "line.separator" ) );
        greek.addItem( "A/B/C/file_in_C",
                "This is a file in /A/B/C." + System.getProperty( "line.separator" ) );
        greek.addItem( "A/B/D/file_in_D",
                "This is a file A/B/D." + System.getProperty( "line.separator" ) );
        greek.addItem( "A/E", null );
        greek.addItem( "A/E/F", null );
        greek.addItem( "A/E/file_in_E",
                "This is a file in /A/E." + System.getProperty( "line.separator" ) );
        greek.addItem( "A/E/F/file_in_F",
                "This is a file in /A/E/F." + System.getProperty( "line.separator" ) );
        greek.addItem( "A/E/F/other_file_in_F",
                "This is another file in /A/E/F." + System.getProperty( "line.separator" ) );
    }

}
