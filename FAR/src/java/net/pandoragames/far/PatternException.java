package net.pandoragames.far;

/**
 * Exception that indicates an invalid search pattern or 
 * invalid regular expression.
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
public class PatternException extends RuntimeException {

    private String pattern;
    
    /**
     * Standard constructor.
     * @param pattern the pattern that caused this exception
     * @param message additional error message for logging
     */
    public PatternException(String pattern, String message) {
        super(message);
        this.pattern = pattern;
    }
    
    /**
     * Constructor for underlying <code>java.util.regex.PatternSyntaxException</code> 
     * or <code>java.text.ParseException</code>.
     * @param pattern the pattern that caused this exception
     * @param t the underlying exception
     */
    public PatternException(String pattern, Throwable t) {
        super(t.getMessage(), t);
        this.pattern = pattern;
    }

    /**
     * Returns the pattern that caused this exception.
     * @return the pattern that caused this exception
     */
    public String getPattern() {
        return pattern;
    }
}
