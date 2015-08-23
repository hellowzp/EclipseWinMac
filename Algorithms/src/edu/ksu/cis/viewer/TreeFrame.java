/*
 * TreeFrame.java     11/15/99
 *
 * Copyright (c) 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;




/**
 * A frame for displaying a tree.  This provides a straightforward means
 * for displaying a tree that implements {@link TreeInterface TreeInterface}.
 * The frame
 * contains a scroll pane which contains a {@link TreeComponent TreeComponent}
 * displaying the tree.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 *
 * @see TreeInterface
 * @see TreeComponent
 */
public class TreeFrame extends JFrame {

  /**
   * Contstructs a TreeFrame displaying the given tree.  The frame has
   * a meaningful initial size and is ready to be displayed.
   * @param t         the tree to be displayed.
   * @param maxHeight the maximum height to be displayed
   * @param c         the Colorizer to colorize the tree
   */
  public TreeFrame(TreeInterface t, int maxHeight, Colorizer c) {
    init(new TreeDrawing(t, maxHeight, c).getDrawing());
  }

  /**
   * Contstructs a TreeFrame displaying the given tree.  The frame has
   * a meaningful initial size and is ready to be displayed.
   * @param t         the tree to be displayed.
   * @param maxHeight the maximum height to be displayed
   */
  public TreeFrame(TreeInterface t, int maxHeight) {
    init(new TreeDrawing(t, maxHeight).getDrawing());
  }

  /**
   * Constructs a TreeFrame displaying the given tree using the given Font.
   * The frame has a meaningful initial size and is ready to be displayed.
   * @param t         the tree to be displayed.
   * @param maxHeight the maximum height to be displayed
   * @param c         the Colorizer to colorize the tree
   * @param fnt       the Font to use
   * @throws NullPointerException if <tt>fnt</tt> is <tt>null</tt>.
   */
  public TreeFrame(TreeInterface t, int maxHeight, Colorizer c, Font fnt) {
    init(new TreeDrawing(t, maxHeight, c).getDrawing(fnt));
  }

  /**
   * Constructs a TreeFrame displaying the given tree using the given Font.
   * The frame has a meaningful initial size and is ready to be displayed.
   * @param t         the tree to be displayed.
   * @param maxHeight the maximum height to be displayed
   * @param fnt       the Font to use
   * @throws NullPointerException if <tt>fnt</tt> is <tt>null</tt>.
   */
  public TreeFrame(TreeInterface t, int maxHeight, Font fnt) {
    init(new TreeDrawing(t, maxHeight).getDrawing(fnt));
  }

  /**
   * Initializes this frame.
   * @param <tt>comp</tt> the <tt>JComponent</tt> to be displayed in the 
   *                      <tt>JScrollPane</tt>.
   */
  private void init(JComponent comp) {
    JScrollPane p = new JScrollPane();
    p.setPreferredSize(new Dimension(300, 300));
    p.setViewportView(comp);
    getContentPane().add(p);
    pack();
  }
}
