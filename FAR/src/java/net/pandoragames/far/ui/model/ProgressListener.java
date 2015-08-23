package net.pandoragames.far.ui.model;

/**
 * Progress listener. Receives notifications about the progress of 
 * searches or find-and-replace operations. Implicitely there are three
 * types of operations:
 * <ul>
 * <li>Simple Find: Lookup for file names that match a particular pattern.
 * <li>Content Find: Allways preceeded by a simple find, this operation will
 * in addition match the content of the fieles in the result set of the previous operations
 * agains some (regular) expression.
 * <li>Find And Replace: Always preceeded by simple find and content find. 
 * </ul>
 * @author Olivier Wehner at 27.02.2008
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
public interface ProgressListener
{
	
	/**
	 * Gets notified when an operation has been started. 
	 * @param type type of operation that has been started
	 */
	public void operationStarted( OperationType type );
	
	/**
	 * Receives progress informations. The total count parameter can not
	 * be provided for simple find operations and will thus be minus one.
	 * @param count progress count
	 * @param total total count to be attained or -1 if unknown
	 * @param type type of operation that is progressing
	 */
	public void operationProgressed( int count, int total,  OperationType type  );
	
	/**
	 * Notified when the operation has terminated.
	 * @param type type of operation that has terminated
	 */
	public void operationTerminated( OperationType type );
	
	/**
	 * Notified when the operation has been aborted.
	 * @param type type of operation that has been aborted
	 */
	public void operationAborted( OperationType type );
}
