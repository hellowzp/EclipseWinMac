/*
 * Viewer.java     11/15/99
 *
 * Copyright (c) 1998, 1999 Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Container;
import java.awt.FlowLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.UIManager;



import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This is the driver class for the Search Tree Viewer.  It may be 
 * run as either an application
 * or an applet.  In either case, it displays the following GUI:
 * <p>
 * <img src="Viewer.gif">
 * <p>
 * On the left are seven choices:
 * <ul>
 * <li> AA Tree
 * <li> AVL Tree
 * <li> Binary Search Tree
 * <li> Red-Black Tree
 * <li> Patricia Trie
 * <li> Splay Tree
 * <li> Trie
 * </ul>
 * When the Start button is pressed, a frame for creating and manipulating an
 * instance of the current choice is displayed.  If the GUI is running as an
 * application, when it is closed, the program terminates.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 *
 * @see         BSTFrame
 * @see         AATree
 * @see         AVLTree
 * @see         BinarySearchTree
 * @see         RedBlackTree
 * @see         PatriciaTrie
 * @see         SplayTree
 * @see         Trie
 */
public class Viewer extends JApplet {

  /**
   * The choice box for selecting the type of search tree.
   */
  private JComboBox choices 
    = new JComboBox(new Object[] {"AA Tree", "AVL Tree", "Binary Search Tree",
				  "Red-Black Tree", "Patricia Trie",
				  "Splay Tree", "Trie"});

  /**
   * Constructs an instance of the Viewer GUI with default size.
   */
  public Viewer() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e) {
      // This shouldn't happen
      e.printStackTrace();
    }
    choices.setSelectedIndex(0);
    choices.setEditable(false);
    Container content = getContentPane();
    content.setLayout(new FlowLayout());
    content.add(choices);
    JButton st = new JButton("Start");
    st.addActionListener(new StartButtonListener(this));
    content.add(st);
  }

  /**
   * Starts the viewer as an application.  Opens a Frame containing the 
   * Viewer GUI.  If the Frame is closed, the application terminates.
   * @param         args    command-line arguments (unused)
   */
  public static void main(String args[]) {
    Viewer v = new Viewer();
    JFrame fr = new JFrame();
    fr.getContentPane().add(v);
    fr.setTitle("Viewer");
    fr.addWindowListener(new Closer());
    fr.pack();
    fr.show();
  }

  /**
   * Event handler for the Start button in the Viewer GUI.  Opens a Frame for
   * creating and manipulating an instance of the current choice.
   */
  public void openWindow() {
    switch (choices.getSelectedIndex()) {
    case 0:
      new BSTFrame(new AATree(), "AA Tree").show();
      break;
    case 1:
      new BSTFrame(new AVLTree(), "AVL Tree").show();
      break;
    case 2:
      new BSTFrame(new BinarySearchTree(), "Binary Search Tree").show();
      break;
    case 3:
      new BSTFrame(new RedBlackTree(), "Red-Black Tree").show();
      break;
    case 4:
      new BSTFrame(new PatriciaTrie(), "Patricia Trie").show();
      break;
    case 5:
      new BSTFrame(new SplayTree(), "Splay Tree").show();
      break;
    case 6:
      new BSTFrame(new Trie(), "Trie").show();
      break;
    }
  }
}

/**
 * The event handler for the Start button.
 */
class StartButtonListener implements ActionListener {

  /**
   * The Viewer containing the button.
   */
  private Viewer theViewer;

  /**
   * Constructs a listener for the Start button.
   */
  public StartButtonListener(Viewer v) {
    theViewer = v;
  }

  /**
   * Handles the event.
   * @param e the event
   */
  public void actionPerformed(ActionEvent e) {
    theViewer.openWindow();
  }
}

/**
 * The event handler for the window-closing event.  This event is handled
 * by terminating the program.
 */
class Closer extends WindowAdapter {

  /**
   * Handles the event.
   * @param e the event
   */
  public void windowClosing(WindowEvent e) {
    System.exit(0);
  }
}
