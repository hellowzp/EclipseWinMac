/*
 * TreeDrawing.java     11/15/99
 *
 * Copyright (c) 1998, 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;




/**
 * An immutable high-level representation of a drawing of a tree. This
 * representation contains a font-independent description of the size of
 * the drawing along with all the information necessary to draw the tree
 * on a given graphics context.  The {@link #paint paint} method is used to 
 * draw the tree.
 * <p>
 * There are two general ways of constructing a <tt>TreeDrawing</tt>.  The
 * first is to recursively build it from {@link Node Node}s.  Drawings of 
 * {@link BinaryTree BinaryTree}s are build in this way.  The second way is
 * to build it from an implementation of {@link TreeInterface TreeInterface} 
 * and optionally
 * an implementation of {@link Colorizer Colorizer}.  When this way is used, no
 * attempt is made to detect cycles or overlapping subtrees; instead,
 * a parameter to the constructor specifies the maximum height of the tree
 * drawn.
 * <p> 
 * A {@link TreeComponent TreeComponent} containing a graphical representation
 * can be 
 * efficiently obtained from the {@link #getDrawing() getDrawing()}</tt> 
 * method.  This method 
 * returns a new <tt>TreeComponent</tt>
 * each time it is called, so they can be used in multiple containers.  These
 * components will all share the same <tt>TreeDrawing</tt>.
 * <p>
 * Empty trees are not drawn, but if a node contains both empty and nonempty
 * children, the horizontal padding that would surround a node is included
 * for empty children.  Consequently, in a binary tree, the line drawn from
 * a node to its left child always angles to the left, and the line drawn
 * from a node to its right child always angles to the right, even when the
 * other child is empty.  On the other hand, it may be more difficult to tell
 * which children may be empty in trees with other branching factors.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 *
 * @see BinaryTree
 * @see TreeInterface
 * @see Colorizer
 */
public final class TreeDrawing {

  /**
   * A string representation of the root of the tree.
   */
  private String root;

  /**
   * The color with which to draw the root.
   */
  private Color theColor;

  /**
   * Drawings of the children of the root.
   */
  private TreeDrawing[] children;

  /**
   * The width in characters of this drawing.
   */
  private int width;

  /**
   * The width in characters of the root, including padding.
   */
  private int rootWidth;

  /**
   * The total width in characters of all children, including padding between
   * children.
   */
  private int childrenWidth;

  /**
   * The height in text lines of this drawing.
   */
  private int height;

  /**
   * The width in characters of the horizontal separation between two nodes.
   */
  public static final int HORIZONTAL_SEPARATION = 1;

  /**
   * The height in lines of text of the vertical separation between parents
   * and children.
   */
  public static final int VERTICAL_SEPARATION = 1;

  /**
   * The character used as the standard for determining the character width.
   */
  public static final char STANDARD_CHARACTER = 'm';

  /**
   * Constructs a drawing of an empty tree.
   */
  public TreeDrawing() {
  }

  /**
   * Constructs a drawing of a tree with the given root and children.
   * @param     root        a drawing of the root without borders
   * @param     children    drawings of each of the children
   * @throws NullPointerException if either argument is <tt>null</tt>
   */
  public TreeDrawing(Node root, TreeDrawing[] children) {
    this.root = root.getContents();
    theColor = root.getColor();
    this.children = new TreeDrawing[children.length];
    System.arraycopy(children, 0, this.children, 0, children.length);
    computeSize();
  }

  /**
   * Constructs a drawing of the given tree.  The foreground of the drawing 
   * is black.  The contents of nodes are obtained using the 
   * <tt>String.valueOf(Object)</tt> method.
   * @param  tree      The tree to be drawn
   * @param  maxHeight The maximum height to display.  If this value is
   *                   negative, no nodes will be displayed.
   * @see java.lang.String String.valueOf(Object)
   */
  public TreeDrawing(TreeInterface tree, int maxHeight) {
    this(tree, maxHeight, null);
  }

