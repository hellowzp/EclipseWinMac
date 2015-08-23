package net.pandoragames.far.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import net.pandoragames.far.ui.FilePropertiesFormater;
import net.pandoragames.far.ui.model.MimeType;
import net.pandoragames.far.ui.model.OperationType;
import net.pandoragames.far.ui.model.Resetable;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.component.FileListPopupMenu;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.far.ui.swing.component.TargetFileListTableCellRenderer;
import net.pandoragames.far.ui.swing.component.listener.ActionView;
import net.pandoragames.far.ui.swing.dialog.FileEditor;
import net.pandoragames.far.ui.swing.dialog.InfoView;
import net.pandoragames.util.i18n.Localizer;
import net.pandoragames.util.j6.DesktopHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Lists the files found by FAR.
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
public class FileListPanel extends JPanel
{
	private FileSetTableModel tableModel;
	private FileListPopupMenu fileListPopupMenu;
	private ActionView viewAction;
	private Localizer localizer;
	private Log logger;

	public FileListPanel(SwingConfig config, ComponentRepository repository) {
		logger = LogFactory.getLog( this.getClass() );
		localizer = config.getLocalizer();
		init( config, repository );
	}
	
	private void init(SwingConfig config, ComponentRepository componentRepository) {
		
		this.setLayout( new BorderLayout() );
		
		this.setBorder( BorderFactory.createEmptyBorder( 0, 
				SwingConfig.PADDING, 
				SwingConfig.PADDING, 
				SwingConfig.PADDING ) );
		
		tableModel = componentRepository.getTableModel();
		componentRepository.getResetDispatcher().addResetable( tableModel );
		componentRepository.getSearchBaseListener().addResetable( tableModel );
		componentRepository.getUndoListener().setTableModel( tableModel );
				
		JTable fileListTable = componentRepository.getFileSetTable();
		int totalWidth = fileListTable.getPreferredSize().width; 
		fileListTable.setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION );
		fileListTable.setColumnSelectionAllowed( true );
		fileListTable.getTableHeader().addMouseListener( new TableHeaderMouseListener() );
		fileListTable.getTableHeader().getColumnModel().getColumn(0).setHeaderRenderer( new TableHeaderCheckBoxColumnRenderer() );
		fileListPopupMenu = new FileListPopupMenu( fileListTable,
													tableModel,
													componentRepository,
													config);
		fileListTable.setComponentPopupMenu( fileListPopupMenu );
		fileListTable.addMouseListener( new FileViewOpener( fileListTable, 
															componentRepository.getRootWindow(),
															config,
															componentRepository) );
		fileListTable.getColumnModel().getColumn( 0 ).setPreferredWidth( 20 );
		fileListTable.getColumnModel().getColumn( 0 ).setMaxWidth( 20 );
		fileListTable.getColumnModel().getColumn( 1 ).setCellRenderer( new TargetFileListTableCellRenderer() );
		fileListTable.getColumnModel().getColumn( 1 ).setPreferredWidth( 2 * totalWidth / 5 );
		fileListTable.getColumnModel().getColumn( 2 ).setCellRenderer( new PathColumnRenderer() );
		fileListTable.getColumnModel().getColumn( 3 ).setCellRenderer( new InfoColumnRenderer( config ) );
	    JScrollPane scrollPane = new JScrollPane( fileListTable );
	    this.add( scrollPane, BorderLayout.CENTER );
	    
