package net.pandoragames.far.ui.swing.component.listener;

import java.awt.event.ActionEvent;

import net.pandoragames.far.ui.UIFace;
import net.pandoragames.far.ui.model.ReplaceForm;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.SwingConfig;

/**
 * The replace command.
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
public class CommandReplace extends CommandExecutor {
	/**
	 * Search criteria. Available for inheriting classes.
	 */
	protected ReplaceForm replaceForm;
	
	private SwingConfig config;

	/**
	 * Takes the UI independend command implementation as argument.
	 * @param uiBean business logic implementation.
	 */
	public CommandReplace(SwingConfig config, UIFace uiBean) {
		super(uiBean);
		this.config = config;
	}

	/**
	 * Executes the command.
	 */
	public void actionPerformed(ActionEvent event)
	{
		applyReset();
		fileSetTable.clearInfo();
		ReplaceCommand rc = new ReplaceCommand();
		rc.setDaemon( true );
		rc.start();
	}

	/**
	 * Sets the container for the operation parameter
	 * @param form operation parameter
	 */
	public void setReplaceForm(ReplaceForm form)
	{
		replaceForm = form;
	}
	
	class ReplaceCommand extends Thread {
		
		public void run() {
			if( ! config.isProcessBinary() ) {
				for(TargetFile file : fileSetTable.listRows()) {
					if( file.isSelected() && file.isBinaryFile()) {
						file.setSelected( false );
						file.info( config.getLocalizer().localize("label.unprocessed-binary-file") );
					}
				}
			}
			uibean.replace( replaceForm, fileSetTable.listRows() );
			fileSetTable.notifyUpdate();
		}
	}
}
