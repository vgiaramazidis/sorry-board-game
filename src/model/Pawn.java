package model;

import model.Square.Square;
import model.Square.Home;


public class Pawn {

	private static final int[] START_EXIT_INDEXES = {56, 26, 41, 11};

	private int color;

	private final int homeSlotIndex;

	private Square currentSquare;

	private Square startSquare;


	/**

	 * Constructs a pawn with its player color. Its start square is assigned by
	 * the board when the game is initialized.

	 * <p>

	 * <b>Precondition:</b>the color must be one of {0,1,2,3}

	 *

	 * @param c the color
	 */

	public Pawn(int c) {
		this(c, 0);
	}

	public Pawn(int c, int homeSlotIndex) {
		setColor(c);
		if (homeSlotIndex < 0 || homeSlotIndex > 1) {
			throw new IllegalArgumentException("Invalid home slot index");
		}
		this.homeSlotIndex = homeSlotIndex;
	}

	public Square getCurrentSquare() {
		return currentSquare;
	}

	public void setStartSquare(Square startSquare) {
		if (startSquare == null) {
			throw new IllegalArgumentException("A pawn start square cannot be null.");
		}
		if (currentSquare != null && currentSquare != startSquare
				&& currentSquare.getOccupant() == this) {
			currentSquare.setOccupant(null);
		}
		this.startSquare = startSquare;
		this.currentSquare = startSquare;
		startSquare.setOccupant(this);
	}

	public void moveTo(Square square) {
		if (square == null) {
			throw new IllegalArgumentException("A pawn destination cannot be null.");
		}
		if (currentSquare != null && currentSquare != square
				&& currentSquare.getOccupant() == this) {
			currentSquare.setOccupant(null);
		}
		currentSquare = square;
		square.setOccupant(this);
	}



	/**

	 * <b>Transformer:</b> sets the value color

	 * <p>

	 * <b> Precondition:</b>the c must be on of {0,1,2,3}

	 *

	 * @param c the color

	 */

	public void setColor(int c) {
		if (c < 0 || c >= START_EXIT_INDEXES.length) {
			throw new IllegalArgumentException("Invalid pawn color");
		}
		this.color = c;

	}



	/**

	 * <b>Accessory:</b> returns the color's value

	 *

	 * @return the value of color

	 */

	public int getColor() {

		return color;

	}

	public int getHomeSlotIndex() {
		return homeSlotIndex;
	}

	public int getStartExitIndex() {
		if (color < 0 || color >= START_EXIT_INDEXES.length) {
			throw new IllegalStateException("Pawn has an invalid player color");
		}
		return START_EXIT_INDEXES[color];
	}



	/**

	 * <b>Accessory:</b> returns the endX's value

	 *

	 * @return the value of endX

	 */

	public int getEndx() {
		return currentSquare == null ? 0 : currentSquare.getX();

	}



	/**

	 * <b>Accessory:</b> returns the endY's value

	 *

	 * @return the value of endY

	 */

	public int getEndy() {
		return currentSquare == null ? 0 : currentSquare.getY();

	}



	/**

	 * <b>Observer</b>:This method check if the pawn is in Start

	 *

	 * @return true if is in Start the pawn otherwise false

	 */

	public boolean isStart() {
		return currentSquare != null && currentSquare == startSquare;
	}



	/**

	 * <b>Observer</b>:This method check if the pawn is in Home

	 */

	public boolean isHome() {
		return currentSquare instanceof Home && currentSquare.getColor() == color + 1;
	}

	public void returnToStart() {
		if (currentSquare != null && currentSquare != startSquare
				&& currentSquare.getOccupant() == this) {
			currentSquare.setOccupant(null);
		}
		currentSquare = startSquare;
		if (startSquare != null) {
			startSquare.setOccupant(this);
		}
	}

}
