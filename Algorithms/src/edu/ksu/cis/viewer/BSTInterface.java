/*
 * BSTInterface.java     11/15/99
 *
 * Copyright (c) 1998, 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;

import java.awt.Font;
import javax.swing.JComponent;




/** 
 * This interface is implemented by each of the data structures whose 
 * functionality is equivalent to a binary search tree.  Functionality 
 * includes the ability to
 * insert and delete String keys, to clone itself, and to draw itself.
 * <p>
 * Note that this interface does not extend {@link java.lang.Cloneable 
 * java.lang.Cloneable}.
 * This is because implementations may not need to use the 
 * <tt>clone</tt>
 * method of {@link java.lang.Object Object} in order to clone themselves.
 *
 * @author      Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 *
 * @see         AVLTree
 * @see         BinarySearchTree
 * @see         SplayTree
 * @see         java.lang.Cloneable
 * @see         java.lang.Object
 */
public interface BSTInterface {

  /** 
   * Returns the result of inserting <tt>key</tt> into this tree.  If
   * <tt>key</tt> is already in the tree, a tree having the same contents 
   * is returned.
   * @exception         NullPointerException
   *                    If <tt>key</tt> is <tt>null</tt>
   */
  public BSTInterface put(String key) throws NullPointerException;

  /**
   * Returns the result of removing <tt>key</tt> from this.  If <tt>key</tt>
   * is not in the tree, a tree having the same contents is returned.
   * @exception         NullPointerException
   *                    If key is <tt>null</tt>
   */
  public BSTInterface remove(String key) throws NullPointerException;

  /**
   * Returns a drawing of the tree.
   */
  public JComponent getDrawing();

  /**
   * Returns a drawing of the tree using the given Font.
   * @throws NullPointerException if <tt>fnt</tt> is <tt>null</tt>.
   */
  public JComponent getDrawing(Font fnt);

  /**
   * Returns a clone of this tree.
   */
  public Object clone();
}
