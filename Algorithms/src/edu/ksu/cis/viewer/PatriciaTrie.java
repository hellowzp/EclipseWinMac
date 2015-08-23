/*
 * PatriciaTrie.java     12/4/99
 *
 * Copyright (c) 1998, 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;

import java.awt.Font;
import javax.swing.JComponent;




/**
 * An immutable Patricia trie that can draw itself.  The trie is immutable in 
 * the sense that any PatriciaTrie returned by a public constructor or method 
 * cannot subsequently be changed.  A Patricia trie contains a set of strings.
 * Each subtree stores in its root the longest common prefix of all strings
 * contained within that subtree.  For each string stored in the subtree, a
 * suffix is computed by removing the prefix stored in the root.  If there is
 * more than one such suffix, each of the suffixes is stored in one of the 
 * children, a different child for each distinct character beginning a suffix,
 * plus a child for the empty suffix if it is present.  Note that according
 * to these rules, no node other than the root or a leaf will store an empty
 * string.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 *
 */
public final class PatriciaTrie implements BSTInterface, TreeInterface {

  /** 
   * The number of Strings stored in this PatriciaTrie.
   */
  private int size;

  /**
   * The children of this PatriciaTrie, stored in order of the Unicode values 
   * of their first character; if the empty string is stored, its
   * PatriciaTrie is stored first.
   */
  private PatriciaTrie[] children;

  /**
   * The first characters of the children, or the empty string.
   */
  private String[] labels;

  /**
   * The root of this PatriciaTrie.
   */
  private String root;

  /**
   * Constructs an empty PatriciaTrie.
   */
  public PatriciaTrie() {
    this(0, null, 0);
  }

  /**
   * Constructs a PatriciaTrie whose fields are partly initialized.  It is up 
   * to the calling method to initialize the individual children.
   * @param size         The number of strings this Trie will store.
   * @param s            The String to be stored at the root of this Trie.
   * @param numChildren  The number of children this Trie will have.
   */
  private PatriciaTrie(int size, String s, int numChildren) {
    this.size = size;
    this.root = s;
    children = new PatriciaTrie[numChildren];
    labels = new String[numChildren];
  }

  /**
   * Returns the <tt>PatriciaTrie</tt> resulting from the insertion of 
   * <tt>key</tt> into this <tt>PatriciaTrie</tt>.  If <tt>key</tt> is 
   * already in this <tt>PatriciaTrie</tt>, an identical <tt>Trie</tt> will 
   * be returned.
   */
  public BSTInterface put(String key) throws NullPointerException {
    if (size == 0) 
      return new PatriciaTrie(1, key, 0);
    else {
      int i = 0;
      while (i < root.length() && i < key.length() && 
	     root.charAt(i) == key.charAt(i)) i++;
      String keySuffix = key.substring(i);
      if (i < root.length()) {  // Split node
	String prefix = root.substring(0, i);
	String rootSuffix = root.substring(i);
	PatriciaTrie rootTrie = new PatriciaTrie(size, rootSuffix, 
						 children.length);
	System.arraycopy(children, 0, rootTrie.children, 0, children.length);
	System.arraycopy(labels, 0, rootTrie.labels, 0, labels.length);
	PatriciaTrie keyTrie = new PatriciaTrie(1, keySuffix, 0);
	PatriciaTrie t = new PatriciaTrie(size+1, prefix, 2);
	int rootLoc = rootSuffix.compareTo(keySuffix) > 0 ? 1 : 0;
	int keyLoc = 1 - rootLoc;
	t.children[rootLoc] = rootTrie;
	t.children[keyLoc] = keyTrie;
	t.labels[rootLoc] = rootSuffix.substring(0, 1);
	t.labels[keyLoc] = keySuffix.length() > 0 ? keySuffix.substring(0, 1)
	  : "";
	return t;
      }
      else {
	String lbl = i < key.length() ? key.substring(i, i+1) : "";
	int j = 0;
	while (j < labels.length && labels[j].compareTo(lbl) < 0) j++;
	if (j < labels.length && labels[j].equals(lbl)) {
	  PatriciaTrie c = (PatriciaTrie) children[j].put(keySuffix);
	  PatriciaTrie t = new PatriciaTrie(size + c.size - children[j].size,
					    root, children.length);
	  System.arraycopy(children, 0, t.children, 0, children.length);
	  System.arraycopy(labels, 0, t.labels, 0, labels.length);
	  t.children[j] = c;
	  return t;
	}
	else {
	  int start = 0;
	  PatriciaTrie c = new PatriciaTrie(1, keySuffix, 0);
	  PatriciaTrie t;
	  if (children.length == 0) {
	    t = new PatriciaTrie(size+1, root, 2);
	    t.children[0] = new PatriciaTrie(1, "", 0);
	    t.labels[0] = "";
	    start = 1;
	  }
	  else 
	    t = new PatriciaTrie(size+1, root, children.length + 1);
	  System.arraycopy(children, 0, t.children, start, j);
	  t.children[start+j] = c;
	  System.arraycopy(children, j, t.children, start+j+1, 
			   children.length-j);
	  System.arraycopy(labels, 0, t.labels, start, j);
	  t.labels[start+j] = lbl;
	  System.arraycopy(labels, j, t.labels, start+j+1, labels.length-j);
	  return t;
	}
      }
    }
  }

