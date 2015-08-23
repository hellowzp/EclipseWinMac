package net.pandoragames.far.ui.swing.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.pandoragames.far.ui.model.FileRepository;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;

/**
 * Export the file list to a file. This dialog asks for a file name and a directory
 * where store the list. It will create a plain text file, containing one absolute file
 * path per line. Deselected files will not be exported.
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
public class ExportFileListDialog extends FileOperationDialog {

	private SwingConfig farConfig;
	private JTextField fileNameTextField;
	
	/**
	 * Constructor.
	 * @param owner root window
	 * @param model holding the file list to be exported
	 * @param config application configuration
	 */
	public ExportFileListDialog(JFrame owner, FileSetTableModel model, SwingConfig config) {
		super(owner, config.getLocalizer().localize("label.export-file-list"), model, null, config);
		farConfig = config;
		
		questionLabel.setText(localizer.localize("message.select-export-file"));
		
		fileNameTextField = new JTextField();
		fileNameTextField.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, 
																config.getStandardComponentHight()) );
		fileNameTextField.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, 
																config.getStandardComponentHight()) );
		fileNameTextField.setEditable( true );
		fileNameTextField.setText("FAR-result-list.txt");
		centerPanel.add( fileNameTextField );
		centerPanel.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
		
		class TargetDirRepository implements FileRepository {
			public File getFile() {
				return farConfig.getFileListExportDirectory();
			}
			public boolean setFile(File file) {
				if( file != null && file.isDirectory() ) {
					farConfig.setFileListExportDirectory( file );
					return true;
				} else {
					return false;					
				}
			}
		}
		addDirectorySelectionControl( config, new TargetDirRepository(), false );
		
		okButton.setEnabled( true );
		okButton.addActionListener( new FileListExporter() );
	}
	
	class FileListExporter implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			File exportFile = new File( farConfig.getFileListExportDirectory(), fileNameTextField.getText().trim());
			boolean doExport = true;
			if( exportFile.exists() ) {
				String title = localizer.localize( "label.replace" );
				String message = localizer.localize("question.file-exists", exportFile.getName());
				int confirmation = JOptionPane.showConfirmDialog(ExportFileListDialog.this, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
				if( confirmation == JOptionPane.NO_OPTION ) {
					doExport = false;
				}
			} else {
				try {
					if( ! exportFile.createNewFile() ) {
						doExport = false;
						questionLabel.setText( localizer.localize("message.could-not-create") );
						questionLabel.setForeground( Color.RED );
					}
				} catch(IOException iox) {
					doExport = false;
					questionLabel.setText( localizer.localize("message.could-not-create") );
					questionLabel.setForeground( Color.RED );					
				}
			}
			if( doExport ) {
				String lineEnd = System.getProperty("line.separator");
				OutputStreamWriter writer = null;
				try {
					writer = new OutputStreamWriter( new FileOutputStream( exportFile ), "UTF-8");
					for(TargetFile file : tableModel.listRows()) {
						if( file.isSelected() ) {
							writer.append( file.getFile().getPath() );
							writer.append( lineEnd );
						}
					}
					writer.flush();
					ExportFileListDialog.this.dispose();
				} catch(IOException iox) {
					logger.error("IOException exporting file list: " + iox.getMessage());
					questionLabel.setText( localizer.localize("message.could-not-create") );
					questionLabel.setForeground( Color.RED );	
				} finally {
					if( writer != null ) try { writer.close(); } catch(IOException x) {/* ignore me*/};
				}
			}
		}
		
	}
}
