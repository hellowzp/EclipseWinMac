package net.pandoragames.far.ui;

import java.util.regex.Pattern;

/**
 * Data for a file filter that tests the file content.
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
public interface ContentFilter extends Cloneable {

	/**
	 * Returns the filter criteria as a java regex Pattern object.
	 * @return filter criteria
	 */
	public Pattern getContentPatternAsRegex();

	/**
	 * Returns the litteral content pattern. This pattern is looked up <i>within</i> the files.
	 * Its interpretation is implementation dependend. The method {@link #getContentPatternAsRegex() getContentPatternAsRegex()}
	 * should be called to retreive the actual regular expression pattern. However, if this method 
	 * (<code>getSearchStringContent()</code>) returns the empty string, then <code>getContentPatternAsRegex()</code>
	 * returns a Pattern that accepts any input (.*).
	 * @return content pattern, possibly empty, never null
	 */
	public String getSearchStringContent();
	
	/**
	 * Returns false if the content pattern is applied as an inclusion filter
	 * (the default). If the method returns true, files matching the
	 * pattern will be <b>excluded</b> from the selection.
	 * @return false to include files matching the content pattern, true to exclude them 
	 */
	public boolean isInvertContentFilter();

	/**
	 * Returns a copy of this object, with all interface method behaving exactly as the original instance.
	 * @return copy of this object.
	 */
	public Object clone();
}
