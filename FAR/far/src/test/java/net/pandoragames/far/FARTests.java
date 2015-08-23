package net.pandoragames.far;

import java.beans.DefaultPersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Set;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import net.pandoragames.far.ui.SimpleFileNamePattern;
import net.pandoragames.far.ui.SimpleFileNamePatternFAR;
import net.pandoragames.far.ui.SimpleFileNamePatternShell;
import net.pandoragames.far.ui.model.FindForm;
/**
 * Miscelanneous Junit tests for FAR.
 * TODO: test with character sets
 * @author Olivier Wehner at 21.02.2008
 */
public class FARTests extends TestCase {

	/**
	 * Tests {@link net.pandoragames.far.RegexUtil#replace(String, Pattern, String) RegexUtil.replace()}.
	 */
    public void testReplace() {
        Pattern pattern = Pattern.compile(".*-(\\d*)(\\.\\w{1,12})?");
        ReplacementString replacement = new ReplacementString("$1-neufile$2", '$');
        // standard
        assertEquals("080215-neufile.txt", FileSelector.rename("myfile-080215.txt", pattern, replacement));
 
        //
        // replacement strings not matching the number of capturing groups
        //
        
        // group 2 not matched
        assertEquals("080216-neufile", FileSelector.rename("myfile-080216", pattern, replacement));
        // group 2 matched but not used
        assertEquals("neufile-080216.xyz", FileSelector.rename("myfile-080216", pattern, new ReplacementString("neufile-$1.xyz")));        
    }
    
    /**
     * Tests the correct transformation of replacement strings with backslash.
     */
    public void testReplaceBackslash() {
    	assertEquals("\\$abc$1und$2", ReplacementString.dollarReplacementString("$abc\\1und\\2", '\\'));
    }
    
    /**
     * Tests building a regex from an ordinary string
     */
    public void testRegexFromPlain() {
    	String plainPattern = ".ab(1)*x";
    	String regex = plainPattern.replaceAll("(\\W)", "\\\\$1");
    	assertEquals(  "\\.ab\\(1\\)\\*x", regex );
    	assertTrue( Pattern.matches( regex, plainPattern ) );
    }
    
    /**
     * Test the file extension identification pattern for file name pattern mime dialog.
     */
    public void testFileExtensionPatternRegex() {
    	String pattern = "\\s*\\*\\.\\w+(\\s*,\\s*\\*\\.\\w+)*\\s*";
    	assertTrue( "simple failed", "*.zarg".matches( pattern ));
    	assertTrue( "multiple failed", "*.htm, *.hmtl".matches( pattern ));
    }
    
    /**
     * Tests FileSelector.listFiles().
     */
    public void testListFiles() {
    	File testDir = TestHelper.findTestDir();
    	try {
    		FileSelector selector = new FileSelector();
    		Pattern pattern = SimpleFileNamePattern.getInstance().createPattern("*.txt", true);
    		Set<?> fileSet = selector.listFiles(pattern, testDir);
    		assertEquals( 2, fileSet.size());
    	} catch (Exception x) {
    		x.printStackTrace();
    		fail(x.getMessage());
    	}
    }
    
    /**
     * Tests the line break matching regex used in
     * {@link net.pandoragames.far.ui.model.FindForm#getContentPatternAsRegex()}
     */
    public void testLineBreakRegex() {
    	String[] lineBreaks = new String[] {"a\nb", "a\r\nb", "a\rb", "a\u0085b", "a\u2028b", "a\u2029b"};
    	Pattern pattern1 = Pattern.compile("$\r?(?s:.)^", Pattern.MULTILINE);
    	for(int i = 0; i < lineBreaks.length; i++){
    		assertTrue( "#" + i + " does not match " + pattern1.toString(), pattern1.matcher( lineBreaks[i] ).find() );
    	}
    	Pattern pattern2 = Pattern.compile("$(?s:.{1,2})^", Pattern.MULTILINE);
    	for(int i = 0; i < lineBreaks.length; i++){
    		assertTrue( "#" + i + " does not match " + pattern2.toString(), pattern2.matcher( lineBreaks[i] ).find() );
    	}
    }
 
    /**
     * Tests if arbitrary line breaks can consistently  be replaced.
     */
    public void testNormalizeLineBreaks() {
    	String[] testlines = new String[] {"\n\nAa\nBb\n", 
    										"\r\rAa\rBb\r", 
    										"\r\n\r\nAa\r\nBb\r\n"};
    	Pattern pattern = Pattern.compile("(\\r\\n)|[\\r\\n\\u0085\\u2028\\u2029]");
    	for(int i = 0; i < testlines.length; i++) {
    		String replaced = pattern.matcher( testlines[i] ).replaceAll("x");
    		assertEquals( "failed in iteration " + i , "xxAaxBbx", replaced );
    	}
    }
    
