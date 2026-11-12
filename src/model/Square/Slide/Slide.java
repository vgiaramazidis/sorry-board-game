package model.Square.Slide;

import model.Square.Square;

/** Base type for the start, internal, and end squares of a slide. */
public abstract class Slide extends Square {
	/**
	 * Creates a slide square at a board coordinate.
	 *
	 * @param x horizontal board coordinate
	 * @param y vertical board coordinate
	 */
	protected Slide(int x, int y) {
		setX(x);
		setY(y);
	}
}
