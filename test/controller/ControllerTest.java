package controller;

import model.Pawn;
import model.save.GameSnapshot;
import model.save.LocationType;
import model.save.PawnPosition;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ControllerTest {
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
