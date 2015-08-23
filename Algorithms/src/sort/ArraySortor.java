package sort;

import heap.BinaryHeap;

import java.util.Arrays;
import java.util.Comparator;

public class ArraySortor {
	
	private static final int CUTOFF = 10;
	
	//each time (outer loop) bubbles the maximum element up to the end
	public static <T extends Comparable<? super T>> int bubbleSort( T[] array) {
		int t = 0;
		for(int i=array.length-2; i>=0; i--) {
			for(int j=0; j<=i; j++ ){
				t++;
				if( array[j].compareTo(array[j+1]) == 1) {
					T temp = array[j];
					array[j] = array[j+1];
					array[j+1] = temp;
				}
			}
		}
		return t;
	}
	
	public static <T> int bubbleSort( T[] array, Comparator<? super T> cmp ) {
		int t = 0;
		for(int i=array.length-2; i>=0; i--) {
			for(int j=0; j<=i; j++ ){
				t++;
				if( cmp.compare(array[j], array[j+1]) == 1) {
					T temp = array[j];
					array[j] = array[j+1];
					array[j+1] = temp;
				}
			}
		}
		return t;
	}
	
	public static <T extends Comparable<? super T>> int insertSort( T[] array) {
		int t = 0;
		for(int i=1; i<array.length; i++) {
			T element = array[i];
			int j = i-1;
			while(j>=0 && element.compareTo(array[j])==-1) {
				array[j+1] = array[j]; //move the bigger element backward 
				array[j] = element;
				j--;
				t++;
			}
		}
		return t;
	}
	
	//low, high are both array index.
	private static <T extends Comparable<? super T>> void insertSort( T[] array, int low, int high) {
		for(int i=low+1; i<=high; i++) {
			T element = array[i];
			int j = i-1;
			while(j>=0 && element.compareTo(array[j])==-1) {
				array[j+1] = array[j]; //move the bigger element backward 
				array[j] = element;
				j--;
			}
		}
	}
	
	public static <T extends Comparable<? super T>> void heapSort( T[] a) {
		BinaryHeap.heapSort(a);
	}

	/**
	 * Mergesort algorithm.
	 * 
	 * @param a
	 *            an array of Comparable items.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Comparable<? super T>> void mergeSort( T[] a) {
//		T[] tmpArray = new T[a.length]; //can not initiate a generic type
		T[] tmpArray = (T[]) new Comparable[a.length];
		mergeSort(a, tmpArray, 0, a.length - 1);
	}

	/**
	 * Internal method that makes recursive calls.
	 * 
	 * @param a
	 *            an array of Comparable items.
	 * @param tmpArray
	 *            an array to place the merged result. * @param left the
	 *            left-most index of the subarray.
	 * @param right
	 *            the right-most index of the subarray.
	 */
	private static <T extends Comparable<? super T>> void mergeSort(
			T[] a, T[] tmpArray, int left, int right) {
		if (left < right) {
			int center = (left + right) / 2;
			mergeSort(a, tmpArray, left, center);
			mergeSort(a, tmpArray, center + 1, right);
			merge(a, tmpArray, left, center + 1, right);
		}
	}

	/**
	 * Internal method that merges two sorted halves of a subarray. * @param a
	 * an array of Comparable items.
	 * 
	 * @param tmpArray
	 *            an array to place the merged result.
	 * @param leftPos
	 *            the left-most index of the subarray.
	 * @param rightPos
	 *            the index of the start of the second half.
	 * @param rightEnd
	 *            the right-most index of the subarray.
	 */
	private static <T extends Comparable<? super T>> void merge(
			T[] a, T[] tmpArray, int leftPos, int rightPos, int rightEnd) {
		int leftEnd = rightPos - 1;
		int tmpPos = leftPos;
		int numElements = rightEnd - leftPos + 1;
		
		while (leftPos <= leftEnd && rightPos <= rightEnd)
			if (a[leftPos].compareTo(a[rightPos]) <= 0)
				tmpArray[tmpPos++] = a[leftPos++];
			else
				tmpArray[tmpPos++] = a[rightPos++];
		
		while (leftPos <= leftEnd)   // Copy rest of first half 
			tmpArray[ tmpPos++ ] = a[ leftPos++ ];
		while (rightPos <= rightEnd) // Copy rest of right half 
			tmpArray[ tmpPos++ ] = a[ rightPos++ ];
		
		// Copy tmpArray back
		for (int i = 0; i < numElements; i++, rightEnd--)
			a[rightEnd] = tmpArray[rightEnd];
	}
	
