package net.pandoragames.far.ui.swing.component.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

import net.pandoragames.far.ui.model.FileRepository;

/**
 * Opens a JFileChooser dialogue that allows to select a directory.
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
public class BrowseButtonListener implements ActionListener
{
	private JTextField display;
	private FileRepository config;
	private String label;
	private List<ActionListener> listenerList;
	/**
	 * Constructor for this listener.
	 * @param textField where the selected path is displayed
	 * @param fileRepository where the selected file or directory is stored
	 * @param titleLabel label for the file chooser dialogue title
	 */
	public BrowseButtonListener(JTextField textField, FileRepository fileRepository, String titleLabel) {
		display = textField;
		config = fileRepository;
		label = titleLabel;
	}
	/**
	 * Opens a JFileChooser dialogue that allows to select a directory.
	 * @param event triggered by a pressed "browse" button
	 */
	public void actionPerformed(ActionEvent event) {
		JFileChooser fileChooser = new JFileChooser( config.getFile() );
		fileChooser.setDialogTitle( label );
		fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		int returnVal = fileChooser.showOpenDialog( display );
	    if( returnVal == JFileChooser.APPROVE_OPTION ) {
	    	if( config.setFile( fileChooser.getSelectedFile() ) ) {
		    	display.setText( fileChooser.getSelectedFile().getPath() );
		    	display.setToolTipText( fileChooser.getSelectedFile().getPath() );
		    	if( listenerList != null ) {
		    		for(ActionListener a : listenerList) {
		    			a.actionPerformed( event );
		    		}
		    	}
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
}
