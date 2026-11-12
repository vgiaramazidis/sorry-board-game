package model.Square;

public class SafetyTrack {
	private static final int SQUARE_COUNT = 5;
	private static final int OUTER_TRACK_SIZE = 60;

	private final Safety[] squares;
	private final Home[] homeSquares;
	private final int entryIndex;

	public SafetyTrack(Safety[] squares, Home[] homeSquares, int entryIndex) {
		if (squares == null || squares.length != SQUARE_COUNT) {
			throw new IllegalArgumentException("A SafetyTrack must contain exactly five squares.");
		}
		if (homeSquares == null || homeSquares.length != 2) {
			throw new IllegalArgumentException("A SafetyTrack must have exactly two Home squares.");
		}
		if (entryIndex < 0 || entryIndex >= OUTER_TRACK_SIZE) {
			throw new IllegalArgumentException("The entry index must be on the 60-square outer track.");
		}

		this.squares = squares.clone();
		this.homeSquares = homeSquares.clone();
		this.entryIndex = entryIndex;
	}

	public Safety getSquare(int index) {
		return squares[index];
	}

	public Home getHomeSquare(int index) {
		return homeSquares[index];
	}

	public int getLength() {
		return squares.length;
	}

	public int getIndexOf(Square square) {
		for (int i = 0; i < squares.length; i++) {
			if (squares[i] == square) {
				return i;
			}
		}
		return -1;
	}

	public int getEntryIndex() {
		return entryIndex;
	}
}