	    SelectCounter fileCounter = new SelectCounter();
	    fileCounter.setBorder( BorderFactory.createEmptyBorder(1, SwingConfig.PADDING, 2, SwingConfig.PADDING));
	    fileCounter.setForeground( Color.GRAY );
	    ErrorCounter errorCounter = new ErrorCounter();
	    errorCounter.setBorder( BorderFactory.createEmptyBorder(1, SwingConfig.PADDING, 2, SwingConfig.PADDING));
	    JPanel counterLine = new JPanel();
	    counterLine.setLayout( new BorderLayout() );
		counterLine.add( fileCounter, BorderLayout.WEST );
		counterLine.add( errorCounter, BorderLayout.EAST );
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setEnabled( false );
		progressBar.setMaximumSize( new Dimension( 100, 20 ));
		progressBar.setBorder( BorderFactory.createEmptyBorder(1, SwingConfig.PADDING, 2, SwingConfig.PADDING));
		componentRepository.getProgressBarUpdater().setProgressBar(progressBar);
		JPanel progressBarPanel = new JPanel();
		progressBarPanel.add( progressBar );
		counterLine.add( progressBarPanel, BorderLayout.CENTER );
		
		counterLine.setBorder( BorderFactory.createLineBorder( Color.GRAY, 1));
		this.add( counterLine, BorderLayout.SOUTH );

		tableModel.addTableModelListener( new ColumnCountListener( fileCounter ) );
		tableModel.addTableModelListener( errorCounter );
		componentRepository.getResetDispatcher().addResetable( fileCounter );
		componentRepository.getSearchBaseListener().addResetable( fileCounter );
		componentRepository.getOperationCallBackListener().addComponentStartReseted( fileCounter, OperationType.FIND );
		componentRepository.getResetDispatcher().addResetable( errorCounter );
		componentRepository.getSearchBaseListener().addResetable( errorCounter );
		
		viewAction = new ActionView(componentRepository, config);
}
	
