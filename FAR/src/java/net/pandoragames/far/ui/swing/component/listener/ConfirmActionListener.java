package net.pandoragames.far.ui.swing.component.listener;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * A container for other action listeners that will only be triggered if 
 * the user confirms the action.
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
public class ConfirmActionListener implements ActionListener {

	private List<ActionListener> listenerList = new ArrayList<ActionListener>();
	private Component parentComponent;
	protected String dialogTitle = "Confirmation Required";
	protected String dialogMessage = "Please confirm.";
	
	/**
	 * The listener must be created with some "parent" component.
	 * @param caller parent component.
	 * @param title show as window title of the dialogue popup
	 * @param message message for the user.
	 */
	public ConfirmActionListener(Component caller, String title, String message) {
		parentComponent = caller;
		dialogTitle = title;
		dialogMessage = message;
	}
	
	/**
	 * Forwards the event to registered listeners, but only if the user confirms.
	 * @param e event to be forwarded
	 */
	public void actionPerformed(ActionEvent e) {
		if( confirm(dialogTitle, dialogMessage) ) {
			fireEvent( e );
		}
	}

	/**
	 * Adds an ActionListener to this component that will be notified
	 * if t
	 * @param listener
	 */
	public void addActionListener(ActionListener listener) {
		if(listener != null) listenerList.add( listener );
	}
	
	/**
	 * Returns true if the user confirms the action.
	 * @param title show as window title of the dialogue popup
	 * @param message message for the user.
	 * @return true if confirmed
	 */
	protected boolean confirm(String title, String message) {
		return JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(parentComponent, 
																		message, 
																		title, 
																		JOptionPane.OK_CANCEL_OPTION, 
																		JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * Forwards the event to the listener registered with this component.
	 * @param event to be forwarded.
	 */
	protected void fireEvent(ActionEvent event) {
		for(ActionListener listener : listenerList) {
			listener.actionPerformed( event );
		}
	}
}
