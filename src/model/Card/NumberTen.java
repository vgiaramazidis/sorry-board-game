package model.Card;



import model.*;

import model.Square.SafetyTrack;

import model.Square.Square;



public class NumberTen extends NumberCard {
	public NumberTen() {
		setNumber(10);
		setDescription("Move a pawn 10 squares forward, or 1 square backward.");
	}


	/**

	 * <b>Observer</b>:Calls the method movePawn and move pawn ten times

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
		return movePawn(p, b, getNumber(), safetyTrack);
	}

	@Override
	public int Act(Pawn p, Square[] b, SafetyTrack safetyTrack, MoveDirection direction) {
		if (direction == MoveDirection.BACKWARD) {
			return movePawn(p, b, -1, safetyTrack);
		}
		return Act(p, b, safetyTrack);
	}



	/**

	 * Card 10 does not use a two-pawn action.
	 */

	public int Act(Pawn p1, Pawn p2, Square[] b, SafetyTrack safetyTrack) {

		return 0;
	}

}
