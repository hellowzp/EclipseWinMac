package net.pandoragames.far.ui.model;

import java.util.regex.Pattern;

/**
 * Superclass for form data of FIND and REPLACE operation.
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
public class FARForm extends OperationForm {

	
	// form data
	private String searchStringContent  = "";
	private boolean ignoreCase = false;
	private boolean regexContentPattern = true;
	private boolean invertContentFilter = false;
	// generated element
	private transient Pattern regexPattern;
	
	/**
	 * Protected constructor for inheriting classes.
	 * Child classes should provide a public no argument constructor.
	 * @param formType inheritors specify their type (one of the constants of this interface)
	 */
	protected FARForm(OperationType formType) {
		super( formType );
	}
	
	
	/**
	 * Returns the specified content pattern string as a java regex object.
	 * If the pattern spans multiple lines, each line break will be replaced by
	 * a soffisticated line break matching regex. If the pattern is to be taken literaly
	 * it is transformed into a coressponding regex.
	 * @return content pattern string as a java regex object
	 * @throws java.util.regex.PatternSyntaxException if the pattern can not be
	 * compiled into a regular expression.
	 */
	public Pattern getContentPatternAsRegex() {
		if( regexPattern == null ) {
			StringBuffer patternString = new StringBuffer();
			String[] patternLines = getSearchStringContent().split("\n");
			// compile the pattern
			for(int i = 0; i < patternLines.length; i++) {
				if( i > 0 ) patternString.append("$\r?(?s:.)^");
				if( isRegexContentPattern() ) {
					patternString.append( patternLines[i] );						
				} else {
					// if the string is to be taken literarily (no regex) replace all special chars
					patternString.append( patternLines[i].replaceAll("(\\W)", "\\\\$1") );
				}
			}
			int flagset = Pattern.MULTILINE;
			// flagset = flagset | Pattern.DOTALL;
			if( isIgnoreCase() ) flagset = flagset | Pattern.CASE_INSENSITIVE;
			regexPattern = Pattern.compile( patternString.toString(), flagset );
		}
		return regexPattern;
	}
	
	/**
	 * Returns true if the {@link #setSearchStringContent(String) content pattern} 
	 * should be interpreted as regular expression.
	 * @return is content pattern a regular expression
	 */
	public boolean isRegexContentPattern()
	{
		return regexContentPattern;
	}

	/**
	 * Specify whether the {@link #setSearchStringContent(String) content pattern} 
	 * should be interpreted as regular expression.
	 * @param regexContentPattern is content pattern a regular expression
	 */
	public void setRegexContentPattern(boolean regexContentPattern)
	{
		if(this.regexContentPattern != regexContentPattern) regexPattern = null;
		this.regexContentPattern = regexContentPattern;
	}

	/**
	 * Returns true if the {@link #setSearchStringContent(String) content pattern} 
	 * should be interpreted as being not case sensitive.
	 * @return is content pattern case insensitive
	 */
	public boolean isIgnoreCase()
	{
		return ignoreCase;
	}
	
	/**
	 * Set to true true if the {@link #setSearchStringContent(String) content pattern} 
	 * should be interpreted as being not case sensitive.
	 * @param ignore is content pattern case insensitive
	 */
	public void setIgnoreCase(boolean ignore)
	{
		if(ignoreCase != ignore) regexPattern = null;
		this.ignoreCase = ignore;
	}
	
	/**
	 * Returns the content pattern. This string is looked up <i>within</i> the files.
	 * @return content pattern, possibly empty, never null
	 */
	public String getSearchStringContent()
	{
		return searchStringContent;
	}
	
	/**
	 * Sets the content pattern. This string is looked up <i>within</i> the files.
	 * The method ignores null values.
	 * @param searchRegexString content pattern (ignores null)
	 */
	public void setSearchStringContent(String searchRegexString)
	{
		if( searchRegexString != null ) {
			regexPattern = null;
			this.searchStringContent = searchRegexString;
		}
	}

	/**
	 * Returns false if the content pattern is applied as an inclusion filter
	 * (the default). If the method returns true, files matching the
	 * pattern will be <b>excluded</b> from the selection.
	 * @return false to include files matching the content pattern, true to exclude them 
	 */
	public boolean isInvertContentFilter() {
		return invertContentFilter;
	}
	
	/**
	 * Set this flag to true to exclude files that match the content pattern
	 * from the file list. By default this flag is disabled (false)
	 * @param invertContentFilter set to false to make content pattern an exclusion filter
	 */
	public void setInvertContentFilter(boolean invertContentFilter) {
		this.invertContentFilter = invertContentFilter;
	}	
	
	/**
	 * Resets the search string to the empty string.
	 */
	public void reset() {
		setSearchStringContent("");
	}
	
	/**
	 * Updates this form with the data from the "form" parameter.
	 * Inheriting classes must take care to call this method
	 * <b>after</b> they have performed the update.
	 * @param form data to replace the content of this form
	 */
	protected void update(FARForm form) {
		ignoreCase = form.ignoreCase;
		invertContentFilter = form.invertContentFilter;
		regexContentPattern = form.regexContentPattern;
		searchStringContent = form.searchStringContent;
		super.update( form );
	}

	
// -- equals & hashcode -------------------------------------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object o) {
		if( ! super.equals(o) ) return false; 
		try {
			FARForm form = (FARForm) o;
			class Diff {
				private boolean different(Object a, Object b) {
					if( a == null ) {
						return (b != null);
					} else {
						return ! a.equals( b );
					}
				}
			}
			Diff diff = new Diff();
			if( diff.different( searchStringContent, form.searchStringContent ) ) return false;
			if( ignoreCase != form.ignoreCase ) return false;
			if( regexContentPattern != form.regexContentPattern ) return false;
			return true;
		} catch (ClassCastException ccx) {
			return false;
		}
	}
		
	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + ( ignoreCase ? 1 : 0 );
		hash = hash * 31 + ( regexContentPattern ? 1 : 0 );
		hash = hash * 31 + ( searchStringContent != null ? searchStringContent.hashCode() : 0 );
		return hash;		
	}
}
