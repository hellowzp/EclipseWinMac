package net.pandoragames.far.ui;

import java.util.List;

import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.ProgressListener;
import net.pandoragames.far.ui.model.ReplaceForm;
import net.pandoragames.far.ui.model.Resetable;
import net.pandoragames.far.ui.model.TargetFile;

/**
 * Defines the bridge between the frontend and the backend in a UserInterface
 * independend way. The object may carry an internal history to optimize searching
 * behaviour. This history can be cleared through the <code>reset()</code> method.
 * @author Olivier Wehner at 27.02.2008
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
public interface UIFace extends Resetable
{

	/**
	 * Uses the "criteria" parameter to configure a 
	 * {@link net.pandoragames.far.FileSelector FileSelector}
	 * and returns the list of found files.
	 * @param criteria search criteria
	 * @return result list, possibly empty, never null
	 */
	public List<TargetFile> findFiles(FindFilter criteria);

	/**
	 * Uses the "criteria" parameter to configure a 
	 * {@link net.pandoragames.far.FileMatcher FileMatcher} and
	 * applies the defined relacement algorithm to all files
	 * in the list that are marked as "selected" (the default).
	 * @param criteria defining a regular expression and a replacement pattern
	 * @param fileList list of files to operate ons
	 */
	public void replace(ReplacementDescriptor criteria, List<TargetFile> fileList);
	
	/**
	 * Undo command for the last replace operation. Copies the backup
	 * back to its place if one was made. Writes an error message in the
	 * sink if no backup was made or if no previous replace operation
	 * has been recorded. 
	 */
	public void undoLastReplace();

	/**
	 * Cycles through the file list and renames each file to the name specified in 
	 * the "newName" property, unless the file is not selected or "newName" is empty.
	 * This operation will only change the file name, not the files path. If a file
	 * with the desired new name exists, the callback will be used to request user 
	 * feedback. If the renaming operation fails for some reason, an error will be
	 * written to the respective TargetFile object. Returns the transformed list of files.
	 * @param fileList files to be renamed
	 * @param callback for user feedback
	 * @return transformed list of files
	 */
	public List<TargetFile> rename(List<TargetFile> fileList, OverwriteFileCallback callback);

	/**
	 * Uses the content pattern (and associated flags) supplied with the "criteria" to filter the specified file list. 
	 * Only those files that carry the "selected" flag will be filtered. 
	 * @param fileList list of files. Only <i>selected</i> files are taken into account.
	 * @param criteria filter criteria
	 * @param removeMismatch true if mismatches should be removed, false if they should only be deselected.
	 */
	public void filter(List<TargetFile> fileList, ContentFilter criteria, boolean removeMismatch);
	
	/**
	 * Aborts a currently running operation (find, replace, ...).
	 */
	public void abort();
	
	/**
	 * Aborts any currently running search and clears the history.
	 * Does not remove any ProgressListener, but resets all internal informations
	 * about previous searches.
	 */
	public void reset();

	/**
	 * Adds a ProgressListener that will reveive notifications about find and
	 * replace operations (but not about renaming operations).
	 * @param prol ProgressListener to be added.
	 */
	public void addProgressListener(ProgressListener prol);

	/**
	 * Removes a previously set ProgressListener
	 * @param prol ProgressListener to be removed
	 */
	public void removeProgressListener(ProgressListener prol);

}