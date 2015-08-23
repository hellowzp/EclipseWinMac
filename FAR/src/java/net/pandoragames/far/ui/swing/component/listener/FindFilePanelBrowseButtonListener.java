package net.pandoragames.far.ui.swing.component.listener;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import net.pandoragames.far.ui.model.FileNamePattern;
import net.pandoragames.far.ui.model.FileRepository;

/**
 * A specialised variant of the BrowseButtonListener that accepts directories and files.
 * This listener controles also the state of two other, related controles; a combobox and
 * and a checkbox, which describe the file name pattern.
 *
 * @author Olivier Wehner at 23/11/2009
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
public class FindFilePanelBrowseButtonListener implements ActionListener 
{
	private Component parentComponent;
	private FileRepository config;
	private String label;
	private List<ActionListener> listenerList;
	private List<Component> toBeDisabled = new ArrayList<Component>();
	private JComboBox comboBox;
	private JCheckBox regexBox;
	private JCheckBox subdirBox;
	private CommandFind findCommand;
	private ResetDispatcher resetCommand;
	
	/**
	 * Constructor for this listener.
	 * @param textField where the selected path is displayed
	 * @param fileRepository where the selected file or directory is stored
	 * @param buttonLabel label for the file chooser dialogue
	 * @param command to trigger the find command
	 */
	public FindFilePanelBrowseButtonListener(JComboBox textField, 
												FileRepository fileRepository, 
												String buttonLabel,
												CommandFind command,
												ResetDispatcher resetDispatcher) {
		parentComponent = textField;
		config = fileRepository;
		label = buttonLabel;
		findCommand = command;
		resetCommand = resetDispatcher;
	}
	/**
	 * Opens a JFileChooser dialogue that allows to select a directory.
	 * @param event triggered by a pressed "browse" button
	 */
	public void actionPerformed(ActionEvent event) {
		JFileChooser fileChooser = new JFileChooser( config.getFile() );
		fileChooser.setDialogTitle( label );
		fileChooser.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
		int returnVal = fileChooser.showOpenDialog( parentComponent );
	    if( returnVal == JFileChooser.APPROVE_OPTION ) {
	    	if( fileChooser.getSelectedFile().isFile() ) {
	    		File dir = fileChooser.getSelectedFile().getParentFile();
	    		if( isAcceptDirectory(dir, event) ) {
	    			FileNamePattern fileName = new FileNamePattern( fileChooser.getSelectedFile().getName() );
	    			resetCommand.actionPerformed( event );
	    			if( comboBox != null ) {
	    				comboBox.setSelectedItem( fileName );
	    			}
	    			if( regexBox != null ) {
	    				regexBox.setSelected( false );
	    			}
	    			if( subdirBox != null ) {
	    				subdirBox.setSelected( false );
	    			}
	    			for( Component c : toBeDisabled ) {
	    				c.setEnabled( false );
	    			}
	    			findCommand.actionPerformed( event );
	    		}
	    	} else {
	    		isAcceptDirectory(fileChooser.getSelectedFile(), event);
	    	}
	    }
	}
	
	/**
	 * Accepts itself ActionListener.
	 * @param listener notified when a directory was selected.
	 */
	public void addActionListener(ActionListener listener) {
		if( listenerList == null ) {
			listenerList = new ArrayList<ActionListener>();
		}
		listenerList.add( listener );
	}
	/**
	 * Adds a component that should be disabled if the file selected is not a 
	 * directory but a single, ordinary file.
	 * @param c to be disabled on file selection
	 */
	public void addComponentToBeDisabledForSingleFiles(Component c) {
		if( c != null ) {
			toBeDisabled.add( c );
		}
	}
	
	/**
	 * Sets the JComboBox that will be manipulated by this class.
	 * If the users selects a single file instead of a directory, 
	 * its name will be displayed in the list.
	 * @param box used for displaying selected file name
	 */
	public void setComboBox(JComboBox box) {
		comboBox = box;
	}
	
	/**
	 * Sets the JCheckBox that holds the information about whether or not the
	 * file name pattern is to be understood as regular expression or not. If a single
	 * file is selected, the butten will be unselected.
	 * @param checkBox regex flag for file name pattern.
	 */
	public void setRegexCheckBox(JCheckBox checkBox) {
		regexBox = checkBox;
	}
	
	/**
	 * Sets the CheckBox that holds the information about whether or not 
	 * subdirectories should be used. If a single file is selected, the 
	 * box will be unselected.
	 * @param checkBox flag for subdirectories
	 */
	public void setSubdirCheckBox(JCheckBox checkBox) {
		subdirBox = checkBox;
	}
		
	private boolean isAcceptDirectory(File directory, ActionEvent event) {
		if( config.setFile( directory ) ) {
	    	if( listenerList != null ) {
	    		for(ActionListener a : listenerList) {
	    			a.actionPerformed( event );
	    		}
	    	}
	    	return true;
    	} else {
    		return false;
    	}
	}
}