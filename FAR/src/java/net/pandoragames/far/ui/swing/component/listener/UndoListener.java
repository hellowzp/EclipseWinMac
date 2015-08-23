package net.pandoragames.far.ui.swing.component.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.pandoragames.far.ui.UIFace;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;

/**
 * ActionListener for the Undo button. Triggers the operation
 * and informs the table model about the changes.
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
public class UndoListener implements ActionListener {

	private UIFace uiFace;
	private FileSetTableModel tableModel;
	
	/**
	 * Constructor takes the backend interface.
	 * @param ui interface to backend
	 */
	public UndoListener(UIFace ui) {
		uiFace = ui;
	}
	
	/**
	 * Triggers the operation and informs the table model about the changes.
	 */
	public void actionPerformed(ActionEvent event) {
		uiFace.undoLastReplace();
		tableModel.notifyUpdate();
	}
	
	/**
	 * Sets the reference to the FileSetTableModel.
	 * @param model reference to TableModel
	 */
	public void setTableModel(FileSetTableModel model) {
		tableModel = model;
	}

}
