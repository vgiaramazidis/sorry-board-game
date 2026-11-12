package model.Square;

/** A protected square on one player's colored route toward Home. */
public class Safety extends Square {
	/**
	 * <b>constructor</b>:Constructs a new Safety an sets the coordinates
	 * <p>
	 * <b>Precondition:</b>the x,y must be int and the c must be on of {0,1,2}
	 *
	 * @param x coordinates of x
	 * @param y coordinates of x
	 * @param c the color
	 */
	public Safety(int x, int y, int c) {
		setX(x);
		setY(y);
		setColor(c);
	}
}
