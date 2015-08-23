package net.pandoragames.far.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import net.pandoragames.far.PatternException;
import net.pandoragames.far.ReplacementString;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.RenameForm;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.far.ui.swing.component.UndoHistory;
import net.pandoragames.util.i18n.Localizer;

import org.apache.commons.logging.LogFactory;

/**
 * Form for rename operation.
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
public class RenameFilesPanel extends JPanel {

	private Localizer localizer;
	private RenameForm dataModel;
	private FileSetTableModel tableModel;
	private MessageBox messageBox;
	private Timer timer = new Timer( true );
	private FileNameUpdater currentUpdater;
	private Action renameCommand;
	
	private boolean patternHasError = false;
	private boolean replaceHasError = false;
	private JTextField filenamePattern;
	private JTextField replacePattern;

	/**
	 * Constructor takes configuration object and component repository.
	 * @param config application configuration
	 * @param componentRepository shared components
	 */
	public RenameFilesPanel(SwingConfig config, ComponentRepository componentRepository) {
		localizer = config.getLocalizer();
		dataModel = componentRepository.getRenameForm();
		tableModel = componentRepository.getTableModel();
		messageBox = componentRepository.getMessageBox();
		renameCommand = componentRepository.getRenameCommand();
		init( config, componentRepository );
	}
	
	private void init(SwingConfig config, ComponentRepository componentRepository) {
		
		this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

		this.setBorder( BorderFactory.createEmptyBorder( 0, 
														SwingConfig.PADDING, 
														SwingConfig.PADDING, 
														SwingConfig.PADDING ) );
		
		this.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
		JLabel patternLabel = new JLabel(localizer.localize("label.find-pattern"));
		this.add( patternLabel );
		filenamePattern = new JTextField();
		filenamePattern.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_LARGE, 
				config.getStandardComponentHight()) );
		filenamePattern.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, 
				config.getStandardComponentHight()) );
		filenamePattern.setAlignmentX( Component.LEFT_ALIGNMENT );
		UndoHistory findUndoManager = new UndoHistory();
		findUndoManager.registerUndoHistory( filenamePattern );
		findUndoManager.registerSnapshotHistory( filenamePattern );
		componentRepository.getReplaceCommand().addResetable( findUndoManager );
		filenamePattern.getDocument().addDocumentListener( new DocumentChangeListener() {
			public void documentUpdated(DocumentEvent e, String text) {
				dataModel.setPatternString(  text );
				updateFileTable();
			}
		});
		componentRepository.getResetDispatcher().addToBeCleared( filenamePattern );
		this.add( filenamePattern );
		JCheckBox caseBox = new JCheckBox(localizer.localize("label.ignore-case"));
		caseBox.setSelected( true );
		caseBox.addItemListener( new ItemListener() {	
			public void itemStateChanged(ItemEvent event) {
					dataModel.setIgnoreCase( (ItemEvent.SELECTED == event.getStateChange()) );
					updateFileTable();
			}
		});
		this.add( caseBox );
		JCheckBox regexBox = new JCheckBox(localizer.localize("label.regular-expression"));
		regexBox.addItemListener( new ItemListener() {	
			public void itemStateChanged(ItemEvent event) {
					dataModel.setRegexPattern( (ItemEvent.SELECTED == event.getStateChange()) );
					updateFileTable();
			}
		});
		this.add( regexBox );
		JPanel extensionPanel = new JPanel();
		extensionPanel.setBorder( BorderFactory.createTitledBorder(localizer.localize("label.modify-extension")));
		extensionPanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
		extensionPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		extensionPanel.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_LARGE, 60) );
		extensionPanel.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, 100) );
		ButtonGroup extensionGroup = new ButtonGroup();
		JRadioButton protectButton = new JRadioButton(localizer.localize("label.protect-extension") );
		protectButton.setSelected( true );
		protectButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				dataModel.setProtectExtension( true );
				updateFileTable();
			}
		});
		extensionGroup.add( protectButton );
		extensionPanel.add( protectButton );
		JRadioButton includeButton = new JRadioButton(localizer.localize("label.include-extension") );
		includeButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				dataModel.setExtensionOnly( false );
				dataModel.setProtectExtension( false );
				updateFileTable();
			}
		});
		extensionGroup.add( includeButton );
		extensionPanel.add( includeButton );
		JRadioButton onlyButton = new JRadioButton(localizer.localize("label.only-extension") );
		onlyButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				dataModel.setExtensionOnly( true );
				updateFileTable();
			}
		});
		extensionGroup.add( onlyButton );
		extensionPanel.add( onlyButton );
		this.add( extensionPanel );

		this.add( Box.createVerticalGlue() );
		this.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
		
		// replace
		JLabel replaceLabel = new JLabel(localizer.localize("label.replacement-pattern"));
		this.add( replaceLabel );
		replacePattern = new JTextField();
		replacePattern.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_LARGE, 
				config.getStandardComponentHight()) );
		replacePattern.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, 
				config.getStandardComponentHight()) );
		replacePattern.setAlignmentX( Component.LEFT_ALIGNMENT );
		UndoHistory undoManager = new UndoHistory();
		undoManager.registerUndoHistory( replacePattern );
		undoManager.registerSnapshotHistory( replacePattern );
		componentRepository.getReplaceCommand().addResetable( undoManager );
		replacePattern.getDocument().addDocumentListener( new DocumentChangeListener() {
			public void documentUpdated(DocumentEvent e, String text) {
				dataModel.setReplacementString( text );
				updateFileTable();
			}
		});
		componentRepository.getResetDispatcher().addToBeCleared( replacePattern );
		this.add( replacePattern );
		
		// treat case
		JPanel modifyCasePanel = new JPanel();
		modifyCasePanel.setBorder( BorderFactory.createTitledBorder(localizer.localize("label.modify-case")));
		modifyCasePanel.setLayout( new BoxLayout( modifyCasePanel, BoxLayout.Y_AXIS ) );
		modifyCasePanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		modifyCasePanel.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_LARGE, 100) );
		modifyCasePanel.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, 200) );
		ButtonGroup modifyCaseGroup = new ButtonGroup();
		ActionListener radioButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				dataModel.setTreatCase( RenameForm.CASEHANDLING.valueOf(cmd) );
				updateFileTable();
			}
		};
		JRadioButton lowerButton = new JRadioButton(localizer.localize("label.to-lower-case") );
		lowerButton.setActionCommand( RenameForm.CASEHANDLING.LOWER.name() );
		lowerButton.addActionListener( radioButtonListener );
		modifyCaseGroup.add( lowerButton );
		modifyCasePanel.add( lowerButton );
		JRadioButton upperButton = new JRadioButton(localizer.localize("label.to-upper-case") );
		upperButton.setActionCommand( RenameForm.CASEHANDLING.UPPER.name() );
		upperButton.addActionListener( radioButtonListener );
		modifyCaseGroup.add( upperButton );
		modifyCasePanel.add( upperButton );
		JRadioButton keepButton = new JRadioButton(localizer.localize("label.preserve-case") );
		keepButton.setActionCommand( RenameForm.CASEHANDLING.PRESERVE.name() );
		keepButton.setSelected( true );
		keepButton.addActionListener( radioButtonListener );
		modifyCaseGroup.add( keepButton );
		modifyCasePanel.add( keepButton );
		this.add( modifyCasePanel );
		
		// prevent case conflict
		JCheckBox caseConflictBox = new JCheckBox(localizer.localize("label.prevent-case-conflict"));
		caseConflictBox.setAlignmentX( Component.LEFT_ALIGNMENT );
		caseConflictBox.setSelected( true );
		caseConflictBox.setEnabled( ! SwingConfig.isWindows() ); // disabled on windows
		caseConflictBox.addItemListener( new ItemListener() {	
			public void itemStateChanged(ItemEvent event) {
					dataModel.setPreventCaseConflict( (ItemEvent.SELECTED == event.getStateChange()) );
					updateFileTable();
			}
		});
		this.add( caseConflictBox );
		
		this.add( Box.createVerticalGlue() );

	}
	
	private void updateFileTable() {
		messageBox.clear();
		renameCommand.setEnabled( false );
		Pattern pattern = null;
		if( dataModel.getPatternString().trim().length() > 0 ) {
			ReplacementString replacementPattern = dataModel.getReplacementPattern();
			if( replacementPattern.getOriginalString().endsWith( String.valueOf( replacementPattern.getGroupReferenceIndicator() ) )) {
				replacePattern.setForeground( Color.RED );
				messageBox.error( localizer.localize("message.trailing-group-reference"));	
				replaceHasError = true;
			} else if( replaceHasError ) {
				replaceHasError = false;
				replacePattern.setForeground( Color.BLACK );
			}
			try {
				pattern = dataModel.getPatternAsRegex();	
				if( patternHasError ) {
					patternHasError = false;
					filenamePattern.setForeground( Color.BLACK );
				}
			} catch(PatternException px) {
				patternHasError = true;
				filenamePattern.setForeground( Color.RED );
				messageBox.error( localizer.localize("message.syntax-error", 
						new Object[]{px.getMessage()} ));		
			}	
			if( !replaceHasError && !patternHasError ) {
				if( currentUpdater != null ) currentUpdater.cancel();
				currentUpdater = new FileNameUpdater( pattern );
				if( tableModel.getRowCount() < 100 ) {
					currentUpdater.run();					
				} else {
					timer.schedule( currentUpdater, 500 );
				}				
			}
		} else {
			tableModel.clearInfo();
		}		
	}
	
	/**
	 * Abstract listener class that preprocesses all notifications
	 * to forward them to a single, convinient, abstract method.
	 * Provides the changed text for implementing classses.
	 */
	abstract class DocumentChangeListener implements DocumentListener {
		public void changedUpdate(DocumentEvent e) {
			documentUpdated( e );
		}
		public void insertUpdate(DocumentEvent e) {
			documentUpdated( e );
		}
		public void removeUpdate(DocumentEvent e) {
			documentUpdated( e );
		}
		private void documentUpdated(DocumentEvent e) {
			try {
				String text = e.getDocument().getText(0, e.getDocument().getLength());
				documentUpdated( e, text );
			} catch(BadLocationException blox) {
				// could this ever happen ?
				documentUpdated( e, blox.getMessage() );
			}
		}
		public abstract void documentUpdated(DocumentEvent e, String text);
	}
	
	
	class FileNameUpdater extends TimerTask {
		private Pattern pattern;
		public FileNameUpdater(Pattern regex) {
			pattern = regex;
		}
		public void run() {
			Set<String> collisions = new HashSet<String>();
			int collisionCount = 0;
			tableModel.clearInfo();
			for(TargetFile file : tableModel.listRows()) {
				// if( ! file.isSelected() ) continue;
				String fileName = file.getName();
				String protectedName = "";
				int extensionMode = 0; // 0 = include, 1 = protect, 2 = only
				if( dataModel.isProtectExtension() && file.hasExtension() ) {
					extensionMode = 1;
					protectedName = file.getFileExtension();
					fileName = fileName.substring(0, fileName.length() - protectedName.length() );
				} else if( dataModel.isExtensionOnly() ) {
					extensionMode = 2;
					if( file.hasExtension() ) {
						protectedName = fileName.substring( 0, fileName.length() - file.getFileExtension().length() + 1 );
						fileName = file.getFileExtension().substring( 1 );						
					} else {
						protectedName = fileName;
						fileName = ""; // no extension
					}
				}
				Matcher matcher = pattern.matcher( fileName );
				String newName = null;
				try{
					newName = matcher.replaceAll( dataModel.getReplacementPattern().getCanonicalString() ).trim();					
				}catch(IndexOutOfBoundsException iox) {
					LogFactory.getLog( this.getClass() ).warn("IndexOutOfBoundsException applying '" + pattern.pattern() 
														+ "' on '" + fileName + "' with replacement '" 
														+ dataModel.getReplacementPattern().getCanonicalString() + "'");
					newName = fileName;
					messageBox.error(localizer.localize( "message.too-many-group-references" ));
				}
				switch( dataModel.getTreatCase() ) {
				case LOWER : newName = newName.toLowerCase(); break;
				case UPPER : newName = newName.toUpperCase(); break;
				}
				switch( extensionMode ) {
				case 1 : newName = newName + protectedName; break;
				case 2 : newName = protectedName + newName; break;
				}
				file.setNewName( newName );
				String collisionTest = dataModel.isPreventCaseConflict() ? newName.toUpperCase() : newName;
				collisionTest = file.getPath() + collisionTest;
				if( collisions.contains( collisionTest )) {
					collisionCount++;
					file.error( newName );
					for(TargetFile firstOccur : tableModel.listRows()) {
						if( firstOccur.getPath().equals( file.getPath() ) && 
								((dataModel.isPreventCaseConflict() && newName.equalsIgnoreCase( firstOccur.getNewName() ))
								|| newName.equals( firstOccur.getNewName() ) )) {
							firstOccur.error( firstOccur.getNewName() );
							break; // look no further
						}
					}
				}
				collisions.add( collisionTest );
			}
			tableModel.notifyUpdate();
			if( collisionCount == 0 ) {
				SwingUtilities.invokeLater( new Runnable(){
					public void run() {
						renameCommand.setEnabled( true );
					}
				});
			}
		}
	}
	
}
