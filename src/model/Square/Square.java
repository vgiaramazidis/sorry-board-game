package model.Square;

import model.Pawn;

/** Base type for every logical board location. */

public abstract class Square {

	private int color;

	private int x;

	private int y;

	private Pawn occupant;

	/** Creates an initially empty square. */
	protected Square() {
	}



	/**

	 * <b>Accessory:</b> returns the color's value

	 *

	 * @return the value of color

	 */

	public int getColor() {

		return color;

	}



	/**

	 * <b>Accessory:</b> returns the x value

	 *

	 * @return the value of x

	 */

	public int getX() {

		return x;

	}



	/**

	 * <b>Accessory:</b> returns the y value

	 *

	 * @return the value of y

	 */

	public int getY() {

		return y;

	}



	/**

	 * <b>Accessory:</b> returns the empty's value

	 *

	 * @return the value of empty

	 */

	public Pawn getOccupant() {
		return occupant;
	}

	/**
	 * Changes the pawn occupying this square.
	 *
	 * @param occupant new occupant, or {@code null} when empty
	 */
	public void setOccupant(Pawn occupant) {
		this.occupant = occupant;
	}

	/**

	 * <b>Transformer:</b> sets the color

	 * <p>

	 * <b> Precondition:</b>the c must be one of {0,1,2}

	 *

	 * @param c encoded square or occupant color

	 */

	public void setColor(int c) {

		this.color = c;

	}



	/**

	 * <b>Transformer:</b> sets the value x

	 * <p>

	 * <b> Precondition:</b>the x must be int

	 *

	 * @param x horizontal board coordinate

	 */

	public void setX(int x) {

		this.x = x;

	}



	/**

	 * <b>Transformer:</b> sets the value y

	 * <p>

	 * <b> Precondition:</b>the y must be int

	 *

	 * @param y vertical board coordinate

	 */

	public void setY(int y) {

		this.y = y;

	}



}
