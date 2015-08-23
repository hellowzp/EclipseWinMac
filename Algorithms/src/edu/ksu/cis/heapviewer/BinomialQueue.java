/*
 * BinomialQueue.java       Dec 28, 2005
 *
 * Copyright (c) 2005, Rod Howell, all rights reserved.
 */
package edu.ksu.cis.heapviewer;

import java.awt.Font;

import javax.swing.JComponent;

import edu.ksu.cis.viewer.ConsList;
import edu.ksu.cis.viewer.TreeDrawing;
import edu.ksu.cis.viewer.Node;

/**
 * An immutable binomial queue that can draw itself.
 *
 * @author Rod Howell
 *         (<a href="mailto:rhowell@ksu.edu">rhowell@ksu.edu</a>)
 *
 */
public final class BinomialQueue implements PriorityQueue {

	/**
	 * The sequence of <tt>BinomialHeap</tt>s comprising this
	 * <tt>BinomialQueue</tt>
	 */
	private ConsList theHeaps;
	
	/**
	 * The drawing of this binomial queue.
	 */
	private TreeDrawing drawing;
	
	/**
	 * Constructs an empty <tt>BinomialQueue</tt>.
	 *
	 */
	public BinomialQueue() {
		theHeaps = new ConsList();
		TreeDrawing[] c = new TreeDrawing[0];
		drawing = new TreeDrawing(new Node(""), c);
	}
	
	/**
	 * Constructs a <tt>BinomialQueue</tt> consisting of the
	 * given <tt>BinomialHeap</tt>s.  The heaps must be
	 * given in order of strictly decreasing rank.
	 * 
	 * @param heaps  The sequence of <tt>BinomialHeap</tt>s
	 *               comprising this <tt>BinomialQueue</tt>.
	 */
	private BinomialQueue(ConsList heaps) {
		theHeaps = heaps;
		drawing = new TreeDrawing(new Node(""), assembleDrawings(heaps, 0));
	}
	
	/**
	 * Returns the result of inserting <tt>p</tt> into this
	 * priority queue.
	 * 
	 * @param p		The priority to be inserted.
	 * @return		The resulting <tt>PriorityQueue</tt>
	 */
	public PriorityQueue put(int p) {
		BinomialHeap h = new BinomialHeap(p);
		ConsList c = merge(theHeaps, new ConsList(h, new ConsList()));
		return new BinomialQueue(c);
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
		if (theHeaps.isEmpty()) {
			throw new EmptyPQException();
		}
		BinomialHeap max = (BinomialHeap) theHeaps.getHead();
		ConsList rem = theHeaps.getTail();
		while (!rem.isEmpty()) {
			BinomialHeap next = (BinomialHeap) rem.getHead();
			if (max.getMax() < next.getMax()) {
				max = next;
			}
			rem = rem.getTail();
		}
		ConsList c1 = max.getChildren();
		ConsList c2 = remove(max, theHeaps);
		return new BinomialQueue(merge(c1, c2));
	}

	/**
	 * Returns a drawing of the tree using the default font.
	 * 
	 * @return A drawing of the tree.
	 */
	public JComponent getDrawing() {
		return drawing.getDrawing();
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
		return drawing.getDrawing(fnt);
	}
	
	/**
	 * Returns <tt>true</tt> iff this <tt>PriorityQueue</tt>
	 * is empty.
	 * 
	 * @return    <tt>true</tt> iff this <tt>PriorityQueue</tt>
	 *            is empty.
	 */
	public boolean isEmpty() {
		return theHeaps.isEmpty();
	}
	
	/**
	 * Returns a clone of the <tt>PriorityQueue</tt>.
	 *
	 * @return  A clone.
	 */
	public Object clone() {
		return this;
	}

	/**
	 * Returns an array of drawings of the <tt>BinomialHeap</tt>s
	 * in the given <tt>ConsList</tt>.
	 * 
	 * @param heaps The heaps to draw.
	 * @param slots The number of locations in the array
	 *              to leave empty.
	 * @return      The drawings of the heaps.
	 */
	private static TreeDrawing[] assembleDrawings(ConsList heaps, int slots) {
		if (heaps.isEmpty()) {
			return new TreeDrawing[slots];
		}
		else {
			TreeDrawing[] a = assembleDrawings(heaps.getTail(), slots+1);
			a[a.length-slots-1] = ((BinomialHeap) heaps.getHead()).getDrawing();
			return a;
		}
	}
	
	/**
	 * Merges the <tt>BinomialHeap</tt>s into a single 
	 * <tt>ConsList</tt> in order of strictly decreasing rank.
	 * <tt>a</tt> and <tt>b</tt> must contain <tt>BinomialHeap</tt>s
	 * order of strictly decreasing rank.
	 * 
	 * @param a The first list of <tt>BinomialHeap</tt>s.
	 * @param b The second list of <tt>BinomialHeap</tt>s.
	 * @return  The resulting sequence.
	 */
	private static ConsList merge(ConsList a, ConsList b) {
		if (a.isEmpty()) {
			return b;
		}
		else if (b.isEmpty()) {
			return a;
		}
		else {
			BinomialHeap aHead = (BinomialHeap) a.getHead();
			BinomialHeap bHead = (BinomialHeap) b.getHead();
			BinomialHeap largest;
			ConsList res;
			if (aHead.getRank() > bHead.getRank()) {
				largest = aHead;
				res = merge(a.getTail(), b);
			}
			else {
				largest = bHead;
				res = merge(a, b.getTail());
			}
			BinomialHeap rHead = (BinomialHeap) res.getHead();
			if (largest.getRank() > rHead.getRank()) {
				return new ConsList(largest, res);
			}
			else if (largest.getRank() == rHead.getRank()) {
				return new ConsList(new BinomialHeap(largest, rHead), res.getTail());
			}
			else {
				return new ConsList(rHead, new ConsList(largest, res.getTail()));
			}
		}
	}
	
	/**
	 * Returns the <tt>ConsList</tt> obtained by removing the
	 * first occurrence of <tt>obj</tt> from </tt>lst</tt>.
	 * Objects are compared for identity.  <tt>lst</tt> must
	 * contain at least one occurrence of <tt>obj</tt>.
	 * 
	 * @param obj The object to be removed.
	 * @param lst The <tt>ConsList</tt> from which <tt>obj</tt>
	 *            is to be removed.
	 * @return    The resulting <tt>ConsList</tt>.
	 */
	private static ConsList remove(Object obj, ConsList lst) {
		if (lst.getHead() == obj) {
			return lst.getTail();
		}
		else {
			return new ConsList(lst.getHead(), remove(obj, lst.getTail()));
		}
	}
}
