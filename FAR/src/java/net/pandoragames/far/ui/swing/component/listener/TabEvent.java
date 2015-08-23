package net.pandoragames.far.ui.swing.component.listener;

import java.util.EventObject;

import javax.swing.JTabbedPane;

/**
 * Event object for {@link TabListener TabListener}.
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
public class TabEvent extends EventObject {

	public enum TAB_EVENT_TYPE { 	/** Event type for a tab getting active.*/
									SELECTED, 
									/** Event type for a tab getting inactive.*/
									UNSELECTED };
	
	private int tabIndex;
	private TAB_EVENT_TYPE eventType;
	
	/**
	 * Counstructor.
	 * @param source JTabbedPane that triggered the event
	 * @param index index of affected tab
	 * @param type event type
	 */
	public TabEvent(JTabbedPane source, int index, TAB_EVENT_TYPE type) {
		super(source);
		tabIndex = index;
		eventType = type;
	}
	/**
	 * Returns the event type this object represents
	 * @return event type
	 */
	public TAB_EVENT_TYPE getEventType() {
		return eventType;
	}
	
	/**
	 * Returns the index of the affected tab.
	 * @return tab index
	 */
	public int getTabIndex() {
		return tabIndex;
	}
	
	
}
