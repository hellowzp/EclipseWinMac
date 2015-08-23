package bst;

public class BinarySearchTree <T extends Comparable<? super T>> {
	private BinaryTreeNode<T> root;
	private int size;
	
	public int getSize() {
		return size;
	}

	public T getRooElement() {
		return root == null ? null : root.element;
	}

}
