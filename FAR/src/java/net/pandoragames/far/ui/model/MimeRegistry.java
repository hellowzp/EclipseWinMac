package net.pandoragames.far.ui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A registry for mime-type to file extension bindings. 
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
public abstract class MimeRegistry {

	/** Extension to MimeType Map. */
	protected Map<String, MimeType> extensionRegistry = new HashMap<String,MimeType>();

	/**
	 * Adds the specified mimetType to the mime registry.
	 * @param mimeType to be registered
	 */
	protected abstract void registerMimeType(MimeType mimeType);
	
	/**
	 * Removes a particular mime type from this registry.
	 * @param mime to be removed
	 */
	public abstract void remove(MimeType mime);
	
	/**
	 * Returns a mime type by name
	 * @param name to be looked up
	 * @return Mime type or null
	 */
	public abstract MimeType getType(String name);
	
	/**
	 * Returns a list of all registered mime types.
	 * @return comprehensive list of mime types
	 */
	public abstract List<MimeType> listAll();

	/**
	 * Finds the mime type for a given extension if any has been defined.
	 * @param fileExtension with, or without leading dot.
	 * @return associated mime type or null
	 */
	public MimeType findMimeType(String fileExtension) {
		String extension = MimeType.canonicExtension(fileExtension);
		if( extension.length() == 0 ) return null;
		return extensionRegistry.get(extension);
	}
	
	/**
	 * Creates a copy of this registry.
	 * @return copy of this registry.
	 */
	public MimeRegistry createShadowRegistry() {
		ShadowRegistry shadow = new ShadowRegistry();
		for(MimeType mimeType : listAll()) {
			shadow.registerMimeType(mimeType.copy(this));
		}
		return shadow;
	}
	
	/**
	 * Registers a file extension for some mime type. If the file extension
	 * has been registered previously for some other mime type, it will be removed first.
	 * @param extension to be registered
	 * @param mimeType type for wich to register the extension
	 * @return false if nothing has been registered
	 */
	protected boolean registerExtension(String extension, MimeType mimeType) {
		if( mimeType == null || extension == null ) return false;
		MimeType old = extensionRegistry.get(extension);
		if (old != null && ! mimeType.equals(old) ) {
			old.removeExtension(extension);
		}
		extensionRegistry.put(extension, mimeType);
		return true;
	}

	/**
	 * Removes a previously associated file extension.
	 * File extensions should be specified with a leading dot, but a plain extension is accepted also.
	 * @param fileExtension with, or without leading dot.
	 */
	protected void removeExtension(String fileExtension) {
		if( fileExtension == null ) return;
		extensionRegistry.remove( fileExtension );	
	}
}

class MainRegistry extends MimeRegistry {
	
	public MimeType getType(String name) {
		FileType type = FileType.getType(name);
		if( type != null && type instanceof MimeType) {
			return ( MimeType ) type;
		} else {
			return null;
		}
	}

	public void registerMimeType(MimeType mimeType)
	{
		FileType.fileTypes.put(mimeType.getName(), mimeType);
		for(String extension : mimeType.listFileExtensions()) {
			registerExtension( extension, mimeType );
		}
	}
	
	public void remove(MimeType mime) {
		MimeType removed = (MimeType) FileType.fileTypes.remove(mime.getName()); 
		if( removed != null ) {
			for(String ext : removed.listFileExtensions()) {
				removeExtension(ext);
			}
		}
	}

	public List<MimeType> listAll() {
		List<MimeType> result = new ArrayList<MimeType>();
		for(FileType type : FileType.fileTypes.values()) {
			if( type instanceof MimeType ) {
				result.add( (MimeType) type );
			}
		}
		Collections.sort( result, new Comparator<MimeType>() {
			public int compare(MimeType a, MimeType b) {
				return b.getName().compareTo( a.getName() );
			}
		});
		return result;
	}

}

class ShadowRegistry extends MimeRegistry {
	private Map<String, MimeType> mimeRegistry = new HashMap<String,MimeType>();
	public MimeType getType(String name) {
		return mimeRegistry.get(name);
	}
	public void registerMimeType(MimeType mimeType)
	{
		mimeRegistry.put(mimeType.getName(), mimeType);
		for(String extension : mimeType.listFileExtensions()) {
			registerExtension( extension, mimeType );
		}
	}	
	public void remove(MimeType mime) {
		MimeType removed = (MimeType) mimeRegistry.remove(mime.getName()); 
		if( removed != null ) {
			for(String ext : removed.listFileExtensions()) {
				removeExtension(ext);
			}
		}
	}
	public List<MimeType> listAll() {
		return new ArrayList<MimeType>(mimeRegistry.values());
	}
}
