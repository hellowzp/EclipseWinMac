/*
 * AA Tree	12/03/99
 *
 * Copyright (c) 1999, All Rights Reserved
 *
 */

package edu.ksu.cis.viewer;

import java.awt.*;
import javax.swing.*;



/**
 * An immutable AA tree that can draw itself.  An AA tree is a binary
 * search tree such that if it is nonempty, the level of a node is 1 if the
 * node is a leaf, the node is red if it is the same level as its parent, and
 * the node is black if it is one level less than the level of the parent.
 *
 * @author 	Stella Houston and Rod Howell
 *              (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 */
public final class AATree implements BSTInterface {
  
  /** 
   * The tree.  The <tt>tag</tt> fields of the nodes will be used to
   * store the levels of the subtrees.
   * @see Node
   */
  private BinaryTree theTree;

  /**
   * Constructs an empty AA Tree.  
   */
  public AATree() {
    theTree = new BinaryTree();}

  /**
   * Constructs an AATree object from the given BinaryTree.  <tt>t</tt>
   * is assumed to be arranged into a valid AA tree with the level of
   * each subtree stored in the Node at its root.
   */
  private AATree(BinaryTree t) {
    theTree = t;}
 
  /** 
   * Returns the <tt>AATree</tt> resulting from the insertion
   * <tt>key</tt> into this <tt>AATree</tt>.  If <tt>key</tt>
   * is already in the tree, an identical tree is returned.
   * @exception        NullPointerException
   *		       If <tt>key</tt) is <tt>null</tt>
   */
  
  public BSTInterface put(String key) throws NullPointerException {
    if (key == null) {
      throw new NullPointerException();}
    
    return new AATree(put(key, theTree));}

  /**
   * Returns the AA Tree that results from the insertion of <tt>s</tt>
   * into <tt>t</tt>, which is assumed to be a valid AA Tree.  If <tt>t</tt>
   * contains <tt>s</tt>, an identical tree is returned.
   */
  private static BinaryTree put(String s, BinaryTree t) {
    if (t.isEmpty()) {
      return new BinaryTree(new Node(s,1), null, null);}
     
    else {
      Node root = t.getRoot();
      BinaryTree left = t.getLeftChild();
      BinaryTree right = t.getRightChild();
      int result = s.compareTo(root.getContents());
      if (result == 0) {
        return t;}
      else if (result < 0) {
        left = put(s, t.getLeftChild());}
      else {
        right = put(s, t.getRightChild());}
      t = skew(root,left,right);
      t = split(t.getRoot(),t.getLeftChild(),t.getRightChild());
      return getBinaryTree(t.getRoot(),t.getLeftChild(),t.getRightChild());}}

  /**
   * Returns the <tt>BinaryTree</tt> resulting from performing a single 
   * rotate left on the binary tree whose root is given by <tt>root</tt>,
   * whose left-hand child is given by <tt>left</tt>, and whose right-hand
   * child is given by <tt>right</tt>.  The <tt>tag</tt> fields of all
   * nodes are guaranteed to record the level of their subtrees provided
   * this was initially the case in both <tt>left</tt> and <tt>right</tt>.
   */
  private static BinaryTree rotateLeft(Node root,BinaryTree left,BinaryTree right) {
    
    BinaryTree newLeft = getBinaryTree(root,left,right.getLeftChild());
    
    return getBinaryTree(right.getRoot(),newLeft,right.getRightChild());}

  /**
   * Returns the <tt>BinaryTree</tt> resulting from performing a single 
   * rotate right on the binary tree whose root is given by <tt>root</tt>,
   * whose left-hand child is given by <tt>left</tt>, and whose right-hand
   * child is given by <tt>right</tt>.  The <tt>tag</tt> fields of all
   * nodes are guaranteed to record the level of their subtrees provided
   * this was initially the case in both <tt>left</tt> and <tt>right</tt>.
   */
  private static BinaryTree rotateRight(Node root,BinaryTree left,BinaryTree right) {
    
    BinaryTree newRight = getBinaryTree(root,left.getRightChild(),right);
    
    return getBinaryTree(left.getRoot(),left.getLeftChild(),newRight);}

