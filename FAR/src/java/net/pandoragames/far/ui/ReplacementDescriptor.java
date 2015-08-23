package net.pandoragames.far.ui;

import java.io.File;

import net.pandoragames.far.ReplacementString;

/**
 * Data for a replacement operation.
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
public interface ReplacementDescriptor extends ContentFilter {

	/**
	 * Returns the doBackup flag. Note that the application may choose to
	 * create temporary backups, even if this flag is set to false, in 
	 * order to allow undo operations.
	 * @see net.pandoragames.far.FileMatcher#isDoBackup()
	 * @return whether backups should be created or not
	 */
	public boolean isDoBackup();

	/**
	 * Returns the backup directory.
	 * @see net.pandoragames.far.FileMatcher#setBackUpDirectory(File)
	 * @return backup directory if any
	 */
	public File getBackupDirectory();
	
	/**
	 * Returns the raw replacement string as specified by the user.
	 * @return replacement string, possibly empty, never null
	 */
	public String getReplacementString();

	/**
	 * Returns the replacement string as a ReplacementString object.
	 * @param lineBreak the lineBreak to be used, if null the system 
	 * dependent line separator will be used
	 * @return ReplacementString object or null if no replacement is defined.
	 */
	public ReplacementString getReplacementPattern(String lineBreak);

	/**
	 * Returns the base directory, if any. This attribute is used for the creation
	 * of back ups. All files subject to a replacement operation should share the same
	 * common base directory. Alternatively, this attribute should be set to null, or
	 * the doBackup flag should be set to false.
	 * @see net.pandoragames.far.FileMatcher#setBaseDirectory(File)
	 * @return base directory for the resolution of relative pathes during backup operations.
	 */
	public File getBaseDirectory();

	/**
	 * Returns a copy of this object, with all interface method behaving exactly as the original instance.
	 * @return copy of this object.
	 */
	public ReplacementDescriptor clone();

}
