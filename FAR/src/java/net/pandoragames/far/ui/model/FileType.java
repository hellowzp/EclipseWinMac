package net.pandoragames.far.ui.model;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Binds certain properties, such as encoding, to a category of files.
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
public class FileType {

	/**
	 * Should files of unknown mime type be treated as binary (or as plain text)?
	 */
	public static boolean TREAT_UNKNOWN_AS_BINARY = true;
	
	/**
	 * The names of the build in file types.
	 */
	public enum BUILDIN {
		FILE,
		BINARY,
		ARCHIVE,
		DOCUMENT,
		TEXT,
		SOURCE,
		TABLE,
		SGML,
		XML};
	
	/** To be accessed by inheriting classes only.*/
	protected static Map<String,FileType> fileTypes = new HashMap<String,FileType>();

	/** The supertype of all file types. It's child types are TEXT and BINARY. */
	public static final FileType FILE = new FileType();
	/** Supertype of all binary file types. */
	public static final FileType BINARY = new FileType(BUILDIN.BINARY.name(), FILE, "X-CP437");
	/** Binary file type for archives such as zip and tar.gz. */
	public static final FileType ARCHIVE = new FileType(BUILDIN.ARCHIVE.name(), BINARY, null);
	/** File type for binary documents such as PDF, odf and docx. */
	public static final FileType DOCUMENT = new FileType(BUILDIN.DOCUMENT.name(), BINARY, null);
	/** Supertype of all text file types. */
	public static final FileType TEXT = new FileType(BUILDIN.TEXT.name(), FILE, null);
	/** Tabular text data such as tab and csv. */
	public static final FileType TABLE = new FileType(BUILDIN.TABLE.name(), TEXT, null);
	/** Source code text files. */
	public static final FileType SOURCE = new FileType(BUILDIN.SOURCE.name(), TEXT, null);
	/** SGML file types such as html. */
	public static final FileType SGML = new FileType(BUILDIN.SGML.name(), TEXT, null);
	/** XML base type. */
	public static final FileType XML = new FileType(BUILDIN.XML.name(), SGML, "UTF-8");
	
	
	private FileType parent;
	private String name;
	private Charset characterSet;
	
	private FileType() {
		name = BUILDIN.FILE.name();
		fileTypes.put(name, this);
	}
	
	private FileType(String name, FileType parent, String charset) {
		this(name, parent);
		if( charset != null) {
			try {
				characterSet = Charset.forName(charset);
			} catch(UnsupportedCharsetException ucx) {
				characterSet = Charset.defaultCharset();
			}
		}
		fileTypes.put(this.name, this);
	}
	
	/**
	 * Every file type has a name. 
	 * If parent is not specified, the file type will be a child of the common super type (FILE).
	 * @param name file type name
	 * @param parent file type parent
	 */
	public FileType(String name, FileType parent) {
		if( name == null || name.trim().length() == 0 ) throw new NullPointerException("Name of file type must not be empty (" + name + ")");
		this.name = name.trim();
		this.parent = (parent == null ? FILE : parent);
	}
	
	
	/**
	 * Returns the specified type by name if it does exist.
	 * The file type name is case sensitive.
	 * @param name file type name to look up
	 * @return file type or null
	 */
	public static FileType getType(String name) {
		return fileTypes.get(name);
	}
	
	/**
	 * Returns the file type name.
	 * @return file type name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the parent type.
	 * @return parent type
	 */
	public FileType getParentType() {
		return parent;
	}
	
	/**
	 * Sets the specified FileType as new parent type of this type.
	 * Checks for null and circular dependencies.
	 * @param newParent to be set, must not be null
	 * @throws NullPointerException if specified parent is null
	 * @throws IllegalArgumentException if specified parent is a descendant of this FileType
	 */
	public void setParentType(FileType newParent) {
		if( newParent == null ) throw new NullPointerException("Parent file type can not be null");
		FileType ancestor = newParent;
		while( ancestor != null ) {
			if( ancestor.equals( this )) throw new IllegalArgumentException("Atempt to define circular parent-child relation for file type " + name);
			ancestor = ancestor.getParentType();
		}
		parent = newParent;
	}
	
	/**
	 * Returns the associated character set.
	 * If no character set has been associated with <i>this</i>
	 * file type, the character set of the parent type will be returned.
	 * @return associated character set
	 */
	public Charset getCharacterset()
	{
		if( characterSet != null) {
			return characterSet;
		} else if ( parent != null ) {
			return parent.getCharacterset();
		} else {
			return Charset.defaultCharset();
		}
	}
	
	/**
	 * Defines the character set for this file type or removes
	 * the definition (if the parameter is null).
	 * @param charset to be associated with this file type or null.
	 */
	public void setCharacterset(Charset charset) {
		characterSet = charset;
	}
	
	/**
	 * Returns true if the character set associate with this file type is
	 * specifically defined for this file type. Returns false if this file
	 * type uses the character set of its parent.
	 * @return true if associated character set is defined on this file type
	 */
	public boolean isCharsetDefined() {
		return characterSet != null;
	}	
	
	/**
	 * Two file types are considered equal if they have the same name.
	 */
	public boolean equals(Object o) {
		if( o == null ) return false;
		try {
			FileType other = (FileType) o;
			return name.equals(other.name);
		} catch(ClassCastException ccx) {
			return false;
		}
	}

	/**
	 * Hashcode for this FileType.
	 */
	public int hashCode() {
		return name.hashCode();
	}
	
	/**
	 * Gibt "FileType:<Typ>" zurück.
	 */
	public String toString() {
		return this.getClass().getSimpleName() + ":" + name;
	}

	/**
	 * Returns true if this Mime type denotes a binary file.
	 * @return true if binary file type
	 */
	public boolean isBinary() {
		FileType toTest = this;
		while( toTest != null ) {
			if (FileType.BINARY.equals(toTest)) return true;
			if (FileType.TEXT.equals(toTest)) return false;
			toTest = toTest.getParentType();
		}
		return false;
	}
	
	/**
	 * Returns true if this Mime type denotes a text file.
	 * @return true if text file type
	 */
	public boolean isText() {
		FileType toTest = this;
		while( toTest != null ) {
			if (FileType.TEXT.equals(toTest)) return true;
			if (FileType.BINARY.equals(toTest)) return false;
			toTest = toTest.getParentType();
		}
		return false;
	}

}
