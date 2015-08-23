package net.pandoragames.far.ui.model;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Saves form data into property sets and vice versa.
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
public abstract class FormPropertySet<O extends OperationForm> {

	private static final FindFormPropertySet findFormSet = new FindFormPropertySet();
	
	private static final ReplaceFormPropertySet replaceFormSet = new ReplaceFormPropertySet();
	
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	/**
	 * Reads the properties from the map into the form object.
	 * @param form to be updated withe the map
	 * @param properties where to read the data from
	 */
	public abstract void load(O form, Properties properties);
	
	/**
	 * Write the form data as properties.
	 * @param form to be saved as properties map
	 * @param properties where to write the properties to
	 */
	public abstract void store(O form, Properties properties);
	
	/**
	 * Returns an instance for a find form.
	 * @return FindForm instance
	 */
	public static FormPropertySet<FindForm> getFindFormPropertySet() {
		return findFormSet;
	}
	
	/**
	 * Returns an instance for a replace form.
	 * @return ReplaceForm instance
	 */
	public static FormPropertySet<ReplaceForm> getReplaceFormPropertySet() {
		return replaceFormSet;
	}
	
	protected Date parseDate(String propertyName, Properties properties ) {
		String value = properties.getProperty( propertyName );
		if( value == null || value.isEmpty() ) return null;
		try {
			return dateFormat.parse( value );
		} catch( ParseException px) {
			throw new IllegalArgumentException( "Can not parse '" + value + "' as a date for " + propertyName, px);
		}
	}
}

class FindFormPropertySet extends FormPropertySet<FindForm> {

	private static final String BASEDIR = "baseDirectory";
	private static final String SUBDIRFLAG = "includeSubDirs";
	private static final String DIRECTORY_PATTERN = "directoryPattern";
	private static final String EXCLUDE_MATCHING_DIRS = "excludeMatchingDirectories";
	private static final String DIRECTORY_PATTERN_REGEX = "directoryPatternIsRegex";
	
	private static final String FILENAMEPATTERN = "fileNamePattern";
	private static final String FILENAMEPATTERN_REGEX = "fnpRegex";

	private static final String SEARCHSTRING = "searchRegexString";
	private static final String ISREGEX = "regexContentPattern";
	private static final String ISINVERT = "invertContentFilter";
	private static final String IGNORECASE = "ignoreCase";
	
	private static final String DATE_BEFORE = "dateBefore";
	private static final String DATE_AFTER = "dateAfter";

	public void load(FindForm form, Properties properties) {
		if( properties.containsKey(BASEDIR) ) form.setBaseDirectory( new File(properties.getProperty(BASEDIR)) );
		if( properties.containsKey(SUBDIRFLAG) ) form.setIncludeSubDirs( Boolean.parseBoolean( properties.getProperty(SUBDIRFLAG) ));
		if( properties.containsKey(DIRECTORY_PATTERN) ) {
			FileNamePattern pattern = new FileNamePattern( properties.getProperty(DIRECTORY_PATTERN), 
					Boolean.parseBoolean( properties.getProperty(DIRECTORY_PATTERN_REGEX)) );
			form.setDirectoryPattern(pattern);
		}
		if( properties.containsKey(EXCLUDE_MATCHING_DIRS) ) form.setExcludeMatchingDirectories(Boolean.parseBoolean( properties.getProperty(EXCLUDE_MATCHING_DIRS) ));

		if( properties.containsKey(FILENAMEPATTERN) ) {
			FileNamePattern pattern = new FileNamePattern( properties.getProperty(FILENAMEPATTERN), 
											Boolean.parseBoolean( properties.getProperty(FILENAMEPATTERN_REGEX)) );
			form.setFileNamePattern(pattern);
		}
		if( properties.containsKey(DATE_BEFORE) ) {
			form.setBefore(parseDate(DATE_BEFORE, properties ));
		} else {
			form.setBefore(null);
		}
		if( properties.containsKey(DATE_AFTER) ) {
			form.setAfter(parseDate(DATE_AFTER, properties ));
		} else {
			form.setAfter(null);
		}
		
		if( properties.containsKey(SEARCHSTRING) ) form.setSearchStringContent(properties.getProperty(SEARCHSTRING));
		if( properties.containsKey(ISREGEX) ) form.setRegexContentPattern(Boolean.parseBoolean( properties.getProperty(ISREGEX) ));
		if( properties.containsKey(IGNORECASE) ) form.setIgnoreCase(Boolean.parseBoolean( properties.getProperty(IGNORECASE) ));
		if( properties.containsKey(ISINVERT) ) form.setInvertContentFilter(Boolean.parseBoolean( properties.getProperty(ISINVERT) ));
		form.fireFormUpdateEvent();
	}
	
