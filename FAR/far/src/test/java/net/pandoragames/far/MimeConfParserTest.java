package net.pandoragames.far;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import junit.framework.TestCase;
import net.pandoragames.far.ui.MimeConfParser;
import net.pandoragames.far.ui.model.MimeType;

public class MimeConfParserTest extends TestCase {

	private MimeConfParser configParser = new MimeConfParser();
	
	public void testRoundTripWithDefault() throws Exception {
		// InputStream mimeDefault = this.getClass().getClassLoader().getResourceAsStream("META-INF/mimeTypes.xml");
		String mimeDefault = readMimeDefaultToString();
		configParser.parse( new ByteArrayInputStream( mimeDefault.getBytes("UTF-8")) );
		ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
		configParser.format( resultStream );
//		System.out.println(mimeDefault);
//		System.out.println(resultStream.toString("UTF-8"));
		assertEquals( mimeDefault, resultStream.toString("UTF-8") );
	}
	
	public void testRestoreChangedEncoding() throws Exception {
		configParser.parse( new ByteArrayInputStream( readMimeDefaultToString().getBytes("UTF-8")) );
		MimeType textPlain = (MimeType) MimeType.getType("text/plain");
		textPlain.setCharacterset(Charset.forName("US-ASCII"));
		ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
		configParser.format( resultStream );
		configParser.parse( new ByteArrayInputStream( resultStream.toByteArray() ) );
		MimeType textPlainCompare = (MimeType) MimeType.getType("text/plain");		
		assertEquals( "US-ASCII", textPlainCompare.getCharacterset().name() );
	}
	
	private String readMimeDefaultToString() throws IOException {
		StringBuilder buffer = new StringBuilder();
		Reader mimeDefault = new InputStreamReader( this.getClass().getClassLoader().getResourceAsStream("META-INF/mimeTypes.xml"), "UTF-8");
		try {
			char[] bb = new char[1024];
			int count = 0;
			do {
				count = mimeDefault.read(bb, 0, 1024);
				if( count > 0 ) buffer.append(bb, 0, count);
			} while ( count > 0 );
			return buffer.toString();
		} finally {
			mimeDefault.close();
		}
	}
	
}
