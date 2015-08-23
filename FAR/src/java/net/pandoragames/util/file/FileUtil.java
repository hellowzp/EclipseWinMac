package net.pandoragames.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A small collection of useful helper methods.
 *
 * @author Olivier Wehner
 * <!--
 *  FAR - Find And Replace
 *  Copyright (C) 2009,  Olivier Wehner

 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  -->
 */
public class FileUtil {

	/**
	 * Copies source to sink. Both files must exist beforehand.
	 * @param source data to copy
	 * @param sink file to copy to
	 * @throws IOException if copy operation failed
	 */
	public static void copy(File source, File sink) throws IOException {
		if( source == null ) throw new NullPointerException("Source file must not be null");
		if( sink == null ) throw new NullPointerException("Target file must not be null");
		if( ! source.exists() ) throw new IOException( "Source file " + source.getPath() + " does not exist");
		if( ! source.isFile() ) throw new IOException( "Source file " + source.getPath() + " is not a regular file");
		if( ! source.canRead() ) throw new IOException( "Source file " + source.getPath() + " can not be read (missing acces right)");
		if( ! sink.exists() ) throw new IOException( "Target file " + sink.getPath() + " does not exist");
		if( ! sink.isFile() ) throw new IOException( "Target file " + sink.getPath() + " is not a regular file");
		if( ! sink.canWrite() ) throw new IOException( "Target file " + sink.getPath() + " is write protected");
		InputStream input = null;
		OutputStream output = null;
		try{
			input = new FileInputStream( source );
			output = new FileOutputStream( sink );
			byte[] buffer = new byte[1024];
			while( input.available() > 0 ) {
				int bread = input.read( buffer );
				if( bread > 0 ) output.write(buffer, 0, bread);
			}
		} finally {
			if( input != null ) try { input.close(); } catch(IOException x) { /* ignore me*/ }
			if( output != null ) try { output.close(); } catch(IOException x) { /* ignore me*/ }
		}
	}
	
	/**
	 * Returns true if the second directory argument is indeed a subdirectory
	 * of the first directory argument.
	 * Returns allways true if ancestor is null. 
	 * Returns allways false if subdirectory is null.
	 * @param ancestor base directory
	 * @param subdirectory to be tested
	 * @return true if "ancestor" is indeed an ancestor directory of "subdirectory"
	 */
	public static boolean isSubdirectory(File ancestor, File subdirectory) {
        if( ancestor == null ) return true;
        
        if(subdirectory == null) return false;
        
        if(ancestor.equals(subdirectory)) return true;
        
        return isSubdirectory(ancestor, subdirectory.getParentFile());

	}
	
	/**
	 * Returns fullPath relative to base if base is indeed a sudirectory of fullpath.
	 * Returns fullPath unchanged othewise.
	 * @param base base directory
	 * @param fullPath to be expressed as a relative path
	 * @return relative path if base is a subdirectory of fullpath
	 */
	public static String getRelativePath(String base, String fullPath) {
		if( fullPath == null ) return null;
		if( base == null || ! fullPath.startsWith( base ) ) {
			return fullPath; 
		}
		String result = fullPath.substring( base.length() );
		if( result.length() == 0 ) {
			return ".";
		} else if( result.startsWith( File.separator )) {
			return result.substring( 1 ); // ommit the leading slash
		} else {
			return result;
		}

	}
}
