package net.pandoragames.far.ui.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import net.pandoragames.far.ui.SimpleFileNamePattern;
import net.pandoragames.far.ui.model.FileNamePattern;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.far.ui.swing.component.TwoComponentsPanel;
import net.pandoragames.far.ui.swing.component.listener.AbstractFileOperationListener;
import net.pandoragames.util.i18n.Localizer;

public class SubSelectByNameDialog extends SubWindow {

	private Localizer localizer;
	private FileNamePattern fileNamePattern = new FileNamePattern("");
	private JCheckBox inversionFlag;
	private JTextField textbox;
	private Action filterAction = null;
	private ButtonGroup containOrMatch = new ButtonGroup();
	private boolean match = false;
	
	
	public SubSelectByNameDialog(JFrame owner, FileSetTableModel model, SwingConfig config) {
		super(owner, config.getLocalizer().localize("label.select-subset"), true);
		localizer = config.getLocalizer();
		filterAction = new OkButtonListener(model, localizer.localize("button.filter"));
		init(config);
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
				
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout( new BoxLayout(centerPanel, BoxLayout.Y_AXIS) );
		JLabel titleLabel = new JLabel(localizer.localize("label.file-name-pattern"));
		titleLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
		centerPanel.add( titleLabel );
		JRadioButton containOption = new JRadioButton(localizer.localize("label.containing"), true);
		containOption.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) { match = false; }
		});		
		containOrMatch.add(containOption);
		JRadioButton matchOption = new JRadioButton(localizer.localize("label.matching"), false);
		matchOption.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) { match = true; }
		});
		containOrMatch.add(matchOption);
		TwoComponentsPanel matchOrContainPanel = new TwoComponentsPanel(containOption, matchOption);
		matchOrContainPanel.setAlignmentX( 0 );
		centerPanel.add( matchOrContainPanel );
		textbox = new JTextField();
		textbox.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, config.getStandardComponentHight()) );
		textbox.setAlignmentX(0);
		textbox.getDocument().addDocumentListener( new TextFieldListener() );
		// textbox.requestFocusInWindow();
		registerEnterKeyListener(textbox, filterAction);
		centerPanel.add(textbox);
		JCheckBox patternFlag = new JCheckBox( localizer.localize("label.regular-expression"));
		patternFlag.setAlignmentX( Component.LEFT_ALIGNMENT );
		patternFlag.setSelected( false );
		patternFlag.addItemListener( new ItemListener() {	
				public void itemStateChanged(ItemEvent event) {
					fileNamePattern.setRegex( (ItemEvent.SELECTED == event.getStateChange()) );
					checkOkButtonEnabled();
				}
			});
		centerPanel.add(patternFlag);
		inversionFlag = new JCheckBox( localizer.localize("label.exclude-matches"));
		inversionFlag.setAlignmentX( Component.LEFT_ALIGNMENT );
		inversionFlag.setSelected( false );
		centerPanel.add(inversionFlag);
		basePanel.add(centerPanel, BorderLayout.CENTER);
		
		JPanel buttonPannel = new JPanel();
		buttonPannel.setLayout( new FlowLayout(FlowLayout.RIGHT) );
		JButton okButton = new JButton( filterAction );
		okButton.setEnabled( false );
		JButton cancelButton = new JButton( localizer.localize("button.cancel") );
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent eve){
				SubSelectByNameDialog.this.dispose();
			}
		});
		buttonPannel.add( okButton );
		buttonPannel.add( cancelButton );
		registerCloseWindowKeyListener( buttonPannel );
		this.add(buttonPannel, BorderLayout.SOUTH);	
		
		this.setFocusTraversalPolicy( new ContainerOrderFocusTraversalPolicy() {
			public Component getInitialComponent(Window window) {
				return textbox;
			}
		});
	}

	
	private void checkOkButtonEnabled() {
		boolean enabled = fileNamePattern.getPattern().length() > 0 && fileNamePattern.isValid();
		filterAction.setEnabled( enabled );
	}
	
	class TextFieldListener implements DocumentListener {
		public void changedUpdate(DocumentEvent e) {
		}
		public void insertUpdate(DocumentEvent e) {
			fileNamePattern.setPattern( getText(e.getDocument()) );
			checkOkButtonEnabled();
		}
		public void removeUpdate(DocumentEvent e) {
			fileNamePattern.setPattern( getText(e.getDocument()) );
			checkOkButtonEnabled();
		}
		private String getText(Document doc) {
			try{
				return doc.getText(0, doc.getLength());
			} catch(BadLocationException bld) {
				return "";
			}
		}
	}
	
	class OkButtonListener extends AbstractFileOperationListener {
		private Pattern pattern;
		public OkButtonListener(FileSetTableModel tableModel, String label) {
			super(tableModel, label);
			setEnabled(false);
		}
		public void actionPerformed(ActionEvent eve){
			if( ! isEnabled() ) return;
			if( ! match ) {
				String raw = fileNamePattern.getPattern();
				if( fileNamePattern.isRegex() ) {
					if( ! raw.startsWith(".*")) raw = ".*" + raw;
					if( ! raw.endsWith(".*")) raw = raw + ".*";					
				} else {
					if( raw.charAt(0) != '*') raw = "*" + raw;
					if( ! raw.endsWith("*")) raw = raw + "*";
				}
				fileNamePattern.setPattern( raw );
			}
			pattern = fileNamePattern.toRegexPattern();
			super.actionPerformed(eve);
			SubSelectByNameDialog.this.dispose();
		}
		public void execute(TargetFile file) {
			boolean match = pattern.matcher( file.getName() ).matches();
			if( inversionFlag.isSelected() ) match = ! match;
			file.setSelected( match );
		}
	}
}
