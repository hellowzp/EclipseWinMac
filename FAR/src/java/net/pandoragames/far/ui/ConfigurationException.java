package net.pandoragames.far.ui;

/**
 * Signalls a <i>fatal</i> error during startup or later. The application must not
 * continue to work if a ConfigurationException was thrown.
 * @author Olivier Wehner at 25.02.2008
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
public class ConfigurationException extends RuntimeException
{
	/**
	 * Always supply a useful message.
	 * @param message always supply a useful message
	 */
	public ConfigurationException(String message) {
		super( message );
	}
	
	/**
	 * Wrapper constructor.
	 * @param message always supply a useful message
	 * @param cause wrapped exception
	 */
	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
