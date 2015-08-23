package net.pandoragames.far.ui.model;

import java.beans.DefaultPersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Standard FormLoader implementation, uses standard java
 * xml serialisation mechanism.
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
public class StandardFormLoader implements FormLoader {

	/**
	 * {@inheritDoc}
	 */
	public FindForm loadForm(InputStream input) 
	 throws IOException {
		XMLDecoder decoder = new XMLDecoder( input );
		return (FindForm) decoder.readObject();
	}

	/**
	 * {@inheritDoc}
	 */
	public void saveForm(FindForm form, OutputStream output)
	 throws IOException {
    	XMLEncoder xmlEncoder = new XMLEncoder( output );
    	xmlEncoder.setPersistenceDelegate( File.class, 
				  new DefaultPersistenceDelegate(new String[]{"path"}));
    	xmlEncoder.writeObject( form );
	}

}
