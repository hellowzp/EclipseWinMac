package net.pandoragames.far.ui.swing.dialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;

/**
 * Delete dialog. Asks back before deleting a file set.
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
public class DeleteDialog extends FileOperationDialog {

	private File baseDirectory;
	private List<TargetFile> toBeDeleted;
	
	/**
	 * Constructor for delete dialog. You may want to call 
	 * {@link FileOperationDialog#deleteDialog(int, int, FileSetTableModel, File, MessageBox, SwingConfig, JFrame) FileOperationDialog.deleteDialog()}
	 * instead.
	 * @param owner root window
	 * @param title dialog title
	 * @param toBeDeleted non null, non empty list of files to be deleted, a subset of the files present in the model
	 * @param model file set
	 * @param baseDir base dir from find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config reference to SwingConfig
	 */
	public DeleteDialog(JFrame owner, String title, List<TargetFile> toBeDeleted, FileSetTableModel model, File baseDir, MessageBox sink, SwingConfig config) {
		super(owner, title, model, sink, config);
		if( toBeDeleted == null ) throw new NullPointerException("List of selected files must not be null");
		if( toBeDeleted.size() == 0 ) throw new IllegalArgumentException("List of selected files must not be empty");
		if( ! model.listRows().containsAll( toBeDeleted ) ) throw new IllegalArgumentException("List of selected files is not a subset of current file list");
		this.toBeDeleted = toBeDeleted;
		baseDirectory = baseDir;
		if( toBeDeleted.size() == 1 ) {
			questionLabel.setText( config.getLocalizer().localize("question.delete", new Object[]{ toBeDeleted.get(0).getName() }) );
		} else {
			questionLabel.setText( config.getLocalizer().localize("question.delete-multiple") );
			for(TargetFile file : this.toBeDeleted) {
				file.setIncluded( true );
			}
			addFileList( toBeDeleted );
		}
		okButton.addActionListener( new DeleteFileActionListener() );
	}
	
	class DeleteFileActionListener implements ActionListener {
		
		public void actionPerformed(ActionEvent eve){
			if( toBeDeleted.size() == 1 ) {
				int rowIndex = tableModel.listRows().indexOf( toBeDeleted.get(0) );
				File file = tableModel.getRow( rowIndex ).getFile();
				if( file.delete() ) {
					tableModel.deleteRow( rowIndex );
					messageBox.info( localizer.localize("message.one-file-deleted", file.getName() ));
					DeleteDialog.this.dispose();
				} else {
					String title = localizer.localize( "label.error" );
					String message = localizer.localize("message.could-not-delete");
					JOptionPane.showMessageDialog(DeleteDialog.this, message, title, JOptionPane.ERROR_MESSAGE);
					DeleteDialog.this.dispose();
				}
			} else {
				List<TargetFile> deleteOk = new ArrayList<TargetFile>();
				for(TargetFile file : toBeDeleted) {
					if( file.getFile().delete() ) {
						deleteOk.add( file );
						file.setIncluded( false ); // grey out in list
						logger.info("File " + file.getFile().getPath() + " deletet from HD"); 
					}
				}
				if( deleteOk.size() > 0) {
					List<TargetFile> newList = tableModel.listRows();
					newList.removeAll( deleteOk );
					tableModel.setFileList( newList, baseDirectory);
				}
				if( deleteOk.size() != toBeDeleted.size() ) {
					okButton.setEnabled( false );
					cancelButton.setText( localizer.localize("button.close"));
					questionLabel.setText( localizer.localize( "message.incomplete-delete" ) );
					questionLabel.setForeground( Color.RED );
					refreshList( toBeDeleted );
				} else {
					messageBox.info( localizer.localize("message.delete-ok", Integer.valueOf( deleteOk.size() ) ));
					DeleteDialog.this.dispose();
				}
			}
		}
	}
}