  /**
   * Returns the <tt>PatriciaTrie</tt> resulting from the removal of 
   * <tt>key</tt> from this <tt>PatriciaTrie</tt>.  If <tt>key</tt> is not 
   * in this <tt>PatriciaTrie</tt>,
   * an identical <tt>PatriciaTrie</tt> is returned.
   */
  public BSTInterface remove(String key) throws NullPointerException {
    if (children.length == 0 && key.equals(root))
      return new PatriciaTrie();
    else if (children.length == 0 || key.length() < root.length() || 
	     !key.substring(0, root.length()).equals(root)) return this;
    else {
      String suffix = key.substring(root.length());
      String lbl = suffix.length() == 0 ? "" : suffix.substring(0, 1);
      int i = 0;
      while (i < labels.length && labels[i].compareTo(lbl) < 0) 
	  i++;
      if (i == labels.length || !labels[i].equals(lbl)) return this;
      else {
	PatriciaTrie c = (PatriciaTrie) children[i].remove(suffix);
	if (c.size == 0) {
	  if (children.length == 2) { // combine nodes
	    PatriciaTrie rem = children[1-i];
	    PatriciaTrie t = 
	      new PatriciaTrie(size-1, root + rem.root, rem.children.length);
	    System.arraycopy(rem.children, 0, t.children, 0, 
			     rem.children.length);
	    System.arraycopy(rem.labels, 0, t.labels, 0, rem.labels.length);
	    return t;
	  }
	  else { // remove node
	    PatriciaTrie t = new PatriciaTrie(size-1, root, children.length-1);
	    System.arraycopy(children, 0, t.children, 0, i);
	    System.arraycopy(children, i+1, t.children, i, 
			     t.children.length-i);
	    System.arraycopy(labels, 0, t.labels, 0, i);
	    System.arraycopy(labels, i+1, t.labels, i, t.labels.length-i);
	    return t;
	  }
	}
	else if (c == children[i]) return this;
	else { // replace child
	  PatriciaTrie t = new PatriciaTrie(size-1, root, children.length);
	  System.arraycopy(children, 0, t.children, 0, children.length);
	  System.arraycopy(labels, 0, t.labels, 0, labels.length);
	  t.children[i] = c;
	  return t;
	}
      }
    }
  }

  /**
   * Returns a drawing of this <tt>PatriciaTrie</tt>.
   */
  public JComponent getDrawing() {
    return new TreeDrawing(this, 0x7fffffff).getDrawing();
  }

  /**
   * Returns a drawing of this tree using the given Font.
   * @throws NullPointerException if <tt>fnt</tt> is <tt>null</tt>.
   */
  public JComponent getDrawing(Font fnt) throws NullPointerException {
    return new TreeDrawing(this, 0x7fffffff).getDrawing(fnt);
  }

  /**
   * Because this structure is immutable, this method simply returns this
   * <tt>PatriciaTrie</tt>.
   */
  public Object clone() {
    return this;
  }

  /**
   * Returns the root of this <tt>PatriciaTrie</tt>.  This method is intended 
   * for use by the <tt>TreeDrawing</tt> class.
   */
  public Object getRoot() {
    return root;
  }

  /**
   * Returns the children of this <tt>PatriciaTrie</tt>.  This method is 
   * intended for use by the <tt>TreeDrawing</tt> class.
   */
  public TreeInterface[] getChildren() {
    PatriciaTrie[] c = new PatriciaTrie[children.length];
    System.arraycopy(children, 0, c, 0, c.length);
    return c;
  }

  /**
   * Returns <tt>true</tt> if this <tt>PatriciaTrie</tt> is empty.
   * This method is intended for use by the <tt>TreeDrawing</tt> class.
   */
  public boolean isEmpty() {
    return size == 0;
  }
}
