package net.pandoragames.far.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import net.pandoragames.far.ui.FARConfig;
import net.pandoragames.far.ui.model.AbstractFileRepository;
import net.pandoragames.far.ui.model.FormUpdateEvent;
import net.pandoragames.far.ui.model.FormUpdateListener;
import net.pandoragames.far.ui.model.OperationType;
import net.pandoragames.far.ui.model.ReplaceForm;
import net.pandoragames.far.ui.swing.component.ContentSearchPanel;
import net.pandoragames.far.ui.swing.component.TwoComponentsPanel;
import net.pandoragames.far.ui.swing.component.UndoHistory;
import net.pandoragames.far.ui.swing.component.UndoHistoryPopupMenu;
import net.pandoragames.far.ui.swing.component.listener.BrowseButtonListener;
import net.pandoragames.util.i18n.Localizer;

/**
 * Defines replacement operations on a given file set.
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
public class ReplaceContentPanel extends JPanel implements FormUpdateListener {

	private ReplaceForm dataModel;
	private Localizer localizer;
	private ContentSearchPanel contentSearchPanel;
	private JTextArea replacementPattern;
	
	public ReplaceContentPanel(SwingConfig config, ComponentRepository componentRepository) {
		localizer = config.getLocalizer();
		dataModel = componentRepository.getReplaceForm();
		dataModel.addFormUpdateListener( this );
		init( config, componentRepository );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void formUpdated( FormUpdateEvent event ) {
		contentSearchPanel.loadForm(dataModel, true);
		replacementPattern.setText( dataModel.getReplacementString() );
	}
	
	private void init(SwingConfig config, ComponentRepository componentRepository) {
		
		this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

		componentRepository.getReplaceCommand().setReplaceForm( dataModel );
		
		this.setBorder( BorderFactory.createEmptyBorder( 0, 
														SwingConfig.PADDING, 
														SwingConfig.PADDING, 
														SwingConfig.PADDING ) );
		
		initContentSearchPanel( config, componentRepository );
		initReplacementPanel( config, componentRepository );
		initBackupPanel( config, componentRepository );
	}
	
	private void initContentSearchPanel(SwingConfig config, ComponentRepository componentRepository) {
		
		this.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
		
		String title = localizer.localize("label.find-pattern");
		contentSearchPanel = new ContentSearchPanel( title, dataModel, config, componentRepository);
		contentSearchPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		this.add( contentSearchPanel );
		componentRepository.getUiface().addProgressListener( contentSearchPanel.getSynchronisationListener( componentRepository.getFindForm() ) );
		
		this.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
		this.add( Box.createVerticalGlue() );

	}
		
	/**
	 * Initialises the REPLACEMENT panel.
	 * @param config configuration properties
	 * @param componentRepository repository for shared components
	 */
	private void initReplacementPanel(SwingConfig config, ComponentRepository componentRepository) {
		
		JLabel labelReplacementPattern = new JLabel( localizer.localize("label.replacement-pattern") );
		labelReplacementPattern.setAlignmentX( Component.LEFT_ALIGNMENT );
		this.add( labelReplacementPattern );
		
		replacementPattern = new JTextArea();
		replacementPattern.setEnabled( false );
		replacementPattern.setBackground( SwingConfig.GRAY_EXTRA_LIGHT );
		replacementPattern.setRows( 5 );
		replacementPattern.setLineWrap( true );
		replacementPattern.addFocusListener( new FocusListener() {
			public void focusGained(FocusEvent event) {}
			public void focusLost(FocusEvent event) {
				JTextComponent textcomponent = (JTextComponent) event.getSource();
				dataModel.setReplacementString( textcomponent.getText() );
			}
		});
		config.setFocusTraversalKeys( replacementPattern );
		replacementPattern.addPropertyChangeListener("enabled", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if( "enabled".equalsIgnoreCase( event.getPropertyName() )) {
					Boolean enabled = (Boolean) event.getNewValue();
					Component textArea = (Component) event.getSource();
					if( enabled.booleanValue() ) {
						textArea.setBackground( Color.WHITE );			
						textArea.setForeground( Color.BLACK );
					} else {
						textArea.setBackground( SwingConfig.GRAY_EXTRA_LIGHT );
						textArea.setForeground( Color.GRAY );
					}
				}
			}
		});
		componentRepository.getOperationCallBackListener().addComponentTerminationEnabled( replacementPattern, OperationType.FIND );
		componentRepository.getResetDispatcher().addToBeDisabled( replacementPattern );
		componentRepository.getResetDispatcher().addToBeCleared( replacementPattern );
		componentRepository.getSearchBaseListener().addToBeDisabled( replacementPattern );
		JScrollPane scrollPane = new JScrollPane( replacementPattern );
		scrollPane.setAlignmentX( Component.LEFT_ALIGNMENT );
		this.add( scrollPane );
		
		UndoHistory undoManager = new UndoHistory();
		undoManager.registerUndoHistory( replacementPattern );
		undoManager.registerSnapshotHistory( replacementPattern );
		componentRepository.getReplaceCommand().addResetable( undoManager );
		UndoHistoryPopupMenu.createPopUpMenu( config, componentRepository, replacementPattern );
		
		this.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
		this.add( Box.createVerticalGlue() );
		
	}
	
	/**
	 * Initialises the BACKUP panel.
	 * @param config configuration properties
	 * @param componentRepository repository for shared components
	 */
	private void initBackupPanel(SwingConfig config, ComponentRepository componentRepository) {

		JTextField backupDirPathTextField = new JTextField();
		backupDirPathTextField.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, 
														config.getStandardComponentHight()) );
		backupDirPathTextField.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, 
														config.getStandardComponentHight()) );
		backupDirPathTextField.setText( dataModel.getBackupDirectory().getPath() );
		backupDirPathTextField.setToolTipText( dataModel.getBackupDirectory().getPath() );
		backupDirPathTextField.setEditable( false );

		JButton openBaseDirFileChooserButton = new JButton( localizer.localize("button.browse") );
		class BackUpDirectoryRepository extends AbstractFileRepository {
			public BackUpDirectoryRepository(FARConfig cfg, ComponentRepository repository ) {
				super(cfg, repository.getFindForm(), repository.getReplaceForm(), repository.getMessageBox());
			}
			public File getFile() {
				return farconfig.getBackupDirectory();
			}
			public boolean setFile(File file) {
				if( isSubdirectory(file, findForm.getBaseDirectory()) ) {
					messageBox.error( farconfig.getLocalizer().localize("message.nested-backup-parent") );
					return false;
				} else if ( isSubdirectory( findForm.getBaseDirectory(), file ) ) {
					messageBox.error( farconfig.getLocalizer().localize("message.nested-backup-child") );
					return false;					
				} else if ( file.canWrite() ) {
					farconfig.setBackupDirectory(file);
					replaceForm.setBackupDirectory(file);
					return true;
				} else {
					String message = farconfig.getLocalizer().localize("message.directory-not-writable", new Object[]{file.getPath()});
					messageBox.error( message );
					return false;
				}
			}
		}
		
		JLabel backupDirectory = new JLabel( localizer.localize("label.choose-backup-directory") );
		backupDirectory.setAlignmentX( Component.LEFT_ALIGNMENT );
		this.add( backupDirectory );
		BrowseButtonListener backupDirButtonListener = new BrowseButtonListener(backupDirPathTextField,
															new BackUpDirectoryRepository( config, componentRepository ),
															localizer.localize( "label.choose-backup-directory" ) );
		openBaseDirFileChooserButton.addActionListener( backupDirButtonListener );
		openBaseDirFileChooserButton.setEnabled(  dataModel.isDoBackup()  );
		componentRepository.getBackupFlagListener().addToBeToggled( openBaseDirFileChooserButton );
		TwoComponentsPanel lineBaseDir = new TwoComponentsPanel( backupDirPathTextField, openBaseDirFileChooserButton);
		lineBaseDir.setAlignmentX( Component.LEFT_ALIGNMENT );
		this.add( lineBaseDir );
		
		this.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
		this.add( Box.createVerticalGlue() );
	}
	
}
