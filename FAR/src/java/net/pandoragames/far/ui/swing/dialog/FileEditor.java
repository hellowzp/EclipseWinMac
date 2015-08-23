package net.pandoragames.far.ui.swing.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.CharsetEncoder;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import net.pandoragames.far.ui.model.CharacterUtil;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.TwoComponentsPanel;
import net.pandoragames.far.ui.swing.component.UndoHistory;

/**
 * A primitive Editor based on a JTextArea.
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
public class FileEditor extends FileViewBase {

	private JTextArea textArea;
	private Action saveAction;
	private JButton saveButton;
	private LineCounterField lineCounter;
	private UndoHistory undoManager;
	
	/**
	 * Constructor takes root window, the file to show and a config object.
	 * @param owner root window
	 * @param file to be displayed
	 * @param config environment information
	 */
	public FileEditor(JFrame owner, TargetFile file, SwingConfig config) {
		super(owner, config.getLocalizer().localize("label.editor-title", file.getName()),file, config);
	}
	
	
	/**
	 * Returns a JTextArea in a scroll pane
	 * @return JTextArea in a scroll pane
	 */
	protected JComponent getCenterComponent() {
		// visually placed in the south !
		lineCounter = new LineCounterField();
		lineCounter.setBorder( BorderFactory.createEmptyBorder(0,SwingConfig.PADDING,0,0) );
 		// end of visually placed in the south

		saveAction = new SaveAction( getLocalizer().localize("button.save") );

		// text area
		textArea = new JTextArea();
		textArea.setLineWrap( false );
		textArea.setEditable( true );
		textArea.addCaretListener( lineCounter );
		textArea.getKeymap().addActionForKeyStroke( KeyStroke.getKeyStroke(KeyEvent.VK_S, 
																			InputEvent.CTRL_DOWN_MASK), 
													saveAction );
		loadFile();
		JScrollPane contentScrollPane = new JScrollPane( textArea );
		contentScrollPane.setAlignmentX( Component.LEFT_ALIGNMENT );
		Rectangle screen = getGraphicsConfiguration().getBounds();
		contentScrollPane.setPreferredSize( new Dimension( Math.min(screen.width/3,420), 
				 Math.min(2*screen.height/3, 560) ) );
		return contentScrollPane;
	}
	
	/**
	 * Returns the Save and Canvel button.
	 * @return Save and Canvel button
	 */
	protected JComponent getSouthComponent() {		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
 		// save button
 		saveButton = new JButton( saveAction );
 		saveButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				saveFile();
				undoManager.reset();
				saveAction.setEnabled( false );
			}						
		});
 		panel.add( saveButton );
 		// close button
 		JButton closeButton = new JButton( getLocalizer().localize("button.close") );
 		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				FileEditor.this.dispose();
			}						
		});
 		panel.add( closeButton );

 		return new TwoComponentsPanel(lineCounter, panel);
	}
		
	/**
	 * Loads the target file into the components text area,
	 * using the indicated character set.
	 */
	protected void loadFile() {
		// do not reload if file has changed
		if( saveAction.isEnabled() ) return;
		File file = getTargetFile().getFile();
		Reader reader = null;
		Cursor originalcursor = this.getOwner().getCursor();
		this.getOwner().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			reader = new InputStreamReader( new FileInputStream( file ), getCharset());
			textArea.read( reader, file );
			textArea.setCaretPosition(0);	
			wireupSaveButton();
			lineCounter.setLineCount(1);
			undoManager = new UndoHistory();
			undoManager.registerUndoHistory( textArea );
		} catch (IOException iox) {
			logger.error("IOException displaying file " + file.getPath() +": " + iox.getMessage());
			textArea.setText("");
			textArea.setForeground( Color.RED );
			textArea.setText( iox.getMessage() );	
		} catch (OutOfMemoryError omu) {
			logger.error( omu.getClass().getName() + ": " + omu.getMessage(), omu);
			textArea.setText("");
			textArea.setForeground( Color.RED );
			String message = getLocalizer().localize("message.document-too-large-for-display");
			textArea.setText( message + "\n(OutOfMemoryError: " + omu.getMessage() + ")");	
		} catch (Exception x) {
			String message = x.getClass().getName() + ": " + x.getMessage();
			logger.error(message, x);
			textArea.setText("");
			textArea.setForeground( Color.RED );
			textArea.setText( message );				
		} finally {
			this.getOwner().setCursor( originalcursor );
			if( reader != null ) try {reader.close(); } catch(IOException iox) {logger.warn("IOException closing stream: " + iox.getMessage());}
		}
	}

	/**
	 * Saves the file back to disk.
	 */
	private void saveFile() {
		File file = getTargetFile().getFile();
		Writer writer = null;
		Cursor originalcursor = this.getOwner().getCursor();
		this.getOwner().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			CharsetEncoder encoder = getCharset().newEncoder();
			if(encoder.canEncode( textArea.getText() )) {
				writer = new OutputStreamWriter( new FileOutputStream( file ), getCharset() );
				textArea.write( writer );
				messageBox.clear();
			} else {
				findIllegalCharacter( encoder );
			}
		} catch (FileNotFoundException fnfx) {
			// happens also if file is RO
			logger.error( "FileNotFoundException saving to " + file.getPath() 
							+ ": " + fnfx.getMessage());
			messageBox.error( getLocalizer().localize("message.file-removed") );
		} catch (IOException iox) {
			logger.error("IOException saving file " + file.getPath() + ": " + iox.getMessage());
			messageBox.error( getLocalizer().localize("message.file-processing-error", new Object[]{iox.getMessage()} ) );
		} finally {
			this.getOwner().setCursor( originalcursor );
			if( writer != null ) try {writer.close(); } catch(IOException iox) {logger.warn("IOException closing stream: " + iox.getMessage());}
		}
	}
	
	/**
	 * Rewire the save action with the documents update listener interface.
	 * This is necessary because the document is discarded on each reload,
	 * i.e. on every change of the character set.
	 */
	private void wireupSaveButton() {
		saveAction.setEnabled( false );
 		textArea.getDocument().addDocumentListener(new DocumentListener() {
 			public void insertUpdate(DocumentEvent e) {
 				activateSaveButton();
 			}
 			public void removeUpdate(DocumentEvent e) {
 				activateSaveButton();		
 			}
 			public void changedUpdate(DocumentEvent e) {
 				activateSaveButton();
 			}
 			private void activateSaveButton() {
 				if( ! saveAction.isEnabled() ) {
 					if( getTargetFile().getFile().canWrite() ) {
 						saveAction.setEnabled( true );
 					} else {
 						messageBox.info( getLocalizer().localize("message.read-only") );
 					}
 				}
 			}
 		});
	}
	
	/**
	 * Find the position of the first illegal character for the present
	 * encoding (if any) and write a suitable message to the message sink.
	 * @param encoder to be used for encoding
	 */
	private void findIllegalCharacter(CharsetEncoder encoder) {
		String text = textArea.getText();		
		CharacterUtil.reportIllegalCharacter(text.toCharArray(), 0, text.length(), encoder, 1, messageBox, getLocalizer());
	}
	
	/**
	 * A JTextField that displays line counts and acts as CaretListener for this purpose.
	 */
	class LineCounterField extends JTextField implements CaretListener {
		private int currentCount = -1;
		public LineCounterField() {
			setEditable( false );
			setBorder( BorderFactory.createEmptyBorder() );
		}
		public void setLineCount(int line) {
			if( line != currentCount ) {
				String total = String.valueOf(textArea.getLineCount());
				String pos = String.valueOf( line );
				StringBuffer label = new StringBuffer();
				for(int i = 0; i < total.length() - pos.length(); i++) label.append(" ");
				label.append(pos).append("/").append(total);
				this.setText( label.toString() );
			}
		}
		public void caretUpdate(CaretEvent e) {
			try {
				setLineCount( textArea.getLineOfOffset(e.getDot()) + 1 );
			} catch (BadLocationException blx) {
				logger.warn("BadLocationException: " + blx.getMessage());
			}
		}
	}
	
	class SaveAction extends AbstractAction {
		public SaveAction(String title){
			super( title );
			setEnabled( false );
			putValue( Action.ACTION_COMMAND_KEY, "SAVE" );
		}
		public void actionPerformed(ActionEvent event) {
			saveFile();
			undoManager.reset();
			saveButton.setEnabled( false );
//			charsetList.setEnabled( true );
		}	
//		public void setEnabled(boolean enabled) {
//			super.setEnabled( enabled );
//			if( charsetList != null ) charsetList.setEnabled( ! enabled );
//		}
	}
}
