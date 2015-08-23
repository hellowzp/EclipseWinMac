package dynamicprogramming;

//Introduction to  Algorithms
public class RodCutting {
	
	private static final int INITIAL_VALUE = Integer.MIN_VALUE;
	
	//recursive top-down: too naive
	public static int t_dRecursiveCut(int[] prices, int length) {
		if(length<=1) 
			return prices[length];
		int max = 0;
//		int downMiddle = length >> 1; //signed left shift; >>> unsigned left shift
		for(int i=1; i<=length; i++) {  //can't start from 0, otherwise will call itself recursively
			max = Math.max( max, prices[i] + t_dRecursiveCut(prices, length-i));
		}
		return max;
	}
	
	//dynamic programming using memoized top-down recursive fashion
	public static int t_dMemoizedCut(int[] prices, int length) {
		if(length<=1) 
			return prices[length];
		
		int[] memoizedLookupTable = new int[length+1];
		for(int i=0; i<=length; i++) 
			memoizedLookupTable[i] = INITIAL_VALUE;
		
		//recursively call the helper function,
		//a third parameter is required to share lookup table among function calls
		return t_dMemoizedCut(prices, length, memoizedLookupTable);		
	}
	
	private static int t_dMemoizedCut(int[] prices, int length, int[] lookupTable) {
		if(lookupTable[length]!=INITIAL_VALUE) 
			return lookupTable[length];
		
		if(length<=1) 
			return prices[length];
		
		int max = 0;
		for(int i=1; i<=length; i++) { //can't start from 0, otherwise will call itself recursively
			max = Math.max( max, prices[i] + t_dMemoizedCut(prices, length - i, lookupTable));
		}
		
		lookupTable[length] = max;
		return max;		
	}
	
	//dynamic programming using bottom-up fashion
	public static int b_uCut(int[] prices, int length) {
		int[] lookupTable = new int[length+1];
		lookupTable[0] = 0;  //base solution
		
		//the first loop is used to construct solutions step by step
		//the second loop solves the sub-problem by using the previously 
		//constructed lookup table so as to avoid using recursion
		for(int i=1; i<=length; i++) {
			int max = 0;
			for(int j=1; j<=i; j++) {
				max = Math.max(max, prices[j] + lookupTable[i-j]);
			}		
			lookupTable[i] = max;
		}
		
		return lookupTable[length];
	}
	
	//dynamic programming using bottom-up fashion and return the constructed solution
	public static int[] b_uCutWithSolution(int[] prices, int length) {
		int[] lookupTable = new int[length+1];		
		int[] cuttingList = new int[length+1]; 
		
		//base solution
		lookupTable[0] = 0;  
		
		//the first loop is used to construct solutions step by step
		//the second loop solves the sub-problem by using the previously 
		//constructed lookup table so as to avoid using recursion
		//Theorem: later solutions must be a combinatuion of the previous two sub-solutions
		for(int i=1; i<=length; i++) {
			int max = 0;
			for(int j=1; j<=i; j++) {
				if(max < prices[j] + lookupTable[i-j] ){
					max = prices[j] + lookupTable[i-j];
					cuttingList[i] = j;  //the optimal solution for length i is (j,i-j) ==> recursive looking up
				}				
			}		
			lookupTable[i] = max;
		}
		
		cuttingList[0] = lookupTable[length]; 
		return cuttingList;
	}
		
	public static void main(String[] args) {
		{
			int[] prices = new int[] {0,1,5,8,9,10,17,17,20,24,30};
			System.out.println("price list: 0,1,5,8,9,10,17,17,20,27,30");
			
			for(int i=1; i<=10; i++) {
				int length = i;
				int[] solution = b_uCutWithSolution(prices, length);
				System.out.print("Best solution for length " + length + " : " + solution[0] + " ==> ");
				while(length>0) {
					System.out.print(solution[length] + " ");
					length -= solution[length];
				}
				System.out.println();
			}				
		}
		
		
		for(int i=20; i<=30; i++) {
			int n = i;
			int[] prices = new int[n+1];
			prices[0] = 0;
			for(int j=1; j<=n; j++) {
				prices[j] = 2 * j + (int)( Math.random() * 3) - 1; 
			}
			System.out.println("\n\n-----N = " + n + "---");
			long time = System.currentTimeMillis();
			System.out.println( t_dRecursiveCut(prices, n) + " time elapsed: " 
							  + ( System.currentTimeMillis() - time ));
			
			time = System.currentTimeMillis();
			System.out.println( t_dMemoizedCut(prices, n) + " time elapsed: " 
							  + ( System.currentTimeMillis() - time ));
			
			time = System.currentTimeMillis();
			System.out.println( b_uCut(prices, n) + " time elapsed: " 
							  + ( System.currentTimeMillis() - time ));			
		}
		
		for(int i=1; i<=50; i++) {
			int n = 1000 * i;
			int[] prices = new int[n+1];
			prices[0] = 0;
			for(int j=1; j<=n; j++) {
				prices[j] = 2 * j + (int)( Math.random() * 3) - 1; 
			}
			System.out.println("\n\n-----N = " + n + "-----");
			long time = System.currentTimeMillis();
			System.out.println( t_dMemoizedCut(prices, n) + " time elapsed: " 
							  + ( System.currentTimeMillis() - time ));
			
			System.out.println( b_uCut(prices, n) + " time elapsed: " 
					  + ( System.currentTimeMillis() - time ));
			
		}
		
	}
	
}
