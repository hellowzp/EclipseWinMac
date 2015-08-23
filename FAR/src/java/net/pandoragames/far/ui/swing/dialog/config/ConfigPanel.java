package net.pandoragames.far.ui.swing.dialog.config;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.util.i18n.Localizer;

/**
 * Base class for panels in the settings window.
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
public abstract class ConfigPanel extends JPanel {
	
	protected MessageBox messageBox;
	
	protected Localizer localizer;
	
	protected Log logger = LogFactory.getLog(this.getClass());

	/**
	 * Constructor takes the settings error handler and swing configuration.
	 * @param box error handler for settings dialog
	 * @param swingConfig configuration 
	 */
	public ConfigPanel(MessageBox box, ComponentRepository repository, SwingConfig swingConfig) {
		messageBox = box;
		localizer = swingConfig.getLocalizer();
		setBorder( BorderFactory.createEmptyBorder( 0, 
				SwingConfig.PADDING, 
				SwingConfig.PADDING, 
				SwingConfig.PADDING ) );
		init(repository, swingConfig);
//		setPreferredSize( new Dimension(300,200));
	}
	
	/**
	 * Called from the constructor.
	 * @param swingConfig configuration
	 */
	protected abstract void init(ComponentRepository repository, SwingConfig swingConfig);
	
	/**
	 * Panels may object saving.
	 * @return default is true
	 */
	public boolean canSave() {
		return true;
	}
	
	/**
	 * Called when the save button on the settings panel is pressed.
	 * 
	 * @param swingConfig where changed configuration settings should be written to.
	 */
	public abstract void save(SwingConfig swingConfig);
	
}
