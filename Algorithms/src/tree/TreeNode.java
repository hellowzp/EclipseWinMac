package tree;

import java.util.LinkedList;
import java.util.List;

class TreeNode<T> {
	T element;
	List<TreeNode<T>> children;
	
	TreeNode(T element) {
		this.element = element;
		this.children = new LinkedList<TreeNode<T>>();
	}
		
	void addChild(T data) {
		children.add( new TreeNode<T>(data));
	}
}
