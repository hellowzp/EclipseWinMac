/*
 * Stack.java   11/15/99
 *
 * Copyright (c) 1998, 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;

import java.util.EmptyStackException;



/**
 * A stack built from an immutable {@link ConsList ConsList}.  Using an 
 * immutable structure
 * for the underlying storage allows a shallow clone to be sufficient.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 */
public final class Stack implements Cloneable {

  /**
   * The structure used for the stack.
   */
  private ConsList theStack;

  
  /**
   * Constructs an empty Stack.
   */
  public Stack() {
    theStack = new ConsList();
  }

  /**
   * Pushes the given Object onto the top of the stack.
   */
  public void push(Object obj) {
    theStack = new ConsList(obj, theStack);
  }

  /**
   * Removes the Object from the top of the stack and returns it.
   * @throws  EmptyStackException  If the stack is empty.
   */
  public Object pop() throws EmptyStackException {
    if (theStack.isEmpty()) {
      throw new EmptyStackException();
    }
    Object top = theStack.getHead();
    theStack = theStack.getTail();
    return top;
  }

  /**
   * Returns true if the stack is empty.
   */
  public boolean empty() {
    return theStack.isEmpty();
  }

  /**
   * Returns a clone of this stack.
   * Because the underlying structure is immutable, a shallow clone is
   * performed.
   */
  public Object clone() {
    try {
      return super.clone();
    }
    catch (CloneNotSupportedException e) { // This shouldn't happen.
      e.printStackTrace();
      return new Stack();
    }
  }
}
