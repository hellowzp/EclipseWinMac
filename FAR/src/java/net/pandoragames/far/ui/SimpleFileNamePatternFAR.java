package net.pandoragames.far.ui;

import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.pandoragames.far.PatternException;

/**
 * Default implementation of the <code>SimpleFileNamePattern</code> class
 * imposing the following rules:
 * <p>
 * The simple pattern may contain any character  greater #31, except the characters '/','\',':' and ';'.
 * In addition, the platform file separator (/ on *X,  \ on Windows)
 * and the platform file-name separator (: on *X, ; on Windows) are not permitted.
 * <p>
 * The characters '*', '?', '!', '#' and ',' have special meaning.
 * The asterisk '*' represents any number of characters (including zero), 
 * '?' represents any single character or none, '!' represents exactly one character,
 * '#' exactly one digit (0-9) and ',' serves as pattern seperator (a logical OR).
 * <p>
 * If a simple pattern starts with a dot '.', this is considdered equivalent to '*.'.
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
public class SimpleFileNamePatternFAR extends SimpleFileNamePattern {

	/**
	 * Default access singleton constructor.
	 */
    SimpleFileNamePatternFAR() {
    }
    
    /**
     * Sets this class as the default returned by 
     * <code>SimpleFileNamePattern.getInstance()</code>.
     */
    public static void setDefault() {
    	SimpleFileNamePattern.setInstance( new SimpleFileNamePatternFAR() );
    }
    
    /**
     * Create a <code>java.util.regex.Pattern</code> from a simple string pattern. 
     * See class description for details on the syntax of the simple pattern.
     * @param simplePattern see class description
     * @param ignoreCase should letter case be considdered
     * @return Pattern object for specified simple pattern string
     * @throws PatternException if the simple pattern is not valid
     */
    public Pattern createPattern(String simplePattern, boolean ignoreCase ) throws PatternException {
        if(simplePattern == null) throw new NullPointerException("Pattern must not be null");
        StringTokenizer tokenizer = new StringTokenizer(simplePattern, ",");
        StringBuffer buffer = new StringBuffer("(");
        while(tokenizer.hasMoreTokens()) {
        	String singlePattern = tokenizer.nextToken().trim();
	        if( singlePattern.length() == 0 ) buffer.append(".*");
	        for(int i = 0; i < singlePattern.length(); i++) {
	            char c = singlePattern.charAt(i);
	            if(forbiddenCharacters.contains(Character.valueOf(c))) throw new PatternException(singlePattern, "Character '" + c + "' not allowed in filename pattern");
	            if(c < ' ') throw new PatternException(singlePattern, "Character values below u20 (blank, ascii 32) not allowed in filename pattern");
	            if((i == 0) && (c == '.')) {
	                buffer.append(".*\\.");
	            } else if ( c == '*' ) {
	                buffer.append(".*");
	            } else if ( c == '?' ) {
	                buffer.append(".?");
	            } else if ( c == '!' ) {
	                buffer.append(".");
	            } else if ( c == '#' ) {
	            	buffer.append("\\d");
	            } else if ( toBeEscaped.contains(Character.valueOf(c)) ) {
	                buffer.append("\\").append( c );
	            } else {
	            	buffer.append( c );
	            }
	        }
	        if(tokenizer.hasMoreTokens()) {
	        	buffer.append(")|(");
	        }
        }
        buffer.append(")");
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

}
