package net.pandoragames.far.ui.model;

/**
 * Line separator characters.
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
public enum LineSeparator {

	/** Undefined line separator. */
	UNDEFINED(),
	/** Unix style line separator. */
	LF('\n'),
	/** DOS style line separator. */
	CRLF('\r', '\n'),
	/** Apple style line separator. */
	CR('\r'),
	/** Exotic line separator. */
	LFCR('\n', '\r');
	
	private char[] sequence;
	
	LineSeparator(char ... sequence) {
		this.sequence = sequence;
	}

	/**
	 * Returns the actual character sequence used for this line separator.
	 * @return character sequence
	 */
	public char[] getSequence() {
		return sequence;
	}
}
