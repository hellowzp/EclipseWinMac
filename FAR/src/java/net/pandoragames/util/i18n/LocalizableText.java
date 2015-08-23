package net.pandoragames.util.i18n;

import java.io.Serializable;
import java.util.Arrays;


/**
 * This object represents a little piece of textual information that can be transformed from some
 * application internal code into a human readable text of a specific language, say it
 * can be <i>localized</i>.
 * While the transformation from code into text is generally a static one-to-one replacement,
 * the resulting text may be filled with variable information, using the means provided
 * by the java.text.MessageFormat class.<br>
 * The class should be considdered immutable in so far, that code and replacement objects
 * can not be changed once the object is created.
 * @see java.text.MessageFormat
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
public class LocalizableText implements Serializable, Cloneable
{
	private String textCode;
	private String text;
	private Object[] variables;
	private String localeID;
	
	/**
	 * Creates a new instance withe the specified message code
	 * @param code application internal code for some message text
	 */
	public LocalizableText(String code)
	{
		this(code, null);
	}
	
	/**
	 * Creates a new instance withe the specified message code and one replace
	 * ment value.
	 * @param code application internal code for some message text
	 * @param replacementValue used with  java.text.MessageFormat
	 */
	public LocalizableText(String code, Object replacementValue)
	{
		this(code, new Object[]{replacementValue});
	}
	
	/**
	 * Creates a new instance withe the specified message code and any
	 * number of replacement values.
	 * @param code application internal code for some message text
	 * @param replacementValues used with  java.text.MessageFormat
	 */
	public LocalizableText(String code, Object[] replacementValues)
	{
		if(code == null) throw new NullPointerException("Parameter 'code' must not be null");
		
		variables = replacementValues;
		textCode = code;		
	}
	
	/**
	 * Returns the application internal code for this text object
	 */
	public String getCode()
	{
		return textCode;
	}
	
	/**
	 * Returns the localized text. If localization failed or took not yet place,
	 * the original code is returned. 
	 * @return text for display to human user
	 */
	public String getText()
	{
		if(text != null) return text;
		
		return textCode;
	}
	
	/**
	 * Trys to translate the code into some human readable text using the specified
	 * Localizer. If this object has allready been localized with the language or the
	 * specified localizer, the call to this method is ignored.
	 */
	public void localize(Localizer loco)
	{
		if((localeID == null) || (!localeID.equals(loco.getLanguage())))
		{
			if(variables == null)
				text = loco.localize(textCode);
			else
				text = loco.localize(textCode, variables);
			
			localeID = loco.getLanguage();
		}
	}
	
	/**
	 * Returns the two letter language code this textobject was localized with
	 * or null, if the textobject wasn't yet localized.
	 */
	public String getLanguage()
	{
		return localeID;
	}
	
	/**
	 * Returns the short name of this class followed by code and language
	 */
	public String toString()
	{
		return "LocalizableText[code="+textCode+", lang='"+localeID+"']";
	}

	/**
	 * Returns a 1:1 deep copy of this object.
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		try
		{
			LocalizableText clown = (LocalizableText) super.clone();
			if( variables != null ) clown.variables = (Object[]) variables.clone();
			return clown;
		}
		catch(CloneNotSupportedException cnsx)
		{
			throw new IllegalStateException(this.getClass().getName()+" surprisingly threw CloneNotSupportedException: "+cnsx.getMessage());
		}
	}

	/**
	 * Inheriting classes may have to have access to the replacement values of this
	 * object, but they should never be altered and are therefor sealed with a protected
	 * accessor.
	 * @return Returns the replacement variables that should be inserted in the localized
	 * text using java.text.MessageFormat
	 */
	protected Object[] getVariables()
	{
		return variables;
	}
	
// -- equals an hashcode ---------------------------------------------------------------------------------------------
	
	
	/**
	 * Two instances of LocalizableText are considdered equal if they carry the same code,
	 * and the same set of replacement objects. 
	 */
	public boolean equals(Object o)
	{
		if (o == null) return false;
		
		try
		{
			LocalizableText other = (LocalizableText) o;
			
			if(! textCode.equals( other.textCode )) return false;
			
			return Arrays.equals(variables, other.variables);
		}
		catch(ClassCastException ccx)
		{
			return false;
		}
	}
	
	/**
	 * Overridden to be consistent with equals;
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		int hc = textCode.hashCode();
		hc = 31 * hc + Arrays.hashCode(variables);
		return hc;
	}
}
