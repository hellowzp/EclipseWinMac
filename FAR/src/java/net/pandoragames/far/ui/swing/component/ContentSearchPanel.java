package net.pandoragames.far.ui.swing.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.FARForm;
import net.pandoragames.far.ui.model.OperationType;
import net.pandoragames.far.ui.model.ProgressListener;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.util.i18n.Localizer;

/**
 * A text area with added functionality for regular expressions.
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
public class ContentSearchPanel extends JPanel {

	private FARForm dataModel;
	private Localizer localizer;
	private UndoHistory undoManager = new UndoHistory(); 
	// child components
	private JTextArea contentPattern = new JTextArea();
	private JPanel contentFlagPanel;
	private JCheckBox regexFlag;
	private JCheckBox ignoreCaseFlag;
	private JCheckBox lineWrapFlag;
		
	/**
	 * Constructor takes title and configuration objects.
	 * @param title for top label
	 * @param config configuration 
	 * @param componentRepository wirering up
	 */
	public ContentSearchPanel(String title, FARForm form, SwingConfig config, ComponentRepository componentRepository) {
		localizer = config.getLocalizer();
		dataModel = form;
		init( title, config, componentRepository );
		initUndoManager( config, componentRepository, form.getType() );
	}
	
	/**
	 * Access to the JTextArea in this compound component.
	 * @return JTextArea in this compound component
	 */
	public JTextArea getTextArea() {
		return contentPattern;
	}
	
	/**
	 * Enables or disables all subcomponents.
	 * @param enabled to enable or disable
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		lineWrapFlag.setEnabled(enabled);
		contentPattern.setEnabled(enabled);
		contentFlagPanel.setEnabled(enabled);
		regexFlag.setEnabled(enabled);
		ignoreCaseFlag.setEnabled(enabled);
	}
	
	/**
	 * Adds the specified Checkbox to the flags under the textarea.
	 * @param flag to be added.
	 */
	public void addFlag(JCheckBox flag) {
		contentFlagPanel.add( flag );
	}

	/**
	 * Copies the content of the specified form into this component
	 * and the underlying data model.
	 * @param form to be loaded
	 * @param overwrite if true, the current state will always be overwritten.
	 * Otherwise the the current state will only be overwritten if the search string is empty
	 */
	public void loadForm(FARForm form, boolean overwrite) {
		if( overwrite || (dataModel.getSearchStringContent().length() == 0)) {
			dataModel.setSearchStringContent( form.getSearchStringContent() );
			contentPattern.setText( form.getSearchStringContent() );
			regexFlag.setSelected( form.isRegexContentPattern() );
			ignoreCaseFlag.setSelected( form.isIgnoreCase() );
		}
	}
	
	/**
	 * Returns a progress listener for the UIFace backend component that allows to 
	 * synchronise the replace content pattern field with the find content pattern field on 
	 * execution of the FIND command.
	 * @return special progress listener
	 */
	public ProgressListener getSynchronisationListener(FindForm findForm) {
		return new SynchronizeSearchFieldsListener(findForm);
	}
	
	private void init(String title, SwingConfig config, ComponentRepository componentRepository) {

		this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );		
		
		JLabel labelContentPattern = new JLabel( title );
		// labelContentPattern.setAlignmentX( Component.LEFT_ALIGNMENT );
		lineWrapFlag = new JCheckBox( localizer.localize("label.wrap-lines"));
		// workaround for windows bug: size not properly calculated for leading text
		int wiz = lineWrapFlag.getPreferredSize().width;
		lineWrapFlag.setHorizontalTextPosition( SwingConstants.LEADING );
		lineWrapFlag.setPreferredSize( new Dimension( wiz, lineWrapFlag.getPreferredSize().height ) );
		lineWrapFlag.setSelected( true );
		lineWrapFlag.setAlignmentX( Component.RIGHT_ALIGNMENT );
		lineWrapFlag.addItemListener( new WrapLinesListener( contentPattern ) );
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout( new BoxLayout( headerPanel, BoxLayout.X_AXIS ) );
		headerPanel.add(labelContentPattern);
		headerPanel.add(Box.createHorizontalGlue());
		headerPanel.add(lineWrapFlag);
		headerPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		this.add( headerPanel );
		
		contentPattern.setRows( 5 );
		contentPattern.setLineWrap( true );
		contentPattern.addFocusListener( new FocusListener() {
			public void focusGained(FocusEvent event) {}
			public void focusLost(FocusEvent event) {
				JTextComponent textcomponent = (JTextComponent) event.getSource();
				dataModel.setSearchStringContent( textcomponent.getText() );
			}
		});
		config.setFocusTraversalKeys( contentPattern );
		JScrollPane contentScrollPane = new JScrollPane(contentPattern);
		contentScrollPane.setAlignmentX( Component.LEFT_ALIGNMENT );
		this.add( contentScrollPane );
		componentRepository.getResetDispatcher().addToBeCleared( contentPattern );
		
		contentFlagPanel = new JPanel();
		FlowLayout flowLayout = new FlowLayout( FlowLayout.LEADING );
		flowLayout.setVgap( 1 );
		contentFlagPanel.setLayout( flowLayout );
		contentFlagPanel.setPreferredSize( new Dimension( contentScrollPane.getPreferredSize().width, 
															config.getStandardComponentHight() * 2 ) );
		contentFlagPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		regexFlag = new JCheckBox( localizer.localize("label.regular-expression"));
		regexFlag.setSelected( dataModel.isRegexContentPattern() );
		regexFlag.addItemListener( new ItemListener() {	
			public void itemStateChanged(ItemEvent event) {
					dataModel.setRegexContentPattern( (ItemEvent.SELECTED == event.getStateChange()) );
			}
		});
		
		contentFlagPanel.add( regexFlag );
		ignoreCaseFlag = new JCheckBox( localizer.localize("label.ignore-case"));
		ignoreCaseFlag.setSelected( dataModel.isIgnoreCase() );
		ignoreCaseFlag.addItemListener( new ItemListener() {	
			public void itemStateChanged(ItemEvent event) {
					dataModel.setIgnoreCase( (ItemEvent.SELECTED == event.getStateChange()) );
			}
		});
		contentFlagPanel.add( ignoreCaseFlag );
		this.add( contentFlagPanel );
	}
	
	private void initUndoManager(SwingConfig config, ComponentRepository componentRepository, OperationType operationType) {
		
		undoManager.registerUndoHistory( contentPattern );
		undoManager.registerSnapshotHistory( contentPattern );
			    
	    if( OperationType.FIND == operationType ) {
	    	componentRepository.getFindCommand().addResetable( undoManager );
	    } else if( OperationType.REPLACE == operationType ) {
	    	componentRepository.getReplaceCommand().addResetable( undoManager );
	    }
	    
	    UndoHistoryPopupMenu.createPopUpMenu(config, componentRepository, contentPattern);
	}
	
