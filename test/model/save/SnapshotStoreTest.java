package model.save;

import controller.Controller;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SnapshotStoreTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void roundTripPreservesGameState() throws IOException {
        Controller original = new Controller(4);
        GameSnapshot expected = original.createSnapshot(3);
        Path file = temporaryFolder.newFile("game.properties").toPath();

        SnapshotStore.save(file, expected);
        GameSnapshot actual = SnapshotStore.load(file);

        assertEquals(4, actual.getPlayerCount());
        assertEquals(3, actual.getNextPlayerIndex());
        assertEquals(expected.getNextCardIndex(), actual.getNextCardIndex());
        assertArrayEquals(expected.getDeckOrder(), actual.getDeckOrder());
        assertEquals(expected.getPawnPositions().size(), actual.getPawnPositions().size());
    }

    @Test(expected = IOException.class)
    public void rejectsMissingVersion() throws IOException {
        Path file = temporaryFolder.newFile("invalid.properties").toPath();
        Files.write(file, "playerCount=2\n".getBytes(StandardCharsets.UTF_8));

        SnapshotStore.load(file);
    }
}
