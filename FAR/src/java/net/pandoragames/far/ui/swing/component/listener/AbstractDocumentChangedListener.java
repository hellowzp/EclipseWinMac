package net.pandoragames.far.ui.swing.component.listener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Delegates all document change calls to a single, abstract method.
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
public abstract class AbstractDocumentChangedListener implements DocumentListener {

	/**
	 * Forwards to {@link #documentChanged(DocumentEvent)}.
	 */
	public void changedUpdate(DocumentEvent eve) {
		documentChanged(eve);
	}

	/**
	 * Forwards to {@link #documentChanged(DocumentEvent)}.
	 */
	public void insertUpdate(DocumentEvent eve) {
		documentChanged(eve);
	}

	/**
	 * Forwards to {@link #documentChanged(DocumentEvent)}.
	 */
	public void removeUpdate(DocumentEvent eve) {
		documentChanged(eve);
	}
	
	/**
	 * To be implemented by inheriting classes.
	 * @param eve
	 */
	public abstract void documentChanged(DocumentEvent eve);

}
