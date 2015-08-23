package net.pandoragames.far.ui.swing.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.pandoragames.far.PatternFilter;
import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;

/**
 * Rename dialog. Asks the user to specify a new file name for some selected file.
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
public class RenameDialog extends FileOperationDialog {

	private FindForm findForm;

	/**
	 * Constructor for rename dialog. You may want to call 
	 * {@link FileOperationDialog#renameDialog(int, FileSetTableModel, FindForm, MessageBox, SwingConfig, JFrame) FileOperationDialog.renameDialog()}
	 * instead.
	 * @param owner root window
	 * @param title window title
	 * @param rowIndex selected row in model
	 * @param model model holding the file to be changed
	 * @param findForm reference to find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config reference to SwingConfig
	 */
	public RenameDialog(JFrame owner, String title, int rowIndex, FileSetTableModel model, FindForm findForm, MessageBox sink, SwingConfig config) {
		super(owner, title, model, sink, config);
		this.findForm = findForm;
		TargetFile file = model.getRow( rowIndex );
		questionLabel.setText( config.getLocalizer().localize("question.renameTo", new Object[]{ file.getName() }) );
		JTextField renameField = new JTextField( file.getName() );
		renameField.setPreferredSize( new Dimension( SwingConfig.COMPONENT_WIDTH, config.getStandardComponentHight() ) );
		Action renameAction = new RenameFileActionListener( file, rowIndex, renameField );
		renameField.getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER , 0), renameAction );
		okButton.addActionListener( renameAction );
		centerPanel.add( renameField );
	}
	
	class RenameFileActionListener extends AbstractAction {
		private JTextField textField;
		private TargetFile targetFile;
		private int fileIndex;
		public RenameFileActionListener(TargetFile file, int index, JTextField tf) {
			super("Rename");
			this.putValue( Action.ACTION_COMMAND_KEY, "RENAME");
			textField = tf;
			targetFile = file;
			fileIndex = index;
		}
		public void actionPerformed(ActionEvent eve){
			String newName = textField.getText().trim();
			// don't do anything if nothing has changed
			if(targetFile.getName().equals( newName )) return;
			if( "".equals( newName ) ) return;
			
			File directory = targetFile.getFile().getParentFile();
			File[] sameName = directory.listFiles( new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.equalsIgnoreCase(textField.getText().trim());
				}
			});
			if((sameName.length == 0) 
					|| (sameName.length == 1
							&& newName.equalsIgnoreCase( targetFile.getName() ))) {
				File oldFile = targetFile.getFile();
				File newFile = new File( oldFile.getParentFile(), newName );
				if( oldFile.renameTo( newFile ) ) {
					TargetFile newRow = targetFile.merge( newFile );
					PatternFilter fileTest = findForm.createPatternFilter();
					newRow.setIncluded( fileTest.accept(newFile.getParentFile(), newFile.getName()) );
					newRow.setSelected( targetFile.isSelected() && newRow.isIncluded() );
					tableModel.setRow(fileIndex, newRow );
					RenameDialog.this.dispose();
				} else {
					String title = localizer.localize( "label.error" );
					String message = localizer.localize("message.could-not-rename");
					JOptionPane.showMessageDialog(RenameDialog.this, message, title, JOptionPane.ERROR_MESSAGE); 
				}
			} else { 
				String title = localizer.localize( "label.error" );
				String message = localizer.localize("message.no-overwrite");
				JOptionPane.showMessageDialog(RenameDialog.this, message, title, JOptionPane.WARNING_MESSAGE); 
			}
		}
	}

}
