package net.pandoragames.far;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class CharsetTest {

	public static void main(String[] arg) {
		String charset = arg.length > 0 ? arg[0] : "X-BinaryCS";
		if( Charset.isSupported(charset) ) {
			System.out.println(charset + " is supported");
			byte[] allChars = new byte[256];
			for(int i = 0; i < 256; i++) {
				allChars[i] = (byte) i;
			}
			try {
			 Reader reader = new InputStreamReader( new ByteArrayInputStream(allChars), Charset.forName(charset) );
			char[] line = new char[16];
			for(int i = 0; i < 16; i++ ) {
				int count = reader.read(line);
				System.out.print( new String(line, 0, count));
				System.out.println(" (" + count + ")");
			}
			} catch(IOException iox) {
				iox.printStackTrace();
			}
		}  else{
			System.out.println("Character set " + charset + " could not be loaded");
		}

	}
}
