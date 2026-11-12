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

    /**
     * Creates turn state for a new or restored game.
     *
     * @param playerCount number of players, from 2 through 4
     * @param firstPlayerIndex zero-based player who draws next
     * @throws IllegalArgumentException if either argument is outside its valid range
     */
    public TurnManager(int playerCount, int firstPlayerIndex) {
        if (playerCount < 2 || playerCount > 4) {
            throw new IllegalArgumentException("Player count must be between 2 and 4");
        }
        this.playerCount = playerCount;
        setNextPlayerIndex(firstPlayerIndex);
    }

    /**
     * Returns the player whose turn is beginning.
     *
     * @return zero-based current player index
     */
    public int beginTurn() {
        return nextPlayerIndex;
    }

    /**
     * Completes a turn and optionally keeps the same player for another draw.
     *
     * @param repeatTurn {@code true} when the card grants another turn
     */
    public void finishTurn(boolean repeatTurn) {
        if (!repeatTurn) {
            nextPlayerIndex = (nextPlayerIndex + 1) % playerCount;
        }
    }

    /**
     * Returns the player who will draw the next card.
     *
     * @return zero-based next-player index
     */
    public int getNextPlayerIndex() {
        return nextPlayerIndex;
    }

    /**
     * Changes the player who will draw next, as required when restoring a game.
     *
     * @param playerIndex zero-based player index
     * @throws IllegalArgumentException if the index is outside this game
     */
    public void setNextPlayerIndex(int playerIndex) {
        if (playerIndex < 0 || playerIndex >= playerCount) {
            throw new IllegalArgumentException("Invalid player index");
        }
        nextPlayerIndex = playerIndex;
    }
}
