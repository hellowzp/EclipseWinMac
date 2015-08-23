package net.pandoragames.far.ui.swing.component;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * A button with the visual Layout of a Hyperlink.
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
public class LinkButton extends JButton {

	/**
	 * Constructor that takes the button label.
	 * @param text button label
	 */
	public LinkButton(String text) {
		super(text);
		initme(null, null);
	}

	/**
	 * Fully featured constructor with font properties
	 * @param text button label
	 * @param bold font weight
	 * @param italic font posture
	 */
	public LinkButton(String text, boolean bold, boolean italic) {
		super(text);
		initme(bold, italic);
	}

	/**
	 * Constructor that takes the button Action.
	 * @param action button Action
	 */
	public LinkButton(Action action) {
		super(action);
		initme(null, null);
	}

	private void initme(Boolean bold, Boolean italic) {
		setContentAreaFilled( false );
		setMargin( new Insets(0,0,0,0) );
		setBorderPainted( false );
		setHorizontalAlignment(SwingConstants.LEFT);
		Font buttonFont = getFont();
		Map attributes = buttonFont.getAttributes();
	    attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
	    if( bold != null ) {
	    	Float value = bold.booleanValue() ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR;
	    	attributes.put(TextAttribute.WEIGHT, value);
	    }
	    if( italic != null ) {
	    	Float value = italic.booleanValue() ? TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR;
	    	attributes.put(TextAttribute.POSTURE, value);
	    }
	    attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		setFont(buttonFont.deriveFont(attributes));
		setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR ));
	}
}
