/*
 * ConsList.java     11/13/99
 *
 * Copyright (c) 1998, 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;



/**
 * An immutable linear recursive structure with a head, which is an Object, and
 * a tail, which is a ConsList.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 */
public final class ConsList {

  /**
   * Is <tt>true</tt> if this <tt>ConsList</tt> is empty.
   */
  private boolean empty;

  /**
   * The head of this <tt>ConsList</tt>.
   */
  private Object head;

  /**
   * The tail of this <tt>ConsList</tt>.
   */
  private ConsList tail;

  /**
   * Constructs an empty ConsList.
   */
  public ConsList() {
    empty = true;
  }

  /**
   * Constructs a ConsList with the given head and tail.  If the given tail is
   * <tt>null</tt>, an empty ConsList is used in its place.
   * @param    h        the head of the list
   * @param    lst      the tail of the list
   */
  public ConsList(Object h, ConsList lst) {
    empty = false;
    head = h;
    tail = lst == null ? new ConsList() : lst;
  }

  /**
   * Returns <tt>true</tt> if this list is empty.
   */
  public boolean isEmpty() {
    return empty;
  }

  /**
   * Returns the head of this list.
   * @exception         EmptyListException
   *                    If this list is empty.
   */
  public Object getHead() throws EmptyListException {
    if (empty) {
      throw new EmptyListException();
    }
    return head;
  }

  /**
   * Returns the tail of the list.
   * @exception         EmptyListException
   *                    If this list is empty.
   */
  public ConsList getTail() throws EmptyListException {
    if (empty) {
      throw new EmptyListException();
    }
    return tail;
  }
}
