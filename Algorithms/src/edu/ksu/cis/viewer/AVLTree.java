/*
 * AVLTree.java     11/13/99
 *
 * Copyright (c) 1998, 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;

import java.awt.Font;
import javax.swing.JComponent;



/**
 * An immutable AVL tree that can draw itself.  An AVL tree is a binary
 * search tree such that if it is nonempty, its children differ in height
 * by at most 1.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 */
public final class AVLTree implements BSTInterface {

  /**
   * The tree.  The <tt>tag</tt> fields of the nodes will be used to
   * store the heights of the subtrees.
   * @see Node
   */
  private BinaryTree theTree;

  /**
   * Constructs an empty AVLTree.
   */
  public AVLTree() {
    theTree = new BinaryTree();
  }

  /**
   * Constructs an AVLTree object from the given BinaryTree.  <tt>t</tt>
   * is assumed to be arranged into a valid AVL tree with the height of
   * each subtree stored in the Node at its root.
   */
  private AVLTree(BinaryTree t) {
    theTree = t;
  }

  /** 
   * Returns the <tt>AVLTree</tt> resulting from the insertion 
   * <tt>key</tt> into this <tt>AVLTree</tt>.  If <tt>key</tt>
   * is already in this tree, an identical tree is returned.
   * @exception         NullPointerException
   *                    If <tt>key</tt> is <tt>null</tt>
   */
  public BSTInterface put(String key) throws NullPointerException {
    if (key == null) {
      throw new NullPointerException();
    }
    return new AVLTree(put(key, theTree));
  }

  /**
   * Returns the AVL Tree that results from the insertion of <tt>key</tt>
   * into <tt>t</tt>, which is assumed to be a valid AVL Tree.  If <tt>t</tt>
   * contains <tt>key</tt>, an identical tree is returned.
   */
  private static BinaryTree put(String key, BinaryTree t) {
    if (t.isEmpty()) {
      return new BinaryTree(new Node(key, 0), null, null);
    }
    else {
      Node root = t.getRoot();
      int result = key.compareTo(root.getContents());
      if (result == 0) {
        return t;
      }
      else if (result < 0) {
	return balance(root, put(key, t.getLeftChild()), t.getRightChild());
      }
      else {
	return balance(root, t.getLeftChild(), put(key, t.getRightChild()));
      }
    }
  }

  /**
   * Returns the <tt>AVLTree</tt> resulting from the removal of <tt>key</tt> 
   * from this <tt>AVLTree</tt>.  If <tt>key</tt>
   * is not in this tree, an identical tree is returned.
   * @exception         NullPointerException
   *                    If key is <tt>null</tt>
   */
  public BSTInterface remove(String key) throws NullPointerException {
    if (key == null) {
      throw new NullPointerException();
    }
    return new AVLTree(remove(key, theTree));
  }

  /**
   * Returns the AVL Tree resulting from the removal of <tt>s</tt> from 
   * <tt>t</tt>, which is assumed to be a valid AVL Tree.  If <tt>t</tt>
   * does not contain <tt>s</tt>, an identical tree is returned.
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
	  return balance(newTree.getRoot(), t.getLeftChild(),
			 newTree.getRightChild());
	}
      }
      else if (result < 0) {
	return balance(root, remove(s, t.getLeftChild()),
		       t.getRightChild());
      }
      else {
	return balance(root, t.getLeftChild(), 
		       remove(s, t.getRightChild()));
      }
    }
  }

  /**
   * Returns a binary search tree containing the same nodes as <tt>t</tt>, but
   * whose left child is empty and whose right 
   * child is an AVL tree.  The <tt>tag</tt> field of the root of the returned
   * tree is meaningless.  <tt>t</tt> is assumed to be a valid AVL Tree.
   */
  private static BinaryTree minToRoot(BinaryTree t) {
    if (t.getLeftChild().isEmpty()) {
      return t;
    }
    else {
      BinaryTree temp = minToRoot(t.getLeftChild());
      return new BinaryTree(temp.getRoot(), null,
			    balance(t.getRoot(), temp.getRightChild(),
				    t.getRightChild()));
    }
  }

  /**
   * Returns an AVL Tree containing the nodes of all three arguments.
   * <p>
   * <b>Assumptions:</b>
   * <ul>
   * <li> The contents of <tt>root</tt> are greater than the contents of
   *      any node in <tt>left</tt> and less than the contents of any node
   *      in <tt>right</tt>.
   * <li> <tt>left</tt> and <tt>right</tt> are valid AVL Trees.
   * <li> The heights of <tt>left</tt> and <tt>right</tt> differ by at most 2.
   * </ul>
   */
  private static BinaryTree balance(Node root, BinaryTree left,
				    BinaryTree right) {
    int leftHeight = height(left);
    int rightHeight = height(right);
    if (leftHeight == rightHeight + 2) {
      if (leftHeight == height(left.getLeftChild()) + 1) {
	return rotateRight(root, left, right);
      }
      else {
	return doubleRotateRight(root, left, right);
      }
    }
    else if (rightHeight == leftHeight + 2) {
      if (rightHeight == height(right.getRightChild()) + 1) {
	return rotateLeft(root, left, right);
      }
      else {
	return doubleRotateLeft(root, left, right);
      }
    }
    else {
      return new BinaryTree(new Node(root.getContents(),
                                     Math.max(leftHeight, rightHeight) + 1),
			    left, right);
    }
  }

