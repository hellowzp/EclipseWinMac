package binarysearch;

//see also Arrays.binarySearch()
public class BinarySearch {
	
	//see Core Java Volume 1 ch12 generic programming
	public static <T extends Comparable<? super T>> int binarySearch(T[] array, T x) {
//		return Arrays.binarySearch(array, x);
		int left = 0;
		int right = array.length - 1;
		int middle = -1;
		
		while(left<=right) {
			middle = (left+right)/2;
			int res = x.compareTo(array[middle]); 
			System.out.println(left + " " + middle + " " + right);
			
			if(res==0) {
				return middle;
			}else if(res==-1){
				right = middle - 1;
			}else{
				left = middle + 1;
			}
		}
		
		return -1;
	}
	
	public static <T extends Comparable<? super T>> 
			int recursiveBinarySearch( T[] array, T x) {
		return recursiveBinarySearch(array, x, 0, array.length-1);
	}
	
	private static <T extends Comparable<? super T>> int recursiveBinarySearch(
			T[] array, T x, int startIndex, int endIndex) {
		if(startIndex > endIndex) {
			return -1;
		}
		
		int middle = (startIndex + endIndex)/2;
		int res = x.compareTo(array[middle]);
		if(res==0) {
			return middle;
		}else if(res==-1) {
			endIndex = middle - 1;
			return recursiveBinarySearch(array, x, startIndex, endIndex);
		}else{
			startIndex = middle + 1;
			return recursiveBinarySearch(array, x, startIndex, endIndex);
		}
	}
	
	public static void main(String[] args) {
		Integer[] array = new Integer[] {1,2,3,4,5,6};
		System.out.println(binarySearch(array, 2));
		
		try {
			System.out.println(recursiveBinarySearch(array,3));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
