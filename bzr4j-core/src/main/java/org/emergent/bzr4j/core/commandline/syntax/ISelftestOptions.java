/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.KeywordOption;
import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface ISelftestOptions
{

    public final static String COMMAND = "selftest"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Generate lsprof output for benchmarked sections of code.
     */
    public static final Option LSPROF_TIMED = new Option( "--lsprof-timed" ); //$NON-NLS-N$

    /**
     * Run the benchmarks rather than selftests.
     */
    public static final Option BENCHMARK = new Option( "--benchmark" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Stop when one test fails.
     */
    public static final Option ONE = new Option( "--one" ); //$NON-NLS-N$

    /**
     * Fail on missing dependencies or known failures.
     */
    public static final Option STRICT = new Option( "--strict" ); //$NON-NLS-N$

    /**
     * List the tests instead of running them.
     */
    public static final Option LIST_ONLY = new Option( "--list-only" ); //$NON-NLS-N$

    /**
     * Cache intermediate benchmark output in this directory.
     */
    public static final KeywordOption CACHE_DIR = new KeywordOption( "--cache-dir", "ARG" );
            //$NON-NLS-N$

    /**
     * Generate line coverage report in this directory.
     */
    public static final KeywordOption COVERAGE = new KeywordOption( "--coverage", "DIRECTORY" );
            //$NON-NLS-N$

    /**
     * Exclude tests that match this regular expression.
     */
    public static final KeywordOption EXCLUDE = new KeywordOption( "--exclude", "PATTERN" );
            //$NON-NLS-N$

    /**
     * Run all tests, but run specified tests first.
     */
    public static final Option FIRST = new Option( "--first" ); //$NON-NLS-N$

    /**
     * Randomize the order of tests using the given seed or "now" for the current time.
     */
    public static final KeywordOption RANDOMIZE = new KeywordOption( "--randomize", "SEED" );
            //$NON-NLS-N$

    /**
     * Use a different transport by default throughout the test suite.
     */
    public static final KeywordOption TRANSPORT = new KeywordOption( "--transport", "ARG" );
            //$NON-NLS-N$

    public final static String HELP =
            "Run internal test suite.\\n\\nIf arguments are given, they are regular expressions that say which tests\\nshould run.  Tests matching any expression are run, and other tests are\\nnot run.\\n\\nAlternatively if --first is given, matching tests are run first and then\\nall other tests are run.  This is useful if you have been working in a\\nparticular area, but want to make sure nothing else was broken.\\n\\nIf --exclude is given, tests that match that regular expression are\\nexcluded, regardless of whether they match --first or not.\\n\\nTo help catch accidential dependencies between tests, the --randomize\\noption is useful. In most cases, the argument used is the word \'now\'.\\nNote that the seed used for the random number generator is displayed\\nwhen this option is used. The seed can be explicitly passed as the\\nargument to this option if required. This enables reproduction of the\\nactual ordering used if and when an order sensitive problem is encountered.\\n\\nIf --list-only is given, the tests that would be run are listed. This is\\nuseful when combined with --first, --exclude and/or --randomize to\\nunderstand their impact. The test harness reports \"Listed nn tests in ...\"\\ninstead of \"Ran nn tests in ...\" when list mode is enabled.\\n\\nIf the global option \'--no-plugins\' is given, plugins are not loaded\\nbefore running the selftests.  This has two effects: features provided or\\nmodified by plugins will not be tested, and tests provided by plugins will\\nnot be run.\\n\\nTests that need working space on disk use a common temporary directory, \\ntypically inside $TMPDIR or /tmp.\\n\\n:Examples:\\n    Run only tests relating to \'ignore\'::\\n\\n        bzr selftest ignore\\n\\n    Disable plugins and list tests as they\'re run::\\n\\n        bzr --no-plugins selftest -v"
            ; //$NON-NLS-N$
}
