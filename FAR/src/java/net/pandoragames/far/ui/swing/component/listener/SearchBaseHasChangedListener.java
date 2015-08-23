package net.pandoragames.far.ui.swing.component.listener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * Notified when the search criteria have been substantially changed.
 * @author Olivier Wehner at 07.03.2008
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
public class SearchBaseHasChangedListener extends ResetDispatcher implements ItemListener, PropertyChangeListener {
	/**
	 * {@inheritDoc}
	 */
	public void itemStateChanged(ItemEvent event) {
		dispatchEvent( event );
	}
	/**
	 * {@inheritDoc}
	 */	
	public void propertyChange(PropertyChangeEvent evt) {
		dispatchEvent( evt );			
	}	
}
