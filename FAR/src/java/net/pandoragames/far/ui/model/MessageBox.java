package net.pandoragames.far.ui.model;

/**
 * Interface for a message sink.
 * @author Olivier Wehner at 26.02.2008
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
public interface MessageBox
{
	
	/**
	 * Consumes an error message.
	 * @param message error message
	 */
	public void error(String message);
	
	/**
	 * Consumes a normal message.
	 * @param message normal message
	 */
	public void info(String message);
	
	/**
	 * Clears all messages.
	 */
	public void clear();
}
