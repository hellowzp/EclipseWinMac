/**
 * 
 */
package net.pandoragames.far.ui.swing.menu;

import java.nio.charset.Charset;

import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.far.ui.swing.component.listener.AbstractFileOperationListener;

/**
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
class AssignCharsetListener extends AbstractFileOperationListener {
	private Charset encoding;
	public AssignCharsetListener(Charset charset, FileSetTableModel model) {
		super( model );
		encoding = charset;
	}
	protected void execute(TargetFile targetFile) {
		// TODO: assignment testen
		targetFile.setCharacterset( encoding );
		targetFile.info( encoding.displayName() );
	}
}
