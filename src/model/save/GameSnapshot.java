package model.save;

import java.util.ArrayList;
import java.util.List;

public class GameSnapshot {
    private final List<PawnPosition> pawnPositions;
    private final int[] deckOrder;
    private final int nextCardIndex;
    private final int nextPlayerIndex;
    private final int playerCount;

    public GameSnapshot(
            List<PawnPosition> pawnPositions, int[] deckOrder,
            int nextCardIndex, int nextPlayerIndex) {
        this(pawnPositions, deckOrder, nextCardIndex, nextPlayerIndex,
                inferPlayerCount(pawnPositions));
    }

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
    public List<PawnPosition> getPawnPositions(){
        return  new ArrayList<>(this.pawnPositions);
    }

    public int[] getDeckOrder(){
        return this.deckOrder.clone();
    }

    public int getNextCardIndex(){
        return this.nextCardIndex;
    }

    public int getNextPlayerIndex(){
        return this.nextPlayerIndex;
    }

    public int getPlayerCount() {
        return playerCount;
    }

}