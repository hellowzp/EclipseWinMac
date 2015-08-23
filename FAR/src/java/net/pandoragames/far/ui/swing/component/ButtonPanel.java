package net.pandoragames.far.ui.swing.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.pandoragames.far.ui.UIFace;
import net.pandoragames.far.ui.model.FileNamePattern;
import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.OperationType;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.listener.ConfirmReplaceListener;
import net.pandoragames.util.i18n.Localizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * The main button panel. 
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
public class ButtonPanel extends JPanel {
	
	private OperationType operationType;
	// private FindForm dataModel;
	private UIFace backend;
	private Localizer localizer;
	private Log logger = LogFactory.getLog( this.getClass() );
	private MessageBox messageBox;
	
	public ButtonPanel(OperationType type, SwingConfig config, ComponentRepository componentRepository) {
		operationType = type;
		localizer = config.getLocalizer();
		backend = componentRepository.getUiface();
		messageBox = componentRepository.getMessageBox();
		init( config, componentRepository );
	}
	
	/**
	 * Sets the component as center.
	 */
	public void setMainPanel(JPanel panel) {
		this.add( panel, BorderLayout.CENTER );
	}
	
	private void init(SwingConfig config, ComponentRepository componentRepository) {
		this.setLayout( new BorderLayout() );
		this.add( initButtonPannel( config, componentRepository ), BorderLayout.SOUTH );
	}
	
	/**
	 * Initialises the BUTTON panel.
	 * @param config configuration properties
	 * @param componentRepository repository for shared components
	 */
	private JPanel initButtonPannel(SwingConfig config, ComponentRepository componentRepository) {
		
		JPanel buttonPannel = new JPanel();
		buttonPannel.setAlignmentX( Component.LEFT_ALIGNMENT );
		buttonPannel.setLayout( new FlowLayout( FlowLayout.TRAILING ) );
		// FIND
		if( operationType == OperationType.FIND ) {
			JButton findButton = new JButton( localizer.localize("button.find"));
			componentRepository.getOperationCallBackListener().addComponentStartDisabled( findButton, OperationType.FIND );
			componentRepository.getOperationCallBackListener().addComponentTerminationEnabled( findButton, OperationType.FIND );
			findButton.addActionListener( componentRepository.getFindCommand() );
			findButton.addActionListener( new ReorderFilePatternListListener( 
												componentRepository.getFindForm(),
												config.getFileNamePatternListModel() ) );
			buttonPannel.add( findButton );
		}
		// REPLACE
		if( operationType == OperationType.REPLACE ) {
			JButton replaceButton = new JButton( localizer.localize("button.replace"));
			replaceButton.setEnabled( false );
			componentRepository.getOperationCallBackListener().addComponentTerminationEnabled( replaceButton, OperationType.FIND );
			componentRepository.getResetDispatcher().addToBeDisabled( replaceButton );
			componentRepository.getSearchBaseListener().addToBeDisabled( replaceButton );
			ConfirmReplaceListener replaceListener = 
				new ConfirmReplaceListener(componentRepository.getRootWindow(), config, componentRepository.getReplaceForm());
			replaceListener.addActionListener( componentRepository.getReplaceCommand() );
			replaceButton.addActionListener( replaceListener );
			buttonPannel.add( replaceButton );
		}
		// RENAME
		if( operationType == OperationType.RENAME ) {
			JButton renameButton = new JButton( componentRepository.getRenameCommand() );
			renameButton.setEnabled( false );
			componentRepository.getResetDispatcher().addToBeDisabled( renameButton );
			buttonPannel.add( renameButton );			
		}
		// CANCEL
		JButton cancelButton = new JButton( localizer.localize("button.cancel"));
		cancelButton.setEnabled( false );
		componentRepository.getOperationCallBackListener().addComponentStartEnabled( cancelButton, OperationType.ANY );
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				backend.abort();
			}						
		});
		buttonPannel.add( cancelButton );	
		componentRepository.getResetDispatcher().addToBeDisabled( cancelButton );
		// UNDO
		if( operationType == OperationType.REPLACE ) {
			JButton undoButton = new JButton( localizer.localize("button.undo"));
			undoButton.setEnabled( false );
			componentRepository.getOperationCallBackListener().addComponentTerminationEnabled( undoButton, OperationType.REPLACE );
			componentRepository.getOperationCallBackListener().addComponentStartDisabled(undoButton, OperationType.FIND );
			undoButton.addActionListener( componentRepository.getUndoListener() );
			undoButton.addActionListener( new OnClickDisable(undoButton) );
			buttonPannel.add( undoButton );
		}
		// RESET
		JButton clearButton = new JButton( localizer.localize("button.reset"));
		clearButton.addActionListener( componentRepository.getResetDispatcher() );
		buttonPannel.add( clearButton );		
		// this.add( buttonPannel );
		return buttonPannel;
	}
	
	/**
	 * Disables a button once it has been clicked.
	 */
	class OnClickDisable implements ActionListener {
		JButton me;
		public OnClickDisable(JButton button) {
			me = button;
		}
		public void actionPerformed(ActionEvent event) {
			me.setEnabled( false );
		}								
	}
	
	/**
	 * Puts the last used file pattern on top of the list.
	 */
	class ReorderFilePatternListListener implements ActionListener {
		private FindForm findForm;
		private FileNamePatternComboboxModel patternList;
		public ReorderFilePatternListListener(FindForm form, FileNamePatternComboboxModel filePattern) {
			findForm = form;
			patternList = filePattern;
		}
		public void actionPerformed(ActionEvent event) {
			if( ! findForm.getFileNamePattern().equals( patternList.getElementAt(0) ) ) {
				boolean found = false;
				int count = 1;
				while((! found) && (count < patternList.getSize())) {
					FileNamePattern pattern = patternList.getElementAt(count);
					if( findForm.getFileNamePattern().equals( pattern ) ){
						found = true;
						if( patternList.remove(pattern) ) {
							patternList.insertElementAt(pattern, 0);
							patternList.setSelectedItem(pattern);
							logger.debug("Moved " + pattern + " on top");
						}
					}
					count++;
				}
			}
		}								
	}
	
}
