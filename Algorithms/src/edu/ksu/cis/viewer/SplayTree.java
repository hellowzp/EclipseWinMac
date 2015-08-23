/*
 * SplayTree.java     11/15/99
 *
 * Copyright (c) 1998, 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;

import java.awt.Font;
import javax.swing.JComponent;



/**
 * An immutable splay tree that can draw itself.  A splay tree is
 * self-adjusting in the sense that the deepest node accessed on any operation
 * is brought to the root.  By using double rotations whenever possible,
 * logarathmic amortized peformance is achieved.  This implementation is
 * top-down.  As a result, if a single rotation must occur, it occurs at
 * the lowest point in the path traversed.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 *
 */
public final class SplayTree implements BSTInterface {

  /**
   * The tree.
   */
  private BinaryTree theTree;

  /**
   * Constructs an empty SplayTree.
   */
  public SplayTree() {
    theTree = new BinaryTree();
  }

  /**
   * Constructs an AVLTree object from the given BinaryTree.  <tt>t</tt>
   * is assumed to be arranged into a valid binary search tree.
   */
  private SplayTree(BinaryTree t) {
    theTree = t;
  }

  /** 
   * Returns the tree resulting from the insert of the given key.  If 
   * <tt>key</tt> is already in the tree, a tree having the same contents with 
   * <tt>key</tt> at the root is returned.
   * @exception         NullPointerException
   *                    If <tt>key</tt> is <tt>null</tt>
   */
  public BSTInterface put(String key) throws NullPointerException {
    if (key == null) {
      throw new NullPointerException();
    }
    return new SplayTree(put(key, theTree));
  }

  /**
   * Returns the Splay Tree that results from the insertion of <tt>key</tt>
   * into <tt>t</tt>, which is assumed to be a valid binary search tree.  
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
        BinaryTree left = t.getLeftChild();
        if (left.isEmpty()) {
          return new BinaryTree(new Node(key), null, t);
        }
        else {
          Node leftRoot = left.getRoot();
          int result2 = key.compareTo(leftRoot.getContents());
          if (result2 == 0) {
            return zigRight(t, left);
          }
          else if (result2 < 0) {
            return zigZigRight(t, put(key, left.getLeftChild()));
          }
          else {
            return zigZagRight(t, put(key, left.getRightChild()));
          }
        }
      }
      else {
        BinaryTree right = t.getRightChild();
        if (right.isEmpty()) {
          return new BinaryTree(new Node(key), t, null);
        }
        else {
          Node rightRoot = right.getRoot();
          int result2 = key.compareTo(rightRoot.getContents());
          if (result2 == 0) {
            return zigLeft(t, right);
          }
          else if (result2 < 0) {
            return zigZagLeft(t, put(key, right.getLeftChild()));
          }
          else {
            return zigZigLeft(t, put(key, right.getRightChild()));
          }
        }
      }
    }
  }

  /**
   * Returns the tree resulting from the removal of the given key.  If the key
   * is not in the tree, a tree having the same contents is returned.
   * @exception         NullPointerException
   *                    If key is <tt>null</tt>
   */
  public BSTInterface remove(String key) throws NullPointerException {
    if (key == null) {
      throw new NullPointerException();
    }
    return new SplayTree(remove(key, theTree));
  }

  /**
   * Returns the Splay Tree resulting from the removal of <tt>s</tt> from 
   * <tt>t</tt>, which is assumed to be a valid binary search tree.  
   */
  private static BinaryTree remove(String s, BinaryTree t) {
    BinaryTree t1 = put(s, t);
    BinaryTree right = t1.getRightChild();
    BinaryTree left = t1.getLeftChild();
    if (right.isEmpty()) {
      return left;
    }
    else {
      BinaryTree t2 = findMin(right);
      return new BinaryTree(t2.getRoot(), left, t2.getRightChild());
    }
  }

  /**
   * Returns the Splay Tree resulting from finding the minimum element of
   * <tt>t</tt>, which is assumed to be a valid binary search tree.
   */
  private static BinaryTree findMin(BinaryTree t) {
    BinaryTree left = t.getLeftChild();
    if (left.isEmpty()) {
      return t;
    }
    else {
      BinaryTree leftLeft = left.getLeftChild();
      if (leftLeft.isEmpty()) {
        return zigRight(t, left);
      }
      else {
        return zigZigRight(t, findMin(leftLeft));
      }
    }
  }

