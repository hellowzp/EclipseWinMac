/*
 * BinaryHeap.java       Dec 27, 2005
 *
 * Copyright (c) 2005, Rod Howell, all rights reserved.
 */
package edu.ksu.cis.heapviewer;

import java.awt.Font;


import javax.swing.JComponent;

import edu.ksu.cis.viewer.BinaryTree;
import edu.ksu.cis.viewer.Node;

/**
 * An immutable binary heap that can draw itself.
 *
 * @author Rod Howell
 *         (<a href="mailto:rhowell@ksu.edu">rhowell@ksu.edu</a>)
 *
 */
public final class BinaryHeap implements PriorityQueue {

	/**
	 * The representation of the heap.
	 */
	private BinaryTree theTree;
	
	/**
	 * The number of nodes in the queue.
	 */
	private int size;
	
	/**
	 * Constructs an empty <tt>BinaryHeap</tt>.
	 *
	 */
	public BinaryHeap() {
		theTree = new BinaryTree();
		size = 0;
	}
	
	/**
	 * Constructs a new <tt>SkewHeap</tt> represented by <tt>t</tt>.
	 * <tt>t</tt> must be in the form of a heap.
	 * 
	 * @param t  The tree representing the heap.
	 * @param n  The number of nodes in the tree.
	 */
	private BinaryHeap(BinaryTree t, int n) {
		theTree = t;
		size = n;
	}
	
	/**
	 * Returns the result of inserting <tt>p</tt> into this
	 * priority queue.
	 * 
	 * @param p		The priority to be inserted.
	 * @return		The resulting <tt>PriorityQueue</tt>
	 */
	public PriorityQueue put(int p) {
		Node n = new Node(String.valueOf(p));
		int path = getPath(size + 1);
		return new BinaryHeap(insert(n, theTree, path), 
								size + 1);
	}

	/**
	 * Returns the <tt>PriorityQueue</tt> that results from
	 * removing a maximum priority.
	 * 
	 * @return		The resulting <tt>PriorityQueue</tt>
	 * @exception   EmptyPQException
	 *              If this <tt>PriorityQueue</tt> is empty.
	 */
	public PriorityQueue removeMax() throws EmptyPQException {
		if (size == 0) {
			throw new EmptyPQException();
		}
		if (size == 1) {
			return new BinaryHeap();
		}
		int path = getPath(size);
		BinaryTree t1 = getLast(theTree, path);
		BinaryTree t2 = t1.getLeftChild();
		return new BinaryHeap(makeHeap(t1.getRoot(), t2.getLeftChild(),
										t2.getRightChild()),
								size - 1);
	}

	/**
	 * Returns a drawing of the tree using the default font.
	 * 
	 * @return A drawing of the tree.
	 */
	public JComponent getDrawing() {
		return theTree.getDrawing();
	}

	/**
	 * Returns a drawing of the tree using the given font.
	 * 
	 * @param fnt  The font to use to draw the tree.
	 * @return     A drawing of the tree.
	 * @exception  NullPointerException
	 *             If <tt>fnt</tt> is <tt>null</tt>.
	 */
	public JComponent getDrawing(Font fnt) throws NullPointerException {
		return theTree.getDrawing(fnt);
	}

	/**
	 * Returns <tt>true</tt> iff this <tt>PriorityQueue</tt>
	 * is empty.
	 * 
	 * @return    <tt>true</tt> iff this <tt>PriorityQueue</tt>
	 *            is empty.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns a clone of this <tt>SkewHeap</tt>.
	 *
	 * @return   A clone of this <tt>SkewHeap</tt>.
	 */
	public Object clone() {
		return this;
	}
	
	/**
	 * Returns an integer describing the path to the node
	 * indexed by the given location. The locations in the
	 * heap are indexed by level from left to right, starting
	 * with index 1. The path is described by the sequence
	 * of bits in the returned value. Starting with the 
	 * low-order bit, a 0 bit indicates the left child, and a
	 * 1 bit indicates the right child.
	 * 
	 * @param loc The index of a node in the heap.
	 * @return    The path to the given node.
	 */
	private static int getPath(int loc) {
		int p = 0;
		while (loc > 1) {
			p = p << 1;
			p = p | (loc & 1);
			loc = loc >> 1;
		}
		return p;
	}
	
