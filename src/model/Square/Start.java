package model.Square;

public class Start extends Square {
	/**
	 * <b>constructor</b>:Constructs a new Start an sets the coordinates for
	 * specific point of start
	 * <p>
	 * <b> Precondition:</b>the x and y must be coordinates and c must be one of
	 * {0,1,2}
	 *
	 * @param x coordinates of x
	 * @param y coordinates of y
	 * @param c the color
	 */
	public Start(int x, int y, int c) {
		setX(x);
		setY(y);
		setColor(c);
	}
}
