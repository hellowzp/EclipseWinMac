package net.pandoragames.far.ui.swing.component;

/**
 * A simple label-value pair for swing lists.
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
public class ListItem<T> {

	private String label;
	private T value;
	
	/**
	 * Constructor takes label and value.
	 * @param label displayed to the user
	 * @param value programmatic value
	 */
	public ListItem(String label, T value) {
		this.label = label;
		this.value = value;
	}

	/**
	 * Returns the label.
	 * @return label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Returns the value.
	 * @return value
	 */
	public T getValue() {
		return value;
	}
	
	/**
	 * Returns the label for use in lists and combo boxes.
	 * @return label specified with constructor
	 */
	public String toString() {
		return label;
	}

	/**
	 * Compares only the values, not the label!
	 */
	public boolean equals(Object o) {
		if( o == null ) return false;
		try {
			ListItem other = (ListItem) o;
			return value == null ? other.value == null : value.equals(other.value);
		} catch(ClassCastException ccx) {
			return false;
		}
	}
	
	public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}
}
