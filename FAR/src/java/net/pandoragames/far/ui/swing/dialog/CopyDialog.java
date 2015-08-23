package net.pandoragames.far.ui.swing.dialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
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
import net.pandoragames.util.file.FileUtil;

/**
 * Copy dialog. Allows to chose a directory where to copy the selected files to.
 * The tree copy operation copies the selected files together with their respective
 * ancestor directories down to the level of the base directory (the base directory
 * itself is never copied). 
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
public class CopyDialog extends FileOperationDialog {

	private PatternFilter patternFilter;
	private List<TargetFile> toBeCopied;
	private File targetDirectory;
	private File baseDirectory;
	private boolean treeCopy;
	private boolean cleanendOnClose = false;

	/**
	 * Constructor for copy dialog. You may want to use
	 * {@link FileOperationDialog#copyDialog(int, int, FileSetTableModel, FindForm, MessageBox, SwingConfig, JFrame) FileOperationDialog.copyDialog()}
	 * or
	 * {@link FileOperationDialog#treeCopyDialog(int, int, FileSetTableModel, FindForm, MessageBox, SwingConfig, JFrame) FileOperationDialog.treeCopyDialog()}
	 * instead.
	 * 
	 * To be called from the context menu. The file set consists of the set of highlighted (mouse selection) files.
	 * 
	 * @param owner root window
	 * @param title dialog title
	 * @param toBeMoved non null, non empty list of files to be copied, a subset of the files present in the model
	 * @param model file set
	 * @param treeCopy should ancestor directories be copied as well
	 * @param findForm reference to find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config reference to SwingConfig
	 */
	public CopyDialog(JFrame owner, String title, List<TargetFile> toBeCopied, FileSetTableModel model, boolean treeCopy, FindForm findForm, MessageBox sink, SwingConfig config) {
		super(owner, title, model, sink, config);
		if( toBeCopied == null ) throw new NullPointerException("List of selected files must not be null");
		if( toBeCopied.size() == 0 ) throw new IllegalArgumentException("List of selected files must not be empty");
		if( ! model.listRows().containsAll( toBeCopied ) ) throw new IllegalArgumentException("List of selected files is not a subset of current file list");
		this.toBeCopied = toBeCopied;
		baseDirectory = findForm.getBaseDirectory();
		targetDirectory = baseDirectory;
		this.treeCopy = treeCopy;
		patternFilter = findForm.createPatternFilter();
		if( toBeCopied.size() == 1 ) {
			questionLabel.setText( config.getLocalizer().localize("question.copy", new Object[]{ toBeCopied.get(0).getName() }) );
		} else {
			questionLabel.setText( config.getLocalizer().localize("question.copy-multiple") );
			for(TargetFile file : toBeCopied  ) {
				file.setIncluded( true );
			}
			addFileList( toBeCopied, treeCopy ? baseDirectory : null );
		}
		addDirectorySelectionControl( config );
		okButton.addActionListener( new CopyFileActionListener() );
		okButton.setEnabled( !(treeCopy || copyOnItself( targetDirectory )) );
		this.addWindowListener( new WindowAdapter() {
			public void windowClosed( WindowEvent e) {
				cleanOnClose();
			}
			public void windowClosing(WindowEvent wen) {
				cleanOnClose();				
			}
			private void cleanOnClose() {
				if( ! cleanendOnClose ) {
					for(TargetFile file : CopyDialog.this.toBeCopied) {
						file.setIncluded( true );
					}
					cleanendOnClose = true;
				}				
			}
		});
	}
	
	
	private void addDirectorySelectionControl(SwingConfig config) {
		class TargetDirRepository implements FileRepository {
			public File getFile() {
				return targetDirectory;
			}
			public boolean setFile(File file) {
				if( file != null && file.isDirectory() 
					&& ! (treeCopy && file.equals( baseDirectory ))
					&& ! copyOnItself( file )) {
					targetDirectory = file;
					okButton.setEnabled( true );
					return true;
				} else {
					return false;					
				}
			}
		}
		addDirectorySelectionControl( config, new TargetDirRepository(), treeCopy || copyOnItself( targetDirectory ) );
	}
	
	private boolean copyOnItself(File dir) {
		if( treeCopy ) return false;
		if( toBeCopied.size() != 1 ) return false;
		return toBeCopied.get(0).getFile().getParentFile().equals( dir );
	}


	class CopyFileActionListener implements ActionListener {
		public void actionPerformed(ActionEvent eve){
			boolean askForOverwrite = true;
			List<TargetFile> copiedFiles = new ArrayList<TargetFile>();
			List<TargetFile> duplicateFiles = new ArrayList<TargetFile>();
			for(TargetFile file : toBeCopied) {
				File newFile = getNewFile( targetDirectory, file );
				if( newFile == null ) {
					file.setIncluded( false ); // grey out in list			
					continue; // next file 
				} else if( newFile.equals(file.getFile()) ) {
					// copy file onto it self
					TargetFile movedFile = (TargetFile) file.clone();
					copiedFiles.add( movedFile );
					duplicateFiles.add( movedFile );
					int index = tableModel.listRows().indexOf( file );
					tableModel.setRow(index, movedFile);
					file.setIncluded( false ); // grey out in list		
					logger.info("Skipped " + movedFile.getFile().getPath() + " because source and target are identical");
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
						int confirmation = JOptionPane.showOptionDialog(CopyDialog.this, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
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
						JOptionPane.showMessageDialog(CopyDialog.this, message, title, JOptionPane.ERROR_MESSAGE);
						continue; // next file
					}
				}
				if( copy(file.getFile(), newFile ) ) {
					TargetFile copiedFile = file.merge( newFile );
					if( ! patternFilter.accept( targetDirectory, newFile.getName() )) {
						copiedFile.setSelected( false );
					}
					if( tableModel.listRows().contains( copiedFile ) ) duplicateFiles.add( copiedFile );
					file.setIncluded( false ); // grey out in list				
					copiedFiles.add( copiedFile );
					logger.info("Copied " + file.getFile().getPath() + " to " + targetDirectory.getPath() );
				}
			}
			if( copiedFiles.size() > 0 ) {
				List<TargetFile> resultList = tableModel.listRows();
				resultList.addAll( copiedFiles );
				if(duplicateFiles.size() > 0) {
					resultList.removeAll( duplicateFiles ); // remove old, overwritten files
					resultList.addAll( duplicateFiles );	// replace with new versions
				}					
				tableModel.setFileList( resultList, baseDirectory );				
			}
			if(copiedFiles.size() < toBeCopied.size()) {
				questionLabel.setText( localizer.localize("message.some-files-not-copied") );
				questionLabel.setForeground( Color.RED );
				refreshList( toBeCopied );
				okButton.setVisible( false );
				cancelButton.setText( localizer.localize("button.close"));
			} else {
				// everything worked ok, we close
				messageBox.info(localizer.localize("message.copy-ok", new Object[]{ Integer.valueOf(copiedFiles.size()), targetDirectory.getName() }));
				for(TargetFile file : toBeCopied) {
					file.setIncluded( true );
				}				
				cleanendOnClose = true;
				CopyDialog.this.dispose();
			}
		}
	}
	
	private File getNewFile(File targetDirectory, TargetFile oldFile) {
		if( ! treeCopy ) {
			return new File( targetDirectory, oldFile.getName() );
		} else {
			String basePath = baseDirectory.getPath();
			String fullPath = oldFile.getFile().getParentFile().getPath();
			if( ! fullPath.startsWith( basePath )) {
				messageBox.error(localizer.localize("message.basedirectory-not-found", oldFile.getName()));
				return null;
			} else {
				String relativePath = fullPath.substring( basePath.length() );
				if( relativePath.startsWith( File.separator )) relativePath = relativePath.substring(1);
				File branchDirectory = new File( targetDirectory, relativePath );
				return new File( branchDirectory, oldFile.getName() );
			}
		}
	}
	
	private boolean copy(File original, File copy) {
		try {
			if( ! copy.exists() ) {
				File parentDir = copy.getParentFile();
				if( ! parentDir.exists() ) {
					if( ! parentDir.mkdirs() ) {
						messageBox.error(localizer.localize("message.directory-not-copied", parentDir.getName()) );
						return false;
					}
				}
				if(! copy.createNewFile() ) {
					messageBox.error(localizer.localize("message.file-not-copied", copy.getName()));
					return false;
				}
			}
			FileUtil.copy(original, copy);
			return true;
		} catch(IOException iox) {
			logger.error("IOException during copy operation: " + iox.getMessage());
			messageBox.error(localizer.localize("message.file-not-copied", copy.getName()));
			return false;
		}
	}
}
