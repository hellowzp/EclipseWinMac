package net.pandoragames.far.ui.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import net.pandoragames.far.PatternFilter;
import net.pandoragames.far.ui.FindFilter;

/**
 * Combines all data necessary to launch a query.
 * @author Olivier Wehner at 26.02.2008
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
public class FindForm extends FARForm implements FindFilter {
	
	private boolean includeSubDirs = true;
	private FileNamePattern filePattern = new FileNamePattern("*");
	private List<PropertyChangeListener> baseDirectoryChangeListener = new ArrayList<PropertyChangeListener>();
	private FileNamePattern directoryPattern;
	private boolean excludeMatchingDirectories;
	private int maxDepth = 256;
	private Date before;
	private Date after;

	public FindForm(){
		super( OperationType.FIND );
	}
	/**
	 * Returns the file name pattern that should be used to filter files.
	 * @return file name pattern
	 */
	public FileNamePattern getFileNamePattern()
	{
		return filePattern;
	}
	
	/**
	 * Sets the file name pattern that should be used to filter files.
	 * @param filePattern file name pattern
	 */
	public void setFileNamePattern(FileNamePattern filePattern)
	{
		if( filePattern != null ) {
			this.filePattern = filePattern;
		}
	}
		
	/**
	 * Should the file search include subdirectories.
	 * @return include subdirectories
	 */
	public boolean isIncludeSubDirs()
	{
		return includeSubDirs;
	}
	
	/**
	 * Set to false to exclude files in subdirectories.
	 * @param includeSubDirs include subdirectories in file search
	 */
	public void setIncludeSubDirs(boolean includeSubDirs)
	{
		this.includeSubDirs = includeSubDirs;
	}
	
	/**
	 * Sets the maximal search depth for find operations.
	 * @param max maximal search depth
	 */
	public void setMaxDepth(int max) {
		if( max < 0 ) throw new IllegalArgumentException("Negative numers are not allowed");
		maxDepth = max;
	}
	
	/**
	 * Returns the directory search pattern currently set or null. The pattern 
	 * is matched against every subdirectory beyond the base directory. Depending on the
	 * value of the {@link #setExcludeMatchingDirectories(boolean) exclude-matching-directories flag},
	 * matching directories will be excluded from search, or only files with matching ancestor directories
	 * will be included.
	 * @return directory name pattern
	 */
	public FileNamePattern getDirectoryPattern() {
		return directoryPattern;
	}
	
	/**
	 * Sets the directory search pattern as a regular expression. The pattern 
	 * is matched against every subdirectory beyond the base directory. Depending on the
	 * value of the {@link #setExcludeMatchingDirectories(boolean) exclude-matching-directories flag},
	 * matching directories will be excluded from search, or only files with matching ancestor directories
	 * will be included.
	 * @param directoryPattern directory search pattern
	 */
	public void setDirectoryPattern(FileNamePattern pattern) {
		directoryPattern = pattern;
	}
	
	/**
	 * Returns the value of the exclude-matching-directories flag.
	 * @see #setExcludeMatchingDirectories(boolean)
	 * @return should directories matching the directory pattern be excluded
	 */
	public boolean isExcludeMatchingDirectories() {
		return excludeMatchingDirectories;
	}
	
	/**
	 * Control flag for the {@link #getDirectoryPattern() directory pattern}. If this flag is set,
	 * directories matching the directory pattern will be excluded from the search. Otherwise, only files
	 * in or under a matching directory (i.e. with matching ancestor directory) will be included in the
	 * search result. If the directory pattern is not set (null), this flag has no effect.
	 * @param excludeMatchingDirectories true to exclude, false to restrict.
	 */
	public void setExcludeMatchingDirectories(boolean excludeMatchingDirectories) {
		this.excludeMatchingDirectories = excludeMatchingDirectories;
	}
	/**
	 * {@inheritDoc}
	 */
	public Date getBefore() {
		return before;
	}
	/**
	 * Sets the maximum modified time stamp.
	 * @param before maximum modified time stamp or null
	 */
	public void setBefore(Date before) {
		this.before = before;
	}
	/**
	 * {@inheritDoc}
	 */
	public Date getAfter() {
		return after;
	}
	/**
	 * Sets the minimum modified time stamp.
	 * @param before minimum modified time stamp or null
	 */
	public void setAfter(Date after) {
		this.after = after;
	}
	/**
	 * {@inheritDoc} <br>
	 * This method additionally calls any registered property change listener.
	 * @param baseDirectory {@inheritDoc}
	 */
	public void setBaseDirectory(File baseDirectory)
	{
		File old = getBaseDirectory();
		super.setBaseDirectory(baseDirectory);
		if((old == null && baseDirectory != null)
			|| (old != null && ! old.equals(baseDirectory))) {
			PropertyChangeEvent event = new PropertyChangeEvent( this, BD_PROPERTY_NAME, old, baseDirectory);
			for(PropertyChangeListener pcl : baseDirectoryChangeListener) {
				pcl.propertyChange( event );
			}
		}
	}

	/**
	 * Updates this form with the data from the "form" parameter.
	 * Inheriting classes must take care to call this method
	 * <b>after</b> they have performed the update.
	 * @param form data to replace the content of this form
	 */
	public void update(FindForm form) {
		filePattern = form.filePattern;
		includeSubDirs = form.includeSubDirs;
		directoryPattern = form.directoryPattern;
		excludeMatchingDirectories = form.excludeMatchingDirectories;
		after = form.after;
		before = form.before;
		super.update( form );
	}

	
