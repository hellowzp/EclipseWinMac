package net.pandoragames.far.ui.model;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Data container for the files in a result list. Files in that list may have errors, be subject to 
 * name changes and selection or be excluded from a subset.  
 * @author Olivier Wehner at 26.02.2008
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
public class TargetFile implements MessageBox, Cloneable
{
	private boolean selected = true;
	private boolean included = true;
	private File file;
	private String newName;
	private String infoMessage;
	private String errorMessage;
	private Charset characterSet;
	// partly computed properties
	private MimeType mimeType;
	private String extension;
	
	/**
	 * Constructor requires a non null file object.
	 * @param file not null
	 */
	public TargetFile(File file) {
		if( file == null) throw new NullPointerException("File must not be null");
		this.file = file;
	}
		
	/**
	 * Returns the file name. This is a read only property.
	 * @return file name
	 */
	public String getName() {
		return file.getName();
	}
	
	/**
	 * Returns the files directory path. This is a read only property.
	 * @return directory path
	 */
	public String getPath() {
		return file.getParentFile().getPath();
	}
	/**
	 * Returns the error message if any.
	 * @return error message or null
	 */
	public String getErrorMessage()
	{
		return errorMessage;
	}
	/**
	 * Returns the info message if any.
	 * @return informative message or null
	 */
	public String getInfoMessage()
	{
		return infoMessage;
	}
	/**
	 * Sets an error message for this file.
	 * @param errorMessage error message
	 */
	public void error(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}
	/**
	 * Sets an info message for this file.
	 * @param message informative message
	 */
	public void info(String message) {
		infoMessage = message;
	}
	/**
	 * Clears all messages.
	 */
	public void clear() {
		errorMessage = null;
		infoMessage = null;
		newName = null;
	}
	/**
	 * Returns the underlying <code>java.io.File</code> object.
	 * @return underlying <code>File</code> object
	 */
	public File getFile()
	{
		return file;
	}
	/**
	 * Returns true (the default) if this file object has bee selected.
	 * @return true if selected
	 */
	public boolean isSelected()
	{
		return selected;
	}
	/**
	 * Selects or deselects this object. By dafault a TargetFile is selected.
	 * @param select true if this file object should be selected.
	 */
	public void setSelected(boolean select)
	{
		this.selected = select;
	}
	/**
	 * Returns a planned new name if any was set.
	 * @return new name property
	 */
	public String getNewName()
	{
		return newName;
	}
	/**
	 * Sets the new name property of this object.
	 * This has no direct programatic consequences.
	 * @param newName property value
	 */
	public void setNewName(String newName)
	{
		this.newName = newName;
	}
	/**
	 * Returns true if this file should be considdered part of 
	 * a subset in the same collection. Returns true by default.
	 * @return true if part of subset
	 */
	public boolean isIncluded()
	{
		return included;
	}
	/**
	 * Includes or excludes this object in a subset of the same
	 * collection this instance was taken from. 
	 * @param included set to false to excluide
	 */
	public void setIncluded(boolean included)
	{
		this.included = included;
	}
	
	/**
	 * Returns the character set of this file, if any was specified.
	 * Returns null if the default character set should be used.
	 * @return character set or null
	 */
	public Charset getDeclaredCharacterset()
	{
		return characterSet;
	}
	
	/**
	 * Returns the character set that should be used to read this file.
	 * If this file has been associated with a particular character set, that one is returned.
	 * Otherwise, if this file is of a specific mime type, the character set for that mime type is returned.
	 * Otherwise the default character set for TEXT or BINARY files is returned, depending on 
	 * the value of the flag {@link net.pandoragames.far.ui.model.FileType#TREAT_UNKNOWN_AS_BINARY FileType.TREAT_UNKNOWN_AS_BINARY}.
	 * @return character set that should be used to read this file, never null
	 */
	public Charset getCharacterset() {
		if( characterSet != null ) {
			return characterSet;
		}
		MimeType mime = getMimeType();
		if( mime != null ) {
			return mime.getCharacterset();
		} else if(FileType.TREAT_UNKNOWN_AS_BINARY) {
			return FileType.BINARY.getCharacterset();
		} else {
			return FileType.TEXT.getCharacterset();
		}
	}
	
