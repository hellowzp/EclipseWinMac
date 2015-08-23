/*
 * BinomialHeap.java       Dec 28, 2005
 *
 * Copyright (c) 2005, Rod Howell, all rights reserved.
 */
package edu.ksu.cis.heapviewer;

import edu.ksu.cis.viewer.Node;
import edu.ksu.cis.viewer.ConsList;
import edu.ksu.cis.viewer.TreeDrawing;

/**
 * An immutable binomial heap that can draw itself.
 *
 * @author Rod Howell
 *         (<a href="mailto:rhowell@ksu.edu">rhowell@ksu.edu</a>)
 *
 */
public final class BinomialHeap {

	/**
	 * The root of the tree.
	 */
	private Node root;
	
	/**
	 * The children of the root. They must be listed in
	 * strictly decreasing order of their ranks.
	 */
	private ConsList children;
	
	/**
	 * The rank of the heap. This gives the lg of the number
	 * of nodes.
	 */
	private int rank;
	
	/**
	 * A drawing of this heap.
	 */
	private TreeDrawing drawing;
	
	/**
	 * Constructs a <tt>BinomialHeap</tt> with rank 0 containing
	 * a node with the given priority.
	 * 
	 * @param p The priority of the node.
	 */
	public BinomialHeap(int p) {
		root = new Node(String.valueOf(p));
		children = new ConsList();
		rank = 0;
		drawing = new TreeDrawing(root, new TreeDrawing[0]);
	}
	
	/**
	 * Constructs a new <tt>BinomialHeap</tt> from the given
	 * heaps.  The given heaps must be of the same rank.
	 *  
	 * @param h1 The first heap.
	 * @param h2 The second heap.
	 * @throws IllegalArgumentException
	 *         if <tt>h1</tt> and <tt>h2</tt> have different
	 *         rank.
	 */
	public BinomialHeap(BinomialHeap h1, BinomialHeap h2) throws IllegalArgumentException {
		if (h1.rank != h2.rank) {
			throw new IllegalArgumentException();
		}
		BinomialHeap larger;
		BinomialHeap smaller;
		if (h1.getMax() > h2.getMax()) {
			larger = h1;
			smaller = h2;
		}
		else {
			larger = h2;
			smaller = h1;
		}
		root = larger.root;
		children = new ConsList(smaller, larger.children);
		rank = h1.rank + 1;
		// Delay construction of drawing.
	}
	
	/**
	 * Returns the maximum priority in this heap.
	 * 
	 * @return The maximum priority.
	 */
	public int getMax() {
		return Integer.parseInt(root.getContents());
	}
	
	/**
	 * Returns the children of the root.
	 * 
	 * @return The children.
	 */
	public ConsList getChildren() {
		return children;
	}
	
	/**
	 * Returns the rank of this heap.
	 * 
	 * @return The rank. 
	 */
	public int getRank() {
		return rank;
	}
	
	/**
	 * Returns a drawing of this heap.
	 * 
	 * @return The drawing.
	 */
	public TreeDrawing getDrawing() {
		if (drawing == null) {
			TreeDrawing[] d = new TreeDrawing[rank];
			ConsList lst = children;
			for (int i = rank-1; i >= 0; i--) {
				d[i] = ((BinomialHeap) lst.getHead()).getDrawing();
				lst = lst.getTail();
			}
			drawing = new TreeDrawing(root, d);
		}
		return drawing;
	}
}
