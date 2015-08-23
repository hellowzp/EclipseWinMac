package net.pandoragames.far.ui.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.pandoragames.far.ui.FARConfig;
import net.pandoragames.far.ui.FilePropertiesFormater;
import net.pandoragames.far.ui.model.CharacterUtil;
import net.pandoragames.far.ui.model.LineSeparator;
import net.pandoragames.far.ui.model.MimeType;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.LinkButton;
import net.pandoragames.far.ui.swing.component.MimeClassificationPanel;
import net.pandoragames.far.ui.swing.component.TwoComponentsPanel;
import net.pandoragames.far.ui.swing.dialog.config.ConfigurationDialog;
import net.pandoragames.util.i18n.Localizer;

/**
 * Displays file information like size and line count.
 *
 * @author olive
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
public class InfoView extends SubWindow {
	
	private static FilePropertiesFormater formater = new FilePropertiesFormater();

	private TargetFile targetFile;
	private boolean isBinary;
	private JPanel basePanel;
	private JPanel buttonPanel;
	private Localizer localizer;
	private LinkButton mimeField;
	private SwingConfig swingConfig;
	private ComponentRepository repository;

	public InfoView(JFrame owner, TargetFile file, SwingConfig config, ComponentRepository repository) {
		super(owner, file.getName());
		targetFile = file;
		isBinary = file.isBinaryFile();
		localizer = config.getLocalizer();
		swingConfig = config;
		this.repository = repository;
		init( config );
	}
	
	private void init(SwingConfig config) {
		
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout( new BorderLayout() );
		
		basePanel = new JPanel();
		basePanel.setLayout( new BoxLayout( basePanel, BoxLayout.Y_AXIS ) );
		basePanel.setBorder( BorderFactory.createEmptyBorder( SwingConfig.PADDING, 
				SwingConfig.PADDING, 
				SwingConfig.PADDING, 
				SwingConfig.PADDING ) );
		registerCloseWindowKeyListener( basePanel );
		
		// some headspace
		basePanel.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
		
		// file name
		JLabel labelFileName = new JLabel( config.getLocalizer().localize("label.filename"));
		JTextField nameField = new JTextField( targetFile.getName() );
		nameField.setEditable( false );
		nameField.setBorder( BorderFactory.createEmptyBorder() );
		JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		namePanel.add( nameField );
		TwoComponentsPanel nameLine = new TwoComponentsPanel(labelFileName, namePanel);
		nameLine.setAlignmentX( Component.LEFT_ALIGNMENT );
		basePanel.add( nameLine );
		
		basePanel.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));

		// file size
		JLabel labelFileSize = new JLabel( config.getLocalizer().localize("label.filesize"));
		JTextField sizeField = new JTextField( formater.formatBytes( targetFile.getFile().length(), config.getLocalizer()) );
		sizeField.setEditable( false );
		sizeField.setBorder( BorderFactory.createEmptyBorder() );
		JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		sizePanel.add( sizeField );
		TwoComponentsPanel sizeLine = new TwoComponentsPanel(labelFileSize, sizePanel);
		sizeLine.setAlignmentX( Component.LEFT_ALIGNMENT );
		basePanel.add( sizeLine );

		basePanel.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));

		// line count and line separator calculated through LineAnalyzer below
		
		// line count
		JLabel labelLineCount = new JLabel( config.getLocalizer().localize("label.lineCount"));
		JTextField lineField = new JTextField( isBinary ? "-" : localizer.localize("message.counting"));
		lineField.setEditable( false );
		lineField.setBorder( BorderFactory.createEmptyBorder() );
		JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		linePanel.add( lineField );
		TwoComponentsPanel linesLine = new TwoComponentsPanel(labelLineCount, linePanel);
		linesLine.setAlignmentX( Component.LEFT_ALIGNMENT );
		basePanel.add( linesLine );

		basePanel.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));

		// line separator
		JLabel labelSeparator = new JLabel( config.getLocalizer().localize("label.line-separator"));
		JTextField sepField = new JTextField( "-" );
		sepField.setEditable( false );
		sepField.setBorder( BorderFactory.createEmptyBorder() );
		JPanel sepPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		sepPanel.add( sepField );
		TwoComponentsPanel sepLine = new TwoComponentsPanel(labelSeparator, sepPanel);
		sepLine.setAlignmentX( Component.LEFT_ALIGNMENT );
		basePanel.add( sepLine );

		if( ! isBinary ) {
			Thread lineAnalyzer = new Thread( new LineAnalyzer(lineField, sepField));
			lineAnalyzer.start();
		}
		
		basePanel.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));

		// last modified
		JLabel labelLastModified = new JLabel( config.getLocalizer().localize("label.last-modified"));
		JTextField modifiedField = new JTextField( formater.timeStamp( targetFile.getFile().lastModified() ) );
		modifiedField.setEditable( false );
		modifiedField.setBorder( BorderFactory.createEmptyBorder() );
		JPanel modPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		modPanel.add( modifiedField );
		TwoComponentsPanel modifiedLine = new TwoComponentsPanel(labelLastModified, modPanel);
		modifiedLine.setAlignmentX( Component.LEFT_ALIGNMENT );
		basePanel.add( modifiedLine );
		
		// Mime Type
		JLabel labelMimeType = new JLabel( config.getLocalizer().localize("label.mime-type"));
		MimeType mime = targetFile.getMimeType();
//		JTextField mimeField = new JTextField( mime != null ? mime.getName() : config.getLocalizer().localize("label.unknown") );
//		mimeField.setEditable( false );
//		mimeField.setBorder( BorderFactory.createEmptyBorder() );
		mimeField = new LinkButton( mime != null ? mime.getName() : config.getLocalizer().localize("label.unknown") , false, false);
		mimeField.addActionListener( new ModifyMimeType() );
		JPanel mimePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		mimePanel.add( mimeField );
		TwoComponentsPanel mimeLine = new TwoComponentsPanel(labelMimeType, mimePanel);
		mimeLine.setAlignmentX( Component.LEFT_ALIGNMENT );
		basePanel.add( mimeLine );
		
		// encoding
		JLabel labelCharset = new JLabel( config.getLocalizer().localize("label.charset"));
		Charset charset = targetFile.getCharacterset();
		JComboBox  charsetList = new JComboBox( CharacterUtil.getCharsetList(config.getCharsetList(),charset) );
		charsetList.setSelectedItem( charset );
 		charsetList.addItemListener( new ItemListener()  {
 			public void itemStateChanged(ItemEvent event) {
 				Charset charset = (Charset) event.getItem();
 				logger.debug("Character set changed to " + charset.name());
 				targetFile.setCharacterset( charset );
 			}
 		} );
 		JPanel boxpanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
 		boxpanel.add( charsetList );
 		TwoComponentsPanel charsetLine = new TwoComponentsPanel(labelCharset, boxpanel);
 		charsetLine.setAlignmentX( Component.LEFT_ALIGNMENT );
 		basePanel.add( charsetLine );
 
 		// full path
 		JLabel labelFilePath = new JLabel( config.getLocalizer().localize("label.path"));
 		labelFilePath.setAlignmentX( Component.LEFT_ALIGNMENT );
 		basePanel.add( labelFilePath );
 		JTextArea pathField = new JTextArea( targetFile.getFile().getPath() );
 		pathField.setLineWrap( true );
 		pathField.setEditable( false );
 		pathField.setRows(2);
 		pathField.setBorder( BorderFactory.createEmptyBorder() );
 		JScrollPane contentScrollPane = new JScrollPane( pathField );
 		contentScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
 		contentScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		contentScrollPane.setAlignmentX( Component.LEFT_ALIGNMENT );
		basePanel.add( contentScrollPane );
		
		// READ ONLY Warning
		if( ! targetFile.getFile().canWrite() ) {
			basePanel.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
			
			JLabel roWarning = new JLabel( config.getLocalizer().localize("label.read-only") + "!");
			roWarning.setAlignmentX( Component.LEFT_ALIGNMENT );
	 		basePanel.add( roWarning );
		}
		
		// Error messages
		if( targetFile.getErrorMessage() != null ) {
			basePanel.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
			
	 		JTextArea errorField = new JTextArea( targetFile.getErrorMessage() );
	 		errorField.setForeground( Color.RED );
	 		errorField.setLineWrap( true );
	 		errorField.setEditable( false );
	 		errorField.setRows(3);
	 		errorField.setBorder( BorderFactory.createEmptyBorder() );
	 		JScrollPane errorScrollPane = new JScrollPane( errorField );
	 		errorScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
	 		errorScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
	 		errorScrollPane.setAlignmentX( Component.LEFT_ALIGNMENT );
			basePanel.add( errorScrollPane );
		}
 		
 		getContentPane().add( basePanel, BorderLayout.CENTER );
 		
 		// close button
 		buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
 		JButton closeButton = new JButton( config.getLocalizer().localize("button.close") );
 		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				InfoView.this.dispose();
			}						
		});
 		buttonPanel.add( closeButton );
 		getContentPane().add( buttonPanel, BorderLayout.SOUTH );
 		
 		placeOnScreen( config.getScreenCenter() );
	}
		
	class LineAnalyzer implements Runnable {
		private JTextField lineCountField;
		private JTextField eolField;
		private CharacterUtil.LineCount result = null;
		public LineAnalyzer(JTextField lineCounter, JTextField eol) {
			lineCountField = lineCounter;
			eolField = eol;
		}
		public void run() {
			File file = targetFile.getFile();
			InputStream instream = null;
			try {
				instream = new BufferedInputStream( new FileInputStream( file ) );
				result = CharacterUtil.countLinebreaks(instream);
			} catch (IOException iox) {
				logger.error(iox.getClass().getSimpleName() + " reading " + file.getPath() + ": " + iox.getMessage());
				result = new CharacterUtil.LineCount(LineSeparator.UNDEFINED, 0);
			} finally {
				if(instream != null) try { instream.close(); } catch(IOException iox) { /* ignore */ }
			}
			SwingUtilities.invokeLater( new Runnable(){
				public void run() {
					lineCountField.setText( String.valueOf(result.getLineCount()) );
					eolField.setText( localizer.localize("label.line-separator." + result.getSeparator().name()) );
					// basePanel.repaint();
					InfoView.this.getContentPane().revalidate();
				}
			});
		}
	}
	
	class ModifyMimeType implements ActionListener {
		private MimeClassificationPanel modifyPanel;
		private JPanel modifyButtonPanel;
		private MimeType currentMimeType;
		private boolean newExtension;
		public void actionPerformed(ActionEvent eve) {
			
			currentMimeType = targetFile.getMimeType();
			
			newExtension = currentMimeType == null && targetFile.hasExtension();
			String introText = newExtension ? localizer.localize("message.set-mime-type-of-extension", targetFile.getFileExtension()) : localizer.localize("message.set-mime-type-of-file", targetFile.getName());

			MimeClassificationPanel.OPTION defaultOption = (currentMimeType == null ? (isBinary ? MimeClassificationPanel.OPTION.BINARY : MimeClassificationPanel.OPTION.TEXT) : MimeClassificationPanel.OPTION.CHOICE); 
			
			modifyPanel = new MimeClassificationPanel(InfoView.this, introText, defaultOption, currentMimeType, swingConfig, repository, newExtension);
			
			registerCloseWindowKeyListener( modifyPanel );
			modifyPanel.setMinimumSize( basePanel.getSize() );
						
			modifyPanel.add( Box.createVerticalGlue() );

			modifyButtonPanel = new JPanel( new FlowLayout(FlowLayout.RIGHT));
	 		JButton applyButton = new JButton( localizer.localize("button.apply") );
	 		applyButton.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					MimeType mimeType = modifyPanel.getSelectedMimeType();
					if( newExtension ) {
						mimeType.addExtension( targetFile.getFileExtension() );
					} else {
						targetFile.setMimeType( mimeType );
					}
					mimeField.setText( targetFile.getMimeType().getName());
					restore();
				}						
			});
	 		modifyButtonPanel.add( applyButton );
	 		JButton cancelButton = new JButton( localizer.localize("button.cancel") );
	 		cancelButton.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					restore();
				}						
			});
	 		modifyButtonPanel.add( cancelButton );

			
 			InfoView.this.getContentPane().remove(basePanel);
			InfoView.this.getContentPane().add( modifyPanel, BorderLayout.CENTER );
 			InfoView.this.getContentPane().remove(buttonPanel);
			InfoView.this.getContentPane().add( modifyButtonPanel, BorderLayout.SOUTH );
			InfoView.this.getContentPane().revalidate();
			InfoView.this.pack();
			// InfoView.this.getContentPane().repaint();			
		}
		
		private void restore() {
 			InfoView.this.getContentPane().remove(modifyPanel);
			InfoView.this.getContentPane().add( basePanel, BorderLayout.CENTER );
 			InfoView.this.getContentPane().remove(modifyButtonPanel);
			InfoView.this.getContentPane().add( buttonPanel, BorderLayout.SOUTH );
			InfoView.this.getContentPane().revalidate();
			InfoView.this.pack();
			// InfoView.this.getContentPane().repaint();			
		}
	}
}