// -- inner classes -----------------------------------------------------------------------------------------------------------------------------------------

	
	/**
	 * ColumnRenderer for the FileName column. 
	 */
	class FileNameColumnRenderer extends TargetFileListTableCellRenderer {
		/**
		 * {@inheritDoc}
		 */
		protected void decorate(JLabel renderer, TargetFile row, Object cellValue) {
			super.decorate( renderer, row, cellValue );
			if( row.getErrorMessage() != null ) {
				renderer.setToolTipText( String.valueOf( row.getErrorMessage() ) );
				renderer.setForeground( Color.RED );
			}
		}
	}

	
	/**
	 * ColumnRenderer for the Path column. 
	 */
	class PathColumnRenderer extends TargetFileListTableCellRenderer {
		/**
		 * {@inheritDoc}
		 */
		protected void decorate(JLabel renderer, TargetFile row, Object cellValue) {
			super.decorate( renderer, row, cellValue );
			renderer.setToolTipText( String.valueOf( cellValue ) );
			// renderer.setHorizontalAlignment( SwingConstants.TRAILING );
		}
	}
	
	/**
	 * ColumnRenderer for the Info column.
	 */
	class InfoColumnRenderer extends TargetFileListTableCellRenderer {
		private SwingConfig swingConfig;
		private FilePropertiesFormater formater = new FilePropertiesFormater();
		public InfoColumnRenderer(SwingConfig config) {
			swingConfig = config;
		}
		/**
		 * {@inheritDoc}
		 */
		protected void decorate(JLabel renderer, TargetFile row, Object cellValue) {
			super.decorate( renderer, row, cellValue );
			if( row.getErrorMessage() != null ) {
				// TODO; display this in a box or something
				renderer.setToolTipText( row.getErrorMessage() );
				renderer.setForeground( Color.RED );
				renderer.setText( row.getErrorMessage() );
			} else if((row.getNewName() != null) && ( row.isSelected() )) {
				renderer.setText( row.getNewName() );
			} else if( row.getInfoMessage() != null ) {
				renderer.setText( row.getInfoMessage() );
				renderer.setToolTipText( row.getInfoMessage() );
			} else {
				switch( swingConfig.getDefaultFileInfo() ) {
					case READONLY : if(row.getFile().canWrite()) {
										if( ! swingConfig.isProcessBinary() ) {
											if( row.isBinaryFile() ) {
												renderer.setText( localizer.localize("label.binary") );	
											} else {
												renderer.setText( "" );											
											}
										} else {
											renderer.setText( "" );											
										}
									} else if( row.getFile().exists() ) {
										renderer.setText(localizer.localize("label.read-only"));
									} else {
										renderer.setText(localizer.localize("label.deleted")); 
									}
									break;
					case SIZE : if(swingConfig.isShowPlainBytes()) {
									renderer.setText( String.valueOf( row.getFile().length() ) ); 
								} else {
									renderer.setText( formater.formatBytes(row.getFile().length(), localizer) ); 
								}
								break;
					case LAST_MODIFIED : renderer.setText( formater.timeStamp(row.getFile().lastModified()) ); break;
					case MIMETYPE : MimeType mime = row.getMimeType(); 
									if( mime != null ) {
										renderer.setText( mime.getName() );
									} else {
										renderer.setText(localizer.localize("label.unknown"));
									}
						break;
					case CHARSET : renderer.setText( row.getCharacterset().displayName() ); break;
					default : renderer.setText( "" ); break;
				}
				if( swingConfig.getDefaultFileInfo() == SwingConfig.DefaultFileInfo.SIZE ) {
					renderer.setHorizontalAlignment(SwingConstants.RIGHT);
				} else {
					renderer.setHorizontalAlignment(SwingConstants.LEFT);					
				}
				renderer.setToolTipText( null );
			}
		}		
	}
	
	/**
	 * Opens the file viewer.
	 */
	class FileViewOpener extends MouseAdapter {
		private JTable fileTable;
		private JFrame masterFrame;
		private SwingConfig swingConfig;
		private ComponentRepository repository;
		public FileViewOpener(JTable table, JFrame jFrame, SwingConfig config, ComponentRepository repository) {
			fileTable = table;
			masterFrame = jFrame; 
			swingConfig = config;
			this.repository = repository;
		}
		public void mouseClicked(MouseEvent e) {
			// right clicks are handled by the popupmenu directly
			if( MouseEvent.BUTTON1 == e.getButton() ) {
				int row = fileTable.getSelectedRow();
				int col = fileTable.getSelectedColumn();
				TargetFile targetFile = tableModel.getRow( row );
				
				if( col == 1 && e.getClickCount() > 1 ) {
					boolean binary = targetFile.isBinaryFile();
					SwingConfig.FILEVIEW fileView = binary ? swingConfig.getBinaryView() : swingConfig.getTextView();
					switch( fileView ) {
						case EDITOR : opeEditor( targetFile );		
										break;
						case INFO : openInfoView( targetFile ); break;
						case MENU : fileListPopupMenu.show( fileTable , e.getX(), e.getY()); break;
						case VIEW : viewAction.setSelectedFile( targetFile );
									viewAction.actionPerformed( new ActionEvent(fileTable, 0, "MOUSE") );
									// TODO: Warn if no view available
									break;
						case EXTERNAL : if(SwingConfig.getEffectiveJavaVersion() > 5 && DesktopHelper.isFileOpeningSupported()) {
											try {
												DesktopHelper.openFile( targetFile.getFile() );
											} catch(IOException iox) {
												targetFile.error( iox.getMessage() );
											}						
										} else {
											// fall back
											if( binary ) {
												swingConfig.setBinaryView( SwingConfig.FILEVIEW.INFO );
												openInfoView( targetFile );
											} else {
												swingConfig.setBinaryView( SwingConfig.FILEVIEW.EDITOR );
												opeEditor( targetFile );
											}
										}
										break;
						case NONE : break;
					}
				} else if( col == 3 
						&& targetFile.getErrorMessage() != null
						&& targetFile.getFile().exists() ) {
					openInfoView( targetFile );
				} else if( SwingConfig.isMacOSX() ){
					fileListPopupMenu.show( fileTable , e.getX(), e.getY()); 
				}
			}
		}
		private void opeEditor(TargetFile targetFile) {
			FileEditor editpr = new FileEditor( masterFrame, 
					targetFile, 
		  			swingConfig);
				editpr.pack();
				editpr.setVisible( true );			
		}
		private void openInfoView(TargetFile targetFile) {
			InfoView infoView = new InfoView( masterFrame, 
											  targetFile, 
											  swingConfig,
											  repository);
			infoView.pack();
			infoView.setVisible( true );			
		}
	}

	/**
	 * TableModelListener for the row counter.
	 */
	class ColumnCountListener implements TableModelListener {
		private SelectCounter counterLabel;
		public ColumnCountListener(SelectCounter selectCounter) {
			counterLabel = selectCounter;
		}
		public void tableChanged(TableModelEvent event) {
			if( event.getType() == TableModelEvent.INSERT ) {
				counterLabel.reset( tableModel.getSelectedRowCount(), tableModel.getRowCount() );
			} else if ( event.getType() == TableModelEvent.UPDATE ) {
				counterLabel.reset( tableModel.getSelectedRowCount(), tableModel.getRowCount() );
			}
		}
	}
	
	/**
	 * Label at the bottom of the table that counts the number
	 * of actually selected files (usually all).
	 */
	class SelectCounter extends JLabel implements Resetable {
		private int total;
		private int selected;
		public SelectCounter() {
			super( localizer.localize("message.empty-set") );
		}
		public void reset() {
			setText( localizer.localize("message.empty-set") );
		}
		public void reset(int selectCount, int totalCount) {
			total = totalCount;
			selected = selectCount;
			updateDisplay();
		}
		private void updateDisplay() {
			if( total > 0 ) {
				setText( localizer.localize("message.n-files-selected", new Integer[] {new Integer(total), new Integer(selected)}));
			} else {
				setText( localizer.localize("message.empty-set") );
			}			
		}
	}
	
	/*
	 * Edit the error counter at the right lower end of the table.
	 */
	class ErrorCounter extends JLabel implements Resetable, TableModelListener {
		public ErrorCounter() {
			super( localizer.localize("message.no-errors"), JLabel.RIGHT );
			setForeground( Color.GRAY );
		}
		public void reset() {
			setForeground( Color.GRAY );
			setText( localizer.localize("message.no-errors") );
		}
		public void eval() {
			int counter = 0;
			for(TargetFile file : tableModel.listRows()){
				if(( file.getErrorMessage() != null ) && ( file.getErrorMessage().length() > 0 )) {
					counter++;
				}
			}
			switch (counter) {
				case 0 : setText( localizer.localize("message.no-errors") ); break;
				case 1 : setText( localizer.localize("message.one-error") ); break;
				default : setText( localizer.localize("message.files-with-error", new Object[]{ new Integer(counter) }) ); break;
			}
			if( counter > 0 ) {
				setForeground( Color.RED );
			} else {
				setForeground( Color.GRAY );
			}
		}
		public void tableChanged(TableModelEvent event) {
			TableModel model = (TableModel) event.getSource();
			if(( event.getFirstRow() == 0 ) && ( event.getLastRow() == model.getRowCount() -1)) {
				if( event.getType() == TableModelEvent.DELETE ) {
					reset();
				} else { 
					eval();
				}
			}
		}
	}
	
	/*
	 * Trigger sorting of rows.
	 */
	class TableHeaderMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent eve) {
			JTableHeader tableHeader = (JTableHeader) eve.getSource();
			int columnIndex = tableHeader.columnAtPoint( eve.getPoint() );
			if( columnIndex > 0 ) {
				logger.debug("Request sorting by column " + columnIndex );
				tableModel.sortByColumn( columnIndex );
			} else {
				for(TargetFile row : tableModel.listRows() ) {
					row.setSelected( ! row.isSelected() );
				}
			}
			tableModel.notifyUpdate();
		}
	}
	
	class TableHeaderCheckBoxColumnRenderer implements TableCellRenderer {
		private JToggleButton header = new JToggleButton( new ImageIcon( this.getClass().getClassLoader().getResource("icons/invert_icon.png") ) );
		public TableHeaderCheckBoxColumnRenderer() {
			header.setToolTipText( localizer.localize("tooltip.invert") );
			header.setContentAreaFilled( false );
		}
		public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
			return header;
		}
	}
}
