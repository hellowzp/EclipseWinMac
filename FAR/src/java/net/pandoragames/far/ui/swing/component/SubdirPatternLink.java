package net.pandoragames.far.ui.swing.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import net.pandoragames.far.ui.model.FileNamePattern;
import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.Resetable;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.listener.SearchBaseHasChangedListener;
import net.pandoragames.far.ui.swing.dialog.SubWindow;
import net.pandoragames.util.i18n.Localizer;

/**
 * Button with dialog for the definition of a directory pattern.
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
public class SubdirPatternLink extends LinkButton implements Resetable {
	private FindForm dataModel;
	private Localizer localizer;
	private SearchBaseHasChangedListener searchBaseListener;

	public SubdirPatternLink(FindForm data, Localizer loco, SearchBaseHasChangedListener sbListener, final JFrame baseWindow) {
		super( getSubdirPatternButtonLabel(data, loco) );
		dataModel = data;
		localizer = loco;
		searchBaseListener = sbListener;
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				SubdirDialog dialog = new SubdirDialog(getLocationOnScreen(), baseWindow);
				dialog.pack();
				dialog.setVisible(true);
			}
		});
	}
	
	public void reset() {
		dataModel.setDirectoryPattern(null);
		dataModel.setExcludeMatchingDirectories(false);
		setText( getSubdirPatternButtonLabel(dataModel, localizer) );
	}
	
	public void update(FindForm data) {
		dataModel = data;
		setText( getSubdirPatternButtonLabel(dataModel, localizer) );
	}
	
	private static String getSubdirPatternButtonLabel(FindForm dataModel, Localizer localizer) {
		if( dataModel.getDirectoryPattern() == null ) {
			return localizer.localize("button.all-subdirectories");
		} else if ( !dataModel.isExcludeMatchingDirectories() && dataModel.getDirectoryPattern().findsAny()) {
			return localizer.localize("button.all-subdirectories");
		} else if( dataModel.isExcludeMatchingDirectories() ){
			return localizer.localize("label.exclude-x", dataModel.getDirectoryPattern().getPattern());
		} else {
			return dataModel.getDirectoryPattern().getPattern();
		}
	}

	class SubdirDialog extends SubWindow {
		private MessageLabel errorText;
		private JTextField patternField;
		private JCheckBox patternFlag;
		private JCheckBox excludeFlag;
//		private AbstractButton button;
				
		public SubdirDialog(Point location, JFrame baseWindow) {
			super( baseWindow, localizer.localize("label.directory-pattern"), true);
						
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setLayout( new BorderLayout() );
			
			JPanel basePanel = new JPanel();
			basePanel.setLayout( new BoxLayout( basePanel, BoxLayout.Y_AXIS ) );
			basePanel.setBorder( BorderFactory.createEmptyBorder( 0, 
					SwingConfig.PADDING, 
					SwingConfig.PADDING, 
					SwingConfig.PADDING ) );
			registerCloseWindowKeyListener( basePanel );
			
			// some headspace
			basePanel.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
			
			// error text
			errorText = new MessageLabel();
			TwoComponentsPanel errorPanel = new TwoComponentsPanel(errorText, new JLabel(" "));
			errorPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
			basePanel.add(errorPanel);
			
			patternField = new JTextField( dataModel.getDirectoryPattern() != null ? dataModel.getDirectoryPattern().getPattern() : "");
			patternField.setAlignmentX( Component.LEFT_ALIGNMENT );
			basePanel.add(patternField);
			
			patternFlag = new JCheckBox( localizer.localize("label.regular-expression"));
			patternFlag.setAlignmentX( Component.LEFT_ALIGNMENT );
			patternFlag.setSelected( dataModel.getDirectoryPattern() != null && dataModel.getDirectoryPattern().isRegex() );
			basePanel.add(patternFlag);
			
			excludeFlag = new JCheckBox( localizer.localize("label.exclude-matching-directories"));
			excludeFlag.setAlignmentX( Component.LEFT_ALIGNMENT );
			excludeFlag.setSelected( dataModel.isExcludeMatchingDirectories() );
			basePanel.add(excludeFlag);
	
			getContentPane().add( basePanel, BorderLayout.CENTER );
			
	 		// close button
	 		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
	 		JButton saveButton = new JButton( localizer.localize("button.save") );
	 		saveButton.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent event) {
						String trimmedPattern = patternField.getText().trim();
						boolean sbChange = false;
						if( trimmedPattern.length() == 0) {
							sbChange = dataModel.getDirectoryPattern() != null;
							dataModel.setDirectoryPattern( null );
						} else {
							FileNamePattern pattern = new FileNamePattern(trimmedPattern, patternFlag.isSelected() );
							if( ! pattern.isValid() ) {
								errorText.error( localizer.localize("message.invalid-expression", trimmedPattern));
								return;
							} else if( pattern.findsAny() && ! excludeFlag.isSelected()) {
								sbChange = dataModel.getDirectoryPattern() != null;
								dataModel.setDirectoryPattern( null );
							} else {
								sbChange = (dataModel.getDirectoryPattern() == null || ! dataModel.getDirectoryPattern().equals(pattern));
								dataModel.setDirectoryPattern(pattern);
							}
						}
						sbChange = sbChange || (excludeFlag.isSelected() != dataModel.isExcludeMatchingDirectories());
						dataModel.setExcludeMatchingDirectories( excludeFlag.isSelected() );
						setText( getSubdirPatternButtonLabel(dataModel, localizer) );
						if(sbChange) searchBaseListener.actionPerformed(event);
						SubdirDialog.this.dispose();
					}
			});
	 		buttonPanel.add( saveButton );
	 		JButton closeButton = new JButton( localizer.localize("button.cancel") );
	 		closeButton.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					SubdirDialog.this.dispose();
				}						
			});
	 		buttonPanel.add( closeButton );
	 		getContentPane().add( buttonPanel, BorderLayout.SOUTH );
	
	 		placeOnScreen( location );
		}
	}
}
