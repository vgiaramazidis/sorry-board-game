package model.Card;



import model.Pawn;
import model.Square.Home;
import model.Square.SafetyTrack;

import model.Square.Square;



public abstract class SimpleCard extends NumberCard {

	/**

	 * <b>Observer</b>:Is a method that move the two pawns of one player

	 * <p>

	 * <b> Precondition:</b>the p1 must be Pawn

	 * <p>

	 * <b> Precondition:</b>the p2 must be Pawn

	 * <p>

	 * <b> Precondition:</b>the boxes must be Simple

	 * <p>

	 * <b> Precondition:</b>the card must be int

	 * <p>

	 * <b> Postcondition:</b>Move the pawns where the card said and if is succeed

	 * return 1 otherwise 0

	 *

	 * @param p1    the first pawn of player

	 * @param p2    the second pawn of player

	 * @param boxes the array of boxes

	 * @param card  the number of card

	 * @return 1 in success and 0 in failure

	 */

	public int moveAllpawn(Pawn p1, Pawn p2, Square[] boxes, int card, SafetyTrack safetyTrack) {
		Square source1 = p1.getCurrentSquare();
		Square source2 = p2.getCurrentSquare();

		// Both pawns move at the same time. Treat their current squares as vacated
		// while destinations are calculated, then reject a destination that would
		// require a pawn which cannot move to vacate its square.
		clearOwnOccupant(source1, p1);
		clearOwnOccupant(source2, p2);
		Square boxp1 = findBox(p1, boxes, card, safetyTrack);
		Square boxp2 = findBox(p2, boxes, card, safetyTrack);
		restoreOwnOccupant(source1, p1);
		restoreOwnOccupant(source2, p2);

		if (boxp1 == source2 && boxp2 == null) {
			boxp1 = null;
		}
		if (boxp2 == source1 && boxp1 == null) {
			boxp2 = null;
		}

		Home home0 = safetyTrack.getHomeSquare(0);
		Home home1 = safetyTrack.getHomeSquare(1);
		if (boxp1 != null && boxp1 == boxp2) {
			if (boxp1 == home0 && home1.getOccupant() == null) {
				boxp2 = home1;
			} else if (boxp1 == home1 && home0.getOccupant() == null) {
				boxp2 = home0;
			}
		}

		if (boxp1 != null && boxp2 != null && boxp1 != boxp2) {

			movePawnsTo(p1, boxp1, p2, boxp2, boxes, safetyTrack);

			return 1;

		}else if(boxp1 != null){
			movePawnTo(p1, boxp1, boxes, safetyTrack);
			return 1;
		}else if (boxp2 != null) {
			movePawnTo(p2, boxp2, boxes, safetyTrack);
			return 1;
		}
		return 0;

	}

	private void clearOwnOccupant(Square square, Pawn pawn) {
		if (square != null && square.getOccupant() == pawn) {
			square.setOccupant(null);
		}
	}

	private void restoreOwnOccupant(Square square, Pawn pawn) {
		if (square != null) {
			square.setOccupant(pawn);
		}
	}

}
