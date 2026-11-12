package model.save;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/** Reads and atomically writes the versioned properties-based save format. */
public final class SnapshotStore {
    private SnapshotStore() {
        // Utility class.
    }

    /**
     * Writes a snapshot through a temporary sibling file and replaces the target.
     *
     * @param file destination save file
     * @param snapshot validated game state to write
     * @throws IOException if the file cannot be written or replaced
     */
    public static void save(Path file, GameSnapshot snapshot) throws IOException {
        if (file == null || snapshot == null) {
            throw new IllegalArgumentException("File and snapshot cannot be null");
        }

        Properties properties = new Properties();
        properties.setProperty("version", "2");
        properties.setProperty("playerCount", Integer.toString(snapshot.getPlayerCount()));
        properties.setProperty(
                "nextPlayerIndex", Integer.toString(snapshot.getNextPlayerIndex()));
        properties.setProperty(
                "nextCardIndex", Integer.toString(snapshot.getNextCardIndex()));

        int[] deckOrder = snapshot.getDeckOrder();
        StringBuilder serializedDeck = new StringBuilder();
        for (int i = 0; i < deckOrder.length; i++) {
            if (i > 0) {
                serializedDeck.append(',');
            }
            serializedDeck.append(deckOrder[i]);
        }
        properties.setProperty("deckOrder", serializedDeck.toString());

        List<PawnPosition> pawnPositions = snapshot.getPawnPositions();
        properties.setProperty("pawnCount", Integer.toString(pawnPositions.size()));
        for (int i = 0; i < pawnPositions.size(); i++) {
            PawnPosition position = pawnPositions.get(i);
            String value = position.getPlayerIndex() + ","
                    + position.getPawnIndex() + ","
                    + position.getLocationType().name() + ","
                    + position.getLocationIndex();
            properties.setProperty("pawn." + i, value);
        }

        Path target = file.toAbsolutePath().normalize();
        Path parent = target.getParent();
        if (parent == null) {
            throw new IOException("Save file must have a parent directory");
        }
        Files.createDirectories(parent);
        Path temporary = Files.createTempFile(
                parent, target.getFileName().toString(), ".tmp");
        try {
            try (OutputStream output = Files.newOutputStream(temporary)) {
                properties.store(output, "Game snapshot");
            }
            try {
                Files.move(temporary, target,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /**
     * Loads and validates a version 1 or version 2 snapshot.
     *
     * @param file snapshot file to read
     * @return validated snapshot
     * @throws IOException if the file is missing, malformed, or inconsistent
     */
    public static GameSnapshot load(Path file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(file)) {
            properties.load(input);
        }

        String version = properties.getProperty("version");
        if (!"1".equals(version) && !"2".equals(version)) {
            throw new IOException("Missing or unsupported snapshot version");
        }

        try {
            int playerCount = "1".equals(version)
                    ? 2 : parseRequiredInt(properties, "playerCount");
            int nextPlayerIndex = parseRequiredInt(properties, "nextPlayerIndex");
            int nextCardIndex = parseRequiredInt(properties, "nextCardIndex");
            String[] serializedDeck = requireProperty(properties, "deckOrder").split(",", -1);
            if (serializedDeck.length != 44) {
                throw new IOException("Snapshot deck must contain 44 cards");
            }

            int[] deckOrder = new int[serializedDeck.length];
            for (int i = 0; i < serializedDeck.length; i++) {
                deckOrder[i] = Integer.parseInt(serializedDeck[i]);
            }

            int pawnCount = parseRequiredInt(properties, "pawnCount");
            if (pawnCount < 1 || pawnCount > 8) {
                throw new IOException("Invalid snapshot pawn count");
            }

            List<PawnPosition> pawnPositions = new ArrayList<>();
            for (int i = 0; i < pawnCount; i++) {
                String[] fields = requireProperty(properties, "pawn." + i).split(",", -1);
                if (fields.length != 4) {
                    throw new IOException("Invalid pawn record: pawn." + i);
                }

                int playerIndex = Integer.parseInt(fields[0]);
                int pawnIndex = Integer.parseInt(fields[1]);
                LocationType locationType = LocationType.valueOf(fields[2]);
                int locationIndex = Integer.parseInt(fields[3]);
                pawnPositions.add(new PawnPosition(
                        playerIndex, pawnIndex, locationType, locationIndex));
            }

            return new GameSnapshot(
                    pawnPositions, deckOrder, nextCardIndex, nextPlayerIndex, playerCount);
        } catch (IllegalArgumentException exception) {
            throw new IOException("Snapshot contains invalid data", exception);
        }
    }

    private static String requireProperty(Properties properties, String key)
            throws IOException {
        String value = properties.getProperty(key);
        if (value == null || value.isEmpty()) {
            throw new IOException("Missing snapshot property: " + key);
        }
        return value;
    }

    private static int parseRequiredInt(Properties properties, String key)
            throws IOException {
        try {
            return Integer.parseInt(requireProperty(properties, key));
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid integer snapshot property: " + key, exception);
        }
    }
}
