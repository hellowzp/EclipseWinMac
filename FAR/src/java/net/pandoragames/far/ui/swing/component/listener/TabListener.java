package net.pandoragames.far.ui.swing.component.listener;

/**
 * Interface for components on an 
 * {@link net.pandoragames.far.ui.swing.OperationTabPane OperationTabPane}.
 * Allows the component to get informed about changes of the
 * state of its tab.
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
public interface TabListener {

	/**
	 * Constant for listener registration representing any tab.
	 * A listener registered with this constant will be called for every tab event
	 * not matter what tab index. 
	 */
	public static final Integer ANYTAB = Integer.valueOf(-1);

	/**
	 * Called when the components tab is selected.
	 * @param event related event
	 */
	public void tabSelected(TabEvent event);
	
	/**
	 * Called when the components tab becomes unselected.
	 * @param event related event
	 */
	public void tabUnselected(TabEvent event);
	
	/**
	 * Called when the components tab is enabled.
	 */
	// public void tabEnabled();
	
	/**
	 * Called when the components tab becomes disabled.
	 */
	// public void tabDisabled();
}
