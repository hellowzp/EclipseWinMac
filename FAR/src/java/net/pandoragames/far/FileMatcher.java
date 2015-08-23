package net.pandoragames.far;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.pandoragames.util.file.FileBuffer;
import net.pandoragames.util.file.FileUtil;

/**
 * Applies regular expressions on files. The class is created with a specific regular expression
 * <code>Pattern</code> that is applied when any of the methods
 * {@link FileMatcher#apply(File, ReplacementString) apply()}, {@link FileMatcher#matches(File) matches()} or
 * {@link FileMatcher#preview(File, ReplacementString) preview()} is called.<p>
 * 
 * Details of the behavior is controlled through the attributes <b>doBackup</b>, 
 * <b>backUpDirectory</b>, <b>baseDirectory</b> and <b>characterSet</b>. See the respective
 * setter methods for details.
 * @author Olivier Wehner at 24.02.2008
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
public class FileMatcher
{
	private boolean doBackup = true;
	private File backUpDirectory;
	private boolean backUpDirTested = false;
	private File baseDirectory;
	private Pattern pattern;
	private Charset charset = Charset.defaultCharset();
	
	/**
	 * The class must be instantiated with a regular expression <code>Pattern</code>.
	 * @param regex to be applied to files
	 */
	public FileMatcher(Pattern regex)
	{
		if(regex == null) throw new NullPointerException("Pattern must not be null");
		pattern = regex;
		backUpDirectory = new File( System.getProperty("java.io.tmpdir") ); 
	}

	/**
	 * Returns true if the regex pattern applies to some substring in the specified file.
	 * @param file to be tested
	 * @return true if this instances regex pattern applies to some substring
	 * @throws IOException if the file can not be found or read
	 */
	public boolean matches(File file) throws IOException {
		testFile( file );
		FileBuffer charSequence = null;
		try {
			charSequence = new FileBuffer( file, charset );
			return pattern.matcher( charSequence ).find();
		} finally {
			if( charSequence != null ) charSequence.close();
		}
	}
	
	/**
	 * Returns the number of times the reges pattern can be found in the 
	 * specified file.
	 * @param file to be tested
	 * @return number of matches
	 * @throws IOException if the file can not be found or read
	 */
	public int countMatches(File file) throws IOException {
		testFile( file );
		FileBuffer charSequence = null;
		try {
			charSequence = new FileBuffer( file, charset );
			Matcher matcher = pattern.matcher( charSequence );
			boolean found = false;
			int count = 0;
			do {
				found = matcher.find();
				if( found ) count ++;
			} while( found );
			return count;
		} finally {
			if( charSequence != null ) charSequence.close();
		}
	}
	
	/**
	 * Applies the regex pattern and the specified replacement string to the file and returns
	 * the result as string.
	 * Allow to test the effect of the {@link FileMatcher#apply(File, ReplacementString) apply()} method on 
	 * some particula file. Returns the string the file would contain if <code>apply()</code>
	 * was called.
	 * @param file subject of replacement but <b>not altered</b>
	 * @param replacement to be applied in conjunction with this instances regex pattern
	 * @return result of the replacement.
	 * @throws IOException  if the file can not be found or read
	 */
	public String preview(File file, ReplacementString replacement) throws IOException {
		testFile( file );
		FileBuffer charSequence = null;
		try {
			charSequence = new FileBuffer( file, charset );
			return pattern.matcher( charSequence ).replaceAll( replacement.getCanonicalString() );
		} finally {
			if( charSequence != null ) charSequence.close();
		}
	}

	/**
	 * Applies the regex pattern and the specified replacement string to the file.
	 * @param file subject of replacement 
	 * @param replacement to be applied in conjunction with this instances reges pattern
	 * @return true if file has been changed, false if it was not altered.
	 * @throws IOException if the file can not be found, read, written or backed up.
	 */
	public int apply(File file, ReplacementString replacement) throws IOException {
		testFile( file );
		if(! file.canWrite() ) throw new IOException("The file " + file.getPath() +" can not be changed. Please check the access rights");
		if (! backUpDirTested ) testBackUpDirectory();
		return applyConservative(file, replacement);
		// return applyFast(file, replacement);
	}
	
	private int applyFast(File file, ReplacementString replacement) throws IOException {
		FileBuffer charSequence = null;
		try {
			charSequence = new FileBuffer( file, charset );
			Matcher matcher = pattern.matcher( charSequence );
			if( matcher.find() ) { 
				File tempFile = File.createTempFile("FAR", ".tmp", backUpDirectory);
				tempFile.deleteOnExit();
				int matchCount = writeChangesToFile( matcher, replacement.getCanonicalString(), tempFile );
				charSequence.close();
				
				File backup = null;
				if( doBackup ) {
					backup = getBackupFileName( file );
					// On Windows: target file must not exist
					if( backup.isFile() ) backup.delete();
				} else {
					backup = File.createTempFile("FAR", ".bkp", backUpDirectory);
					// On Windows: target file must not exist
					backup.delete();
				}
				// if the backup can not be created - abort
				if(! file.renameTo( backup ) ) throw new IOException("Could not rename " + file.getPath() 
																	+ " to " + backup.getPath() + " for (temporary) backup."
																	+ " Another process may have locked the file.");
				// if the replacement cant be established - try roleback
				if(! tempFile.renameTo( file ) ) {
					if( backup.renameTo(file)) {
						throw new IOException("Could not write to " + file.getPath() );
					} else {
						throw new IOException("Could not restore " + file.getPath()
								+ ". Please find a backup at " + backup.getPath()
								+ ". Could not write to " + file.getName());					
					}
				}
				// if backup was disabled - remove temporary backup
				if( ! doBackup ) {
					backup.delete();
				}
				tempFile.delete();
				return matchCount;
			} else {
				// no match in this file - do nothing
				return 0;
			}
		} finally {
			if( charSequence != null ) charSequence.close();
		}
	}
	
	private int applyConservative(File file, ReplacementString replacement) throws IOException {
		FileBuffer charSequence = null;
		File backup = null;
		try {
			if( doBackup ) {
				backup = getBackupFileName( file );
				if( ! backup.isFile() ) {
					if( ! backup.createNewFile() ) throw new IOException("Could not create backup file " + backup.getPath());
				}
			} else {
				backup = File.createTempFile("FAR", ".bkp", backUpDirectory);
			}
			FileUtil.copy(file, backup);			
		} catch(IOException iox) {
			throw new IOException("Could not copy " + file.getPath() 
					+ " to " + backup.getPath() + " for (temporary) backup "
					+ " due to IOException: " + iox.getMessage());
		}
		int matchCount = 0;
		charSequence = new FileBuffer( backup, charset );
		Matcher matcher = pattern.matcher( charSequence );
		if( matcher.find() ) { 
			long lastmodified = file.lastModified();
			try {
				try{
					matchCount = writeChangesToFile( matcher, replacement.getCanonicalString(), file );
				} finally {
					if( charSequence != null ) charSequence.close();
				}
			} catch(Exception x) {
				if( lastmodified != file.lastModified() ) {
					// try to establish the backup
					if(file.delete() && backup.renameTo(file)) {
						return 0;
					} else {
						throw new IOException(x.getClass().getSimpleName() + " applying changes: " + x.getMessage()
								+ ". Could not restore " + file.getPath()
								+ ". Please find a backup at " + backup.getPath() + ".");			
					}
				}
			}			
			// if backup was disabled - remove temporary backup
			if( ! doBackup ) {
				backup.delete();
			}
		}
		return matchCount;
	}
	

	/**
	 * Returns the abstract path name of the backup file for the
	 * specified "original" file, given the current base directory
	 * and current backup directory. The method does not create the
	 * file, nore does it check if the file exists.
	 * @param originalFile to be backed up
	 * @return abstract path name of the back up file
	 */
	public File getBackupFileName(File originalFile) {
		if (( baseDirectory == null ) || ( originalFile.getParentFile() == null ) // parent can only be null if misused 
				|| ( baseDirectory.equals( originalFile.getParentFile() ))){
			return new File( backUpDirectory, originalFile.getName() );
		} else {
			ArrayList<String> parentList = new ArrayList<String>();
			File parent = originalFile.getParentFile();
			do {
				parentList.add( parent.getName() );
				parent = parent.getParentFile();
			} while ((parent != null) && (! parent.equals(baseDirectory)));
			Collections.reverse( parentList );
			StringBuffer relativePath = new StringBuffer();
			for(String dir : parentList) {
				relativePath.append( dir ).append( File.separatorChar );
			}
			File targetDir = new File( backUpDirectory, relativePath.toString() );
			if( ! targetDir.exists() ) targetDir.mkdirs();
			return new File( targetDir, originalFile.getName() );
		}
	}

