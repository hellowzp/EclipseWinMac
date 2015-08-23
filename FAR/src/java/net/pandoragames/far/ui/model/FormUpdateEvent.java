package net.pandoragames.far.ui.model;

import java.util.EventObject;

/**
 * Event object for {@link FormUpdateListener FormUpdateListener}.
 * The updated form will be returned as "event source".
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
public class FormUpdateEvent extends EventObject {

	private OperationType formType;
	/**
	 * Constructor takes the form that has been updated.
	 * It will be returned as "event source".
	 * @param form
	 */
	public FormUpdateEvent(OperationForm form) {
		super(form);
		formType = form.getType();
	}
	
	/**
	 * Returns the form type. Shortcut for
	 * <code>((OperationForm) <b>this</b>.getSource()).getType()</code>
	 * @return form type
	 */
	public OperationType getType() {
		return formType;
	}
}
