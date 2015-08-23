package net.pandoragames.far.ui.swing;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import net.pandoragames.far.ui.ConfigurationException;
import net.pandoragames.far.ui.FARConfig;
import net.pandoragames.far.ui.FARConfigurationFactory;
import net.pandoragames.far.ui.model.FileNamePattern;
import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.OperationType;
import net.pandoragames.far.ui.model.ProgressListener;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.component.ButtonPanel;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.far.ui.swing.component.InfoLine;
import net.pandoragames.far.ui.swing.component.listener.FileImportNotifier;
import net.pandoragames.far.ui.swing.component.listener.OperationCallBackListener;
import net.pandoragames.far.ui.swing.dialog.HelpView;
import net.pandoragames.far.ui.swing.dialog.update.UpdateDialog;
import net.pandoragames.far.ui.swing.menu.MenuFactory;
import net.pandoragames.util.i18n.DefaultLocalizer;
import net.pandoragames.util.i18n.Localizer;
import net.pandoragames.util.j6.DesktopHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Main class of the Swing application.
 * 
 * @author Olivier Wehner at 26.02.2008 <!--
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
public class FindAndReplace extends JFrame {
	
	private SwingConfig config;
	
	private ComponentRepository componentRepository;

	private FARConfigurationFactory configFactory;

	private FileImporter fileImporter;
	
	private Log logger = LogFactory.getLog(this.getClass());

	/**
	 * Default constructor. Defines the application title and initialises the
	 * logging for this component.
	 */
	private FindAndReplace() {
		super(SwingConfig.APPLICATION_TITLE);
		String message = "Starting FAR - Find And Replace "
				+ getClass().getPackage().getSpecificationVersion() + " ("
				+ getClass().getPackage().getImplementationVersion() + ")";
		logger.info(message);
	}

	/**
	 * The main method. Expects no arguments.
	 * 
	 * @param args
	 *            not evaluated
	 */
	public static void main(String[] args) {
		if (FARConfig.getEffectiveJavaVersion() == 0) { // obscure jvm
			LogFactory
					.getLog(FindAndReplace.class)
					.warn(
							"Java version could not be read. This may very well lead to unexpected crashes");
		} else if (FARConfig.getEffectiveJavaVersion() < 5) { // won't work -
																// exit
			LogFactory.getLog(FindAndReplace.class).error(
					"FAR requires java 5 (1.5.x) or higher. Found 1."
							+ FARConfig.getEffectiveJavaVersion());
			System.exit(1);
		} else {
			LogFactory.getLog(FindAndReplace.class).debug(
					"Running on java " + FARConfig.getEffectiveJavaVersion());
		}
		JFrame.setDefaultLookAndFeelDecorated(SwingConfig.isMacOSX());
		UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
		FindAndReplace far = new FindAndReplace();
		far.configure();
		far.init();
		far.pack();
		far.setVisible(true);
		// First time initialisation - speeds up the loading of the help views
		HelpView helpView = new HelpView(far, "About", "about.html", new Point(
				100, 100));
		helpView.pack();
		helpView.setVisible(false);

		if (args.length > 0 && SwingConfig.getEffectiveJavaVersion() > 5) {
			List<File> fileList = new ArrayList<File>();
			for(String arg : args) {
				File file = new File( arg );
				if( file.exists() ) fileList.add( file );
			}
			if( fileList.size() > 0 ) { 	
				far.fileImporter.importFiles(fileList);
			}
		}
		
		if( far.config.versionHasChanged() ) {
			UpdateDialog updateWizzard = new UpdateDialog(far, far.config, far.componentRepository);
			if(updateWizzard.isUserInteractionRequired()) {
				updateWizzard.pack();
				updateWizzard.setVisible( true );
			} else {
				updateWizzard.dispose();
			}
		}
	}

