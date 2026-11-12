package model.Square;

/** A normal square on the shared outer track. */
public class Simple extends Square {
	/**
	 * <b>constructor</b>:Constructs a new Simple an sets the coordinates and set
	 * the color with 0
	 * <p>
	 * <b>Precondition:</b>the x,y must be int
	 *
	 * @param x coordinates of x
	 * @param y coordinates of y
	 */
	public Simple(int x, int y) {
		setX(x);
		setY(y);
		setColor(0);
	}
}
