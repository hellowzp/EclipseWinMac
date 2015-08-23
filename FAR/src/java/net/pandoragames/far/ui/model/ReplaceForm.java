package net.pandoragames.far.ui.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.regex.Pattern;

import net.pandoragames.far.ReplacementString;
import net.pandoragames.far.ui.ReplacementDescriptor;

/**
 * Defines the parameter for a replace operation.
 * This form is supposed to be coupled to a {@link FindForm FindForm}
 * by means of its quality as <code>PropertyChangeListener</code>.
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
public class ReplaceForm extends FARForm implements ReplacementDescriptor, PropertyChangeListener {

	final private Pattern linebreakPattern = Pattern.compile("(\\r\\n)|[\\r\\n\\u0085\\u2028\\u2029]");

	private String replacementString = "";
	private File backUpdDirectory;
	private boolean doBackup = true;
	
	// generated element
	private transient ReplacementString replacementPattern;
	private transient String lineBreakUsed;
	
	public ReplaceForm() {
		super( OperationType.REPLACE );
	}
	/**
	 * Returns the replacement string as a ReplacementString object.
	 * @param lineBreak the lineBreak to be used, if null the system 
	 * dependent line separator will be used
	 * @return ReplacementString object or null if no replacement is defined.
	 */
	public ReplacementString getReplacementPattern(String lineBreak) {
		String lb = lineBreak != null ? lineBreak : System.getProperty("line.separator");
		if((replacementPattern == null) || (! lb.equals( lineBreakUsed ))){
			lineBreakUsed = lb;
			String replacementClean = linebreakPattern.matcher( replacementString ).replaceAll( lineBreakUsed );
			replacementPattern = new ReplacementString( replacementClean, groupReference);
		}
		return replacementPattern;
	}
	
	/**
	 * Returns the backup directory.
	 * @see net.pandoragames.far.FileMatcher#setBackUpDirectory(File)
	 * @return backup directory if any
	 */
	public File getBackupDirectory()
	{
		return backUpdDirectory;
	}

	/**
	 * Sets the backup directory.
	 * @see net.pandoragames.far.FileMatcher#setBackUpDirectory(File)
	 * @param backUpdDirectory where to write backups to
	 */
	public void setBackupDirectory(File backUpdDirectory)
	{
		this.backUpdDirectory = backUpdDirectory;
	}
	
	/**
	 * Returns the doBackup flag. Note that the application may choose to
	 * create temporary backups, even if this flag is set to false, in 
	 * order to allow undo operations.
	 * @see net.pandoragames.far.FileMatcher#isDoBackup()
	 * @return whether backups should be created or not
	 */
	public boolean isDoBackup()
	{
		return doBackup;
	}

	/**
	 * Sets the doBackup flag. Note that the application may choose to
	 * create temporary backups, even if this flag is set to false, in 
	 * order to allow undo operations.
	 * @see net.pandoragames.far.FileMatcher#isDoBackup()
	 * @param doBackup whether backups should be created or not
	 */
	public void setDoBackup(boolean doBackup)
	{
		this.doBackup = doBackup;
	}


	/**
	 * Returns the raw replacement string as specified by the user.
	 * @return replacement string, possibly empty, never null
	 */
	public String getReplacementString()
	{
		return replacementString;
	}

	/**
	 * Sets the string that will be used to replace parts of file content.
	 * If the parameter is null, the replacement string will be reset.
	 * @param replacement replacement string
	 */
	public void setReplacementString(String replacement)
	{
		replacementPattern = null;
		if( replacement == null ) {
			replacementString = "";						
		} else {
			replacementString = replacement;			
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setGroupReference(char groupReferenceIndicator)
	{
		if(groupReference != groupReferenceIndicator) replacementPattern = null;
		super.setGroupReference( groupReferenceIndicator );
	}

	
	/**
	 * Updates this form with the data from the "form" parameter.
	 * Inheriting classes must take care to call this method
	 * <b>after</b> they have performed the update.
	 * @param form data to replace the content of this form
	 */
	public void update(ReplaceForm form) {
		replacementString = form.replacementString;
		groupReference = form.groupReference;
		backUpdDirectory = form.backUpdDirectory;
		doBackup = form.doBackup;
		replacementPattern = null;
		lineBreakUsed = null;
		super.update( form );
	}

//	 -- equals & hashcode -------------------------------------------------------------------------

	/**
	 * Listens to changes of the baseDirectory property value.
	 * @param evt property change event
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if( BD_PROPERTY_NAME.equals(evt.getPropertyName()) ) {
			setBaseDirectory( (File) evt.getNewValue() );
		}
	}
	
	/**
	 * Sets search and replacement string both to the empty string.
	 */
	public void reset() {
		super.reset();
		setReplacementString("");
	}
	
	/**
	 * Returns a copy of this object.
	 * @see java.lang.Object#clone()
	 */
	public ReplaceForm clone() {
		ReplaceForm clown = (ReplaceForm) super.clone();
		if( replacementPattern != null ) {
			clown.replacementPattern = (ReplacementString) replacementPattern.clone();			
		}
		return clown;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object o) {
		if( o == null ) return false;
		if(! super.equals(o)) return false;
		try {
			ReplaceForm form = (ReplaceForm) o;
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
			if( diff.different( replacementString, form.replacementString ) ) return false;
			if( groupReference != form.groupReference ) return false;
			if( diff.different( backUpdDirectory, form.backUpdDirectory ) ) return false;
			if( doBackup != form.doBackup ) return false;
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
		hash = hash * 31 + ( replacementString != null ? replacementString.hashCode() : 0 );
		hash = hash * 31 + groupReference;
		hash = hash * 31 + ( backUpdDirectory != null ? backUpdDirectory.hashCode() : 0 );
		hash = hash * 31 + ( doBackup ? 1 : 0 );
		return hash;		
	}
}
