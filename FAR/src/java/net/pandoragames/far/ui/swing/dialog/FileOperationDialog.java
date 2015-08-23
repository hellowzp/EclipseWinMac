package net.pandoragames.far.ui.swing.dialog;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.WindowConstants;

import net.pandoragames.far.ui.model.FileRepository;
import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.far.ui.swing.component.TwoComponentsPanel;
import net.pandoragames.far.ui.swing.component.listener.BrowseButtonListener;
import net.pandoragames.util.file.FileUtil;
import net.pandoragames.util.i18n.Localizer;

/**
 * Base class for standard file operation dialogs. Rename, copy, move and delete. 
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
public class FileOperationDialog extends SubWindow {

	/** Localizer for label codes.*/
	protected Localizer localizer;
	/** Reference to current table model. */
	protected FileSetTableModel tableModel;
	/** Label at top of window. Inheriting classes must set text. */
	protected JLabel questionLabel;
	/** OK button. Inheritting classes must add action listener.*/
	protected JButton okButton;
	/** Canel button. Closes the window. */
	protected JButton cancelButton;
	/** Space to add custom components. */
	protected JPanel centerPanel;
	/** External error sink for messages that should be displayed AFTER closing */
	protected MessageBox messageBox;
	
	private JList fileDisplay;
	
	/**
	 * Constructor for subclassing.
	 * @param owner root window
	 * @param title window title
	 * @param model reference to file result list
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config SwingConfiguration
	 */
	protected FileOperationDialog(JFrame owner, String title, FileSetTableModel model, MessageBox sink, SwingConfig config) {
		super(owner, title, true);
		setResizable( false );
		localizer = config.getLocalizer();
		tableModel = model;
		messageBox = sink;
		init();
		placeOnScreen( config.getScreenCenter() );
	}
	/**
	 * Convenience method to create a Delete dialog.
	 * @param toBeDeleted non null, non empty list of files to be deleted, a subset of the files present in the model
	 * @param model reference to file list model
	 * @param baseDir base directory from find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config reference to SwingConfig
	 * @param owner root window
	 */
	private static void deleteDialog(List<TargetFile> toBeDeleted, FileSetTableModel model, File baseDir, MessageBox sink, SwingConfig config, JFrame owner) {
		String title = config.getLocalizer().localize("label.delete");
		DeleteDialog dialog = new DeleteDialog(owner, title, toBeDeleted, model, baseDir, sink, config);
		dialog.pack();
		dialog.setVisible( true );
	}
	/**
	 * Convenience method to create a Delete dialog. Attempts to delete all files that are selected in the model.
	 * @param model reference to file list model
	 * @param baseDir base directory from find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config reference to SwingConfig
	 * @param owner root window
	 */
	public static void deleteDialog(FileSetTableModel model, File baseDir, MessageBox sink, SwingConfig config, JFrame owner) {
		List<TargetFile> toBeDeleted = new ArrayList<TargetFile>();
		for( TargetFile file : model.listRows() ){
			if( file.isSelected() ) toBeDeleted.add( file );
		}
		deleteDialog(toBeDeleted, model, baseDir, sink, config, owner);
	}
	/**
	 * Convenience method to create a Delete dialog. Attempts to delete the specified number of files, starting at the indicated offset.
	 * @param offset first selected row
	 * @param rowCount number of selected rows
	 * @param model reference to file list model
	 * @param baseDir base directory from find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config reference to SwingConfig
	 * @param owner root window
	 */
	public static void deleteDialog(int offset, int rowCount, FileSetTableModel model, File baseDir, MessageBox sink, SwingConfig config, JFrame owner) {
		if(offset < 0) throw new IllegalArgumentException("Parameter offset may not be negative");
		if(rowCount < 1) throw new IllegalArgumentException("Parameter rowCount may not be smaller one");
		// we need a copy, sublist is a view
		List<TargetFile> toBeDeleted = new ArrayList<TargetFile>(model.listRows().subList(offset, offset + rowCount )); 
		deleteDialog(toBeDeleted, model, baseDir, sink, config, owner);
	}
	/**
	 * Convenience method to create a Rename dialog.
	 * @param rowIndex index of file to rename in result list
	 * @param model result list
	 * @param findForm reference to find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config SwingConfig
	 * @param owner root window
	 */
	public static void renameDialog(int rowIndex, FileSetTableModel model, FindForm findForm, MessageBox sink, SwingConfig config, JFrame owner) {
		String title = config.getLocalizer().localize("label.rename");
		RenameDialog dialog = new RenameDialog(owner, title, rowIndex, model, findForm, sink, config);	
		dialog.pack();
		dialog.setVisible( true );
	}
	
	/**
	 * Convenience method to create a Move dialog.
	 * @param toBeMoved non null, non empty list of files to be moved, a subset of the files present in the model
	 * @param model file set
	 * @param findForm reference to find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config reference to SwingConfig
	 * @param owner root window
	 */
	private static void moveDialog(List<TargetFile> toBeMoved, FileSetTableModel model, FindForm findForm, MessageBox sink, SwingConfig config, JFrame owner) {
		String title = config.getLocalizer().localize("label.move");
		MoveDialog dialog = new MoveDialog(owner, title, toBeMoved, model, findForm, sink, config);
		dialog.pack();
		dialog.setVisible( true );
	}
	/**
	 * Convenience method to create a Move dialog. Attempts to move all files that are selected in the model.
	 * @param model file set
	 * @param findForm reference to find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config reference to SwingConfig
	 * @param owner root window
	 */
	public static void moveDialog(FileSetTableModel model, FindForm findForm, MessageBox sink, SwingConfig config, JFrame owner) {
		List<TargetFile> toBeMoved = new ArrayList<TargetFile>();
		for( TargetFile file : model.listRows() ){
			if( file.isSelected() ) toBeMoved.add( file );
		}
		moveDialog(toBeMoved, model, findForm, sink, config, owner);
	}
	/**
	 * Convenience method to create a Move dialog. Attempts to move the specified number of files, starting at the indicated offset.
	 * @param offset index of first selected file in model
	 * @param rowCount number of selected files
	 * @param model file set
	 * @param findForm reference to find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config reference to SwingConfig
	 * @param owner root window
	 */
	public static void moveDialog(int offset, int rowCount, FileSetTableModel model, FindForm findForm, MessageBox sink, SwingConfig config, JFrame owner) {
		if(offset < 0) throw new IllegalArgumentException("Parameter offset may not be negative");
		if(rowCount < 1) throw new IllegalArgumentException("Parameter rowCount may not be smaller one");
		// we need a copy, sublist is a view
		List<TargetFile> toBeMoved = new ArrayList<TargetFile>(model.listRows().subList(offset, offset + rowCount ));
		moveDialog(toBeMoved, model, findForm, sink, config, owner);
	}

	/**
	 * Convenience method to create a Copy dialog.
	 * @param toBeMoved non null, non empty list of files to be copied, a subset of the files present in the model
	 * @param model file set
	 * @param findForm reference to find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config reference to SwingConfig
	 * @param owner root window
	 */
	private static void copyDialog(List<TargetFile> toBeCopied, FileSetTableModel model, FindForm findForm, MessageBox sink, SwingConfig config, JFrame owner) {
		String title = config.getLocalizer().localize("label.copy");
		CopyDialog dialog = new CopyDialog(owner, title, toBeCopied, model, false, findForm, sink, config);
		dialog.pack();
		dialog.setVisible( true );
	}
	/**
	 * Convenience method to create a Copy dialog. Attempts to copy all files that are selected in the model.
	 * @param model file set
	 * @param findForm reference to find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config reference to SwingConfig
	 * @param owner root window
	 */
	public static void copyDialog(FileSetTableModel model, FindForm findForm, MessageBox sink, SwingConfig config, JFrame owner) {
		List<TargetFile> toBeCopied = new ArrayList<TargetFile>();
		for( TargetFile file : model.listRows() ){
			if( file.isSelected() ) toBeCopied.add( file );
		}
		copyDialog(toBeCopied, model, findForm, sink, config, owner);
	}
	/**
	 * Convenience method to create a Copy dialog. Attempts to copy the specified number of files, starting at the indicated offset.
	 * @param offset index of first selected file in model
	 * @param rowCount number of selected files
	 * @param model file set
	 * @param findForm reference to find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config reference to SwingConfig
	 * @param owner root window
	 */
	public static void copyDialog(int offset, int rowCount, FileSetTableModel model, FindForm findForm, MessageBox sink, SwingConfig config, JFrame owner) {
		if(offset < 0) throw new IllegalArgumentException("Parameter offset may not be negative");
		if(rowCount < 1) throw new IllegalArgumentException("Parameter rowCount may not be smaller one");
		// we need a copy, sublist is a view
		List<TargetFile> toBeCopied = new ArrayList<TargetFile>(model.listRows().subList(offset, offset + rowCount ));
		copyDialog(toBeCopied, model, findForm, sink, config, owner);
	}

	/**
	 * Convenience method to create a Tree Copy dialog.
	 * @param toBeMoved non null, non empty list of files to be copied, a subset of the files present in the model
	 * @param model file set
	 * @param findForm reference to find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config reference to SwingConfig
	 * @param owner root window
	 */
	public static void treeCopyDialog(List<TargetFile> toBeCopied, FileSetTableModel model, FindForm findForm, MessageBox sink, SwingConfig config, JFrame owner) {
		String title = config.getLocalizer().localize("label.treeCopy");
		CopyDialog dialog = new CopyDialog(owner, title, toBeCopied, model, true, findForm, sink, config);
		dialog.pack();
		dialog.setVisible( true );
	}
	/**
	 * Convenience method to create a Tree Copy dialog. Attempts to copy all files that are selected in the model.
	 * @param model file set
	 * @param findForm reference to find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config reference to SwingConfig
	 * @param owner root window
	 */
	public static void treeCopyDialog(FileSetTableModel model, FindForm findForm, MessageBox sink, SwingConfig config, JFrame owner) {
		List<TargetFile> toBeCopied = new ArrayList<TargetFile>();
		for( TargetFile file : model.listRows() ){
			if( file.isSelected() ) toBeCopied.add( file );
		}
		treeCopyDialog(toBeCopied, model, findForm, sink, config, owner);
	}
	/**
	 * Convenience method to create a Tree Copy dialog. Attempts to copy the specified number of files, starting at the indicated offset.
	 * @param offset index of first selected file in model
	 * @param rowCount number of selected files
	 * @param model file set
	 * @param findForm reference to find form
	 * @param sink for messages that should be displayed AFTER closing
	 * @param config reference to SwingConfig
	 * @param owner root window
	 */
	public static void treeCopyDialog(int offset, int rowCount, FileSetTableModel model, FindForm findForm, MessageBox sink, SwingConfig config, JFrame owner) {
		if(offset < 0) throw new IllegalArgumentException("Parameter offset may not be negative");
		if(rowCount < 1) throw new IllegalArgumentException("Parameter rowCount may not be smaller one");
		// we need a copy, sublist is a view
		List<TargetFile> toBeCopied = new ArrayList<TargetFile>(model.listRows().subList(offset, offset + rowCount ));
		treeCopyDialog(toBeCopied, model, findForm, sink, config, owner);
	}

