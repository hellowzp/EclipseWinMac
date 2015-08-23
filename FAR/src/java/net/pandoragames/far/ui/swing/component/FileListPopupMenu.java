package net.pandoragames.far.ui.swing.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.apache.commons.logging.LogFactory;

import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.ReplaceForm;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.listener.ActionPreview;
import net.pandoragames.far.ui.swing.component.listener.ActionView;
import net.pandoragames.far.ui.swing.dialog.FileEditor;
import net.pandoragames.far.ui.swing.dialog.FileOperationDialog;
import net.pandoragames.far.ui.swing.dialog.InfoView;
import net.pandoragames.util.i18n.Localizer;
import net.pandoragames.util.j6.DesktopHelper;

/**
 * A popupmenu for the file list table that displays the different viewer options.
 * 
 * @author olive
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
public class FileListPopupMenu extends JPopupMenu {
	
	private JFrame mainFrame;
	private SwingConfig config;
	private ComponentRepository repository;
	private MessageBox errorSink;
	private JTable fileTable;
	private FileSetTableModel tableModel;
	private ReplaceForm replaceForm;
	private FindForm findForm;
	private ActionView viewAction;
	private ActionPreview previewAction;
	private int selectedRow = -1;
	boolean openItemsAreActive = false;
	// menu items
	private JMenuItem edit;
	private JMenuItem info;
	private JMenuItem rename;
	private JMenuItem copy;
	private JMenuItem treeCopy;
	private JMenuItem move;
	private JMenuItem delete;
	private JMenuItem openFile;
	private JMenuItem openDir;
	
	/**
	 * Constructor.
	 * @param jTable the table on which is operated
	 * @param tableModel the underlying table model
	 * @param componentRepository providing references to other components
	 * @param swingConfig holding the localizer and other information
	 */
	public FileListPopupMenu(JTable jTable, FileSetTableModel tableModel, ComponentRepository componentRepository, SwingConfig swingConfig) {
		fileTable = jTable;
		this.repository = componentRepository;
		this.tableModel = tableModel;
		config = swingConfig;
		errorSink = componentRepository.getMessageBox();
		mainFrame = componentRepository.getRootWindow();
		replaceForm = componentRepository.getReplaceForm();
		findForm = componentRepository.getFindForm();
		viewAction = new ActionView(componentRepository, config);
		previewAction = new ActionPreview(componentRepository, config);
		init( config.getLocalizer(), componentRepository );
	}

	private void init( Localizer localizer, ComponentRepository componentRepository) {
		//	Edit
		edit = new JMenuItem( localizer.localize( "label.edit" ) );
		edit.setToolTipText( localizer.localize( "tooltip.edit" ) );
		edit.setMnemonic( config.getAccessKey("popup.label.edit") );
		edit.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileEditor editor = new FileEditor( mainFrame, 
						  tableModel.getRow( selectedRow ), 
						  config);
				editor.pack();
				editor.setVisible( true );
			}
		});
		this.add( edit );
		//	View
		JMenuItem view = new JMenuItem( viewAction );
		view.setToolTipText( localizer.localize( "tooltip.view" ) );
		this.add( view );
		//	Preview
		JMenuItem preview = new JMenuItem( previewAction );
		preview.setToolTipText( localizer.localize( "tooltip.preview" ) );
		this.add( preview );
		//	Info
		info = new JMenuItem( localizer.localize( "label.info" ) );
		info.setToolTipText( localizer.localize( "tooltip.info" ) );
		info.setMnemonic( config.getAccessKey("popup.label.info") );
		info.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InfoView infoView = new InfoView( mainFrame, 
						  tableModel.getRow( selectedRow ), 
						  config,
						  repository);
				infoView.pack();
				infoView.setVisible( true );
			}
		});
		this.add( info );
		if(SwingConfig.getEffectiveJavaVersion() > 5 && DesktopHelper.isFileOpeningSupported()) {
			
			openItemsAreActive = true;
			// seperator 
			this.addSeparator();
			
			openFile = new JMenuItem( localizer.localize( "label.open-file" ) );
			openFile.setToolTipText( localizer.localize( "tooltip.open-file" ) );
			openFile.setMnemonic( config.getAccessKey("popup.label.open-file") );
			openFile.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						DesktopHelper.openFile( tableModel.getRow( selectedRow ).getFile() );
					} catch(IOException iox) {
						errorSink.error( iox.getMessage() );
					}
				}
			});
			this.add( openFile );
			openDir = new JMenuItem( localizer.localize( "label.open-directory" ) );
			openDir.setToolTipText( localizer.localize( "tooltip.open-directory" ) );
			openDir.setMnemonic( config.getAccessKey("popup.label.open-directory") );
			this.add( openDir );
			openDir.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						DesktopHelper.openFile( tableModel.getRow( selectedRow ).getFile().getParentFile() );
					} catch(IOException iox) {
						errorSink.error( iox.getMessage() );
					}
				}
			});
		}
		// seperator 
		this.addSeparator();
		// rename
		rename = new JMenuItem( localizer.localize( "menu.rename-dialog" ) );
		rename.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileOperationDialog.renameDialog(selectedRow,
													tableModel,
													findForm, 
													errorSink,
													config,
													mainFrame);
			}
		});
		this.add( rename );
		// copy
		copy = new JMenuItem( localizer.localize( "menu.copy" ) );
		copy.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = selectedRow;
				int count = 1;
				if(fileTable.getSelectedRowCount() > 0) {
					index = fileTable.getSelectedRow();
					count = fileTable.getSelectedRowCount();
				}
				FileOperationDialog.copyDialog(index, 
													count, 
													tableModel, 
													findForm,
													errorSink,
													config,
													mainFrame);
			}
		});
		this.add( copy );
		// tree copy
		treeCopy = new JMenuItem( localizer.localize( "menu.treeCopy" ) );
		treeCopy.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = selectedRow;
				int count = 1;
				if(fileTable.getSelectedRowCount() > 0) {
					index = fileTable.getSelectedRow();
					count = fileTable.getSelectedRowCount();
				}
				FileOperationDialog.treeCopyDialog(index, 
													count, 
													tableModel, 
													findForm,
													errorSink,
													config,
													mainFrame);
			}
		});
		this.add( treeCopy );
		// move
		move = new JMenuItem( localizer.localize( "menu.move" ) );
		move.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = selectedRow;
				int count = 1;
				if(fileTable.getSelectedRowCount() > 0) {
					index = fileTable.getSelectedRow();
					count = fileTable.getSelectedRowCount();
				}
				FileOperationDialog.moveDialog(index, 
													count, 
													tableModel, 
													findForm,
													errorSink,
													config,
													mainFrame);
			}
		});
		this.add( move );
		// delete
		delete = new JMenuItem( localizer.localize( "menu.delete" ) );
		delete.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = selectedRow;
				int count = 1;
				if(fileTable.getSelectedRowCount() > 0) {
					index = fileTable.getSelectedRow();
					count = fileTable.getSelectedRowCount();
				}
				FileOperationDialog.deleteDialog(index, 
													count, 
													tableModel, 
													findForm.getBaseDirectory(),
													errorSink,
													config,
													mainFrame);
			}
		});
		this.add( delete );
	}
	
	/**
	 * Overwrites the method of the superclass to fetch mouse coordinates 
	 * and to deduce the selected row of the file list table.
	 */
	public void show( Component jTable, int mouseX, int mouseY ) {
		Point mousePointer = new Point( mouseX, mouseY );
		selectedRow = fileTable.rowAtPoint( mousePointer );
		if(fileTable.getSelectedRowCount() == 1) {
			viewAction.setSelectedFile(tableModel.getRow( selectedRow ));
			previewAction.setSelectedFile(tableModel.getRow( selectedRow ));
		} else {
			viewAction.setSelectedFile(null);
			previewAction.setSelectedFile(null);
		}
		enableItems( fileTable.getSelectedRowCount() < 2 , tableModel.getRow( selectedRow ).getFile().exists() );
		super.show( jTable, mouseX, mouseY );
	}
	
	
	private void enableItems(boolean singleFile, boolean fileExists) {
			edit.setEnabled( singleFile && fileExists );
			info.setEnabled( singleFile && fileExists );
			rename.setEnabled( singleFile && fileExists );
			if( openItemsAreActive ) {
				openFile.setEnabled( singleFile && fileExists );
				openDir.setEnabled( singleFile ); // directory can be opened even if file has been removed
			}
			copy.setEnabled( fileExists || !singleFile );
			treeCopy.setEnabled( fileExists || !singleFile );
			move.setEnabled( fileExists || !singleFile );
			delete.setEnabled( fileExists || !singleFile );
	}
		
}
