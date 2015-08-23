package net.pandoragames.far.ui.swing.component.listener;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import net.pandoragames.far.ui.UIFace;
import net.pandoragames.far.ui.model.Resetable;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;

/**
 * Base class for the Find and the Replace command. 
 * This classes serve as a bridge between Swing specific classes
 * and the business logic. 
 * They actually start a Threads in which the command will be processed.
 * 
 * @author Olivier Wehner at 26.02.2008
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
public abstract class CommandExecutor implements ActionListener
{
	private List<Resetable> resetables;
	
	/**
	 * Interface to business logic. Available for inheriting classes.
	 */
	protected UIFace uibean;
	/**
	 * Result table. Available for inheriting classes.
	 */
	protected FileSetTableModel fileSetTable;
	
	/**
	 * Takes the UI independend command implementation as argument.
	 * @param uiBean business logic implementation.
	 */
	public CommandExecutor(UIFace uiBean) {
		uibean = uiBean;
	}

	/**
	 * Sets the Swing table that displays the result
	 * @param fileSetTable result table
	 */
	public void setFileSetTable(FileSetTableModel fileSetTable)
	{
		this.fileSetTable = fileSetTable;
	}

	/**
	 * Adds some component that should be reseted when (i.e. before)
	 * the command is triggered).
	 * @param toBeReseted to be reseted on command execution
	 */
	public void addResetable(Resetable toBeReseted) {
		if(resetables == null) resetables = new ArrayList<Resetable>();
		resetables.add( toBeReseted );
	}
	
	/**
	 * Resets all registered resetable components.
	 */
	protected void applyReset() {
		if(resetables != null) {
			for(Resetable resetable : resetables) {
				resetable.reset();
			}
		}
	}
}