    /**
     * Tests counting of capturing groups.
     */
    public void testCountGroups() {
    	ReplacementString.setDefaultGroupReferenceIndicator('$');
    	assertEquals( 3, new ReplacementString("\\$4some$3text").getMaxGroupReference());
    	assertEquals( 3, new ReplacementString("$3some$2text").getMaxGroupReference());
    	assertEquals( 20, new ReplacementString("some\\20text", '\\').getMaxGroupReference());
    	Pattern pattern = Pattern.compile("(\\d)\\D(\\d)");
    	assertTrue( new ReplacementString("a$2").doesApply( pattern ));
    	assertFalse( new ReplacementString("a$3") .doesApply( pattern ));
    }
    
    
    /**
     * Tests serialisation and deserialisation of the FindForm class.
     */
    public void testFindFormSerialisation() {
    	File testDir = TestHelper.findTestDir();
    	File outputFile = new File( testDir, "FindForm.xml");
    	XMLEncoder xmlEncoder = null;
    	FindForm testForm = new FindForm();
    	try {
	    	xmlEncoder = new XMLEncoder( new BufferedOutputStream( new FileOutputStream( outputFile ) ));
	    	xmlEncoder.setPersistenceDelegate(File.class, 
	    									  new DefaultPersistenceDelegate(new String[]{"path"}));
	    	testForm.setBaseDirectory( testDir );
	    	testForm.setSearchStringContent("\\w \\d{2} .*");
	    	xmlEncoder.writeObject( testForm );
    	} catch (Exception x) {
    		x.printStackTrace();
    		fail( x.getMessage() );    		
    	}
    	finally {
    		if( xmlEncoder != null ) xmlEncoder.close();
    	}
    	XMLDecoder decoder = null;
    	try {
    		decoder = new XMLDecoder( new BufferedInputStream( new FileInputStream( outputFile ) ));
    		FindForm compForm = (FindForm) decoder.readObject();
    		// assertEquals( testForm, compForm );
    		assertTrue( "Deserialised FindForm differs from original", testForm.equals(compForm));
    	} catch (Exception x) {
    		x.printStackTrace();
    		fail( x.getMessage() );    		
    	}
    	finally {
    		if( decoder != null ) decoder.close();
    	}
    }
    
    
    public void testFindNoLineSeperator() {
    	String original = "\\\\no string with \\\\no line seperator";
    	String unescaped = original.replaceAll("(^|[^\\\\])\\n", "\n");
    	// nothing must change !
    	assertEquals(original, unescaped);
    	unescaped = original.replaceAll("\\\\{2}", "\\\\");
    	assertEquals("\\no string with \\no line seperator", unescaped);
    }
    
    public void testEscapeLineBreaks() {
    	String line = "text\nmit \\ slash und\nbreak";
		line = line.replaceAll("\\\\", "\\\\\\\\");
		line = line.replaceAll("\\n", "\\\\n");
		assertEquals("text\\nmit \\\\ slash und\\nbreak", line);
    }
    
    public void testSimpleFileNamePattern() {
    	SimpleFileNamePatternFAR.setDefault();
    	SimpleFileNamePattern fileNamePattern = SimpleFileNamePattern.getInstance();
    	String testString1 = ".html,.htm";
    	String testString2 = " blanks in and around ";
    	String testString3 = "extension_of_three.!!!";
    	String testString4 = "with_digit###.*";
    	try {
	    	String regex = fileNamePattern.createPattern(testString1, true).toString();
	    	assertEquals("Unexpected pattern: " + regex, "(.*\\.html)|(.*\\.htm)", regex);
	    	regex = fileNamePattern.createPattern(testString2, true).toString();
	    	assertEquals("Unexpected pattern: " + regex, "(blanks in and around)", regex);
	    	regex = fileNamePattern.createPattern(testString3, true).toString();
	    	assertEquals("Unexpected pattern: " + regex, "(extension_of_three\\....)", regex);
	    	regex = fileNamePattern.createPattern(testString4, true).toString();
	    	assertEquals("Unexpected pattern: " + regex, "(with_digit\\d\\d\\d\\..*)", regex);
    	} catch (PatternException px) {
    		fail("PatternException: " + px.getMessage());
    	}
    }
    
    public void testSimpleShellPattern() {
    	SimpleFileNamePatternShell.setDefault();
    	SimpleFileNamePattern fileNamePattern = SimpleFileNamePattern.getInstance();
    	String testString1 = "*.txt";
    	String testString2 = "{*.html,*.htm}";
    	String testString3 = "extension_of_three.???";
    	String testString4 = "a[char]class";
    	String testString5 = "a[!negation]class";
    	String testString6 = "a_r[a-n]ge_class";
    	String testString7 = "a{[c-o[mpl]]ex,exa{m,p,l}e}";
    	try {
	    	String regex = fileNamePattern.createPattern(testString1, true).toString();
	    	assertEquals("Unexpected pattern: " + regex, ".*\\.txt", regex);
	    	regex = fileNamePattern.createPattern(testString2, true).toString();
	    	assertEquals("Unexpected pattern: " + regex, "((.*\\.html)|(.*\\.htm))", regex);
	    	regex = fileNamePattern.createPattern(testString3, true).toString();
	    	assertEquals("Unexpected pattern: " + regex, "extension_of_three\\....", regex);
	    	regex = fileNamePattern.createPattern(testString4, true).toString();
	    	assertEquals("Unexpected pattern: " + regex, "a[char]class", regex);
	    	regex = fileNamePattern.createPattern(testString5, true).toString();
	    	assertEquals("Unexpected pattern: " + regex, "a[^negation]class", regex);
	    	regex = fileNamePattern.createPattern(testString6, true).toString();
	    	assertEquals("Unexpected pattern: " + regex, "a_r[a-n]ge_class", regex);
	    	regex = fileNamePattern.createPattern(testString7, true).toString();
	    	assertEquals("Unexpected pattern: " + regex, "a(([c-o[mpl]]ex)|(exa((m)|(p)|(l))e))", regex);
    	} catch (PatternException px) {
    		fail("PatternException: " + px.getMessage());
    	}
     }
// -- private helper ---------------------------------------------------------------------------------------------------------------------------------    
    
    
}

