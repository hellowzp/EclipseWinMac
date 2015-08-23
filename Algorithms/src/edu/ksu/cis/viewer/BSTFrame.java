/*
 * BSTFrame.java     11/15/99
 *
 * Copyright (c) 1998, 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;




import java.awt.Point;

/**
 * A Frame for creating and manipulating a data structure with functionality
 * equivalent to a binary search tree.  The Frame has a text field for entering
 * a String to be used as a key, along with the following buttons:
 * <ul>
 * <li> Put - inserts the key into the tree
 * <li> Remove - removes the key from the tree
 * <li> Back - moves backward through the history of trees created
 * <li> Forward - moves forward through the history of trees created
 * <li> Clone - creates a new Frame with an identical tree and identical 
 *      history
 * </ul>
 * The frame also contains controls for changing the font used to render
 * the trees.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 *
 * @see         BSTInterface
 * @see         AVLTree
 * @see         BinarySearchTree
 * @see         SplayTree
 */
public class BSTFrame extends JFrame {

  /**
   * The scroll pane containing the drawing of the tree.
   */
  private JScrollPane theScrollPane = new JScrollPane();

  /**
   * The text field for entering keys.
   */
  private JTextField input = new JTextField(10);

  /**
   * The "Back" button.
   */
  private JButton backBtn = new JButton("Back");

  /**
   * The "Forward" button.
   */
  private JButton forwardBtn = new JButton("Forward");

  /**
   * The tree displayed.
   */
  private BSTInterface theTree;

  /**
   * The trees accessible via the "Back" button.
   */
  private Stack history;

  /**
   * The trees accessible via the "Forward" button.
   */
  private Stack future;

  /**
   * The choice box for selecting the font size for rendering the tree.
   */
  private JComboBox fonts = 
    new JComboBox(new Object[] {"10", "12", "14", "16", "18", "24"});

  /**
   * The last font size requested by the user for any BSTFrame.
   */
  private static int lastSize = 12;

  /**
   * The last font style requested by the user for any BSTFrame.
   */
  private static int lastStyle = Font.PLAIN;

  /**
   * The checkbox for selecting bold font.
   */
  private JCheckBox boldBox = new JCheckBox("Bold");

  /**
   * The checkbox for selecting italic font.
   */
  private JCheckBox italicBox = new JCheckBox("Italic");

  /**
   * The font currently used to render the tree.
   */
  private Font currentFont;

  /**
   * Constructs a BSTFrame for manipulating the given tree.
   * @param      t      the tree to manipulate
   * @param      title  the title of the window
   */
  public BSTFrame(BSTInterface t, String title) {
    this(t, title, lastSize, lastStyle, new Stack(), new Stack());
  }

  /**
   * Constructs a BSTFrame for manipulating the given tree.
   * @param      t      the tree to manipulate
   * @param      title  the title of the window
   * @param      size   the size of the font used for the nodes
   * @param      style  the style of the font used for the nodes, as documented
   *                    in {@link java.awt.Font java.awt.Font}
   * @throws IllegalArgumentException if <tt>size</tt> is outside its
   *                                  proper range.
   * @see java.awt.Font java.awt.Font.Font(String, int, int)
   */
  public BSTFrame(BSTInterface t, String title, int size, int style) {
    this(t, title, size, style, new Stack(), new Stack());
  }
  
