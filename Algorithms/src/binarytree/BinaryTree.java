package binarytree;

import java.util.ArrayList;
import java.util.List;

public class BinaryTree<T> {
	protected BinaryTreeNode<T> root;
	
	public BinaryTree() {
		this.root = null;
	}
	
	public BinaryTree(T element) {
		this.root = new BinaryTreeNode<T>(element);
	}
	
	public BinaryTree(BinaryTreeNode<T> root) {
		this.root = root;
	}

	public BinaryTreeNode<T> getRoot() {
		return root;
	}
	
	public void setRoot(BinaryTreeNode<T> root) {
		this.root = root;
	}
	
	public boolean isEmpty() {
		return root!=null;
	}

	/**
	 * post-order traversal
	 * the node is processed after both children are processed recursively
	 * @return
	 */
	public final int size() {
		return getNodeSize(root);
	}
	
	private int getNodeSize(BinaryTreeNode<T> node) {
		if(node==null)
			return 0;
		return 1 + getNodeSize(node.left) + getNodeSize(node.right);
	}
	
	/**
	 * @return the length of the path from this node to its deepest leaf
	 *  return -1 if empty tree
	 */
	public final int height() {
		return getNodeHeight(root);
	}
	
	private int getNodeHeight(BinaryTreeNode<T> node) {
		if(node==null)
			return -1;
		int leftHeight = getNodeHeight(node.left);
		int rightHeight = getNodeHeight(node.right);
		return 1 + Math.max(leftHeight, rightHeight); 
	}
	
	/**
	 * pre-order traversal
	 * the node is processed and then its children are processed recursively
	 * @return the new tree
	 */
	public BinaryTree<T> duplicate() {
		BinaryTreeNode<T> root = duplicateNode(this.root);
		return  new BinaryTree<>(root);
	}
	
	private BinaryTreeNode<T> duplicateNode(BinaryTreeNode<T> node) {
		if(node==null)
			return null;
		BinaryTreeNode<T> root = new BinaryTreeNode<>(node.element);
		if(node.left!=null)
			root.left = duplicateNode(node.left);
		if(node.right!=null)
			root.right = duplicateNode(node.right);		
		return root;
	}
		
	public BinaryTree<T> merge(BinaryTree<T> tree) {
		BinaryTree<T> mergedTree = null;
		return mergedTree;
	}
	
	// in-order traversal: left + self + right
	public void printInOrder() {
		printNodeInOrder(root);
	}
	
	private void printNodeInOrder(BinaryTreeNode<T> node) {
		if(node==null)
			return;
		printNodeInOrder(node.left);
		System.out.println(node.element);
		printNodeInOrder(node.right);
	}
	
	/**
	 * print all elements reversely: from bottom to up and left to right
	 * traverse using graph search agorithms
	 */
	public void printReverse() {
		int i = 0, size = size();
		ArrayList<BinaryTreeNode<T>> nodes = new ArrayList<>(size);
		nodes.add(root);
		while( i < nodes.size()) {
			BinaryTreeNode<T> node = nodes.get(i);
			if(node.right!=null)
				nodes.add(node.right);  // mind the order here
			if(node.left!=null)
			nodes.add(node.left);
			i++;
		}
		
		for(i=1; i<=size; i++)
			System.out.println(nodes.get(size-i).element);
	}

	public void clear() {
		clearNode(root);
	}
	
	private void clearNode(BinaryTreeNode<T> node) {
		if(node==null)
			return;
		clearNode(node.left);
		clearNode(node.right);
	}
	
	public static void main(String[] args) {
		List<BinaryTreeNode<Integer>> nodes = new ArrayList<>(10);
		for(int i=0; i<10; i++)
			nodes.add(new BinaryTreeNode<Integer>(i));
		BinaryTreeNode<Integer> root = nodes.get(0);
		root.setLeft(nodes.get(1));
		root.setRight(nodes.get(2));
		root.getLeft().setRight(nodes.get(3));
		root.getRight().setLeft(nodes.get(4));
		root.getRight().setRight(nodes.get(5));
		root.getRight().getRight().setRight(nodes.get(6));
		
		BinaryTree<Integer> tree = new BinaryTree<>(root);
		tree.printInOrder();
		tree.printReverse();
		System.out.println(""+tree.size());
		tree.clear();

	}
	
}
