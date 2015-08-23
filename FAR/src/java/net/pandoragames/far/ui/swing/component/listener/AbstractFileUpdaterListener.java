package net.pandoragames.far.ui.swing.component.listener;

import java.io.File;
import java.io.IOException;

import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.util.i18n.Localizer;

/**
 * Extension of the <code>AbstractFileOperationListener</code> class for
 * operations that modify the file data on disk.
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
public abstract class AbstractFileUpdaterListener extends
		AbstractFileOperationListener {

	/**
	 * The Localizer supplied with the constructor, available for inheriting classes.
	 */
	protected Localizer localizer;
	
	/**
	 * The listener is instantiated with a reference to the current FileSetTableModel
	 * and a Localizer. Error messages are written to the error property of the
	 * corresponding {@link net.pandoragames.far.ui.model.TargetFile TargetFile} object. 
	 * @param model current FileSetTableModel
	 * @param localizer for error messages
	 */
	protected AbstractFileUpdaterListener(FileSetTableModel model, Localizer localizer) {
		super( model );
		this.localizer = localizer;
	}
	
	@Override
	protected final void execute(TargetFile targetFile) {
		File source = targetFile.getFile();
		if( ! source.exists() ) {
			targetFile.error( localizer.localize("message.file-not-found") );
			return;
		}
		if( ! source.canWrite() ) {
			targetFile.error( localizer.localize("message.read-only") );
			return;
		}

		File tempfile = null;
		try {
			tempfile = File.createTempFile("FAR", ".tmp");
			
			if( ! executeForUpdate(targetFile, tempfile) ) return;
			
		} catch(IOException iox) {
			targetFile.error( localizer.localize("message.file-processing-error", new Object[]{ targetFile.getName(), iox.getMessage()}) );
			logger.error( iox.getClass().getName() + " processing " + targetFile.getFile().getPath() + ": " + iox.getMessage());
			if( tempfile != null ) tempfile.delete();
			return;
		}
		if( source.delete() ) {
			if( tempfile.renameTo( source ) ) {
				success( targetFile );
			} else {
				// uh, worst case				
				targetFile.error( localizer.localize("message.backup-not-restored", tempfile.getPath() ) );			
				logger.error(targetFile.getFile().getPath() + " was deleted but could not be replaced with processing result from " + tempfile.getPath());
			}
		} else {
			targetFile.error( localizer.localize("message.could-not-process") );			
			logger.error(targetFile.getFile().getPath() + " was processed, but original file could not be replaced with operation result");
		}
	}

	/**
	 * Called to actually perform the desired update operation. The source file is guaranteed to
	 * exist and to be writable. The modified data must be written to the supplied target file.
	 * If the method returns true, then the source file will be replaced with the target file.
	 * Otherwise the code assumes that some suitable error message has been written to the source
	 * files error property and further processing is skiped.
	 * 
	 * @param source the current file
	 * @param target where to write the updated data to
	 * @throws IOException unhandled IOExceptions are taken care of by calling code
	 */
	protected abstract boolean executeForUpdate(TargetFile source, File target) throws IOException;
	
	/**
	 * Called when the operation could successfully be performed.
	 * Implementing classes will typically use this method to write some information to the 
	 * target files info property.
	 * 
	 * @param targetFile on which the operation was successfully performed.
	 */
	protected abstract void success(TargetFile targetFile);
}
