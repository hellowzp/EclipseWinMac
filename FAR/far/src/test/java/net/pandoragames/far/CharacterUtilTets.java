package net.pandoragames.far;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Properties;

import net.pandoragames.far.ui.model.CharacterUtil;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.util.i18n.DefaultLocalizer;
import net.pandoragames.util.i18n.Localizer;
import junit.framework.TestCase;

public class CharacterUtilTets extends TestCase {

	
	public void testFlatWhiteSpace() {
		String flattend = CharacterUtil.flatWhiteSpace("ab\tc\n\rnext  line");
		assertEquals("ab c next line", flattend);
	}
	
	public void testReportIllegalCharacter() throws Exception {
		String testText = "eine zeile ohne fehler\nwährend hier einer ist";
		TestBox testbox = new TestBox();
		Localizer localizer = getLocalizer(); 
		CharacterUtil.reportIllegalCharacter(testText.toCharArray(), 
				0, 
				testText.length(), 
				Charset.forName("US-ASCII").newEncoder(), 
				1, 
				testbox, 
				localizer);
		assertEquals(testbox.getError(), "Illegal character 'ä' at line 2 (\"währen...\")", testbox.getError());
	}

	public void testReportIllegalCharacter2() throws Exception {
		String testText = "Flussuferböschung";
		TestBox testbox = new TestBox();
		Localizer localizer = getLocalizer(); 
		CharacterUtil.reportIllegalCharacter(testText.toCharArray(), 
				0, 
				testText.length(), 
				Charset.forName("US-ASCII").newEncoder(), 
				1, 
				testbox, 
				localizer);
		assertEquals(testbox.getError(), "Illegal character 'ö' at line 1 (\"...uferböschu...\")", testbox.getError());
	}

	public void testReportSingleIllegalCharacter() throws Exception {
		String testText = "ü";
		TestBox testbox = new TestBox();
		Localizer localizer = getLocalizer(); 
		CharacterUtil.reportIllegalCharacter(testText.toCharArray(), 
				0, 
				testText.length(), 
				Charset.forName("US-ASCII").newEncoder(), 
				1, 
				testbox, 
				localizer);
		assertEquals(testbox.getError(), "Illegal character 'ü' at line 1 (\"ü\")", testbox.getError());
	}

	public void testReportIsolatedIllegalCharacter() throws Exception {
		String testText = "100 € per anno";
		TestBox testbox = new TestBox();
		Localizer localizer = getLocalizer(); 
		CharacterUtil.reportIllegalCharacter(testText.toCharArray(), 
				0, 
				testText.length(), 
				Charset.forName("US-ASCII").newEncoder(), 
				1, 
				testbox, 
				localizer);
		assertEquals(testbox.getError(), "Illegal character '€' at line 1 (\" € \")", testbox.getError());
	}

	public void testReportFirstIllegalCharacter() throws Exception {
		String testText = "ä\nö\nü\n";
		TestBox testbox = new TestBox();
		Localizer localizer = getLocalizer(); 
		CharacterUtil.reportIllegalCharacter(testText.toCharArray(), 
				0, 
				testText.length(), 
				Charset.forName("US-ASCII").newEncoder(), 
				1, 
				testbox, 
				localizer);
		assertEquals(testbox.getError(), "Illegal character 'ä' at line 1 (\"ä \")", testbox.getError());
	}
	
	public void testCP437() throws Exception {
		assertNotNull( Charset.forName("X-CP437") );
	}

	private Localizer getLocalizer() throws IOException {
		Properties props = new Properties();
		InputStream farPropertiesStream = null;
		try {
			farPropertiesStream = getClass().getClassLoader().getResourceAsStream("fartext.properties");
			props.load(farPropertiesStream);
			return new DefaultLocalizer(props, Locale.ENGLISH);
		} finally {
			if( farPropertiesStream != null ) try { farPropertiesStream.close(); } catch(IOException iox) {}
		}
	}
}

class TestBox implements MessageBox {
	private String error = null;
	private String info = null;
	public void clear() {
		error = null;
		info = null;
	}
	public void error(String message) {
		error = message;
	}
	public void info(String message) {
		info = message;
	}
	public String getError() {
		return error;
	}
	public String getInfo() {
		return info;
	}
}