// -- attribute accessor -----------------------------------------------------------------------------------------------------	
	
	/**
	 * Returns the backup directory.
	 * @see FileMatcher#setBackUpDirectory(File)
	 * @return backup directory.
	 */
	public File getBackUpDirectory()
	{
		return backUpDirectory;
	}

	/**
	 * Sets the  backup directory. If the <code>doBackup</code> flag is set to true, files will
	 * be saved under this directory. The default for this attribute is defined by the java system 
	 * property "java.io.tmpdir". 
	 * @param backUpDirectory backup directory.
	 */
	public void setBackUpDirectory(File backUpDirectory)
	{
		if( backUpDirectory == null ) throw new NullPointerException("Backup directory must not be null");
		this.backUpDirectory = backUpDirectory;
		backUpDirTested = false;
	}

	/**
	 * Returns the doBackup flag. 
	 * @see FileMatcher#setDoBackup(boolean)
	 * @return doBackup flag
	 */
	public boolean isDoBackup()
	{
		return doBackup;
	}

	/**
	 * Sets the  doBackup flag. If set to true (the default), files will be saved in the
	 * current <code>backup directory</code> before any replacement is applied.
	 * @param doBackup flag should files be backed up?
	 */
	public void setDoBackup(boolean doBackup)
	{
		this.doBackup = doBackup;
	}
	
	/**
	 * Returns the character set used to read and write files.
	 * @see FileMatcher#setCharacterSet(Charset)
	 * @return charset character set
	 */
	public Charset getCharacterSet()
	{
		return charset;
	}

	/**
	 * Sets the  character set used to read and write files.
	 * The default is to use the platforms default character set.
	 * @param charset character set
	 */
	public void setCharacterSet(Charset charset)
	{
		this.charset = charset;
	}
	
	/**
	 * Returns the <i>base</i> directory for backup.
	 * @see FileMatcher#setBaseDirectory(File)
	 * @return baseDirectory
	 */
	public File getBaseDirectory()
	{
		return baseDirectory;
	}

	/**
	 * Sets the <i>base</i> directory. If this attribute is set to a non null value, backed up files will
	 * be resolved relative to the specified base directory. The entire path below that directory
	 * will be copied to the <i>backup</i> directory, creating a copy of the subdirectory tree.
	 * This avoids overwriting files with identical names in different subdirectories during  
	 * replacement operations on larger file sets.
	 * @param baseDirectory base directory
	 */
	public void setBaseDirectory(File baseDirectory)
	{
		this.baseDirectory = baseDirectory;
	}

