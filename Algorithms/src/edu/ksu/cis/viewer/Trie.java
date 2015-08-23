/*
 * BSTInterface.java     12/4/99
 *
 * Copyright (c) 1998, 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;

import java.util.Arrays;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JComponent;



/**
 * An immutable trie that can draw itself.  The trie is immutable in 
 * the sense that any Trie returned by a public constructor or method 
 * cannot subsequently be changed.  A trie is comprised of a nonempty tree
 * containing all of the characters used in the strings that it stores.
 * The root of the tree stores <tt>null</tt>, and its children store the
 * first characters of all of the strings stored.  Characters occurring in
 * subsequent positions of a string are stored in children of the characters
 * they follow.  Nodes that end contained strings are colored red, and other
 * nodes are colored black.  Thus a string is contained in the trie if its
 * first character is a child of the root, its second character is a child
 * of that character, and so on until its last character is reached, and this
 * character is red.  If the empty string is to be stored, the root will
 * be drawn red.  The children of a given node will be drawn in order of 
 * their Unicode values.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 *
 */
public final class Trie implements BSTInterface, TreeInterface {

  /** 
   * The number of Strings stored in this Trie.
   */
  private int size;

  /**
   * The children of this Trie, stored in order of the Unicode values of
   * their labels.
   */
  private Trie[] children;

  /**
   * The labels of the children.
   */
  private char[] labels;

  /**
   * The root of this Trie.  This Object contains a Character and a boolean,
   * indicating whether this Trie denotes the end of a stored String.
   */
  private TrieNode root;

  /**
   * The Colorizer for any Trie.  It will color a node red if it denotes
   * the end of a stored string, or black otherwise.
   */
  private static TrieColorizer theColorizer = new TrieColorizer();

  /**
   * Constructs an empty Trie.
   */
  public Trie() {
    this(false, 0, null, 0);
  }

  /**
   * Constructs a Trie whose fields are partly initialized.  It is up to
   * the calling method to initialize the individual children and their labels.
   * @param word         Indicates whether this Trie will denote the end of 
   *                     a stored string.
   * @param size         The number of strings this Trie will store.
   * @param c            The Character to be stored at the root of this Trie.
   * @param numChildren  The number of children this Trie will have.
   */
  private Trie(boolean word, int size, Character c, int numChildren) {
    this.size = size;
    this.root = new TrieNode(c, word);
    children = new Trie[numChildren];
    labels = new char[numChildren];
  }

  /**
   * Returns the <tt>Trie</tt> resulting from the insertion of <tt>key</tt>
   * into this <tt>Trie</tt>.  If <tt>key</tt> is already in this 
   * <tt>Trie</tt>, an identical <tt>Trie</tt> will be returned.
   */
  public BSTInterface put(String key) throws NullPointerException {
    if (key.length() == 0) {
      if (root.containsEmptyString()) {
	return this;
      }
      else {
	Trie t = new Trie(true, size + 1, root.getCharacter(), 
			  children.length);
	System.arraycopy(children, 0, t.children, 0, children.length);
	System.arraycopy(labels, 0, t.labels, 0, labels.length);
	return t;
      }
    }
    else {
      char c = key.charAt(0);
      int loc = Arrays.binarySearch(labels, c);
      if (loc < 0) {
	loc = -(loc + 1);
	Trie t = new Trie(root.containsEmptyString(), size + 1, 
			  root.getCharacter(), children.length + 1);
	System.arraycopy(children, 0, t.children, 0, loc);
	t.children[loc] = (Trie) new Trie().put(key.substring(1));
	t.children[loc].root = new TrieNode(new Character(c), 
					    key.length() == 1);
	System.arraycopy(children, loc, t.children, loc + 1, 
			 children.length - loc);
	System.arraycopy(labels, 0, t.labels, 0, loc);
	t.labels[loc] = c;
	System.arraycopy(labels, loc, t.labels, loc + 1, labels.length - loc);
	return t;
      }
      else {
	Trie t = new Trie(root.containsEmptyString(), 
			  size - children[loc].size, root.getCharacter(), 
			  children.length);
	System.arraycopy(children, 0, t.children, 0, loc);
	t.children[loc] = (Trie) children[loc].put(key.substring(1));
	System.arraycopy(children, loc + 1, t.children, loc + 1,
			 children.length - loc - 1);
	System.arraycopy(labels, 0, t.labels, 0, labels.length);
	t.size += t.children[loc].size;
	return t;
      }
    }
  }

