package net.pandoragames.util.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
/**
 * Reads and writes lists of arbitrary objects. This class extends {@link ListFile ListFile} to
 * support serialisation and deserialisation in a user defined way. This is useful when 
 * lists of configuration data should be kept in a simple, human readable format.<p>
 * 
 * The format to be used for (de-) serialisation is defined by the actual implementation
 * of this class. This allows also the use of arbitrary constructors.<p>
 * 
 * You should sure consider the use of XML or the serialisation support from 
 * java.io package. 
 * @author Olivier Wehner at 30.03.2008
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
public abstract class ObjectListFile<T> extends ListFile
{
	/**
	 * Instantiate this class with the file that holds the data.
	 * @param file to be read or written.
	 */
	public ObjectListFile(File file) {
		super(file);
	}
	
	/**
	 * Loads and returns a list of objects from the file specified with the constructor.
	 * The list may be empty but will never be null, nor will it contain null entries.
	 * @see #parse(String)
	 * @return list of non null objects, possibly empty, never null
	 * @throws IOException if the file could not be read, or if the <code>parse</code>
	 * method threw an IOException
	 */
	public List<T> loadList() throws IOException {
		List<T> result = new ArrayList<T>();
		List<String> raw = load();
		for(String line : raw) {
			T t = parse( line );
			if( t != null) result.add( t );
		}
		return result;
	}
	
	/**
	 * Writes the list to the file specified with the constructor. null entries in the list will silently be ignored.
	 * If a non null header is given, it will be put as comment on top of the list.  
	 * @param list to be stored in the file
	 * @param header optional comment, may span multiple lines
	 * @throws IOException if the file could not be written.
	 */
	public void storeList(List<? extends T> list, String header) throws IOException {
		List<String> toBeWritten = new ArrayList<String>();
		if(header != null) {
			toBeWritten.add("##");
			StringTokenizer tokenizer = new StringTokenizer(header, "\n");
			while( tokenizer.hasMoreTokens() ){
				toBeWritten.add( "# " + tokenizer.nextToken() );				
			}
			toBeWritten.add("##");			
		}
		for(T t : list) {
			if(t != null) toBeWritten.add( format( t ) );
		}
		store( toBeWritten );
	}
	
	/**
	 * Implement this method to specify how the object should be formated as string. Note that the returned
	 * string must not start with a '#' hash character, as such lines will be interpreted as comments. 
	 * This method is called from method {@link #storeList(List, String) storeList()}. If it returns null, the respective
	 * object will be ignored and not be written to file.
	 * @param type to be formated
	 * @return formated object
	 */
	public abstract String format(T type);
	
	/**
	 * Implement this method to reconstruct an object from its serialised form. If no object can be constructed
	 * from the given String, you may either return null or throw an IOException. This method is called
	 * from {@link #loadList() loadList()}. If it returns null, the respective line will be ignored, if it throws an 
	 * IOException parsing of the file will stop, and <code>loadList()</code> will return the exception.
	 * @param string to be parsed
	 * @return parsed object or null
	 * @throws IOException if parsing should be interrupted
	 */
	public abstract T parse(String string) throws IOException;
}
