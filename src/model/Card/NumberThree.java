package model.Card;



import model.Pawn;

import model.Square.SafetyTrack;

import model.Square.Square;



/** Card 3: moves every pawn that can legally advance three squares. */
public class NumberThree extends SimpleCard {
	/** Creates card 3. */
	public NumberThree() {
		setNumber(3);
		setDescription("Move a pawn 3 squares forward.");
	}


	private int made;



	/**

	 * <b>Observer</b>:Calls method moveAll and move 3 times the pawns of player

	 * <p>

	 * <b> Precondition:</b>the p1 must be pawn

	 * <p>

	 * <b> Precondition:</b>the p2 must be pawn

	 * <p>

	 * <b> Precondition:</b>the b must be Simple

	 *

	 * @param p1 the first pawn of player

	 * @param p2 the second pawn of player

	 * @param b  the array of boxes

	 * @return 1 in success and 0 in failure

	 */

	public int Act(Pawn p1, Pawn p2, Square[] b, SafetyTrack safetyTrack) {

		made = moveAllpawn(p1, p2, b, getNumber(), safetyTrack);

		if (made == 1) {

			return 1;

		} else {

			return 0;

		}
	}



	/**

	 * <b>Observer</b>:Calls method movePawn and move three times one pawn of player

	 * <p>

	 * <b> Precondition:</b>the p must be pawn

	 * <p>

	 * <b> Precondition:</b>the b must be Simple

	 *

	 * @param p the selectable pawn of player

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



}
