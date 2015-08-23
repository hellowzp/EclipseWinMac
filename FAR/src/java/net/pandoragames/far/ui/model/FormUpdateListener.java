package net.pandoragames.far.ui.model;

import java.util.EventListener;

/**
 * Allows to register with a {@link OperationForm OperationForm}
 * to get notified about form udpates. Changes of single properties
 * by means of ordinary getter calls are not counted as "form updates".
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
public interface FormUpdateListener extends EventListener {

	/**
	 * Called when the form is updated with a new form.
	 * @param event holding the updated form as source
	 */
	public void formUpdated( FormUpdateEvent event );
}
