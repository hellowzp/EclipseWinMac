package net.pandoragames.far.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.util.i18n.Localizer;

/**
 * Utility class with formating methods.
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
public class FilePropertiesFormater {

	// Mind, this is not fit for concurrent use!
	private SimpleDateFormat dateTimeFormat = new SimpleDateFormat( SwingConfig.STANDARD_DATE_TIME_FORMAT );

	/**
	 * Formats the long value as a human readable time
	 * stamp using yyyy-MM-dd HH:mm as format.
	 * @see net.pandoragames.far.ui.FARConfig#STANDARD_DATE_TIME_FORMAT
	 * @param timestamp to be formated
	 * @return human readable time stamp
	 */
	public synchronized String timeStamp(long timestamp) {
		return dateTimeFormat.format( new Date(timestamp) );
	}
	
	/**
	 * Formats bytes in a human readable form as kB, MB or GB.
	 * @param bytes to be formated.
	 * @param localizer for fraction formating
	 * @return human readable byte format
	 */
	public String formatBytes( long bytes, Localizer localizer ) {
		final double KILO = 1024;
		if( bytes < KILO ) return bytes + " byte";
		double reduced = bytes / KILO;
		if( reduced < KILO ) return localizer.localize(reduced, 1) + " kB";
		reduced  = reduced / KILO;
		if( reduced < KILO ) return localizer.localize(reduced, 1) + " MB";
		reduced  = reduced / KILO;
		return localizer.localize(reduced, 1) + " GB";
	}

}
