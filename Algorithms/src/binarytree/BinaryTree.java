package binarytree;

import java.util.ArrayList;
import java.util.List;

public class BinaryTree<T> {
	private BinaryTreeNode<T> root;
	
	public BinaryTree(BinaryTreeNode<T> root) {
		this.root = root;
	}

	public BinaryTreeNode<T> getRoot() {
		return root;
	}
	
	public void setRoot(BinaryTreeNode<T> root) {
		this.root = root;
	}

	public int size() {
		return root!=null ? root.size() : 0;
	}
	
	public boolean isEmpty() {
		return root!=null;
	}
	
	public BinaryTree<T> merge(BinaryTree<T> tree) {
		BinaryTree<T> mergedTree = null;
		return mergedTree;
	}
	
	public void clear() {
		if(root!=null)
			root.clear();
		root = null;
	}
	
	
	
	public static void main(String[] args) {
		List<BinaryTreeNode<Integer>> nodes = new ArrayList<>(10);
		for(int i=0; i<10; i++)
			nodes.add(new BinaryTreeNode<Integer>(i));
		BinaryTreeNode<Integer> root = nodes.get(0);
		root.setLeft(nodes.get(1));
		root.setRight(nodes.get(2));
		
		root.printInOrder();
		System.out.println(root.height());
		System.out.println(root.size());
		
		BinaryTree<Integer> tree = new BinaryTree<>(root);
		tree.clear();
		System.out.println(""+tree.size()+root.size()+nodes.get(0).size());
	}
	
}
