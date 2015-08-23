package net.pandoragames.far.ui.swing.menu;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.nio.charset.Charset;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import net.pandoragames.far.ui.model.OperationType;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.MacOSXMenuAdapter;
import net.pandoragames.far.ui.swing.component.listener.ConfirmReplaceListener;
import net.pandoragames.far.ui.swing.dialog.HelpView;
import net.pandoragames.far.ui.swing.dialog.LoadFormDialog;
import net.pandoragames.far.ui.swing.dialog.SaveFormDialog;
import net.pandoragames.far.ui.swing.dialog.SettingsDialog;
import net.pandoragames.far.ui.swing.dialog.config.ConfigurationDialog;
import net.pandoragames.util.i18n.Localizer;

/**
 * Utility class that creates the application menu.
 * Just keeps the ugly code out of the way.
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
public class MenuFactory {

	private SwingConfig config;
	private ComponentRepository repository; 
	private Localizer localizer;
	
	/**
	 * Constructor.
	 * @param swingConfig configuration object
	 * @param componentRepository repository of shared components
	 */
	public MenuFactory(SwingConfig swingConfig, ComponentRepository componentRepository) {
		if(swingConfig == null) throw new NullPointerException("SwingConfig object was null");
		if(componentRepository == null) throw new NullPointerException("ComponentRepository object was null");
		config = swingConfig;
		repository = componentRepository;
		localizer = componentRepository.getLocalizer();
	}
	
	/**
	 * Creates and returns the application menu.
	 * @return application menu.
	 */
	public JMenuBar createMenu() {
		FarMenuBar menubar = new FarMenuBar();
		
		addFARMenu( menubar );
		addSearchMenu( menubar );
		addSelectionMenu( menubar );
		addFileMenu( menubar );
		addToolMenu( menubar );
		addHelpMenu( menubar );
		
		return menubar;
	}
	
	private void addFARMenu(FarMenuBar menubar) {
		if(!SwingConfig.isMacOSX()) {
			// -- First Menu (FAR)
			JMenu farMenu = new JMenu( SwingConfig.APPLICATION_NAME );
			farMenu.setMnemonic( config.getAccessKey("menu.far") );
			menubar.add( farMenu );
			JMenuItem settingsMenu = new JMenuItem( localizer.localize("menu.settings") );
			settingsMenu.addActionListener( new SettingsListener(repository.getRootWindow(), config, repository ) );
			farMenu.add( settingsMenu );
			JMenuItem quitMenu = new JMenuItem( localizer.localize("menu.quit") );
			quitMenu.addActionListener( repository.getExitListener() );
			quitMenu.setMnemonic( KeyEvent.VK_Q );
			farMenu.add( quitMenu );
		} else {
			// On Mac OS we do it slightly different
			String aboutTitle = localizer.localize("menu.about");
			menubar.getMacOSXMenuAdapter().registerEventHandler(new AboutListener(repository.getRootWindow(), 
																					aboutTitle,
																					"about.html",
																					config.getScreenCenter()),
																MacOSXMenuAdapter.OSXCOMMAND.About);
			menubar.getMacOSXMenuAdapter().registerEventHandler(new SettingsListener(repository.getRootWindow(), config, repository ),
																MacOSXMenuAdapter.OSXCOMMAND.Preferences);
			menubar.getMacOSXMenuAdapter().registerEventHandler(repository.getExitListener(), MacOSXMenuAdapter.OSXCOMMAND.Quit);
		}
	}
	
	private void addSearchMenu(FarMenuBar menubar) {	
		// -- Search Menu
		JMenu searchMenu = new JMenu( localizer.localize("menu.search") );
		searchMenu.setMnemonic( config.getAccessKey("menu.search") );
		menubar.add( searchMenu );
		JMenuItem newMenu = new JMenuItem( localizer.localize("menu.new") );
		newMenu.addActionListener( repository.getResetDispatcher() );
		searchMenu.add( newMenu );
		JMenu loadMenu = new JMenu( localizer.localize("menu.load") );
		JMenuItem loadFind = new JMenuItem( localizer.localize("menu.find") + "..." );
		loadFind.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				LoadFormDialog loadDialog = new LoadFormDialog(repository.getRootWindow(), repository.getFindForm(), config);
				loadDialog.pack();
				loadDialog.setVisible( true );				
			}
		});
		loadMenu.add( loadFind );
		JMenuItem loadReplace = new JMenuItem( localizer.localize("menu.replace") + "..." );
		loadReplace.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				LoadFormDialog loadDialog = new LoadFormDialog(repository.getRootWindow(), repository.getReplaceForm(), config);
				loadDialog.pack();
				loadDialog.setVisible( true );				
			}
		} );
		activateAfterFind( loadReplace );		
		loadMenu.add( loadReplace );
		searchMenu.add( loadMenu );
		JMenu saveMenu = new JMenu( localizer.localize("menu.save") );
		JMenuItem saveFind = new JMenuItem( localizer.localize("menu.find") + "..." );
		saveFind.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				SaveFormDialog saveDialog = new SaveFormDialog(repository.getRootWindow(), repository.getFindForm(), config);
				saveDialog.pack();
				saveDialog.setVisible( true );				
			}
		});
		saveMenu.add( saveFind );
		JMenuItem saveReplace = new JMenuItem( localizer.localize("menu.replace") + "..." );
		saveReplace.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				SaveFormDialog saveDialog = new SaveFormDialog(repository.getRootWindow(), repository.getReplaceForm(), config);
				saveDialog.pack();
				saveDialog.setVisible( true );				
			}
		} );
		activateAfterFind( saveReplace );
		saveMenu.add( saveReplace );
		searchMenu.add( saveMenu );
		
		// separator
		searchMenu.add( new JSeparator( JSeparator.HORIZONTAL ) );

		JMenuItem findMenu = new JMenuItem( localizer.localize("menu.find") );
		findMenu.addActionListener( repository.getFindCommand() );
		searchMenu.add( findMenu );
		JMenuItem replaceMenu = new JMenuItem( localizer.localize("menu.replace") );
		activateAfterFind( replaceMenu );
		repository.getSearchBaseListener().addToBeDisabled( replaceMenu );
		ConfirmReplaceListener replaceListener = new ConfirmReplaceListener(repository.getRootWindow(), config, repository.getReplaceForm());
		replaceListener.addActionListener( repository.getReplaceCommand() );
		replaceMenu.addActionListener( replaceListener );
		searchMenu.add( replaceMenu );
		JMenuItem renameMenu = new JMenuItem( repository.getRenameCommand() );
		activateAfterFind( renameMenu );
		searchMenu.add( renameMenu );
	}
	
	private void addSelectionMenu(FarMenuBar menubar) {	
		JMenu selectMenu = new JMenu( localizer.localize("menu.select") );
		selectMenu.setMnemonic( config.getAccessKey("menu.select") );
		menubar.add( selectMenu );
		JMenuItem selectAll = new JMenuItem( localizer.localize("menu.select-all") );
		activateAfterFind( selectAll );
		selectAll.addActionListener( new StandardFileSelector( repository.getTableModel(), 1) );
		selectMenu.add( selectAll );
		JMenuItem selectNone = new JMenuItem( localizer.localize("menu.select-none") );
		activateAfterFind( selectNone );
		selectNone.addActionListener( new StandardFileSelector( repository.getTableModel(), -1) );
		selectMenu.add( selectNone );
		JMenuItem selectInvert = new JMenuItem( localizer.localize("menu.invert-selection") );
		activateAfterFind( selectInvert );
		selectInvert.addActionListener( new StandardFileSelector( repository.getTableModel(), 0) );
		selectMenu.add( selectInvert );
		JMenuItem selectName = new JMenuItem( localizer.localize("menu.select-by-name") );
		activateAfterFind( selectName );
		selectName.addActionListener( new SelectByNameListener(repository.getRootWindow(), repository.getTableModel(), config) );
		selectMenu.add( selectName );
		JMenuItem selectContent = new JMenuItem( localizer.localize("menu.select-by-content") );
		activateAfterFind( selectContent );
		selectContent.addActionListener( new SelectByContentListener(repository.getRootWindow(), repository.getTableModel(), repository, config) );
		selectMenu.add( selectContent );
	}
	
	private void addFileMenu(FarMenuBar menubar) {
		menubar.add( new FileMenu(repository, config) );
	}
	
	private void addToolMenu(FarMenuBar menubar) {
		JMenu toolMenu = new JMenu( localizer.localize("menu.tools") );
		toolMenu.setMnemonic( config.getAccessKey("menu.tools") );
		menubar.add( toolMenu );
		
		// assign charset
		JMenu assignCharset = new JMenu( localizer.localize("menu.assign-charset") );
		activateAfterFind( assignCharset );
		for(Charset charset : config.getCharsetList() ) {
			JMenuItem selectCharset = new JMenuItem( charset.name() );
			selectCharset.addActionListener( new AssignCharsetListener( charset, repository.getTableModel() ) );
			assignCharset.add( selectCharset );
		}
		toolMenu.add( assignCharset );		
		
		// test charset
		JMenuItem testCharset = new JMenuItem( localizer.localize("menu.test-charset") );
		activateAfterFind( testCharset );
		testCharset.addActionListener( new TestCharsetListener( repository.getTableModel(), repository.getLocalizer()) );
		toolMenu.add( testCharset );		
		
		// change charset
		JMenu changeCharset = new JMenu( localizer.localize("menu.change-charset") );
		activateAfterFind( changeCharset );
		JMenuItem charsetlistTitle = new JMenuItem( localizer.localize("label.target-charset") );
		changeCharset.add( charsetlistTitle );
		for(Charset charset : config.getCharsetList() ) {
			JMenuItem selectCharset = new JMenuItem( charset.name() );
			ChangeCharsetListener actionListener = new ChangeCharsetListener( charset, repository.getTableModel(), repository.getLocalizer());
			actionListener.addProgressListener( repository.getProgressBarUpdater() );
			selectCharset.addActionListener( actionListener );
			changeCharset.add( selectCharset );
		}
		toolMenu.add( changeCharset );		
		
		// separator
		toolMenu.add( new JSeparator( JSeparator.HORIZONTAL ) );
		
		// change line separator
		JMenu changelineSeparator = new JMenu( localizer.localize("menu.change-line-separator") );
		activateAfterFind( changelineSeparator );
		JMenuItem windos = new JMenuItem( localizer.localize("label.line-separator.CRLF") );
		windos.addActionListener( new ChangeLineSeparatorListener(repository.getTableModel(), repository.getLocalizer(), '\r', '\n') );
		changelineSeparator.add(windos);
		JMenuItem unix = new JMenuItem( localizer.localize("label.line-separator.LF") );
		unix.addActionListener( new ChangeLineSeparatorListener(repository.getTableModel(), repository.getLocalizer(), '\n') );
		changelineSeparator.add(unix);
		JMenuItem caret = new JMenuItem( localizer.localize("label.line-separator.CR") );
		caret.addActionListener( new ChangeLineSeparatorListener(repository.getTableModel(), repository.getLocalizer(), '\r') );
		changelineSeparator.add(caret);
		toolMenu.add( changelineSeparator );	
		
		// remove blank lines
		JMenuItem remLines = new JMenuItem( localizer.localize("menu.remove-empty-lines") );
		activateAfterFind( remLines );
		remLines.addActionListener( new RemoveEmptyLinesListener( repository.getTableModel(), repository.getLocalizer()) );
		toolMenu.add( remLines );		
		
		// truncate lines
		JMenuItem trimLines = new JMenuItem( localizer.localize("menu.trim-lines") );
		activateAfterFind( trimLines );
		trimLines.addActionListener( new TrimLinesListener( repository.getTableModel(), repository.getLocalizer() ) );
		toolMenu.add( trimLines );		
		
		// sort lines
		// concatenate files
		// split files
		// format xml, html, text
		
		// TODO: add saved search & replace operations
	}
	
	private void addHelpMenu(FarMenuBar menubar) {	
		// -- Help Menu
		JMenu infoMenu = new JMenu( localizer.localize("menu.help") );
		infoMenu.setMnemonic( config.getAccessKey("menu.help") );
		menubar.add( infoMenu );
		String manualTitle = localizer.localize("menu.manual");
		JMenu manualMenu = new JMenu( manualTitle );
		JMenuItem helpOverviewMenu = new JMenuItem( localizer.localize("menu.overview") );
		helpOverviewMenu.addActionListener( new HelpListener(repository.getRootWindow(), 
									manualTitle,
									"index.html",
									config.getScreenCenter()) );
		manualMenu.add(helpOverviewMenu);
		JMenuItem helpFindMenu = new JMenuItem( localizer.localize("menu.find") );
		helpFindMenu.addActionListener( new HelpListener(repository.getRootWindow(), 
									manualTitle,
									"manual01-find.html",
									config.getScreenCenter()) );
		manualMenu.add(helpFindMenu);
		JMenuItem helpReplaceMenu = new JMenuItem( localizer.localize("menu.replace") );
		helpReplaceMenu.addActionListener( new HelpListener(repository.getRootWindow(), 
									manualTitle,
									"manual02-replace.html",
									config.getScreenCenter()) );
		manualMenu.add(helpReplaceMenu);
		JMenuItem helpRenameMenu = new JMenuItem( localizer.localize("menu.rename") );
		helpRenameMenu.addActionListener( new HelpListener(repository.getRootWindow(), 
									manualTitle,
									"manual03-rename.html",
									config.getScreenCenter()) );
		manualMenu.add(helpRenameMenu);
		JMenuItem helpFileListMenu = new JMenuItem( localizer.localize("menu.file-list") );
		helpFileListMenu.addActionListener( new HelpListener(repository.getRootWindow(), 
									manualTitle,
									"manual04-files.html",
									config.getScreenCenter()) );
		manualMenu.add(helpFileListMenu);
		JMenu helpMenuMenu = new JMenu( localizer.localize("menu.menu") );
		JMenuItem helpSettingsMenu = new JMenuItem( localizer.localize("menu.settings") );
		helpSettingsMenu.addActionListener( new HelpListener(repository.getRootWindow(), 
									manualTitle,
									"manual11-menu-settings.html",
									config.getScreenCenter()) );
		helpMenuMenu.add(helpSettingsMenu);
		JMenuItem helpSearchMenu = new JMenuItem( localizer.localize("menu.search") );
		helpSearchMenu.addActionListener( new HelpListener(repository.getRootWindow(), 
									manualTitle,
									"manual12-menu-search.html",
									config.getScreenCenter()) );
		helpMenuMenu.add(helpSearchMenu);
		JMenuItem helpSelectMenu = new JMenuItem( localizer.localize("menu.select") );
		helpSelectMenu.addActionListener( new HelpListener(repository.getRootWindow(), 
									manualTitle,
									"manual13-menu-select.html",
									config.getScreenCenter()) );
		helpMenuMenu.add(helpSelectMenu);
		JMenuItem helpFileMenu = new JMenuItem( localizer.localize("menu.file") );
		helpFileMenu.addActionListener( new HelpListener(repository.getRootWindow(), 
									manualTitle,
									"manual14-menu-file.html",
									config.getScreenCenter()) );
		helpMenuMenu.add(helpFileMenu);
		JMenuItem helpToolsMenu = new JMenuItem( localizer.localize("menu.tools") );
		helpToolsMenu.addActionListener( new HelpListener(repository.getRootWindow(), 
									manualTitle,
									"manual15-menu-tools.html",
									config.getScreenCenter()) );
		helpMenuMenu.add(helpToolsMenu);
		manualMenu.add(helpMenuMenu);
		infoMenu.add( manualMenu );
		String regexTitle = localizer.localize("menu.regex");
		JMenuItem regexMenu = new JMenuItem( regexTitle );
		regexMenu.setMnemonic( KeyEvent.VK_X );
		regexMenu.addActionListener( new HelpListener(repository.getRootWindow(), 
									regexTitle,
									"regex.html",
									config.getScreenCenter()) );
		infoMenu.add( regexMenu );
		if(!SwingConfig.isMacOSX()) {
			String aboutTitle = localizer.localize("menu.about");
			JMenuItem aboutMenu = new JMenuItem( aboutTitle );
			aboutMenu.addActionListener( new AboutListener(repository.getRootWindow(), 
										aboutTitle,
										"about.html",
										config.getScreenCenter()) );
			infoMenu.add( aboutMenu );
		}
	}
	
	private void activateAfterFind(JMenuItem menuItem) {
		menuItem.setEnabled( false );
		repository.getOperationCallBackListener().addComponentTerminationEnabled( menuItem, OperationType.FIND );
		repository.getResetDispatcher().addToBeDisabled( menuItem );

	}
	
}

