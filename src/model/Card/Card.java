package model.Card;



import model.Pawn;
import model.Square.Home;
import model.Square.SafetyTrack;

import model.Square.Square;



public abstract class Card {
	private String description;

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	/**

	 * <b>Observer</b>:This method find the box where the pawn is up

	 * <p>

	 * <b> Precondition:</b>the p must be Pawn

	 * <p>

	 * <b> Precondition:</b>the boxes must be Simple

	 * <p>

	 * <b> Precondition:</b>the card must be int

	 * <p>

	 * <b> Postcondition:</b> Must find the box of pawn and return this box if its

	 * succeed and if it is failure return null

	 *

	 * @param p     the selectable pawn

	 * @param boxes the array of boxes

	 * @param card  the number of card

	 * @return returns the box if its succeed null in failure

	 */

	public Square findBox(Pawn p, Square[] boxes, int card, SafetyTrack safetyTrack) {

		Square currentSquare = p.getCurrentSquare();
		if (currentSquare == null) {
			return null;
		}

		int safetyIndex = safetyTrack.getIndexOf(currentSquare);
		if (safetyIndex >= 0) {
			int destinationIndex = safetyIndex + card;
			if (destinationIndex < 0) {
				int outerIndex = (safetyTrack.getEntryIndex() - destinationIndex - 1 + boxes.length)
						% boxes.length;
				Square destination = boxes[outerIndex];
				Pawn occupant = destination.getOccupant();
				return occupant == null || occupant.getColor() != p.getColor()
						? destination : null;
			}
			if (destinationIndex > safetyTrack.getLength()) {
				return null;
			}
			if (destinationIndex == safetyTrack.getLength()) {
				return findAvailableHome(p, safetyTrack);
			}
			Square destination = safetyTrack.getSquare(destinationIndex);
			return destination.getOccupant() == null ? destination : null;
		}

		int currentIndex = -1;
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i] == currentSquare) {
				currentIndex = i;
				break;
			}
		}
		if (currentIndex < 0) {
			return null;
		}

		if (card > 0) {
			int distanceToEntry = (currentIndex - safetyTrack.getEntryIndex() + boxes.length) % boxes.length;
			if (card > distanceToEntry) {
				int destinationIndex = card - distanceToEntry - 1;
				if (destinationIndex > safetyTrack.getLength()) {
					return null;
				}
				if (destinationIndex == safetyTrack.getLength()) {
					return findAvailableHome(p, safetyTrack);
				}
				Square destination = safetyTrack.getSquare(destinationIndex);
				return destination.getOccupant() == null ? destination : null;
			}
		}

		Square destination = boxes[(currentIndex - card + boxes.length) % boxes.length];
		Pawn occupant = destination.getOccupant();

		if (occupant == null || occupant.getColor() != p.getColor()) {
			return destination;
		}
		return null;
	}

	private Home findAvailableHome(Pawn pawn, SafetyTrack safetyTrack) {
		Home home = safetyTrack.getHomeSquare(pawn.getHomeSlotIndex());
		return home.getOccupant() == null || home.getOccupant() == pawn ? home : null;
	}



	public abstract int Act(Pawn p, Square[] b, SafetyTrack safetyTrack);



	public abstract int Act(Pawn p1, Pawn p2, Square[] b, SafetyTrack safetyTrack);



	/**

	 * <b>Accessory:</b> returns the card's value

	 *

	 * @return the card

	 */

	public abstract int getCard();

}