  /**
   * Constructs a colorized drawing of the given tree.  The contents of nodes
   * are obtained using the <tt>String.valueOf(Object)</tt> method.
   * @param  tree      The tree to be drawn.  If <tt>null</tt>, no tree is
   *                   drawn.
   * @param  maxHeight The maximum height to display.  If this value is
   *                   negative, no nodes will be displayed.
   * @param  col       The Colorizer used to obtain the color of each node.
   *                   If <tt>null</tt>, each node is colored black.
   * @throws RuntimeException if <tt>col</tt> throws a RuntimeException on
   *                          any node of <tt>tree</tt>
   * @see java.lang.String String.valueOf(Object)
   */
  public TreeDrawing(TreeInterface tree, int maxHeight, Colorizer col) {
    if (maxHeight-- >= 0 && tree != null && !tree.isEmpty()) {
      Object r = tree.getRoot();
      root = String.valueOf(r);
      theColor = col == null ? Color.black : col.getColor(r);
      if (theColor == null) {
	theColor = Color.black;
      }
      TreeInterface[] chldr = tree.getChildren();
      if (chldr == null) {
	children = new TreeDrawing[0];
      }
      else {
	children = new TreeDrawing[chldr.length];
	for (int i = 0; i < chldr.length; i++) {
	  children[i] = new TreeDrawing(chldr[i], maxHeight, col);
	}
      }
      computeSize();
    }
  }

  /**
   * Computes the size fields for a nonempty tree.
   */
  private synchronized void computeSize() {
    rootWidth = root.length() + 2; // pad both ends
    childrenWidth = 0;
    if (children.length > 0) {
      if (children[0] != null) {
	childrenWidth += children[0].getWidth();
	height = children[0].getHeight();
      }
      for (int i = 1; i < children.length; i++) {
	childrenWidth += HORIZONTAL_SEPARATION;
	childrenWidth += children[i].getWidth();
	height = Math.max(height, 
			  children[i].getHeight());
      }
    }
    width = Math.max(rootWidth, childrenWidth);
    height += (height == 0 ? 0 : VERTICAL_SEPARATION) + 1;
  }


  /**
   * Returns the width of this tree in characters.
   */
  public int getWidth() {
    return width;
  }

  /**
   * Returns the height of this tree in text lines.
   */
  public int getHeight() {
    return height;
  }

  /**
   * Returns a <tt>TreeComponent</tt> displaying this drawing using
   * {@link TreeComponent#DEFAULT_FONT TreeComponent.DEFAULT_FONT}.
   */
  public TreeComponent getDrawing() {
    return new TreeComponent(this);
  }

  /**
   * Returns a <tt>TreeComponent</tt> displaying this drawing using the 
   * given Font.
   * @throws NullPointerException if <tt>fnt</tt> is <tt>null</tt>
   */
  public TreeComponent getDrawing(Font fnt) throws NullPointerException {
    return new TreeComponent(this, fnt);
  }

  /** 
   * Paints the TreeDrawing on the given graphics context.  
   */
  public void paint(Graphics g) {
    if (g != null) {
      FontMetrics fm = g.getFontMetrics();
      int charWidth = fm.charWidth(STANDARD_CHARACTER);
      int lineHeight = fm.getHeight();
      int viewWidth = charWidth * width;
      int viewHeight = lineHeight * height;
      int viewRootWidth = charWidth * rootWidth;
      int viewChildrenWidth = charWidth * childrenWidth;
      if (g.hitClip(0, 0, viewWidth, viewHeight)) {
	Color col = g.getColor();
	g.setColor(Color.black);
	int rootLoc = (width - rootWidth) * charWidth / 2;
	g.translate(rootLoc, 0);
	g.setColor(theColor);
	g.drawString(" " + root, 0, lineHeight - fm.getDescent());
	g.setColor(Color.black);
	g.drawRect(0, 0, viewRootWidth - 1, lineHeight - 1);
	g.translate(-rootLoc, 0);
	Point edgeStart = new Point(viewWidth / 2, lineHeight);
	Point loc = new Point((viewWidth - viewChildrenWidth) / 2,
			      (VERTICAL_SEPARATION + 1) * lineHeight);
	for (int i = 0; i < children.length; i++) {
	  if (children[i].getWidth() != 0) {
	    g.translate(loc.x, loc.y);
	    children[i].paint(g);
	    g.translate(-loc.x, -loc.y);
	    int cw = children[i].getWidth() * charWidth;
	    g.drawLine(edgeStart.x, edgeStart.y, loc.x + cw / 2, loc.y);
	    loc.translate(cw, 0);
	  }
	  loc.translate(HORIZONTAL_SEPARATION * charWidth, 0);
	}
	g.setColor(col);
      }
    }
  }
}