  /**
   * Returns the level <tt>t</tt>.  <tt>t</tt> is assumed to have its level
   * recorded in the <tt>tag</tt> field of its root if it is non-empty.
   */
  private static int level(BinaryTree t) {
    if (t.isEmpty()) {
      return 0;}
    else {
      return (t.getRoot()).getTag();}}

  /**
   * Returns the <tt>BinaryTree</tt> resulting from the removal of the horizontal
   * left link on the tree whose root is given by <tt>root</tt>, whose left-hand 
   * child is given by <tt>left</tt>, and whose right-hand child is given 
   * by <tt>right</tt>.  The <tt>tag</tt> fields of all nodes are guaranteed to 
   * record the level of their subtrees provided this was initially the case in 
   * both <tt>left</tt> and <tt>right</tt>.
   */
  private static BinaryTree skew(Node root,BinaryTree left,BinaryTree right) {
    if (!left.isEmpty() && level(left) == root.getTag()) {
      BinaryTree t = rotateRight(root,left,right);
      return getBinaryTree(t.getRoot(),t.getLeftChild(),t.getRightChild());}

    else { 
      return getBinaryTree(root,left,right);}}

  /**
   * Returns the <tt>BinaryTree</tt> resulting from the removal of consecutive horizontal
   * right links on the tree whose root is given by <tt>root</tt>, whose left-hand child is 
   * given by <tt>left</tt>, and whose right-hand child is given by <tt>right</tt>.  The 
   * <tt>tag</tt> fields of all nodes are guaranteed to record the level of their subtrees 
   * provided this was initially the case in both <tt>left</tt> and <tt>right</tt>.
   */  
  private static BinaryTree split(Node root,BinaryTree left,BinaryTree right) {
    if (!right.isEmpty() && level(right.getRightChild()) == root.getTag()) {
      BinaryTree t = rotateLeft(root,left,right);
      return getBinaryTree(new Node(t.getRoot().getContents(), level(t)+1),
                             t.getLeftChild(),t.getRightChild());}
    
    else {
      return getBinaryTree(root,left,right);}}

  /**
   * Returns the <tt>AATree</tt> resulting from the removal of <tt>key</tt>
   * from this <tt>AATree</tt>.  If <tt>key</tt> is not in the tree, an
   * identical tree is returned.
   * @exception         NullPointerException
   *                    If key is <tt>null</tt>
   */
  public BSTInterface remove(String key) throws NullPointerException{
    if (key == null) {
      throw new NullPointerException();}
     
    return new AATree(remove(key, theTree));}

  /**
   * Returns the AA Tree resulting from the removal of <tt>s</tt> from 
   * <tt>t</tt>, which is assumed to be a valid AA Tree.  If <tt>t</tt>
   * does not contain <tt>s</tt>, an identical tree is returned.
   */
  private static BinaryTree remove(String s, BinaryTree t) {
    if (t.isEmpty()) { 
      return t;}
     
    else {
      Node root = t.getRoot();
      BinaryTree left = t.getLeftChild();
      BinaryTree right = t.getRightChild();
      int result = s.compareTo(root.getContents());
      if (result == 0) {
        if (!left.isEmpty()) {
          BinaryTree newTree = minToRoot(right);
	  root = new Node(newTree.getRoot().getContents(),level(t));
          right = newTree.getRightChild();
          return balance(root,left,right);} 

        else{              
	  return right;}}

      else if (result < 0) {
        left = remove(s, left);
        return balance(root,left,right);}
        
      else {
        right = remove(s, right);
        return balance(root,left,right);}}}
        