	/**
	 * User preferences.
	 */
	private void configure() {
		logger.debug("Configuration...");
		try {
			config = new SwingConfig();
			configFactory = new FARConfigurationFactory();
			configFactory.loadConfig(config);
			// localizer
			File localizerProperties = new File(this.getClass()
					.getClassLoader().getResource("fartext.properties").toURI());
			Localizer loco = new DefaultLocalizer(localizerProperties,
					Locale.ENGLISH);
			JComponent.setDefaultLocale(loco.getLocale());
			config.setLocalizer(loco);
		} catch (Exception x) {
			logger.error(x.getClass().getName() + ": " + x.getMessage(), x);
			throw new ConfigurationException(x.getClass().getName() + ": "
					+ x.getMessage());
		}

		// standard component hight
		JButton testButton = new JButton("TEST");
		config.setStandardComponentHight(testButton.getPreferredSize().height);

		// screen center
		Rectangle screen = getGraphicsConfiguration().getBounds();
		config.setScreenCenter(new Point((screen.width - screen.x) / 2,
				(screen.height - screen.y) / 2));

	}

	/**
	 * Pannels and other swing settings
	 */
	private void init() {
		logger.debug("Setting up FAR Swing GUI");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ExitListener closeFARlistener = new ExitListener();
		addWindowListener(closeFARlistener);

		setIconImage(new ImageIcon(this.getClass().getClassLoader()
				.getResource("icons/far.gif")).getImage());

		setLocation(config.getScreenCenter().x / 3,
				config.getScreenCenter().y / 3);

		this.setLayout(new BorderLayout());
		InfoLine infoLine = new InfoLine(config);
		componentRepository = new ComponentRepository(
				config, infoLine, closeFARlistener, this);
		componentRepository.getOperationCallBackListener()
				.addComponentStartReseted(infoLine, OperationType.REPLACE);
		componentRepository.getResetDispatcher().addResetable(infoLine);
		componentRepository.getSearchBaseListener().addResetable(infoLine);

		// menu
		MenuFactory menuFactory = new MenuFactory(config, componentRepository);
		this.setJMenuBar(menuFactory.createMenu());
		closeFARlistener.registerCloseApplicationKeyListener( this.getJMenuBar() );

		this.add(infoLine, BorderLayout.NORTH);

		//
		// sub components
		//

		OperationTabPane tabbedPane = componentRepository.getTabPane();
		closeFARlistener.registerCloseApplicationKeyListener( tabbedPane );

		// find tab
		FindFilePanel findFilePanel = new FindFilePanel(config,
				componentRepository);
		ButtonPanel findFileBasePanel = new ButtonPanel(OperationType.FIND,
				config, componentRepository);
		findFileBasePanel.setMainPanel(findFilePanel);
		tabbedPane.addTab(config.getLocalizer().localize("button.find"),
				findFileBasePanel);
		tabbedPane.setMnemonicAt(0, config.getAccessKey("menu.find"));
		// replace tab
		ReplaceContentPanel replacePanel = new ReplaceContentPanel(config,
				componentRepository);
		ButtonPanel replaceBasePanel = new ButtonPanel(OperationType.REPLACE,
				config, componentRepository);
		replaceBasePanel.setMainPanel(replacePanel);
		tabbedPane.addTab(config.getLocalizer().localize("button.replace"),
				replaceBasePanel);
		tabbedPane.setMnemonicAt(1, config.getAccessKey("menu.replace"));
		// rename tab
		RenameFilesPanel renamePanel = new RenameFilesPanel(config,
				componentRepository);
		ButtonPanel renameBasePanel = new ButtonPanel(OperationType.RENAME,
				config, componentRepository);
		renameBasePanel.setMainPanel(renamePanel);
		tabbedPane.addTab(config.getLocalizer().localize("button.rename"),
				renameBasePanel);
		tabbedPane.setMnemonicAt(2, config.getAccessKey("menu.rename"));
		// listener
		componentRepository.getOperationCallBackListener()
				.addComponentTerminationEnabled(tabbedPane, OperationType.FIND);
		componentRepository.getResetDispatcher().addResetable(tabbedPane);
		componentRepository.getSearchBaseListener().addResetable(tabbedPane);
		tabbedPane.reset();
		this.add(tabbedPane, BorderLayout.WEST);

		FileListPanel fileListPanel = new FileListPanel(config,
				componentRepository);
		this.add(fileListPanel, BorderLayout.CENTER);
		closeFARlistener.registerCloseApplicationKeyListener( fileListPanel );

		// drag and drop
		if (SwingConfig.getEffectiveJavaVersion() > 5) {
			
			fileImporter = new FileImporter( componentRepository );
			
			DesktopHelper.installTransferHandler(this, fileImporter);
		} else {
			infoLine.info( config.getLocalizer().localize("message.please-update-java") );
		}

	}
	