	/**
	 * Returns a binary tree whose root is the leaf of <tt>t</tt>
	 * described by the path in <tt>p</tt>; whose left child is
	 * <tt>t</tt> with the above node removed; and whose right
	 * child is empty.  <tt>t</tt> must be nonempty.
	 * 
	 * @param t The binary tree.
	 * @param p The path to the leaf.
	 * @return  The binary tree whose root is the leaf of <tt>t</tt>
	 *          described by the path in <tt>p</tt>; whose left child is
	 *          <tt>t</tt> with the above node removed; and whose right
	 *          child is empty.
	 */
	private static BinaryTree getLast(BinaryTree t, int p) {
		BinaryTree left = t.getLeftChild();
		BinaryTree right = t.getRightChild();
		Node root = t.getRoot();
		if (left.isEmpty()) {
			return t;
		}
		else if ((p & 1) == 0) {
			BinaryTree res = getLast(left, p >> 1);
			return new BinaryTree(res.getRoot(), 
									new BinaryTree(root, 
													res.getLeftChild(), 
													right), 
									res.getRightChild());
		}
		else {
			BinaryTree res = getLast(right, p >> 1);
			return new BinaryTree(res.getRoot(), 
									new BinaryTree(root, 
													left, 
													res.getLeftChild()), 
									res.getRightChild());
		}
	}

	/**
	 * Returns the binary heap that results from
	 * inserting the given node into the given tree.
	 * <tt>t</tt> must a binary heap with at least one node.
	 * 
	 * @param n The node to insert.
	 * @param t The tree in which to insert the node.
	 * @param p The path to the insertion location.
	 * @return  The resulting tree.
	 */
	private static BinaryTree insert(Node n, BinaryTree t, int p) {
		if (t.isEmpty()) {
			return new BinaryTree(n, new BinaryTree(), new BinaryTree());
		}
		Node smaller, larger;
		if (Viewer.compare(n.getContents(), t.getRoot().getContents()) >= 0) {
			larger = n;
			smaller = t.getRoot();
		}
		else {
			larger = t.getRoot();
			smaller = n;
		}
		if ((p & 1) == 0) { // p is even
			return new BinaryTree(larger,
									insert(smaller, 
											t.getLeftChild(),
											p >> 1),
									t.getRightChild());
		}
		else {
			return new BinaryTree(larger,
									t.getLeftChild(),
									insert(smaller,
											t.getRightChild(),
											p >> 1));
		}
	}
	
	/**
	 * Forms a heap from the node <tt>n</tt> and the two heaps
	 * <tt>left</tt> and <tt>right</tt>. The returned heap has
	 * a left child of the same shape as <tt>left</tt> and a
	 * right child of the same shape as <tt>right</tt>. If 
	 * <tt>left</tt> is empty, then <tt>right</tt> must also be
	 * empty.
	 * 
	 * @param n     The given node.
	 * @param left  The tree giving the shape of the left child.
	 * @param right The tree giving the shape of the right child.
	 * @return      The resulting heap.
	 */
	private static BinaryTree makeHeap(Node n, BinaryTree left, BinaryTree right) {
		if (left.isEmpty()) {
			return new BinaryTree(n, left, right);
		}
		if (right.isEmpty() || 
			Viewer.compare(left.getRoot().getContents(), 
							right.getRoot().getContents()) >= 0) {
			Node newRoot = left.getRoot();
			if (Viewer.compare(n.getContents(), newRoot.getContents()) >= 0) {
				return new BinaryTree(n, left, right);
			}
			else {
				return new BinaryTree(newRoot, 
										makeHeap(n, left.getLeftChild(), 
													left.getRightChild()), 
										right);
			}
		}
		else {
			Node newRoot = right.getRoot();
			if (Viewer.compare(n.getContents(), newRoot.getContents()) >= 0) {
				return new BinaryTree(n, left, right);
			}
			else {
				return new BinaryTree(newRoot,
										left,
										makeHeap(n, right.getLeftChild(),
													right.getRightChild()));
			}
		}
	}
}
