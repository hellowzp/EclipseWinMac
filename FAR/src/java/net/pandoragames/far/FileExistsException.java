package net.pandoragames.far;

import java.io.File;
import java.io.IOException;

/**
 * Thrown during a renaming operation if the target file already exists.
 * @author Olivier Wehner at 22.02.2008
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
public class FileExistsException extends IOException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Specify target file name and directory.
	 * @param name target file name
	 * @param dir directory
	 */
	public FileExistsException(String name, File dir)
	{
		super("A file with the name " + name + " already exists in " + dir.getPath());
	}

}
