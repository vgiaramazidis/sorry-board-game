package model.Card;



import model.Pawn;

import model.Square.SafetyTrack;

import model.Square.Square;



/** Card 8: moves both movable pawns or may be exchanged for another draw. */
public class NumberEight extends SimpleCard {
	/** Creates card 8. */
	public NumberEight() {
		setNumber(8);
		setDescription("Move a pawn 8 squares forward.");
	}


	private int made;



	/**

	 * <b>Observer</b>:Calls the method movePawn to move the pawn

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

		if (made == 0) {

			return 0;

		} else {

			return 1;

		}

	}



	@Override

	public int Act(Pawn p1, Pawn p2, Square[] b, SafetyTrack safetyTrack) {

		return 0;

	}



}
