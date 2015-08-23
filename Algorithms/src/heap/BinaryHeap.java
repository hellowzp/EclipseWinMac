package heap;

import java.util.Arrays;
import java.util.Collection;

/**
 * Dynamic Max BinaryHeap
 * 
 * @author Benchun
 *
 * @param <T>
 */
public class BinaryHeap <T extends Comparable<? super T>> implements IHeap<T> {
	private int size;
	private T[] elementData;
	
	public BinaryHeap() {
		this(15);
	}
	
	@SuppressWarnings("unchecked")
	public BinaryHeap(int initialCapacity){		
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		//java.lang.Object cannot be cast to java.lang.Comparable
		//elementData = (T[]) new Object[initialCapacity]; 
		elementData = (T[]) new Comparable[initialCapacity];  
	}
	
	/**
	 * leave the parameter check to System.arraycopy()
	 * which will throw NullPointException if src or des is null
	 * @param array
	 */
	@SuppressWarnings("unchecked")
	public BinaryHeap(T[] array) {
		elementData = (T[]) new Comparable[array.length]; //must be init first
		System.arraycopy(array, 0, elementData, 0, array.length);
		size = array.length;
		makeHeap();
	}
	
	@SuppressWarnings("unchecked")
	public BinaryHeap(Collection<T> col) {
		this( (T[])col.toArray(), 888);
	}
	
	/**
	 * avoid allocating memory for the array
	 * called by sort() for in-place heap-sort
	 * and build heap from collection
	 */
	private BinaryHeap(T[] array, int dummy) {
		elementData = array;
		size = array.length;
		makeHeap();
	}

	public boolean isEmpty() {
		return size==0;
	}
	
	public int size() {
		return size;
	}
	
	public void insert(T element) {
		ensureCapacity(size+1);
		elementData[size++] = element;
		bubbleUp(size-1);
	}
	
	public T peak() {
		return size>0 ? elementData[0] : null;
	}
	
	/**
	 * peak and return the top element
	 * heap is maintained after the opertion
	 * time complexity: logN
	 * @return
	 */
	public T extract() {
		if(size==0)
			return null;
		T top = elementData[0];
		
		size--;
		elementData[0] = elementData[size];
		elementData[size] = null;
		
		siftDown(0);
		return top;
	}
	
	@SuppressWarnings("unchecked")
	public T[] toArray() {
		T[] array = (T[]) new Comparable[elementData.length];
		System.arraycopy(elementData, 0, array, 0, elementData.length);
		return array;
	}
	
	/**
	 * find the top minimum elements since maxheap is used  
	 * You can't use a class's generic type parameters in static methods or static fields.
	 * The class's type parameters are only in scope for instance methods and instance fields.
	 * For static fields and static methods, they are shared among all instances of the class,
	 * even instances of different type parameters, so obviously 
	 * they cannot depend on a particular type parameter.
	 * also mind that key word static should be preceeding the generic type in the signature
	 * see http://stackoverflow.com/questions/936377/static-method-in-a-generic-class.
	 * 
	 * time complexity: N*log(amount), almost linear if amount << N
	 */
	public static <E extends Comparable<? super E>> E[] findTops(E[] array, int amount) {
		if(amount<1 || amount>array.length)
			throw new IllegalArgumentException("Illegal amount: " + amount);		
		
		BinaryHeap<E> heap = new BinaryHeap<>(amount);
		for(int i=0; i<amount; i++)
			heap.insert(array[i]);
		for(int i=amount; i<array.length; i++) {
			if(array[i].compareTo(heap.peak())==-1) {
				heap.elementData[0] = array[i];
				heap.siftDown(0);
			}
		}
		heap.sortHeap(); //time complexity for this is small cauze N is small now
		return heap.elementData;
	}
	
	public static <E extends Comparable<? super E>> void heapSort(E[] array) {
		new BinaryHeap<>(array, array.length).sortHeap();
	}
	
