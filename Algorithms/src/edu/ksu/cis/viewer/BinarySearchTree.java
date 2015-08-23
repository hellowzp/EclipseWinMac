/*
 * BinarySearchTree.java     11/13/99
 *
 * Copyright (c) 1998, 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;

import java.awt.Font;
import javax.swing.JComponent;



/**
 * An immutable binary search tree that can draw itself.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 *
 */
public final class BinarySearchTree implements BSTInterface {

  /**
   * The tree.
   */
  private BinaryTree theTree;

  /**
   * Constructs an empty BinarySearchTree.
   */
  public BinarySearchTree() {
    theTree = new BinaryTree();
  }

  /**
   * Constructs a BinarySearchTree object from the given BinaryTree.  
   * <tt>t</tt> is assumed to be arranged into a valid binary search tree.
   */
  private BinarySearchTree(BinaryTree t) {
    theTree = t;
  }

  /** 
   * Returns the <tt>BinarySearchTree</tt> resulting from the insertion of 
   * <tt>key</tt> into this <tt>BinarySearchTree</tt>.  If <tt>key</tt>
   * is already in this tree, an identical tree is returned.
   * @exception  NullPointerException If <tt>key</tt> is <tt>null</tt>
   */
  public BSTInterface put(String key) throws NullPointerException {
    if (key == null) {
      throw new NullPointerException();
    }
    return new BinarySearchTree(put(key, theTree));
  }

  /**
   * Returns the binary search tree that results from the insertion of 
   * <tt>key</tt> into <tt>t</tt>, which is assumed to be a valid binary
   * search tree.  If <tt>t</tt>
   * contains <tt>key</tt>, an identical tree is returned.
   */
  private static BinaryTree put(String key, BinaryTree t) {
    if (t.isEmpty()) {
      return new BinaryTree(new Node(key), null, null);
    }
    else {
      Node root = t.getRoot();
      int result = key.compareTo(root.getContents());
      if (result == 0) {
        return t;
      }
      else if (result < 0) {
	return new BinaryTree(root, put(key, t.getLeftChild()), t.getRightChild());
      }
      else {
	return new BinaryTree(root, t.getLeftChild(), put(key, t.getRightChild()));
      }
    }
  }

  /**
   * Returns the <tt>BinarySearchTree</tt> resulting from the removal of 
   * <tt>key</tt> from this <tt>BinarySearchTree</tt>.  If <tt>key</tt>
   * is not in the tree, an identical tree is returned.
   * @exception         NullPointerException
   *                    If <tt>key</tt> is <tt>null</tt>
   */
  public BSTInterface remove(String key) throws NullPointerException {
    if (key == null) {
      throw new NullPointerException();
    }
    return new BinarySearchTree(remove(key, theTree));
  }

  /**
   * Returns the binary tree resulting from the removal of <tt>s</tt> from 
   * <tt>t</tt>, which is assumed to be a valid binary search tree.  If 
   * <tt>t</tt> does not contain <tt>s</tt>, an identical tree is returned.
   */
  private static BinaryTree remove(String s, BinaryTree t) {
    if (t.isEmpty()) {
      return t;
    }
    else {
      Node root = t.getRoot();
      int result = s.compareTo(root.getContents());
      if (result == 0) {
        if (t.getLeftChild().isEmpty()) {
          return t.getRightChild();
        }
        else if (t.getRightChild().isEmpty()) {
          return t.getLeftChild();
        }
        else {
          BinaryTree newTree = minToRoot(t.getRightChild());
          return new BinaryTree(newTree.getRoot(), t.getLeftChild(),
				newTree.getRightChild());
        }
      }
      else if (result < 0) {
        return new BinaryTree(root, remove(s, t.getLeftChild()),
			      t.getRightChild());
      }
      else {
        return new BinaryTree(root, t.getLeftChild(), remove(s, t.getRightChild()));
      }
    }
  }

  /**
   * Returns a binary search tree containing the same nodes as <tt>t</tt>, but
   * whose left child is empty.  <tt>t</tt> is assumed to be a valid binary
   * search tree.
   */
  private static BinaryTree minToRoot(BinaryTree t) {
    if (t.getLeftChild().isEmpty()) {
      return t;
    }
    else {
      BinaryTree temp = minToRoot(t.getLeftChild());
      return new BinaryTree(temp.getRoot(), null,
			    new BinaryTree(t.getRoot(), temp.getRightChild(),
					   t.getRightChild()));
    }
  }

  /**
   * Returns a drawing of this tree.
   */
  public JComponent getDrawing() {
    return theTree.getDrawing();
  }

  /**
   * Returns a drawing of this tree using the given Font.
   * @throws NullPointerException if <tt>fnt</tt> is <tt>null</tt>.
   */
  public JComponent getDrawing(Font fnt) throws NullPointerException {
    return theTree.getDrawing(fnt);
  }

  /** 
   * Because this structure is immutable, this method simply returns this tree.
   */
  public Object clone() {
    return this;
  }
}
