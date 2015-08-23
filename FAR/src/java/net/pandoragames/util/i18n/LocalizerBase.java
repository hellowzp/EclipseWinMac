package net.pandoragames.util.i18n;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Base class for simple Localizer implementations. Inheriting classes only
 * have to implement the abstract method
 * {@link net.pandoragames.util.i18n.Localizer#localize(String) localize()}.
 * <p>
 * If multiple threads are accessing this object, they should synchronize on it.
 * </p>
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
public abstract class LocalizerBase implements Localizer {

	private Locale locale;
	protected NumberFormat integerFormater= null;
	protected NumberFormat decimalFormater= null;
	private HashMap dateFormater = new HashMap( 2, 1F );
	private int useDigits = 0;

	
	public LocalizerBase(Locale locale) {
		if( locale == null ) throw new NullPointerException("Locale must not be null");
		this.locale = locale;
		integerFormater = NumberFormat.getIntegerInstance( locale );
		decimalFormater = NumberFormat.getNumberInstance( locale );
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getLanguage() {
		return locale.getLanguage();
	}

	/**
	 * {@inheritDoc}
	 */
	public Locale getLocale() {
		return locale;
	}
		
	/**
	 * {@inheritDoc}
	 */
	public String localize(String code, Object replacementValue) {
		return localize( code, new Object[]{replacementValue} );
	}

	/**
	 * {@inheritDoc}
	 */
	public String localize(String code, Object[] replacementValues) {
		String text = localize(code);
		if(text != null)
		{
			text = MessageFormat.format(text, replacementValues);
		}
		return text;
	}

	/**
	 * {@inheritDoc}
	 */
	public String localize(long number) {
		return integerFormater.format(number);
	}

	/**
	 * {@inheritDoc}
	 */
	public String localize(double number, int digits)
	{
		String result = null;
		if(useDigits != digits)
		{
			decimalFormater.setMinimumFractionDigits(digits);
			decimalFormater.setMaximumFractionDigits(digits);
			useDigits = digits;
		}
		result = decimalFormater.format(number);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public String localize(Date date, String patternCode)
	{
		if(date == null) return "";
		String pattern = localize( patternCode );
		if(pattern == null) return "";
		return dateFormater(pattern).format( date );
	}

	
	/**
	 * {@inheritDoc}
	 */
	public Date parseDate(String date, String patternCode) {
		if((date == null) || (date.length() == 0)) return null;
		String pattern = localize( patternCode );
		if(pattern == null) return null;
		try {
			return dateFormater(pattern).parse( date );
		} catch (ParseException px) {
			return null;
		}
	}

	/**
	 * Returns the int value that is represented by the specified String, or throws
	 * a ParseException if parsing fails.
	 * @param integerNumber String that represents an integer value
	 * @return integer value represented by the specified String, or null if the String could not be parsed
	 */
	public Integer parseInt(String integerNumber)
	{
		try
		{
			return new Integer(integerFormater.parse(integerNumber).intValue());
		}
		catch(ParseException px)
		{
			return null;
		}
	}

	/**
	 * Returns the long value that is represented by the specified String, or throws
	 * a ParseException if parsing fails.
	 * @param longNumber String that represents a long value
	 * @return long value represented by the specified String, or null if the String could not be parsed
	 */
	public Long parseLong(String longNumber)
	{
		try
		{
			return new Long(integerFormater.parse(longNumber).longValue());
		}
		catch(ParseException px)
		{
			return null;
		}
	}

	/**
	 * Returns the double value that is represented by the specified String, or throws
	 * a ParseException if parsing fails.
	 * @param doubleNumber String that represents a double value
	 * @return double value represented by the specified String, or null if the String could not be parsed
	 */
	public Double parseDouble(String doubleNumber)
	{
		try
		{
			return new Double( decimalFormater.parse(doubleNumber).doubleValue());
		}
		catch(ParseException px)
		{
			return null;
		}
	}
	
	/**
	 * Returns a SimpleDateFormat instance for the specified pattern.
	 * @param pattern date pattern
	 * @return instance of SimpleDateFormat for specified pattern
	 */
	private synchronized SimpleDateFormat dateFormater(String pattern)
	{
		SimpleDateFormat formater = (SimpleDateFormat) dateFormater.get( pattern );
		if(formater == null)
		{
			formater = new SimpleDateFormat( pattern );
		}
		return formater;
	}

}
