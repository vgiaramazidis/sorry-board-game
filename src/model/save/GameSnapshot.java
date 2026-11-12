package model.save;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable, validated representation of all state needed to resume a game.
 */
public class GameSnapshot {
    private final List<PawnPosition> pawnPositions;
    private final int[] deckOrder;
    private final int nextCardIndex;
    private final int nextPlayerIndex;
    private final int playerCount;

    /**
     * Creates a snapshot and infers the player count from its pawn records.
     *
     * @param pawnPositions logical location of every pawn
     * @param deckOrder complete 44-card draw order
     * @param nextCardIndex index of the next card to draw
     * @param nextPlayerIndex zero-based player who draws next
     */
    public GameSnapshot(
            List<PawnPosition> pawnPositions, int[] deckOrder,
            int nextCardIndex, int nextPlayerIndex) {
        this(pawnPositions, deckOrder, nextCardIndex, nextPlayerIndex,
                inferPlayerCount(pawnPositions));
    }

    /**
     * Creates and validates a complete snapshot.
     *
     * @param pawnPositions logical location of every pawn
     * @param deckOrder complete 44-card draw order
     * @param nextCardIndex index of the next card to draw
     * @param nextPlayerIndex zero-based player who draws next
     * @param playerCount number of players, from 2 through 4
     * @throws IllegalArgumentException if the supplied state is inconsistent
     */
    public GameSnapshot(
            List<PawnPosition> pawnPositions, int[] deckOrder,
            int nextCardIndex, int nextPlayerIndex, int playerCount) {
        if(pawnPositions == null || deckOrder == null){
            throw new IllegalArgumentException("Snapshot data cannot be null");
        }
        if (playerCount < 2 || playerCount > 4) {
            throw new IllegalArgumentException("Player count must be between 2 and 4");
        }
        if(nextPlayerIndex < 0 || nextPlayerIndex >= playerCount){
            throw new IllegalArgumentException("Invalid player index");
        }
        if (deckOrder.length != 44) {
            throw new IllegalArgumentException("A full deck must contain 44 cards");
        }
        if(nextCardIndex < 0 || nextCardIndex > deckOrder.length){
            throw new IllegalArgumentException("Invalid card index");
        }
        if (pawnPositions.size() != playerCount * 2) {
            throw new IllegalArgumentException(
                    "Snapshot must contain exactly two pawns per player");
        }
        boolean[][] seenPawns = new boolean[playerCount][2];
        for (PawnPosition position : pawnPositions) {
            if (position == null) {
                throw new IllegalArgumentException("Pawn positions cannot contain null");
            }

            int player = position.getPlayerIndex();
            int pawn = position.getPawnIndex();
            if (player >= playerCount) {
                throw new IllegalArgumentException(
                        "Pawn position exceeds snapshot player count");
            }
            if (seenPawns[player][pawn]) {
                throw new IllegalArgumentException("Duplicate pawn position");
            }
            seenPawns[player][pawn] = true;
        }
        for (boolean[] playerPawns : seenPawns) {
            for (boolean pawnSeen : playerPawns) {
                if (!pawnSeen) {
                    throw new IllegalArgumentException(
                            "Snapshot is missing a pawn position");
                }
            }
        }

        int[] counts = new int[13];

        for (int card : deckOrder) {
            if (card < 1 || card > 12 || card == 9) {
                throw new IllegalArgumentException("Invalid card number: " + card);
            }
            counts[card]++;
        }

        for (int card = 1; card <= 12; card++) {
            if (card == 9) {
                continue;
            }
            if (counts[card] != 4) {
                throw new IllegalArgumentException("A full deck must contain four of card " + card);
            }
        }
        this.pawnPositions = new ArrayList<>(pawnPositions);
        this.deckOrder = deckOrder.clone();
        this.nextCardIndex=nextCardIndex;
        this.nextPlayerIndex=nextPlayerIndex;
        this.playerCount=playerCount;
    }

    private static int inferPlayerCount(List<PawnPosition> pawnPositions) {
        if (pawnPositions == null || pawnPositions.isEmpty()) {
            throw new IllegalArgumentException("Snapshot must contain pawn positions");
        }
        int playerCount = 0;
        for (PawnPosition position : pawnPositions) {
            if (position == null) {
                throw new IllegalArgumentException("Pawn positions cannot contain null");
            }
            playerCount = Math.max(playerCount, position.getPlayerIndex() + 1);
        }
        return playerCount;
    }
    /**
     * Returns the saved pawn positions without exposing internal state.
     *
     * @return defensive copy of all saved pawn positions
     */
    public List<PawnPosition> getPawnPositions(){
        return  new ArrayList<>(this.pawnPositions);
    }

    /**
     * Returns the saved deck without exposing internal state.
     *
     * @return defensive copy of the complete deck order
     */
    public int[] getDeckOrder(){
        return this.deckOrder.clone();
    }

    /**
     * Returns the saved draw position.
     *
     * @return index of the next card to draw
     */
    public int getNextCardIndex(){
        return this.nextCardIndex;
    }

    /**
     * Returns the saved turn position.
     *
     * @return zero-based player who draws next
     */
    public int getNextPlayerIndex(){
        return this.nextPlayerIndex;
    }

    /**
     * Returns the saved multiplayer size.
     *
     * @return number of players in the saved game
     */
    public int getPlayerCount() {
        return playerCount;
    }

}
