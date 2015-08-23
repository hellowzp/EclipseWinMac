package net.pandoragames.far.ui.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.pandoragames.far.ui.model.FARForm;
import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.ReplaceForm;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.TwoComponentsPanel;

/**
 * A file viewer that shows either a <i>view</i> on the matches of a regular expression,
 * or a <i>preview</i> on a replace operation.
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
public class FileView extends FileViewBase
{
	private static final Pattern LINEBREAK = Pattern.compile("\\r\\n|\\n|\\r[^\\n]");
	
	private static final String MARKED = "marked";
	private static final String CMD_VIEW = "VIEW";
	private static final String CMD_PREVIEW = "PREVIEW";
	private JTextPane textArea;
	private JLabel countDisplay;
	private Pattern regex;
	private String replacementPattern;
	private boolean runAsPreview;
	private CommandButtonGroup buttonGroup;
	private JViewport viewport;
	private Ruler findingsRuler;
	private Color markerColor;
	
	/**
	 * Constructor that takes a ReplaceForm and allows both, view and preview.
	 * @param owner root window
	 * @param file to be displayed
	 * @param form a ReplaceForm instance that holds the raw regex pattern
	 * @param config environment information
	 * @param preview <i>true</i> if this view should display a 'preview' of the replace command,
	 * <i>false</i> if this view should rather show the matches of the regular expression.
	 */
	public FileView(JFrame owner, TargetFile file, ReplaceForm form, SwingConfig config, boolean preview) {
		this(owner, file, form, form.getReplacementPattern("\n").getCanonicalString(), config, preview);
	}
	
	/**
	 * Constructor that takes a FindForm and allows only a view of the matches.
	 * @param owner root window
	 * @param file to be displayed
	 * @param form a FindForm instance that holds the raw regex pattern
	 * @param config environment information
	 */
	public FileView(JFrame owner, TargetFile file, FindForm form, SwingConfig config) {
		this(owner, file, form, null, config, false);
	}
	
	
	private FileView(JFrame owner, TargetFile file, FARForm form, String replacement, SwingConfig config, boolean preview) {
		super(owner, file.getName(), file, config);
		try {
			regex = form.getContentPatternAsRegex();		
			findingsRuler = new Ruler();
			getContentPane().add( findingsRuler, BorderLayout.EAST );
			replacementPattern = replacement;
			if(replacement == null) buttonGroup.disable( CMD_PREVIEW );
			runAsPreview = preview;
			buttonGroup.select( preview ? CMD_PREVIEW : CMD_VIEW);
			if( preview ) {
				markerColor = Color.BLUE;
				Style style = textArea.addStyle(MARKED, null); 
				StyleConstants.setForeground(style, Color.WHITE); 
				StyleConstants.setBackground(style, markerColor);
				setTitle( config.getLocalizer().localize("label.preview") + " " + file.getName());
			} else {
				markerColor = Color.GREEN;
				Style style = textArea.addStyle(MARKED, null); 
				StyleConstants.setForeground(style, Color.BLACK); 
				StyleConstants.setBackground(style, markerColor);
			}
			loadFile();
			textArea.setCaretPosition(0);			
		} catch (PatternSyntaxException px) {
			buttonGroup.disable( CMD_VIEW );
			buttonGroup.disable( CMD_PREVIEW );
			logger.error( "PatternSyntaxException: " + px.getMessage(), px);
			textArea.setText("");
			textArea.setFont( new Font("Monospaced", Font.PLAIN, 11 ) );
			textArea.setForeground( Color.RED );
			textArea.setText( getLocalizer().localize("message.syntax-error", 
								new Object[]{px.getMessage()} ));	
		}
	}
	
	
	/**
	 * Returns a JTextArea in a scroll pane
	 * @return JTextArea in a scroll pane
	 */
	protected JComponent getCenterComponent() {
		// text area
		textArea = new JTextPane();
		textArea.setEditable( false );
		JScrollPane contentScrollPane = new JScrollPane( textArea );
		contentScrollPane.setAlignmentX( Component.LEFT_ALIGNMENT );
		Rectangle screen = getGraphicsConfiguration().getBounds();
		contentScrollPane.setPreferredSize( new Dimension( Math.min(screen.width/3,420), 
				 Math.min(2*screen.height/3, 560) ) );
		viewport = contentScrollPane.getViewport();
		return contentScrollPane;
	}
	
	/**
	 * Returns a counter for the matches / replacements in the file.
	 */
	protected JComponent getSouthComponent() {
		countDisplay = new JLabel();
		countDisplay.setBorder( BorderFactory.createEmptyBorder( 0, 
				SwingConfig.PADDING, 
				0, 
				SwingConfig.PADDING ) );
		JPanel buttonPannel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		buttonGroup = new CommandButtonGroup();
		JToggleButton viewSelectButton = new JToggleButton();
		viewSelectButton.setIcon( new ImageIcon( this.getClass().getClassLoader().getResource("icons/toggle_button_green_disabled.png") ) );
		viewSelectButton.setSelectedIcon( new ImageIcon( this.getClass().getClassLoader().getResource("icons/toggle_button_green_enabled.png") ) );
		viewSelectButton.setDisabledIcon( new ImageIcon( this.getClass().getClassLoader().getResource("icons/toggle_button_grey.png") ) );
		viewSelectButton.setToolTipText(getLocalizer().localize("label.view"));
		viewSelectButton.setActionCommand( CMD_VIEW );
		viewSelectButton.setContentAreaFilled( false );
		viewSelectButton.setMargin( new Insets(0,0,0,0) );
		viewSelectButton.setBorderPainted( false );
		viewSelectButton.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				runAsPreview = false;
				markerColor = Color.GREEN;
				setTitle( getTargetFile().getFile().getName() );
				Point topleft = viewport.getViewPosition();
				loadFile();
				int spot = textArea.viewToModel(topleft);
				textArea.setCaretPosition(spot);
			}
		}); 
		buttonGroup.add( viewSelectButton );
		buttonPannel.add(viewSelectButton);
		JToggleButton previewSelectButton = new JToggleButton();
		previewSelectButton.setIcon( new ImageIcon( this.getClass().getClassLoader().getResource("icons/toggle_button_blue_disabled.png") ) );
		previewSelectButton.setSelectedIcon( new ImageIcon( this.getClass().getClassLoader().getResource("icons/toggle_button_blue_enabled.png") ) );
		previewSelectButton.setDisabledIcon( new ImageIcon( this.getClass().getClassLoader().getResource("icons/toggle_button_grey.png") ) );
		previewSelectButton.setToolTipText(getLocalizer().localize("label.preview"));
		previewSelectButton.setActionCommand( CMD_PREVIEW );
		previewSelectButton.setContentAreaFilled( false );
		previewSelectButton.setMargin( new Insets(0,0,0,0) );
		previewSelectButton.setBorderPainted( false );
		previewSelectButton.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				runAsPreview = true;
				markerColor = Color.BLUE;
				setTitle( getConfig().getLocalizer().localize("label.preview") + " " + getTargetFile().getFile().getName());
				Point topleft = viewport.getViewPosition();
				loadFile();
				int spot = textArea.viewToModel(topleft);
				textArea.setCaretPosition(spot);
			}
		}); 
		buttonGroup.add( previewSelectButton );
		buttonPannel.add(previewSelectButton);
		return new TwoComponentsPanel(countDisplay,buttonPannel);
	}
	
		
	/**
	 * Loads the target file into the components text area,
	 * using the indicated character set.
	 */
	protected void loadFile() {
		File file = getTargetFile().getFile();
		Reader reader = null;
		Cursor originalcursor = this.getOwner().getCursor();
		this.getOwner().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			textArea.setForeground( Color.BLACK );
			reader = new InputStreamReader( new FileInputStream( file ), getCharset());
			textArea.read( reader, file );
			resetDocumentStyle();
			highlight();
		} catch (IOException iox) {
			logger.error("IOException displaying file " + file.getPath() +": " + iox.getMessage());
			textArea.setText("");
			textArea.setForeground( Color.RED );
			textArea.setText( iox.getMessage() );	
		} catch (OutOfMemoryError omu) {
			logger.error( omu.getClass().getSimpleName() + ": " + omu.getMessage(), omu);
			textArea.setText("");
			textArea.setForeground( Color.RED );
			String message = getLocalizer().localize("message.document-too-large-for-display");
			textArea.setText( message + "\n(OutOfMemoryError: " + omu.getMessage() + ")");	
		} catch (Exception x) {
			String message = x.getClass().getName() + ": " + x.getMessage();
			logger.error(message, x);
			textArea.setText("");
			textArea.setForeground( Color.RED );
			textArea.setText( x.getClass().getSimpleName() + ": " + message );				
		} finally {
			this.getOwner().setCursor( originalcursor );
			if( reader != null ) try {reader.close(); } catch(IOException iox) {logger.warn("IOException closing stream: " + iox.getMessage());}
		}
	}
	
	// JTextPane.read() erases the underlying document and its styles.
	private void resetDocumentStyle() {
		if( runAsPreview ) {
			Style style = textArea.addStyle(MARKED, null); 
			StyleConstants.setForeground(style, Color.WHITE); 
			StyleConstants.setBackground(style, markerColor);
		} else {
			Style style = textArea.addStyle(MARKED, null); 
			StyleConstants.setForeground(style, Color.BLACK); 
			StyleConstants.setBackground(style, markerColor);
		}		
	}
	
	private void highlight() {
		StyledDocument doc = textArea.getStyledDocument(); 
		List<Integer> matchList = new ArrayList<Integer>();
		String rawText = null;
		if(! runAsPreview ) {
			// replace all as dirty workaround for windows line seperator
			rawText = textArea.getText().replaceAll("\\r\\n", "\n");
			Matcher matcher = regex.matcher( rawText );
			int count = 0;
			while( matcher.find() ) {
				count++;
				int length = matcher.end() - matcher.start();
				doc.setCharacterAttributes(matcher.start(), length, textArea.getStyle(MARKED), true);
				matchList.add( matcher.start() );
			}
			switch( count ) {
				case 0 : countDisplay.setText( getLocalizer().localize("message.no-matches") ); break;
				case 1 : countDisplay.setText( getLocalizer().localize("message.one-match") ); break;
				default : countDisplay.setText( getLocalizer().localize("message.n-matches", new Object[]{Integer.valueOf(count)}) );
			}			
		} else {
			// replace all as dirty workaround for windows line seperator
			rawText = textArea.getText().replaceAll("\\r\\n", "\n");
			Matcher matcher = regex.matcher( rawText );
			int count = 0;
			StringBuffer buffer = new StringBuffer();
			List<MatchBounds> matchBounds = new ArrayList<MatchBounds>();
			int start = 0;
			while( matcher.find() ) {
				count++;
				buffer.append( rawText.substring(start, matcher.start()) );
				Matcher helper = regex.matcher( matcher.group() );
				if( helper.matches() ) {
					String replaced = helper.replaceFirst( replacementPattern );
					matchBounds.add( new MatchBounds(buffer.length(), replaced.length()) );
					buffer.append( replaced );
				} else {
					String message = "Could not rematch match #" + count + " for " + regex.pattern() + " in file " + getTargetFile().getFile().getPath();
					logger.error( message );
					throw new IllegalStateException( message );
				}
				start = matcher.end();
			}
			buffer.append( rawText.substring(start, rawText.length()) );
			rawText = buffer.toString();
			textArea.setText( rawText ); // raw text is now replaced
			for(int i = 0; i < matchBounds.size(); i++) {
				doc.setCharacterAttributes(matchBounds.get(i).getStart(), matchBounds.get(i).getLength(), 
											textArea.getStyle(MARKED), true);
				matchList.add( matchBounds.get(i).getStart() );
			}
			String infotext = getLocalizer().localize("message.replacement-count", new Object[]{Integer.valueOf(count)});
			if( count > 0 ) infotext = infotext + " (" + getLocalizer().localize("message.file-not-affected") + ")";
			countDisplay.setText( infotext );
		}
		List<Integer> lineBreaks = new ArrayList<Integer>();
		List<Integer> lineMarks = new ArrayList<Integer>();
		int lineCount = 1;
		if(matchList.size() > 0) {
			Matcher breakCounter = LINEBREAK.matcher( rawText );
			int matchIndex = 0;
			while( breakCounter.find() ) {
				int breakPosition = breakCounter.start();
				lineBreaks.add( breakPosition );
				while( matchIndex < matchList.size() 
						&& matchList.get( matchIndex ) < breakPosition) {
					lineMarks.add( lineCount );
					matchIndex++;
				}
				lineCount++;			
			}			
		}
		findingsRuler.setLineIndex( lineBreaks );
		findingsRuler.setMarker(lineMarks, lineCount);
	}
	
		
	/**
	 * Used to keep a list of match boundaries for the result text of a replacement operation.
	 */
	class MatchBounds {
		int start;
		int length;
		public MatchBounds(int start, int length) {
			this.start = start;
			this.length = length;
		}
		public int getStart() {
			return start;
		}
		public int getLength() {
			return length;
		}
	}
	
	/**
	 * Extends the superclass to allow selection by action command string.
	 */
	class CommandButtonGroup extends ButtonGroup {
		private Map<String,ButtonModel> buttonMap = new HashMap<String,ButtonModel>();
		public void add(AbstractButton b) {
			buttonMap.put( b.getActionCommand(), b.getModel());
			super.add(b);
		}
		public void select(String actionCommand) {
			ButtonModel model = buttonMap.get( actionCommand );
			if( model != null ) {
				setSelected(model, true);
			}
		}
		public void disable(String actionCommand) {
			ButtonModel model = buttonMap.get( actionCommand );
			if( model != null ) {
				model.setEnabled( false );
			}
		}
	}
	
	/**
	 * A side bar that displays the matches (findings) as a simple line
	 * and responds to mouse clicks.
	 */
	class Ruler extends JPanel {
		private static final int WIZZ = 10;
		private List<Integer> markerList;
		private List<Integer> lineIndex;
		private int lineCount;
		// private int charCount;
		public Ruler(){
			setPreferredSize( new Dimension( WIZZ, 200 ) );
			this.addMouseListener( new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
					if( event.getButton() == MouseEvent.BUTTON1 ) {
						Point clickPoint = event.getPoint();
						double percent = clickPoint.getY() / getSize().height;
						int line = (int) Math.round( percent * lineCount );
						int position = 0;
						if( line > 1 && (line - 1) < lineIndex.size() ) {
							position = lineIndex.get( line - 1 );
						}
						// logger.debug( "Index " + line + " = " + percent + " * " + lineCount + " => pos " + position );
						try{
							textArea.setCaretPosition( position );							
						}catch(IllegalArgumentException iax) {
							logger.warn("Bad position: " + position + " > " + textArea.getDocument().getLength());
						}
					}
				}
			});
		}
		public void setMarker(List<Integer> matchList, int count) {
			markerList = matchList;
			lineCount = count;
			repaint();
		}
		public void setLineIndex(List<Integer> index) {
			lineIndex = index;
			// charCount = textArea.getDocument().getLength();
		}
		protected void paintComponent(Graphics graphics) {
			super.paintComponent( graphics );
			if( markerList != null && markerList.size() > 0 ) {
				if( lineCount < markerList.get( markerList.size() - 1 )) {
					lineCount = markerList.get( markerList.size() - 1 );
				}
				int rulerHeight = getSize().height;
				graphics.setColor( markerColor );
				for(Integer mark : markerList) {
					int relheight = rulerHeight * mark / lineCount;
					graphics.drawLine(0, relheight, WIZZ, relheight);
				}
			}
		}
	}

}
