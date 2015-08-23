package net.pandoragames.util.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

/**
 * This default instance of the Localizer interface reads it replacement codes
 * from some property file (or object). It effectively does not care very much 
 * about the Locale it is instantiated with, except for the decimal seperator.
 * Relevant code parts are supposed to be synchronized.
 * 
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
public class DefaultLocalizer extends LocalizerBase
{

	private Properties myCodeList;
	/**
	 * Instantiates the DefaultLocalizer with some locale object and a Properties
	 * object that contains the code translations as key-value pairs.
	 */
	public DefaultLocalizer(Properties codelist, Locale locale)
	{
		super( locale );
		if( codelist == null )throw new NullPointerException("Codelist must not be null");
		myCodeList = codelist;
	}
	
	/**
	 * Instantiates the DefaultLocalizer with some locale object and a (properties)
	 * file that contains the code translations as key-value pairs.
	 */
	public DefaultLocalizer(File propertyFile, Locale locale)
	throws IOException
	{
		this( readProperties(propertyFile), locale);
	}

	/**
	 * Returns some locale specific, human readable text for the specified code, 
	 * or the code unchanged if no such translation could be done,
	 * @param code application internal code for some text
	 * @return human readable text (or supplied code)
	 */
	public String localize(String code)
	{
		String text = myCodeList.getProperty(code);
		if( text == null ) return code;
		return text;
	}

	private static Properties readProperties(File properties)
	throws IOException
	{
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(properties);
			Properties props = new Properties();
			props.load(fis);
			return props;
		}
		finally
		{
			if (fis != null) try { fis.close(); }catch(IOException iox){ /* ignore */ }
		}
	}
}
