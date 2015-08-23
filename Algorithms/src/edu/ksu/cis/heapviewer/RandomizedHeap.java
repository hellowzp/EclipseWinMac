/*
 * RandomizedHeap.java       Dec 26, 2005
 *
 * Copyright (c) 2005, Rod Howell, all rights reserved.
 */
package edu.ksu.cis.heapviewer;


import java.awt.Font;
import java.util.Random;

import javax.swing.JComponent;

import edu.ksu.cis.viewer.BinaryTree;
import edu.ksu.cis.viewer.Node;


/**
 * An immutable Randomized heap that can draw itself.
 *
 * @author Rod Howell
 *         (<a href="mailto:rhowell@ksu.edu">rhowell@ksu.edu</a>)
 *
 */
public final class RandomizedHeap implements PriorityQueue {

	/** The representation of the heap. 
	 */
	private BinaryTree theTree;

	/**
	 * The random number generator.
	 */
	private static Random rand = new Random();
	
	/**
	 * Constructs an empty <tt>RandomizedHeap</tt>.
	 *
	 */
	public RandomizedHeap() {
		theTree = new BinaryTree();
	}
	
	/**
	 * Constructs a new <tt>RandomizedHeap</tt> represented by <tt>t</tt>.
	 * <tt>t</tt> must be in the form of a heap.
	 * 
	 * @param t  The tree representing the heap.
	 */
	private RandomizedHeap(BinaryTree t) {
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
		BinaryTree t = new BinaryTree(new Node(String.valueOf(p)),
										new BinaryTree(),
										new BinaryTree());
		return new RandomizedHeap(merge(theTree, t));
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
			return new RandomizedHeap(merge(theTree.getLeftChild(), 
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
	 * Merges the two given trees into a single <tt>BinaryTree</tt>
	 * that forms a heap.
	 * 
	 * @param t1  The first tree.
	 * @param t2  The second tree.
	 * @return    The merged trees.
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
			if (rand.nextBoolean()) {
				BinaryTree t3 = merge(large.getLeftChild(), small);
				return new BinaryTree(large.getRoot(), t3, large.getRightChild());
			}
			else {
				BinaryTree t3 = merge(large.getRightChild(), small);
				return new BinaryTree(large.getRoot(), large.getLeftChild(), t3);
			}
		}
	}

	/**
	 * Returns a clone of this <tt>RandomizedHeap</tt>.
	 *
	 * @return   A clone of this <tt>RandomizedHeap</tt>.
	 */
	public Object clone() {
		return this;
	}
}
