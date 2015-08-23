package net.pandoragames.far.ui;

import java.util.regex.Pattern;

import net.pandoragames.far.PatternException;
import net.pandoragames.far.PatternFilter;
import net.pandoragames.far.ui.model.FileNamePattern;

/**
 * Data for an abstract file name filter.
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
public interface FileNameFilter extends Cloneable {

	/**
	 * Returns a PatternFilter for this FileNameFilter. If the currently specified file pattern
	 * is valid, the filter will accept only files that comply with this pattern. 
	 * If the currently specified file pattern
	 * is <i>not</i> valid, an accept all filter will be returned.
	 * @return PatternFilter for this FileNameFilter if file name pattern is valid, accept all 
	 * filter otherwise.
	 */
	public PatternFilter createPatternFilter();
	
	/**
	 * Returns the file name pattern that should be used to filter files.
	 * @return file name pattern
	 */
	public FileNamePattern getFileNamePattern();

	/**
	 * Returns a copy of this object, with all interface method behaving exactly as the original instance.
	 * @return copy of this object.
	 */
	public Object clone();

}