// -- private methods --------------------------------------------------------------------------------------------------
	
	/**
	 * Tests the file for existance and readabiltiy.
	 * @throws IOException if the specified file does not exist, does not denote a fiel (but rather a directory)
	 * or if it can not be read.
	 */
	private void testFile(File file) throws IOException {
		if( file == null ) throw new NullPointerException("File was null");
		if( ! file.exists() ) throw new FileNotFoundException("The fiel " + file.getPath() + " could not be found");
		if( ! file.isFile() ) throw new FileNotFoundException(file.getPath() + " is a directory (not a file)");
		if( ! file.canRead() ) throw new IOException("File " + file.getPath() +" could not be opend");
	}
	
	private void testBackUpDirectory() throws IOException {
		if( ! backUpDirectory.isDirectory() ) throw new FileNotFoundException("The current temp/backup directory " 
																		+ backUpDirectory.getPath() + " can not be found");
		if( ! backUpDirectory.canWrite() ) throw new FileNotFoundException("The current temp/backup directory " 
				+ backUpDirectory.getPath() + " is write proteced");
		File testFile = File.createTempFile("FAR", ".tst", backUpDirectory);
		testFile.deleteOnExit();
		testFile.delete();
		backUpDirTested = true;
	}

	/**
	 * Applies the changes to the character sequence mapped in the matcher and
	 * writes them to the supplied file. 
	 * <p>
	 * The method assumes that <code>matcher.find()</code> was called <b>successfully</b> before!
	 * </p>
	 * @param matcher holding a successful match
	 * @param replacement replacement string
	 * @param target file to write to
	 * @return number of matches processed
	 * @throws IOException if writing the file fails
	 */
	private int writeChangesToFile(Matcher matcher, String replacement, File target) throws IOException {
		BufferedWriter writer = new BufferedWriter( 
									new OutputStreamWriter( 
											new FileOutputStream(target), charset ));
		try {	
			StringBuffer buffer = new StringBuffer();
			int count = 0;
			 do {
				 count++;
			     matcher.appendReplacement(buffer, replacement);
			     writer.write( buffer.toString() );
			     buffer = new StringBuffer();
			 } while (matcher.find());
			 matcher.appendTail( buffer );
		     writer.write( buffer.toString() );
		     return count;
		} finally {
			try { writer.close(); } catch (IOException iox) { /* ignore */ }			
		}
		
	}
}
