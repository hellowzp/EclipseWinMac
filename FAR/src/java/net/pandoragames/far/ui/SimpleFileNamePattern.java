package net.pandoragames.far.ui;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.pandoragames.far.PatternException;
import net.pandoragames.far.ui.model.FileNamePattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility for simplified regular expressions. At several places, especially for the
 * selection of file names, the application will accept a <i>simple pattern</i> as
 * alternative to an ordinary regular expression. 
 * A simple pattern is a simplified regular expression syntax,
 * typically allowing <b>*</b> as a wild card for "any number of characters" 
 * (in contrast to <b>.*</b> in proper regular expressions). 
 * <p>
 * The default implementation for a simple pattern is 
 * {@link net.pandoragames.far.ui.SimpleFileNamePatternFAR SimpleFileNamePatternFAR},
 * but an alternative
 * {@link net.pandoragames.far.ui.SimpleFileNamePatternShell SimpleFileNamePatternShell}
 * is provided and others could be added. See the respective implementations for
 * details of the pattern syntax.
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
public abstract class SimpleFileNamePattern {

    private static final char[] forbiddenCharactersInSimplePattern = new char[]{'/', '\\', ';', ':'};
    private static final char[] toBeEscapedInSimplePattern = new char[]{'.', '^', '$', '+', '-', '*', '?',
                                                                    '(', ')', '[', ']', '{', '}', '|'};
    /**
     * Set of characters that should not be tolerated in a simple pattern.
     * This set contains the characters '/','\',':' and ';'.
     * In addition, the platform file separator (/ on *X,  \ on Windows)
     * and the platform file-name separator (: on *X, ; on Windows) are not permitted.
     * Implementations should not allow characters below below u20 (blank, ascii 32) either.
     */
    protected Set<Character> forbiddenCharacters = new HashSet<Character>();
    /**
     * Set of characters that must be escaped when producing a regular expression.
     * The set contains the following characters:
     * '.', '^', '$', '+', '-', '*', '?', '(', ')', '[', ']', '{', '}' and '|'.
     */
    protected Set<Character> toBeEscaped = new HashSet<Character>();

	/**
	 * Never leave without a logger.
	 */
    protected Log logger = LogFactory.getLog( this.getClass() );
    
    private static SimpleFileNamePattern singleton = new SimpleFileNamePatternFAR();
    
    /**
     * Protected constructor for inheriting classes.
     */
    protected SimpleFileNamePattern() {
        for(char character : forbiddenCharactersInSimplePattern) {
            forbiddenCharacters.add(Character.valueOf(character));
        }
        forbiddenCharacters.add( Character.valueOf(System.getProperty("file.separator").charAt(0)) );
        forbiddenCharacters.add( Character.valueOf(System.getProperty("path.separator").charAt(0)) );
        for(char character : toBeEscapedInSimplePattern) {
            toBeEscaped.add(Character.valueOf(character));
        }    	
    }    
    
    /**
     * Returns an instance of this utility class.
     * By default this returns an instance of class
     * {@link net.pandoragames.far.ui.SimpleFileNamePatternFAR SimpleFileNamePatternFAR}.
     * This behavior can be changed by calling the <code>setInstance()</code> method.
     * @return instance of this class
     */
    public static SimpleFileNamePattern getInstance() {
    	return singleton;
    }
    
    /**
     * Sets a new implementation of this class as to be returned by the 
     * <code>getInstance()</code> method. Null values will be ignored.
     * @param instance new default instance for <code>getInstance()</code> method.
     */
    protected static void setInstance(SimpleFileNamePattern instance) {
    	if( instance != null ) singleton = instance;
    }
    
    /**
     * Create a <code>java.util.regex.Pattern</code> from a simple string pattern. 
     * The syntax of the simple pattern is implementation dependend.
     * @param simplePattern see class description
     * @param ignoreCase should letter case be considered
     * @return Pattern object for specified simple pattern string
     * @throws PatternException if the simple pattern is not valid
     */
    public abstract Pattern createPattern(String simplePattern, boolean ignoreCase ) throws PatternException;
    
    
    /**
     * Validates the specified FileNamePattern for syntactically correctness.
     * @param pattern to be validated
     * @return true if pattern is ok
     */
    public boolean validateFileNamePattern(FileNamePattern pattern) {
    	if(pattern == null) {
    		logger.warn("FileNamePattern object is null");
    		return false;
    	}
    	if(pattern.getPattern() == null) {
    		logger.warn("Pattern string is null");
    		return false;
    	}
		if( pattern.isRegex() ) {
			try {
				return (null != Pattern.compile( pattern.getPattern() ));
			} catch (PatternSyntaxException psx) {
				logger.info( psx.getMessage() );
				return false;
			}
		} else {
			try {
				return (null != createPattern(pattern.getPattern(), true ));
			} catch(PatternException px) {
				logger.info( px.getMessage() );
				return false;
			}
		}
	}
}
