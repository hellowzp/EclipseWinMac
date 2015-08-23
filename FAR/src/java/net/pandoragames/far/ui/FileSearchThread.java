package net.pandoragames.far.ui;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import net.pandoragames.far.FileSelector;
import net.pandoragames.far.ui.model.MessageBox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Executes {@link net.pandoragames.far.FileSelector#listFiles(Pattern, File) FileSelector.listFiles()}
 * in a thread on its own. This class is used internally by {@link UIBean UIBean}.
 * @author Olivier Wehner at 07.03.2008
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
public class FileSearchThread extends Thread
{
	private FileSelector fileSelector;
	private FileFilter criteria;
	private Set<File> fileSet;
	private MessageBox messageBox;
	private Log threadLog;
	
	/**
	 * This class is used internally by {@link UIBean UIBean}.
	 * @param selector not null
	 * @param findForm not null
	 * @param messageSink not null
	 */
	FileSearchThread(FileSelector selector,  FileFilter findForm, MessageBox messageSink) {
		threadLog = LogFactory.getLog( this.getClass() );
		criteria = findForm;
		fileSelector = selector;
		messageBox = messageSink;
	}
	
	/**
	 * Executes {@link net.pandoragames.far.FileSelector#listFiles(Pattern, File) FileSelector.listFiles()}.
	 */
	public void run() {
		if( threadLog.isInfoEnabled() ) {
			StringBuilder buffer = new StringBuilder();
			buffer.append("Searching for ");
			buffer.append(criteria.getFileNamePattern().getPattern());
			if( criteria.getFileNamePattern().isRegex()) buffer.append(" (regex)");
			buffer.append(" at ").append( criteria.getBaseDirectory().getPath());
			if( criteria.isIncludeSubDirs()) {
				buffer.append(" and subdirectories");
				if( criteria.getDirectoryPattern() != null ) {
					buffer.append(" (");
					if( criteria.getFileNamePattern().isRegex()) buffer.append("regex: ");
					buffer.append(criteria.getDirectoryPattern().getPattern());
					if( criteria.isExcludeMatchingDirectories()) buffer.append(" excluded");
					buffer.append(")");
				}
			}
			threadLog.info( buffer.toString() );
		}
		fileSelector.setMaxSearchDepth( criteria.isIncludeSubDirs() ? criteria.getMaxDepth() : 0);
		Pattern directoryPattern = null;
		if( criteria.getDirectoryPattern() != null ) {
			if( criteria.isExcludeMatchingDirectories() ) {
				fileSelector.setDirectoriesExcluded( criteria.getDirectoryPattern().toRegexPattern() );
			} else {
				directoryPattern = criteria.getDirectoryPattern().toRegexPattern();
				fileSelector.setDirectoriesExcluded( null );
			}
		} else {
			fileSelector.setDirectoriesExcluded( null );			
		}
		try{
			Pattern pattern = criteria.getFileNamePattern().toRegexPattern();
			fileSet = fileSelector.listFiles( pattern, criteria.getBaseDirectory(), criteria.getAfter(), criteria.getBefore(), directoryPattern );
			threadLog.info( fileSet.size() + " files found");
		} catch (Exception x) {
			threadLog.error( x.getClass().getName() + ": " +x.getMessage(), x);
			messageBox.error( x.getMessage() );
			fileSet = new HashSet<File>();
		}			
	}
	
	/**
	 * Returns the search result.
	 * @return found files.
	 */
	public Set<File>  getResult() {
		return fileSet;
	}
}