// -- functional methods ----------------------------------------------------------------------------
	
	/**
	 * Adds a PropertyChangeListener for the BaseDirectory property.
	 * @param pcl PropertyChangeListener to be added
	 */
	public void addBaseDirectoryListener(PropertyChangeListener pcl) {
		if(pcl != null) baseDirectoryChangeListener.add( pcl );
	}
	
	/**
	 * Removes a PropertyChangeListener.
	 * @param pcl PropertyChangeListener to be removed
	 */
	public void removeBaseDirectoryListener(PropertyChangeListener pcl) {
		if(pcl != null) baseDirectoryChangeListener.remove( pcl );
	}
	
	/**
	 * Returns true if the properties "baseDirectory" and "filePattern" of both FindForm instances
	 * are non null and equal, together with their respective flags. Returns false if one (of the four)
	 * properties has not the same value in both forms. 
	 */
	public boolean sameBaseSearch(FindForm otherForm){
		if(otherForm == null) return false;
		if(( getBaseDirectory() == null ) || (! getBaseDirectory().equals( otherForm.getBaseDirectory() ))) return false;
		if( includeSubDirs != otherForm.isIncludeSubDirs() ) return false;
		if(( filePattern == null ) || (! filePattern.equals( otherForm.getFileNamePattern()))) return false;
		return true;
	}
	
	/**
	 * Returns a PatternFilter for this FindForm. If the currently specified file pattern
	 * is valid, the filter will accept only files that comply with this find form (ignoring
	 * any content pattern that might be specified). If the currently specified file pattern
	 * is <i>not</i> valid, an accept all filter will be returned.
	 * @return PatternFilter for this FindForm if file name pattern is valid, accept all 
	 * filter otherwise.
	 */
	public PatternFilter createPatternFilter() {
		try {
			return new PatternFilter( this.getBaseDirectory(), this.isIncludeSubDirs(), this.getFileNamePattern().toRegexPattern() );
		} catch(Exception x) {
			try {
				return new PatternFilter( null, true, Pattern.compile(".*") );
			} catch(IOException iox) {
				throw new IllegalStateException("Can not instantiate fallback PatternFilter");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMaxDepth() {
		return maxDepth;
	}
		
	/**
	 * Returns a copy of this object.
	 * @see java.lang.Object#clone()
	 */
	public FindForm clone() {
		FindForm clown = (FindForm) super.clone();
		clown.baseDirectoryChangeListener = new ArrayList<PropertyChangeListener>();
		clown.filePattern = (FileNamePattern) filePattern.clone();
		if( directoryPattern  != null ) {
			clown.directoryPattern = (FileNamePattern) directoryPattern.clone();
		}
		// immutable objects anyway
//		clown.after = (Date) after.clone();
//		clown.before = (Date) before.clone();
		return clown;
	}

	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object o) {
		if(! super.equals(o)) return false;
		try {
			FindForm form = (FindForm) o;
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
			if( includeSubDirs != form.includeSubDirs ) return false;
			if( diff.different( filePattern, form.filePattern ) ) return false;
			if( diff.different( directoryPattern, form.directoryPattern ) ) return false;
			if( diff.different( after, form.after ) ) return false;
			if( diff.different( before, form.before ) ) return false;
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
		hash = hash * 31 + ( includeSubDirs ? 1 : 0 );
		hash = hash * 31 + ( filePattern != null ? filePattern.hashCode() : 0 );
		hash = hash * 31 + ( directoryPattern != null ? directoryPattern.hashCode() : 0 );
		hash = hash * 31 + ( after != null ? after.hashCode() : 0 );
		hash = hash * 31 + ( before != null ? before.hashCode() : 0 );
		return hash;		
	}
}
