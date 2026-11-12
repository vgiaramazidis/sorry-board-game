package controller;

/**
 * Keeps track of which player draws the next card.
 *
 * <p>The UI tells this class when a turn finishes and whether the same player
 * must play again. Keeping this state outside the Swing window makes the turn
 * rules easy to test and avoids off-by-one counter logic in the view.</p>
 */
public final class TurnManager {
    private final int playerCount;
    private int nextPlayerIndex;

    public TurnManager(int playerCount, int firstPlayerIndex) {
        if (playerCount < 2 || playerCount > 4) {
            throw new IllegalArgumentException("Player count must be between 2 and 4");
        }
        this.playerCount = playerCount;
        setNextPlayerIndex(firstPlayerIndex);
    }

    public int beginTurn() {
        return nextPlayerIndex;
    }

    public void finishTurn(boolean repeatTurn) {
        if (!repeatTurn) {
            nextPlayerIndex = (nextPlayerIndex + 1) % playerCount;
        }
    }

    public int getNextPlayerIndex() {
        return nextPlayerIndex;
    }

    public void setNextPlayerIndex(int playerIndex) {
        if (playerIndex < 0 || playerIndex >= playerCount) {
            throw new IllegalArgumentException("Invalid player index");
        }
        nextPlayerIndex = playerIndex;
    }
}
