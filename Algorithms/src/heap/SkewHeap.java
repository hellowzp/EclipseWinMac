package heap;

import java.util.Stack;

/**
 * A self-adjusting heap that is implemented as a binary tree
 * It is advantageous over binary heap because of the ability to merge more quickly
 * no structural constraints, so no guarantee that theheight is logarithmics 
 * 
 * a self-adjusting form of a leftist heap which attempts to maintain balance
 * by unconditionally swapping all nodes in the merge path when merging two heaps.
 * 
 * merge operation: amortized logarithmics, worst linear 
 * 
 * http://blog.csdn.net/ljsspace/article/details/6716818
 * https://en.wikipedia.org/wiki/Skew_heap
 * https://www.cs.cmu.edu/~sleator/papers/adjusting-heaps.pdf
 * http://www.cse.yorku.ca/~andy/courses/4101/lecture-notes/LN5.pdf
 * 
 * @author Benchun
 *
 * @param <T> element type, must implements Comparable 
 */
public class SkewHeap<T extends Comparable<? super T>> implements IHeap<T> {
	private BinaryTreeNode<T> root;
	private int size;
	
	public SkewHeap(T rootElement) {
		root = new BinaryTreeNode<T>(rootElement);
		size = 1;
	}
	
	@Override
	public T peak() {
		return root == null ? null : root.element;
	}

	@Override
	public T extract() {
		if(root==null)
			throw new RuntimeException("empty heap exception");
		T rootElement = root.element;
		root = iterativeMerge(root.left, root.right);
		size--;
		return rootElement;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size<=0;
	}
	
	public int height() {
		return root==null ? -1 : root.height();
	}

	@Override
	public void insert(T element) {
		if(element==null)
			throw new IllegalArgumentException("element cannot be null");
		root = recursiveMerge(root, new BinaryTreeNode<T>(element));
		size++;
	}
	
	/**
	 * merge two heaps ==> minHeap
	 * mind that after merge the two roots of the original two heap
	 * should both point to the new root
	 * 
	 * @param heap: can be null
	 * @return the merged new heap
	 */
	public SkewHeap<T> merge(SkewHeap<T> heap) {
		if(heap==null)
			return this;
		BinaryTreeNode<T> newRoot = iterativeMerge(this.root, heap.root);	
		this.root = heap.root = newRoot;
		this.size = heap.size = (this.size + heap.size);
		return this;
	}
		
	private BinaryTreeNode<T> recursiveMerge(BinaryTreeNode<T> left, BinaryTreeNode<T> right) {
		if(left==null)
			return right;
		if(right==null)
			return left;
		
		//during merge, new left and new right are swapped to avoid unbalanced tree
		if( left.element.compareTo(right.element) == -1 ){
			BinaryTreeNode<T> temp = left.left;
			left.left = recursiveMerge(left.right, right);
			left.right = temp;
			return left;
		} else {
			BinaryTreeNode<T> temp = right.left;
			right.left = recursiveMerge(right.right, left);
			right.right = temp;
			return right;
		}
	}
	
	public BinaryTreeNode<T> nonrecursiveMerge(BinaryTreeNode<T> left, BinaryTreeNode<T> right) {
		return this.root;
	}

	/**
	 * traversing the right paths of the two heap, merging them into a single right path in increasing order, 
	 * the left subtrees along the nodes in the right paths remain unchanged
	 * 
	 * 1. Split each heap into subtrees by cutting every rightmost path. 
	 * From the root node, sever the right node and make the right child its own subtree.
	 * This will result in a set of trees in which the root either 
	 * only has a left child or no children at all.
	 * 
	 * 2. Sort the subtrees in ascending order based on the value of the root node of each subtree.
	 * 
	 * 3. While there are still multiple subtrees, iteratively recombine the last two (from right to left).
	 * If the root of the second-to-last subtree has a left child, swap it to be the right child.
	 * Link the root of the last subtree as the left child of the second-to-last subtree.
	 */
	private BinaryTreeNode<T> iterativeMerge(BinaryTreeNode<T> left, BinaryTreeNode<T> right){  
        if(left == null) return right;  
        if(right == null) return left;  
          
        Stack<BinaryTreeNode<T>> stack = new Stack<>();  
        BinaryTreeNode<T> r1 = left;  
        BinaryTreeNode<T> r2 = right;  
        while(r1 != null && r2 != null) {  
            if(r1.element.compareTo(r2.element)==-1) {  
                stack.push(r1);  
                r1 = r1.right;  
            }else{  
                stack.push(r2);  
                r2 = r2.right;  
            }             
        }  
        
        //At this point, exactly one of r1 and r2 is null and it points to the max subtree
        //Again we need to swap the left and right children of r  
        BinaryTreeNode<T> r = (r1 != null) ? r1 : r2;       
        while(!stack.isEmpty()) {  
            BinaryTreeNode<T> node = stack.pop();              
            node.right = node.left;  
            node.left = r;  
            r = node;  
        }        
        return r;  
    }  

	@Override
	public void distroy() {
		root = null;
		size = 0;
	}
	
	public void print() {
		System.out.print("Heap dump: ");
		printNode(root);
		System.out.println();
	}
	
	private void printNode(BinaryTreeNode<T> node) {
		if(node==null)
			return;
		printNode(node.left);
		System.out.print(node.element + " ");
		printNode(node.right);
	}
	
	private static class BinaryTreeNode<T> {		
		T element;			     
		BinaryTreeNode<T> left;  
		BinaryTreeNode<T> right; 
		
		BinaryTreeNode(T theElement) {
			element = theElement;
			left = right = null;
		}
		
		int height() {
			int leftHeight = left!=null ? left.height() : -1;
			int rightHeight = right!=null ? right.height() : -1;
			return 1 + Math.max(leftHeight, rightHeight); 
		}
	}
	
	public static void main(String[] args) {
		SkewHeap<Integer> heap = new SkewHeap<Integer>(10);
		heap.insert(3);
		heap.insert(5);
		heap.print();
		heap.insert(4);
		heap.print();
		heap.insert(8);
		heap.print();
	}
}
