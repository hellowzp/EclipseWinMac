/*
 * Viewer.java       Dec 26, 2005
 *
 * Copyright (c) 2005, Rod Howell, all rights reserved.
 */
package edu.ksu.cis.heapviewer;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.UIManager;



/**
 * 
 *
 * @author Rod Howell
 *         (<a href="mailto:rhowell@ksu.edu">rhowell@ksu.edu</a>)
 *
 */
public class Viewer extends JApplet {

	  /**
	   * The choice box for selecting the type of heap.
	   */
	  private JComboBox choices 
	    = new JComboBox(new Object[] {"Binary Heap", "Binomial Queue",
										"Leftist Heap", "Randomized Heap", 
										"Skew Heap"});

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
	public static void main(String[] args) {
	    Viewer v = new Viewer();
	    JFrame fr = new JFrame();
	    fr.getContentPane().add(v);
	    fr.setTitle("Heap Viewer");
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
			new HeapFrame(new BinaryHeap(), "Binary Heap").show();
			break;
        case 1:
			new HeapFrame(new BinomialQueue(), "Binomial Queue").show();
	    	break;
		case 2:
			new HeapFrame(new LeftistHeap(), "Leftist Heap").show();
			break;
		case 3:
        	new HeapFrame(new RandomizedHeap(), "Randomized Heap").show();
        	break;
		case 4:
	    	new HeapFrame(new SkewHeap(), "Skew Heap").show();
	    }
	  }
	/**
	 * Compares the integers encoded by the given <tt>Strings</tt>.
	 * 
	 * @param s   The first <tt>String</tt>
	 * @param t   The second <tt>String</tt>
	 * @return    A negative value if <tt>s &lt; t</tt>, zero if
	 *            <tt>s = t</tt>, or a positive value if
	 *            <tt>s &gt; t</tt>, where <tt>s</tt> and <tt>t</tt>
	 *            are interpreted as integers.
	 * 
	 * @throws NumberFormatException
	 *         if either <tt>s</tt> or <tt>t</tt> does not encode
	 *         an integer.
	 */
	public static int compare(String s, String t)
		throws NumberFormatException {
		
		int i = Integer.parseInt(s);
		int j = Integer.parseInt(t);
		return i - j;
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