  /**
   * Returns an AA Tree containing the nodes of all three arguments.
   */
  private static BinaryTree balance(Node root,BinaryTree left,BinaryTree right) {
    
    int leftLevel = level(left);
    int rightLevel = level(right);
    int treeLevel = (root.getTag());

    if (leftLevel < treeLevel-1 || rightLevel < treeLevel-1) {
      root = new Node(root.getContents(),--treeLevel);

      if (rightLevel > treeLevel) {
        right = getBinaryTree(new Node(right.getRoot().getContents(),treeLevel),
                               right.getLeftChild(),right.getRightChild());}
             
      BinaryTree t = skew(root,left,right);
      right = t.getRightChild();
      
      if (!right.isEmpty()) {
        BinaryTree tree = skew(right.getRoot(),right.getLeftChild(),right.getRightChild());
        root = t.getRoot();
        left = t.getLeftChild();
        
        if (!tree.getRightChild().isEmpty()) {
          BinaryTree treeRight = skew(tree.getRightChild().getRoot(),
                                      tree.getRightChild().getLeftChild(),
                                      tree.getRightChild().getRightChild());
          right = getBinaryTree(tree.getRoot(),tree.getLeftChild(),treeRight);}
          
        else {
          right = tree;}}
                     
      t = split(root,left,right);
      root = t.getRoot();
      left = t.getLeftChild();
      right = t.getRightChild();
      
      if (!right.isEmpty()) {
        right = split(right.getRoot(),right.getLeftChild(),right.getRightChild());}
      
      t = getBinaryTree(root,left,right);
      return t;}
        
      else {
        return getBinaryTree(root,left,right);}}
            
  /**
   * Returns a binary search tree containing the same nodes as <tt>t</tt>, but
   * whose left child is empty and whose right child is an AA tree.  The 
   * <tt>tag</tt> field of the root of the returned tree is meaningless.  
   * <tt>t</tt> is assumed to be a valid AA Tree.
   */
  private static BinaryTree minToRoot(BinaryTree t) {
    if (t.getLeftChild().isEmpty()) {
      return t;}

    else {
      BinaryTree temp = minToRoot(t.getLeftChild());
      return new BinaryTree(temp.getRoot(), null,
                         balance(t.getRoot(),temp.getRightChild(),
                                 t.getRightChild()));}}
 
  /**
   * Returns an <tt>AA Tree</tt> containing the nodes of all three arguments, where
   * the <tt>right</tt> child will be red if it is at the same level as the root on the
   * binary tree whose root is given by <tt>root</tt>, whose left-hand child is given 
   * by <tt>left</tt>, and whose right-hand child is given by <tt>right</tt>. 
   */
  private static BinaryTree getBinaryTree(Node root,BinaryTree left,   
                                          BinaryTree right) {
    if ((!left.isEmpty())&&(left != null)) {
      left = new BinaryTree(new Node(left.getRoot().getContents(),left.getRoot().getTag()),
                            left.getLeftChild(),left.getRightChild());}
    
    if ((!right.isEmpty())&&(right != null)) {
      if (level(right) == root.getTag()) {
        right = new BinaryTree(new Node(right.getRoot().getContents(),
                                        right.getRoot().getTag(),Color.red),
                               right.getLeftChild(),right.getRightChild());}
      else {
        right = new BinaryTree(new Node(right.getRoot().getContents(),level(right)),
                               right.getLeftChild(),right.getRightChild());}}
    return new BinaryTree(new Node(root.getContents(),root.getTag()),left,right);}
             
  /**
   * Returns a drawing of the tree.
   */
  public JComponent getDrawing(){
    return theTree.getDrawing();}

  /**
   * Returns a drawing of the tree using the given Font.
   * @throws NullPointerException if <tt>fnt</tt> is <tt>null</tt>.
   */
  public JComponent getDrawing(Font fnt){
    return theTree.getDrawing(fnt);}
  
  /**
   * Returns a clone of this tree.
   * @return            a clone of this tree
   */
  public Object clone() {
    return this;}
}
 
