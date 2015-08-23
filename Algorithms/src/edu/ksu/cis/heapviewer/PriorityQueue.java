/* PriorityQueue.java    12/26/05 
 * 
 * Copyright (c) 2005, Rod Howell, all rights reserved.
 * 
 */

package edu.ksu.cis.heapviewer;

import javax.swing.JComponent;




import java.awt.Font;

/**
 * This interface is implemented by each of the data structures
 * whose functionality is equivalent to a priority queue.
 * Functionality includes the ability to insert
 * <tt>int</tt> priorities, to remove a node with maximum priority,
 * to clone itself, and to draw itself. Note that this interface
 * does not extend {@link java.lang.Cloneable java.lang.Cloneable}.
 * This is because implementations may not need to use the 
 * <tt>clone</tt> method of {@link java.lang.Object Object) in
 * order to clone themselves.
 * 
 *
 * @author howell
 *         (<a href="mailto:rhowell@ksu.edu">rhowell@ksu.edu</a>)
 *
 * @see java.lang.Cloneable
 * @see java.lang.Object
 */
public interface PriorityQueue {

	/**
	 * Returns the result of inserting <tt>p</tt> into this
	 * priority queue.
	 * 
	 * @param p		The priority to be inserted.
	 * @return		The resulting <tt>PriorityQueue</tt>
	 */
	public PriorityQueue put(int p);
	
	/**
	 * Returns the <tt>PriorityQueue</tt> that results from
	 * removing a maximum priority.
	 * 
	 * @return		The resulting <tt>PriorityQueue</tt>
	 * @exception   EmptyPQException
	 *              If this <tt>PriorityQueue</tt> is empty.
	 */
	public PriorityQueue removeMax() throws EmptyPQException;

	/**
	 * Returns a drawing of the tree using the default font.
	 * 
	 * @return A drawing of the tree.
	 */
	public JComponent getDrawing();
	
	/**
	 * Returns a drawing of the tree using the given font.
	 * 
	 * @param fnt  The font to use to draw the tree.
	 * @return     A drawing of the tree.
	 * @exception  NullPointerException
	 *             If <tt>fnt</tt> is <tt>null</tt>.
	 */
	public JComponent getDrawing(Font fnt) throws NullPointerException;
	
	/**
	 * Returns <tt>true</tt> iff this <tt>PriorityQueue</tt>
	 * is empty.
	 * 
	 * @return    <tt>true</tt> iff this <tt>PriorityQueue</tt>
	 *            is empty.
	 */
	public boolean isEmpty();
	
	/**
	 * Returns a clone of the <tt>PriorityQueue</tt>.
	 *
	 * @return  A clone.
	 */
	public Object clone();
}