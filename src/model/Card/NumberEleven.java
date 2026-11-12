package model.Card;



import model.*;

import model.Square.SafetyTrack;

import model.Square.Square;
import model.Square.Slide.StartSlide;



/** Card 11: moves forward eleven or swaps with an opponent on the outer track. */
public class NumberEleven extends NumberCard {
	/** Creates card 11. */
	public NumberEleven() {
		setNumber(11);
		setDescription("Move a pawn 11 squares forward, or swap with an opponent's pawn.");
	}


	private int made;



	/**

	 * <b>Observer</b>:Calls the method movePawn to move the pawn if in card 11

	 * chose the move pawn

	 * <p>

	 * <b> Precondition:</b>the p must be pawn

	 * <p>

	 * <b> Precondition:</b>the b must be Simple

	 *

	 * @param p the pawn of one player

	 * @param b the array of boxes

	 * @return 1 in success and 0 in failure

	 */

	public int Act(Pawn p, Square[] b, SafetyTrack safetyTrack) {

		made = movePawn(p, b, getNumber(), safetyTrack);

		if (made == 1) {

			return 1;

		} else {

			return 0;

		}

	}



	/**

	 * <b>Observer</b>:Change the Empty and the Color of the box

	 * <p>

	 * <b> Precondition:</b>the p must be pawn

	 * <p>

	 * <b> Precondition:</b>the b must be Square

	 *

	 * @param p the pawn of one player

	 * @param b the array of boxes

	 */

	public void changeEmpty(Pawn p, Square[] b) {

		for (Square square : b) {
			if (p.getCurrentSquare() == square) {
				square.setOccupant(p);
				if (!(square instanceof StartSlide)) {
					square.setColor(p.getColor() + 1);
				}
				return;
			}
		}
	}



	/**

	 * <b>Observer</b>:This Act use if chose change pawns and swap the coordinates

	 * of pawns

	 * <p>

	 * <b> Precondition:</b>the p1,p2 must be Pawn

	 * <p>

	 * <b> Precondition:</b>the p2 must be Pawn

	 * <p>

	 * <b> Precondition:</b>the b must be Square

	 *

	 * @param p1 the selectable pawn of player

	 * @param p2 the enemy pawn

	 * @param b  the array of boxes

	 * @return 1 in success and 0 in failure

	 */

	public int Act(Pawn p1, Pawn p2, Square[] b, SafetyTrack safetyTrack) {

		made = 0;
		if (p1 == null || p2 == null || p1 == p2 || p1.getColor() == p2.getColor()) {
			return 0;
		}

		Square square1 = p1.getCurrentSquare();
		Square square2 = p2.getCurrentSquare();
		int index1=-1,index2=-1;
		for (int i = 0; i < b.length; i++) {
			if (square1 == b[i]) {
				index1 = i;
			}
			if (square2 == b[i]) {
				index2 = i;
			}
		}
		if (index1 == -1 || index2 == -1
				|| square1.getOccupant() != p1 || square2.getOccupant() != p2) {
			return 0;
		}
		p1.moveTo(square2);
		p2.moveTo(square1);

		changeEmpty(p1, b);

		changeEmpty(p2, b);

		return 1;
	}



}
