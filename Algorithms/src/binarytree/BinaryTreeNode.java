package binarytree;

public class BinaryTreeNode<T> {
	protected T element;
	protected BinaryTreeNode<T> left, right;
	
	public BinaryTreeNode(T element) {
		this.element = element;
	}

	public final T getElement() {
		return element;
	}

	public final void setElement(T element) {
		this.element = element;
	}

	public final BinaryTreeNode<T> getLeft() {
		return left;
	}

	public final void setLeft(BinaryTreeNode<T> left) {
		this.left = left;
	}

	public final BinaryTreeNode<T> getRight() {
		return right;
	}

	public final void setRight(BinaryTreeNode<T> right) {
		this.right = right;
	}
	
}
