package net.pandoragames.far.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.pandoragames.far.FileMatcher;
import net.pandoragames.far.FileSelector;
import net.pandoragames.far.ui.model.FileType;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.OperationType;
import net.pandoragames.far.ui.model.ProgressListener;
import net.pandoragames.far.ui.model.ReplaceForm;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.util.file.FileUtil;
import net.pandoragames.util.i18n.DummyLocalizer;
import net.pandoragames.util.i18n.Localizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * GUI neutral bridge between backend and frontend. Since searches can take quite a lot
 * of time, the <code>findFiles()</code>, <code>rename()</code> and <code>replace()</code>
 * methods execute the respective commands in a Thread on their own.
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
public class UIBean implements UIFace
{
	private enum MISMATCH_OPERATION { REMOVE, UNSELECT, EXCLUDE };
	
	private static final String LINEBREAK = System.getProperty("line.separator");
	
	private MessageBox messageBox;
	private Localizer localizer;
	private FARConfig configuration;
	private FileSelector fileSelector = new FileSelector();
	private boolean wasAborted = false;
	private OperationType operationInProgress = OperationType.NONE;
	private List<ProgressListener> listener;
	private Log logger;
	// last replace operation for undo
	private ReplaceForm lastReplaceForm;
	private List<TargetFile> lastFileSet;
	
