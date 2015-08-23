/*
 * LeftistHeap.java       Dec 26, 2005
 *
 * Copyright (c) 2005, Rod Howell, all rights reserved.
 */
package edu.ksu.cis.heapviewer;

import java.awt.Font;

import javax.swing.JComponent;

import edu.ksu.cis.viewer.BinaryTree;
import edu.ksu.cis.viewer.Node;



/**
 * An immutable leftist heap that can draw itself.
 *
 * @author Rod Howell
 *         (<a href="mailto:rhowell@ksu.edu">rhowell@ksu.edu</a>)
 *
 */
public final class LeftistHeap implements PriorityQueue {
	
	/** The representation of the heap. 
	 */
	private BinaryTree theTree;
	
	/**
	 * Constructs an empty LeftistHeap.
	 *
	 */
	public LeftistHeap() {
		theTree = new BinaryTree();
	}
	
	/** Constructs a LeftistHeap represented by the given tree.
	 * 
	 * @param t  The representation of the LeftistHeap.
	 */
	private LeftistHeap(BinaryTree t) {
		theTree = t;
	}

	/**
	 * Returns the result of inserting <tt>p</tt> into this
	 * priority queue.
	 * 
	 * @param p		The priority to be inserted.
	 * @return		The resulting <tt>PriorityQueue</tt>
	 */
	public PriorityQueue put(int p) {
		BinaryTree t = leftistTree(p);
		return new LeftistHeap(merge(theTree, t));
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
		if (isEmpty()) {
			throw new EmptyPQException();
		}
		else {
			return new LeftistHeap(merge(theTree.getLeftChild(), 
											theTree.getRightChild()));
		}
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
		return theTree.isEmpty();
	}
	
	/**
	 * Returns a clone of this <tt>LeftistHeap</tt>.
	 *
	 * @return   A clone of this <tt>LeftistHeap</tt>.
	 */
	public Object clone() {
		return this;
	}

	/**
	 * Merges the two trees into a single leftist heap.  The 
	 * two trees must be leftist heaps.
	 * 
	 * @param t1  The first tree.
	 * @param t2  The second tree.
	 * @return    A <tt>BinaryTree</tt> in the form of a leftist
	 *            heap containing all the nodes in <tt>t1</tt>
	 *            and <tt>t2</tt>.
	 */
	private static BinaryTree merge(BinaryTree t1, BinaryTree t2) {
		if (t1.isEmpty()) {
			return t2;
		}
		else if (t2.isEmpty()) {
			return t1;
		}
		else {
			String s1 = t1.getRoot().getContents();
			String s2 = t2.getRoot().getContents();
			BinaryTree large, small;
			if (Viewer.compare(s1, s2) > 0) {
				large = t1;
				small = t2;
			}
			else {
				large = t2;
				small = t1;
			}
			BinaryTree t3 = merge(large.getRightChild(), small);
			return leftistTree(large.getRoot(), large.getLeftChild(),
								t3);
		}
	}
	
	/**
	 * Forms a leftist tree from the given node and trees.
	 * <t1> and <t2> must be leftist trees.
	 * @param r   The root of the leftist tree.
	 * @param t1  One of the children.
	 * @param t2  The other child.
	 * @return    The resulting leftist tree.
	 */
	private static BinaryTree leftistTree(Node r, BinaryTree t1, BinaryTree t2) {
		int len1 = nullPathLength(t1);
		int len2 = nullPathLength(t2);
		r = new Node(r.getContents(), Math.min(len1, len2)+1);
		if (len1 >= len2) {
			return new BinaryTree(r, t1, t2);
		}
		else {
			return new BinaryTree(r, t2, t1);
		}
	}
	
	/**
	 * Forms a leftist tree with a single node containing <tt>i</tt>
	 * encoded as a String.
	 * 
	 * @param i  The content of the single node.
	 * @return   The resulting leftist tree.
	 */
	private static BinaryTree leftistTree(int i) {
		String s = String.valueOf(i);
		Node n = new Node(s, 1);
		return new BinaryTree(n, new BinaryTree(),
								new BinaryTree());
	}
	
	/**
	 * Returns the null path length of the given leftist tree.
	 * 
	 * @param t  The given tree.
	 * @return   The null path length.
	 */
	private static int nullPathLength(BinaryTree t) {
		if (t.isEmpty()) {
			return 0;
		}
		else {
			return t.getRoot().getTag();
		}
	}
}
