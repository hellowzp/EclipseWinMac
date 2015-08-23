/**
 * 
 */
package net.pandoragames.far.ui.swing.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;

/**
 * 
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
class StandardFileSelector implements ActionListener {
	private FileSetTableModel tableModel;
	private int mode;
	/** 
	 * Define if this selector should select all (1), nothing (-1) or invert selection (0).
	 * @param model files to be selected
	 * @param mode select all (1), unselect all (-1) or invert(0)
	 */
	public StandardFileSelector(FileSetTableModel model, int mode) {
		tableModel = model;
		this.mode = mode;
	}
	public void actionPerformed(ActionEvent e) {
		if(mode == 0) {
			for(TargetFile row : tableModel.listRows() ) {
				row.clear();
				row.setSelected( ! row.isSelected() );
			}
		} else {
			for(TargetFile row : tableModel.listRows() ) {
				row.clear();
				row.setSelected( mode > 0 );
			}
		}
		tableModel.notifyUpdate();
	}	
}
