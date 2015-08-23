package net.pandoragames.util.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * ResourceBundle based implementation of the Localizer interface. 
 *
 * @see java.util.ResourceBundle
 * @author Olivier Wehner
 * <!--
 *  FAR - Find And Replace
 *  Copyright (C) 2009,  Olivier Wehner

 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  --> 
 */
public class BundleLocalizer extends LocalizerBase {
	

	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor takes the bundle base name and the desired locale.
	 * See <code>java.util.ResourceBundle</code> for details.
	 * @param baseName the base name of the resource bundle, a fully qualified class name
	 * @param locale non null locale
	 */
	public BundleLocalizer(String baseName, Locale locale) {
		super( locale );
		resourceBundle = ResourceBundle.getBundle(baseName, locale);
	}

	/**
	 * Returns some locale specific, human readable text for the specified code, 
	 * or the code unchanged if no such translation could be done,
	 * @param code application internal code for some text
	 * @return human readable text (or supplied code)
	 */
	public String localize(String code) {
		if( code == null ) return "";
		try {
			return resourceBundle.getString( code );
		} catch (MissingResourceException mrx) {
			return code;
		}
	}
}
