package net.pandoragames.far.ui.swing.menu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import java.util.Arrays;

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
class ChangeCharsetListener extends AbstractFileUpdaterListener {
	private Charset encoding;
	public ChangeCharsetListener(Charset charset, FileSetTableModel model, Localizer loco) {
		super( model, loco );
		encoding = charset;
	}
	protected boolean executeForUpdate(TargetFile targetFile, File tempfile) throws IOException {
		final char NULLCHAR = '\u0000';
		File source = targetFile.getFile();
		Reader input = null;
		Writer output = null;
		Charset fileEncoding = targetFile.getCharacterset();
		try{
			final int BUFSIZ = 1024;
			input = new InputStreamReader(new FileInputStream( source ), fileEncoding.newDecoder());
			output = new OutputStreamWriter(new FileOutputStream( tempfile ), encoding.newEncoder());
			char[] buffer = new char[BUFSIZ];
			int charsread = 0;
			String context = "";
			int lineCount = 1;
			do {
				Arrays.fill(buffer, NULLCHAR);
				try{
					charsread = input.read(buffer, 0, BUFSIZ);
				}catch(MalformedInputException mix) {
					// charsread not reliable at this point
					charsread = 0;
					for(; charsread < BUFSIZ; charsread++) {
						if(buffer[charsread] == NULLCHAR) break;
					}
					if( charsread > 1 ) context = getContext(buffer, charsread );
					Integer lineNumber = Integer.valueOf( lineCount + CharacterUtil.countLinebreaks(buffer, 0, charsread) );
					targetFile.error( localizer.localize("message.illegal-byte-sequence", new Object[]{ context, lineNumber} ) );	
					return false;
				}
				if( charsread > 0 ) {
					try{
						output.write(buffer, 0, charsread);
					}catch(UnmappableCharacterException ucx) {
						if( charsread < 3 && context.length() > 0) {
							String expandedContext = context + String.valueOf(buffer, 0, charsread);
							CharacterUtil.reportIllegalCharacter(expandedContext.toCharArray(), context.length(), charsread, encoding.newEncoder(), lineCount, targetFile, localizer);
						} else {
							CharacterUtil.reportIllegalCharacter(buffer, 0, charsread, encoding.newEncoder(), lineCount, targetFile, localizer);
						}
						return false;						
					}
					context = getContext(buffer, charsread );
					lineCount = lineCount + CharacterUtil.countLinebreaks(buffer, 0, charsread);
				}
			} while( charsread > 0 );
		} finally {
			if( input != null ) try { input.close(); } catch(IOException x) { /* ignore me*/ }
			if( output != null ) try { output.close(); } catch(IOException x) { /* ignore me*/ }
		}
		return true;
	}
	protected void success(TargetFile targetFile) {
		targetFile.setCharacterset( encoding );
		targetFile.info( encoding.displayName() );
		logger.info("Changed character set for " + targetFile.getFile().getPath() + " to " + encoding.displayName());
	}
	private String getContext(char[] buffer, int pos) {
		if( pos < 1 ) return "";
		int rawLength = Math.min(pos, 8);
		String raw = String.valueOf(buffer, pos - rawLength, rawLength);
		return CharacterUtil.flatWhiteSpace(raw);
	}	
}
