package net.pandoragames.far.ui.swing.dialog.config;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import net.pandoragames.far.ui.FARConfig;
import net.pandoragames.far.ui.SimpleFileNamePattern;
import net.pandoragames.far.ui.model.AbstractFileRepository;
import net.pandoragames.far.ui.model.FileNamePattern;
import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.RenameForm;
import net.pandoragames.far.ui.model.ReplaceForm;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.FileNamePatternComboboxModel;
import net.pandoragames.far.ui.swing.component.TwoComponentsPanel;
import net.pandoragames.far.ui.swing.component.listener.BackupFlagListener;
import net.pandoragames.far.ui.swing.component.listener.BrowseButtonListener;
import net.pandoragames.util.i18n.Localizer;

import org.apache.commons.logging.LogFactory;

public class MiscellaneousPanel extends ConfigPanel {

	private DefaultComboBoxModel fileNamePatternList;
	private JCheckBox patternFlag;
	private char groupReference;
	private boolean doBackup;
	private File backupDirectory;
	private List<FileNamePatternListAction> fnpActionList;
	private boolean treatUnknwonAsBinaryFlag;
	private boolean processBinaryFlag;
	// listeners to be informed on change
	private BackupFlagListener backupFlagListener;
	private ItemEvent backupFlagEvent;
	// model beans
	private ReplaceForm activeReplaceForm;
	private RenameForm renameForm;


	public MiscellaneousPanel(SwingConfig swingConfig, ComponentRepository repository, MessageBox messageBox) {
		super(messageBox, repository, swingConfig);
		
	}

	@Override
	protected void init(ComponentRepository repository, SwingConfig swingConfig) {

		ReplaceForm replaceForm = repository.getReplaceForm();
		groupReference = replaceForm.getGroupReference();
		doBackup = replaceForm.isDoBackup();
		backupFlagListener = repository.getBackupFlagListener();
		backupDirectory = replaceForm.getBackupDirectory();
		activeReplaceForm = repository.getReplaceForm();
		renameForm = repository.getRenameForm();
		processBinaryFlag = swingConfig.isProcessBinary();
		treatUnknwonAsBinaryFlag = swingConfig.isTreatUnknownFileTypesAsBinary();

		fileNamePatternList = new DefaultComboBoxModel();
		for(FileNamePattern pattern : swingConfig.getFileNamePatternListModel().asList()) {
			fileNamePatternList.addElement(pattern);
		}
		fnpActionList = new ArrayList<FileNamePatternListAction>();
		
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

		
		// add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
		add( Box.createVerticalGlue());

		
		// file name pattern
		JPanel fileNamePatternPanel = new JPanel();
		fileNamePatternPanel.setBorder( BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ),
				swingConfig.getLocalizer().localize("label.file-name-pattern")));
		fileNamePatternPanel.setLayout( new BoxLayout( fileNamePatternPanel, BoxLayout.Y_AXIS ) );
		fileNamePatternPanel.setAlignmentX( Component.LEFT_ALIGNMENT );

