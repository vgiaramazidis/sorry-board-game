package model;

/** Owns the two pawns belonging to one player. */
public class Player {

	private Pawn[] p = new Pawn[2];



	/**

	 * <b>constructor</b>:Constructs a new Player and set the color for the pawns and the coordinates of Start accordingly the color

	 * <b> Precondition:</b>the color must be one of {0,1,2,3}

	 *

	 * @param color the color of pawn

	 */

	public Player(int color) {
		if (color < 0 || color > 3) {
			throw new IllegalArgumentException("Invalid player color");
		}
		int firstHomeSlot = color == 2 ? 1 : 0;
		int secondHomeSlot = 1 - firstHomeSlot;
		p[0] = new Pawn(color, firstHomeSlot);
		p[1] = new Pawn(color, secondHomeSlot);
	}



	/**

	 * <b>Accessory:</b> returns the pawns value

	 *

	 * @return the value of two pawns

	 */

	public Pawn[] getPawns() {

		return p.clone();

	}

}