	/**
	 * Quicksort algorithm (driver)
	 */
	public static <T extends Comparable<? super T>> void quickSort(T[] a) {
		quickSort(a, 0, a.length - 1);
	}

	/**
	 * Internal quicksort method that makes recursive calls. Uses
	 * median-of-three partitioning and a cutoff.
	 * insertSort is used when unsorted elements are less than the cutoff
	 * because it is faster for small array: hybrid algorithm optimization
	 */
	private static <T extends Comparable<? super T>> void quickSort(
			T[] a, int low, int high) {
		if (low + CUTOFF > high)    
			insertSort(a, low, high);
		else { // Sort low, middle, high
			int middle = (low + high) / 2;
			if (a[middle].compareTo(a[low]) < 0)
				swap(a, low, middle);
			if (a[high].compareTo(a[low]) < 0)
				swap(a, low, high);
			if (a[high].compareTo(a[middle]) < 0)
				swap(a, middle, high);

			// Place pivot in the end
			swap(a, middle, high);
			T pivot = a[high];

			// Begin partitioning
			int i = low, j = high - 1;
			for(;;) {
				while (a[++i].compareTo(pivot) < 0);
				while (pivot.compareTo(a[--j]) < 0);
				if (i >= j) break;
				swap(a, i, j);
			}
			// Restore pivot
			swap(a, i, high);

			quickSort(a, low, i - 1);  // Sort small elements
			quickSort(a, i + 1, high); // Sort large elements
		}
	}

	private static final <T extends Comparable<? super T>> void swap( T[] a, int i, int j) {
		T t = a[i];
		a[i] = a[j];
		a[j] = t;
	}
	
	/**
     * Quick selection algorithm.
     * Places the kth smallest item in a[k-1].
     * @param a an array of Comparable items.
     * @param k the desired rank (1 is minimum) in the entire array.
     */ 
	public static <T extends Comparable<? super T>> T quickSelect(T[] a, int k) {
		quickSelect(a, 0, a.length - 1, k);
		return a[k-1];
	}
	
	/**
	 * Internal selection method that makes recursive calls.
	 * Uses median-of-three partitioning and a cutoff.
	 * Places the kth smallest item in a[k-1].
	 * @param a an array of Comparable items.
	 * @param low the left-most index of the subarray.
	 * @param high the right-most index of the subarray.
	 * @param k the desired rank (1 is minimum) in the entire array.
	 */
	private static <T extends Comparable<? super T>> void quickSelect(T[] a,
			int low, int high, int k) {
		if (low + CUTOFF > high)
			insertSort(a, low, high);
		else {
			// Sort low, middle, high
			int middle = (low + high) / 2;
			if (a[middle].compareTo(a[low]) < 0)
				swap(a, low, middle);
			if (a[high].compareTo(a[low]) < 0)
				swap(a, low, high);
			if (a[high].compareTo(a[middle]) < 0)
				swap(a, middle, high);

			// Place pivot in the end
			swap(a, middle, high);
			T pivot = a[high];

			// Begin partitioning
			int i = low, j = high - 1;
			for(;;) {
				while (a[++i].compareTo(pivot) < 0) ;
				while (pivot.compareTo(a[--j]) < 0) ;
				if (i >= j) break;
				swap(a, i, j);
			}
			// Restore pivot
			swap(a, i, high);

			// Recurse; only this part changes
			if (k <= i)
				quickSelect(a, low, i - 1, k);
			else if (k > i + 1)
				quickSelect(a, i + 1, high, k);
		}
	}
	
	public static void main(String[] args) {
		Integer[] array = new Integer[] {7,1,2,8,3,4,5,6,8,34,43,12,334,5,10};
//		bubbleSort(array.clone(), new Comparator<Integer>() {
//			@Override
//			public int compare(Integer o1, Integer o2) {
//				return o1.compareTo(o2);
//			}		
//		});
		System.out.println("N = " + array.length + " : " + Arrays.toString(array));
		
		System.out.println( bubbleSort(array.clone()) + " " + insertSort(array.clone()));
		
		System.out.println(quickSelect(array,9));
		System.out.println("N = " + array.length + " : " + Arrays.toString(array));
	}
}
