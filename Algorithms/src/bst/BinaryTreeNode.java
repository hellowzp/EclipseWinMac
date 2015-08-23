package bst;

class BinaryTreeNode<T> {
	
	T element;			     
	BinaryTreeNode<T> left;  
	BinaryTreeNode<T> right; 
	
	BinaryTreeNode(T theElement) {
		element = theElement;
		left = right = null;
	}
}
