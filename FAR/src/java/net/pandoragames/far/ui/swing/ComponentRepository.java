package net.pandoragames.far.ui.swing;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTable;

import net.pandoragames.far.ui.UIBean;
import net.pandoragames.far.ui.UIFace;
import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.RenameForm;
import net.pandoragames.far.ui.model.ReplaceForm;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.far.ui.swing.component.listener.BackupFlagListener;
import net.pandoragames.far.ui.swing.component.listener.CommandFind;
import net.pandoragames.far.ui.swing.component.listener.CommandRename;
import net.pandoragames.far.ui.swing.component.listener.CommandReplace;
import net.pandoragames.far.ui.swing.component.listener.OperationCallBackListener;
import net.pandoragames.far.ui.swing.component.listener.ProgressBarUpdater;
import net.pandoragames.far.ui.swing.component.listener.ResetDispatcher;
import net.pandoragames.far.ui.swing.component.listener.SearchBaseHasChangedListener;
import net.pandoragames.far.ui.swing.component.listener.UndoListener;
import net.pandoragames.util.i18n.Localizer;

/**
 * Creates and deploys some Swing components that are supposed to be availabel 
 * accross different places in the application.
 * @author Olivier Wehner at 03.03.2008
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
public class ComponentRepository
{
	private JFrame mama;
	private MessageBox messageBox;
	private UIFace uiface;
	private CommandFind findCommand;
	private CommandReplace replaceCommand;
	private CommandRename renameCommand;
	private ResetDispatcher resetDispatcher;
	private ProgressBarUpdater progressBarUpdater;
	private OperationCallBackListener operationCallBackListener;
	private SearchBaseHasChangedListener searchBaseListener;
	private BackupFlagListener backupFlagListener;
	private Localizer localiser;
	private ActionListener exitListener;
	private FindForm findForm;
	private ReplaceForm replaceForm;
	private RenameForm renameForm;
	private UndoListener undoListener;
	private FileSetTableModel tableModel;
	private JTable fileSetTable;
	private OperationTabPane tabbedPane;
	
	public ComponentRepository(SwingConfig configuration, 
								MessageBox messageSink, 
								FindAndReplace.ExitListener applicationExitLisener, 
								JFrame rootWindow) {
		mama = rootWindow;
		localiser = configuration.getLocalizer();
		messageBox = messageSink;
		exitListener = applicationExitLisener;
		uiface = new UIBean( configuration, messageSink );
		findCommand = new CommandFind( uiface );
		replaceCommand = new CommandReplace( configuration, uiface );
		renameCommand = new CommandRename( uiface, localiser, mama );
		resetDispatcher = new ResetDispatcher();
		progressBarUpdater = new ProgressBarUpdater();
		operationCallBackListener = new OperationCallBackListener();
		uiface.addProgressListener( progressBarUpdater );
		uiface.addProgressListener( operationCallBackListener );
		searchBaseListener = new SearchBaseHasChangedListener();
		backupFlagListener = new BackupFlagListener();
		findForm = configuration.getDefaultFindForm();
		replaceForm = configuration.getDefaultReplaceForm();
		renameForm = configuration.getDefaultRenameForm();
		findForm.addBaseDirectoryListener( replaceForm );
		undoListener = new UndoListener( uiface );
		tableModel = new FileSetTableModel( new ArrayList<TargetFile>(), localiser );
		fileSetTable = new JTable( tableModel );
		findCommand.setFileSetTable( tableModel );
		findCommand.setFindForm( findForm );
		replaceCommand.setFileSetTable( tableModel );
		renameCommand.setFileSetTable( tableModel );
		renameCommand.setFindForm( findForm );
		tabbedPane = new OperationTabPane();
		// initialise the reset dispatcher
		resetDispatcher.addResetable(uiface);
		resetDispatcher.addResetable(findForm);
		resetDispatcher.addResetable(replaceForm);
		resetDispatcher.addResetable(renameForm);
	}

	/**
	 * Action listener that will trigger the FIND command.
	 * @return ActionListener
	 */
	public CommandFind getFindCommand()
	{
		return findCommand;
	}

	/**
	 * Action listener that will trigger the REPLACE command.
	 * @return ActionListener
	 */
	public CommandReplace getReplaceCommand()
	{
		return replaceCommand;
	}

	/**
	 * Action listener that will trigger the RENAME command.
	 * @return ActionListener
	 */
	public CommandRename getRenameCommand() {
		return renameCommand;
	}
	
	/**
	 * Target component for messages.
	 * @return Message sink
	 */
	public MessageBox getMessageBox()
	{
		return messageBox;
	}

	/**
	 * Central interface to business logic.
	 * @return business logic interface
	 */
	public UIFace getUiface()
	{
		return uiface;
	}
	
	/**
	 * Action listener that will trigger a reset of the search form.
	 * @return ActionListener
	 */
	public ResetDispatcher getResetDispatcher() 
	{
		return resetDispatcher;
	}
	
	/**
	 * Returns the ProgressBarUpdater.
	 * @return ProgressBarUpdater
	 */
	public ProgressBarUpdater getProgressBarUpdater() {
		return progressBarUpdater;
	}
	
	/**
	 * Returns the OperationCallBackListener.
	 * @return OperationCallBackListener
	 */
	public OperationCallBackListener getOperationCallBackListener() {
		return operationCallBackListener;
	}

	/**
	 * Returns the SearchBaseHasChangedListener.
	 * @return SearchBaseHasChangedListener
	 */
	public SearchBaseHasChangedListener getSearchBaseListener()
	{
		return searchBaseListener;
	}
	
	/**
	 * Returns the UndoListener.
	 * @return UndoListener
	 */
	public UndoListener getUndoListener() {
		return undoListener;
	}
	
	/**
	 * Application main window. Needed for close operation.
	 * @return application root window
	 */
	public JFrame getRootWindow() {
		return mama;
	}
	
	/**
	 * Localizer for translation of labels and messages.
	 * @return "Localizer"
	 */
	public Localizer getLocalizer() {
		return localiser;
	}
	
	/**
	 * Closes the application.
	 * @return hook for exit action
	 */
	public ActionListener getExitListener() {
		return exitListener;
	}

	/**
	 * Returns the FindForm instance used by the application.
	 * @return shared FindForm instance
	 */
	public FindForm getFindForm() {
		return findForm;
	}

	/**
	 * Returns the ReplaceForm instance used by the application.
	 * @return shared ReplaceForm instance
	 */
	public ReplaceForm getReplaceForm() {
		return replaceForm;
	}

	/**
	 * Returns the RenameForm instance used by the application.
	 * @return shared RenameForm instance
	 */
	public RenameForm getRenameForm() {
		return renameForm;
	}
	
	/**
	 * Returns a listener for the doBackup flag, interested components
	 * may register with.
	 * @return backup flag listener
	 */
	public BackupFlagListener getBackupFlagListener() {
		return backupFlagListener;
	}

	/**
	 * Returns the table model for the result table.
	 * @return table model for result table
	 */
	public FileSetTableModel getTableModel() {
		return tableModel;
	}
	
	/**
	 * Returns the JTable that holds the file set table model.
	 * @return JTable for result table
	 */
	public JTable getFileSetTable() {
		return fileSetTable;
	}
	
	/**
	 * Returns the main TabPane withe Find and Repace tabs.
	 * @return main TabPane
	 */
	public OperationTabPane getTabPane() {
		return tabbedPane;
	}
}
