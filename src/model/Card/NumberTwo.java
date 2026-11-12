package model.Card;



import model.*;

import model.Square.SafetyTrack;

import model.Square.Square;



public class NumberTwo extends NumberCard {
	public NumberTwo() {
		setNumber(2);
		setDescription("Move a pawn from Start or 2 squares forward. Play again!");
	}


	private int made;



	/**

	 * <b>Observer</b>:Calls method movePawn and move two time the pawn or move the

	 * pawn from Start

	 * <p>

	 * <b> Precondition:</b>The p must be Pawn

	 * <p>

	 * <b> Precondition:</b>The b must be Simple

	 *

	 * @param p the pawn of one player

	 * @param b the array of boxes

	 * @return 1 in success and 0 in failure

	 */

	public int Act(Pawn p, Square[] b, SafetyTrack safetyTrack) {
		if (p.isStart()) {
			int exitIndex = p.getStartExitIndex();
			Pawn occupant = b[exitIndex].getOccupant();
			if (occupant != null && occupant.getColor() == p.getColor()) {
				return 0;
			}
			b[exitIndex].setColor(p.getColor() + 1);
			p.moveTo(b[exitIndex]);
			return 1;
		}

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
