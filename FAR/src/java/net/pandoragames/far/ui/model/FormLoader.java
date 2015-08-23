package net.pandoragames.far.ui.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for saving and loading a particular search configuration.
 * Implementing classes controle the way the form object is persisted.
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
public interface FormLoader
{
	/**
	 * Loads the form from a stream.
	 * @param input where to read from
	 * @return FindForm data model
	 */
	public FindForm loadForm(InputStream input) throws IOException;
	
	/**
	 * Writes the form to a stream.
	 * @param form to be saved
	 * @param output where to write to
	 */
	public void saveForm(FindForm form, OutputStream output) throws IOException;
}