  /**
   * Constructs a BSTFrame for manipulating the given tree with the given
   * history any future.
   * @param t          the tree to manipulate
   * @param title      the title of the window
   * @param fontSize   the size of the font used for the nodes
   * @param fontStyle  the style of the font used for the nodes, as documented
   *                   in {@link java.awt.Font java.awt.Font}
   * @param history    the trees to be accessible via the "Back" button
   * @param future     the trees to be accessible via the "Forward" button
   * @throws IllegalArgumentException if <tt>size</tt> is outside its
   *                                  proper range.
   * @see java.awt.Font java.awt.Font(String, int, int)
   */
  private BSTFrame(BSTInterface t, String title,
		   int fontSize, int fontStyle,
		   Stack history, Stack future) {
    if (fontSize < 1) 
      throw new IllegalArgumentException();
    theTree = t;
    this.history = history;
    this.future = future;

    setTitle(title);
    Container content = getContentPane();

    // Set up the scroll pane containing the tree
    theScrollPane.setPreferredSize(new Dimension(300, 300));
    JComponent drawing = currentFont == null ? theTree.getDrawing() :
      theTree.getDrawing(currentFont);
    currentFont = drawing.getFont();
    theScrollPane.setViewportView(drawing);
    content.add(theScrollPane);

    // The panel at the top
    JPanel top = new JPanel();
    top.add(new JLabel("Key (any string): "));
    top.add(input);
    top.add(new JLabel("Font Size: "));
    fonts.setSelectedItem(String.valueOf(fontSize));
    fonts.setEditable(true);
    FontListener fl = new FontListener(this);
    fonts.addActionListener(fl);
    top.add(fonts);
    boldBox.setSelected((fontStyle & Font.BOLD) != 0);
    boldBox.addActionListener(fl);
    top.add(boldBox);
    italicBox.setSelected((fontStyle & Font.ITALIC) != 0);
    italicBox.addActionListener(fl);
    top.add(italicBox);
    content.add(top, "North");

    // The panel at the bottom
    JPanel p = new JPanel();
    JButton putBtn = new JButton("Put");
    putBtn.addActionListener(new PutListener(this));
    p.add(putBtn);
    JButton removeBtn = new JButton("Remove");
    removeBtn.addActionListener(new RemoveListener(this));
    p.add(removeBtn);
    if (history.empty()) {
      backBtn.setEnabled(false);
    }
    backBtn.addActionListener(new BackListener(this));
    p.add(backBtn);
    if (future.empty()) {
      forwardBtn.setEnabled(false);
    }
    forwardBtn.addActionListener(new ForwardListener(this));
    p.add(forwardBtn);
    JButton cloneBtn = new JButton("Clone");
    cloneBtn.addActionListener(new CloneListener(this));
    p.add(cloneBtn);
    content.add(p, "South");

    adjustFont();
    pack();
  }

  /**
   * Changes the font used to render the tree using values chosen by the
   * user.  If either value is illegal, the font is unchanged.
   */
  public synchronized void adjustFont() {
    try {
      int size = 
	Integer.parseInt(fonts.getSelectedItem().toString());
      if (size < 1) throw new NumberFormatException();
      int style = getStyle();
      currentFont = 
	new Font("Monospaced", style, size);
      redisplay();
      lastSize = size;
      lastStyle = style;
    }
    catch (NumberFormatException e) {
      fonts.setSelectedItem(String.valueOf(currentFont.getSize()));
      input.requestFocus();
    }
  }

  /**
   * Returns the font style selected by the user.
   */
  private int getStyle() {
    int boldMask = boldBox.isSelected() ? Font.BOLD : 0;
    int italicMask = italicBox.isSelected() ? Font.ITALIC : 0;
    return boldMask | italicMask;
  }


  /**
   * Displays the current tree.
   */
  private synchronized void redisplay() {
    Point loc = theScrollPane.getViewport().getViewPosition();
    JComponent drawing = theTree.getDrawing(currentFont);
    theScrollPane.setViewportView(drawing);
    theScrollPane.validate();
    theScrollPane.getViewport().setViewPosition(loc);
    input.setText("");
    input.requestFocus();
  }

  /**
   * Inserts a String provided by the user into the tree.
   */
  public synchronized void put() {
    forwardBtn.setEnabled(false);
    history.push(theTree);
    theTree = ((BSTInterface) theTree.clone()).put(input.getText());
    if (!future.empty()) {
      future = new Stack();
    }
    backBtn.setEnabled(true);
    redisplay();
  }

  /**
   * Removes a String given by the use from the tree.
   */
  public synchronized void remove() {
    forwardBtn.setEnabled(false);
    history.push(theTree);
    theTree = ((BSTInterface) theTree.clone()).remove(input.getText());
    if (!future.empty()) {
      future = new Stack();
    }
    backBtn.setEnabled(true);
    redisplay();
  }

