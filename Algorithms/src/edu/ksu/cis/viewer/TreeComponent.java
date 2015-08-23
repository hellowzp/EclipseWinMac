/*
 *
 * TreeComponent.java    11/15/99
 * Copyright (c) 1998, 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.JPanel;



/**
 * A component displaying a graphical representation of a tree.
 * <tt>TreeComponent</tt>s can be constructed quickly from 
 * {@link TreeDrawing TreeDrawing}s.  The actual rendering is done by the 
 * underlying
 * <tt>TreeDrawing</tt>.  The preferred size is determined automatically, so
 * that calls to <tt>setPrefferedSize</tt> have no effect.  
 * Because this
 * component should not have children, no children are rendered, even if
 * they are added.  The colors of the drawing are determined by the
 * underlying <tt>TreeDrawing</tt>, so the foreground color is unused.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 *
 * @see TreeDrawing
 */
public class TreeComponent extends JPanel {

  /**
   * The object containing the description of the tree.
   */
  private TreeDrawing theTree;

  /**
   * The default Font.
   */
  public static final Font DEFAULT_FONT = 
    new Font("Monospaced", Font.PLAIN, 12);

  /**
   * Constructs a new TreeComponent displaying the given TreeDrawing using
   * the default font.
   * @throws NullPointerException if <tt>tree</tt> is <tt>null</tt>
   */
  public TreeComponent(TreeDrawing tree) throws NullPointerException {
    this(tree, DEFAULT_FONT);
  }

  /**
   * Constructs a new TreeComponent displaying the given TreeDrawing using
   * the given Font.
   * @throws NullPointerException  if either argument is <tt>null</tt>
   */
  public TreeComponent(TreeDrawing tree, Font fnt) 
    throws NullPointerException {
    if (tree == null || fnt == null) throw new NullPointerException();
    theTree = tree;
    setFont(fnt);
    setLayout(null);
  }

  /**
   * Returns the preferred size of this component.  This value is based
   * solely on the underlying <tt>TreeDrawing</tt> and the component's current
   * font.  Any value set using <tt>setPreferredSize</tt> is ignored.
   * @return               the preferred size of the drawing
   */
  public Dimension getPreferredSize() {
    FontMetrics fm = getFontMetrics(getFont());
    int width = 
      theTree.getWidth() * fm.charWidth(TreeDrawing.STANDARD_CHARACTER);
    int height = theTree.getHeight() * fm.getHeight();
    return new Dimension(width, height);
  }

  /**
   * Paints this component on the given graphics context.
   * @param   g   The graphics context to use for painting.
   */
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    theTree.paint(g);
  }

  /**
   * As this component should have no children, this method does nothing.
   */
  protected void paintChildren(Graphics g) {}
}
