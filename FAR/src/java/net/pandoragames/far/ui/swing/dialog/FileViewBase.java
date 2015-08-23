package net.pandoragames.far.ui.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.pandoragames.far.ui.FARConfig;
import net.pandoragames.far.ui.model.CharacterUtil;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.MessageLabel;
import net.pandoragames.far.ui.swing.component.TwoComponentsPanel;
import net.pandoragames.util.i18n.Localizer;

/**
 * Base class for file viewer and file editor.
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
public abstract class FileViewBase extends SubWindow {

	private static final int MAXPATHLENGHT = 80;

	private static final int MINWIDTH = 475;
	private static final int MINHEIGHT = 150;
	
	private static int PREFWIDTH = 475;
	private static int PREFHEIGHT = 294;
	
	private static Point PREFEREDPOSITION = null;
	
	private SwingConfig swingConfig;
	private TargetFile targetFile;
	/**
	 * Sink for messages that should not go on the main text area.
	 */
	protected MessageBox messageBox;
	/**
	 * The character set selection drop down list. 
	 * Selection changes trigger document reload.
	 */
	protected JComboBox  charsetList;
	
	/**
	 * Constructor takes root window, the file to show and a config object.
	 * @param owner root window
	 * @param title window title
	 * @param file to be displayed
	 * @param config environment information
	 */
	public FileViewBase(JFrame owner, String title, TargetFile file, SwingConfig config) {
		super(owner, title);
		targetFile = file;
		swingConfig = config;
		init( config );
	}
	
	/**
	 * Implement this to load the target file into the
	 * respective TextComponent, using the character set
	 * defined by <tt><b>this.</b>getCharset()</tt>.
	 */
	protected abstract void loadFile();
	
	private void init(SwingConfig config) {
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout( new BorderLayout() );
		setPreferredSize( new Dimension(PREFWIDTH, PREFHEIGHT));
		setMinimumSize( new Dimension(MINWIDTH, MINHEIGHT) );
		addWindowListener( new RememberDimensionListener() );
		
		JComponent north = getNorthComponent();
		if ( north != null ) {
			registerCloseWindowKeyListener( north );
			getContentPane().add( north, BorderLayout.NORTH );
		}

		JComponent center = getCenterComponent();
		if ( center != null ) {
			registerCloseWindowKeyListener( center );
			getContentPane().add( center, BorderLayout.CENTER );
		}
		
		JComponent south = getSouthComponent();
		if ( south != null ) {
			registerCloseWindowKeyListener( south );
			getContentPane().add( south, BorderLayout.SOUTH );
		}
		
		JPanel emptyPanel = new JPanel();
		emptyPanel.setPreferredSize( new Dimension(5, 250) );
		getContentPane().add( emptyPanel, BorderLayout.WEST );
		
		if( PREFEREDPOSITION != null && ! hasPeer() ) {
			logger.debug("SET to fixed position");
			setLocation( PREFEREDPOSITION );
		} else {
			logger.debug("position = " + PREFEREDPOSITION + " / " + getOwner().getOwnedWindows().length);
			placeOnScreen( config.getScreenCenter() );
		}
	}
		
	/**
	 * Returns the swing component that will be placed in the 
	 * NORTH part of the windows BorderLayout. 
	 * The default implementation Adds the file path and a dropdown list
	 * for the character set to be used.
	 * @return swing component or null.
	 */
	protected JComponent getNorthComponent() {
		// display path in header
		String fullPath = targetFile.getPath();
		if( fullPath.length() > MAXPATHLENGHT) {
			int firstCut = fullPath.indexOf( File.separator, 6 );
			if((firstCut < 0) || (firstCut > 20)) firstCut = 20;
			String firstComponent = fullPath.substring(0, firstCut);
			int secondCut = fullPath.indexOf( File.separator, (fullPath.length() - MAXPATHLENGHT + 25) );
			if( secondCut < 0) secondCut = fullPath.length() - MAXPATHLENGHT + 25;
			String lastComponent = fullPath.substring(secondCut);
			fullPath = firstComponent + "..." + lastComponent;
		}
		FileViewMessages title = new FileViewMessages( fullPath );
		messageBox = title;
 		// combobox with character set
		Charset charset = getCharset();
 		charsetList = new JComboBox( CharacterUtil.getCharsetList(getConfig().getCharsetList(),charset) );
		charsetList.setSelectedItem( charset );
 		charsetList.addItemListener( new ItemListener()  {
 			public void itemStateChanged(ItemEvent event) {
 				Charset charset = (Charset) event.getItem();
 				logger.debug("Character set changed to " + charset.name());
 				targetFile.setCharacterset( charset );
 				messageBox.clear();
 				loadFile();
 			}
 		} );
 		JPanel boxpanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
 		boxpanel.add( charsetList );
 		TwoComponentsPanel headerBar = new TwoComponentsPanel(title, boxpanel);
 		headerBar.setBorder( BorderFactory.createEmptyBorder( 0, 
				SwingConfig.PADDING, 
				0, 
				SwingConfig.PADDING ) );
		return headerBar;
	}
	
	
	/**
	 * Returns the swing component that will be placed in the 
	 * CENTER part of the windows BorderLayout. 
	 * The default implementation returns null.
	 * @return swing component or null.
	 */
	protected JComponent getCenterComponent() {
		return null;
	}

	/**
	 * Returns the swing component that will be placed in the 
	 * SOUTH part of the windows BorderLayout. 
	 * The default implementation returns null.
	 * @return swing component or null.
	 */
	protected JComponent getSouthComponent() {
		return null;
	}
	
	/**
	 * Returns the current localizer instance.
	 * This is a shortcut for <tt>getConfig().getLocalizer()</tt>.
	 * @return Localizer
	 */
	protected Localizer getLocalizer() {
		return swingConfig.getLocalizer();
	}
	
	/**
	 * Returns the application configuration.
	 * @return configuration object
	 */
	protected SwingConfig getConfig() {
		return swingConfig;
	}
	
	/**
	 * Returns the TargetFile object that encapsulates
	 * the file to be displayed.
	 * @return respective TargetFile instance
	 */
	protected TargetFile getTargetFile() {
		return targetFile;
	}

	/**
	 * Returns the character set to be used to read the target file.
	 * Returns the character set defined for the target file if any,
	 * or the default character set if no specific character set is
	 * defined for the file.
	 * @return character set to be used to read the target file
	 */
	protected Charset getCharset() {
		return targetFile.getCharacterset();
	}
	
	/**
	 * Extends the MessageLabel class to display the file path by default.
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
	class FileViewMessages extends MessageLabel {
	
		private String filePath;
		
		public FileViewMessages(String path) {
			filePath = path;
			info( filePath );
		}
		
		public void clear() {
			super.clear();
			info( filePath );
		}
	}
	
	class RememberDimensionListener extends WindowAdapter {
		public void windowClosed(WindowEvent e) {
			Window win = e.getWindow();
			if(win != null) {
				Dimension dim = win.getSize();
				if( dim.width > MINWIDTH ) {
					PREFWIDTH = dim.width;
				} else {
					PREFWIDTH = MINWIDTH;
				}
				if( dim.height > MINHEIGHT ) {
					PREFHEIGHT = dim.height;
				} else {
					PREFHEIGHT = MINHEIGHT;
				}
				Point topleft = win.getLocation();
				if( ! hasPeer() ) {
					Point parentTopLeft = win.getOwner().getLocation();
					Dimension parentSize = win.getOwner().getSize();
					if( ! isOverlapping(topleft, dim, parentTopLeft, parentSize)) {
						PREFEREDPOSITION = topleft;
						logger.debug("Fixing position to " + topleft);
					} else {
						PREFEREDPOSITION = null;
					}
				}
			}
		}
		private boolean isOverlapping(Point testTopLeft, Dimension testSize, Point containerTopLeft, Dimension containerSize) {
			if( testTopLeft.x > containerTopLeft.x && testTopLeft.x < containerTopLeft.x + containerSize.width
					&& testTopLeft.y > containerTopLeft.y && testTopLeft.y < containerTopLeft.y + containerSize.height) return true;
			if( testTopLeft.x + testSize.width > containerTopLeft.x && testTopLeft.x + testSize.width < containerTopLeft.x + containerSize.width
					&& testTopLeft.y + testSize.height > containerTopLeft.y && testTopLeft.y + testSize.height < containerTopLeft.y + containerSize.height) return true;
			// otherwise
			return false;
		}
	}
}
