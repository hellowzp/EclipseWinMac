package net.pandoragames.far;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Methods for selecting the files on which to work. 
 * This class provides some facilities for concurrently running Threads to abort an 
 * ongoing search, and to retrieve the number of files found by such a search running in
 * parallel. Note that the methods of this class are not synchronized, and that 
 * other methods but {@link #abort()} and {@link #getFileCounter()} should
 * not be called by concurrently running Threads.
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
public class FileSelector {

	// directory filter
    private DirectoryFilter directoryFilter = new DirectoryFilter();
    // max search depth
    private int maxDepth = 256;
    // counter for list() method
    private int fileCounter = 0;
    // counter for list() method
    private int totalCounter = 0;
    // abort flag
    private boolean abort = false;
    // directories that must be excluded
    private Pattern excludedDirectoriesPattern;
   
    
    /**
     * Returns the set of files under the indicated directory 
     * that matches the specified file name pattern.
     * @param pattern applied to file name
     * @param baseDirectory must not be null
     * @return set of matching files, possibly empty
     * @throws PatternException if the pattern is invalid
     * @throws IOException if baseDirectory denotes a file and not a directory
     */
    public Set<File> listFiles(Pattern pattern, File baseDirectory) 
    throws PatternException, IOException {
    	return listFiles(pattern, baseDirectory, null, null, null);
    }
    
    /**
     * Returns the set of files under the indicated directory 
     * that matches the specified file name pattern.
     * @param pattern applied to file name
     * @param baseDirectory must not be null
     * @param after file filter. Files before this date will be discarded. May be null.
     * @param before file filter. Files after this date will be discarded. May be null.
     * @param directoryMatcher if not null, only files with an ancestor directory (below the base directory) matching this pattern will be included in the result
     * @return set of matching files, possibly empty
     * @throws PatternException if the pattern is invalid
     * @throws IOException if baseDirectory denotes a file and not a directory
     */
    public Set<File> listFiles(Pattern pattern, File baseDirectory, Date after, Date before, Pattern directoryMatcher) 
    	    throws PatternException, IOException {
    	if(baseDirectory == null)  throw new NullPointerException("Base directory must not be null");
        if (!baseDirectory.isDirectory()) throw new IOException(baseDirectory.getPath() + " does not denote a directory");   
        abort = false; // make sure abort is reset
        Set<File> fileSet = new HashSet<File>();
        PatternFilter filter = new PatternFilter( null, true, pattern );
        listFilesRecursive( filter, baseDirectory, fileSet, after, before, directoryMatcher, 0, directoryMatcher == null );
        fileCounter = 0;	// reset the file counter
        totalCounter = 0; // reset the total counter
        abort = false; // reset the abort flag
        return fileSet;
    }
    
    
    /**
     * Returns the <code>FilenameFilter</code> for the specified pattern
     * and base directory. If the argument 'baseDirectory' is null, the filter
     * won't take the directory into consideration. Otherwise it will only filter
     * files below the specified directory.
     * @param pattern applied to file name
     * @param baseDirectory if null the filter will ignore the directory.
     * @return <code>FilenameFilter</code> that for specified pattern
     * @throws IOException if baseDirectory denotes a file and not a directory
     */
    public FilenameFilter getFilter(Pattern pattern, File baseDirectory) 
    throws IOException {
        return new PatternFilter(baseDirectory, true, pattern);
    }

    /**
     * Directories matching the specified pattern will be excluded from further search.
     * Neither files directly from these directories, nor from any of its subdirectories 
     * will be part of the result list. Null is accepted to clear this property.
     * @param excludedNames directories to be excluded
     */
    public void setDirectoriesExcluded(Pattern excludedNames) {
    	excludedDirectoriesPattern = excludedNames;
    }
    
    
    /**
     * Renames the file using the "pattern" to 
     * generate capturing groups that may be used in the replacement string. Note that this
     * method will return the original file object if the file was not renamed because
     * the pattern can not be applied to its name.
     * @param file to be renamed
     * @param pattern applied on each file name to find capturing groups
     * @param replacement defining the new file name
     * @return the renamed file, or the original file if the pattern could not be applied to
     * the file name.
     * @throws IOException if the file could not be renamed because a file with that name allready
     * exists, because the file is write protected or because it does not exist
     * @throws PatternException if the replacement string is invalid
     * @throws UnmatchedGroupReferenceException if the replacement string references more groups than
     * capturing groups defined in the pattern.
     */
    public File rename(File file, Pattern pattern, ReplacementString replacement) 
    throws IOException, PatternException {
    	if(! file.exists()) throw new FileNotFoundException("The file " + file.getPath() + " does not exist");
    	if(! file.isDirectory()) throw new FileNotFoundException("The file " + file.getPath() + " denotes a directory");
    	String oldName = file.getName();
    	String newName = rename(oldName, pattern, replacement );
    	if(oldName.equals(newName)) return file;
    	File target = new File( file.getParent(), newName );
    	if( target.exists() ) throw new FileExistsException( newName, file.getParentFile() );
    	if(! file.renameTo( target )) throw new IOException("File " + oldName + " could not be renamed to " + newName);
        return target;
    }

	/**
	 * Allows to test the result of a renaming operation.
	 * If the specified pattern matches the string, it will be replaced by the replacement string,
	 * applying capturing group matches beforehand. If the pattern does not match, the
	 * input string will be returned unchanged.
	 * @param input to be replaced
	 * @param pattern matching the string, possibly containing capturing groups
	 * @param replacement replacement string, possibly containing group references
	 * @return transformed string
	 */
    public static String rename(String input, Pattern pattern, ReplacementString replacement) {
        Matcher matcher = pattern.matcher( input );
        if(matcher.matches()){
        	try {
        		return matcher.replaceAll( replacement.getCanonicalString() );
        	}catch(IndexOutOfBoundsException ioobx) {
        		throw new UnmatchedGroupReferenceException( ioobx.getMessage() );
        	}
        } else {
            return input;
        }
    }
 
//  -- attribute acccessors ---------------------------------------------------------------------------------------------------------------------------------
     
    /**
     * Sets the maximal depth for file searches with the <code>listFiles()</code> operations.
     * @param depth maximal depth for file searches
     */
    public void setMaxSearchDepth(int depth) {
    	if( depth >= 0 ) {
    		maxDepth = depth;
    	}
    }
    
    /**
     * Returns the maximal depth for file searches.
     * @return maximal depth for file searches
     */
    public int getMaxSearchDepth()  {
    	return maxDepth;
    }
    
    /**
     * Allows to retreive the number of files <b>found</b> by the <code>listFiles()</code>
     * method, when called from a different thread. Returns 0 (zero) after
     * the <code>listFiles()</code> method terminates. 
     * @return number of files already found if <code>listFiles()</code>
     * is running in a different thread.
     */
    public int getFileCounter() {
    	return fileCounter;
    }
    
    /**
     * Allows to retreive the number of files <b>tested</b> by the <code>listFiles()</code>
     * method, when called from a different thread. Returns 0 (zero) after
     * the <code>listFiles()</code> method terminates. 
     * @return number of files already tested if <code>listFiles()</code>
     * is running in a different thread.
     */    
    public int getTotalCounter() {
    	return totalCounter;
    }
    
    /**
     * This method may be called by a concurently running thread to abort a search
     * started with the <code>listFiles()</code>.
     */
    public void abort() {
    	abort = true;
    }
    
    
// -- private methods ------------------------------------------------------------------------------------------------------------------
    
    /**
     * Recursively lists the files in the specified directory, using the specified "filter" and descending
     * as far as the current value of <b>maxSearchDepth</b> allows.
     * @param filter to be used for files
     * @param directory where to start
     * @param fileSet where to add found files
     * @param currentDepth current search depth
     */
    private void listFilesRecursive(PatternFilter filter, File directory, Set<File> fileSet, Date after, Date before, Pattern directoryMatcher, int currentDepth, boolean forceInclude) {
    	// has search been canceled ?
    	if( abort ) return;
    	
    	// first list files
    	File[] fileList = directory.listFiles( filter );
		totalCounter = totalCounter + filter.getCallCount();
    	
    	// if the directory is read protected, the list may be null
    	if(fileList == null) return;
    	
    	// once a directory has matched, forceInclude will be set and files from all subdirectories will be included
    	boolean doInclude = forceInclude || (directoryMatcher != null && directoryMatcher.matcher(directory.getName()).matches());
    	
    	// add found files to the list
    	if( doInclude ) {
	    	for(File file : fileList) {
	    		if(file.isFile() 
	    				&& (after == null || after.getTime() < file.lastModified())
	    				&& (before == null || file.lastModified() < before.getTime())) {
	    			fileSet.add( file );
	    			fileCounter++;
	    		} 
	    	}
    	}
    	
		// now recurse
		if(currentDepth < maxDepth) {
			File[] dirList = directory.listFiles( directoryFilter );
	    	for(File file : dirList) {
	    		listFilesRecursive( filter, file, fileSet, after, before, directoryMatcher, currentDepth + 1, doInclude);
	    	}
		}
    }
    
// -- inner class ---------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Filters directories.
     * @author Olivier Wehner at 21.02.2008
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
    class DirectoryFilter implements FileFilter {
    	
    	/**
    	 * Returns true if the specified file is a directory.
    	 * @see java.io.FileFilter#accept(java.io.File)
    	 */
    	public boolean accept(File fileOrDir) {
    		if( excludedDirectoriesPattern == null ) {
    			return fileOrDir.isDirectory();
    		} else if(fileOrDir.isDirectory()) {
    			return ! excludedDirectoriesPattern.matcher( fileOrDir.getName() ).matches();
    		} else {
    			return false;
    		}
    	}
    }

}
