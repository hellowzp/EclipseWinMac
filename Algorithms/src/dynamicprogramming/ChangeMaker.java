package dynamicprogramming;

public class ChangeMaker {

	// naive recursion
	public static int makeChangesWithRecursion(int[] changes, int change) {
		int minChanges = Integer.MAX_VALUE;
		for (int i = 0; i < changes.length; i++) {
			if (changes[i] == change)
				return 1;
		}

		for (int i = 0; i < changes.length; i++) {
			if (changes[i] < change) {
				int thisChanges = makeChangesWithRecursion(changes, change - changes[i])
								+ makeChangesWithRecursion(changes, changes[i]);
				if (thisChanges <= minChanges)
					minChanges = thisChanges;
			}
		}

		return minChanges;
	}

	// dynamic programming: save intermediate results in a table to reduce
	// recursive calls
	// the another two parameters are used to be transferred between function
	// calls
	public static int[] makeChangesWithDP(int[] changes, int change) {
		int[] changesUsed = new int[ change+1 ];
		int[] lastChange = new int[ change+1 ];
		makeChanges(changes, change, changesUsed, lastChange);
		return changesUsed;
	}

	private static void makeChanges(int[] changes, int money, int[] changesUsed, int[] lastChange) {
		changesUsed[0] = 0;

		for (int cents = 1; cents <= money; cents++) {
			int minCoins = cents;
			int newCoin = 1;

			for (int j = 0; j < changes.length; j++) {
				if (changes[j] > cents) // Cannot use coin j
					continue;
				if (changesUsed[cents - changes[j]] + 1 < minCoins) {
					minCoins = changesUsed[cents - changes[j]] + 1;
					newCoin = changes[j];
				}
			}

			changesUsed[cents] = minCoins;
			lastChange[cents] = newCoin;
		}
	}

	public static void main(String[] args) {
		int[] changes = new int[] { 1, 5, 10, 21, 25 };
		int money = 37;
		
		System.out.println(makeChangesWithRecursion(changes, money));
		
		int[] used = makeChangesWithDP(changes, money);
		System.out.println( "Best is " + used[ money ] + " coins" );

	}
}
