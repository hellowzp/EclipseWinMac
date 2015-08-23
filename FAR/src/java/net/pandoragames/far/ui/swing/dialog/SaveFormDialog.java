package net.pandoragames.far.ui.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.FormPropertySet;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.ReplaceForm;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.MessageLabel;
import net.pandoragames.util.i18n.Localizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Dialog for saving of operation form data to file system.
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
public class SaveFormDialog extends SubWindow {

	private Localizer localizer;
	private File configdir;
	private File formListFile;
	private Properties formProperties = new Properties();
	private Properties formList = new Properties();
	private String extension;
	private JTextField textbox;
	private MessageBox messageBox;
	private Action saveAction = new SaveButtonListener();
	
	private Log logger = LogFactory.getLog( this.getClass() );
	
	public SaveFormDialog(JFrame owner, FindForm form, SwingConfig config) {
		this(owner, config.getLocalizer().localize("label.save-findform"), config);
		extension = SwingConfig.EXTENSION_FIND;
		FormPropertySet.getFindFormPropertySet().store(form, formProperties);
	}

	public SaveFormDialog(JFrame owner, ReplaceForm form, SwingConfig config) {
		this(owner, config.getLocalizer().localize("label.save-replaceform"), config);
		extension = SwingConfig.EXTENSION_REPLACE;
		FormPropertySet.getReplaceFormPropertySet().store(form, formProperties);
	}
	
	private SaveFormDialog(JFrame owner, String title, SwingConfig config) {
		super(owner, title, true);
		localizer = config.getLocalizer();
		configdir = config.getConfigDir();
		init(config);
		loadFormList();
		placeOnScreen( config.getScreenCenter() );		
	}
	
	private void init(SwingConfig config) {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		JPanel basePanel = new JPanel();
		basePanel.setBorder( BorderFactory.createEmptyBorder( SwingConfig.PADDING * 2, 
				SwingConfig.PADDING, 
				SwingConfig.PADDING, 
				SwingConfig.PADDING ) );
		basePanel.setLayout( new BorderLayout() );
		registerCloseWindowKeyListener( basePanel );
		this.add( basePanel );
		
		MessageLabel errorLabel = new MessageLabel();
		basePanel.add(errorLabel, BorderLayout.NORTH);
		messageBox = errorLabel;
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout( new BoxLayout(centerPanel, BoxLayout.Y_AXIS) );
		JLabel titleLabel = new JLabel(localizer.localize("label.name"));
		titleLabel.setAlignmentX(0);
		centerPanel.add( titleLabel );
		textbox = new JTextField();
		textbox.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, config.getStandardComponentHight()) );
		textbox.setAlignmentX(0);
		registerEnterKeyListener(textbox, saveAction);
		centerPanel.add(textbox);
		basePanel.add(centerPanel, BorderLayout.CENTER);
		
		JPanel buttonPannel = new JPanel();
		buttonPannel.setLayout( new FlowLayout(FlowLayout.RIGHT) );
		JButton okButton = new JButton( localizer.localize("button.save") );
		okButton.addActionListener( saveAction );
		JButton cancelButton = new JButton( localizer.localize("button.cancel") );
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent eve){
				SaveFormDialog.this.dispose();
			}
		});
		buttonPannel.add( okButton );
		buttonPannel.add( cancelButton );
		registerCloseWindowKeyListener( buttonPannel );
		this.add(buttonPannel, BorderLayout.SOUTH);	
	}
	
	private void loadFormList() {
		formListFile = new File( configdir, "formlist.properties");
		if( formListFile.exists() ) {
			InputStream input = null;
			try {
				input = new FileInputStream( formListFile );
				formList.load( input );
			} catch(IOException iox) {
				logger.error("IOException loading form list data: " + iox.getMessage());
			}
			finally {
				if( input != null ) try { input.close(); } catch(IOException iox) {}
			}
		} else {
			try {
				if( ! formListFile.createNewFile() ) logger.error("Form list could not be created");
			} catch(IOException iox) {
				logger.error("Form list could not be created");
			}
		}
	}

	class SaveButtonListener extends AbstractAction {
		public void actionPerformed(ActionEvent eve) {
			messageBox.clear();
			String formName = textbox.getText().trim();
			if( formName.isEmpty() ) {
				messageBox.error( localizer.localize("message.mandatory-text") );
				return;
			}
			String fileName = null;
			if( formList.containsValue( formName )) {
				String confirmTitle = localizer.localize("label.replace");
				String confirmMessage = localizer.localize("question.operation-exists");
				int confirmation = JOptionPane.showConfirmDialog(SaveFormDialog.this, confirmMessage, confirmTitle, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if( confirmation == JOptionPane.YES_OPTION ) {
					findAndRemove( formName );
				} else {
					SaveFormDialog.this.dispose();
					return;
				}
			} 
			long ts = System.currentTimeMillis() / 3000;
			String raw = String.valueOf(ts);
			fileName = raw.substring(raw.length() - 8 , raw.length());
			formList.setProperty(fileName, formName);
			File formFile = new File( configdir, fileName + "." + extension );
			if( saveProperties(formFile, formProperties) ) saveProperties( formListFile, formList);
			SaveFormDialog.this.dispose();
		}
		private boolean saveProperties(File file, Properties properties) {
			OutputStream output = null;
			try {
				if( ! file.exists() ) file.createNewFile();
				output = new FileOutputStream( file );
				properties.store(output, "Form Index");
				return true;
			} catch(IOException iox) {
				messageBox.error( localizer.localize("message.could-not-save") );
				logger.error("IOException saving form data: " + iox.getMessage() );
				return false;
			} finally {
				if( output != null ) try { output.close(); } catch(IOException iox) {}
			}			
		}
		private void findAndRemove(String formName) {
			String fileName = null;
			for(Object key : formList.keySet()) {
				if( formName.equals( formList.get(key))) {
					fileName = (String) key;
					break;
				}
			}
			if( fileName == null ) {
				logger.warn("Duplicate form name not found");
				return;
			}
			File file = new File( configdir, fileName + "." + SwingConfig.EXTENSION_FIND );
			if( file.exists() ) {
				file.delete();
			} else {
				file = new File( configdir, fileName + "." + SwingConfig.EXTENSION_REPLACE );
				if( file.exists() ) file.delete();
			}
			formList.remove(fileName);
		}
	}	
}
