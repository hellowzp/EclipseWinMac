/*
 * TreeInterface.java    11/15/99
 *
 * Copyright (c) 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;



/**
 * This interface may be implemented by a tree in order to allow a
 * {@link TreeDrawing TreeDrawing} to be constructed from the tree.  
 * Empty trees may be
 * represented either by <tt>null</tt> references or by objects whose
 * {@link #isEmpty isEmpty} methods return <tt>true</tt>.  If <tt>null</tt> 
 * references
 * are used, {@link #isEmpty isEmpty} should always return <tt>false</tt>.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 *
 * @see        TreeDrawing
 */
public interface TreeInterface {

  /**
   * Returns the root of the tree.  The <tt>TreeDrawing</tt> class
   * converts the returned <tt>Object</tt> to a <tt>String</tt>.  In
   * order to control how this conversion is done, its <tt>toString</tt>
   * method may be overridden.
   * @throws EmptyTreeException if the tree is empty.
   * @see java.lang.Object Object.toString()
   */
  public Object getRoot() throws EmptyTreeException;

  /**
   * Returns the children of the tree.  A <tt>null</tt> returned value
   * is treated as equivalent to an array of length 0.
   * @throws EmptyTreeException if the tree is empty.
   */
  public TreeInterface[] getChildren() throws EmptyTreeException;

  /**
   * Returns <tt>true</tt> if the tree is empty.
   */
  public boolean isEmpty();
}
