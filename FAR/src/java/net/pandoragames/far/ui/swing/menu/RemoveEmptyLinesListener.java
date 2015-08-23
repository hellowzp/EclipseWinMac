package net.pandoragames.far.ui.swing.menu;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.pandoragames.far.ui.model.CharacterUtil;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.far.ui.swing.component.listener.AbstractFileUpdaterListener;
import net.pandoragames.util.i18n.Localizer;

/**
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
class RemoveEmptyLinesListener extends AbstractFileUpdaterListener {
	private int blankLineCount;
	public RemoveEmptyLinesListener(FileSetTableModel model, Localizer localizer) {
		super(model, localizer);
	}
	protected boolean executeForUpdate(TargetFile targetFile, File tempfile) throws IOException {
		blankLineCount = 0;
		int[] sepp = null;
		int currentChar = 0;
		int lastChar = 0;
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream( targetFile.getFile() );
			output = new BufferedOutputStream( new FileOutputStream( tempfile ) );
			
			byte[] buffer = new byte[1024];
			int count = 0;
			
			// read leading whitespace
			do {
				lastChar = currentChar;
				currentChar = input.read();
				if( currentChar < 0 ) break;
				if( isWhite( (char) currentChar )) {
					buffer[count] = (byte) currentChar;
					count++;
				}
			} while( currentChar >= 0 && isWhite( (char) currentChar ) && count < buffer.length);
			
			if( currentChar < 0 ) {
				// EOF on first blank line
				blankLineCount++;
				return true;
			} else if( ! CharacterUtil.isLineBreakChar( (char) currentChar ) ) {
				// first line not blank
				output.write( buffer, 0, count );
				// read rest of first line
				do {
					output.write( currentChar );
					lastChar = currentChar;
					currentChar = input.read();
				} while( currentChar >= 0 && ! CharacterUtil.isLineBreakChar( (char) lastChar ));
			} else {
				// first line IS blank
				blankLineCount++;
				lastChar = currentChar;
				currentChar = input.read();				
			}
			// reset buffer anyway
			count = 0;
			
			// EOF directly after first line break
			if( currentChar < 0 ) return true;
			
			// identify the line break
			if( lastChar != currentChar && CharacterUtil.isLineBreakChar( (char) currentChar )) {
				sepp = new int[]{lastChar, currentChar};
			} else {
				sepp = new int[]{lastChar};
				if(CharacterUtil.isLineBreakChar( (char) currentChar )) {
					blankLineCount++;
				} else {
					output.write( currentChar );
				}
			}
			
			// the rest
			do{
				count = readLine( buffer, input, sepp);
				if( count > 0 ) {
					output.write(buffer, 0, count);
				} else if( count == 0 ) {
					blankLineCount++;
				}
			} while( count >= 0 );		
			
			output.flush();
		} finally {
			if( input != null ) try { input.close(); } catch(IOException x) { /* ignore me*/ }
			if( output != null ) try { output.close(); } catch(IOException x) { /* ignore me*/ }
		}
		return true;		
	}
	private int readLine(byte[] buffer, InputStream input, int[] sepp) throws IOException {
		boolean empty = true;
		int index = 0;
		int c = input.read();
		// index < buffer.length-1 : leave one byte space for two byte line separator
		while( c >= 0 && index < buffer.length-1) {
			buffer[ index ] = (byte) c;
			index++;
			// end of line reached ? 
			if(c == sepp[0]) {
				if(sepp.length == 2) {
					c = input.read();
					buffer[index] = (byte) sepp[1];
					index++;
				}
				break;
			} else if( ! isWhite( (char) c ) && ! CharacterUtil.isLineBreakChar( (char) c ) ) { // whitespace
				empty = false;
			}
			c = input.read();
		}
		if( c < 0 ) {
			return -1;
		} else if( empty ) {
			return 0;
		} else {
			return index;
		}
	}
	private boolean isWhite(int c) {
		return c == ' ' || c == '\t';
	}
	protected void success(TargetFile targetFile) {
		targetFile.info( localizer.localize("message.n-lines-removed", Integer.valueOf(blankLineCount)) );
		logger.info(blankLineCount + " lines removed in " + targetFile.getFile().getPath());
	}
}