	/**
	 * refer to the implementation in ArrayList
	 */
	public void distroy() {
        for (int i = 0; i < size; i++)
            elementData[i] = null;
        size = 0;
    }
	
	/**
	 * get the parent index of the element, return -1 if not exists
	 * @param i: the index of the internal node element
	 * @return the parent index
	 */
	private final int left(int i) {
		//Mind that index starts from 0
		int index = 2*(i+1)-1;
		return index<size ? index : -1;
	}
	
	private final int right(int i) {
		int index = 2*(i+1);
		return index<size ? index : -1;
	}
	
	private final int parent(int i) {
		return i>0? (i+1)/2-1 : -1;
	}
	
	@SuppressWarnings("unchecked")
	private void ensureCapacity(int requiredCapacity) {
		if(requiredCapacity<=elementData.length) 
			return;
		T[] oldData = elementData;
		int newCapacity = Math.max(requiredCapacity, 2 * elementData.length + 1);
		elementData = (T[]) new Object[newCapacity];
		System.arraycopy(oldData, 0, elementData, 0, oldData.length);
	}

	private final void swap(int i, int j) {
		T t = elementData[i];
		elementData[i] = elementData[j];
		elementData[j] = t;
	}
	
	/**
	 * buddle up the element at the given index 
	 * @param index: element index
	 * logN time complexity
	 */
	private void bubbleUp(int index) {
		int parentIndex = parent(index);
		while( parentIndex >= 0 && 
			   elementData[parentIndex].compareTo(elementData[index]) == -1 ){
			swap(index,parentIndex);
			index = parentIndex;
			parentIndex = parent(index);
		}
	}

	/**
	 * sift down the element at the given index
	 * @param index: element index
	 */
	private void siftDown(int index) {
		int max = maxElementIndex( index, left(index), right(index));
		while( max != index ){
			swap(index,max);
			index = max;
			max = maxElementIndex( index, left(index), right(index));
		}
	}
	
	/**
	 * get the index at which the element has the maximum value
	 * @param index: current index
	 * @param left: left child index
	 * @param right: right child index
	 * @return the index at which the element has the maximum value
	 */
	private int maxElementIndex(int index, int left, int right) {
		int max = index;
		if(left>0 && elementData[left].compareTo(elementData[max])==1) 
			max = left;
		if(right>0 && elementData[right].compareTo(elementData[max])==1) 
			max = right;
		return max;
	}
	
	/**
	 * bottom-up algorithm to heap-ify the internal elementData array
	 * called when the internal elementData has just been initialized
	 * linear time complexity
	 */
	private void makeHeap() {
		if(size<2) return;
		for(int i=size/2; i>=0; i--){
			siftDown(i);
		}
	}
	
	/**
	 * sort the internal elementData array in ascending order
	 * time complexity: N*logN
	 */
	private void sortHeap() {
		int realSize = size;
		for(int i=1; i<realSize; i++) {
			T top = extract();
			size++;  // extract() reduce size, increase it back
			elementData[realSize-i] = top;
			size--;  // make the extract() work again
		}
		size = realSize;
	}
	
	public static void main(String[] args) {
		BinaryHeap<Integer> heap = new BinaryHeap<>();
		for(int i=1;i<=8;i++) {
			heap.insert(i);
		}
		System.out.println(Arrays.toString(heap.toArray()));
		heap.extract();
		System.out.println(Arrays.toString(heap.toArray()));
		
		//must be declared as Integer instead of int
		Integer[] array = new Integer[]{5,4,2,6,7,3,8,1,9};
		
		heap = new BinaryHeap<>(array);
		System.out.println(Arrays.toString(heap.toArray()));

		System.out.println(Arrays.toString( BinaryHeap.findTops(array, 5)));
		
		BinaryHeap.heapSort(array);
		System.out.println(Arrays.toString(array));
	}
}
