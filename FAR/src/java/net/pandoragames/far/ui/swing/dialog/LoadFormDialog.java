package net.pandoragames.far.ui.swing.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.pandoragames.far.ui.model.FARForm;
import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.FormPropertySet;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.OperationType;
import net.pandoragames.far.ui.model.ReplaceForm;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.MessageLabel;
import net.pandoragames.util.i18n.Localizer;

/**
 * Dialog to load a previously saved operation form.
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
public class LoadFormDialog extends SubWindow {

	private Localizer localizer;
	private File configdir;
	private FormListEntryListModel formListModel;
	private JList operationList;
	private FARForm form;
	private JButton loadButton;
	private JButton deleteButton;
	private MessageBox messageBox;
	private SwingConfig config;

	public LoadFormDialog(JFrame owner, FindForm form, SwingConfig config) {
		this(owner, config.getLocalizer().localize("label.load-findform"), SwingConfig.EXTENSION_FIND, config);
		this.form = form;
	}

	public LoadFormDialog(JFrame owner, ReplaceForm form, SwingConfig config) {
		this(owner, config.getLocalizer().localize("label.load-replaceform"), SwingConfig.EXTENSION_REPLACE, config);
		this.form = form;
	}

	private LoadFormDialog(JFrame owner, String title, String extension, SwingConfig config) {
		super(owner, title, true);
		this.config = config;
		localizer = config.getLocalizer();
		configdir = config.getConfigDir();
		loadFormList(extension);
		init(config);
		placeOnScreen( config.getScreenCenter() );		
	}

	private void loadFormList(final String extension) {
		File formListFile = new File( configdir, SwingConfig.FORMLIST_FILENAME);
		if( formListFile.exists() ) {
			Properties formSet = new Properties();
			InputStream input = null;
			try {
				input = new FileInputStream( formListFile );
				formSet.load( input );
			} catch(IOException iox) {
				logger.error("IOException loading form list data: " + iox.getMessage());
			}
			finally {
				if( input != null ) try { input.close(); } catch(IOException iox) {}
			}
			File[] fileList = configdir.listFiles( new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith("." + extension);
				}
			});
			List<FormListEntry> formList = new ArrayList<FormListEntry>();
			for(File file : fileList ) {
				if( file.getName().length() < 8) {
					logger.warn("Illegal file name: " + file.getName());
					continue;
				}
				String simpleName = file.getName().substring(0, 8);
				if( formSet.containsKey( simpleName )) {
					formList.add( new FormListEntry(simpleName, formSet.getProperty(simpleName), file));
				} else {
					logger.warn("File " + file.getName() + " not listed");
				}
			}
			Collections.sort(formList, new Comparator<FormListEntry>() {
				public int compare(FormListEntry o1, FormListEntry o2) {
					return o1.getLabel().compareTo(o2.getLabel());
				}
			});
			formListModel = new FormListEntryListModel( formList );
		} else {
			try {
				formListModel = new FormListEntryListModel( new ArrayList<FormListEntry>() );
				if( ! formListFile.createNewFile() ) logger.error("Form list could not be created");
			} catch(IOException iox) {
				logger.error("Form list could not be created");
			}
		}
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
		if( formListModel.getSize() == 0 ) {
			messageBox.error( localizer.localize("message.no-operation") );
		}
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout( new BoxLayout(centerPanel, BoxLayout.Y_AXIS) );
		operationList = new JList( formListModel );
		operationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		operationList.setVisibleRowCount(7);
		operationList.setAlignmentX(0);
		operationList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				JList list = (JList) e.getSource();
				loadButton.setEnabled( list.getSelectedIndex() >= 0 );
				deleteButton.setEnabled( list.getSelectedIndex() >= 0 );
			}
		});
		JScrollPane scrollPane = new JScrollPane(operationList);
		centerPanel.add(scrollPane);
		basePanel.add(centerPanel, BorderLayout.CENTER);
		
		JPanel buttonPannel = new JPanel();
		buttonPannel.setLayout( new FlowLayout(FlowLayout.RIGHT) );
		loadButton = new JButton( localizer.localize("button.load") );
		loadButton.addActionListener( new LoadButtonListener() );
		loadButton.setEnabled( false );
		deleteButton = new JButton( localizer.localize("button.delete") );
		deleteButton.addActionListener( new DeleteButtonListener() );
		deleteButton.setEnabled( false );
		JButton cancelButton = new JButton( localizer.localize("button.cancel") );
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent eve){
				LoadFormDialog.this.dispose();
			}
		});
		buttonPannel.add( loadButton );
		buttonPannel.add( deleteButton );
		buttonPannel.add( cancelButton );
		registerCloseWindowKeyListener( buttonPannel );
		this.add(buttonPannel, BorderLayout.SOUTH);	
	}
	
	// TODO: shield this shit behind a proper DAO
	class DeleteButtonListener implements ActionListener  {
		public void actionPerformed(ActionEvent eve) {
			FormListEntry entry = (FormListEntry) operationList.getSelectedValue();
			if( entry.getFile().delete() ) {
				formListModel.remove( entry );
				InputStream input = null;
				OutputStream output = null;
				try {
					File formListFile = new File( configdir, SwingConfig.FORMLIST_FILENAME);
					input = new FileInputStream( formListFile );
					Properties formSet = new Properties();
					formSet.load(input);
					input.close();
					input = null;
					formSet.remove( entry.getCode() );
					output = new FileOutputStream( formListFile );
					formSet.store(output, "Form Index");
				} catch(IOException iox) {
					logger.error("IOException saving form data: " + iox.getMessage() );
				} finally {
					if( input != null ) try{ input.close(); }catch(IOException icx) {/*ignore me*/}
					if( output != null ) try{ output.close(); }catch(IOException icx) {/*ignore me*/}
				}				
			} else {
				logger.warn("Formlist file " + entry.getFile().getName() + " could not be removed");
			}
		}
	}
	
	class LoadButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent eve) {
			FormListEntry entry = (FormListEntry) operationList.getSelectedValue();
			InputStream input = null;
			try {
				input = new FileInputStream( entry.getFile() );
				Properties properties = new Properties();
				properties.load( input );
				if( OperationType.FIND == form.getType() ) {
					FindForm findForm = ( FindForm ) form;
					FormPropertySet.getFindFormPropertySet().load(findForm, properties);
					config.setBaseDirectory( form.getBaseDirectory() );
				} else if( OperationType.REPLACE == form.getType() ) {
					ReplaceForm repForm = (ReplaceForm) form;
					FormPropertySet.getReplaceFormPropertySet().load(repForm, properties);
				} else {
					logger.error("Unexpected OperationType: " + form.getType());
					messageBox.error( localizer.localize("message.could-not-load-form"));
					return;
				}
				LoadFormDialog.this.dispose();
			} catch(IOException iox) {
				logger.error("IOException loading form definition from " + entry.getFile().getName() + ": " + iox.getMessage());
				messageBox.error( localizer.localize("message.could-not-load-form"));
			} catch(Exception x) {
				logger.error( x.getClass().getSimpleName() + " loading form definition from " + entry.getFile().getName() + ": " + x.getMessage());
				messageBox.error( localizer.localize("message.could-not-load-form"));				
			} finally {
				if( input != null ) try{ input.close(); }catch(IOException icx) {/*ignore me*/}
			}
		}
	}

	class FormListEntryListModel extends AbstractListModel {

		private List<FormListEntry> formList = new ArrayList<FormListEntry>();
		
		public FormListEntryListModel(List<FormListEntry> data) {
			formList = data;
		}
		public Object getElementAt(int index) {
			return formList.get(index);
		}

		public int getSize() {
			return formList.size();
		}
		
		public void remove(FormListEntry entry) {
			if(formList.contains( entry ) ) {
				int index = formList.indexOf( entry );
				formList.remove(index);
				fireIntervalRemoved(this, index, index);
			}
		}
	}
}

class FormListEntry {
	private String code;
	private String label;
	private File file;
	public FormListEntry(String code, String label, File file) {
		this.code = code;
		this.label = label;
		this.file = file;
	}
	public String getLabel() {
		return label;
	}
	public File getFile() {
		return file;
	}
	public String toString() { 
		return label;
	}
	public String getCode() {
		return code;
	}
	public boolean equals(Object o) {
		if( o == null ) return false;
		try {
			FormListEntry other = (FormListEntry) o;
			return label.equals(other.label) && file.equals( other.file );
		} catch(ClassCastException ccx) {
			return false;
		}
	}
	public int hashCode() {
		return label.hashCode() + file.hashCode();
	}
}
