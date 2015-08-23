package net.pandoragames.far.ui.swing.component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.pandoragames.far.ui.model.Resetable;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.model.TargetFileComparator;
import net.pandoragames.util.file.FileUtil;
import net.pandoragames.util.i18n.DummyLocalizer;
import net.pandoragames.util.i18n.Localizer;

/**
 * TableModel for the file list. The methods of this class can safely be called
 * by any non-event thread, since the class it self will take care to inform all
 * TableModelListeners via the event queue. Nonetheless it should be noted that
 * the methods of this class are <i>not</i> threadsafe.
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
public class FileSetTableModel implements TableModel, Resetable
{
	private static final String[] columnHeaderCode = new String[] {"VOID", 
																  "label.name", 
																  "label.path", 
																  "label.preview-rename"};
	private List<TargetFile> fileSet;
	private List<TableModelListener> listener;
	private  Localizer localizer;
	private File baseDir;
	private TargetFileComparator comparator = TargetFileComparator.orderByPath();
	
	/**
	 * Must be instantiated with a non null file list and a Localizer instance.
	 * @param fileList must not be null
	 * @param localizer for translation of column header
	 */
	public FileSetTableModel(List<TargetFile> fileList, Localizer localizer) {
		if( fileList == null ) throw new NullPointerException("File list must not be null");
		for(TargetFile row : fileList) {
			disableIfNotExists( row );
		}
		Collections.sort(fileList, comparator);
		fileSet = fileList;
		setLocalizer( localizer );
	}
	
	/**
	 * Resets all rows to the elements from the specified List. The base directory parameter
	 * is used for formatting. Calls to {@link #getValueAt(int, int) getValueAt(row, 2)} will
	 * return a relative path, if the respective file object resides under the base directory.
	 * @param fileList new table content
	 * @param baseDirectory common base directory for this file set. May be null.
	 */
	public void setFileList(List<TargetFile> fileList, File baseDirectory) {
		if( fileList == null ) throw new NullPointerException("File list must not be null");
		if(( fileSet != null ) && ( fileSet.size() > 0 )) {
			callListener( new TableModelEvent( this, 0, fileSet.size() - 1,  TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE) );			
		}
		for(TargetFile row : fileList) {
			disableIfNotExists( row );
		}
		Collections.sort(fileList, comparator);
		fileSet = fileList;
		if( fileSet.size() > 0 ) {
			callListener( new TableModelEvent( this, 0, fileSet.size() - 1,  TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT) );
		}
		baseDir = baseDirectory;
	}
	
	/**
	 * Sets the Localizer that should be used to translate the
	 * column header.
	 * @param localizer for translations
	 */
	public void setLocalizer( Localizer localizer ) {
		if( localizer == null ) {
			this.localizer = new DummyLocalizer();
		} else {
			this.localizer = localizer;
		}		
	}
	
	/**
	 * Clears the internal row list.
	 */
	public void reset() {
		setFileList( new ArrayList<TargetFile>(), null );
	}
	
	/**
	 * Resets the info column to default.
	 */
	public void clearInfo() {
		if( fileSet.size() > 0 ) {
			for(TargetFile file : fileSet) {
				file.clear();
			}
			callListener( new TableModelEvent( this, 0, fileSet.size() - 1,  3, TableModelEvent.UPDATE) );
		}
	}
	
	/**
	 * Triggers a call to the registered TableModelListeners
	 * in order to force a repaint operation.
	 */
	public void notifyUpdate() {
		if( fileSet.size() > 0 ) {
			callListener( new TableModelEvent( this, 0, fileSet.size() - 1,  TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE) );
		}
		
	}
	
	/**
	 * Returns the number of currently selected rows.
	 * @return number of selected rows
	 */
	public int getSelectedRowCount() {
		int counter = 0;
		for( TargetFile file : fileSet ) {
			if( file.isSelected() ) counter++;
		}
		return counter;
	}
	
	/**
	 * Returns the set of selected rows.
	 * @return selected rows
	 */
	public List<TargetFile> getSelectedRows() {
		List<TargetFile> selection = new ArrayList<TargetFile>();
		for( TargetFile file : fileSet ) {
			if( file.isSelected() ) selection.add( file );
		}
		return selection;
	}
	
	/**
	 * Returns the number of included rows.
	 * @return number of included rows
	 */
	public int getIncludedRowCount() {
		int counter = 0;
		for( TargetFile file : fileSet ) {
			if( file.isIncluded() ) counter++;
		}
		return counter;		
	}
	
	/**
	 * Returns the index of the specified file
	 * in this table or -1 if it is not present.
	 * @param file to be looked up
	 * @return row index or -1 if not found
	 */
	public int getRowIndex(TargetFile file) {
		if( file == null ) return -1;
		for(int i = 0; i < fileSet.size(); i++) {
			if(file.equals(fileSet.get(i))) return i;
		}
		return -1;
	}
	
	/**
	 * Returns the row object for the specified index.
	 * @param rowIndex zero base row index
	 * @return row object
	 */
	public TargetFile getRow(int rowIndex) {
		return fileSet.get( rowIndex );
	}
	
	/**
	 * Replace the indicated row with the specified new object.
	 * If the new row is null, the call will be ignored.
	 * @param rowIndex zero base row index
	 * @param row new row object
	 */
	public void setRow(int rowIndex, TargetFile row) {
		if( row == null ) return;
		disableIfNotExists( row );
		fileSet.set(rowIndex, row);
		callListener( new TableModelEvent( this, rowIndex, rowIndex,  TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE) );
	}
	/**
	 * Removes the indicated row from this table model.
	 * @param rowIndex zero base row index
	 */
	public void deleteRow(int rowIndex) {
		fileSet.remove( rowIndex );
		callListener( new TableModelEvent( this, rowIndex, rowIndex,  TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE) );
	}
	
	/**
	 * Returns the entire row set
	 * @return all rows at once
	 */
	public List<TargetFile> listRows() {
		return fileSet;
	}
	
	/**
	 * Returns the base directory that was specified when the file set was set.
	 * @return base directory, possibly null
	 */
	public File getBaseDirectory() {
		return baseDir;
	}
	
	/**
	 * Resorts the table by the specified column. 
	 * @param columnIndex only 1 or two can trigger a resort
	 */
	public void sortByColumn(int columnIndex) {
		if( columnIndex < 0 || columnIndex >= columnHeaderCode.length ) {
			throw new IndexOutOfBoundsException("Illegal column index: " + columnIndex );
		}
		if(comparator == null) return; // no state change if table is empty 
		if( columnIndex == 1 ) {
			if( comparator.isOrderByName() ) {
				comparator.invert();
			} else {
				comparator = TargetFileComparator.orderByName();				
			}
		} else if( columnIndex == 2 ) {
			if( comparator.isOrderByName() ) {
				comparator = TargetFileComparator.orderByPath();
			} else {
				comparator.invert();
			}
		} else {
			// other columns can not be sorted
			return;
		}
		Collections.sort(fileSet, comparator);
		if( fileSet.size() > 0 ) {
			callListener( new TableModelEvent( this, 0, fileSet.size() - 1,  TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE) );
		}
	}
	
	
