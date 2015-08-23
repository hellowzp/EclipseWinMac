package net.pandoragames.far.ui.model;

import java.io.File;

import net.pandoragames.far.ui.FARConfig;

/**
 * Base class for FileRepository implementations that reference 
 * the find form and replace form models.
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
public abstract class AbstractFileRepository implements FileRepository {

	/** Protected member for inheriting classes */
	protected FindForm findForm; 
	/** Protected member for inheriting classes */
	protected ReplaceForm replaceForm;
	/** Protected member for inheriting classes */
	protected FARConfig farconfig;
	/** Protected member for inheriting classes */
	protected MessageBox messageBox;
	
	/**
	 * Constructor takes both forms and a FARConfig instance.
	 * @param cfg FAR configuration.
	 * @param find data of the find form
	 * @param replace data of the replace form
	 */
	public AbstractFileRepository(FARConfig cfg, FindForm find, ReplaceForm replace, MessageBox errorsink) {
		farconfig = cfg;
		findForm = find;
		replaceForm = replace;
		messageBox = errorsink;
	}

	/**
	 * Returns true if test is a subdirectory of base.
	 * @param base base directory
	 * @param test potential subdirectory
	 * @return true if test is a subdirectory of base
	 */
	protected boolean isSubdirectory( File base, File test ) {
		return test.getPath().startsWith( base.getPath() );
	}

}
