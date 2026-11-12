package model.Card;



import model.Pawn;

import model.Square.SafetyTrack;

import model.Square.Square;



public class NumberTwelve extends SimpleCard {
	public NumberTwelve() {
		setNumber(12);
		setDescription("Move a pawn 12 squares forward.");
	}


	private int made;



	/**

	 * <b>Observer</b>:Calls the method movePawn and move twelve times the pawn of

	 * player

	 * <p>

	 * <b> Precondition:</b>the p must be pawn and the board

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



	@Override

	public int Act(Pawn p1, Pawn p2, Square[] b, SafetyTrack safetyTrack) {

		return 0;

	}



}
