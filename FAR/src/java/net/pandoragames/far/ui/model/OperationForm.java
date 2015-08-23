package net.pandoragames.far.ui.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Common base class for FAR data models. Each tab of the FAR GUI is backed
 * by a specialized form class. The form receives data from the UI and serves as
 * "protocol" for the backend.
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
public abstract class OperationForm implements Serializable, Cloneable, Resetable {
	
	/** Used for PropertyChangeListener. */
	protected static final String BD_PROPERTY_NAME = "baseDirectory";
	
	/**
	 * The group reference indicator used in replacement strings.
	 * This class uses the backslash ('\') as default.
	 */
	protected char groupReference = '\\';
	
	// base directory for relative pathes
	private File baseDirectory;
	// shortcut for instanceof
	private OperationType type = OperationType.NONE;
	// listener for form updates
	private List<FormUpdateListener> listenerList;

	protected OperationForm(OperationType operation) {
		type = operation;
	}
	
	/**
	 * Returns the type of this form.
	 * This is a convinience form of the instanceof operator,
	 * suitable for switch-case.
	 * @return type of form, one of the constants of this class
	 */
	public OperationType getType() {
		return type;
	}
	
	/**
	 * Returns the base directory. The base directory is used
	 * for the construction of search and backup trees.
	 * @see net.pandoragames.far.FileMatcher#getBaseDirectory()
	 * @return base directory for file operations
	 */
	public File getBaseDirectory()
	{
		return baseDirectory;
	}
	
	/**
	 * Sets the base directory. The base directory is used
	 * for the construction of search and backup trees.
	 * @see net.pandoragames.far.FileMatcher#getBaseDirectory()
	 * @param baseDirectory base directory for file operations
	 */
	public void setBaseDirectory(File baseDirectory)
	{
		this.baseDirectory = baseDirectory;
	}

	/**
	 * Returns the group reference indicator used in replacement strings.
	 * This class uses the backslash ('\') as default.
	 * @see net.pandoragames.far.ReplacementString
	 * @return group reference indicator
	 */
	public char getGroupReference()
	{
		return groupReference;
	}

	/**
	 * Sets the group reference indicator used in replacement strings.
	 * @see net.pandoragames.far.ReplacementString
	 * @param groupReferenceIndicator group reference indicator
	 */
	public void setGroupReference(char groupReferenceIndicator)
	{
		groupReference = groupReferenceIndicator;
	}

	
	/**
	 * Updates this form with the data from the "form" parameter.
	 * Inheriting classes must take care to call this method
	 * <b>after</b> they have performed the update.
	 * @param form data to replace the content of this form
	 */
	protected void update(OperationForm form) {
		baseDirectory = form.baseDirectory;
		groupReference = form.groupReference;
		fireFormUpdateEvent();
	}
	
	/**
	 * Adds a Listener that will be informed whenever this form is
	 * updated through method {@link #update(OperationForm) update()}.
	 * The listener will be called <i>after</i> the fields of this
	 * form have been updated.
	 * @param listener to be informed whenever this form is updated.
	 */
	public void addFormUpdateListener(FormUpdateListener listener) {
		if( listener == null ) return;
		if( listenerList == null ) listenerList = new ArrayList<FormUpdateListener>();
		listenerList.add( listener );
	}
	/**
	 * Removes a previously registered update listener.
	 * @param listener to be removed
	 */
	public void removeFormUpdateListener(FormUpdateListener listener) {
		if( listener != null && listenerList != null ) listenerList.remove( listener );
	}
	
	// default access, so it can be triggered form FormPropertySet
	void fireFormUpdateEvent() {
		if( listenerList == null ) return;
		FormUpdateEvent event = new FormUpdateEvent( this );
		for(FormUpdateListener listener : listenerList) {
			listener.formUpdated( event );
		}
	}
	
//-- equals & hashcode -------------------------------------------------------------------------

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
	 * Only the base directory is taken into account for equals!
	 */
	public boolean equals(Object o) {
		if( o == null ) return false;
		try {
			OperationForm form = (OperationForm) o;
			if( baseDirectory == null ) return (form.baseDirectory == null);
			return baseDirectory.equals( form.baseDirectory );
		} catch (ClassCastException ccx) {
			return false;
		}
	}
		
	/**
	 * Only the base directory is taken into account for the hash code!
	 */
	public int hashCode() {
		return ((baseDirectory != null) ? baseDirectory.hashCode() : 0);
	}
}