// -- methods of interface TableModel --------------------------------------------------	
	
	/**
	 * {@inheritDoc}
	 */
	public int getRowCount()
	{
		return fileSet.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getColumnCount()
	{
		return columnHeaderCode.length;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getColumnName(int columnIndex)
	{
		return localizer.localize( columnHeaderCode[ columnIndex ] );
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<?> getColumnClass(int columnIndex)
	{
		if( columnIndex == 0 ) {
			return Boolean.class;
		} else {
			return String.class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return ( columnIndex == 0 ) && fileSet.get( rowIndex ).getFile().exists();
	}

	/**
	 * Returns the following values from the underlying file object:<br>
	 * <ul>
	 * <li>column 0: {@link net.pandoragames.far.ui.model.TargetFile#isSelected() TargetFile.isSelected()}</li>
	 * <li>column 1: {@link net.pandoragames.far.ui.model.TargetFile#getName() TargetFile.getName()}</li>
	 * <li>column 2: {@link net.pandoragames.far.ui.model.TargetFile#getPath() TargetFile.getPath()}, 
	 * formated as relative path with respect to the base directory</li>
	 * <li>column 3: {@link net.pandoragames.far.ui.model.TargetFile#getNewName() TargetFile.getNewName()}</li>
	 * </ul>
	 * @param rowIndex zero based row number
	 * @param columnIndex zero based column index
	 * @return property from underlying TargetFile
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		TargetFile row = fileSet.get( rowIndex );
		switch( columnIndex ) {
			case 0 : return new Boolean( row.isSelected() );
			case 1 : return row.getName();
			case 2 : return formatPath( row.getPath() );
			case 3 : return row.getNewName();
			// case 4 : return row.getErrorMessage();
			default : throw new IndexOutOfBoundsException("Illegal column index: " + columnIndex );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValueAt(Object value, int rowIndex, int columnIndex)
	{
		if( columnIndex == 0) {
			fileSet.get( rowIndex ).setSelected( ((Boolean) value).booleanValue() );
			// trigger update on entire row
			callListener( new TableModelEvent( this, rowIndex, rowIndex,  TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE) );			
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTableModelListener(TableModelListener tmListener)
	{
		if( listener == null ) {
			listener = new ArrayList<TableModelListener>();
		}
		listener.add( tmListener );
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeTableModelListener(TableModelListener tmListener)
	{
		if( listener != null ) {
			listener.remove( tmListener );
		}
	}
	
	/**
	 * Informs the listener about a table change event.
	 * @param event to be broadcasted
	 */
	private void callListener( final TableModelEvent event) {
		if( listener != null ) {
			if( SwingUtilities.isEventDispatchThread() ) {
				for(TableModelListener tml : listener) {
					tml.tableChanged( event );
				}				
			} else {
				SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						for(TableModelListener tml : listener) {
							tml.tableChanged( event );
						}										
					}
				});
			}
		}
	}
	
	/**
	 * Displays only the part beneath the base directory.
	 * @param path to be formated
	 * @return path relative to base directory
	 */
	private String formatPath(String path) {
		if( path == null ) return null;
		if( baseDir == null ) {
			return path; 
		}
		return FileUtil.getRelativePath(baseDir.getPath(), path);
	}

	private void disableIfNotExists(TargetFile row) {
		if(! row.getFile().exists() ) {
			row.setSelected( false );
			row.setIncluded( false );
		}
	}
}
