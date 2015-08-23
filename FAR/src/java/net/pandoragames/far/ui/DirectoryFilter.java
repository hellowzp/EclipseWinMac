package net.pandoragames.far.ui;

import java.io.File;
import java.util.regex.Pattern;

import net.pandoragames.far.ui.model.FileNamePattern;

/**
 * Data for a file filter that tests the files directory.
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
public interface DirectoryFilter {

	/**
	 * Returns the base directory. Only files in this directory
	 * (or below if the includeSubDirs flag is set) will pass the filter.
	 *  
	 * @return base directory
	 */
	public File getBaseDirectory();
	
	
	/**
	 * Should files in subdirectories of the base directory pass the filter?
	 * @return include subdirectories
	 */
	public boolean isIncludeSubDirs();

	/**
	 * Returns the maximal search depth. The default is 256.
	 * @return maximal search depth
	 */
	public int getMaxDepth();
	
	/**
	 * Returns the directory search pattern currently set or null. The pattern 
	 * is matched against every subdirectory beyond the base directory. Depending on the
	 * value of the exclude-matching-directories flag,
	 * matching directories will be excluded from search, or only files with matching ancestor directories
	 * will be included.
	 * @return directory name pattern
	 */
	public FileNamePattern getDirectoryPattern();
	
	/**
	 * Control flag for the {@link #getDirectoryPattern() directory pattern}. If this flag is set,
	 * directories matching the directory pattern will be excluded from the search. Otherwise, only files
	 * in or under a matching directory (i.e. with matching ancestor directory) will be included in the
	 * search result. If the directory pattern is not set (null), this flag has no effect.
	 * @return should directories matching the directory pattern be excluded
	 */
	public boolean isExcludeMatchingDirectories();

}