// -- private and protected -------------------------------------------------------------	
	
	private void init() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		JPanel basePanel = new JPanel();
		basePanel.setBorder( BorderFactory.createEmptyBorder( SwingConfig.PADDING * 2, 
				SwingConfig.PADDING, 
				SwingConfig.PADDING, 
				SwingConfig.PADDING ) );
		basePanel.setLayout( new BorderLayout() );
		registerCloseWindowKeyListener( basePanel );
		this.add( basePanel );
		
		questionLabel = new JLabel();
		basePanel.add(questionLabel, BorderLayout.NORTH);
		
		centerPanel = new JPanel();
		centerPanel.setLayout( new BoxLayout(centerPanel, BoxLayout.Y_AXIS) );
		basePanel.add(centerPanel, BorderLayout.CENTER);
		
		JPanel buttonPannel = new JPanel();
		buttonPannel.setLayout( new FlowLayout(FlowLayout.RIGHT) );
		okButton = new JButton( localizer.localize("button.ok") );
		cancelButton = new JButton( localizer.localize("button.cancel") );
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent eve){
				FileOperationDialog.this.dispose();
			}
		});
		buttonPannel.add( okButton );
		buttonPannel.add( cancelButton );
		registerCloseWindowKeyListener( buttonPannel );
		this.add(buttonPannel, BorderLayout.SOUTH);	
	}

	/**
	 * Convinience method for inheriting classes. Adds a scrollable list
	 * with the specified file objects to the center panel.
	 * @param fileList to be included in JList.
	 */
	protected void addFileList(List<TargetFile> fileList) {
		addFileList(fileList, null);
	}

	/**
	 * Convinience method for inheriting classes. Adds a scrollable list
	 * with the specified file objects to the center panel.
	 * @param fileList to be included in JList.
	 * @param baseDirectory if not null, tooltip shows relative path
	 */
	protected void addFileList(List<TargetFile> fileList, final File baseDirectory) {
		fileDisplay = new JList( fileList.toArray() );
		fileDisplay.setCellRenderer( new ListCellRenderer(){
			JLabel label = new JLabel();
			public Component getListCellRendererComponent(JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
				TargetFile file = (TargetFile) value;
				label.setText( file.getName() );
				label.setToolTipText( baseDirectory != null ? FileUtil.getRelativePath(baseDirectory.getPath(), file.getFile().getPath()) : file.getFile().getPath() );
				if( file.isIncluded() ) {
					label.setForeground( Color.BLACK );
				} else {
					label.setForeground( Color.GRAY );
				}
				return label;
			}
		});
		JScrollPane scrollPane = new JScrollPane( fileDisplay );
		scrollPane.setPreferredSize( new Dimension( SwingConfig.COMPONENT_WIDTH, 150) );
		centerPanel.add( scrollPane );
	}
	
	/**
	 * Convinience method for inheriting classes. Adds a JFileChooser for directory selection,
	 * rendered through a textfiled and a button.
	 * @param config reference to SwingConfig
	 * @param fileRepository call back hander for selected directory
	 * @param forceSelection if true, no file will initially be displayed. Otherwise,
	 * the file repository will be queried for an initial value (and must not return null)
	 */
	protected void addDirectorySelectionControl(SwingConfig config, FileRepository fileRepository, boolean forceSelection) {
		JTextField targetDirTextField = new JTextField();
		targetDirTextField.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, 
																config.getStandardComponentHight()) );
		targetDirTextField.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, 
																config.getStandardComponentHight()) );
		targetDirTextField.setEditable( false );
		if(!forceSelection) {
			targetDirTextField.setText( fileRepository.getFile().getPath() );
			targetDirTextField.setToolTipText( fileRepository.getFile().getPath() );
		}

		JButton openTargetDirFileChooserButton = new JButton( localizer.localize("button.browse") );
		BrowseButtonListener browseButtonListener = new BrowseButtonListener(targetDirTextField,
															fileRepository,
															localizer.localize("label.choose-target-directory"));
		openTargetDirFileChooserButton.addActionListener( browseButtonListener );
		TwoComponentsPanel lineTargetDir = new TwoComponentsPanel( targetDirTextField, openTargetDirFileChooserButton);
		centerPanel.add( lineTargetDir );
		centerPanel.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
	}

	
	/**
	 * Refreshs the file list with the specified list of files. Requires
	 * {@link #addFileList(List) addFileList()} to be called first (otherwise
	 * calls to this method will be ignored).
	 * @param fileList new list to display
	 */
	protected void refreshList(List<TargetFile> fileList) {
		if( fileDisplay != null ) {
			fileDisplay.setListData( fileList.toArray() );
		}
	}
}
