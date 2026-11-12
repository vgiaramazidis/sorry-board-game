package model.Square;

/** Groups one player's five Safety squares, two Home slots, and entry point. */
public class SafetyTrack {
	private static final int SQUARE_COUNT = 5;
	private static final int OUTER_TRACK_SIZE = 60;

	private final Safety[] squares;
	private final Home[] homeSquares;
	private final int entryIndex;

	/**
	 * Creates a player's complete Safety/Home route.
	 *
	 * @param squares exactly five colored Safety squares
	 * @param homeSquares exactly two Home slots
	 * @param entryIndex outer-track index immediately before Safety entry
	 */
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

	/**
	 * Returns one protected square.
	 *
	 * @param index zero-based Safety index
	 * @return Safety square at that index
	 */
	public Safety getSquare(int index) {
		return squares[index];
	}

	/**
	 * Returns one Home slot.
	 *
	 * @param index zero-based Home index
	 * @return Home square at that index
	 */
	public Home getHomeSquare(int index) {
		return homeSquares[index];
	}

	/**
	 * Returns the protected route length.
	 *
	 * @return number of Safety squares
	 */
	public int getLength() {
		return squares.length;
	}

	/**
	 * Finds a square within this Safety track.
	 *
	 * @param square candidate square
	 * @return zero-based Safety index, or {@code -1} when absent
	 */
	public int getIndexOf(Square square) {
		for (int i = 0; i < squares.length; i++) {
			if (squares[i] == square) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the point at which the player leaves the shared track.
	 *
	 * @return outer-track index immediately before Safety entry
	 */
	public int getEntryIndex() {
		return entryIndex;
	}
}