  /**
   * Goes to the previous tree in the history.
   */
  public synchronized void back() {
    if (!history.empty()) {
      future.push(theTree);
      forwardBtn.setEnabled(true);
      theTree = (BSTInterface) history.pop();
      if (history.empty()) {
	      backBtn.setEnabled(false);
      }
      redisplay();
    }
  }

  /**
   * Goes to the next tree in the history.
   */
  public synchronized void forward() {
    if (!future.empty()) {
      history.push(theTree);
      backBtn.setEnabled(true);
      theTree = (BSTInterface) future.pop();
      if (future.empty()) {
	      forwardBtn.setEnabled(false);
      }
      redisplay();
    }
  }

  /**
   * Creates and displays a clone of this frame.
   */
  public synchronized void makeClone() {
    int font = Integer.parseInt(fonts.getSelectedItem().toString());
    JFrame f = new BSTFrame((BSTInterface) theTree.clone(), getTitle(),
			    font, getStyle(),
			    (Stack) history.clone(),
			    (Stack) future.clone());
    f.setSize(getSize());
    f.show();
    input.requestFocus();
  }

  /**
   * Displays this frame.
   */
  public void show() {
    super.show();
    input.requestFocus();
  }
}

/**
 * The event handler for the "Put" button.
 */
class PutListener implements ActionListener {

  /**
   * The frame containing the tree to be modified.
   */
  private BSTFrame theFrame;

  /**
   * Constructs a listener for adding nodes to the tree displayed by 
   * <tt>f</tt>.
   */
  public PutListener(BSTFrame f) {
    theFrame = f;
  }

  /**
   * Handles the event.
   * @param e the event
   */
  public void actionPerformed(ActionEvent e) {
    theFrame.put();
  }
}

/**
 * The event handler for the "Remove" button.
 */
class RemoveListener implements ActionListener {

  /**
   * The frame containing the tree to be modified.
   */
  private BSTFrame theFrame;

  /**
   * Constructs a listener for removing nodes from the tree displayed by 
   * <tt>f</tt>.
   */
  public RemoveListener(BSTFrame f) {
    theFrame = f;
  }

  /**
   * Handles the event.
   * @param e the event
   */
  public void actionPerformed(ActionEvent e) {
    theFrame.remove();
  }
}

/**
 * The event handler for the "Back" button.
 */
class BackListener implements ActionListener {

  /**
   * The frame to be modified.
   */
  private BSTFrame theFrame;

  /**
   * Constructs a listener for moving backward in the history of <tt>f</tt>.
   */
  public BackListener(BSTFrame f) {
    theFrame = f;
  }

  /**
   * Handles the event.
   * @param e the event
   */
  public void actionPerformed(ActionEvent e) {
    theFrame.back();
  }
}

/**
 * The event handler for the "Forward" button.
 */
class ForwardListener implements ActionListener {

  /**
   * The frame to be modified.
   */
  private BSTFrame theFrame;

  /**
   * Constructs a listener for moving forward in the history of <tt>f</tt>.
   */
  public ForwardListener(BSTFrame f) {
    theFrame = f;
  }

  /**
   * Handles the event.
   * @param e the event
   */
  public void actionPerformed(ActionEvent e) {
    theFrame.forward();
  }
}

/**
 * The event handler for the "Clone" button.
 */
class CloneListener implements ActionListener {

  /**
   * The frame to be cloned.
   */
  private BSTFrame theFrame;

  /**
   * Constructs a listener for cloning <tt>f</tt>.
   */
  public CloneListener(BSTFrame f) {
    theFrame = f;
  }

  /**
   * Handles the event.
   * @param e the event
   */
  public void actionPerformed(ActionEvent e) {
    theFrame.makeClone();
  }

}

/**
 * The event handler for the font-changing components.
 */
class FontListener implements ActionListener {

  /**
   * The frame whose font is to be changed.
   */
  private BSTFrame theFrame;

  /**
   * Constructs a listener for changing the font of <tt>f</tt>.
   */
  public FontListener(BSTFrame f) {
    theFrame = f;
  }

  /**
   * Handles the event.
   * @param e the event
   */
  public void actionPerformed(ActionEvent e) {
    theFrame.adjustFont();
  }
}