//		JLabel labelPattern = new JLabel( localizer.localize("label.file-name-pattern") );
//		labelPattern.setAlignmentX( Component.LEFT_ALIGNMENT );
//		this.add( labelPattern );
		
		JComboBox listPattern = new JComboBox( fileNamePatternList );
		listPattern.setEditable( true );
		listPattern.setSelectedIndex(0);
		listPattern.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, 
				swingConfig.getStandardComponentHight()) );
		
		JButton buttonSavePattern = new JButton( localizer.localize("button.add"));
		buttonSavePattern.setEnabled( false );
		
		patternFlag = new JCheckBox( localizer.localize("label.regular-expression"));
		patternFlag.setAlignmentX( Component.LEFT_ALIGNMENT );
		patternFlag.setSelected( ((FileNamePattern)fileNamePatternList.getElementAt(0)).isRegex() );
		patternFlag.addItemListener( new ItemListener() {	
				public void itemStateChanged(ItemEvent event) {
					((FileNamePattern) fileNamePatternList.getSelectedItem()).setRegex((ItemEvent.SELECTED == event.getStateChange()));
				}
			});
		listPattern.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent event) {
		        JComboBox cbox = (JComboBox) event.getSource();
		        FileNamePattern fileNamePattern = (FileNamePattern) cbox.getSelectedItem();
		        patternFlag.setSelected( fileNamePattern.isRegex() );

			}
		});
		ComboBoxEditor comboBoxEditor = new FileNamePatternEditor( buttonSavePattern, patternFlag ); 
		listPattern.setEditor( comboBoxEditor );
		buttonSavePattern.addActionListener( new SaveFileNamePatternListener(listPattern, 
														patternFlag,
														fnpActionList,
														messageBox,
														localizer) );
		JPanel patternEditPanel = new JPanel();
		patternEditPanel.setLayout( new BoxLayout( patternEditPanel, BoxLayout.X_AXIS ) );
		patternEditPanel.add(listPattern);
		// patternEditPanel.add(Box.createRigidArea(new Dimension( SwingConfig.PADDING, 1 )));
		patternEditPanel.add(patternFlag);
		patternEditPanel.setAlignmentX( Component.LEFT_ALIGNMENT );

		fileNamePatternPanel.add( patternEditPanel );
		
		JPanel patternButtonPanel = new JPanel();
		patternButtonPanel.setLayout( new FlowLayout(FlowLayout.RIGHT) );
		patternButtonPanel.add(buttonSavePattern);
		JButton buttonDeletePattern = new JButton( localizer.localize("button.remove"));
		buttonDeletePattern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileNamePattern pattern = (FileNamePattern) fileNamePatternList.getSelectedItem();
				if( pattern != null ) {
					fileNamePatternList.removeElement(pattern);
					fnpActionList.add( FileNamePatternListAction.deleteAction(pattern) );
				}
			}
		});
		patternButtonPanel.add(buttonDeletePattern);
		patternButtonPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		fileNamePatternPanel.add( patternButtonPanel );
		this.add( fileNamePatternPanel );
		
		// file processing
		add( Box.createVerticalGlue());

		JCheckBox treatUnknwonAsBinaryBox = new JCheckBox( swingConfig.getLocalizer().localize("label.treat-unknown-mime-as-binary"));
		treatUnknwonAsBinaryBox.setAlignmentX( Component.LEFT_ALIGNMENT );
		treatUnknwonAsBinaryBox.setSelected( swingConfig.isTreatUnknownFileTypesAsBinary() );
		treatUnknwonAsBinaryBox.addItemListener( new ItemListener() {
 			public void itemStateChanged(ItemEvent event) {
 				treatUnknwonAsBinaryFlag = ItemEvent.SELECTED == event.getStateChange();
 			}
 		});
		add( treatUnknwonAsBinaryBox );

		JCheckBox processBinaryBox = new JCheckBox( swingConfig.getLocalizer().localize("label.do-not-process-binary"));
		processBinaryBox.setAlignmentX( Component.LEFT_ALIGNMENT );
		processBinaryBox.setSelected( ! swingConfig.isProcessBinary() );
		processBinaryBox.addItemListener( new ItemListener() {
 			public void itemStateChanged(ItemEvent event) {
 				processBinaryFlag = ItemEvent.DESELECTED == event.getStateChange();
 			}
 		});
		add( processBinaryBox );
		

		add( Box.createVerticalGlue());
		
		// add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
		
		// select the group selector
//		JPanel selectorPanel  = new JPanel();
//		selectorPanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
//		selectorPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		JLabel labelSelector = new JLabel( swingConfig.getLocalizer().localize("label.group-ref-indicator") + ": " );
//		selectorPanel.add(labelSelector);
		JComboBox selectorBox = new JComboBox( FARConfig.GROUPREFINDICATORLIST );
		selectorBox.setPreferredSize( new Dimension( selectorBox.getPreferredSize().width, swingConfig.getStandardComponentHight()));
		selectorBox.setMaximumSize( new Dimension( selectorBox.getPreferredSize().width, swingConfig.getStandardComponentHight()));
		selectorBox.setSelectedItem( Character.toString( groupReference ) );
		selectorBox.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
		        JComboBox cbox = (JComboBox) event.getSource();
		        String indicator = (String) cbox.getSelectedItem();
		        groupReference = indicator.charAt(0);
			}
		});
