package net.pandoragames.charset;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * CharsetProvider for CP437Extra.
 *
 * @author Olivier Wehner
 * <!--
 *  FAR - Find And Replace
 *  Copyright (C) 2009,  Olivier Wehner

 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  -->
 */
public class FARSetProvider extends CharsetProvider {

	@Override
	public Charset charsetForName(String charsetName) {
		if( charsetName == null ) return null;
		if( CP437Extra.names[0].equals(charsetName) || CP437Extra.names[1].equals(charsetName) ) {
			return new CP437Extra();
//		} else if( BinaryCharset.names[0].equals(charsetName) || BinaryCharset.names[1].equals(charsetName) ) {
//			return new BinaryCharset();
		}
		return null;
	}

	@Override
	public Iterator<Charset> charsets() {
		List<Charset> clist = new ArrayList<Charset>();
		clist.add(new CP437Extra());
//		clist.add(new BinaryCharset());
		return clist.iterator();		
	}

}
