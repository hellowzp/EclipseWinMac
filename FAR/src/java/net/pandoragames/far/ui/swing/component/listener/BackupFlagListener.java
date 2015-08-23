package net.pandoragames.far.ui.swing.component.listener;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Informs interested components about the state of the doBackup flag.
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
public class BackupFlagListener implements ItemListener {

	private List<Component> toBeToggled = new ArrayList<Component>();
	
	/**
	 * {@inheritDoc}
	 */
	public void itemStateChanged(ItemEvent event) {
		for(Component component: toBeToggled) {
			component.setEnabled( (ItemEvent.SELECTED == event.getStateChange()) );
		}
	}
	
	/**
	 * Adds a component that will be toggled according to the state of the doBackup flag.
	 * @param component to be added to the list
	 */
	public void addToBeToggled(Component component) {
		if( component != null ) {
			toBeToggled.add(component);
		}
	}
}
