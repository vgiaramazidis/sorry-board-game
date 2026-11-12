package model.Square;

public class Home extends Square {
	/**
	 * <b>constructor</b>:Constructs a new Home an sets the coordinates
	 * <p>
	 * <b> Precondition:</b>the x and y must be coordinates and the c must be one of
	 * {0,1,2}
	 *
	 * @param x coordinates of x
	 * @param y coordinates of y
	 * @param c the color
	 */
	public Home(int x, int y, int c) {
		setX(x);
		setY(y);
		setColor(c);
	}
}
