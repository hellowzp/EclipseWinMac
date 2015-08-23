package net.pandoragames.far.ui.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;

/**
 * Displays single HTML pages.
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
public class HelpView extends SubWindow {

	private static final int MINWIDTH = 100;
	private static final int MINHEIGHT = 100;
	
	private static int PREFWIDTH = 0;
	private static int PREFHEIGHT = 0;

	private JEditorPane textArea;
	private JScrollPane contentScrollPane;
	private boolean isSmall = false;
	
	/**
	 * Constructor.
	 * @param owner root window
	 * @param title window title
	 * @param fileName a file relative to the "help" directory
	 * @param screenCenter screen coordinates
	 */
	public HelpView(JFrame owner, String title, String fileName, Point screenCenter) {
		super(owner, title);
		init(fileName, screenCenter);
	}
	
	/**
	 * Called when used for the about page.
	 */
	public void setSmall() {
		contentScrollPane.setPreferredSize( new Dimension( 375, 300 ) );
		contentScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		isSmall = true;
	}
	
	/*
	 * paint the window
	 */
	private void init(String fileName, Point screenCenter) {
		
		long timer = System.currentTimeMillis();
		// text area
		textArea = new JEditorPane();
		textArea.setEditable( false );
		textArea.setContentType("text/html;charset=UTF-8");
		textArea.addHyperlinkListener( new HelpLink() );

		readURI( textArea , "help/en/" + fileName );
		
		contentScrollPane = new JScrollPane( textArea );
		contentScrollPane.setAlignmentX( Component.LEFT_ALIGNMENT );
		registerCloseWindowKeyListener( contentScrollPane );
		
		contentScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
		
		if(PREFWIDTH < MINWIDTH || PREFHEIGHT < MINHEIGHT) calculatePreferedDim();
		contentScrollPane.setPreferredSize( new Dimension(PREFWIDTH, PREFHEIGHT) );
		contentScrollPane.setMinimumSize(new Dimension(MINWIDTH, MINHEIGHT));
		addWindowListener( new RememberDimensionListener() );
		getContentPane().add( contentScrollPane, BorderLayout.CENTER );
		
		placeOnScreen( screenCenter );
		logger.debug("file " + fileName + " loaded in " + (System.currentTimeMillis() - timer) + " ms");
	}
	
	private void calculatePreferedDim() {
		Rectangle screen = getGraphicsConfiguration().getBounds();
		PREFWIDTH = Math.min(screen.width/2,520);
		PREFHEIGHT = Math.min(2*screen.height/3, 560);
	}

	/*
	 * Read the resource and write it to the editor pane
	 */
	private void readURI(JEditorPane editorPane, String resource) {
		URL helpURL = this.getClass().getClassLoader().getResource( resource );
		if (helpURL != null) {
			readURI( editorPane, helpURL );
		} else {
			logger.error("Couldn't find file: " + resource );
		}		
	}

	/*
	 * Read the resource and write it to the editor pane
	 */
	private void readURI(JEditorPane editorPane, URL helpURL) {
	    try {
	        editorPane.setPage(helpURL);
	    } catch (IOException e) {
	        logger.error("Attempted to read a bad URL: " + helpURL);
	    }
	}
	
	// HyperlinkListener
    class HelpLink implements HyperlinkListener {  	 
        public void hyperlinkUpdate(HyperlinkEvent event) {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            	URL target = event.getURL();
            	if( target != null ) {
            		if( target.getRef() == null ) {
	            		logger.debug("Link to " + target.toString());
	            		readURI( textArea, target );
            		} else {
            			// finds <a name=ref> only
            			textArea.scrollToReference( target.getRef() );
            		}
            	}
            }
        }
    }

	class RememberDimensionListener extends WindowAdapter {
		public void windowDeactivated(WindowEvent e) {
			Window win = e.getWindow();
			if(win != null && ! isSmall) {
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
			}
		}
	}

}
