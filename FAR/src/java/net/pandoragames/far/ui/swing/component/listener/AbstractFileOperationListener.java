package net.pandoragames.far.ui.swing.component.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.pandoragames.far.ui.model.OperationType;
import net.pandoragames.far.ui.model.ProgressListener;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;

/**
 * Abstract base class for ActionListeners that operate on the set of selected files.
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
public abstract class AbstractFileOperationListener extends AbstractAction implements Runnable {
	
	private OperationType operation = OperationType.REPLACE;
	private List<ProgressListener> listener;
	protected Log logger = LogFactory.getLog( this.getClass() );
	/**
	 * The FileSetTableModel supplied with the constructor, available for inheriting classes.
	 */
	protected FileSetTableModel tableModel;
	/**
	 * The listener is instantiated with a reference to the current FileSetTableModel.
	 * @param model current FileSetTableModel
	 */
	protected AbstractFileOperationListener(FileSetTableModel model) {
		tableModel = model;
	}
	/**
	 * The listener is instantiated with a reference to the current FileSetTableModel
	 * and a label for this Action.
	 * 
	 * @param model current FileSetTableModel
	 * @param name label for controles
	 */
	protected AbstractFileOperationListener(FileSetTableModel model, String name) {
		super(name);
		tableModel = model;
	}
	/**
	 * Calls the {@link #execute(TargetFile) execute()} method for every
	 * selected file from the FileSetTableModel.
	 */
	public void actionPerformed(ActionEvent e) {
		if( listener != null ) {
			Thread opthread = new Thread( this );
			opthread.start();
		} else {
			for(TargetFile row : tableModel.listRows() ) {
				row.clear();
				if( row.isSelected() ) {
					execute( row );
				}
			}
			tableModel.notifyUpdate();
		}
	}
	public void run(){
		int total = tableModel.getSelectedRowCount();
		int count = 1;
		for(ProgressListener pl : listener) pl.operationStarted( operation );
		for(TargetFile row : tableModel.listRows() ) {
			row.clear();
			if( row.isSelected() ) {
				execute( row );
				for(ProgressListener pl : listener) pl.operationProgressed(count++, total, operation);
			}
		}	
		SwingUtilities.invokeLater( new Runnable(){public void run(){tableModel.notifyUpdate();}} );
		for(ProgressListener pl : listener) pl.operationTerminated(operation);
	}
	/**
	 * This method will be called for every <i>selected</i> file from the FileSetTableModel.
	 * 
	 * @param targetFile on which operations must be performed
	 */
	protected abstract void execute(TargetFile targetFile);
	
	/**
	 * Adds a ProgressListener that will be informed about operation progress.
	 * The ProgressListener will be called with OperationType.REPLACE.
	 * Adding a ProgressListener will result in the operations being executed in a separate thread.
	 * @param pl ProgressListener
	 */
	public void addProgressListener(ProgressListener pl) {
		if(pl == null) return;
		if(listener == null) listener = new ArrayList<ProgressListener>();
		listener.add(pl);
	}
}
