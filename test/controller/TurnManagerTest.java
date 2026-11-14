package controller;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TurnManagerTest {
    @Test
    public void acceptsEveryExplicitFirstPlayerChoice() {
        for (int playerCount = 2; playerCount <= 4; playerCount++) {
            for (int firstPlayer = 0; firstPlayer < playerCount; firstPlayer++) {
                TurnManager turns = new TurnManager(playerCount, firstPlayer);
                assertEquals(firstPlayer, turns.beginTurn());
            }
        }
    }

    @Test
    public void advancesThroughAllPlayersAndWraps() {
        TurnManager turns = new TurnManager(3, 1);

        assertEquals(1, turns.beginTurn());
        turns.finishTurn(false);
        assertEquals(2, turns.beginTurn());
        turns.finishTurn(false);
        assertEquals(0, turns.beginTurn());
    }

    @Test
    public void repeatTurnKeepsTheCurrentPlayer() {
        TurnManager turns = new TurnManager(4, 3);

        turns.finishTurn(true);

        assertEquals(3, turns.getNextPlayerIndex());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidFirstPlayer() {
        new TurnManager(2, 2);
    }
}