	/**
	 * Specifies a character set for this file.
	 */
	public void setCharacterset(Charset charaset)
	{
		characterSet = charaset;
	}

	/**
	 * Returns true if the character set associate with this file is
	 * specifically defined for this file. Returns false if this file
	 * uses the default character set or the characterset of its mime type.
	 * @return true if associated character set is defined on this file
	 */
	public boolean isCharsetDefined() {
		return characterSet != null;
	}	
	
	/**
	 * Returns the mime type of this file if defined.
	 * Mime type are usually assigned via lookup of the file name extension in
	 * the mime registry. 
	 * @return MimeType or null if unefined
	 */
	public MimeType getMimeType() {
		if( mimeType == null && hasExtension()) {
			return MimeType.MimeRegistry.findMimeType(getFileExtension());
		} else {
			return mimeType;
		}
	}
	
	/**
	 * Returns true if this TargetFile has an assigned mime type 
	 * that represents a binary file type, or if the it has no 
	 * assigned mime type at all and the flag {@link net.pandoragames.far.ui.model.FileType#TREAT_UNKNOWN_AS_BINARY FileType.TREAT_UNKNOWN_AS_BINARY} is true.
	 * @return true if this TargetFile is a binary file (or should be treated as one)
	 */
	public boolean isBinaryFile() {
		// TODO: add some caching
		MimeType mime = getMimeType();
		if( mime != null ) {
			return mime.isBinary();
		} else {
			return FileType.TREAT_UNKNOWN_AS_BINARY;
		}
	}
	
	/**
	 * Returns the fileExtension as a non null String, separating dot included.
	 * @return file extension with leading dot or empty string
	 */
	public String getFileExtension() {
		if( extension == null ) {
			int dot = file.getName().indexOf('.');
			if( dot < 1 ) {
				extension = "";
			} else {
				extension = file.getName().substring(dot);					
				int last = file.getName().lastIndexOf('.');
				if( dot != last ) {
					// find longest extension with associated mime type
					extension = longestExtension(file.getName(), last);
				}
			}
		}
		return extension;
	}
	
	private String longestExtension(String fileName, int lastDot) {
		int second = fileName.lastIndexOf('.', lastDot-1);
		// dot at first char or no dot at all
		if( second < 1 ) return fileName.substring( lastDot );
		// else, another dot
		String ext = fileName.substring( second );
		if( MimeType.MimeRegistry.findMimeType(ext) != null ) {
			// try next
			return longestExtension( fileName, second );
		} else {
			// no match, use last
			return fileName.substring( lastDot );
		}
	}
	
	/**
	 * Returns true if the the underlying file object has an extension.
	 * @return true if file name with extension.
	 */
	public boolean hasExtension() {
		return extension != null || file.getName().indexOf('.') > 0;
	}
	
	/**
	 * Explicitely sets a mime type for this file.
	 * This overrides the definitions in the mime registry.
	 * @param mimeType mime type
	 */
	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}
	
	/**
	 * Returns a copy of this object.
	 * @see java.lang.Object#clone()
	 */
	public TargetFile clone() {
		try {
			return (TargetFile) super.clone();
		} catch(CloneNotSupportedException cnsx) {
			throw new IllegalStateException("A clown refused to clone!");
		}
	}	

	/**
	 * Returns a copy of this object but with the underlying file object
	 * replaced with the specified file object. 
	 * All other fields are copied as is or merged if necessary.
	 * @return copy of this object with file object replaced
	 */
	public TargetFile merge(File file) {
		TargetFile copy = clone();
		copy.file = file;
		if( copy.extension != null && ! file.getName().endsWith( copy.extension ))  copy.extension = null;
		return copy;
	}	

	/**
	 * Two target files are equal if the wraped files are equal.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if( o == null ) return false;
		try {
			TargetFile other = (TargetFile) o;
			return this.file.equals( other.file );
		} catch(ClassCastException ccx) {
			return false;
		}
	}
		
	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return file.hashCode();
	}
	
}
