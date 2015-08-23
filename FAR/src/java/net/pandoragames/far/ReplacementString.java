package net.pandoragames.far;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a replacement string for use with regex patterns. Such strings may contain
 * references to capturing groups in the pattern of the form 'x#', where 'x' stands for some
 * character called the "group reference indicator" and # is a sequence of digits.<p>
 * 
 * Unless specified otherwise, the default group reference indicator is '$'. It may be escaped
 * using the backslash '\' and will appear as such in the <i>canonical</i> string of this object.
 * However, instead of escaping '$', other characters may be used as group reference indicator,
 * typically the backslash it self is used for this purpose.
 * @author Olivier Wehner at 23.02.2008
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
public class ReplacementString implements Cloneable
{
	
	private static char defaultGroupReferenceIndicator = '$';
	private String originalString;
	private String canonicalString;
	private char groupReferenceIndicator;
	private int maxGroupReference;

	/**
	 * Creates a new ReplacementString with the specified pattern and the current 
	 * default group reference indicator. The use of this constructor in multi thread 
	 * environments is strongly discouraged, as the default group reference indicator 
	 * may be changed by another thread any time.
	 * @param replacementString with capturing group references
	 */
	public ReplacementString(String replacementString)
	{
		this( replacementString, defaultGroupReferenceIndicator );
	}
	
	/**
	 * Creates a new ReplacementString with the specified pattern and group reference indicator.
	 * Letters, digits or whitespace are <b>not</b> allowed as group reference indicator.
	 * @param replacementString with capturing group references
	 * @param groupReference group reference indicator
	 */
	public ReplacementString(String replacementString, char groupReference) {
		if( replacementString == null ) throw new NullPointerException("Replacement string must not be null");		
		testGroupReferenceIndicator( groupReference );
		if( '$' == groupReference ) {
			canonicalString = replacementString;
		} else {
			canonicalString = dollarReplacementString(replacementString, groupReference);
		}
		maxGroupReference = maxGroupReference( canonicalString );
		originalString = replacementString;
		groupReferenceIndicator = groupReference;
	}
	
	/**
	 * Sets the default group reference indicator that applies for new instances created 
	 * with the single argument constructor. Letters, digits or whitespace are <b>not</b>
	 * allowed as group reference indicator.
	 * @param groupReference default group reference indicator
	 */
	public static void setDefaultGroupReferenceIndicator(char groupReference) {
		testGroupReferenceIndicator( groupReference );
		defaultGroupReferenceIndicator = groupReference;
	}
	
    /**
     * Verifies that this replacement string may be applied to the regex pattern.
     * The method fails if the highest group reference in the replacement string exceeds
     * the number of capturing groups defined in the pattern
     * @param pattern possibly containing capturing groups
     * @return true if replacement may be applied to pattern
     */
    public boolean doesApply(Pattern pattern) {
    	return getMaxGroupReference() <= pattern.matcher("").groupCount();
    }

	
	/**
	 * Returns the highest group reference in the replacement pattern. 
	 * @return highest group reference in the replacement pattern 
	 */
	public int getMaxGroupReference() {
		return maxGroupReference;
	}
	
	/**
	 * Returns the canonical form of this replacement string object using '$' as group reference indicator.
	 * @return canonical replacement string
	 */
	public String getCanonicalString() {
		return canonicalString;
	}
	
	/**
	 * Returns the replacement string as it was supplied with the constructor.
	 * @return original replacement string
	 */
	public String getOriginalString() {
		return originalString;
	}
	
	/**
	 * Returns the group reference indicator that was used in the 
	 * original replacement string.
	 * @return group reference indicator
	 */
	public char getGroupReferenceIndicator() {
		return groupReferenceIndicator;
	}
	
	/**
	 * Returns a string representation of this object, actually the canonical form of the replacement string.
	 * @return string representation of this object
	 */
	public String toString() {
		return canonicalString;
	}

// -- private methods --------------------------------------------------------------------------------------------	
	
    /**
     * Creates a replacement string that uses '$' as group reference from a replacement string that uses '\'. 
     * While the latter is usefull for replacement patterns of file names (allowing '$' to be an 
     * unescaped part of the file name pattern), the former has to be used with the java
     * Regex API.
     * @param backslashReplacementString regex replacement string using '\#' as greoup reference.
     * @return replacement string with '$' as group reference indicator, all literal  '$' being escaped.
     */
    static String dollarReplacementString(String backslashReplacementString, char groupReference) {
    	// this method has default acces to allow for testing
    	String findGroupReference = "\\" + String.valueOf(groupReference) + "(\\d)";
    	return backslashReplacementString.replace("$", "\\$").replaceAll( findGroupReference, "\\$$1");
    }
    
    private static void testGroupReferenceIndicator(char gri) {
    	if( gri < ' ' ) throw new IllegalArgumentException("Character values below u20 (blank, ascii 32) not allowed as group reference indicator");
    	if( Character.isLetterOrDigit( gri )) throw new IllegalArgumentException("Characters (" + String.valueOf(gri) +
																										")may not be used as group reference indicator.");
    	if( Character.isWhitespace( gri )) throw new IllegalArgumentException("Whitespace is not allowed as group reference indicator");
    }

    /**
     * Finds the highest group reference in the replacement pattern. This is the larges
     * digit that stands directly behind an unescaped '$' character.
     * @param replacementString containing  '$' group references (or none)
     * @return highest reference number
     */
    private int maxGroupReference(String replacementString) {
    	int count = 0;
    	Matcher matcher = Pattern.compile("(\\A|[^\\\\])\\$(\\d*)").matcher( replacementString );
    	while(matcher.find()) {
    		String digit = matcher.group(2);
    		count = Math.max( count, Integer.parseInt( digit ));
    	}
    	return count;
    }
    
	/**
	 * Returns a copy of this object.
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch(CloneNotSupportedException cnsx) {
			throw new IllegalStateException("A clown refused to clone!");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object o) {
		if( o == null ) return false;
		try {
			ReplacementString other = (ReplacementString) o;
			if( other.groupReferenceIndicator != groupReferenceIndicator ) return false;
			return other.originalString.equals( originalString );
		} catch (ClassCastException ccx) {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		int hash = originalString.hashCode();
		hash = hash + 31 * ((int) groupReferenceIndicator);
		return hash;
	}
}
