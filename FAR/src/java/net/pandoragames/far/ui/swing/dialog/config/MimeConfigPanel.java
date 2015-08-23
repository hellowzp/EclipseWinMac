package net.pandoragames.far.ui.swing.dialog.config;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.DocumentEvent;

import net.pandoragames.far.ui.model.FileType;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.MimeRegistry;
import net.pandoragames.far.ui.model.MimeType;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.listener.AbstractDocumentChangedListener;

/**
 * Edit Mime Type definitions.
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
public class MimeConfigPanel extends ConfigPanel {

	private static final String TXTPLAIN = "text/plain";
	
	private JTextField nameField;

	private JComboBox mimeList;
	private JComboBox textTypeList;
	private JComboBox binaryTypeList;
	private JComboBox listCharset;
	private JTextField extensionField;
	private JButton addButton;
	private JButton applyButton;
	private JButton deleteButton;
	
	private MimeType currentType = (MimeType) MimeType.getType( TXTPLAIN );
	private List<MimeType> modifiedTypes = new ArrayList<MimeType>();
	
	private MimeRegistry shadowRegistry;
	
	public MimeConfigPanel(SwingConfig swingConfig, ComponentRepository repository, MessageBox box) {
		super(box, repository, swingConfig);
	}

	@Override
	protected void init(ComponentRepository repository, SwingConfig swingConfig) {
		setLayout( new BorderLayout() );
		
		initTopPanel( repository, swingConfig);
		initLeftPanel( repository, swingConfig);
		initRightPanel( repository, swingConfig);
	}

	private void initTopPanel(ComponentRepository repositoryz, SwingConfig swingConfig) {

		JPanel topPanel = new JPanel();
		topPanel.setLayout( new BoxLayout( topPanel, BoxLayout.Y_AXIS ) );		
		JPanel topButtonPanel = new JPanel();
		topButtonPanel.setLayout( new FlowLayout(FlowLayout.RIGHT));
		topButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		JButton newTextTypeButton = new JButton(localizer.localize("button.newTextType"));
		newTextTypeButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				clearFormFields();
				nameField.setText("text/");
				binaryTypeList.setVisible( false );
				textTypeList.setVisible( true );
				textTypeList.setSelectedItem( FileType.TEXT );
				textTypeList.setEnabled( true );
				listCharset.setEnabled( true );
			}
		});
		topButtonPanel.add(newTextTypeButton);
		JButton newBinaryTypeButton = new JButton(localizer.localize("button.newBinaryType"));
		newBinaryTypeButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				clearFormFields();
				nameField.setText("application/");
				textTypeList.setVisible( false );
				binaryTypeList.setVisible( true );
				binaryTypeList.setSelectedItem( FileType.BINARY );
				binaryTypeList.setEnabled( true );
				listCharset.setEnabled( false );
			}
		});
		topButtonPanel.add(newBinaryTypeButton);
		topPanel.add(topButtonPanel);
		
		shadowRegistry = MimeType.MimeRegistry.createShadowRegistry();
		List<MimeType> mimeTypes = shadowRegistry.listAll();
		Collections.sort(mimeTypes, new Comparator<MimeType>(){
			public int compare(MimeType first, MimeType second) {
				// reverse order
				return second.getName().compareTo( first.getName() );
			}
		});
		mimeList = new JComboBox( mimeTypes.toArray() );
		mimeList.setSelectedItem( MimeType.getType(TXTPLAIN) );
		mimeList.setAlignmentX( Component.LEFT_ALIGNMENT );
		mimeList.addItemListener( new ItemListener() {
			public void itemStateChanged(ItemEvent eve) {
				if( eve.getStateChange() == ItemEvent.SELECTED ) {
					MimeType mime = (MimeType) eve.getItem();
					nameField.setText( mime.getName() );
					nameField.setEnabled(false);
					if( mime.isBinary() ) {
						textTypeList.setVisible( false );
						binaryTypeList.setVisible( true );
						binaryTypeList.setSelectedItem( mime.getParentType() );
						binaryTypeList.setEnabled( false );
						listCharset.setEnabled( false );
					} else {
						binaryTypeList.setVisible( false );
						textTypeList.setVisible( true );
						textTypeList.setSelectedItem( mime.getParentType() );
						textTypeList.setEnabled( false );
						listCharset.setEnabled( true );
					}
					listCharset.setSelectedItem( mime.isCharsetDefined() ? mime.getCharacterset() : null );
					extensionField.setText( formatExtensionList( mime.listFileExtensions() ) );
					applyButton.setVisible( true );
					addButton.setVisible( false );
					applyButton.setEnabled( false );
					deleteButton.setEnabled( !mime.isPredefined() );
					currentType = mime;
				}
			}
		});
		topPanel.add(mimeList);

		topPanel.add( Box.createVerticalStrut( SwingConfig.PADDING ));
		
		add( topPanel, BorderLayout.NORTH );
	}
	
	private void clearFormFields() {
		nameField.setEnabled(true);
		nameField.setText("");
		extensionField.setText("");
		listCharset.setSelectedItem(null);
		applyButton.setVisible( false );
		addButton.setVisible( true );
		addButton.setEnabled( false );
		deleteButton.setEnabled( false );
		currentType = null; 
	}
	
	private void initLeftPanel(ComponentRepository repositoryz, SwingConfig swingConfig) {
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.Y_AXIS ) );		
		
		JLabel nameLabel = new JLabel(localizer.localize("label.name"));
		nameLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
		leftPanel.add( nameLabel );
		
		nameField = new JTextField(TXTPLAIN);
		nameField.setAlignmentX( Component.LEFT_ALIGNMENT );
		nameField.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, swingConfig.getStandardComponentHight()) );
		nameField.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, swingConfig.getStandardComponentHight()) );
		nameField.getDocument().addDocumentListener(new AbstractDocumentChangedListener() {
			public void documentChanged(DocumentEvent eve) {
				if( MimeType.isValidMimeIdentifier( nameField.getText() )) {
					addButton.setEnabled( true );
				} else {
					addButton.setEnabled( false );
				}
			}
		});
		nameField.setEnabled( false );
		leftPanel.add( nameField );

		leftPanel.add( Box.createVerticalStrut( SwingConfig.PADDING ));

		JLabel typeLabel = new JLabel(localizer.localize("label.file-type"));
		typeLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
		leftPanel.add( typeLabel );

		List<FileType> textList = getTextTypes();
		textTypeList = new JComboBox( textList.toArray() );
		textTypeList.setAlignmentX( Component.LEFT_ALIGNMENT );
		textTypeList.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, swingConfig.getStandardComponentHight()) );
		textTypeList.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, swingConfig.getStandardComponentHight()) );
		textTypeList.setSelectedItem( FileType.TEXT );
		textTypeList.setEnabled( false );
		textTypeList.setRenderer(new FileTypeListCellRenderer() );
		leftPanel.add( textTypeList );

		List<FileType> binaryList = getBinaryTypes();
		binaryTypeList = new JComboBox( binaryList.toArray() );
		binaryTypeList.setAlignmentX( Component.LEFT_ALIGNMENT );
		binaryTypeList.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, swingConfig.getStandardComponentHight()) );
		binaryTypeList.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, swingConfig.getStandardComponentHight()) );
		binaryTypeList.setVisible( false );
		binaryTypeList.setRenderer(new FileTypeListCellRenderer() );
		leftPanel.add( binaryTypeList );

		leftPanel.add( Box.createVerticalGlue() );
		
		add( leftPanel, BorderLayout.WEST);
	}
	
	private void initRightPanel(ComponentRepository repositoryz, SwingConfig swingConfig) {
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.Y_AXIS ) );		
		
		JLabel encodingLabel = new JLabel(localizer.localize("label.charset"));
		encodingLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
		rightPanel.add( encodingLabel );
		
		Charset[] charsetlist = new Charset[swingConfig.getCharsetList().size() + 1];
		swingConfig.getCharsetList().toArray(charsetlist);
		listCharset = new JComboBox( charsetlist );
		listCharset.setAlignmentX( Component.LEFT_ALIGNMENT );
		listCharset.setSelectedItem( MimeType.getType(TXTPLAIN).isCharsetDefined() ? MimeType.getType(TXTPLAIN).getCharacterset() : null );
		listCharset.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, 
				swingConfig.getStandardComponentHight()) );
		listCharset.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, 
										swingConfig.getStandardComponentHight()) );
		listCharset.setRenderer( new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list,
		            Object value,
		            int index,
		            boolean isSelected,
		            boolean cellHasFocus) {
				JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if( value == null ) {
					comp.setText( localizer.localize("label.default") + " (" + getSelectedFileType().getCharacterset() + ")");
				}
				return comp;
			}
		});
		listCharset.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent eve) {
				if( ! applyButton.isEnabled() ) {
					applyButton.setEnabled( true );
				}
			}
		});
		// Make sure the list contains all used charsets, even if not public available
		for(MimeType mime : shadowRegistry.listAll() ) {
			if( mime.isCharsetDefined() && ! swingConfig.getCharsetList().contains( mime.getCharacterset() )) {
				((MutableComboBoxModel) listCharset.getModel()).addElement( mime.getCharacterset() );
			}
		}
		rightPanel.add( listCharset );
		
		rightPanel.add( Box.createVerticalStrut( SwingConfig.PADDING ));

		JLabel extensionLabel = new JLabel(localizer.localize("label.file-extension-list"));
		extensionLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
		rightPanel.add( extensionLabel );
		
		extensionField = new JTextField( formatExtensionList(((MimeType) MimeType.getType(TXTPLAIN)).listFileExtensions()));
		extensionField.setAlignmentX( Component.LEFT_ALIGNMENT );
		extensionField.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, 
				swingConfig.getStandardComponentHight()) );
		extensionField.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, 
										swingConfig.getStandardComponentHight()) );
		extensionField.getDocument().addDocumentListener(new AbstractDocumentChangedListener() {
			public void documentChanged(DocumentEvent eve) {
				applyButton.setEnabled( true );
			}
		});
		rightPanel.add( extensionField );
		
		
		rightPanel.add( Box.createVerticalStrut( SwingConfig.PADDING ));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		buttonPanel.setLayout( new FlowLayout(FlowLayout.RIGHT));

		addButton = new JButton(localizer.localize("button.add"));
 		addButton.setVisible( false );
		addButton.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				JComboBox typeList = textTypeList.isVisible() ? textTypeList : binaryTypeList;
				MimeType mimeType = new AddedMime(nameField.getText(), (FileType) typeList.getSelectedItem(), shadowRegistry);
				mimeType.setCharacterset( (Charset) listCharset.getSelectedItem() );
				if( extensionField.getText().length() > 0 ) {
					String[] extensions = extensionField.getText().split("\\s+");
					for(String ext : extensions) {
						mimeType.addExtension(ext);
					}
				}
				mimeList.addItem(mimeType);
				mimeList.setSelectedItem(mimeType);
				addButton.setVisible( false );
				applyButton.setVisible( true );
				applyButton.setEnabled( false );
				currentType = mimeType;
				modifiedTypes.add( mimeType );
			}
		});
		buttonPanel.add( addButton );

		applyButton = new JButton(localizer.localize("button.apply"));
		applyButton.setEnabled( false );
		applyButton.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				currentType.setCharacterset(  (Charset) listCharset.getSelectedItem() );
				List<String> extlist = currentType.listFileExtensions();
				String[] extensions = extensionField.getText().split("\\s+");
				for(String ext : extensions) {
					if( ! extlist.contains( ext )) {
						currentType.addExtension(ext);
						extlist.add( ext );
					}
				}
				List<String> extensionList = Arrays.asList( extensions );
				for(String ext : extlist) {
					if( ! extensionList.contains(ext)) currentType.removeExtension( ext );
				}
				modifiedTypes.add( currentType );
				applyButton.setEnabled( false );
			}
		});
		buttonPanel.add( applyButton );

		deleteButton = new JButton(localizer.localize("button.delete"));
		deleteButton.setEnabled( false );
		deleteButton.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				modifiedTypes.add( new RemovedMime(currentType) );
				mimeList.removeItem( currentType );
				clearFormFields();
			}
		});
		buttonPanel.add( deleteButton );
		rightPanel.add( buttonPanel );
		
		rightPanel.add( Box.createVerticalGlue() );
		
		add( rightPanel, BorderLayout.EAST);

	}
	
	private String formatExtensionList(List<String> list) {
		if( list == null || list.size() == 0 ) return "";
		if( list.size() == 1 ) return list.get(0);
		StringBuilder buffer = new StringBuilder();
		for( String ext : list ) buffer.append( ext ).append(' ');
		return buffer.toString();
	}

	private List<FileType> getTextTypes() {
		List<FileType> list = new ArrayList<FileType>();
		for( FileType.BUILDIN type : FileType.BUILDIN.values() ) {
			FileType candidate = FileType.getType(type.name());
			if( candidate.isText() ) list.add( candidate );
		}
		return list;
	}

	private List<FileType> getBinaryTypes() {
		List<FileType> list = new ArrayList<FileType>();
		for( FileType.BUILDIN type : FileType.BUILDIN.values() ) {
			FileType candidate = FileType.getType(type.name());
			if( candidate.isBinary() ) list.add( candidate );
		}
		return list;
	}
	
	private FileType getSelectedFileType() {
		if( textTypeList.isVisible() ) {
			return (FileType) textTypeList.getSelectedItem();
		} else {
			return (FileType) binaryTypeList.getSelectedItem();
		}
	}

	@Override
	public void save(SwingConfig swingConfig) {
		for(MimeType mime : modifiedTypes) {
			if( mime instanceof AddedMime) {
				MimeType tbAdded = ((AddedMime) mime).copy( MimeType.MimeRegistry );
				logger.info("MimeType " + tbAdded.getName() + " added");
			} else if( mime instanceof RemovedMime) {
				MimeType.MimeRegistry.remove(mime);
				logger.info("MimeType " + mime.getName() + " removed");
			} else {
				MimeType tbModified = (MimeType) MimeType.getType( mime.getName() );
				Charset charset = mime.isCharsetDefined() ? mime.getCharacterset() : null;
				tbModified.setCharacterset( charset );
				for(String ext : mime.listFileExtensions()) {
					if( !tbModified.matchExtension(ext) ) tbModified.addExtension(ext);
				}
				for(String ext : tbModified.listFileExtensions() ) {
					if(!mime.matchExtension(ext) ) tbModified.removeExtension(ext);
				}
			}
		}
	}
	
	class AddedMime extends MimeType {
		public AddedMime(String name, FileType category,
				net.pandoragames.far.ui.model.MimeRegistry registry) {
			super(name, category, registry);
		}
		public MimeType copy(MimeRegistry registry) {
			List<String> extensionlist = listFileExtensions();
			clearInternalExtensionList();
			MimeType copy = super.copy(registry);
			for(String ext : extensionlist) {
				copy.addExtension(ext);
			}
			return copy;
		}
	}

	class RemovedMime extends MimeType {
		public RemovedMime(MimeType type) {
			super(type.getName(), type.getParentType(), shadowRegistry);
			shadowRegistry.remove(type);
		}
	}
}

class FileTypeListCellRenderer extends DefaultListCellRenderer {
	public Component getListCellRendererComponent(JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
		JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		FileType type = (FileType) value;
		comp.setText(type.getName());
		return comp;
	}
}
