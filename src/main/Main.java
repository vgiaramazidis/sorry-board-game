package main;

import view.Board;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Starts the Swing application and collects the new-game options.
 */
public class Main {
	private Main() {
		// Application entry-point class.
	}

	/**
	 * Opens the player-count and first-player dialogs on the Swing event thread.
	 *
	 * @param args command-line arguments; currently unused
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			String[] playerCounts = {"2", "3", "4"};
			String selected = (String) JOptionPane.showInputDialog(
					null,
					"Select the number of players:",
					"New Game",
					JOptionPane.QUESTION_MESSAGE,
					null,
					playerCounts,
					playerCounts[0]);
			if (selected != null) {
				int playerCount = Integer.parseInt(selected);
				String[] firstPlayerOptions = new String[playerCount + 1];
				firstPlayerOptions[0] = "Random";
				for (int i = 0; i < playerCount; i++) {
					firstPlayerOptions[i + 1] = "Player " + (i + 1);
				}
				String firstPlayer = (String) JOptionPane.showInputDialog(
						null,
						"Choose who plays first:",
						"First Player",
						JOptionPane.QUESTION_MESSAGE,
						null,
						firstPlayerOptions,
						firstPlayerOptions[0]);
				if (firstPlayer != null) {
					int firstPlayerIndex = "Random".equals(firstPlayer)
							? ThreadLocalRandom.current().nextInt(playerCount)
							: Integer.parseInt(firstPlayer.substring("Player ".length())) - 1;
					new Board(playerCount, firstPlayerIndex);
				}
			}
		});
	}
}
