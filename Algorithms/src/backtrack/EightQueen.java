package backtrack;

//http://ideone.com/8iamUa
public class EightQueen {
	// Number of rows or columns
	public static final int BOARD_SIZE = 8;

	boolean[][] board;
	// Indicate an empty square
	public static final boolean EMPTY = false;
	// Indicate a square which containing a queen
	public static final boolean QUEEN = true;
	// Number of moves
	public static final int MOVES = 4;
	// Horizontal moves
	int[] horizontal;
	// Vertical moves
	int[] vertical;

	public int queens = 0;

	public static void main(String[] s) {
		EightQueen main = new EightQueen();
		main.placeQueens(0);
		main.displayBoard();
	}

	public EightQueen() {
		// Constructor creates an empty board
		board = new boolean[BOARD_SIZE][BOARD_SIZE];
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				board[row][col] = EMPTY;
			}
		}

		horizontal = new int[MOVES];
		vertical = new int[MOVES];
		// up right
		horizontal[0] = -1;
		vertical[0] = 1;
		// down left
		horizontal[1] = 1;
		vertical[1] = -1;
		// up left
		horizontal[2] = -1;
		vertical[2] = -1;
		// down right
		horizontal[3] = 1;
		vertical[3] = 1;

	}
	
	public boolean placeQueens() {
		return placeQueens(0);
	}

	private boolean placeQueens(int column) {
		if (column >= BOARD_SIZE) {
			return true;
		}
		
		boolean queenPlaced = false;
		int row = 0;

		while (!queenPlaced && row < BOARD_SIZE) {
			if(isUnderAttack(row, column)) {
				++row;
			}else{
				setQueen(row, column);
				queenPlaced = placeQueens(column + 1);
				if (!queenPlaced) {
					removeQueen(row, column);
					++row;
				}// end if
			}// end else
		}// end while
		
		return queenPlaced;
	}

	private void removeQueen(int row, int column) {
		board[row][column] = EMPTY;
		System.out.printf("queen REMOVED from [%d][%d]\n", row, column);
		--queens;
	}

	private void setQueen(int row, int column) {
		board[row][column] = QUEEN;
		System.out.printf("queen PLACED in [%d][%d]\n", row, column);
		++queens;
	}

	public boolean isUnderAttack(int row, int col) {

		boolean condition = false;
		// check row
		for (int column = 0; column < BOARD_SIZE; column++) {
			if ((board[row][column] == true)) {
				condition = true;
			}
		}

		// check column
		for (int row_ = 0; row_ < board.length; row_++) {
			if (board[row_][col] == true) {
				condition = true;
			}
		}

		// check diagonal
		for (int row_ = row, col_ = col; row_ >= 0 && col_ < 8; row_ += horizontal[0], col_ += vertical[0]) {
			if (board[row_][col_] == true) {
				condition = true;
			}
		}
		for (int row_ = row, col_ = col; row_ < 8 && col_ >= 0; row_ += horizontal[1], col_ += vertical[1]) {
			if (board[row_][col_] == true) {
				condition = true;
			}
		}
		for (int row_ = row, col_ = col; row_ >= 0 && col_ >= 0; row_ += horizontal[2], col_ += vertical[2]) {
			if (board[row_][col_] == true) {
				condition = true;
			}
		}
		for (int row_ = row, col_ = col; row_ < 8 && col_ < 8; row_ += horizontal[3], col_ += vertical[3]) {
			if (board[row_][col_] == true) {
				condition = true;
			}
		}

		return condition;
	}

	public void displayBoard() {
		int counter = 0;
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				if (board[row][col] == true) {
					System.out.printf("|%s|", "x");
					counter++;
				} else {
					System.out.printf("|%s|", "o");
				}
			}
			System.out.println();
		}

		System.out.printf("%d queens has been placed\n", counter);
	}
}
