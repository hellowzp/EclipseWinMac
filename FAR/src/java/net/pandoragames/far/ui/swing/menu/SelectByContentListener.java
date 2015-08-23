package net.pandoragames.far.ui.swing.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.far.ui.swing.dialog.SubSelectDialog;

/**
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
class SelectByContentListener implements ActionListener {
	private SwingConfig config;
	private ComponentRepository repository; 
	private JFrame jframe;
	private FileSetTableModel tableModel;
	public SelectByContentListener(JFrame owner, FileSetTableModel model, ComponentRepository cr, SwingConfig swc) {
		config = swc;
		repository = cr;
		jframe = owner;
		tableModel = model;
	}
	public void actionPerformed(ActionEvent e) {
		SubSelectDialog dialog = new SubSelectDialog(jframe, tableModel, repository, config);
		dialog.pack();
		dialog.show();
	}
}

