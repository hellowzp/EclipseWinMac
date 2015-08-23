package net.pandoragames.far.ui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.pandoragames.far.FileExistsException;

/**
 * Mime type definition for FAR. Adds association with 
 * file extensions to the FileType superclass.
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
public class MimeType extends FileType {
	
	public static final MimeRegistry MimeRegistry = new MainRegistry();
	
	private static final Pattern mimePattern = Pattern.compile("[a-z]+/[[a-z][0-9]\\-\\.\\+]+");

	private MimeRegistry registry = null;
	
	private List<String> fileExtensions = new ArrayList<String>();
	
	private boolean predefined = false;
	
	/**
	 * A mime type name must have the form &lt;media type&gt; "/" &lt;subtype&gt;.
	 * @param name a valid mime type name
	 * @param category parent type
	 */
	public MimeType(String name, FileType category, MimeRegistry registry) {
		super( name, category );
		if( name.indexOf('/') < 1) {
			throw new IllegalArgumentException("Not a proper mime type name: " + name);
		}
		this.registry = registry;
		registry.registerMimeType(this);
	}
	
	/**
	 * Returns the first part of the mime type name, the media type.
	 * @return media type
	 */
	public String getMediaType() {
		return getName().split("/")[0];
	}
	
	/**
	 * Returns the second part of the mime type name, the subtype.
	 * @return subtype
	 */
	public String getSubType() {
		return getName().split("/")[1];
	}
	
	/**
	 * Adds an entry to the list of associated file extensions.
	 * File extensions should be specified with a leading dot, but a plain extension is accepted also.
	 * @param fileExtension with, or without leading dot.
	 */
	public void addExtension(String fileExtension) {
		if( fileExtension == null ) throw new NullPointerException("File extension must not be null");
		String extension = fileExtension.trim();
		if( extension.length() == 0) throw new IllegalArgumentException("File extension must not be empty");
		extension = canonicExtension(extension);
		if( extension.length() == 0 ) throw new IllegalArgumentException("Illegal characters in file extension '" + fileExtension + "'");
		if( registry.registerExtension(extension, this) ) {
			fileExtensions.add( extension );			
		}
	}
	
	/**
	 * Removes a previously associated file extension.
	 * File extensions should be specified with a leading dot, but a plain extension is accepted also.
	 * @param fileExtension with, or without leading dot.
	 */
	public void removeExtension(String fileExtension) {
		String extension = canonicExtension(fileExtension);
		if( extension.length() == 0 ) return;
		if( fileExtensions.remove(extension)) {
			registry.removeExtension( extension );
		}
	}
	
	/**
	 * Returns the list of file extensions associated with this mime type.
	 * File extensions are returned with leading dot.
	 * @return list of associated file extensions, possibly empty.
	 */
	public List<String> listFileExtensions() {
		return new ArrayList( fileExtensions );
	}
	
	/**
	 * Returns true if the specified file extension is in the list of extensions
	 * associated with this mime type.
	 * File extensions should be specified with a leading dot, but a plain extension is accepted also.
	 * @param fileExtension with, or without leading dot.
	 * @return true if extension is associated with this mime type
	 */
	public boolean matchExtension(String fileExtension) {
		String extension = canonicExtension(fileExtension);
		if( extension.length() == 0 ) return false;
		return fileExtension.contains(extension);
	}
		
	/**
	 * Returns true if the specified mime type name is valid, 
	 * i.e. if it matches the pattern "[a-z]+/[[a-z][0-9]\\-\\.\\+]+".
	 * @param mime to be tested
	 * @return true if valid mime type name
	 */
	public static boolean isValidMimeIdentifier(String mime) {
		if( mime == null ) return false;
		return mimePattern.matcher(mime).matches();
	}
	

	/**
	 * Returns the number of registered MimeTypes.
	 * @return number of mime types
	 */
	public static int numberOfMimeTypes() {
		return fileTypes.size() - FileType.BUILDIN.values().length;
	}
	
	/**
	 * Returns the mime type name.
	 */
	public String toString() {
		return getName();
	}
	
	/**
	 * Returns true if this mime type is a predefined type.
	 * @return true if predefined by application
	 */
	public boolean isPredefined() {
		return predefined;
	}

	/**
	 * Marks this mime type as predefined.
	 * @param predefined true to define as predefined
	 */
	public void setPredefined(boolean predefined) {
		this.predefined = predefined;
	}

	/**
	 * Creates a copy of this mime type in the specified MimeRegistry.
	 * @param registry where to register the copy
	 * @return copy of this Mime type
	 */
	protected MimeType copy(MimeRegistry registry) {
		MimeType copy = new MimeType(getName(), getParentType(), registry);
		if( isCharsetDefined() ) copy.setCharacterset(getCharacterset());
		copy.fileExtensions.addAll( fileExtensions );
		copy.predefined = predefined;
		return copy;
	}
	
	/**
	 * Technical helper method for inheriting classes.
	 * Clears the internal list of file extensions but does not
	 * alter the mime registry.
	 */
	protected void clearInternalExtensionList() {
		fileExtensions.clear();
	}
	
	static String canonicExtension(String extension) {
		if( extension == null ) return "";
		String ext = extension.trim();
		if( ext.length() == 0 || (ext.length() == 1 && ext.charAt(0) == '.')) return "";
		return ext.charAt(0) == '.' ? ext.toLowerCase() : "." + ext.toLowerCase();
	}	

}
