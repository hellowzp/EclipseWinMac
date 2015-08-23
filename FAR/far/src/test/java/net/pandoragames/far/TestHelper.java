package net.pandoragames.far;

import java.io.File;
import java.net.URL;

/**
 * Common helper methods for JUnit tests.
 *
 * @author Olivier Wehner
 */
public class TestHelper {

	/**
	 * Returns the directory of the test files at BASE/src/test/testfiles.
	 * @return test directory
	 */
    public static File findTestDir() {
    	final String basePath = "src/test/testfiles";
    	try{
    		String myResourceFile = TestHelper.class.getName().replace('.', '/') + ".class";
    		URL muri = TestHelper.class.getClassLoader().getResource( myResourceFile );
    		File me = new File( muri.toURI() );
    		File testDir = new File( me.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile(), basePath);
    		if(! testDir.exists() ) throw new IllegalStateException("The directory " + testDir.getPath() + " does not exist");
    		return testDir;
    	} catch (Exception x) {
    		x.printStackTrace();
    		throw new IllegalStateException( x );
    	}
    }

}
