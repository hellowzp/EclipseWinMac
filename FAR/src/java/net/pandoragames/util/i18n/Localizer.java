package net.pandoragames.util.i18n;

import java.util.Date;
import java.util.Locale;



/**
 * This interface describes how to bundle standard support for the locale sensitive
 * rendering of Java objects and application internal message codes in the form of
 * human readable text, brief, for "localization".<br>
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
 * 
 * <!-- CopyrightNote -->
 */
public interface Localizer
{
	
	/**
	 * Returns the java.util.Locale object that is currently used for formatting
	 * @return currently used Locale
	 */
	public Locale getLocale();
	
	/**
	 * Returns the ISO language code of the java.util.Locale object currently used for rendering
	 * @return ISO language code
	 */
	public String getLanguage();
	
	/**
	 * Returns some locale specific, human readable text for the specified code, 
	 * or the code unchanged if no such translation could be done,
	 * @param code application internal code for some text
	 * @return human readable text (or supplied code)
	 */
	public String localize(String code);
	

	/**
	 * Returns some locale specific, human readable text for the specified code, 
	 * filled with the specified replacement value using java.text.MessageFormat syntax.
	 * This is a convinience shortcut for the multi object 
	 * {@link #localize(String, Object[]) localize()} method.
	 * @see #localize(String, Object[])
	 * @param code application internal code for some text
	 * @param replacementValue object that shall be filled into the place holder of the resulting text.
	 * @return human readable text (or null)
	 */
	public String localize(String code, Object replacementValue);

	/**
	 * Returns some locale specific, human readable text for the specified code, 
	 * filled with the specified replacement values using java.text.MessageFormat syntax.
	 * @see java.text.MessageFormat
	 * @param code application internal code for some text
	 * @param replacementValues objects that shall be filled into the place holder of the resulting text.
	 * @return human readable text (or null)
	 */
	public String localize(String code, Object[] replacementValues);
	
	/**
	 * Returns a String representing the specified long value and rendered for the currently
	 * used Locale of this Localizer instance.
	 * @param number value to be rendered.
	 * @return locale specific representation of the specified long value
	 */
	public String localize(long number);
	
	/**
	 * Renders the specified double value with <i>exactly</i> the number of specified
	 * digits and according to the rulse imposed by the currently used Locale object of
	 * this Localizer instance.
	 * @param number value to be rendered.
	 * @return locale specific representation of the specified double value with the indicated
	 * number of digits
	 */
	public String localize(double number, int digits);
		
	/**
	 * Returns a String representing the specified Date object using the format pattern that is represented
	 * by the specified code. The <i>patterCode</i> argument is not directly used as a date pattern,
	 * it rather represents a date patern in the Locale of this instance.
	 * @param date date to be rendered.
	 * @param patternCode refering a special date pattern
	 * @return locale specific representation of the specified Date object
	 */
	public String localize(Date date, String patternCode);
		
	/**
	 * Returns the int value that is represented by the specified String, or throws
	 * a ParseException if parsing fails.
	 * @param integerNumber String that represents an integer value
	 * @return integer value represented by the specified String, or null if the String could not be parsed
	 */
	public Integer parseInt(String integerNumber);
	
	/**
	 * Returns the long value that is represented by the specified String, or throws
	 * a ParseException if parsing fails.
	 * @param longNumber String that represents a long value
	 * @return long value represented by the specified String, or null if the String could not be parsed
	 */
	public Long parseLong(String longNumber);
	
	/**
	 * Returns the double value that is represented by the specified String, or throws
	 * a ParseException if parsing fails.
	 * @param doubleNumber String that represents a double value
	 * @return double value represented by the specified String, or null if the String could not be parsed
	 */
	public Double parseDouble(String doubleNumber);
	
	/**
	 * Returns the java.util.Date object  that is represented by the specified String. 
	 * The <i>patterCode</i> argument is not directly used as a date pattern,
	 * it rather represents a date patern in the Locale of this instance.
	 * @param date String that represents a date
	 * @param patternCode refering a special date pattern
	 * @return java.util.Date object represented by the specified String, or null if the String could not be parsed
	 */
	public Date parseDate(String date, String patternCode);

}
