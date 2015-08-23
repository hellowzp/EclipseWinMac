package net.pandoragames.far.ui.swing.component;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import net.pandoragames.far.ui.swing.SwingConfig;

/**
 * Provides space for two neighbouring components in a single line.
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
public class TwoComponentsPanel extends JPanel
{
	
	/**
	 * Create the line with the two components it shall hold.
	 * @param leftComponent the left component
	 * @param rightComponent the right component
	 */
	public TwoComponentsPanel(Component leftComponent, Component rightComponent) {
		setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
		setLeftComponent( leftComponent );
		add( Box.createRigidArea( new Dimension(SwingConfig.PADDING, 1) ), 1);
		setRightComponent( rightComponent );
	}
	
	/**
	 * Gets the left component.
	 * @return left component
	 */
	public Component getLeftComponent()
	{
		return getComponent( 0 );
	}
	
	/**
	 * Sets the left component.
	 * @param leftComponent the left component
	 */
	public void setLeftComponent(Component leftComponent)
	{
		this.add( leftComponent, 0);
	}
	
	/**
	 * Gets the right component.
	 * @return right component
	 */
	public Component getRightComponent()
	{
		return getComponent( 2 );
	}
	
	/**
	 * Sets the right component.
	 * @param rightComponent the right component
	 */
	public void setRightComponent(Component rightComponent)
	{
		this.add( rightComponent, 2);
	}
	
}
