package model.Square.Slide;

import model.*;
import model.Square.Square;

public class StartSlide extends Slide {
	private int length; // 4 or 5
	/**
	 * <b>Observer</b>:This method checks if the StartSlide is the same color with
	 * the selectable pawn
	 * <p>
	 * <b> Precondition:</b>the p1 the selectable Pawn
	 * <p>
	 * <b> Postcondition:</b>return true if it is the same color otherwise return
	 * false
	 *
	 * @param p1 the selectable
	 * @return true if it is the same color otherwise return false
	 */
	public boolean checkColor(Pawn p1) {
		if (getColor() == p1.getColor()) {
			return true;
		}
		return false;
	}

	/**
	 * <b>Observer</b>:This method move the pawn that is in the coordinates of
	 * StartSlide and goes to the coordinates of EndSlide And if inside the Slide is
	 * enemy's pawn move it to the Start position
	 * <p>
	 * <b> Precondition:</b>the p1 must be Player array,p2 must be the selectable
	 * Pawn and b must be the array of boxes
	 *
	 * @param p1 this is a array of Players that have inside the two pawns
	 * @param p2 the selectable pawn
	 * @param b  the array of square boxes
	 */
	public void Act(Player[] p1, Pawn p2, Square[] b) {
		if(checkColor(p2)){
			return;
		}
		int startIndex = -1;

		for (int i = 0; i < b.length; i++) {
			if (b[i] == this) {
				startIndex = i;
				break;
			}
		}
		if (startIndex == -1) {
			return;
		}
		int endIndex = (startIndex - (length - 1) + 60) % 60;
		for (int j = 0; j < length; j++) {

			int i = (startIndex - j + 60) % 60;

			for (Player player : p1) {

				Pawn[] pawns = player.getPawns();

				for (int k = 0; k < pawns.length; k++) {

					Pawn pawn = pawns[k];

					if (pawn == p2) {
						continue;
					}

					if (pawn.getCurrentSquare() == b[i]) {
						if (!(b[i] instanceof StartSlide)) {
							b[i].setColor(0);
						}
						pawn.returnToStart();
					}
				}
			}
		}
		if (!(b[endIndex] instanceof StartSlide)) {
			b[endIndex].setColor(p2.getColor() + 1);
		}
		p2.moveTo(b[endIndex]);
	}

	/**
	 * <b>constructor</b>:Constructs a new StartSlide and sets the coordinates for
	 * specific point of the array
	 * <p>
	 * <b> Precondition:</b>the c must be one of {0,1,2} and the l must be one of {4,5}
	 *
	 *
	 * @param c the color
	 * @param l the length
	 */
	public StartSlide(int x, int y, int c, int l) {
		super(x, y);
		setColor(c);
		this.length = l;
	}
}