//		selectorPanel.add(selectorBox);
		JPanel selectorPanel  = new TwoComponentsPanel(labelSelector, selectorBox);
		selectorPanel.setAlignmentX( Component.LEFT_ALIGNMENT );		
		add(selectorPanel);
		
		add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));

		// checkbox DO BACKUP
		JCheckBox doBackupFlag = new JCheckBox( swingConfig.getLocalizer().localize("label.create-backup"));
		doBackupFlag.setAlignmentX( Component.LEFT_ALIGNMENT );
		// doBackupFlag.setHorizontalTextPosition(SwingConstants.LEADING );
		doBackupFlag.setSelected( replaceForm.isDoBackup() );
 		doBackupFlag.addItemListener( new ItemListener() {
 			public void itemStateChanged(ItemEvent event) {
 				doBackup = ItemEvent.SELECTED == event.getStateChange();
 				backupFlagEvent = event;
 			}
 		});
		add( doBackupFlag );
		
		JTextField backupDirPathTextField = new JTextField();
		backupDirPathTextField.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, 
												swingConfig.getStandardComponentHight()) );
		backupDirPathTextField.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, 
												swingConfig.getStandardComponentHight()) );
		backupDirPathTextField.setText( backupDirectory.getPath() );
		backupDirPathTextField.setToolTipText( backupDirectory.getPath() );
		backupDirPathTextField.setEditable( false );
		JButton openBaseDirFileChooserButton = new JButton( swingConfig.getLocalizer().localize("button.browse") );
		BrowseButtonListener backupDirButtonListener = new BrowseButtonListener(backupDirPathTextField,
				new BackUpDirectoryRepository( swingConfig, repository.getFindForm(), replaceForm, messageBox ),
				swingConfig.getLocalizer().localize( "label.choose-backup-directory" ) );
		openBaseDirFileChooserButton.addActionListener( backupDirButtonListener );
		TwoComponentsPanel lineBaseDir = new TwoComponentsPanel( backupDirPathTextField, openBaseDirFileChooserButton);
		lineBaseDir.setAlignmentX( Component.LEFT_ALIGNMENT );
		add( lineBaseDir );
		
		add( Box.createVerticalGlue());

	}

	@Override
	public void save(SwingConfig swingConfig) {
		swingConfig.setGroupReferenceIndicator( groupReference );
		activeReplaceForm.setGroupReference( groupReference );
		renameForm.setGroupReference( groupReference );
		
		swingConfig.setProcessBinary( processBinaryFlag );
		swingConfig.setTreatUnknownFileTypesAsBinary(treatUnknwonAsBinaryFlag);
		
		activeReplaceForm.setDoBackup( doBackup );
		activeReplaceForm.setBackupDirectory(backupDirectory);

		for(int i = 0; i < fnpActionList.size(); i++ ) {
			FileNamePatternListAction action = fnpActionList.get(i);
			if( action.isDelete() ) {
				logger.debug("remove pattern" + action.getPattern());
				swingConfig.getFileNamePatternListModel().remove(action.getPattern());				
			} else {
				logger.debug("add pattern" + action.getPattern());
				swingConfig.getFileNamePatternListModel().addElement(action.getPattern());
			}
		}
		
		if( backupFlagEvent != null ) {
			backupFlagListener.itemStateChanged(backupFlagEvent);
		}
	}

	
	/**
	 * Tests if the new backup directory can be accepted.
	 */
	class BackUpDirectoryRepository extends AbstractFileRepository {
		public BackUpDirectoryRepository(FARConfig cfg, FindForm find, ReplaceForm replace, MessageBox errorsink) {
			super(cfg, find, replace, errorsink);
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
				backupDirectory = file;
				return true;
			} else {
				String message = farconfig.getLocalizer().localize("message.directory-not-writable", new Object[]{file.getPath()});
				messageBox.error( message );
				return false;
			}
		}
	}
}