	/**
	 * Constructor requires a sink for (error) messages
	 * @param config FAR configuration
	 * @param sink for (error) messages
	 */
	public UIBean(FARConfig config, MessageBox sink) {
		if( sink == null ) throw new NullPointerException("Message sink must not be null");
		configuration = config;
		logger = LogFactory.getLog( this.getClass() );
		messageBox = sink;
		localizer = config.getLocalizer();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<TargetFile> findFiles(FindFilter criteriaForm) {
		if( criteriaForm == null ) throw new NullPointerException("Search criteria must not be null");
		FindFilter criteria = (FindFilter) criteriaForm.clone();
		// abort any previously running search
		fileSelector.abort();
		wasAborted = false;
		messageBox.clear();		
		notifyStarted( OperationType.FIND );
		operationInProgress = OperationType.FIND;

		// reset undo for replace
		lastReplaceForm = null;
		lastFileSet = null;

		// search for file names
		List<TargetFile> searchResult;
		Set<File> fileSet = fileNamePatternSearch( criteria );
		searchResult = new ArrayList<TargetFile>();
		for(File file : fileSet) {
			searchResult.add( new TargetFile( file ) );
		}			
		
		// search for pattern 
		if ((! wasAborted )  && ( criteria.getSearchStringContent().trim().length() > 0)){
			regexPatternSearchFilter( searchResult, criteria, MISMATCH_OPERATION.REMOVE );
		}
		
		// notifications
		if(! wasAborted ) {
			int resultCount = searchResult.size();
			messageBox.info( localizer.localize( "message.find-count", new Object[] { new Integer( resultCount ) }));
			notifyTerminated( OperationType.FIND );
		}
		
		// clean up and  history
		wasAborted = false;
		operationInProgress = OperationType.NONE;
		return searchResult;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void filter(List<TargetFile> fileList, ContentFilter criteriaForm, boolean removeMismatch) {
		if( fileList == null ) throw new NullPointerException("File list must not be null");
		if( criteriaForm == null ) throw new NullPointerException("Search criteria must not be null");
		
		ContentFilter criteria = (ContentFilter) criteriaForm.clone();
		// abort any previously running search
		fileSelector.abort();
		wasAborted = false;
		messageBox.clear();		
		// notifyStarted( criteria.getType() );
		operationInProgress = OperationType.FILTER;//criteria.getType();
	
		MISMATCH_OPERATION handleMismatch = removeMismatch ? MISMATCH_OPERATION.REMOVE : MISMATCH_OPERATION.UNSELECT;
		
		// search for pattern 
		if ((! wasAborted )  && ( criteria.getSearchStringContent().trim().length() > 0)){
			regexPatternSearchFilter( fileList, criteria, handleMismatch );
		}

		// if(! wasAborted ) notifyTerminated( criteria.getType() );
		
		// clean up and  history
		wasAborted = false;
		operationInProgress = OperationType.NONE;
	}

	
	/**
	 * {@inheritDoc}
	 */
	public void replace(ReplacementDescriptor criteriaForm, List<TargetFile> fileList) {
		if( criteriaForm == null ) throw new NullPointerException("Replace criteria must not be null");
		ReplacementDescriptor criteria = (ReplacementDescriptor) criteriaForm.clone();
		wasAborted = false;
		int counter = 0;
		try {
			FileMatcher matcher = new FileMatcher( criteria.getContentPatternAsRegex() );
			matcher.setBaseDirectory( criteria.getBaseDirectory() );
			matcher.setDoBackup( criteria.isDoBackup() );
			if(criteria.isDoBackup() ) matcher.setBackUpDirectory( criteria.getBackupDirectory() );
			int matchCounter = 0;
			int selectCounter = 0;
			for(int i = 0; i < fileList.size(); i++) { if( fileList.get(i).isSelected()) selectCounter++; }
			notifyStarted( OperationType.REPLACE );
			operationInProgress = OperationType.REPLACE;
			lastReplaceForm = (ReplaceForm) criteria.clone();
			lastFileSet = new ArrayList<TargetFile>();
			logger.info("Applying " + criteria.getContentPatternAsRegex().pattern() + " --> "
						+ criteria.getReplacementString() + " on " + selectCounter + " files ");
			for(TargetFile file : fileList) {
				counter ++;
				if((! 	wasAborted ) && file.isSelected()) {
					file.clear();
					lastFileSet.add( file );
					try {
						matcher.setCharacterSet( file.getCharacterset() );
//						 TODO: use file dependent line break
						// like so:
						// if( criteria.patternContainsLineBreak() ) {
						// 		String lb = findLineBreakUsed( file );
						//	}
						int numberOfMatches = matcher.apply( file.getFile(), criteria.getReplacementPattern( LINEBREAK ) );
						file.setIncluded( numberOfMatches > 0 );
						if( numberOfMatches > 0 ) {
							matchCounter++;
							file.info( localizer.localize("message.replacement-count", new Integer[]{numberOfMatches}));
							logger.debug( "Replacement pattern applied to " + file.getFile().getName() );
						}
					} catch(Exception x) {
						logger.error( x.getClass().getName() + ": " +x.getMessage(), x);
						file.error( x.getMessage() );
					}
				} else {
					file.setIncluded( false );
				}
				notifyCount( counter, selectCounter, OperationType.REPLACE );
			}
			notifyTerminated( OperationType.REPLACE );
			logger.info( matchCounter + " files changed");
			messageBox.info( localizer.localize("message.update-count", new Integer[]{ Integer.valueOf( matchCounter ) }));
		} catch (PatternSyntaxException px) {
			abort();
			logger.error( "PatternSyntaxException: " + px.getMessage(), px);
			messageBox.clear();
			messageBox.error( localizer.localize("message.syntax-error", 
								new Object[]{px.getMessage()} ));							
		} catch (Exception x) {
			abort();
			logger.error( x.getClass().getName() + ": " +x.getMessage(), x);
			messageBox.clear();
			messageBox.error( localizer.localize("message.file-processing-error", 
								new Object[]{fileList.get(counter-1).getName(), x.getMessage()} ));				
		} catch (OutOfMemoryError omu) {
			abort();
			String message = "OutOfMemoryError: " + omu.getMessage();
			logger.error( message, omu );
			messageBox.clear();
			messageBox.error( localizer.localize("message.document-too-large-for-processing", 
								new Object[]{fileList.get(counter-1).getName(), omu.getMessage()} ));				
		}
		wasAborted = false;
		operationInProgress = OperationType.NONE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void undoLastReplace() {
		if((lastReplaceForm == null) || (lastFileSet == null)) {
			messageBox.error( localizer.localize("message.no-previous-replace") );
			return;
		}
		if( lastReplaceForm.isDoBackup() == false ) {
			messageBox.error( localizer.localize("message.no-backup-made") );
			return;
		}
		FileMatcher matcher = new FileMatcher( lastReplaceForm.getContentPatternAsRegex() );
		matcher.setBaseDirectory( lastReplaceForm.getBaseDirectory() );
		matcher.setDoBackup( lastReplaceForm.isDoBackup() );
		matcher.setBackUpDirectory( lastReplaceForm.getBackupDirectory() );
		int undoCounter = 0;
		for(TargetFile file : lastFileSet) {
			file.clear();
			if( file.isIncluded() ) {
				File backup = matcher.getBackupFileName( file.getFile() );
				if( ! backup.exists() ) {
					file.error(localizer.localize("message.backup-not-found"));
				} else {
					try{
						if( !file.getFile().exists() ) file.getFile().createNewFile();
						FileUtil.copy(backup, file.getFile());
						file.info(localizer.localize("message.ok"));
						undoCounter++;
						messageBox.info(localizer.localize("message.files-restored", new Integer[]{ undoCounter }));
					} catch(IOException iox) {
						logger.error("IOException restoring " + file.getPath() + " from backup " + backup.getPath() + ": " + iox.getMessage());
						file.error(localizer.localize("message.backup-not-restored", new String[]{backup.getPath()}));						
					}
				}
//				} else if(file.getFile().delete() && backup.renameTo(file.getFile())) {
//					file.info(localizer.localize("message.ok"));
//					undoCounter++;
//					messageBox.info(localizer.localize("message.files-restored", new Integer[]{ undoCounter }));
//				} else {
//					file.error(localizer.localize("message.backup-not-restored", new String[]{backup.getPath()}));				
//				}
			} else {
				file.setIncluded( true );
			}
		}
		// if no files where restored - issue a message though
		messageBox.info(localizer.localize("message.files-restored", new Integer[]{ undoCounter }));
		
		logger.debug( undoCounter + " files restored");
		
		// reset undo for replace
		lastReplaceForm = null;
		lastFileSet = null;		
	}

	
	/**
	 * Cycles through the file list and renames each file to the name specified in 
	 * the "newName" property, unless the file is not selected or "newName" is empty.
	 * This operation will only change the file name, not the files path. If a file
	 * with the desired new name exists, the callback will be used to request user 
	 * feedback. If the renaming operation fails for some reason, an error will be
	 * written to the respective TargetFile opject. Returns the transformed list of files.
	 * @param fileList files to be renamed
	 * @param callback for user feedback
	 * @return transformed list of files
	 */
	public List<TargetFile> rename(List<TargetFile> fileList, OverwriteFileCallback callback) {
		wasAborted = false;
		messageBox.clear();		
		notifyStarted( OperationType.RENAME );
		operationInProgress = OperationType.RENAME;
		lastReplaceForm = null;
		lastFileSet = null;
		
		// count number of files to be renamed
		int totalExpected = 0;
		for(TargetFile file : fileList) {
			if( file.isSelected() 
					&& file.getNewName() != null 
					&& file.getNewName().length() > 0 
					&& ! file.getName().equals( file.getNewName() )) {
				totalExpected++;
			}
		}
		
		List<TargetFile> resultList = new ArrayList<TargetFile>();
		int counter = 0; 
		int renameCounter = 0;
		for(TargetFile file : fileList) {
			if( wasAborted ) {
				file.setNewName(null);
				resultList.add( file );
			} else if( file.isSelected() 
					&& file.getNewName() != null 
					&& file.getNewName().length() > 0 
					&& ! file.getName().equals( file.getNewName() )) {
				counter++;
				File newFile = new File( file.getFile().getParentFile(), file.getNewName() );
				boolean doRename = true;
				if( newFile.exists() ) {
					if( newFile.isFile() ) {
						// on win it may happen that file names are different but files are equal
						if( ! newFile.equals( file.getFile() )) {
							if( callback.askForOverwrite( newFile ) ) {
								if( ! newFile.delete() ) {
									logger.error("Could not delete " + newFile.getName() + " for preparation of rename operation");
									file.error( localizer.localize("message.could-not-rename") );
									doRename = false;
								}
							} else {
								doRename = false;
				 				logger.debug("Skip renaming " + file.getName() + " to " + file.getNewName());
				 				file.setNewName(null);
							}							
						}
					} else {
						logger.warn("Could not rename " + file.getName() + " to " + newFile.getPath() 
								+ " because a directory, device, symlink or other non-file object exists at the same location.");
						file.error( localizer.localize("message.could-not-rename") );
						doRename = false;
					}
				}
				if( doRename ) {
					if( file.getFile().renameTo(newFile) ) {
						TargetFile result = new TargetFile( newFile );
						result.setCharacterset( file.getCharacterset() );
						resultList.add( result );
						renameCounter++;
						logger.debug("Renamed " + file.getFile().getPath() + " to " + result.getName());
					} else {
						file.error( localizer.localize("message.could-not-rename") );
						resultList.add( file );
						logger.error("Failed renaming " + file.getName() + " to " + file.getNewName());
					}
				} else {
					resultList.add( file );
				}
			} else {
				file.setNewName(null);
				resultList.add( file );
			}
			notifyCount( counter, totalExpected, OperationType.RENAME );
		}
		notifyTerminated( OperationType.RENAME );
		messageBox.info(localizer.localize("message.files-renamed", renameCounter));		
		logger.info( renameCounter + " files renamed, " + (counter - renameCounter) + " errors");
		wasAborted = false;
		operationInProgress = OperationType.NONE;
		return resultList;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void abort() {
		messageBox.clear();
		if( operationInProgress != OperationType.NONE ) {
			wasAborted = true;
			switch( operationInProgress ) {
			case FIND : 
				fileSelector.abort();
				messageBox.info( localizer.localize( "message.find-abort") );
				break;
			case REPLACE :
				messageBox.info( localizer.localize( "message.replace-abort") );
				break;
			case RENAME :
				messageBox.info( localizer.localize( "message.rename-abort") );
				break;
			}
			if( listener != null ) {
				for(ProgressListener prol : listener) {
					prol.operationAborted( operationInProgress );
				}
			}
			operationInProgress = OperationType.NONE;
		}
	}
	
	/**
	 * Aborts any currently running search and clears the history.
	 * Does not remove any ProgressListener, but resets all internal informations
	 * about previous searches.
	 */
	public void reset() {
		abort();
	}
		
	/**
	 * Sets the Localizer instance to be used for translations.
	 * @param localizer for translations
	 */
	public void setLocalizer(Localizer localizer) {
		if( localizer != null ) this.localizer = localizer;
	}
	
	/**
	 * Adds a ProgressListener that will reveive notifications about find and
	 * replace operations (but not about renaming operations).
	 * @param prol ProgressListener to be added.
	 */
	public void addProgressListener(ProgressListener prol) {
		if( listener == null ) {
			listener = new ArrayList<ProgressListener>();
		}
		listener.add( prol );
	}
	
	/**
	 * Removes a previously set ProgressListener
	 * @param prol ProgressListener to be removed
	 */
	public void removeProgressListener(ProgressListener prol) {
		if( listener != null ) {
			listener.remove( prol );
		}
	}

// -- private methods ---------------------------------------------------------------------------------------------------------------------------------	
	
	private void notifyStarted(OperationType type) {
		if( listener != null ) {
			for(ProgressListener prol : listener) {
				prol.operationStarted( type );
			}
		}
	}
	
	private void notifyCount(int count, int total,  OperationType type ) {
		if( listener != null ) {
			for(ProgressListener prol : listener) {
				prol.operationProgressed( count, total, type );
			}
		}
	}

	private void notifyTerminated( OperationType type ) {
		if( listener != null ) {
			for(ProgressListener prol : listener) {
				prol.operationTerminated( type );
			}
		}
	}
	
	/**
	 * Runs the file name search in a thread on its own
	 * @param criteria search pattern
	 * @return search result
	 */
	private Set<File> fileNamePatternSearch(FileFilter criteria) {
		messageBox.info( localizer.localize("message.looking-for-pattern", new String[] { criteria.getFileNamePattern().getPattern() }) );
		FileSearchThread fileSearch = new FileSearchThread( fileSelector, criteria, messageBox );
		fileSearch.setDaemon( true );
		fileSearch.start();
		do {
			try{
				synchronized( this ) {
					this.wait( 250L );
					notifyCount( fileSelector.getFileCounter(), fileSelector.getTotalCounter(), OperationType.FIND );
				}
			} catch (InterruptedException ix) {
				abort();
			}
		} while ((! wasAborted ) && ( fileSearch.isAlive() ));
		return fileSearch.getResult();
	}
	
	/**
	 * Cycles over a list of files and applies a regular expression patern match
	 * to it. Runs the pattern search in a thread on its own.
	 * @param fileSet to be checked 
	 * @param criteria search pattern
	 * @param handleUnmatchingFiles what to do with files that do not match
	 * @return search result
	 */
	private void regexPatternSearchFilter(List<TargetFile> fileSet, ContentFilter criteria, MISMATCH_OPERATION handleUnmatchingFiles) {
		messageBox.info( localizer.localize("message.scanning-for-regex") );
		RegexSearch  regexSearch = new RegexSearch( fileSet, criteria, handleUnmatchingFiles );
		regexSearch.setDaemon( true );
		regexSearch.start();
		do {
			try{
				synchronized( this ) {
					this.wait( 250L );
					// notification of counter done in thread
				}
			} catch (InterruptedException ix) {
				abort();
			}
		} while ((! wasAborted ) && ( regexSearch.isAlive() ));
	}
	
// -- inner classes --------------------------------------------------------------------------------------------------------------------------------------	
	
	
	/**
	 * Thread class that executes the regex search.
	 */
	class RegexSearch extends Thread {
		private ContentFilter criteria;
		private List<TargetFile> resultList;
		private Log threadLog;
		private MISMATCH_OPERATION handleMismatch;
		
		public RegexSearch(List<TargetFile> fileSet, ContentFilter criteria, MISMATCH_OPERATION handleUnmatchingFiles) {
			threadLog = LogFactory.getLog( this.getClass());
			resultList = fileSet;
			this.criteria = criteria;
			handleMismatch = handleUnmatchingFiles;
		}
		
		public void run(){
			int matchCount = 0;
			try{
				Pattern pattern = criteria.getContentPatternAsRegex();
				FileMatcher fileMatcher = new FileMatcher( pattern );
				Set<TargetFile> exlusionSet = new HashSet<TargetFile>();
				int testcounter = 1;
				int selectCounter = 0;
				for(int i = 0; i < resultList.size(); i++) { if( resultList.get(i).isSelected()) selectCounter++; }
				notifyStarted( OperationType.FILTER );
				for(TargetFile file : resultList) {
					file.clear();
					notifyCount( testcounter++, selectCounter, OperationType.FILTER );
					threadLog.debug("Scanning " + file.getFile().getPath() );
					try {
						if( wasAborted ) break;
						if( file.isSelected() ) {
							boolean match = fileMatcher.matches( file.getFile() );
							if( match ) matchCount++;
							if( criteria.isInvertContentFilter() ) match = ! match;
							if( match ) {
								threadLog.debug( file.getName() +" matches");
							} else {
								switch(handleMismatch) {
									case REMOVE : exlusionSet.add( file ); break;
									case UNSELECT : file.setSelected( false ); break;
									case EXCLUDE : file.setIncluded( false );
								}
							}
						}
					} catch (IOException iox) {
						threadLog.error( "IOException parsing " + file.getFile().getPath() + ": " +iox.getMessage(), iox );
						file.error( iox.getMessage() );
						file.setIncluded( false );
					}
				}
				
				// clean up
				messageBox.clear();
				if( handleMismatch ==  MISMATCH_OPERATION.REMOVE ) {
					resultList.removeAll( exlusionSet );
				}
				
			} catch (PatternSyntaxException px) {
				threadLog.error( "PatternSyntaxException: " + px.getMessage(), px);
				messageBox.error( localizer.localize("message.syntax-error", 
									new Object[]{px.getMessage()} ));							
			} catch (Exception x) {
				threadLog.error( x.getClass().getName() + ": " +x.getMessage(), x);
				messageBox.error( x.getMessage() );
				resultList.clear();
			}	
			notifyTerminated( OperationType.FILTER );
			threadLog.debug(matchCount + " matches in content filter");
		}		
	}
}
