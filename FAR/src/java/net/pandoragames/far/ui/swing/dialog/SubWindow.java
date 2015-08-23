package net.pandoragames.far.ui.swing.dialog;

import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for subwindowss. 
 * @author Olivier Wehner at 31.03.2008
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
public class SubWindow extends JDialog
{
	protected Log logger;	

	protected WindowCloseAction windowCloseAction = new WindowCloseAction();
	
	/**
	 * Always specify owner and title. The window will be created non-modal.
	 * @param owner top level window
	 * @param title for this window
	 */
	public SubWindow(JFrame owner, String title) {
		super(owner, title, false);
		logger = LogFactory.getLog( this.getClass() );
	}

	/**
	 * Constructor for modal subwindows (modal windows stay on top).
	 * @param owner top level window
	 * @param title for this window
	 * @param modal set to true for a modal window.
	 */
	public SubWindow(JFrame owner, String title, boolean modal) {
		super(owner, title, modal);
		logger = LogFactory.getLog( this.getClass() );

	}
	
	/**
	 * Registers Ctrl + w as a window close event on the specified component;
	 * 
	 * @param component to becomce receptive for ctrl + w
	 */
	protected void registerCloseWindowKeyListener(JComponent component) {
		component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK), "windowCloseAction");
		component.getActionMap().put("windowCloseAction", windowCloseAction);		
	}
	
	/**
	 * Registers an event listener for the Enter key on some text component.
	 * 
	 * @param component on which to register
	 * @param action what is registered
	 */
	protected void registerEnterKeyListener(JTextComponent component, Action action) {
		component.getKeymap().addActionForKeyStroke( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), action );
	}

	/**
	 * Positions this FileWindow on the screen. The position is choosen with respect to
	 * the specified screen center, the position of the owner and the position of the last 
	 * visible subwindow of the owner, if any.
	 * @param screenCenter coordinates of the screen center
	 */
	protected void placeOnScreen(Point screenCenter) {
		Window parent = getOwner();
		Point indent = parent.getLocationOnScreen();
		Point leftUpper = new Point(200,50);
		Window sister = getLatestPeer();
		if(sister != null) {
			try {
				leftUpper = sister.getLocationOnScreen();
				indent = new Point(25,25);
			} catch (IllegalComponentStateException icsx) {
				leftUpper = new Point(screenCenter);
			}
		}
//		
//		Window[] peers = parent.getOwnedWindows();
//		if( peers.length > 1 ) {
//			int count = peers.length -2;
//			Window sister = null;
//			do {
//				sister = peers[count];
//				count--;
//			} while ((count >= 0) 
//							&& ((sister == null) || (! sister.isShowing())));
//			if((sister != null) && (sister.isShowing())) {
//				try {
//					leftUpper = sister.getLocationOnScreen();
//					indent = new Point(25,25);
//				} catch (IllegalComponentStateException icsx) {
//					leftUpper = new Point(screenCenter);
//				}
//			}
//		} 
		if(leftUpper == null) {
			logger.warn("Location was null, using screen center");
			leftUpper = new Point(screenCenter);
		}
		if(leftUpper.x > screenCenter.x) indent.x = -indent.x;
		if(leftUpper.y > screenCenter.y) indent.y = -indent.y;
		setLocation( leftUpper.x + indent.x, leftUpper.y + indent.y);
	}	
	
	/**
	 * Returns true if another Window of the same type as this Window is currently open.
	 * @return true if another Window of same type is open.
	 */
	protected boolean hasPeer() {
		Window[] sistas = getOwner().getOwnedWindows();
		for(int i = sistas.length-2; i >= 0; i--) {
			if( sistas[i] != null && sistas[i].isShowing() && (sistas[i].getClass().equals( this.getClass())) ) return true;
		}
		return false;
	}
	
	/**
	 * Returns the last window of the same type as this window that has been opened and is still displayed, or null.
	 * @return last window of same type as this window or null
	 */
	protected Window getLatestPeer() {
		Window[] sistas = getOwner().getOwnedWindows();
		for(int i = sistas.length-2; i >= 0; i--) {
			if( sistas[i] != null && sistas[i].isShowing() && (sistas[i].getClass().equals( this.getClass())) ) return sistas[i];
		}
		return null;		
	}
	
	class WindowCloseAction extends AbstractAction {
		public void actionPerformed(ActionEvent eve) {
			SubWindow.this.dispose();
		}
	}
}
