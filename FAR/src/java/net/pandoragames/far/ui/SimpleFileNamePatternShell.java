package net.pandoragames.far.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.pandoragames.far.PatternException;

/**
 * Alternative implementation of the <code>SimpleFileNamePattern</code> class
 * mimicking POSIX shell wildcards:
 * <p>
 * The simple pattern may contain any character greater #31, except the characters '/','\',':' and ';'
 * ('\' may however be used to escape the special characters listed below).
 * In addition, the platform file separator (/ on *X,  \ on Windows)
 * and the platform file-name separator (: on *X, ; on Windows) are not permitted.
 * <p>
 * The characters '*', '?', '[' (']') and '{' ('}') have special meaning.
 * The asterisk '*' represents any number of characters (including zero) and 
 * '?' represents exactly one character.<br>
 * '[rgb]' matches the given set of characters (a character "class"), '[!rgb]' matches 
 * any character <b>not</b> in the set (negation) and '[a-z]' matches a range.<br>
 * '{either,or}' defines an alternative.
 * <p>
 * Special characters may be escaped using the backslash (\). 
 * White space at both ends of a simple pattern is of corse discarded.
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
public class SimpleFileNamePatternShell extends SimpleFileNamePattern {

	private static final char[] metaCharactersInSimplePattern = new char[]{'*', '?', '[', ']', '{', '}'};
	
	private Set<Character> metaCharacters = new HashSet<Character>();
	/**
	 * Private singleton constructor.
	 */
	private SimpleFileNamePatternShell() {		
        for(char character : metaCharactersInSimplePattern) {
        	metaCharacters.add(Character.valueOf(character));
        }    	
	}
	
    /**
     * Sets this class as the default returned by 
     * <code>SimpleFileNamePattern.getInstance()</code>.
     */
    public static void setDefault() {
    	SimpleFileNamePattern.setInstance( new SimpleFileNamePatternShell() );
    }

	
	@Override
	public Pattern createPattern(String simplePattern, boolean ignoreCase) throws PatternException {
        if(simplePattern == null) throw new NullPointerException("Pattern must not be null");
        StringBuilder buffer = new StringBuilder("");
        String pattern = simplePattern.trim();

        parseSimplePattern( pattern, buffer );
        
        try {
            if( ignoreCase ) {
                return Pattern.compile(buffer.toString(), Pattern.CASE_INSENSITIVE);
            } else {
                return Pattern.compile(buffer.toString());
            }
        } catch (PatternSyntaxException psx) {
            throw new PatternException(simplePattern, psx.getMessage());
        }	
    }
	
	private void parseSimplePattern(String pattern, StringBuilder buffer) throws PatternException {
        int index = 0;
        while(index < pattern.length()) {
        	char c = pattern.charAt( index );
        	if( isEscapeConstruct( pattern, index ) ) {
        		buffer.append("\\").append( pattern.charAt( index + 1) );
            	index++;
        	} else if(forbiddenCharacters.contains(Character.valueOf(c))) {
        		throw new PatternException(pattern, "Character '" + c + "' not allowed in filename pattern");
        	} else if(c < ' ') {
        		throw new PatternException(pattern, "Character values below u20 (blank, ascii 32) not allowed in filename pattern");
            } else if ( c == '*' ) {
                buffer.append(".*");
            } else if ( c == '?' ) {
                buffer.append(".");
            } else if ( c == '[' ) {
            	int end = findMatchingBracket(pattern, index);
            	parseCharacterClass( pattern.substring(index + 1, end), buffer );
            	index = end;
            } else if ( c == '{' ) {
            	int end = findMatchingBracket(pattern, index);
            	parseAlternative( pattern.substring(index + 1, end), buffer );
            	index = end;
            } else if ( toBeEscaped.contains(Character.valueOf(c)) ) {
                buffer.append("\\").append( c );
            } else {
            	buffer.append( c );
            }
        	index++;
        }
	}
	
	/**
	 * Returns true if the character at index position escapes its successor.
	 * More precisely true is returned if the character at position <i>index</i>
	 * is '\' (backslash), has a successor (is not the end of the string), 
	 * and is succeeded by a meta character, i.e. '*', '?', '[', ']', '{' or '}'.
	 * @param pattern containing the questioned character
	 * @param index character to be tested
	 * @return true if the character at index position escapes its successor
	 */
	private boolean isEscapeConstruct(String pattern, int index) {
		return pattern.charAt(index) == '\\' 
    		&& index < pattern.length() - 1
    		&& metaCharacters.contains(Character.valueOf( pattern.charAt( index + 1) ));
	}

	/**
	 * Finds the position of the closing bracket to a given opening bracket.
	 * This method currently recognises only '[' and '{' as bracket.
	 * @param string containing the brackets
	 * @param index position of the opening bracket
	 * @return position of the closing bracket
	 * @throws PatternException if the bracket is not matched
	 * @throws IllegalArgumentException if the character at the index position is
	 * not recognised as a bracket.
	 */
	private int findMatchingBracket(String string, int index) throws PatternException {
		char opening = string.charAt(index);
		char closing = '0';
		if( opening == '[') {
			closing = ']';
		} else if( opening == '{') {
			closing = '}';
		} else {
			throw new IllegalArgumentException("Character '" + opening + "' is not a meta character bracket");
		}
		int count = 1;
		int pos = index;
		while( count > 0 && pos < string.length() ) {
			pos++;
			if( isEscapeConstruct( string, pos ) ) {
				pos++;
			} else {
				char c = string.charAt( pos );
				if(c == opening) count++;
				if(c == closing) count--;
			}
		}
		if(count > 0) {
			throw new PatternException( string, "Unmatched opening bracket '" + opening + "'" );
		}
		return pos;
	}
	
	private void parseCharacterClass(String pattern, StringBuilder buffer) throws PatternException {
		if(pattern.length() == 0) return;
		buffer.append("[");
		boolean negate = (pattern.charAt(0) == '!');
		if( negate ) buffer.append("^");
		int index = negate ? 1 : 0;
		while( index < pattern.length() ) {
        	char c = pattern.charAt( index );
        	if( isEscapeConstruct( pattern, index ) ) {
        		buffer.append("\\").append( pattern.charAt( index + 1) );
            	index++;
        	} else if(forbiddenCharacters.contains(Character.valueOf(c))) {
        		throw new PatternException(pattern, "Character '" + c + "' not allowed in filename pattern");
        	} else if(c < ' ') {
        		throw new PatternException(pattern, "Character values below u20 (blank, ascii 32) not allowed in filename pattern");
            } else if ( c == '-' ) { // otherwise escaped later
                buffer.append("-");
            } else if ( c == '[' ) {
            	int end = findMatchingBracket(pattern, index);
            	parseCharacterClass( pattern.substring(index + 1, end), buffer );
            	index = end;
            } else if( metaCharacters.contains(Character.valueOf(c)) ) {
            	throw new PatternException(pattern, "Meta character '" + c + "' must be escaped with '\\'");
            } else if ( toBeEscaped.contains(Character.valueOf(c)) ) {
                buffer.append("\\").append( c );
            } else {
            	buffer.append( c );
            }
        	index++;			
		}
		buffer.append("]");
	}
	
	private void parseAlternative(String pattern, StringBuilder buffer) throws PatternException {
		if(pattern.length() == 0) return;
		int start = 0;
		int index = 0;
		List<String> tokenlist = new ArrayList<String>();
		while( index < pattern.length() ) {
			char c = pattern.charAt( index );
			if( c == ',' ) {
				if(index > start) tokenlist.add( pattern.substring(start, index));
				start = index + 1;
			} else if( c == '{' ) {
				index = findMatchingBracket(pattern, index);
			}
			index++;	
		}
		tokenlist.add( pattern.substring(start, index)); // add the last part
		
		// process the tokens
		buffer.append("(");
		for(int i = 0; i < tokenlist.size(); i++) {
			buffer.append("(");
			parseSimplePattern( tokenlist.get(i), buffer );
			buffer.append(")");
			if( i < tokenlist.size() - 1 ) buffer.append("|");
		}
		buffer.append(")");
	}
}
