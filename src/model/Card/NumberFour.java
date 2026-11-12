package model.Card;



import model.*;

import model.Square.SafetyTrack;

import model.Square.Square;



public class NumberFour extends NumberCard {
	public NumberFour() {
		setNumber(4);
		setDescription("Move a pawn 4 squares backward.");
	}


	private int made;



	/**

	 * <b>Observer</b>:Calls method movePawn and goes backward 4 times

	 * <p>

	 * <b> Precondition:</b>the p must be Pawn

	 * <p>

	 * <b> Precondition:</b>the b must be Simple

	 *

	 * @param p the pawn of one player

	 * @param b the array of boxes

	 * @return 1 in success and 0 in failure

	 */

	public int Act(Pawn p, Square[] b, SafetyTrack safetyTrack) {

		made = movePawn(p, b, -getNumber(), safetyTrack);

		if (made == 1) {

			return 1;

		} else {

			return 0;

		}

	}



	@Override

	public int Act(Pawn p1, Pawn p2, Square[] b, SafetyTrack safetyTrack) {

		return 0;

	}

}