// -- inner classes (listener components) ---------------------------------------------------	
	
	/**
	 * Listens to the wrap-lines checkbox and changes the
	 * respective text area property accordingly.
	 */
	class WrapLinesListener implements ItemListener{
		private JTextArea textArea;
		public WrapLinesListener(JTextArea textField) {
			textArea = textField;
		}
		public void itemStateChanged(ItemEvent event) {
			textArea.setLineWrap( (ItemEvent.SELECTED == event.getStateChange()) );
		}
	}		
	
	/**
	 * This listener allows to synchronize the <i>search</i> content pattern
	 * with the <i>replace</i> content pattern.
	 */
	class SynchronizeSearchFieldsListener implements ProgressListener {
		private FindForm findForm;
		public SynchronizeSearchFieldsListener(FindForm findForm) {
			this.findForm = findForm;
		}
		/** not implemented */
		public void operationAborted(OperationType type) {
		}
		/** not implemented */
		public void operationProgressed(int count, int total, OperationType type) {
		}
		/** assumes that dataModel is an instance of ReplaceForm */
		public void operationStarted(OperationType type) {
			if( OperationType.FIND.equals(type) ) {
				loadForm( findForm, true );
			}
		}
		/** not implemented */
		public void operationTerminated(OperationType type) {
		}
	}
}

