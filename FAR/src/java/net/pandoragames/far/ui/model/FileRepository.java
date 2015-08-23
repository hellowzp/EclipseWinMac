package net.pandoragames.far.ui.model;

import java.io.File;

/**
 * A wrapper for a directory. Used together with a file chooser dialogue
 * to add a check if the selected directory is accepted.
 * @author Olivier Wehner at 22.03.2008
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
public interface FileRepository
{
	/**
	 * Returns a file 
	 * @return non null file
	 */
	public abstract File getFile();
	
	/**
	 * Sets a new file and returns true if the file was accepted. 
	 * Null will be ignored, the method returning false.
	 * @param file file object
	 * @return true if the file was acccepted, false otherwise
	 */
	public abstract boolean setFile(File file);
	
}