  /**
   * Returns the <tt>Trie</tt> resulting from the removal of <tt>key</tt>
   * from this <tt>Trie</tt>.  If <tt>key</tt> is not in this <tt>Trie</tt>,
   * an identical <tt>Trie</tt> is returned.
   */
  public BSTInterface remove(String key) throws NullPointerException {
    if (key.length() == 0) {
      if (root.containsEmptyString()) {
	Trie t = new Trie(false, size - 1, root.getCharacter(), 
			  children.length);
        System.arraycopy(children, 0, t.children, 0, children.length);
	System.arraycopy(labels, 0, t.labels, 0, labels.length);
	return t;
      }
      else {
	return this;
      }
    }
    else {
      char c = key.charAt(0);
      int loc = Arrays.binarySearch(labels, c);
      if (loc < 0) {
	return this;
      }
      else {
	Trie child = (Trie) children[loc].remove(key.substring(1));
	if (child.size == 0) {
	  Trie t = new Trie(root.containsEmptyString(), 
			    size - children[loc].size, root.getCharacter(), 
			    children.length - 1);
	  System.arraycopy(children, 0, t.children, 0, loc);
	  System.arraycopy(children, loc + 1, t.children, loc, 
			   t.children.length - loc);
	  System.arraycopy(labels, 0, t.labels, 0, loc);
	  System.arraycopy(labels, loc + 1, t.labels, loc, 
			   t.labels.length - loc);
	  return t;
	}
	else {
	  Trie t = new Trie(root.containsEmptyString(), 
			    size - children[loc].size + child.size,
			    root.getCharacter(), children.length);
	  System.arraycopy(children, 0, t.children, 0, loc);
	  t.children[loc] = child;
	  System.arraycopy(children, loc + 1, t.children, loc + 1,
			   children.length - loc - 1);
	  System.arraycopy(labels, 0, t.labels, 0, labels.length);
	  return t;
	}
      }
    }
  }

  /**
   * Returns a drawing of this <tt>Trie</tt>.
   */
  public JComponent getDrawing() {
    return new TreeDrawing(this, 0x7fffffff, theColorizer).getDrawing();
  }

  /**
   * Returns a drawing of this tree using the given Font.
   * @throws NullPointerException if <tt>fnt</tt> is <tt>null</tt>.
   */
  public JComponent getDrawing(Font fnt) throws NullPointerException {
    return new TreeDrawing(this, 0x7fffffff, theColorizer).getDrawing(fnt);
  }

  /**
   * Because this structure is immutable, this method simply returns this
   * <tt>Trie</tt>.
   */
  public Object clone() {
    return this;
  }

  /**
   * Returns the root of this <tt>Trie</tt>.  This method is intended for
   * use by the <tt>TreeDrawing</tt> class.
   */
  public Object getRoot() {
    return root;
  }

  /**
   * Returns the children of this <tt>Trie</tt>.  This method is intended
   * for use by the <tt>TreeDrawing</tt> class.
   */
  public TreeInterface[] getChildren() {
    Trie[] c = new Trie[children.length];
    System.arraycopy(children, 0, c, 0, c.length);
    return c;
  }

  /**
   * Always returns <tt>false</tt>.  This method is intended
   * for use by the <tt>TreeDrawing</tt> class.
   */
  public boolean isEmpty() {
    return false;
  }
}

/**
 * The node to be drawn at the root of a <tt>Trie</tt>.
 */
class TrieNode {

  /**
   * The character to be drawn.
   */
  private Character theCharacter;

  /**
   * <tt>true</tt> if this node denotes the end of a String.
   */
  private boolean isWord;

  /**
   * Constructs a TrieNode.
   * @param c  The Character to be drawn at this node.
   * @param w  <tt>true</tt> if this node denotes the end of a String.
   */
  public TrieNode(Character c, boolean w) {
    theCharacter = c;
    isWord = w;
  }

  /**
   * Returns a String containing the Character to be drawn.
   */
  public String toString() {
    return String.valueOf(theCharacter);
  }

  /**
   * Returns <tt>true</tt> if this node denotes the end of a String.
   */
  public boolean containsEmptyString() {
    return isWord;
  }

  /**
   * Returns the Character to be drawn.
   */
  public Character getCharacter() {
    return theCharacter;
  }
}

/**
 * A Colorizer for a Trie.
 */
class TrieColorizer implements Colorizer {

  /**
   * Returns the color to draw the given Object.  If the given Object
   * is a valid root of a <tt>Trie</tt> and indicates the end of a String,
   * the color returned is red.  Otherwise, the color returned is black.
   */
  public Color getColor(Object obj) {
    if (obj != null && obj instanceof TrieNode) {
      return ((TrieNode) obj).containsEmptyString() ? Color.red : Color.black;
    }
    else {
      return Color.black;
    }
  }
}
