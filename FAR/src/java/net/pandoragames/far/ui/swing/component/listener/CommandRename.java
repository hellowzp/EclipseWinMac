package net.pandoragames.far.ui.swing.component.listener;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.pandoragames.far.PatternFilter;
import net.pandoragames.far.ui.OverwriteFileCallback;
import net.pandoragames.far.ui.UIFace;
import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.OperationType;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.util.i18n.Localizer;

import org.apache.commons.logging.LogFactory;

/**
 * The Rename command.
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
public class CommandRename extends CommandExecutor implements Action {

	private Localizer localizer;
	private JFrame rootWindow;
	private boolean enabled;
	protected FindForm findForm; // infos from FIND form needed for RENAME!
	private List<PropertyChangeListener> propertyListener;
	private Map<String, Object> properties = new HashMap<String, Object>();
	
	public CommandRename(UIFace uiBean, Localizer localizer, JFrame root) {
		super(uiBean);
		properties.put( Action.ACTION_COMMAND_KEY, OperationType.RENAME.name() );
		properties.put(Action.NAME, localizer.localize("button.rename"));
		this.localizer = localizer;
		rootWindow = root;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void actionPerformed(ActionEvent e) {
		applyReset();
		setEnabled( false );
		RenameCommand ce = new RenameCommand();
		ce.setDaemon( true );
		ce.start();
	}
	
	
	/**
	 * The RENAME command does not use the RenameForm object, but rather
	 * needs some information from the FindForm.
	 * @param findForm query criteria
	 */
	public void setFindForm(FindForm findForm)
	{
		this.findForm = findForm;
	}

// -- methods from interface Action -------------------------------------------------------	
	
	/**
	 * {@inheritDoc}
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if( listener != null ) {
			if( propertyListener == null ) propertyListener = new ArrayList<PropertyChangeListener>();
			propertyListener.add( listener );
		}			
	}
	/**
	 * {@inheritDoc}
	 */
	public Object getValue(String key) {
		return properties.get( key );
	}
	/**
	 * {@inheritDoc}
	 */
	public boolean isEnabled() {
		return enabled;
	}
	/**
	 * {@inheritDoc}
	 */
	public void putValue(String key, Object value) {
		Object oldValue = properties.put(key, value);
		firePropertyChangeEvent( key, oldValue, value);
	}
	/**
	 * {@inheritDoc}
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if( propertyListener != null && listener != null ) {
			propertyListener.remove( listener );
		}
	}
	/**
	 * {@inheritDoc}
	 */
	public void setEnabled(boolean enabled) {
		boolean state = this.enabled;
		this.enabled = enabled;
		firePropertyChangeEvent( "enabled", Boolean.valueOf(state), Boolean.valueOf(enabled));
	}
	
	private void firePropertyChangeEvent(String property, Object oldValue, Object newValue) {
		if(( propertyListener != null ) &&
				((oldValue != null && ! oldValue.equals( newValue )) 
						|| (oldValue == null && newValue != null))) {
			PropertyChangeEvent event = new PropertyChangeEvent(this, property, oldValue, newValue);
			for(PropertyChangeListener listener : propertyListener) {
				listener.propertyChange( event );
			}			
		}
	}
	
// -- inner class ---------------------------------------------------------------
	
	/**
	 * Executes the rename command in a thread of its own.
	 */
	class RenameCommand extends Thread {		
		public void run() {
			List<TargetFile> result = uibean.rename( fileSetTable.listRows(), new OverwriteCallBackHandler() );
			PatternFilter patternFilter = findForm.createPatternFilter();
			for(TargetFile file : result) {
				if( file.isSelected() && ! patternFilter.accept(file.getFile().getParentFile(), file.getName()) ) {
					file.setIncluded( false );
					// file.setSelected( false );
				}
			}
			fileSetTable.setFileList( result, findForm.getBaseDirectory() );
		}
	}
	
	/**
	 * Prepares a user call back.
	 */
	class OverwriteCallBackHandler implements OverwriteFileCallback {

		private int state = 0; // -1 = no to all, 0 = neutral, +1 = yes to all
		
		public boolean askForOverwrite(File toBeReplaced) {
			if( state < 0 ) return false;
			if( state > 0 ) return true;
			OverwriteCallBackCaller popupCaller = new OverwriteCallBackCaller( toBeReplaced );
			try {
				SwingUtilities.invokeAndWait( popupCaller );
				switch( popupCaller.getConfirmation() ) {
				case 0 : return true;
				case 1 : return false;
				case 2 : state = 1; return true;
				case 3 : state = -1; return false;
				default : uibean.abort(); return false;
				}
			} catch(Exception x) { // InterruptedException or InvocationTargetException
				LogFactory.getLog( CommandRename.class ).error( x.getClass().getSimpleName() 
						+ " waiting for user feedback: " + x.getMessage(), x);
				uibean.abort();
			}
			
			return false;
		}
		
	}
	
	/**
	 * Opens a popup window to request user feed back.
	 */
	class OverwriteCallBackCaller implements Runnable {
		private File toBeReplaced;
		private int confirmation;
		OverwriteCallBackCaller(File file) {
			toBeReplaced = file;
		}
		public void run() {
			String title = localizer.localize( "label.replace" );
			String message = localizer.localize("question.overwrite", toBeReplaced.getPath());
			String[] options = new String[] {
					localizer.localize("button.yes"),
					localizer.localize("button.no"),
					localizer.localize("button.yes-to-all"),
					localizer.localize("button.no-to-all"),
					localizer.localize("button.abort")
			};
			confirmation = JOptionPane.showOptionDialog(rootWindow, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);					
		}	
		public int getConfirmation() {
			return confirmation;
		}
	}
}