  /**
   * Returns the <tt>BinaryTree</tt> resulting from performing a single 
   * rotate left on the binary tree whose root is given by <tt>root</tt>,
   * whose left-hand child is given by <tt>left</tt>, and whose right-hand
   * child is given by <tt>right</tt>.  The <tt>tag</tt> fields of all
   * nodes are guaranteed to record the height of their subtrees provided
   * this was initially the case in both <tt>left</tt> and <tt>right</tt>.
   */
  private static BinaryTree rotateLeft(Node root, BinaryTree left, 
				       BinaryTree right) {
    int leftHeight = Math.max(height(left), height(right.getLeftChild())) + 1;
    BinaryTree newLeft = new BinaryTree(new Node(root.getContents(), 
						 leftHeight),
					left, right.getLeftChild());
    int newHeight = Math.max(leftHeight, height(right.getRightChild())) + 1;
    return new BinaryTree(new Node(right.getRoot().getContents(), newHeight),
			  newLeft, right.getRightChild());
  }

  /**
   * Returns the <tt>BinaryTree</tt> resulting from performing a single 
   * rotate right on the binary tree whose root is given by <tt>root</tt>,
   * whose left-hand child is given by <tt>left</tt>, and whose right-hand
   * child is given by <tt>right</tt>.  The <tt>tag</tt> fields of all
   * nodes are guaranteed to record the height of their subtrees provided
   * this was initially the case in both <tt>left</tt> and <tt>right</tt>.
   */
  private static BinaryTree rotateRight(Node root, BinaryTree left,
					BinaryTree right) {
    int rightHeight = Math.max(height(right), 
			       height(left.getRightChild())) + 1;
    BinaryTree newRight = new BinaryTree(new Node(root.getContents(), 
						  rightHeight),
					 left.getRightChild(), right);
    int newHeight = Math.max(height(left.getLeftChild()), rightHeight) + 1;
    return new BinaryTree(new Node(left.getRoot().getContents(), newHeight),
			  left.getLeftChild(), newRight);
  }

  /**
   * Returns the <tt>BinaryTree</tt> resulting from performing a double 
   * rotate left on the binary tree whose root is given by <tt>root</tt>,
   * whose left-hand child is given by <tt>left</tt>, and whose right-hand
   * child is given by <tt>right</tt>.  The <tt>tag</tt> fields of all
   * nodes are guaranteed to record the height of their subtrees provided
   * this was initially the case in both <tt>left</tt> and <tt>right</tt>.
   */
  private static BinaryTree doubleRotateLeft(Node root, BinaryTree left,
					     BinaryTree right) {
    int leftHeight = Math.max(height(left), 
			      height(right.getLeftChild().getLeftChild())) + 1;
    BinaryTree newLeft = new BinaryTree(new Node(root.getContents(), 
						 leftHeight), left,
					right.getLeftChild().getLeftChild());
    int rightHeight = Math.max(height(right.getLeftChild().getRightChild()),
			       height(right.getRightChild())) + 1;
    BinaryTree newRight = 
      new BinaryTree(new Node(right.getRoot().getContents(), rightHeight),
		     right.getLeftChild().getRightChild(),
		     right.getRightChild());
    int newHeight = Math.max(leftHeight, rightHeight) + 1;
    return 
      new BinaryTree(new Node(right.getLeftChild().getRoot().getContents(),
			      newHeight),
		     newLeft, newRight);
  }

  /**
   * Returns the <tt>BinaryTree</tt> resulting from performing a double 
   * rotate right on the binary tree whose root is given by <tt>root</tt>,
   * whose left-hand child is given by <tt>left</tt>, and whose right-hand
   * child is given by <tt>right</tt>.  The <tt>tag</tt> fields of all
   * nodes are guaranteed to record the height of their subtrees provided
   * this was initially the case in both <tt>left</tt> and <tt>right</tt>.
   */
  private static BinaryTree doubleRotateRight(Node root, BinaryTree left,
					      BinaryTree right) {
    int leftHeight = Math.max(height(left.getLeftChild()),
			      height(left.getRightChild().getLeftChild())) + 1;
    BinaryTree newLeft = new BinaryTree(new Node(left.getRoot().getContents(),
                                                 leftHeight),
				        left.getLeftChild(),
				        left.getRightChild().getLeftChild());
    int rightHeight = Math.max(height(left.getRightChild().getRightChild()),
			       height(right)) + 1;
    BinaryTree newRight = new BinaryTree(new Node(root.getContents(), 
						  rightHeight),
					 left.getRightChild().getRightChild(),
					 right);
    int newHeight = Math.max(leftHeight, rightHeight) + 1;
    return 
      new BinaryTree(new Node(left.getRightChild().getRoot().getContents(),
			      newHeight),
		     newLeft, newRight);
  }

  /**
   * Returns the height <tt>t</tt>.  <tt>t</tt> is assumed to have its height
   * recorded in the <tt>tag</tt> field of its root if it is non-empty.
   */
  private static int height(BinaryTree t) {
    if (t.isEmpty()) {
      return -1;
    }
    else {
      return (t.getRoot()).getTag();
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
  public JComponent getDrawing(Font fnt) {
    return theTree.getDrawing(fnt);
  }

  /** 
   * Because this structure is immutable, this method simply returns this tree.
   */
  public Object clone() {
    return this;
  }
}

