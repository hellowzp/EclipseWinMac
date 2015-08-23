package net.pandoragames.far.ui.swing.dialog.config;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.SwingConfig.FILEVIEW;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.far.ui.swing.component.ListItem;
import net.pandoragames.far.ui.swing.component.TwoComponentsPanel;
import net.pandoragames.util.j6.DesktopHelper;
/**
 * Configuration settings for the file list.
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
public class FileListConfigPanel extends ConfigPanel {

	private ButtonGroup fileInfoOptions;
	private boolean showBytes;
	private Vector<ListItem<SwingConfig.FILEVIEW>> fileViewerList;
	private JComboBox textViewerList;
	private JComboBox binaryViewerList;
	
	public FileListConfigPanel(SwingConfig swingConfig, ComponentRepository repository, MessageBox messageBox) {
		super(messageBox, repository, swingConfig);
	}
	
	@Override
	protected void init(ComponentRepository repositoryz, SwingConfig swingConfig) {
		
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

		fileViewerList = new Vector<ListItem<SwingConfig.FILEVIEW>>();
		fileViewerList.add( new ListItem<FILEVIEW>( localizer.localize("label.view." + SwingConfig.FILEVIEW.EDITOR ), SwingConfig.FILEVIEW.EDITOR ));
		fileViewerList.add( new ListItem<FILEVIEW>( localizer.localize("label.view." + SwingConfig.FILEVIEW.VIEW ), SwingConfig.FILEVIEW.VIEW ));
		fileViewerList.add( new ListItem<FILEVIEW>( localizer.localize("label.view." + SwingConfig.FILEVIEW.INFO ), SwingConfig.FILEVIEW.INFO ));
		if(SwingConfig.getEffectiveJavaVersion() > 5 && DesktopHelper.isFileOpeningSupported()) {
			fileViewerList.add( new ListItem<FILEVIEW>( localizer.localize("label.view." + SwingConfig.FILEVIEW.EXTERNAL ), SwingConfig.FILEVIEW.EXTERNAL ));
		}
		fileViewerList.add( new ListItem<FILEVIEW>( localizer.localize("label.view." + SwingConfig.FILEVIEW.MENU ), SwingConfig.FILEVIEW.MENU ));
		fileViewerList.add( new ListItem<FILEVIEW>( localizer.localize("label.view." + SwingConfig.FILEVIEW.NONE ), SwingConfig.FILEVIEW.NONE ));
		
		add( Box.createVerticalStrut( SwingConfig.PADDING ));
		
		JLabel textviewLabel = new JLabel( localizer.localize("label.fileviewopener-text" ) );
		textviewLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
		add( textviewLabel );
		textViewerList = new JComboBox( fileViewerList );
		textViewerList.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, swingConfig.getStandardComponentHight()) );
		textViewerList.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, swingConfig.getStandardComponentHight()) );
		textViewerList.setSelectedItem( new ListItem<FILEVIEW>("", swingConfig.getTextView()) );
		textViewerList.setAlignmentX( Component.LEFT_ALIGNMENT );
		add( textViewerList );
		
		add( Box.createVerticalStrut( SwingConfig.PADDING ));
		
		JLabel binaryviewLabel = new JLabel( localizer.localize("label.fileviewopener-binary" ) );
		binaryviewLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
		add( binaryviewLabel );
		binaryViewerList = new JComboBox( fileViewerList );
		binaryViewerList.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, swingConfig.getStandardComponentHight()) );
		binaryViewerList.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, swingConfig.getStandardComponentHight()) );
		binaryViewerList.setAlignmentX( Component.LEFT_ALIGNMENT );
		binaryViewerList.setSelectedItem( new ListItem<FILEVIEW>("", swingConfig.getBinaryView()) );
		add( binaryViewerList );
		
		add( Box.createVerticalStrut( SwingConfig.PADDING * 2 ));
		
		// file info
		fileInfoOptions = new ButtonGroup();
		JPanel fileInfoPanel = new JPanel();
		fileInfoPanel.setBorder( BorderFactory.createTitledBorder(
								BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ),
								swingConfig.getLocalizer().localize("message.displayed-in-info-column")));
		fileInfoPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		fileInfoPanel.setLayout( new BoxLayout( fileInfoPanel, BoxLayout.Y_AXIS ) );
		fileInfoPanel.add( Box.createHorizontalGlue() );
		
		JPanel fileInfoPanelLeft = new JPanel();
		fileInfoPanelLeft.setLayout( new BoxLayout( fileInfoPanelLeft, BoxLayout.Y_AXIS ) );
		fileInfoPanelLeft.add( Box.createVerticalStrut( SwingConfig.PADDING ));
		JRadioButton nothingRadio = new JRadioButton(swingConfig.getLocalizer().localize("label.nothing"));
		nothingRadio.setAlignmentX( Component.LEFT_ALIGNMENT );
		nothingRadio.setActionCommand( SwingConfig.DefaultFileInfo.NOTHING.name() );
		nothingRadio.setSelected( swingConfig.getDefaultFileInfo() == SwingConfig.DefaultFileInfo.NOTHING );
		fileInfoOptions.add(nothingRadio);
		fileInfoPanelLeft.add(nothingRadio);
		JRadioButton readOnlyRadio = new JRadioButton(swingConfig.getLocalizer().localize("label.read-only-warning"));
		readOnlyRadio.setAlignmentX( Component.LEFT_ALIGNMENT );
		readOnlyRadio.setActionCommand( SwingConfig.DefaultFileInfo.READONLY.name() );
		readOnlyRadio.setSelected( swingConfig.getDefaultFileInfo() == SwingConfig.DefaultFileInfo.READONLY );
		fileInfoOptions.add(readOnlyRadio);
		fileInfoPanelLeft.add(readOnlyRadio);
		JRadioButton sizeRadio = new JRadioButton(swingConfig.getLocalizer().localize("label.filesize"));
		sizeRadio.setAlignmentX( Component.LEFT_ALIGNMENT );
		sizeRadio.setActionCommand( SwingConfig.DefaultFileInfo.SIZE.name() );
		sizeRadio.setSelected( swingConfig.getDefaultFileInfo() == SwingConfig.DefaultFileInfo.SIZE );
		fileInfoOptions.add(sizeRadio);
		fileInfoPanelLeft.add(sizeRadio);
		JCheckBox showPlainBytesFlag = new JCheckBox( "    " + swingConfig.getLocalizer().localize("label.show-plain-bytes"));
		showPlainBytesFlag.setAlignmentX( Component.LEFT_ALIGNMENT );
		showPlainBytesFlag.setHorizontalTextPosition(SwingConstants.LEADING );
		showPlainBytesFlag.setSelected( swingConfig.isShowPlainBytes() );
		showPlainBytesFlag.addItemListener( new ItemListener() {
 			public void itemStateChanged(ItemEvent event) {
 				showBytes = ItemEvent.SELECTED == event.getStateChange();
 			}
 		});
		fileInfoPanelLeft.add(showPlainBytesFlag);
		fileInfoPanelLeft.add(Box.createVerticalGlue());
		
		JPanel fileInfoPanelRight = new JPanel();
		fileInfoPanelRight.setLayout( new BoxLayout( fileInfoPanelRight, BoxLayout.Y_AXIS ) );
		fileInfoPanelRight.add( Box.createVerticalStrut( SwingConfig.PADDING ));
		JRadioButton lastModifiedRadio = new JRadioButton(swingConfig.getLocalizer().localize("label.last-modified"));
		lastModifiedRadio.setAlignmentX( Component.LEFT_ALIGNMENT );
		lastModifiedRadio.setActionCommand( SwingConfig.DefaultFileInfo.LAST_MODIFIED.name() );
		lastModifiedRadio.setSelected( swingConfig.getDefaultFileInfo() == SwingConfig.DefaultFileInfo.LAST_MODIFIED );
		fileInfoOptions.add(lastModifiedRadio);
		fileInfoPanelRight.add(lastModifiedRadio);
		JRadioButton mimeRadio = new JRadioButton(swingConfig.getLocalizer().localize("label.mime-type"));
		mimeRadio.setAlignmentX( Component.LEFT_ALIGNMENT );
		mimeRadio.setActionCommand( SwingConfig.DefaultFileInfo.MIMETYPE.name() );
		mimeRadio.setSelected( swingConfig.getDefaultFileInfo() == SwingConfig.DefaultFileInfo.MIMETYPE );
		fileInfoOptions.add(mimeRadio);
		fileInfoPanelRight.add(mimeRadio);
		JRadioButton charsetRadio = new JRadioButton(swingConfig.getLocalizer().localize("label.charset"));
		charsetRadio.setAlignmentX( Component.LEFT_ALIGNMENT );
		charsetRadio.setActionCommand( SwingConfig.DefaultFileInfo.CHARSET.name() );
		charsetRadio.setSelected( swingConfig.getDefaultFileInfo() == SwingConfig.DefaultFileInfo.CHARSET );
		fileInfoOptions.add(charsetRadio);
		fileInfoPanelRight.add(charsetRadio);
		fileInfoPanelRight.add(Box.createVerticalGlue());
		
		TwoComponentsPanel splitPanel = new TwoComponentsPanel(fileInfoPanelLeft,fileInfoPanelRight);
		splitPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		fileInfoPanel.add( splitPanel );
		add( fileInfoPanel );
		add( Box.createVerticalGlue() );
	}

	@Override
	public void save(SwingConfig swingConfig) {
		swingConfig.setDefaultFileInfo( fileInfoOptions.getSelection().getActionCommand() );
		swingConfig.setShowPlainBytes(showBytes);
		swingConfig.setBinaryView( ((ListItem<FILEVIEW>) binaryViewerList.getSelectedItem()).getValue() );
		swingConfig.setTextView( ((ListItem<FILEVIEW>) textViewerList.getSelectedItem()).getValue() );
	}

}
