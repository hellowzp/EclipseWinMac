package net.pandoragames.far.ui.swing.dialog.config;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.far.ui.swing.component.MessageLabel;
import net.pandoragames.far.ui.swing.dialog.SubWindow;
import net.pandoragames.util.i18n.Localizer;

/**
 * The new configuration dialog
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
public class ConfigurationDialog extends SubWindow {

	/** List of defined tabs on this dialog. */
	public enum CONFIG_TAB { MISC, FILE_LIST, CHARSET, MIME};
	
	private SwingConfig swingConfig;
	private MessageLabel messageBox;
	private List<ConfigPanel> subPanels = new ArrayList<ConfigPanel>();
	private Localizer localizer;
	private FileSetTableModel tableModel;

	/**
	 * Constructor takes root window, the application configuration and the
	 * component repository.
	 * @param owner root window
	 * @param config the configuration object where changes are applied to
	 * @param repository holding listeners and other shared components.
	 */
	public ConfigurationDialog(JFrame owner, SwingConfig config, ComponentRepository repository) {
		this(owner, config, repository, CONFIG_TAB.MISC);
	}
	/**
	 * Constructor takes root window, the application configuration and the
	 * component repository.
	 * @param owner root window
	 * @param config the configuration object where changes are applied to
	 * @param repository holding listeners and other shared components.
	 * @param selectedTab selected category when opening dialog.
	 */
	public ConfigurationDialog(JFrame owner, SwingConfig config, ComponentRepository repository, CONFIG_TAB selectedTab) {
		super(owner, config.getLocalizer().localize("menu.settings"), true);
		swingConfig = config;
		localizer = config.getLocalizer();
		tableModel = repository.getTableModel();
		init(repository, selectedTab);
	}
	
	private void init(ComponentRepository repository, CONFIG_TAB selectedTab) {
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setLayout( new BorderLayout() );
		this.setResizable(false);

		messageBox = new MessageLabel();
		add(messageBox, BorderLayout.NORTH);
		
		JTabbedPane tabPane = new JTabbedPane();
		addConfigTab( new MiscellaneousPanel(swingConfig, repository, messageBox), "button.miscellaneous", tabPane);
		addConfigTab( new FileListConfigPanel(swingConfig, repository, messageBox), "button.file-list", tabPane);
		addConfigTab( new CharsetConfigPanel(swingConfig, repository, messageBox), "button.charset", tabPane);
		addConfigTab( new MimeConfigPanel(swingConfig, repository, messageBox), "button.mime", tabPane);
		tabPane.setSelectedIndex( selectedTab.ordinal() );
		add(tabPane, BorderLayout.CENTER);
		
		// buttons
		JPanel buttonPannel = new JPanel();
		buttonPannel.setAlignmentX( Component.LEFT_ALIGNMENT );
		buttonPannel.setLayout( new FlowLayout( FlowLayout.TRAILING ) );
		// cancel
		JButton cancelButton = new JButton( swingConfig.getLocalizer().localize("button.cancel"));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ConfigurationDialog.this.dispose();
			}						
		});
		buttonPannel.add( cancelButton );	
		// save
		JButton saveButton = new JButton( swingConfig.getLocalizer().localize("button.save"));
		saveButton.addActionListener( new SaveButtonListener() );
		buttonPannel.add( saveButton );	
		this.getRootPane().setDefaultButton(saveButton);
		add( buttonPannel, BorderLayout.SOUTH);
		
		placeOnScreen( swingConfig.getScreenCenter() );
	}
	
	private void addConfigTab(ConfigPanel tabPanel, String tabTitle, JTabbedPane tabPane) {
		tabPane.add( localizer.localize(tabTitle), tabPanel );
		registerCloseWindowKeyListener(tabPanel );
		subPanels.add(tabPanel);
	}
	
	/**
	 * Changes the Configuration and Form objects and informs any interested listener.
	 */
	class SaveButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if( canSave() ) {
				for(ConfigPanel cfg : subPanels) {
					cfg.save( swingConfig );
				}
				tableModel.notifyUpdate();
				ConfigurationDialog.this.dispose();
			}
		}
		private boolean canSave() {
			for(ConfigPanel cfg : subPanels) {
				if( ! cfg.canSave() ) return false;
			}
			return true;
		}
	}
}
