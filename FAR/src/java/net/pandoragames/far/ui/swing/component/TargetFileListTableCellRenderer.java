package net.pandoragames.far.ui.swing.component;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.SwingConfig;

/**
 * Base functionality for the cell renderer of the file list table.
 * @author Olivier Wehner at 04.03.2008
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
public class TargetFileListTableCellRenderer extends DefaultTableCellRenderer
{
	/**
	 * {@inheritDoc}
	 */
	public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) 
	{
		JLabel comp = (JLabel) super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
		FileSetTableModel tableModel = ( FileSetTableModel ) table.getModel();
		decorate( comp, tableModel.getRow(row), value );
		if( isSelected && ! hasFocus ) {
			comp.setForeground( table.getSelectionForeground() );
			comp.setBackground( table.getSelectionBackground() );
		}
		return comp;
	}
	
	/**
	 * Basic renderer functionality, handy for inheriting classes.
	 * @param renderer the actual cell renderer
	 * @param row current row object
	 * @param cellValue curren cell value
	 */
	protected void decorate(JLabel renderer, TargetFile row, Object cellValue) {
		if( ! row.isSelected() ) {
			renderer.setForeground( Color.GRAY );
		} else {
			renderer.setForeground( Color.BLACK );
		}
		if(! row.isIncluded() ) {
			renderer.setBackground( SwingConfig.GRAY_EXTRA_LIGHT );
		} else {
			renderer.setBackground( Color.WHITE );			
		}
	}
}
