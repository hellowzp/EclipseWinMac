/*
 * BinaryTree.java     11/13/99
 *
 * Copyright (c) 1998, 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;

import java.awt.Font;



/**
 * An immutable binary tree that can draw itself.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 */
public final class BinaryTree implements Cloneable {

  /**
   * Is <tt>true</tt> if this tree is empty.
   */
  private boolean empty;

  /**
   * The root of this tree if it is not empty.
   */
  private Node root;

  /**
   * The left-hand child of this tree if this tree is not empty.
   */
  private BinaryTree left;

  /**
   * The right-hand child of this tree if this tree is not empty.
   */ 
  private BinaryTree right;

  /**
   * An abstract drawing of this tree.
   */
  private TreeDrawing drawing;

  /**
   * Constructs an empty BinaryTree.
   */
  public BinaryTree() {
    empty = true;
    drawing = new TreeDrawing();
  }

  /**
   * Constructs a BinaryTree with the given root and children.  If either
   * BinaryTree parameter is <tt>null</tt>, an empty BinaryTree will be used
   * for that child.
   * @param    root     the root of the tree
   * @param    left     the left child
   * @param    right    the right child
   * @throws NullPointerException if <tt>root</tt> is <tt>null</tt>
   */
  public BinaryTree(Node root, BinaryTree left, BinaryTree right) 
    throws NullPointerException {
    empty = false;
    this.root = root;
    this.left = left == null ? new BinaryTree() : left;
    this.right = right == null ? new BinaryTree() : right;
    drawing = new TreeDrawing(root,
			      new TreeDrawing[] {this.left.drawing,
						 this.right.drawing});
  }

  /**
   * Returns <tt>true</tt> if this tree is empty.
   */
  public boolean isEmpty() {
    return empty;
  }

  /**
   * Returns the root of this tree.
   * @exception         EmptyTreeException
   *                    If this tree is empty.
   */
  public Node getRoot() throws EmptyTreeException {
    if (empty) {
      throw new EmptyTreeException();
    }
    else {
      return root;
    }
  }

  /**
   * Returns the left-hand child of this tree.
   * @exception          EmptyTreeException
   *                     If this tree is empty.
   */
  public BinaryTree getLeftChild() throws EmptyTreeException {
    if (empty) {
      throw new EmptyTreeException();
    }
    else {
      return left;
    }
  }

  /**
   * Returns the right-hand child of this tree.
   * @return             The right child
   *                     If this tree is empty.
   */
  public BinaryTree getRightChild() throws EmptyTreeException {
    if (empty) {
      throw new EmptyTreeException();
    }
    else {
      return right;
    }
  }

  /**
   * Returns a drawing of this tree.
   */
  public TreeComponent getDrawing() {
    return drawing.getDrawing();
  }

  /**
   * Returns a drawing of the tree using the given Font.
   * @throws NullPointerException if <tt>fnt</tt> is <tt>null</tt>
   */
  public TreeComponent getDrawing(Font fnt) throws NullPointerException {
    return drawing.getDrawing(fnt);
  }

  /** 
   * Because this structure is immutable, this method simply returns this tree
   * itself.
   */
  public Object clone() {
    return this;
  }
}
