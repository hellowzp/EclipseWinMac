package jdk;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class HashSetTest {

	public static void main(String[] args) {
		Integer[] intArray = new Integer[] {1,2,3,4,5,2};
		
		Set<Integer> intSet = new HashSet<>(Arrays.asList(intArray));
//		System.out.println(intSet.size());
		
		intSet.addAll(Arrays.asList(intArray));
//		System.out.println(intSet.size());
		
		solveEquation(10000);
	}
	
	private static void solveEquation(int size) {
		for(int x=-size; x<size; x++) {
			for(int y=-size; y<size; y++) {
				if(9*x-3==x*y+14*y)
					System.out.println(x + " " + y);
			}
		}
	}
}
