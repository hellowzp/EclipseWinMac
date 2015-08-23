package net.pandoragames.far.ui;

import java.io.File;

/**
 * A callback handler used by the 
 * {@link UIFace#rename(java.util.List, OverwriteFileCallback) UIFace.rename()}
 * operation.
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
public interface OverwriteFileCallback {

	
	/**
	 * Called to request user feedback. Implementations should display a question like
	 * "A file called 'toBeReplaced' already exists. Do you want to replace it?" to the user.
	 * @param toBeReplaced a file carrying the same name as the file currently to be renamed
	 * @return true if the file should be replaced, false otherwise
	 */
	public boolean askForOverwrite(File toBeReplaced);
}
