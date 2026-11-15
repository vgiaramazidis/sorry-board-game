package model.Card;



import model.*;

import model.Square.SafetyTrack;

import model.Square.Home;
import model.Square.Square;



/** Card 7: moves one pawn seven squares or splits seven between two pawns. */
public class NumberSeven extends NumberCard {
	/** Creates card 7. */
	public NumberSeven() {
		setNumber(7);
		setDescription("Move a pawn 7 squares forward, or split between two pawns.");
	}


	private int made;



	/**

	 * <b>Observer</b>:Calls method movePawn and move seven time the pawn

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

		made = movePawn(p, b, getNumber(), safetyTrack);

		if (made == 1) {

			return 1;

		} else {

			return 0;

		}

	}

	/**
	 * Splits seven forward steps between two pawns and moves them together.
	 *
	 * @param firstPawn pawn receiving {@code firstSteps}
	 * @param secondPawn pawn receiving the remaining steps
	 * @param firstSteps steps assigned to the first pawn, from 1 through 6
	 * @param boxes outer-track squares
	 * @param safetyTrack current player's Safety and Home route
	 * @return {@code 1} when both moves are legal and applied; otherwise {@code 0}
	 */
	public int splitMove(Pawn firstPawn, Pawn secondPawn, int firstSteps, Square[] boxes,
			SafetyTrack safetyTrack) {
		Square[] destinations = findSplitDestinations(
				firstPawn, secondPawn, firstSteps, boxes, safetyTrack);
		if (destinations == null) {
			return 0;
		}

		movePawnsTo(
				firstPawn, destinations[0], secondPawn, destinations[1], boxes, safetyTrack);
		return 1;
	}

	/**
	 * Tests a proposed split without changing either pawn.
	 *
	 * @param firstPawn pawn receiving {@code firstSteps}
	 * @param secondPawn pawn receiving the remaining steps
	 * @param firstSteps steps assigned to the first pawn, from 1 through 6
	 * @param boxes outer-track squares
	 * @param safetyTrack current player's Safety and Home route
	 * @return whether the complete split is legal
	 */
	public boolean canSplitMove(Pawn firstPawn, Pawn secondPawn, int firstSteps,
			Square[] boxes, SafetyTrack safetyTrack) {
		return findSplitDestinations(
				firstPawn, secondPawn, firstSteps, boxes, safetyTrack) != null;
	}

	private Square[] findSplitDestinations(Pawn firstPawn, Pawn secondPawn, int firstSteps,
			Square[] boxes, SafetyTrack safetyTrack) {
		if (firstPawn == null || secondPawn == null || firstPawn == secondPawn
				|| firstPawn.getColor() != secondPawn.getColor()
				|| firstSteps < 1 || firstSteps > 6) {
			return null;
		}

		int secondSteps = getCard() - firstSteps;
		Square firstSource = firstPawn.getCurrentSquare();
		Square secondSource = secondPawn.getCurrentSquare();
		clearOwnOccupant(firstSource, firstPawn);
		clearOwnOccupant(secondSource, secondPawn);
		Square firstDestination;
		Square secondDestination;
		try {
			firstDestination = findBox(firstPawn, boxes, firstSteps, safetyTrack);
			secondDestination = findBox(secondPawn, boxes, secondSteps, safetyTrack);
		} finally {
			restoreOwnOccupant(firstSource, firstPawn);
			restoreOwnOccupant(secondSource, secondPawn);
		}

		if (firstDestination != null && firstDestination == secondDestination) {
			Home home0 = safetyTrack.getHomeSquare(0);
			Home home1 = safetyTrack.getHomeSquare(1);
			if (firstDestination == home0 && home1.getOccupant() == null) {
				secondDestination = home1;
			} else if (firstDestination == home1 && home0.getOccupant() == null) {
				secondDestination = home0;
			}
		}

		if (firstDestination == null || secondDestination == null
				|| firstDestination == secondDestination) {
			return null;
		}
		return new Square[]{firstDestination, secondDestination};
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


	@Override

	public int Act(Pawn p1, Pawn p2, Square[] b, SafetyTrack safetyTrack) {

		return 0;

	}

}
