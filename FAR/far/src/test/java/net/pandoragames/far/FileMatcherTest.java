package net.pandoragames.far;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * JUnit tests for the FileMatcher class.
 *
 * @author Olivier Wehner
 */
public class FileMatcherTest extends TestCase {

	private File testDir = TestHelper.findTestDir();
	
	/**
	 * Tests the apply() method without making a backup.
	 */
    public void testApplyNoBackup() {
    	File testFile = new File( testDir, "testfile002.test");
    	Pattern pattern = Pattern.compile("(\\w*):\\d*");
    	FileMatcher fileMatcher = new FileMatcher( pattern );
    	fileMatcher.setDoBackup(false);
    	String timestamp = String.valueOf( System.currentTimeMillis() );
    	String replacement = "$1:" + timestamp;;
    	try {
    		fileMatcher.apply( testFile, new ReplacementString( replacement, '$') );
    		String content = readFile( testFile ).toString();
    		Matcher matcher = Pattern.compile(".*:(\\d*)").matcher( content );
    		matcher.find();
    		String extract = matcher.group(1);
    		assertEquals( timestamp, extract );
    	} catch (Exception x) {
    		x.printStackTrace();
    		fail( x.getMessage() );
    	}
    }
    
    /**
     * Tests the preview method.
     */
    public void testPreview() {
    	Pattern pattern = Pattern.compile("(f\\w*) (s\\w*) (t\\w*)");
       	FileMatcher fileMatcher = new FileMatcher( pattern );
       	File testFile = new File(testDir, "testfile003.test");
       	ReplacementString replacement = new ReplacementString("\\3 \\2 \\1",'\\');
       	try {
       		String original = readFile( testFile ).toString();
       		String preview = fileMatcher.preview( testFile, replacement);
       		String expected = pattern.matcher( original ).replaceAll("$3 $2 $1");
       		assertEquals( expected, preview );
       	} catch (IOException iox) {
    		fail( iox.getMessage() );       		
       	}
    }
    
    /**
     * Tests the method getBackupFileName.
     */
    public void testGetBackupFileName() {
    	FileMatcher fileMatcher = new FileMatcher( Pattern.compile(".*") );
    	fileMatcher.setBaseDirectory( testDir );
    	String relativePath = "some" + File.separator + "subdir" + File.separator + "testfile.nix";
    	File doesNotExist = new File( testDir, relativePath );
    	File backup = fileMatcher.getBackupFileName(doesNotExist);
    	File expected = new File(fileMatcher.getBackUpDirectory(), relativePath);
    	assertEquals( expected, backup );
    }
    
	/**
	 * Tests the apply() method with backup.
	 * This is the most complete test in this serie.
	 */
    public void testApplyWithBackup() {
    	// two subdirectories - DO NOT CHANGE (or change the cleanup at the end of this test)
    	final String subdir = "sub1/sub2";
    	File testFile = new File( testDir, subdir + "/testfile004.test");
    	Pattern pattern = Pattern.compile("(f\\w*) (s\\w*) (t\\w*)");
    	FileMatcher fileMatcher = new FileMatcher( pattern );
    	fileMatcher.setBaseDirectory( testDir );
    	fileMatcher.setDoBackup(true);
    	try {
    		String original = readFile( testFile ).toString();
           	ReplacementString replacement = new ReplacementString("\\3 \\2 \\1",'\\');
    		fileMatcher.apply( testFile, replacement );
    		String content = readFile( testFile ).toString();
        	File backup = fileMatcher.getBackupFileName( testFile );
       		testFile.delete();
       		backup.renameTo( testFile );
    		String restored = readFile( testFile ).toString();
    		String expected = pattern.matcher( original ).replaceAll("$3 $2 $1"); 
    		assertEquals( "The transformation had not the expected result", expected, content );
    		assertEquals( "The testfile could not be restored", original, restored );
    	} catch (Exception x) {
    		x.printStackTrace();
    		fail( x.getMessage() );
    	}
    	// clean up
    	File toBeRemoved = new File( fileMatcher.getBackUpDirectory(), subdir );
    	toBeRemoved.delete();
    	toBeRemoved.getParentFile().delete();
    }
  
    /**
     * Test if matches are properly counted.
     */
    public void testCountMatches() {
    	File testFile = new File( testDir, "sub1/testfile005.test");
    	Pattern pattern = Pattern.compile("ein");
    	FileMatcher fileMatcher = new FileMatcher( pattern );
    	fileMatcher.setBaseDirectory( testDir );
    	try {
    		int count = fileMatcher.countMatches( testFile );
    		assertEquals( 3, count );
    	} catch (IOException iox) {
    		iox.printStackTrace();
    		fail(iox.getMessage());
    	}
    }
    
    /**
     * Tests if ALL occurences are properly replaced.
     */
    public void testApplyMultiple() {
    	File testFile = new File( testDir, "testfile006.test");
    	Pattern pattern = Pattern.compile("\\d+");
    	FileMatcher fileMatcher = new FileMatcher( pattern );
    	fileMatcher.setDoBackup(false);
    	String replacement = Integer.toString( (int) Math.floor( Math.random() * 100 )); 
    	try {
        	String oldContent = readFile( testFile ).toString();
    		fileMatcher.apply( testFile, new ReplacementString( replacement ) );
    		String newContent = readFile( testFile ).toString();
    		assertEquals( oldContent.replaceAll("\\d+", replacement), newContent );
    	} catch (Exception x) {
    		x.printStackTrace();
    		fail( x.getMessage() );
    	}
    }
// -- private helper --------------------------------------------------------------    
    
    /**
     * Reads the specified file into a CharSequence, using the current character set.
     * @param file to be read
     * @return file content
     * @throws IOException if an IOException occurs
     */
    private CharSequence readFile(File file) throws IOException {
    	final int buffersize = 1024;
    	BufferedReader reader =  new BufferedReader(new InputStreamReader( new FileInputStream( file ) ));
    	StringBuffer buffer = new StringBuffer();
    	char[] puffer = new char[ buffersize ];
    	int end = -1;
    	try {
    		while( reader.ready() ) {
    			end = reader.read(puffer, 0, buffersize);
    			if ( end < 0 ) {
    				break;
    			} else {
    				buffer.append( puffer, 0, end );
    			}
    		}
    	} finally {
    		try { reader.close(); } catch (IOException iox) { /* ignore */ }
    	}
    	return buffer;
    }

}
