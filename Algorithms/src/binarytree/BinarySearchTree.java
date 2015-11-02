package binarytree;

public class BinarySearchTree <T extends Comparable<? super T>>
				extends BinaryTree<T>{

	private int size;
	private int type;
	public static final int MAX = 1;
	public static final int MIN = 1;
		
	public BinarySearchTree() {
		this(null,MIN);
	}
	
	public BinarySearchTree(T element) {
		this(element,MIN);
	}
	
	public BinarySearchTree(T element, int type) {
		super(element);
		this.type = type;
	}

	public T getRooElement() {
		return root == null ? null : root.element;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public void add(T element) {
		size++;
	}
	
	public void makeBalance() {
		
	}

}
