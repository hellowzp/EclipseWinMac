/*
 * EmptyTreeException.java     11/13/99
 *
 * Copyright (c) 1998, 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;



/**
 * An exception thrown when an attempt is made to access the root or a child of
 * an emtpy BinaryTree.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 *
 * @see BinaryTree
 */
public class EmptyTreeException extends RuntimeException{};
