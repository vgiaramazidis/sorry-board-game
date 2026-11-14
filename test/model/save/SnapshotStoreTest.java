package model.save;

import controller.Controller;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import static org.junit.Assume.assumeFalse;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

    @Test(expected = IOException.class)
    public void rejectsUnsupportedVersion() throws IOException {
        Path file = temporaryFolder.newFile("future.properties").toPath();
        Files.write(file, "version=99\n".getBytes(StandardCharsets.UTF_8));

        SnapshotStore.load(file);
    }

    @Test(expected = IOException.class)
    public void rejectsMalformedSnapshotValues() throws IOException {
        Path file = temporaryFolder.newFile("malformed.properties").toPath();
        Files.write(file, ("version=2\nplayerCount=two\n")
                .getBytes(StandardCharsets.UTF_8));

        SnapshotStore.load(file);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerRejectsSnapshotForDifferentPlayerCount() {
        Controller twoPlayerGame = new Controller(2);
        GameSnapshot threePlayerSave = new Controller(3).createSnapshot(0);

        twoPlayerGame.restoreSnapshot(threePlayerSave);
    }

    @Test
    public void reportsStorageFailureWhenParentCannotBeUsedAsDirectory() throws IOException {
        Path regularFile = temporaryFolder.newFile("not-a-directory").toPath();
        Path impossibleTarget = regularFile.resolve("game.properties");

        try {
            SnapshotStore.save(impossibleTarget, new Controller(2).createSnapshot(0));
            fail("Expected an IOException for an unusable save directory");
        } catch (IOException expected) {
            // The application catches this and shows its Save failed dialog.
        }
    }

    @Test
    public void reportsPermissionFailureForReadOnlySaveDirectory() throws IOException {
        Path directory = temporaryFolder.newFolder("read-only").toPath();
        Set<PosixFilePermission> originalPermissions;
        try {
            originalPermissions = Files.getPosixFilePermissions(directory);
        } catch (UnsupportedOperationException exception) {
            return;
        }

        try {
            Files.setPosixFilePermissions(
                    directory, PosixFilePermissions.fromString("r-x------"));
            assumeFalse("Current user can bypass POSIX write permissions",
                    Files.isWritable(directory));
            try {
                SnapshotStore.save(
                        directory.resolve("game.properties"),
                        new Controller(2).createSnapshot(0));
                fail("Expected an IOException for a read-only save directory");
            } catch (IOException expected) {
                // The UI converts this checked failure into its Save failed dialog.
            }
        } finally {
            Files.setPosixFilePermissions(directory, originalPermissions);
        }
    }
}
