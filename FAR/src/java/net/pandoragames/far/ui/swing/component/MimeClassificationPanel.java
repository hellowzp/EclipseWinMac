package net.pandoragames.far.ui.swing.component;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.pandoragames.far.ui.model.MimeType;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.dialog.config.ConfigurationDialog;
import net.pandoragames.util.i18n.Localizer;
/**
 * A Panel that allows to quickly select a MimeType.
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
public class MimeClassificationPanel extends JPanel {

	public enum OPTION {TEXT, BINARY, CHOICE}
	
	private ButtonGroup optionGroup;
	private JDialog dialog;
	private JComboBox mimeBox;
	private JRadioButton otherRadio;	
	private SwingConfig swingConfig;
	private ComponentRepository repository;


	/**
	 * Constructor.
	 * 
	 * @param dialog on which the panel is displayed
	 * @param introText explanatory text
	 * @param preselected selected option
	 * @param selectedMime mime type selected in drop down (may be null)
	 * @param config swing config
	 * @param componentRepository component repository
	 * @param allowCreateMime should the linkt to the mime configuration be displayed
	 */
	public MimeClassificationPanel(JDialog dialog, String introText, OPTION preselected, MimeType selectedMime, SwingConfig config, ComponentRepository componentRepository, boolean allowCreateMime) {
		this.dialog = dialog;
		swingConfig = config;
		repository = componentRepository;
		optionGroup = new ButtonGroup();
		init(introText, preselected, selectedMime, config, allowCreateMime);
	}

	private void init(String introText, OPTION preselected, MimeType selectedMime, SwingConfig config, boolean createMimeTypeOptionDisplayed) {
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		setBorder( BorderFactory.createEmptyBorder( SwingConfig.PADDING, 
				SwingConfig.PADDING, 
				SwingConfig.PADDING, 
				SwingConfig.PADDING ) );
		
		Localizer localizer = config.getLocalizer();
		
		JLabel intro = new JLabel(introText);
		intro.setAlignmentX( Component.LEFT_ALIGNMENT );
		add( intro );

		JRadioButton textRadio = new JRadioButton(localizer.localize("label.generic-mime-type-text"));
		textRadio.setAlignmentX( Component.LEFT_ALIGNMENT );
		textRadio.setActionCommand( OPTION.TEXT.name() );
		textRadio.setSelected( preselected == OPTION.TEXT );
		optionGroup.add(textRadio);
		add( textRadio );

		JRadioButton binaryRadio = new JRadioButton(localizer.localize("label.generic-mime-type-binary"));
		binaryRadio.setAlignmentX( Component.LEFT_ALIGNMENT );
		binaryRadio.setActionCommand( OPTION.BINARY.name() );
		binaryRadio.setSelected( preselected == OPTION.BINARY );
		optionGroup.add(binaryRadio);
		add( binaryRadio );

		otherRadio = new JRadioButton(localizer.localize("label.other-mime-type"));
		otherRadio.setAlignmentX( Component.LEFT_ALIGNMENT );
		otherRadio.setActionCommand( OPTION.CHOICE.name() );
		otherRadio.setSelected( preselected == OPTION.CHOICE );
		optionGroup.add(otherRadio);
		add( otherRadio );

		mimeBox = new JComboBox( MimeType.MimeRegistry.listAll().toArray() );
		mimeBox.setAlignmentX(Component.LEFT_ALIGNMENT );
		MimeType defaultType = selectedMime != null ? selectedMime : (MimeType) (preselected == OPTION.BINARY ?  MimeType.getType("application/octet-stream" ): MimeType.getType("text/plain" ));
		mimeBox.setSelectedItem( defaultType );
		mimeBox.addItemListener( new ItemListener() {
			public void itemStateChanged(ItemEvent eve) {
				if( eve.getStateChange() == ItemEvent.SELECTED ) {
					otherRadio.setSelected( true );
				}
			}
		});
		add( mimeBox );
		
		if( createMimeTypeOptionDisplayed ) {
		
			add( Box.createVerticalStrut( SwingConfig.PADDING ) );
			
			JLabel neitherlabel = new JLabel( localizer.localize("label.none-of-these"));
			neitherlabel.setAlignmentX( Component.LEFT_ALIGNMENT );
			add( neitherlabel );
			
			JButton openConfigButton = new JButton(localizer.localize("button.create-new-mime-type"));
			openConfigButton.setAlignmentX( Component.LEFT_ALIGNMENT );
			openConfigButton.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent eve) {
					ConfigurationDialog settings = new ConfigurationDialog((JFrame) dialog.getOwner(), swingConfig, repository, ConfigurationDialog.CONFIG_TAB.MIME);
					settings.pack();
					dialog.dispose();
					settings.setVisible( true );		
				}
			});
			add( openConfigButton );
		}

	}
	
	public MimeType getSelectedMimeType() {
		String selectedOption = optionGroup.getSelection().getActionCommand();
		OPTION option = OPTION.valueOf( selectedOption );
		switch( option ) {
			case TEXT : return (MimeType)  MimeType.getType( "text/plain" );
			case BINARY : return (MimeType)  MimeType.getType( "application/octet-stream" );
			case CHOICE : return (MimeType) mimeBox.getSelectedItem();
			default : throw new IllegalArgumentException( selectedOption + " is not a registered action");
		}
	}
}
