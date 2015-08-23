package net.pandoragames.far.ui;

import java.util.Date;

/**
 * Aggregates DirectoryFilter and FileNameFilter.
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
public interface FileFilter extends DirectoryFilter, FileNameFilter {

	/**
	 * May return a non null Date to request files modified after 
	 * the specified date.
	 * @return minimum modified time stamp or null
	 */
	public Date getAfter();
	
	/**
	 * May return a non null Date to request files modified before 
	 * the specified date.
	 * @return maximum modified time stamp or null
	 */
	public Date getBefore();
	
}
