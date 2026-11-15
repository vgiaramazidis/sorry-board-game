package controller;

import model.Pawn;
import model.Card.Card;
import model.Card.MoveDirection;
import model.save.GameSnapshot;
import model.save.LocationType;
import model.save.PawnPosition;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ControllerTest {
    @Test
    public void completeGameUsesTheRealDrawMoveFoldAndTurnFlow() {
        Controller controller = new Controller(2);
        GameSnapshot initial = controller.createSnapshot(0);
        int[] deckOrder = interleavedDeck();
        controller.restoreSnapshot(new GameSnapshot(
                initial.getPawnPositions(), deckOrder, 0, 0, 2));
        TurnManager turns = new TurnManager(2, 0);
        boolean[] seenCards = new boolean[13];
        int completedCards = 0;

        while (controller.win() == 0 && completedCards < 5000) {
            int currentPlayer = turns.beginTurn();
            controller.setSelectPlayer(currentPlayer);
            if (controller.tableIsEmpty()) {
                GameSnapshot state = controller.createSnapshot(currentPlayer);
                controller.restoreSnapshot(new GameSnapshot(
                        state.getPawnPositions(), deckOrder, 0, currentPlayer, 2));
            }

            Card card = controller.pickedCard();
            seenCards[card.getCard()] = true;
            boolean drawAgain = playCurrentCard(controller, card.getCard());
            controller.finishCurrentCard();
            turns.finishTurn(card.getCard() == 2 || drawAgain);
            completedCards++;
        }

        assertTrue("Complete game did not reach a winner", completedCards < 5000);
        assertTrue(controller.win() == 1 || controller.win() == 2);
        int[] cardValues = {1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12};
        for (int cardValue : cardValues) {
            assertTrue("Complete game never drew card " + cardValue, seenCards[cardValue]);
        }
    }

    @Test
    public void scriptedGamesReachHomeAndDetectWinnerForEveryPlayerCount() {
        for (int playerCount = 2; playerCount <= 4; playerCount++) {
            Controller controller = new Controller(playerCount);
            int winnerIndex = playerCount - 1;
            TurnManager turns = new TurnManager(playerCount, 0);
            int expectedPlayer = 0;
            int completedTurns = 0;

            while (controller.win() == 0 && completedTurns < 1000) {
                int currentPlayer = turns.beginTurn();
                assertEquals(expectedPlayer, currentPlayer);

                if (currentPlayer == winnerIndex) {
                    GameSnapshot state = controller.createSnapshot(currentPlayer);
                    controller.restoreSnapshot(new GameSnapshot(
                            state.getPawnPositions(), deckStartingWith(1), 0,
                            currentPlayer, playerCount));
                    controller.setSelectPlayer(currentPlayer);

                    int pawnIndex = controller.getChoosePawn(0, currentPlayer).isHome() ? 1 : 0;
                    assertEquals(1, controller.makeMove(false, pawnIndex));
                }

                turns.finishTurn(false);
                expectedPlayer = (expectedPlayer + 1) % playerCount;
                completedTurns++;
            }

            assertTrue("Scripted game did not finish", completedTurns < 1000);
            assertTrue(controller.getChoosePawn(0, winnerIndex).isHome());
            assertTrue(controller.getChoosePawn(1, winnerIndex).isHome());
            assertEquals(winnerIndex + 1, controller.win());
        }
    }

    @Test
    public void createsTwoThreeAndFourPlayerGames() {
        for (int count = 2; count <= 4; count++) {
            Controller controller = new Controller(count);
            assertEquals(count, controller.getPlayerCount());
            assertEquals(count, controller.getPlayer().length);
            for (int player = 0; player < count; player++) {
                assertTrue(controller.getChoosePawn(0, player).isStart());
                assertTrue(controller.getChoosePawn(1, player).isStart());
            }
        }
    }

    @Test
    public void cardElevenCanSwapWhenForwardElevenIsBlockedByHomeBoundary() {
        Controller controller = controllerWith(2, 11,
                position(0, 0, LocationType.OUTER, 0),
                position(0, 1, LocationType.HOME, 1),
                position(1, 0, LocationType.OUTER, 10),
                position(1, 1, LocationType.START, 1));
        controller.setSelectPlayer(0);

        assertFalse(controller.canMovePawn(0));
        assertTrue(controller.canSwapWithEnemy(0, 1, 0));
        assertTrue(controller.hasLegalMove());
        assertEquals(1, controller.makeMove(0, 1, 0));
        assertSame(controller.getBoxes()[10], controller.getChoosePawn(0, 0).getCurrentSquare());
        assertSame(controller.getBoxes()[0], controller.getChoosePawn(0, 1).getCurrentSquare());
    }

    @Test
    public void cardSevenCanSplitAcrossBothPawns() {
        Controller controller = controllerWith(2, 7,
                position(0, 0, LocationType.OUTER, 30),
                position(0, 1, LocationType.OUTER, 20),
                position(1, 0, LocationType.START, 0),
                position(1, 1, LocationType.START, 1));
        controller.setSelectPlayer(0);

        assertTrue(controller.canSplitSeven());
        assertEquals(1, controller.makeSevenSplit(0, 3));
        assertSame(controller.getBoxes()[27], controller.getChoosePawn(0, 0).getCurrentSquare());
        assertSame(controller.getBoxes()[16], controller.getChoosePawn(1, 0).getCurrentSquare());
    }

    @Test
    public void cardSevenSplitMayUseASquareVacatedByTheOtherPawn() {
        Controller controller = controllerWith(2, 7,
                position(0, 0, LocationType.OUTER, 10),
                position(0, 1, LocationType.OUTER, 7),
                position(1, 0, LocationType.START, 0),
                position(1, 1, LocationType.START, 1));
        controller.setSelectPlayer(0);
        assertEquals(7, controller.pickedCard().getCard());

        assertTrue(controller.canSplitSeven());
        assertEquals(1, controller.makeSevenSplit(1, 4));
        assertSame(controller.getBoxes()[7], controller.getChoosePawn(0, 0).getCurrentSquare());
        assertSame(controller.getBoxes()[3], controller.getChoosePawn(1, 0).getCurrentSquare());
    }

    @Test
    public void cardTenSupportsBothForwardAndBackwardChoices() {
        Controller forward = controllerWith(2, 10,
                position(0, 0, LocationType.OUTER, 30),
                position(0, 1, LocationType.START, 1),
                position(1, 0, LocationType.START, 0),
                position(1, 1, LocationType.START, 1));
        forward.setSelectPlayer(0);
        assertEquals(1, forward.makeMove(false, 0, MoveDirection.FORWARD));
        assertSame(forward.getBoxes()[20], forward.getChoosePawn(0, 0).getCurrentSquare());

        Controller backward = controllerWith(2, 10,
                position(0, 0, LocationType.OUTER, 30),
                position(0, 1, LocationType.START, 1),
                position(1, 0, LocationType.START, 0),
                position(1, 1, LocationType.START, 1));
        backward.setSelectPlayer(0);
        assertEquals(1, backward.makeMove(false, 0, MoveDirection.BACKWARD));
        assertSame(backward.getBoxes()[31], backward.getChoosePawn(0, 0).getCurrentSquare());
    }

    @Test
    public void drawnCardRemainsTheRuleCardAfterDeckAdvances() {
        Controller controller = allPawnsAtStart(4);
        controller.setSelectPlayer(0);

        assertEquals(4, controller.pickedCard().getCard());
        assertEquals("the next deck card must not control this turn",
                1, controller.getDeck().getCard().getCard());
        assertFalse(controller.hasLegalMove());
        assertTrue(controller.canFoldCurrentCard());
        assertEquals(0, controller.makeMove(false, 0));
        assertTrue(controller.getChoosePawn(0, 0).isStart());
    }

    @Test
    public void startOnlyCardRulesMatchTheAssignment() {
        int[] cards = {1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12};
        boolean[] expectedLegalMove = {
                true, true, false, false, false, false, false, true, false, false, true
        };

        for (int i = 0; i < cards.length; i++) {
            Controller controller = allPawnsAtStart(cards[i]);
            controller.setSelectPlayer(0);
            assertEquals(cards[i], controller.pickedCard().getCard());
            assertEquals("card " + cards[i], expectedLegalMove[i], controller.hasLegalMove());
            assertEquals("Fold for card " + cards[i], !expectedLegalMove[i],
                    controller.canFoldCurrentCard());
        }
    }

    @Test
    public void cardTenRequiresBackwardOneWhenForwardTenIsImpossible() {
        Controller controller = controllerWith(2, 10,
                position(0, 0, LocationType.OUTER, 0),
                position(0, 1, LocationType.HOME, 1),
                position(1, 0, LocationType.START, 0),
                position(1, 1, LocationType.START, 1));
        controller.setSelectPlayer(0);
        assertEquals(10, controller.pickedCard().getCard());

        assertFalse(controller.canMovePawn(0, MoveDirection.FORWARD));
        assertTrue(controller.canMovePawn(0, MoveDirection.BACKWARD));
        assertFalse(controller.canFoldCurrentCard());
        assertEquals(1, controller.makeMove(false, 0, MoveDirection.BACKWARD));
        assertSame(controller.getBoxes()[1], controller.getChoosePawn(0, 0).getCurrentSquare());
    }

    @Test
    public void cardElevenSwapIsOptionalWhenForwardMoveIsImpossible() {
        Controller controller = controllerWith(2, 11,
                position(0, 0, LocationType.OUTER, 0),
                position(0, 1, LocationType.HOME, 1),
                position(1, 0, LocationType.OUTER, 10),
                position(1, 1, LocationType.START, 1));
        controller.setSelectPlayer(0);
        assertEquals(11, controller.pickedCard().getCard());

        assertFalse(controller.canMovePawn(0, MoveDirection.FORWARD));
        assertTrue(controller.canSwapWithEnemy(0, 1, 0));
        assertTrue(controller.hasLegalMove());
        assertTrue(controller.canFoldCurrentCard());
    }

    @Test
    public void cardElevenCannotFoldWhenForwardMoveIsAvailable() {
        Controller controller = controllerWith(2, 11,
                position(0, 0, LocationType.OUTER, 30),
                position(0, 1, LocationType.START, 1),
                position(1, 0, LocationType.OUTER, 10),
                position(1, 1, LocationType.START, 1));
        controller.setSelectPlayer(0);
        assertEquals(11, controller.pickedCard().getCard());

        assertTrue(controller.canMovePawn(0, MoveDirection.FORWARD));
        assertFalse(controller.canFoldCurrentCard());
    }

    @Test
    public void cardTwelveMovesSelectedPawnForward() {
        Controller controller = controllerWith(2, 12,
                position(0, 0, LocationType.OUTER, 30),
                position(0, 1, LocationType.START, 1),
                position(1, 0, LocationType.START, 0),
                position(1, 1, LocationType.START, 1));
        controller.setSelectPlayer(0);

        assertEquals(1, controller.makeMove(false, 0));
        assertSame(controller.getBoxes()[18], controller.getChoosePawn(0, 0).getCurrentSquare());
    }

    @Test
    public void sorryCardReplacesChosenOpponentInMultiplayerGame() {
        Controller controller = controllerWith(3, 6,
                position(0, 0, LocationType.START, 0),
                position(0, 1, LocationType.START, 1),
                position(1, 0, LocationType.START, 0),
                position(1, 1, LocationType.START, 1),
                position(2, 0, LocationType.OUTER, 30),
                position(2, 1, LocationType.START, 1));
        controller.setSelectPlayer(0);

        assertTrue(controller.canSwapWithEnemy(0, 2, 0));
        assertEquals(1, controller.makeMove(0, 2, 0));
        assertSame(controller.getBoxes()[30], controller.getChoosePawn(0, 0).getCurrentSquare());
        assertTrue(controller.getChoosePawn(0, 2).isStart());
    }

    @Test
    public void cardThreeMovesIntoSquareVacatedByOtherPawn() {
        Controller controller = controllerWith(2, 3,
                position(0, 0, LocationType.OUTER, 10),
                position(0, 1, LocationType.OUTER, 7),
                position(1, 0, LocationType.START, 0),
                position(1, 1, LocationType.START, 1));
        controller.setSelectPlayer(0);

        assertEquals(1, controller.makeMove(true, 0));
        assertSame(controller.getBoxes()[7], controller.getChoosePawn(0, 0).getCurrentSquare());
        assertSame(controller.getBoxes()[4], controller.getChoosePawn(1, 0).getCurrentSquare());
    }

    @Test
    public void cardThreeMovesOnlyThePawnWithALegalDestination() {
        Controller controller = controllerWith(2, 3,
                position(0, 0, LocationType.OUTER, 10),
                position(0, 1, LocationType.START, 1),
                position(1, 0, LocationType.START, 0),
                position(1, 1, LocationType.START, 1));
        controller.setSelectPlayer(0);

        assertEquals(1, controller.makeMove(true, 0));
        assertSame(controller.getBoxes()[7], controller.getChoosePawn(0, 0).getCurrentSquare());
        assertTrue(controller.getChoosePawn(1, 0).isStart());
    }

    @Test
    public void landingOnEnemyCapturesIt() {
        Controller controller = controllerWith(2, 1,
                position(0, 0, LocationType.OUTER, 10),
                position(0, 1, LocationType.START, 1),
                position(1, 0, LocationType.OUTER, 9),
                position(1, 1, LocationType.START, 1));
        controller.setSelectPlayer(0);

        assertEquals(1, controller.makeMove(false, 0));

        assertSame(controller.getBoxes()[9], controller.getChoosePawn(0, 0).getCurrentSquare());
        assertTrue(controller.getChoosePawn(0, 1).isStart());
    }

    @Test
    public void landingOnOpponentSlideBumpsPawnsAndMovesToEnd() {
        Controller controller = controllerWith(2, 1,
                position(0, 0, LocationType.OUTER, 22),
                position(0, 1, LocationType.START, 1),
                position(1, 0, LocationType.OUTER, 19),
                position(1, 1, LocationType.START, 1));
        controller.setSelectPlayer(0);

        assertEquals(1, controller.makeMove(false, 0));

        assertSame(controller.getBoxes()[17], controller.getChoosePawn(0, 0).getCurrentSquare());
        assertTrue(controller.getChoosePawn(0, 1).isStart());
    }

    @Test
    public void exactSafetyMoveEntersAssignedHomeSlot() {
        Controller controller = controllerWith(2, 1,
                position(0, 0, LocationType.SAFETY, 4),
                position(0, 1, LocationType.START, 1),
                position(1, 0, LocationType.START, 0),
                position(1, 1, LocationType.START, 1));
        controller.setSelectPlayer(0);

        assertEquals(1, controller.makeMove(false, 0));
        assertTrue(controller.getChoosePawn(0, 0).isHome());
        assertEquals(0, controller.win());
    }

    @Test
    public void backwardFourWrapsAcrossTrackBoundary() {
        Controller controller = controllerWith(2, 4,
                position(0, 0, LocationType.OUTER, 58),
                position(0, 1, LocationType.START, 1),
                position(1, 0, LocationType.START, 0),
                position(1, 1, LocationType.START, 1));
        controller.setSelectPlayer(0);

        assertEquals(1, controller.makeMove(false, 0));
        assertSame(controller.getBoxes()[2], controller.getChoosePawn(0, 0).getCurrentSquare());
    }

    @Test
    public void winnerRequiresBothPawnsInHome() {
        Controller controller = controllerWith(2, 1,
                position(0, 0, LocationType.HOME, 0),
                position(0, 1, LocationType.HOME, 1),
                position(1, 0, LocationType.START, 0),
                position(1, 1, LocationType.START, 1));

        assertEquals(1, controller.win());
    }

    private static Controller controllerWith(
            int playerCount, int firstCard, PawnPosition... positions) {
        Controller controller = new Controller(playerCount);
        controller.restoreSnapshot(new GameSnapshot(
                asList(positions), deckStartingWith(firstCard), 0, 0, playerCount));
        return controller;
    }

    private static Controller allPawnsAtStart(int firstCard) {
        return controllerWith(2, firstCard,
                position(0, 0, LocationType.START, 0),
                position(0, 1, LocationType.START, 1),
                position(1, 0, LocationType.START, 0),
                position(1, 1, LocationType.START, 1));
    }

    private static boolean playCurrentCard(Controller controller, int cardNumber) {
        if (cardNumber == 8 || cardNumber == 12) {
            for (int pawn = 0; pawn < 2; pawn++) {
                if (controller.canMovePawn(pawn)) {
                    assertEquals(1, controller.makeMove(false, pawn));
                    return false;
                }
            }
            return true;
        }

        if (cardNumber == 3 || cardNumber == 5) {
            if (controller.hasLegalMove()) {
                assertEquals(1, controller.makeMove(true, 0));
            } else {
                assertTrue(controller.canFoldCurrentCard());
            }
            return false;
        }

        if (cardNumber == 7) {
            for (int pawn = 0; pawn < 2; pawn++) {
                if (controller.canMovePawn(pawn)) {
                    assertEquals(1, controller.makeMove(false, pawn));
                    return false;
                }
            }
            for (int firstPawn = 0; firstPawn < 2; firstPawn++) {
                for (int firstSteps = 1; firstSteps <= 6; firstSteps++) {
                    if (controller.makeSevenSplit(firstPawn, firstSteps) == 1) {
                        return false;
                    }
                }
            }
            assertTrue(controller.canFoldCurrentCard());
            return false;
        }

        if (cardNumber == 10) {
            for (MoveDirection direction :
                    new MoveDirection[]{MoveDirection.FORWARD, MoveDirection.BACKWARD}) {
                for (int pawn = 0; pawn < 2; pawn++) {
                    if (controller.canMovePawn(pawn, direction)) {
                        assertEquals(1, controller.makeMove(false, pawn, direction));
                        return false;
                    }
                }
            }
            assertTrue(controller.canFoldCurrentCard());
            return false;
        }

        if (cardNumber == 6 || cardNumber == 11) {
            if (cardNumber == 11) {
                for (int pawn = 0; pawn < 2; pawn++) {
                    if (controller.canMovePawn(pawn, MoveDirection.FORWARD)) {
                        assertEquals(1, controller.makeMove(false, pawn));
                        return false;
                    }
                }
            }
            for (int ownPawn = 0; ownPawn < 2; ownPawn++) {
                for (int enemyPawn = 0; enemyPawn < 2; enemyPawn++) {
                    if (controller.canSwapWithEnemy(ownPawn, enemyPawn)) {
                        assertEquals(1, controller.makeMove(ownPawn, enemyPawn));
                        return false;
                    }
                }
            }
            assertTrue(controller.canFoldCurrentCard());
            return false;
        }

        for (int pawn = 0; pawn < 2; pawn++) {
            if (controller.canMovePawn(pawn)) {
                assertEquals(1, controller.makeMove(false, pawn));
                return false;
            }
        }
        assertTrue(controller.canFoldCurrentCard());
        return false;
    }

    private static int[] interleavedDeck() {
        int[] values = {1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12};
        int[] deck = new int[44];
        int index = 0;
        for (int copy = 0; copy < 4; copy++) {
            for (int value : values) {
                deck[index++] = value;
            }
        }
        return deck;
    }

    private static List<PawnPosition> asList(PawnPosition... positions) {
        List<PawnPosition> list = new ArrayList<>();
        for (PawnPosition position : positions) {
            list.add(position);
        }
        return list;
    }

    private static PawnPosition position(
            int player, int pawn, LocationType type, int index) {
        return new PawnPosition(player, pawn, type, index);
    }

    private static int[] deckStartingWith(int firstCard) {
        int[] values = {1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12};
        int[] deck = new int[44];
        int index = 0;
        for (int value : values) {
            for (int copy = 0; copy < 4; copy++) {
                deck[index++] = value;
            }
        }
        int firstCardIndex = 0;
        while (deck[firstCardIndex] != firstCard) {
            firstCardIndex++;
        }
        int value = deck[0];
        deck[0] = deck[firstCardIndex];
        deck[firstCardIndex] = value;
        return deck;
    }
}