// ----------------------------------------------------------------------
// -- Listener classes - not implemented as inner classes to allow the factory to be disposed
// ----------------------------------------------------------------------

class FarMenuBar extends JMenuBar {
	
	private MacOSXMenuAdapter macOSXMenuAdapter;
	
	public FarMenuBar() {
		if(SwingConfig.isMacOSX()) {
			macOSXMenuAdapter = new MacOSXMenuAdapter();
		}
	}
	
	public MacOSXMenuAdapter getMacOSXMenuAdapter() {
		return macOSXMenuAdapter;
	}
}

/**
 * Open the Settings dialogue.
 */
class SettingsListener implements ActionListener {
	private JFrame jframe;
	private SwingConfig swingConfig;
	private ComponentRepository componentRepository;
	public SettingsListener(JFrame owner, SwingConfig config, ComponentRepository repository) {
		jframe = owner;
		swingConfig = config;
		componentRepository = repository;
	}
	public void actionPerformed(ActionEvent e) {
		// SettingsDialog settings = new SettingsDialog(jframe, swingConfig, componentRepository);
		ConfigurationDialog settings = new ConfigurationDialog(jframe, swingConfig, componentRepository);
		settings.pack();
		settings.setVisible( true );		
	}
}
/**
 * Opens a help window.
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
class HelpListener implements ActionListener {
	private JFrame jframe;
	private Point center;
	private String title;
	private String file;
	public HelpListener(JFrame owner, String windowTitle, String helpFile, Point screenCenter) {
		jframe = owner;
		center = screenCenter;
		title = windowTitle;
		file = helpFile;
	}
	public void actionPerformed(ActionEvent e) {
		HelpView helpView = new HelpView(jframe, title, file, center);
		helpView.pack();
		helpView.setVisible( true );
	}		
}

/**
 * Opens the About box.
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
class AboutListener implements ActionListener {
	private JFrame jframe;
	private Point center;
	private String title;
	private String file;
	public AboutListener(JFrame owner, String windowTitle, String helpFile, Point screenCenter) {
		jframe = owner;
		center = screenCenter;
		title = windowTitle;
		file = helpFile;
	}
	public void actionPerformed(ActionEvent e) {
		HelpView helpView = new HelpView(jframe, title, file, center);
		helpView.setSmall();
		helpView.setResizable(false);
		helpView.pack();
		helpView.setVisible( true );
	}		
}
