package net.pandoragames.far.ui.model;
/**
 * Represents an operation like "find" or "replace".
 * Each page (or tab) in FAR is dedicated to a particular <i>operation</i>,
 * e.g. "find" or "replace". This enum serves to identify those operations
 * in varying contexts.
 * @see OperationForm
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
public enum OperationType  {
	/** Represents "no type" */
	NONE,
	/** Type for find forms and operations */
	FIND,
	/** Type for filter forms and operations */
	FILTER,
	/** Type for replace forms and operations */
	REPLACE,
	/** Type for rename forms and operations */
	RENAME,
	/** Represents any type of operation */
	ANY
}