  /**
   * Returns the <tt>BinaryTree</tt> resulting from performing a single
   * rotate left on the binary tree resulting from replacing the right-hand
   * child of <tt>t</tt> with <tt>right</tt>.
   */
  private static BinaryTree zigLeft(BinaryTree t, BinaryTree right) {
    BinaryTree newLeft = new BinaryTree(t.getRoot(), t.getLeftChild(),
                                        right.getLeftChild());
    return new BinaryTree(right.getRoot(), newLeft, right.getRightChild());
  }

  /**
   * Returns the <tt>BinaryTree</tt> resulting from performing a "zig-zig"
   * double rotate left on the tree resulting from replacing the right-hand
   * child of the right-hand child of <tt>t</tt> with <tt>rightRight</tt>.
   */
  private static BinaryTree zigZigLeft(BinaryTree t, BinaryTree rightRight) {
    BinaryTree right = t.getRightChild();
    BinaryTree newLeftLeft = new BinaryTree(t.getRoot(), t.getLeftChild(),
                                            right.getLeftChild());
    BinaryTree newLeft = new BinaryTree(right.getRoot(), newLeftLeft,
                                        rightRight.getLeftChild());
    return new BinaryTree(rightRight.getRoot(), newLeft, 
			  rightRight.getRightChild());
  }

  /**
   * Returns the <tt>BinaryTree</tt> resulting from performing a "zig-zag"
   * double rotate left on the tree resulting from replacing the left-hand
   * child of the right-hand child of <tt>t</tt> with <tt>rightLeft</tt>.
   */
  private static BinaryTree zigZagLeft(BinaryTree t, BinaryTree rightLeft) {
    BinaryTree right = t.getRightChild();
    BinaryTree newLeft = new BinaryTree(t.getRoot(), t.getLeftChild(),
                                        rightLeft.getLeftChild());
    BinaryTree newRight = new BinaryTree(right.getRoot(), 
					 rightLeft.getRightChild(),
                                         right.getRightChild());
    return new BinaryTree(rightLeft.getRoot(), newLeft, newRight);
  }

  /**
   * Returns the <tt>BinaryTree</tt> resulting from performing a single
   * rotate right on the binary tree resulting from replacing the left-hand
   * child of <tt>t</tt> with <tt>left</tt>.
   */
  private static BinaryTree zigRight(BinaryTree t, BinaryTree left) {
    BinaryTree newRight = new BinaryTree(t.getRoot(), left.getRightChild(),
                                         t.getRightChild());
    return new BinaryTree(left.getRoot(), left.getLeftChild(), newRight);
  }

  /**
   * Returns the <tt>BinaryTree</tt> resulting from performing a "zig-zig"
   * double rotation on the binary tree resulting from replacing the left-hand
   * child of the left-hand child of <tt>t</tt> with <tt>leftLeft</tt>.
   */
  private static BinaryTree zigZigRight(BinaryTree t, BinaryTree leftLeft) {
    BinaryTree left = t.getLeftChild();
    BinaryTree newRightRight = new BinaryTree(t.getRoot(), 
					      left.getRightChild(),
                                              t.getRightChild());
    BinaryTree newRight = new BinaryTree(left.getRoot(), 
					 leftLeft.getRightChild(),
                                         newRightRight);
    return new BinaryTree(leftLeft.getRoot(), leftLeft.getLeftChild(), 
			  newRight);
  }

  /**
   * Returns the <tt>BinaryTree</tt> resulting from performing a "zig-zag"
   * double rotation on the binary tree resulting from replacing the right-hand
   * child of the left-hand child of <tt>t</tt> with <tt>leftRight</tt>.
   */
  private static BinaryTree zigZagRight(BinaryTree t, BinaryTree leftRight) {
    BinaryTree left = t.getLeftChild();
    BinaryTree newLeft = new BinaryTree(left.getRoot(), left.getLeftChild(),
                                        leftRight.getLeftChild());
    BinaryTree newRight = new BinaryTree(t.getRoot(), 
					 leftRight.getRightChild(),
                                         t.getRightChild());
    return new BinaryTree(leftRight.getRoot(), newLeft, newRight);
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
