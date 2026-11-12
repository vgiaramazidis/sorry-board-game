package model;



import java.util.Random;



import model.Card.*;



/** Maintains the shuffled 44-card draw order and current draw position. */
public class Deck {
	private static final int[] CARD_VALUES = {1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12};

	private int sumCard = 43;

	private Card[] cards = new Card[44];



	/**

	 * <b>constructor</b>:Constructs a new Deck an sets the Deck

	 */

	public Deck() {
		int[] drawOrder = new int[44];
		int index = 0;
		for (int value : CARD_VALUES) {
			for (int copy = 0; copy < 4; copy++) {
				drawOrder[index++] = value;
			}
		}
		Random random = new Random();
		for (int i = drawOrder.length - 1; i > 0; i--) {
			int swapIndex = random.nextInt(i + 1);
			int value = drawOrder[i];
			drawOrder[i] = drawOrder[swapIndex];
			drawOrder[swapIndex] = value;
		}
		initializeCards(drawOrder);
	}
	/**
	 * Restores a deck from a complete draw order and next-card index.
	 *
	 * @param drawOrder all 44 card values in draw order
	 * @param nextCardIndex index of the next card to draw
	 */
	public Deck(int[] drawOrder, int nextCardIndex){
		if (drawOrder == null) {
            throw new IllegalArgumentException("Deck drawOrder cannot be null");
        }
        if (drawOrder.length != 44) {
            throw new IllegalArgumentException("A full deck must contain 44 cards");
        }
        if(nextCardIndex < 0 || nextCardIndex > drawOrder.length){
            throw new IllegalArgumentException("Invalid card index");
        }
		validateDrawOrder(drawOrder);
		initializeCards(drawOrder);
		sumCard = 43 - nextCardIndex;
	}

	private void initializeCards(int[] drawOrder) {
		for (int i = 0; i < drawOrder.length; i++) {
			cards[43 - i] = createCard(drawOrder[i]);
		}
	}

	private void validateDrawOrder(int[] drawOrder) {
		int[] counts = new int[13];
		for (int value : drawOrder) {
			if (value < 1 || value >= counts.length || value == 9) {
				throw new IllegalArgumentException("Invalid card number: " + value);
			}
			counts[value]++;
		}
		for (int value : CARD_VALUES) {
			if (counts[value] != 4) {
				throw new IllegalArgumentException(
						"A full deck must contain four cards of every type");
			}
		}
	}

	private Card createCard(int number) {
		switch (number) {
			case 1: return new NumberOne();
			case 2: return new NumberTwo();
			case 3: return new NumberThree();
			case 4: return new NumberFour();
			case 5: return new NumberFive();
			case 6: return new SorryCard();
			case 7: return new NumberSeven();
			case 8: return new NumberEight();
			case 10: return new NumberTen();
			case 11: return new NumberEleven();
			case 12: return new NumberTwelve();
			default:
				throw new IllegalArgumentException("Invalid card number: " + number);
		}
	}
	/**

	 * <b>Observer</b>:Return the sum of the non use cards

	 *

	 * @return size of Deck

	 */

	public int sum() {

		return this.sumCard + 1;

	}



	/**

	 * <b>Observer</b>:Return the top card of the non use card of the deck

	 *

	 * @return the card

	 */

	public Card getCard() {

		int p = sumCard;

		return cards[p];

	}


	/**
	 * Returns the complete deck without exposing its internal array.
	 *
	 * @return a copy of all 44 card values in draw order
	 */
	public int[] getDrawOrder() {
		int[] drawOrder = new int[cards.length];
		for (int i = 0; i < cards.length; i++) {
			drawOrder[i] = cards[cards.length - 1 - i].getCard();
		}
		return drawOrder;
	}

	/**
	 * Returns the current position within the draw order.
	 *
	 * @return index of the next card to draw
	 */
	public int getNextCardIndex() {
		return cards.length - sum();
	}
	/**

	 * <b>Observer</b>:Remove cards of the deck

	 */

	public void removeCard() {

		sumCard--;

	}

}
