package net.pandoragames.far.ui.swing.component.listener;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import net.pandoragames.far.ui.model.OperationType;
import net.pandoragames.far.ui.model.ProgressListener;

/**
 * Synchronises a JProgressBar Swing component.
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
public class ProgressBarUpdater implements ProgressListener
{
	private enum TODO {START, UPDATE, STOP};
	
	private JProgressBar progBar;
	private int totalCount = -1;
	private int progress;
	private OperationType barType = OperationType.NONE;
	/**
	 * Default Constructor.
	 * Call methods of this object will only have effect
	 * if a non null JProgressBar has been supplied.
	 */
	public ProgressBarUpdater(){}
	/**
	 * Constructor that takes the JProgressBar to be updated.
	 * @param progressBar JProgressBar to be updated
	 */
	public ProgressBarUpdater(JProgressBar progressBar) {
		setProgressBar(progressBar);
	}
	/**
	 * Sets the JProgressBar to be updated.
	 * @param progressBar JProgressBar to be updated
	 */
	public void setProgressBar(JProgressBar progressBar) {
		progBar = progressBar;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void operationStarted(OperationType type)
	{
		barType = type;
		// enable the progress bar
		SwingUtilities.invokeLater( new EventQueueThread(TODO.START) );
	}

	/**
	 * {@inheritDoc}
	 */
	public void operationProgressed(int count, int total, OperationType type)
	{
		totalCount = total;
		progress = count;
		// update the bar
		SwingUtilities.invokeLater( new EventQueueThread(TODO.UPDATE) );
	}

	/**
	 * {@inheritDoc}
	 */
	public void operationTerminated( OperationType type )
	{
		totalCount = -1;
		// disable the progress bar
		SwingUtilities.invokeLater( new EventQueueThread(TODO.STOP) );
	}

	/**
	 * {@inheritDoc}
	 */
	public void operationAborted( OperationType type )
	{
		totalCount = -1;
		// disable the progress bar
		SwingUtilities.invokeLater( new EventQueueThread(TODO.STOP) );
	}

	class EventQueueThread implements Runnable {
		private TODO whatToDo; // -1 = stop, 0 = update, 1 = start
		public EventQueueThread(TODO cmd) {
			whatToDo = cmd;
		}
		public void run() {
			if( progBar == null ) {
				return;
			}
			if( whatToDo == TODO.STOP) {	
				progBar.setEnabled( false );
				progBar.setIndeterminate( false );						
				progBar.setStringPainted( false );
				progBar.setMaximum( 0 );
				progBar.setValue( 0 );
			} else if(whatToDo == TODO.START ){	 
				progBar.setEnabled( true );
				progBar.setIndeterminate( OperationType.FIND == barType );
				progBar.setString( "..." );
				progBar.setStringPainted( true );
			} else if( whatToDo == TODO.UPDATE) { 	// UPDATE
				if(( totalCount > 0 ) && ( OperationType.FIND != barType )) {
						progBar.setMaximum( totalCount );
						progBar.setValue( progress );
				}
				String label = String.valueOf( progress );
				if( totalCount > 0 ) label = label + "/" +String.valueOf( totalCount );
				progBar.setString( label );
			}
		}
	}
}