/**
 * Editor component for the FileNamePattern combobox.
 * @author Olivier Wehner at 03.03.2008
 */
class FileNamePatternEditor extends JTextField implements ComboBoxEditor {
	private JButton saveButton;
	private JCheckBox patternFlag;
	private FileNamePattern lastUnsavedValue;
	public FileNamePatternEditor(JButton button, JCheckBox patternbox) {
		saveButton = button;
		patternFlag = patternbox;
		getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
			}
			public void removeUpdate(DocumentEvent e){
				if( e.getDocument().getLength() > 0) {
					saveButton.setEnabled( true );
					lastUnsavedValue = new FileNamePattern( getText( e.getDocument() ), patternFlag.isSelected() );
				}
			}
			public void insertUpdate(DocumentEvent e) {
				String text = getText( e.getDocument() );
				if( ! text.equals( lastUnsavedValue.getPattern() )) {
					saveButton.setEnabled( true );
					lastUnsavedValue = new FileNamePattern( text, patternFlag.isSelected() );
				}
			}
			private String getText(Document document) {
				try {
					return document.getText(0, document.getLength());
				} catch (Exception x) {
					LogFactory.getLog( this.getClass() ).error(x.getClass().getName() 
										+ " reading file name pattern: " + x.getMessage(), x);
					return "";
				}
			}
		}
		);
	}
	public Component getEditorComponent() {
		return this;
	}
	public Object getItem() {
		return lastUnsavedValue;
	}
	public void setItem(Object anObject) {
		FileNamePattern pattern = (FileNamePattern) anObject;
		if( ! pattern.equals( lastUnsavedValue ) ) saveButton.setEnabled( false );
		lastUnsavedValue = pattern;
		setText( pattern.getPattern() );
	}
}

/**
 * Listens to changes of the file name pattern.
 *
 * @author Olivier Wehner
 */
class SaveFileNamePatternListener implements ActionListener {
	private JCheckBox checkBox; 
	private JComboBox comboBox;
	private MessageBox messageBox;
	private Localizer localizer;
	private List<FileNamePatternListAction> actionList;
	public SaveFileNamePatternListener(JComboBox combo, 
										JCheckBox check,
										List<FileNamePatternListAction> actionList,
										MessageBox messageSink,
										Localizer localizer) {
		comboBox = combo;
		checkBox = check;
		this.actionList = actionList;
		messageBox = messageSink;
		this.localizer = localizer;
	}
	public void actionPerformed(ActionEvent event) {
		FileNamePattern pattern = (FileNamePattern) comboBox.getSelectedItem();
		pattern.setRegex( checkBox.isSelected() );
		if( SimpleFileNamePattern.getInstance().validateFileNamePattern(pattern) ) {
			// comboBox.getSelectedIndex() always < 0 !
			boolean found = false;
			int count = 0;
			do {
				FileNamePattern item = (FileNamePattern) comboBox.getItemAt( count );
				if( item.getPattern().equals( pattern.getPattern() )) {
					found = true;
					item.setRegex( pattern.isRegex() );
				}
				count++;
			} while ((! found) && (count < comboBox.getItemCount()));
			if( ! found ) {
				comboBox.addItem( pattern );
				actionList.add( FileNamePatternListAction.addAction(pattern) );
			} 
			((JButton) event.getSource() ).setEnabled( false );
		} else {
			messageBox.error(localizer.localize("message.invalid-file-name-pattern", 
												new String[]{pattern.getPattern()}));
		}
	}
}

class FileNamePatternListAction {
	private FileNamePattern pattern;
	private boolean add;
	private FileNamePatternListAction(boolean add, FileNamePattern fnp) {
		this.add = add;
		pattern = fnp;
	}
	public FileNamePattern getPattern() {
		return pattern;
	}
	public boolean isAdd() {
		return add;
	}
	public boolean isDelete() {
		return ! add;
	}
	public static FileNamePatternListAction addAction(FileNamePattern fnp) {
		return new FileNamePatternListAction(true, fnp);
	}
	public static FileNamePatternListAction deleteAction(FileNamePattern fnp) {
		return new FileNamePatternListAction(false, fnp);
	}
}

