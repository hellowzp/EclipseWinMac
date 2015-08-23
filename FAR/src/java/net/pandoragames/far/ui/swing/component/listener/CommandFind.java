package net.pandoragames.far.ui.swing.component.listener;

import java.awt.event.ActionEvent;
import java.util.List;

import net.pandoragames.far.ui.UIFace;
import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.TargetFile;

/**
 * The Find command. 
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
public class CommandFind extends CommandExecutor {
	
	/**
	 * Search criteria. Available for inheriting classes.
	 */
	protected FindForm findForm;

	/**
	 * Takes the UI independend command implementation as argument.
	 * @param uiBean business logic implementation.
	 */
	public CommandFind(UIFace uiBean) {
		super(uiBean);
	}

	/**
	 * Executes the command.
	 */
	public void actionPerformed(ActionEvent event)
	{
		applyReset();
		fileSetTable.clearInfo();
		FindCommand ce = new FindCommand();
		ce.setDaemon( true );
		ce.start();
	}
	
	/**
	 * Sets the container for the query criteria
	 * @param findForm query criteria
	 */
	public void setFindForm(FindForm findForm)
	{
		this.findForm = findForm;
	}
	
	
	class FindCommand extends Thread {
		
		public void run() {
			List<TargetFile> result = uibean.findFiles( findForm );
			fileSetTable.setFileList( result, findForm.getBaseDirectory() );
		}
	}

}
