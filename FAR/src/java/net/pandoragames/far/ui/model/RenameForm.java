package net.pandoragames.far.ui.model;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.pandoragames.far.PatternException;
import net.pandoragames.far.ReplacementString;
import net.pandoragames.far.ui.SimpleFileNamePattern;

/**
 * Defines the parameter for a RENAME operation.
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
public class RenameForm extends OperationForm {

	public enum CASEHANDLING {
		/** Transform to all upper case.*/
		UPPER, 
		/** Transform to all lower case.*/
		LOWER, 
		/** Preserve case.*/
		PRESERVE};
	
	private FileNamePattern fileNamePattern = new FileNamePattern("*");
	
	// protect extension and extension only are mutually exclusive
	private boolean protectExtension = true;
	private boolean extensionOnly = false;
	
	private String replacementString = "";
	private CASEHANDLING treatCase = CASEHANDLING.PRESERVE;
	private boolean preventCaseConflict = true;
	
	// generated element
	private transient ReplacementString replacementPattern;

	
	public RenameForm() {
		super( OperationType.RENAME );
	}
	
	/**
	 * Returns the search pattern as a regular expression object. This includes
	 * the value of the ignoreCase flag, but not the protectExtension flag.
	 * @return search pattern as a regular expression object
	 * @throws PatternException if the pattern is invalid
	 */
	public Pattern getPatternAsRegex() throws PatternException {
		if( fileNamePattern.isRegex() ) {
			try {
				if( fileNamePattern.isIgnoreCase() ) {
					return Pattern.compile( fileNamePattern.getPattern(), Pattern.CASE_INSENSITIVE );
				} else {
					return Pattern.compile( fileNamePattern.getPattern() );
				}
				
			} catch (PatternSyntaxException psx) {
				throw new PatternException( fileNamePattern.getPattern(), psx.getMessage() );
			}
		} else {
			return SimpleFileNamePattern.getInstance().createPattern(fileNamePattern.getPattern(), fileNamePattern.isIgnoreCase() );
		}
	}
	
	/**
	 * Returns true if the pattern string should be 
	 * interpreted as being not case sensitive.
	 * @return true if letter case should be ignored
	 */
	public boolean isIgnoreCase() {
		return fileNamePattern.isIgnoreCase();
	}
	/**
	 * Set to true if the pattern string should be 
	 * interpreted as being not case sensitive.
	 * @param ignoreCase true if letter case should be ignored
	 */
	public void setIgnoreCase(boolean ignoreCase) {
		fileNamePattern.setIgnoreCase( ignoreCase );
	}
	/**
	 * Returns the search pattern as typed by the user.
	 * @return raw search pattern
	 */
	public String getPatternString() {
		return fileNamePattern.getPattern();
	}
	/**
	 * Sets the raw search pattern. This is interpreted with
	 * respect to the flags on this object. 
	 * @param patternString raw search pattern
	 */
	public void setPatternString(String patternString) {
		fileNamePattern.setPattern( patternString );
	}
	/**
	 * Returns true if the application should not allow that two file
	 * names differ only in letter case. In Other words, the rename operation
	 * must not produce files that, when uppercased, carry equal names.
	 * @return true if file names must be substantially different.
	 */
	public boolean isPreventCaseConflict() {
		return preventCaseConflict;
	}
	/**
	 * Set to true if the application should not allow that two file
	 * names differ only in letter case. In Other words, the rename operation
	 * must not produce files that, when uppercased, carry equal names.
	 * @param preventCaseConflict true if file names must be substantially different.
	 */
	public void setPreventCaseConflict(boolean preventCaseConflict) {
		this.preventCaseConflict = preventCaseConflict;
	}
	/**
	 * Returns true if the application shall not modify the file extension.
	 * Everything after the first dot (.) should be considdered as extension.
	 * @return true if extension must not be modified
	 */
	public boolean isProtectExtension() {
		return protectExtension;
	}
	/**
	 * Set to true if the application shall not modify the file extension.
	 * Setting this to true will set the "extensionOnly" property to false.
	 * Everything after the first dot (.) should be considdered as extension.
	 * @param protectExtension true if extension must not be modified
	 */
	public void setProtectExtension(boolean protectExtension) {
		this.protectExtension = protectExtension;
		if( protectExtension ) extensionOnly = false;
	}
	/**
	 * Returns true if the application shall ONLY modify the file extension
	 * but not the file name. Everything after the first dot (.) should be considdered as extension.
	 * @return true if only the extension must be modified
	 */
	public boolean isExtensionOnly() {
		return extensionOnly;
	}
	/**
	 * Set to true if the application shall ONLY modify the file extension
	 * but not the file name. Setting this to true will set the 
	 * "protectExtension" property to false.
	 * Everything after the first dot (.) should be considdered as extension.
	 * @param onlyExtension true if only the extension must be modified
	 */
	public void setExtensionOnly(boolean onlyExtension) {
		extensionOnly = onlyExtension;
		if( onlyExtension ) protectExtension = false;
	}
	/**
	 * Returns true if the pattern string should be interpreted as
	 * a regular expression pattern. If false it will be interpreted as 
	 * a "simple" pattern.
	 * @return true for regex, false for simple pattern interpretation
	 */
	public boolean isRegexPattern() {
		return fileNamePattern.isRegex();
	}
	/**
	 * Set to true if the pattern string should be interpreted as
	 * a regular expression pattern. If false it will be interpreted as 
	 * a "simple" pattern.
	 * @param regexPattern true for regex, false for simple pattern interpretation
	 */
	public void setRegexPattern(boolean regexPattern) {
		fileNamePattern.setRegex( regexPattern );
	}
	/**
	 * The replacement string. 
	 * @return replacement string
	 */
	public String getReplacementString() {
		return replacementString;
	}
	/**
	 * The replacement string. 
	 * @param replacementString replacement string
	 */
	public void setReplacementString(String replacementString) {
		this.replacementString = replacementString;
		replacementPattern = null;
	}
	/**
	 * Returns the replacement string as a ReplacementString object.
	 * @return ReplacementString object.
	 */
	public ReplacementString getReplacementPattern() {
		if( replacementPattern == null ) {
			replacementPattern = new ReplacementString( replacementString, getGroupReference() );
		}
		return replacementPattern;
	}
	/**
	 * Defines how letter case of the result name should be handled. 
	 * @return letter case of result name
	 */
	public CASEHANDLING getTreatCase() {
		return treatCase;
	}
	/**
	 * Sets how letter case of the result name should be handled. 
	 * @param treatCase letter case of result name
	 */
	public void setTreatCase(CASEHANDLING treatCase) {
		this.treatCase = treatCase;
	}

	public void reset() {
		fileNamePattern.setPattern("");
		replacementString = "";
	}
	
	/**
	 * Updates this form with the data from the "form" parameter.
	 * Inheriting classes must take care to call this method
	 * <b>after</b> they have performed the update.
	 * @param form data to replace the content of this form
	 */
	public void update(RenameForm form) {
		fileNamePattern = form.fileNamePattern;
		protectExtension = form.protectExtension;
		preventCaseConflict = form.preventCaseConflict;
		replacementString = form.replacementString;
		treatCase = form.treatCase;
		super.update( form );
	}

	
// -- ekel, haschkeks, clown ---------------------------------------------------------	
	
	/**
	 * Returns a copy of this object.
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		RenameForm clown = (RenameForm) super.clone();
		clown.fileNamePattern = (FileNamePattern) fileNamePattern.clone();
		return clown;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object o) {
		if(! super.equals(o)) return false;
		try {
			RenameForm form = (RenameForm) o;
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
			if( protectExtension != form.protectExtension ) return false;
			if( preventCaseConflict != form.preventCaseConflict ) return false;
			if( diff.different( replacementString, form.replacementString ) ) return false;
			if( diff.different( treatCase, form.treatCase ) ) return false;
			if( diff.different( fileNamePattern, form.fileNamePattern ) ) return false;
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
		hash = hash * 31 + ( protectExtension ? 1 : 0 );
		hash = hash * 31 + ( preventCaseConflict ? 1 : 0 );
		hash = hash * 31 + ( replacementString != null ? replacementString.hashCode() : 0 );
		hash = hash * 31 + ( treatCase != null ? treatCase.hashCode() : 0 );
		hash = hash * 31 + ( fileNamePattern != null ? fileNamePattern.hashCode() : 0 );
		return hash;		
	}
}
