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
	
	//post-order traversal:  the node is processed after both children are processed recursively
	public final int size() {
//		if(element==null && left == null && right == null) 
//			return 0; //dummy node
		return 1 + ( left!=null? left.size() : 0) + ( right!=null? right.size() : 0);
	}
	
	//the length of the path from this node to its deepest leaf
	public final int height() {
		int leftHeight = left!=null? left.height() : -1;
		int rightHeight = right!=null? right.height() : -1;
		return 1 + Math.max(leftHeight, rightHeight); 
	}
	
	//pre-order traversal: the node is processed and then its children are processed recursively
	//return a new tree rooted at this node ==> deep clone
	public BinaryTreeNode<T> duplicate() {
		BinaryTreeNode<T> root = new BinaryTreeNode<>(element);
		if(left!=null) {
			root.left = left.duplicate();
		}		
		if(right!=null) {
			root.right = right.duplicate();
		}			
		return root;
	}
	
	//in-order traversal: left + self + right
	public void printInOrder() {
		if (left != null) // Left
			left.printInOrder();
		System.out.println(element); // Node
		if (right != null)
			right.printInOrder(); // Right
	}

	public void clear() {
		if(left!=null) left.clear();
		if(right!=null) right.clear();
		element = null;
//		parent = null;
		left = null;
		right = null;
	}
	
}
