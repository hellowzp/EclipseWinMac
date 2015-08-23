package net.pandoragames.far.ui.swing.dialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.pandoragames.far.PatternFilter;
import net.pandoragames.far.ui.model.FileRepository;
import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;

/**
 * Move dialog. Allows to chose a directory where to move the selected files to.
 * 
 * @author Olivier Wehner <!--
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
public class MoveDialog extends FileOperationDialog {

	private PatternFilter patternFilter;
	private List<TargetFile> toBeMoved;
	private File targetDirectory;
	private File baseDirectory;

	/**
	 * Constructor for move dialog. You may want to use
	 * {@link FileOperationDialog#moveDialog(int, int, FileSetTableModel, FindForm, MessageBox, SwingConfig, JFrame)
	 * FileOperationDialog.moveDialog()} instead.
	 * 
	 * @param owner
	 *            root window
	 * @param title
	 *            dialog title
	 * @param toBeMoved
	 *            non null, non empty list of files to be moved, a subset of the
	 *            files present in the model
	 * @param model
	 *            file set
	 * @param findForm
	 *            reference to find form
	 * @param sink
	 *            for messages that should be displayed AFTER closing
	 * @param config
	 *            reference to SwingConfig
	 */
	public MoveDialog(JFrame owner, String title, List<TargetFile> toBeMoved,
			FileSetTableModel model, FindForm findForm, MessageBox sink,
			SwingConfig config) {
		super(owner, title, model, sink, config);
		if (toBeMoved == null)
			throw new NullPointerException(
					"List of selected files must not be null");
		if (toBeMoved.size() == 0)
			throw new IllegalArgumentException(
					"List of selected files must not be empty");
		if (!model.listRows().containsAll(toBeMoved))
			throw new IllegalArgumentException(
					"List of selected files is not a subset of current file list");
		this.toBeMoved = toBeMoved;
		baseDirectory = findForm.getBaseDirectory();
		targetDirectory = baseDirectory;
		patternFilter = findForm.createPatternFilter();
		if (toBeMoved.size() == 1) {
			targetDirectory = toBeMoved.get(0).getFile().getParentFile();
		}
		if (toBeMoved.size() == 1) {
			questionLabel.setText(config.getLocalizer().localize(
					"question.move",
					new Object[] { toBeMoved.get(0).getName() }));
		} else {
			questionLabel.setText(config.getLocalizer().localize(
					"question.move-multiple"));
			for (TargetFile file : toBeMoved) { // model.listRows()) {
				file.setIncluded(true);
			}
			addFileList(toBeMoved);
		}
		addDirectorySelectionControl(config);
		okButton.addActionListener(new MoveFileActionListener());
		okButton.setEnabled(false);
	}

	private void addDirectorySelectionControl(SwingConfig config) {
		class TargetDirRepository implements FileRepository {
			public File getFile() {
				return targetDirectory;
			}

			public boolean setFile(File file) {
				if (file != null && file.isDirectory()) {
					targetDirectory = file;
					okButton.setEnabled(true);
					return true;
				} else {
					return false;
				}
			}
		}
		addDirectorySelectionControl(config, new TargetDirRepository(), true);
	}

	class MoveFileActionListener implements ActionListener {
		public void actionPerformed(ActionEvent eve){
			boolean askForOverwrite = true;
			List<TargetFile> movedFiles = new ArrayList<TargetFile>();
			List<TargetFile> duplicateFiles = new ArrayList<TargetFile>();
			for(TargetFile file : toBeMoved) {
				int index = tableModel.listRows().indexOf( file );
				File newFile = new File( targetDirectory, file.getName() );
				if( newFile.equals(file.getFile()) ) {
					// move file onto it self
					TargetFile movedFile = (TargetFile) file.clone();
					movedFiles.add( movedFile );
					tableModel.setRow(index, movedFile);
					file.setIncluded( false ); // grey out in list			
					continue; // next file 
				} else if( newFile.exists() ) {
					if( askForOverwrite ) {
						String title = localizer.localize( "label.replace" );
						String message = localizer.localize("question.file-exists", file.getName());
						String[] options = new String[] {
								localizer.localize("button.yes"),
								localizer.localize("button.no"),
								localizer.localize("button.yes-to-all"),
								localizer.localize("button.abort")
						};
						int confirmation = JOptionPane.showOptionDialog(MoveDialog.this, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
						if( confirmation == 1 ) {	// NO
							continue; // next file
						} else if( confirmation == 2 ) { // YES TO ALL
							askForOverwrite = false;
						} else if( confirmation == 3  // ABORT
								|| confirmation == JOptionPane.CLOSED_OPTION ) {
							break; // abort
						}
					}
					// for windows delete target file first
					if(! newFile.delete() ) {
						String title = localizer.localize( "label.error" );
						String message = localizer.localize("message.could-not-replace", file.getName());
						JOptionPane.showMessageDialog(MoveDialog.this, message, title, JOptionPane.ERROR_MESSAGE);
						continue; // next file
					}
				}
				if( file.getFile().renameTo( newFile ) ) {
					TargetFile movedFile = file.merge( newFile );
					if( ! patternFilter.accept( targetDirectory, newFile.getName() )) {
						movedFile.setSelected( false );
					} else {
						movedFile.setSelected( file.isSelected() );
					}
					if( tableModel.listRows().contains( movedFile ) ) duplicateFiles.add( movedFile );
					tableModel.setRow(index, movedFile);
					file.setIncluded( false ); // grey out in list				
					movedFiles.add( movedFile );
					logger.info("Moved " + file.getFile().getPath() + " to " + targetDirectory.getPath() );
				}
			}
			if(duplicateFiles.size() > 0) {
				List<TargetFile> resultList = tableModel.listRows();
				resultList.removeAll( duplicateFiles );	// remove old, overwritten files
				resultList.addAll( duplicateFiles );	// replace with new versions
				tableModel.setFileList( resultList, baseDirectory );
			}
			if(movedFiles.size() < toBeMoved.size()) {
				questionLabel.setText( localizer.localize("message.some-files-not-moved") );
				questionLabel.setForeground( Color.RED );
				refreshList( toBeMoved );
				okButton.setVisible( false );
				cancelButton.setText( localizer.localize("button.close"));
			} else {
				// everything worked ok, we close
				messageBox.info(localizer.localize("message.move-ok", new Object[]{ Integer.valueOf(movedFiles.size()), targetDirectory.getName() }));
				MoveDialog.this.dispose();
			}
		}
	}
}
