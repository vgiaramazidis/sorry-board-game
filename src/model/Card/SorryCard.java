package model.Card;



import model.*;

import model.Square.SafetyTrack;

import model.Square.Square;
import model.Square.Slide.StartSlide;

/** Sorry! card: replaces an opponent on the outer track with a Start pawn. */
public class SorryCard extends Card {
	private static final int CARD_NUMBER = 6;

	/** Creates a Sorry! card. */
	public SorryCard() {
		setDescription("Take a pawn from Start to replace an opponent's pawn!");
	}



	/**

	 * <b>Observer</b>:Change the Empty and the Color of the box

	 * <p>

	 * <b> Precondition:</b>the p must be pawn

	 * <p>

	 * <b> Precondition:</b>the b must be Simple

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

	 * <b>Observer</b>:This method change the coordinates of the pawn that is in the

	 * start position only and move it directly to coordinates of one of the enemy

	 * pawns

	 * <p>

	 * <b> Precondition:</b>the p1 must be Pawn

	 * <p>

	 * <b> Precondition:</b>the p2 must be Pawn

	 * <p>

	 * <b> Precondition:</b>the b must be Simple

	 *

	 * @param p1 the selectable pawn

	 * @param p2 the enemy's pawn

	 * @param b  the array of boxes

	 */

	public int Act(Pawn p1, Pawn p2, Square[] b, SafetyTrack safetyTrack) {

		int made = 0;

		boolean opponentOnOuterTrack = false;
		for (Square square : b) {
			if (square == p2.getCurrentSquare()) {
				opponentOnOuterTrack = true;
				break;
			}
		}
		if (p1.isStart() && !p2.isStart() && !p2.isHome() && opponentOnOuterTrack) {
			p1.moveTo(p2.getCurrentSquare());

			changeEmpty(p1, b);

			made = 1;

		}

		if (made == 1) {

			return 1;

		} else {

			return 0;

		}

	}



	/**

	 * <b>Accessory:</b> returns the card's value

	 *

	 * @return the card

	 */

	public int getCard() {

		return CARD_NUMBER;

	}



	@Override

	public int Act(Pawn p, Square[] b, SafetyTrack safetyTrack) {

		return 0;

	}

}