	public void store(FindForm form, Properties properties) {
		properties.setProperty(BASEDIR,  form.getBaseDirectory().getAbsolutePath() );
		properties.setProperty(SUBDIRFLAG, String.valueOf(form.isIncludeSubDirs() ));
		if( form.isIncludeSubDirs() && form.getDirectoryPattern() != null ) {
			properties.setProperty(DIRECTORY_PATTERN, form.getDirectoryPattern().getPattern());
			properties.setProperty(DIRECTORY_PATTERN_REGEX, String.valueOf(form.getDirectoryPattern().isRegex() ));
			properties.setProperty(EXCLUDE_MATCHING_DIRS, String.valueOf(form.isExcludeMatchingDirectories() ));
		}
		properties.setProperty(FILENAMEPATTERN,  form.getFileNamePattern().getPattern() );
		properties.setProperty(FILENAMEPATTERN_REGEX,  String.valueOf(form.getFileNamePattern().isRegex()) );
		if( form.getBefore() != null ) properties.setProperty(DATE_BEFORE, dateFormat.format( form.getBefore()));
		if( form.getAfter() != null ) properties.setProperty(DATE_AFTER, dateFormat.format( form.getAfter()));
		properties.setProperty(SEARCHSTRING,  form.getSearchStringContent() );
		properties.setProperty(ISREGEX, String.valueOf(form.isRegexContentPattern()) );
		properties.setProperty(ISINVERT, String.valueOf(form.isInvertContentFilter() ));
		properties.setProperty(IGNORECASE, String.valueOf(form.isIgnoreCase() ));
	}
}

class ReplaceFormPropertySet extends FormPropertySet<ReplaceForm> {

	private String SEARCH_STRING = "searchRegexString";
	private String ISREGEX = "regexContentPattern";
	private String IGNORECASE = "ignore";
	private String REPLACE_STRING = "replacement";
	private String GROUPREFERENCE = "groupReferenceIndicator";
	
	@Override
	public void load(ReplaceForm form, Properties properties) {
		if( properties.containsKey(SEARCH_STRING) ) form.setSearchStringContent( properties.getProperty(SEARCH_STRING) );
		if( properties.containsKey(ISREGEX) ) form.setRegexContentPattern( Boolean.parseBoolean( properties.getProperty(ISREGEX)) );
		if( properties.containsKey(IGNORECASE) ) form.setIgnoreCase( Boolean.parseBoolean( properties.getProperty(IGNORECASE) ));
		if( properties.containsKey(REPLACE_STRING) ) form.setReplacementString( properties.getProperty( REPLACE_STRING ));
		if( properties.containsKey(GROUPREFERENCE) && properties.getProperty(GROUPREFERENCE).length() > 0 ) form.setGroupReference( properties.getProperty(GROUPREFERENCE).charAt(0) );
		form.fireFormUpdateEvent();
	} 

	@Override
	public void store(ReplaceForm form, Properties properties) {
		properties.setProperty(SEARCH_STRING, form.getSearchStringContent());
		properties.setProperty(ISREGEX, String.valueOf( form.isRegexContentPattern()));
		properties.setProperty(IGNORECASE, String.valueOf( form.isIgnoreCase()));
		properties.setProperty(REPLACE_STRING, form.getReplacementString());
		properties.setProperty(GROUPREFERENCE, String.valueOf( form.getGroupReference()));
	}
	
}