package model.Card;



import model.*;
import model.Square.SafetyTrack;
import model.Square.Square;
import model.Square.Slide.StartSlide;



/** Base class for cards represented by a numeric movement value. */
public abstract class NumberCard extends Card {
	private int number;

	/** Creates a numbered card whose subclass sets the value. */
	protected NumberCard() {
	}

	/**
	 * Applies a directional movement; subclasses override when backward movement differs.
	 *
	 * @param pawn selected pawn
	 * @param boxes outer-track squares
	 * @param safetyTrack selected pawn's Safety and Home route
	 * @param direction requested movement direction
	 * @return {@code 1} when the pawn moved; otherwise {@code 0}
	 */
	public int Act(Pawn pawn, Square[] boxes, SafetyTrack safetyTrack, MoveDirection direction) {
		return Act(pawn, boxes, safetyTrack);
	}

	/**
	 * Returns the numeric value represented by this card.
	 *
	 * @return numeric card value
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Sets the numeric value represented by this card.
	 *
	 * @param number numeric card value
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public int getCard() {
		return getNumber();
	}

	/**

	 * <b>Observer</b>:Move the pawns where the card said

	 * <p>

	 * <b> Precondition:</b>the p must be Pawn

	 * <p>

	 * <b> Precondition:</b>the boxes must be Simple

	 * <p>

	 * <b> Precondition:</b>the card must be int

	 * <p>

	 * <b> Postcondition:</b>Move the pawns where the card said and if is succeed

	 * return 1 otherwise 0

	 *

	 * @param p     selectable pawn of player

	 * @param boxes the array of boxes

	 * @param card  number of card

	 * @param safetyTrack the moving pawn's Safety and Home route

	 * @return 1 in success and 0 in failure

	 */

	public int movePawn(Pawn p, Square[] boxes, int card, SafetyTrack safetyTrack) {

		Square boxp = findBox(p, boxes, card, safetyTrack);

		if (boxp != null) {
			movePawnTo(p, boxp, boxes, safetyTrack);

			return 1;

		} else {

			return 0;

		}

	}

	/**
	 * Updates occupancy and places one pawn on a validated destination.
	 *
	 * @param pawn pawn to move
	 * @param destination validated destination
	 * @param boxes outer-track squares
	 * @param safetyTrack pawn's Safety and Home route
	 */
	protected void movePawnTo(Pawn pawn, Square destination, Square[] boxes, SafetyTrack safetyTrack) {
		Square source = pawn.getCurrentSquare();
		clearSquare(source, boxes, safetyTrack);
		occupySquare(destination, pawn);
		pawn.moveTo(destination);
	}

	/**
	 * Updates occupancy for two pawns that move simultaneously.
	 *
	 * @param pawn1 first pawn
	 * @param destination1 first destination
	 * @param pawn2 second pawn
	 * @param destination2 second destination
	 * @param boxes outer-track squares
	 * @param safetyTrack current player's Safety and Home route
	 */
	protected void movePawnsTo(Pawn pawn1, Square destination1, Pawn pawn2, Square destination2,
			Square[] boxes, SafetyTrack safetyTrack) {
		Square source1 = pawn1.getCurrentSquare();
		Square source2 = pawn2.getCurrentSquare();

		if (source1 != destination1 && source1 != destination2) {
			clearSquare(source1, boxes, safetyTrack);
		}
		if (source2 != destination1 && source2 != destination2 && source2 != source1) {
			clearSquare(source2, boxes, safetyTrack);
		}

		occupySquare(destination1, pawn1);
		occupySquare(destination2, pawn2);
		pawn1.moveTo(destination1);
		pawn2.moveTo(destination2);
	}

	private void clearSquare(Square square, Square[] boxes, SafetyTrack safetyTrack) {
		if (isTrackSquare(square, boxes, safetyTrack)) {
			square.setOccupant(null);
			if (!(square instanceof StartSlide) && safetyTrack.getIndexOf(square) < 0) {
				square.setColor(0);
			}
		}
	}

	private void occupySquare(Square square, Pawn pawn) {
		square.setOccupant(pawn);
		if (!(square instanceof StartSlide)) {
			square.setColor(pawn.getColor() + 1);
		}
	}

	private boolean isTrackSquare(Square square, Square[] boxes, SafetyTrack safetyTrack) {
		if (square == null) {
			return false;
		}
		if (safetyTrack.getIndexOf(square) >= 0) {
			return true;
		}
		for (Square box : boxes) {
			if (box == square) {
				return true;
			}
		}
		return false;
	}

}