	class ExitListener extends AbstractAction implements WindowListener {
		
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
		
		public void windowClosed(WindowEvent wen) {
			logger.debug("Exit");
			backupBeforeExit();
		}

		public void windowClosing(WindowEvent wen) {
			logger.debug("Closing...");
			backupBeforeExit();
		}

		public void windowActivated(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}

		public void registerCloseApplicationKeyListener(JComponent component) {
			component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK), "exitAction");
			component.getActionMap().put("exitAction", this);		
		}

		private void backupBeforeExit() {
			if (!configFactory.saveConfig(config))
				logger.warn("Preferences not properly saved, exit though.");
			logger.info(" - End -");
		}
	}

	class FileImporter implements FileImportNotifier, Runnable {
		private Localizer localizer;
		private ProgressListener findProgressListener;
		private OperationCallBackListener callbackLister;
		private MessageBox errorSink;
		private FindForm findForm;
		private FileSetTableModel tableModel; 
		
		// should be externalized
		private int fileCount;
		private List<File> fileList;

		public FileImporter(ComponentRepository repository) {
			localizer = config.getLocalizer();
			findProgressListener = repository.getOperationCallBackListener();
			errorSink = repository.getMessageBox();
			findForm = repository.getFindForm();
			tableModel = repository.getTableModel();
			callbackLister = repository.getOperationCallBackListener();
		}

		public void importFiles(List<File> fileList) {
			this.fileList = fileList;
			Thread thread = new Thread( this );
			thread.start();
		}
		
		public void run() {
			fileCount = 0;
	    	findProgressListener.operationStarted( OperationType.FIND );
	    	callbackLister.operationStarted(OperationType.FIND);
			List<File> expanded = new ArrayList<File>();
			for(File file : fileList) {
				if(file.isFile()) {
					// possibly relative path through command line
					expanded.add( file.getAbsoluteFile() );
					findProgressListener.operationProgressed(fileCount++, 0, OperationType.FIND);
				} else if( file.isDirectory() ) {
					expanded.addAll( resolve( file ) );
				}
			}
			if( expanded.size() == 0 ) return;
			// build the target file list
			logger.info("Importing " + expanded.size() + " files from external source");
			List<TargetFile> importList = new ArrayList<TargetFile>();
			File basedir = null;
			for(int i = 0; i < expanded.size(); i++) {
				File nextFile = expanded.get(i);
				if(basedir == null) {
					basedir = nextFile.getParentFile();
				} else {
					String path = nextFile.getPath(); 
					while( basedir != null && ! path.startsWith( basedir.getPath() )) {
						basedir = basedir.getParentFile();
					}
					if( basedir == null ) basedir = File.listRoots()[0];
				}
				importList.add( new TargetFile( nextFile ) );
				findProgressListener.operationProgressed(i, expanded.size(), OperationType.FIND);
			}
			// import
			FindForm inferedForm = config.getDefaultFindForm();
			inferedForm.setBaseDirectory( basedir );
			String fileNamePattern = "";
			if( fileList.size() == 1 ) {
				if( fileList.get(0).isFile() ) {
					fileNamePattern = fileList.get(0).getName(); 
				} else {
					fileNamePattern = "*";
				}
			}
			inferedForm.setFileNamePattern( new FileNamePattern( fileNamePattern ) );
			findForm.update( inferedForm );
			findProgressListener.operationTerminated(OperationType.FIND);
			callbackLister.operationTerminated(OperationType.FIND);
			tableModel.setFileList( importList , basedir );
			// write to message sink AFTER updating the model to avoid clear() erasing the message
			errorSink.info(localizer.localize("message.drop-import-ok", new Object[]{ Integer.valueOf( importList.size())} ));
			logger.debug(importList.size() + " files imported.");

		}

		private List<File> resolve(File directory) {
			List<File> result = new ArrayList<File>();
			File[] raw = directory.listFiles();
			for (File file : raw) {
				if (file.isFile()) {
					// possibly relative path through command line
					result.add(file.getAbsoluteFile());
					findProgressListener.operationProgressed(fileCount++, 0,
							OperationType.FIND);
				} else if (file.isDirectory()) {
					result.addAll(resolve(file));
				}
			}
			return result;
		}
	}
}
