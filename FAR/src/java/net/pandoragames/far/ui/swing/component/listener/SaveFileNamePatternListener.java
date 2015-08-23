package net.pandoragames.far.ui.swing.component.listener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.pandoragames.far.ui.SimpleFileNamePattern;
import net.pandoragames.far.ui.model.FileNamePattern;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.MimeType;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.MimeClassificationPanel;
import net.pandoragames.far.ui.swing.dialog.SubWindow;
import net.pandoragames.far.ui.swing.dialog.config.ConfigurationDialog;
import net.pandoragames.util.i18n.Localizer;

/**
 * Listens to changes of the file name pattern.
 *
 * @author Olivier Wehner
 */
public class SaveFileNamePatternListener implements ActionListener {
	
	/**
	 * Regex pattern used to identify a list of file name extensions.
	 */
	public static final String FNP_EXTENSIONLIST_PATTERN = "\\s*\\*\\.\\w+(\\s*,\\s*\\*\\.\\w+)*\\s*";
	
	private JCheckBox checkBox; 
	private JComboBox comboBox;
	private Localizer localizer;
	private MessageBox messageBox;
	private SwingConfig config;  
	private ComponentRepository repository;
	public SaveFileNamePatternListener(JComboBox combo, 
										JCheckBox check, 
										SwingConfig config,  
										ComponentRepository componentRepository) {
		comboBox = combo;
		checkBox = check;
		localizer = config.getLocalizer();
		messageBox = componentRepository.getMessageBox();
		this.config = config;
		repository = componentRepository;
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
				askForMimeType(pattern, config, repository);
			} 
			((JButton) event.getSource() ).setEnabled( false );
		} else {
			messageBox.error(localizer.localize("message.invalid-file-name-pattern", 
												new String[]{pattern.getPattern()}));
		}
	}
	
	private void askForMimeType(FileNamePattern pattern, SwingConfig config,  ComponentRepository componentRepository) {
		if( pattern.isRegex() ) return;
		if( pattern.getPattern().matches(FNP_EXTENSIONLIST_PATTERN)) {
			String[] rawList = pattern.getPattern().toLowerCase().split(",");
			StringBuilder buffer = new StringBuilder();
			for(String ext : rawList) {
				if( MimeType.MimeRegistry.findMimeType(ext.trim().substring(1)) == null ) {
					if( buffer.length() > 0 ) buffer.append(",");
					buffer.append( ext.trim() );
				}
			}
			if( buffer.length() == 0 ) return;
			AskForMimeDialog mimeDialog = new AskForMimeDialog(buffer.toString(), config, componentRepository);
			mimeDialog.pack();
			mimeDialog.setVisible(true);
		}
	}
}

class AskForMimeDialog extends SubWindow {
	private MimeClassificationPanel centerPanel;
	private String extensions;
	private SwingConfig config;  
	private ComponentRepository repository;

	public AskForMimeDialog(String pattern, SwingConfig swingConfig,  ComponentRepository componentRepository)
	{
		super(componentRepository.getRootWindow(), swingConfig.getLocalizer().localize("label.mime-type"));
		
		extensions = pattern;
		config = swingConfig;
		repository = componentRepository;
		
		Localizer localizer = config.getLocalizer();
		
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout( new BorderLayout() );

		centerPanel = new MimeClassificationPanel(this, localizer.localize("message.set-mime-type-of-extension", pattern.trim()), 
													MimeClassificationPanel.OPTION.TEXT, null, config, componentRepository, false);
		getContentPane().add( centerPanel, BorderLayout.CENTER );
		
		JPanel buttonPanel = new JPanel( new FlowLayout(FlowLayout.RIGHT) );
 		JButton applyButton = new JButton( localizer.localize("button.apply") );
 		applyButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MimeType mimeType = centerPanel.getSelectedMimeType();
				String[] extlist = extensions.split(",");
				for(String ext : extlist) {
					mimeType.addExtension( ext.trim().substring(1) );
				}
				AskForMimeDialog.this.dispose();
			}						
		});
 		buttonPanel.add( applyButton );
		JButton openConfigButton = new JButton(localizer.localize("button.create-new-mime-type"));
		openConfigButton.setAlignmentX( Component.LEFT_ALIGNMENT );
		openConfigButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				ConfigurationDialog settings = new ConfigurationDialog((JFrame) AskForMimeDialog.this.getOwner(), config, repository, ConfigurationDialog.CONFIG_TAB.MIME);
				settings.pack();
				AskForMimeDialog.this.dispose();
				settings.setVisible( true );		
			}
		});
		buttonPanel.add( openConfigButton );
 		JButton cancelButton = new JButton( localizer.localize("button.cancel") );
 		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				AskForMimeDialog.this.dispose();
			}						
		});
 		buttonPanel.add( cancelButton );
		getContentPane().add( buttonPanel, BorderLayout.SOUTH );
		
		placeOnScreen( config.getScreenCenter() );
	}
}
