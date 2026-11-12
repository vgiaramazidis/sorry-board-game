package model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeckTest {
    @Test
    public void shuffledDeckContainsFourOfEveryAllowedCard() {
        Deck deck = new Deck();
        int[] counts = new int[13];

        for (int card : deck.getDrawOrder()) {
            counts[card]++;
        }

        for (int card = 1; card <= 12; card++) {
            if (card != 9) {
                assertEquals("card " + card, 4, counts[card]);
            }
        }
        assertEquals(0, counts[9]);
        assertEquals(44, deck.sum());
    }

    @Test(expected = IllegalArgumentException.class)
    public void restoredDeckRejectsWrongCardDistribution() {
        new Deck(new int[44], 0);
    }
}
