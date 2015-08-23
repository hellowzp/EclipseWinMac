package net.pandoragames.far.ui.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;

import net.pandoragames.far.ui.FARConfig;
import net.pandoragames.util.i18n.Localizer;

/**
 * Static helper for character handling routines.
 *
 * @author Olivier Wehner
 * <!--
 *  FAR - Find And Replace
 *  Copyright (C) 2009,  Olivier Wehner

 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  -->
 */
public class CharacterUtil {

	public static class LineCount {
		private LineSeparator separator;
		private int lineCount;
		public LineCount( LineSeparator sep, int count) {
			separator = sep;
			lineCount = count;
		}
		public LineSeparator getSeparator() {
			return separator;
		}
		public int getLineCount() {
			return lineCount;
		}
	}
	
	/**
	 * Returns the number of line breaks (\n, \r or combination thereof)
	 * found in the specified buffer.
	 * @param buffer where to look
	 * @param offset where to start looking
	 * @param length how many characters to test
	 * @return number of line breaks found, counting \n\r (and \r\n) as a single linebreak
	 */
	public static int countLinebreaks(char[] buffer, int offset, int length) {
		int lfCount = 0;
		int crCount = 0;
		for(int i = offset; i < length; i++) {
			if(buffer[i] == '\r') crCount++;
			if(buffer[i] == '\n') lfCount++;
		}
		return Math.max(lfCount, crCount);
	}

	/**
	 * Returns the number of lines (\n, \r or combination thereof)
	 * found in the specified input and its type.
	 * 
	 * @param instream where to look
	 * @return number of line breaks found, counting \r\n (and \n\r) as a single linebreak
	 * @throws IOException if the stream throws one
	 */
	public static LineCount countLinebreaks(InputStream instream) throws IOException {
		int lfCount = 0;
		int crCount = 0;
		int first = 0;
		while( instream.available() > 0 ) {
			int b = instream.read();
			if(b == 10) lfCount++;
			if(b == 13) crCount++;
			if( first == 0 && (b == 10 || b == 13)) first = b;
		}
		LineSeparator sep = LineSeparator.UNDEFINED;
		if(lfCount > crCount ) {
			sep = LineSeparator.LF;
		} else if( crCount > lfCount ) {
			sep = LineSeparator.CR;
		} else if( first == 13 ) {
			sep = LineSeparator.CRLF;
		} else if( first == 10 ) {
			sep = LineSeparator.LFCR;
		}
		return new LineCount(sep, Math.max(lfCount, crCount) + 1);
	}
	
	/**
	 * Find the position of the first illegal character for the present
	 * encoding (if any) and write a suitable message to the message sink.
	 * 
	 * @param text to be scanned for illegal characters
	 * @param offset where to start scanning
	 * @param length how many characters to scan
	 * @param encoder encoder to be used for testing
	 * @param firstLine number of first line
	 * @param messageBox where to write the message to
	 * @param localizer to be used for translations
	 */
	public static void reportIllegalCharacter(char[] text, int offset, int length, CharsetEncoder encoder, int firstLine, MessageBox messageBox, Localizer localizer) {
		final char NULLCHAR  = '\u0000';
		int index = offset;
		int lfCount = 0;
		int crCount = 0;
		char highSurrogateChar = NULLCHAR;
		while( index < length ) {
			char currChar = text[index];
			// count lines
			if(currChar == '\r') crCount++;
			if(currChar == '\n') lfCount++;
			// is the present character writable (excluding surrogates)
			if( (! encoder.canEncode( currChar )) ||  highSurrogateChar != NULLCHAR ) {
				if( Character.isHighSurrogate(currChar)) {
					highSurrogateChar = currChar;
					index++;
					continue;
				} 
				if(Character.isLowSurrogate(currChar)) {
					if( highSurrogateChar != NULLCHAR 
							&& encoder.canEncode( String.valueOf( new char[]{highSurrogateChar, currChar }) )) {
						highSurrogateChar = NULLCHAR;
						index++;
						continue;
					}
				}
				if( highSurrogateChar != NULLCHAR ) {
					currChar = highSurrogateChar;
					index--;
				}
				int start = Math.max(0, index-5);
				int end = Math.min(index + 4, offset + length - 1);
				for(int i = start; i < index; i++) {
					if( Character.isWhitespace( text[i] )) start = i + 1;
				}
				for(int i = end; i > index; i--) {
					if( Character.isWhitespace( text[i] )) end = i - 1;
				}
				StringBuilder context = new StringBuilder( flatWhiteSpace( String.valueOf(text, start, end - start + 1) ) );
				if( context.length() > 1 ) {
					if( start > 0 && ! Character.isWhitespace( text[start-1] )) context.insert(0, "...");
					if( end < offset + length - 1 && ! Character.isWhitespace( text[end + 1] )) context.insert( context.length(), "...");
				} else if( context.length() == 1 ) {
					if( start > 0 ) context.insert(0, " ");
					if( end < offset + length - 1 ) context.insert( context.length(), " ");
				}
				int lineCount = firstLine + Math.max(lfCount, crCount);
				messageBox.error( localizer.localize("message.illegal-character",
									new Object[]{Character.valueOf(currChar), 
													context, 
													Integer.valueOf(lineCount)}) );
				break; // look no further
			}
			index++;
		}		
	}

	/**
	 * Replaces every sequence of whitespace characters
	 * with a single blank (" ").
	 * @param raw to be flattend
	 * @return raw string with linebreaks, tabs and ws sequeces transformed to single blank
	 */
	public static String flatWhiteSpace(String raw) {
		StringBuilder cleaned = new StringBuilder();
		boolean lastWasWhite = false;
		for(int i = 0; i < raw.length(); i++) {
			if( Character.isWhitespace( raw.charAt(i))) {
				if( ! lastWasWhite) cleaned.append(' ');
				lastWasWhite = true;
			} else {
				cleaned.append( raw.charAt(i));
				lastWasWhite = false;
			}
		}
		return cleaned.toString();
	}
	
	/**
	 * Returns true if the specified character is 
	 * a linefeed (LF) or a carriage return (CR).
	 * 
	 * @param c to be tested
	 * @return true if LF or CR
	 */
	public static boolean isLineBreakChar( char c ) {
		return c == '\r' || c == '\n';
	}
	
	/**
	 * Returns the supplied List as an array, and guarantees that the specified actual Charset is contained.
	 * @param availableCharsets list to be returned as array, actual Charset included
	 * @param actualCharset the one charset that must be included in the list
	 * @return charset list as array with actual charset included
	 */
	public static Charset[] getCharsetList(List<Charset> availableCharsets, Charset actualCharset) {
		if( actualCharset == null || availableCharsets.contains(actualCharset )) {
			return (Charset[]) availableCharsets.toArray(new Charset[availableCharsets.size()]);
		} else {
			List<Charset> resultList = new ArrayList<Charset>();
			resultList.addAll( availableCharsets );
			resultList.add( actualCharset );
			return (Charset[]) resultList.toArray(new Charset[resultList.size()]);
		}
	}

}
