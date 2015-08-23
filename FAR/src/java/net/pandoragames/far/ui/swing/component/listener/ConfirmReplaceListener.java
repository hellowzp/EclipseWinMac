package net.pandoragames.far.ui.swing.component.listener;

import java.awt.Component;
import java.awt.event.ActionEvent;

import net.pandoragames.far.ui.model.ReplaceForm;
import net.pandoragames.far.ui.swing.SwingConfig;

/**
 * Specialisation of the confirm listener for the backup button.
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
public class ConfirmReplaceListener extends ConfirmActionListener {

	private ReplaceForm replaceForm;
	
	/**
	 * Constructor needs a reference to the actual FindForm instance.
	 * @param caller "parent" component
	 * @param config used for translations
	 * @param form reference to the used FindForm
	 */
	public ConfirmReplaceListener(Component caller, SwingConfig config, ReplaceForm form) {
		super(caller, 
				config.getLocalizer().localize("label.confirm-replacement"), 
				config.getLocalizer().localize("question.confirm-replacement"));
		replaceForm = form;
	}
	/**
	 * Forwards the event to registered listeners, but only if 
	 * backup is enabled, or if the user confirms.
	 * @param e event to be forwarded
	 */
	public void actionPerformed(ActionEvent e) {
		if( replaceForm.isDoBackup() ) {
			fireEvent( e );
		} else {
			super.actionPerformed(e);
		}
	}

}
