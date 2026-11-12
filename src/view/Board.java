package view;



import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicButtonUI;

import controller.*;

import java.awt.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.net.URL;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import model.*;
import model.Card.Card;
import model.Card.MoveDirection;
import model.save.GameSnapshot;
import model.save.SnapshotStore;



public class Board {

	private static final int PAWN_ICON_SIZE = 40;

	private Controller control;

	private URL imageURL;

	private Image image;

	private JButton FoldButton;

	private JButton Cards;

	private JLabel drawnCardImage;

	private JLabel CurrentCard;

	private JLabel ReceiveCard;

	private JTextArea infoBox;

	private final JLabel[][] pawnLabels = new JLabel[4][2];

	private final ImageIcon[][] pawnIcons = new ImageIcon[4][2];

	private final ImageIcon[][] rotatedStartPawnIcons = new ImageIcon[4][2];

	private final JLabel[] startLabels = new JLabel[4];

	private final JLabel[] homeLabels = new JLabel[4];

	private final JLabel[][] safetyLabels = new JLabel[4][5];

	private JLabel[] box=new JLabel[60];

	private JLayeredPane GeneralGraph;

	private JDialog winner;

	private JFrame Sorry=new JFrame("SORRY GAME");

	private int count=0;

	private boolean turn,currentCardHasLegalMove;

	private TurnManager turnManager;

	private Card pickCard;




	/**

	 * <b>constructor</b>: Creates a new Window and initializes some buttons and

	 * panels <br />

	 */

	public Board() {
		this(2, 0);
	}

	public Board(int playerCount) {
		this(playerCount, 0);
	}

	public Board(int playerCount, int firstPlayerIndex) {

		imageURL = getClass().getResource("/image/icon.png");

		image = new ImageIcon(imageURL).getImage();

		Sorry.setIconImage(image);

		GeneralGraph = new JLayeredPane();

		control = new Controller(playerCount);
		turnManager = new TurnManager(playerCount, firstPlayerIndex);
		Sorry.setTitle("SORRY GAME - " + playerCount + " Players");

		initComponents();

		initButtons();

		Sorry.add(GeneralGraph);

		Sorry.setSize(1100, 780);
		Sorry.setResizable(false);
		Sorry.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Sorry.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				handleWindowClose();
			}
		});
		Sorry.setJMenuBar(createGameMenu());
		Sorry.setLocationRelativeTo(null);
		Sorry.setVisible(true);

		Cards.addActionListener(new cardsListener());

		FoldButton.addActionListener(new FoldListener());



	}

	private JMenuBar createGameMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		JMenuItem resumeItem = new JMenuItem("Resume Saved Game");
		resumeItem.addActionListener(event -> resumeSavedGame());
		JMenuItem saveExitItem = new JMenuItem("Save and Exit");
		saveExitItem.addActionListener(event -> saveAndExit());
		gameMenu.add(resumeItem);
		gameMenu.add(saveExitItem);
		menuBar.add(gameMenu);
		return menuBar;
	}

	private Path getSnapshotFile() {
		return Paths.get(System.getProperty("user.home"),
				".sorry-game", "snapshot.properties");
	}

	private int getNextPlayerIndex() {
		return turnManager.getNextPlayerIndex();
	}

	private void saveAndExit() {
		if (turn) {
			JOptionPane.showMessageDialog(Sorry,
					"Save and exit is available between turns.");
			return;
		}

		try {
			saveSnapshot(getSnapshotFile());
			Sorry.dispose();
		} catch (IOException | IllegalArgumentException | IllegalStateException
				| SecurityException exception) {
			JOptionPane.showMessageDialog(Sorry,
					"Could not save the game: " + exception.getMessage(),
					"Save failed", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void handleWindowClose() {
		Object[] options = {"Save", "Don't Save", "Cancel"};
		int choice = JOptionPane.showOptionDialog(
				Sorry, "Do you want to save this game before closing?",
				"Close Game", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (choice == 0) {
			if (turn) {
				JOptionPane.showMessageDialog(Sorry,
						"Save is available between turns. Finish or fold this turn first.");
				return;
			}
			try {
				saveSnapshot(getSnapshotFile());
				Sorry.dispose();
			} catch (IOException | IllegalArgumentException | IllegalStateException
					| SecurityException exception) {
				JOptionPane.showMessageDialog(Sorry,
						"Could not save the game: " + exception.getMessage(),
						"Save failed", JOptionPane.ERROR_MESSAGE);
			}
		} else if (choice == 1) {
			Sorry.dispose();
		}
	}

	private void saveSnapshot(Path snapshotFile) throws IOException {
		Files.createDirectories(snapshotFile.toAbsolutePath().getParent());
		GameSnapshot snapshot = control.createSnapshot(getNextPlayerIndex());
		SnapshotStore.save(snapshotFile, snapshot);
	}

	private boolean saveCurrentGameCopy(Path protectedFile) {
		JFileChooser fileChooser = new JFileChooser();
		Path defaultCopy = getSnapshotFile().resolveSibling("current-game.properties");
		fileChooser.setSelectedFile(defaultCopy.toFile());
		fileChooser.setDialogTitle("Save Current Game As");
		fileChooser.setFileFilter(
				new FileNameExtensionFilter("Sorry game saves (*.properties)", "properties"));
		if (fileChooser.showSaveDialog(Sorry) != JFileChooser.APPROVE_OPTION) {
			return false;
		}

		Path selectedFile = fileChooser.getSelectedFile().toPath().toAbsolutePath().normalize();
		if (selectedFile.equals(protectedFile.toAbsolutePath().normalize())) {
			JOptionPane.showMessageDialog(Sorry,
					"Choose a different file so the saved game being loaded is not overwritten.",
					"Choose another file", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		try {
			saveSnapshot(selectedFile);
			return true;
		} catch (IOException | IllegalArgumentException | IllegalStateException
				| SecurityException exception) {
			JOptionPane.showMessageDialog(Sorry,
					"Could not save the current game: " + exception.getMessage(),
					"Save failed", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private void resumeSavedGame() {
		if (turn) {
			JOptionPane.showMessageDialog(Sorry,
					"Resume is available between turns.");
			return;
		}

		JFileChooser fileChooser = new JFileChooser(getSnapshotFile().getParent().toFile());
		fileChooser.setDialogTitle("Choose Saved Game");
		fileChooser.setFileFilter(
				new FileNameExtensionFilter("Sorry game saves (*.properties)", "properties"));
		fileChooser.setSelectedFile(getSnapshotFile().toFile());
		if (fileChooser.showOpenDialog(Sorry) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		Path selectedSave = fileChooser.getSelectedFile().toPath().toAbsolutePath().normalize();

		GameSnapshot snapshot;
		try {
			snapshot = SnapshotStore.load(selectedSave);
			if (snapshot.getPlayerCount() != control.getPlayerCount()) {
				throw new IllegalArgumentException(
						"Saved game uses " + snapshot.getPlayerCount()
								+ " players; restart and choose that player count.");
			}
		} catch (IOException | IllegalArgumentException | SecurityException exception) {
			JOptionPane.showMessageDialog(Sorry,
					"Could not resume the saved game: " + exception.getMessage(),
					"Resume failed", JOptionPane.ERROR_MESSAGE);
			return;
		}

		int saveCurrent = JOptionPane.showConfirmDialog(
				Sorry,
				"Save the current game to another file before loading this saved game?",
				"Save Current Game", JOptionPane.YES_NO_CANCEL_OPTION);
		if (saveCurrent == JOptionPane.CANCEL_OPTION
				|| saveCurrent == JOptionPane.CLOSED_OPTION) {
			return;
		}
		if (saveCurrent == JOptionPane.YES_OPTION
				&& !saveCurrentGameCopy(selectedSave)) {
			return;
		}

		try {
			control.restoreSnapshot(snapshot);
		} catch (IllegalArgumentException | IllegalStateException exception) {
			JOptionPane.showMessageDialog(Sorry,
					"Could not resume the saved game: " + exception.getMessage(),
					"Resume failed", JOptionPane.ERROR_MESSAGE);
			return;
		}

		turnManager.setNextPlayerIndex(snapshot.getNextPlayerIndex());
		turn = false;
		count = 0;
		currentCardHasLegalMove = false;
		pickCard = null;
		Cards.setEnabled(true);
		setFoldButtonEnabled(false);
		if (drawnCardImage != null) {
			GeneralGraph.remove(drawnCardImage);
			drawnCardImage = null;
		}
		control.setSelectPlayer(snapshot.getNextPlayerIndex());
		infoBox.setText(getTurnInfo(snapshot.getNextPlayerIndex()));
		updatePawnLabels();
		GeneralGraph.revalidate();
		GeneralGraph.repaint();
	}


	/**

	 * <b>Transformer</b>:changes the info box and set the select player <br />

	 * <p>

	 * <b>Postcondition:</b>update the info box

	 * </p>

	 *

	 */

	public void changeName() {
		int playerIndex = turnManager.beginTurn();
		control.setSelectPlayer(playerIndex);
		infoBox.setText(getTurnInfo(playerIndex));
	}

	private String getTurnInfo(int playerIndex) {
		return "Game Info\n\nTurn: Player " + (playerIndex + 1)
				+ " (" + getPlayerColorName(playerIndex) + ")\nCards left: "
				+ Math.max(0, control.getDeck().sum() - (turn && pickCard != null ? 1 : 0));
	}

	private String getPlayerColorName(int playerIndex) {
		switch (playerIndex) {
			case 0: return "Red";
			case 1: return "Yellow";
			case 2: return "Blue";
			case 3: return "Green";
			default: throw new IllegalArgumentException("Invalid player index");
		}
	}

	private Color getPlayerColor(int playerIndex) {
		switch (playerIndex) {
			case 0: return Color.RED;
			case 1: return Color.YELLOW;
			case 2: return Color.BLUE;
			case 3: return new Color(0, 150, 0);
			default: throw new IllegalArgumentException("Invalid player index");
		}
	}


	private boolean checkForWinner() {
		if (control.win() == 0) {
			return false;
		}
		win();
		return true;
	}

	private void finishSuccessfulMove() {
		turn = false;
		count = 0;
		currentCardHasLegalMove = false;
		turnManager.finishTurn(pickCard.getCard() == 2);
		Cards.setEnabled(true);
		setFoldButtonEnabled(false);
		checkForWinner();
	}

	private void setFoldButtonEnabled(boolean enabled) {
		FoldButton.setEnabled(enabled);
		FoldButton.setContentAreaFilled(true);
		FoldButton.setBackground(enabled ? Color.RED : Color.LIGHT_GRAY);
		FoldButton.setForeground(enabled ? Color.WHITE : Color.DARK_GRAY);
		FoldButton.repaint();
	}

	private void configureDialogClose(JDialog dialog) {
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				if (!currentCardHasLegalMove) {
					foldCurrentCard(dialog);
				} else {
					JOptionPane.showMessageDialog(dialog,
							"Choose a legal move before ending your turn.");
				}
			}
		});
	}

	private void foldCurrentCard(JDialog dialog) {
		boolean repeatTurn = pickCard != null && pickCard.getCard() == 2;
		turn = false;
		count = 0;
		control.setFold(1);
		currentCardHasLegalMove = false;
		Cards.setEnabled(true);
		setFoldButtonEnabled(false);
		turnManager.finishTurn(repeatTurn);
		if (dialog != null) {
			dialog.dispose();
		}
	}

	private void showNoLegalMoveDialog() {
		setFoldButtonEnabled(true);
		infoBox.append("\n\nNo legal move. Press the red Fold button to end your turn.");
	}

	private boolean hasSinglePawnMove() {
		return control.canMovePawn(0) || control.canMovePawn(1);
	}

	private boolean canSelectPawn(int pawnIndex, boolean swapping) {
		if (swapping && pickCard.getCard() == 11) {
			return control.canSwapWithAnyEnemy(pawnIndex);
		}
		return control.canMovePawn(pawnIndex);
	}

	private void updatePawnLabels() {
		Player[] players = control.getPlayer();
		for (int playerIndex = 0; playerIndex < players.length; playerIndex++) {
			Pawn[] pawns = players[playerIndex].getPawns();
			for (int pawnIndex = 0; pawnIndex < pawns.length; pawnIndex++) {
				if (playerIndex >= 2) {
					pawnLabels[playerIndex][pawnIndex].setIcon(
							pawns[pawnIndex].isStart() || pawns[pawnIndex].isHome()
									? rotatedStartPawnIcons[playerIndex][pawnIndex]
									: pawnIcons[playerIndex][pawnIndex]);
				}
				pawnLabels[playerIndex][pawnIndex].setBounds(
						pawns[pawnIndex].getEndx(), pawns[pawnIndex].getEndy(), 45, 45);
			}
		}
	}

	private void moveSelectedSevenPawn(JDialog choosePawn) {
		int made = control.makeMove(false, control.getSelectPawn());
		choosePawn.dispose();
		if (made == 1) {
			updatePawnLabels();
			finishSuccessfulMove();
		} else {
			showNoLegalMoveDialog();
		}
	}

	private boolean canMoveInDirection(boolean backward) {
		int cardNumber = pickCard.getCard();
		if (cardNumber == 3 || cardNumber == 5) {
			return !backward && control.hasLegalMove();
		}
		if (backward) {
			if (cardNumber == 4) {
				return control.canMovePawn(control.getSelectPawn());
			}
			return cardNumber == 10
					&& control.getDeck().getCard().findBox(
							control.getChoosePawn(control.getSelectPawn(), control.getSelectPlayer()),
							control.getBoxes(), -1,
							control.getSafetyTrack(control.getSelectPlayer())) != null;
		}
		if (cardNumber == 10) {
			return control.getDeck().getCard().findBox(
					control.getChoosePawn(control.getSelectPawn(), control.getSelectPlayer()),
					control.getBoxes(), 10,
					control.getSafetyTrack(control.getSelectPlayer())) != null;
		}
		return control.canMovePawn(control.getSelectPawn());
	}
	/**

	 * <b>Transformer</b>:shows the panel of the winner <br />

	 * <p>

	 * <b>Postcondition:</b> show the winner

	 * </p>

	 *

	 */

	public void win() {

		winner = new JDialog(Sorry, "Winner", true);

		int winnerIndex = control.win() - 1;

		JLabel winnerText = new JLabel("Player " + (winnerIndex + 1)
				+ " (" + getPlayerColorName(winnerIndex) + ") wins!");

		winnerText.setHorizontalAlignment(SwingConstants.CENTER);

		winnerText.setFont(new Font("Arial", Font.BOLD, 20));

		winner.add(winnerText);

		winner.setSize(200, 100);

		winner.setLocationRelativeTo(Sorry);

		winner.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		winner.setVisible(true);

		winner.dispose();
		Sorry.dispose();

	}



	/**

	 * <b>Transformer</b>:initializes some buttons and labels <br />

	 * <p>

	 * <b>Postcondition:</b> initializes some buttons and labels

	 * </p>

	 *

	 */

	private void initComponents() {

		infoBox = new JTextArea();

		infoBox.setBackground(Color.WHITE);

		infoBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

		infoBox.setFont(new Font("Arial", Font.BOLD, 15));
		infoBox.setLineWrap(true);
		infoBox.setWrapStyleWord(true);

		int firstPlayerIndex = turnManager.getNextPlayerIndex();
		control.setSelectPlayer(firstPlayerIndex);

		infoBox.setText(getTurnInfo(firstPlayerIndex));

		infoBox.setEditable(false);

		infoBox.setBounds(750, 460, 290, 150);

		imageURL = getClass().getResource("/image/background.png");

		image = new ImageIcon(imageURL).getImage();

		JLabel background = new JLabel(new ImageIcon(image));

		imageURL = getClass().getResource("/image/sorryImage.png");

		image = new ImageIcon(imageURL).getImage();

		JLabel sorryImg = new JLabel(new ImageIcon(image));

		JLabel blueBackground = new JLabel();

		blueBackground.setOpaque(true);

		blueBackground.setBackground(Color.CYAN);

		int x = 0;

		int y = 0;

		for (int i = 0; i < 60; i++) {

			box[i] = new JLabel();

			box[i].setOpaque(true);

			box[i].setBackground(Color.WHITE);

			box[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

			box[i].setBounds(x, y, 45, 45);

			if (i < 15) {

				if (i == 2 || i == 11) {

					imageURL = getClass().getResource("/image/greenSlideEnd.png");

					image = new ImageIcon(imageURL).getImage();

					box[i] = new JLabel(new ImageIcon(image));

				} else if ((i < 6 && i > 2) || (i > 11 && i < 14)) {

					imageURL = getClass().getResource("/image/greenSlideMedium.png");

					image = new ImageIcon(imageURL).getImage();

					box[i] = new JLabel(new ImageIcon(image));

				} else if (i == 6 || i == 14) {

					imageURL = getClass().getResource("/image/greenSlideStart.png");

					image = new ImageIcon(imageURL).getImage();

					box[i] = new JLabel(new ImageIcon(image));

				}

				box[i].setBounds(x, y, 45, 45);

				y = y + 45;

			} else if (i < 15 + 15) {

				if (i == 17 || i == 26) {

					imageURL = getClass().getResource("/image/yellowSlideEnd.png");

					image = new ImageIcon(imageURL).getImage();

					box[i] = new JLabel(new ImageIcon(image));

				} else if ((i < 21 && i > 17) || (i > 26 && i < 29)) {

					imageURL = getClass().getResource("/image/yellowSlideMedium.png");

					image = new ImageIcon(imageURL).getImage();

					box[i] = new JLabel(new ImageIcon(image));

				} else if (i == 21 || i == 29) {

					imageURL = getClass().getResource("/image/yellowSlideStart.png");

					image = new ImageIcon(imageURL).getImage();

					box[i] = new JLabel(new ImageIcon(image));

				}

				box[i].setBounds(x, y, 45, 45);

				x = x + 45;

			} else if (i < 15 + 15 + 15) {

				if (i == 14 + 15 + 3 || i == 11 + 15 + 15) {

					imageURL = getClass().getResource("/image/blueSlideEnd.png");

					image = new ImageIcon(imageURL).getImage();

					box[i] = new JLabel(new ImageIcon(image));

				} else if ((i < 15 + 14 + 15 && i > 11 + 15 + 15) || (i > 14 + 15 + 3 && i < 15 + 15 + 6)) {

					imageURL = getClass().getResource("/image/blueSlideMedium.png");

					image = new ImageIcon(imageURL).getImage();

					box[i] = new JLabel(new ImageIcon(image));

				} else if (i == 15 + 14 + 15 || i == 15 + 15 + 6) {

					imageURL = getClass().getResource("/image/blueSlideStart.png");

					image = new ImageIcon(imageURL).getImage();

					box[i] = new JLabel(new ImageIcon(image));

				}

				box[i].setBounds(x, y, 45, 45);

				y = y - 45;

			} else {

				if (i == 15 + 15 + 15 + 2 || i == 11 + 15 + 15 + 15) {

					imageURL = getClass().getResource("/image/redSlideEnd.png");

					image = new ImageIcon(imageURL).getImage();

					box[i] = new JLabel(new ImageIcon(image));

				} else if ((i < 15 + 15 + 15 + 14 && i > 11 + 15 + 15 + 15)

						|| (i >= 15 + 15 + 15 + 3 && i <= 15 + 15 + 15 + 5)) {

					imageURL = getClass().getResource("/image/redSlideMedium.png");

					image = new ImageIcon(imageURL).getImage();

					box[i] = new JLabel(new ImageIcon(image));

				} else if (i == 15 + 15 + 15 + 14 || i == 15 + 15 + 15 + 6) {

					imageURL = getClass().getResource("/image/redSlideStart.png");

					image = new ImageIcon(imageURL).getImage();

					box[i] = new JLabel(new ImageIcon(image));

				}

				box[i].setBounds(x, y, 45, 45);

				x = x - 45;

			}

			GeneralGraph.add(box[i], JLayeredPane.MODAL_LAYER);

		}

		background.setBounds(0, 0, 1920, 1080);

		sorryImg.setBounds(5 * 45, 7 * 45, 271, 77);

		blueBackground.setSize(690, 720);

		GeneralGraph.add(background, JLayeredPane.DEFAULT_LAYER);

		GeneralGraph.add(blueBackground, JLayeredPane.PALETTE_LAYER);

		GeneralGraph.add(sorryImg, JLayeredPane.MODAL_LAYER);

		GeneralGraph.add(infoBox, JLayeredPane.MODAL_LAYER);

		addPlayerAreas();
		addTrackSquareBorders();

	}

	private void addTrackSquareBorders() {
		for (JLabel square : box) {
			addTrackSquareBorder(square.getBounds());
		}
		for (JLabel[] playerSafetyLabels : safetyLabels) {
			for (JLabel square : playerSafetyLabels) {
				if (square != null) {
					addTrackSquareBorder(square.getBounds());
				}
			}
		}
	}

	private void addTrackSquareBorder(Rectangle bounds) {
		JLabel border = new JLabel();
		border.setOpaque(false);
		border.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		border.setBounds(bounds);
		GeneralGraph.add(border, JLayeredPane.DRAG_LAYER);
	}

	private void addPlayerAreas() {
		Player[] players = control.getPlayer();
		for (int playerIndex = 0; playerIndex < players.length; playerIndex++) {
			Color color = getPlayerColor(playerIndex);
			model.Square.Start[] starts = control.getStartSquares(playerIndex);
			model.Square.Home[] homes = control.getHomeSquares(playerIndex);
			boolean verticalArea = playerIndex >= 2;
			int startX = verticalArea
					? starts[0].getX() - 22
					: starts[0].getX() - 5;
			int startY = verticalArea
					? Math.min(starts[0].getY(), starts[1].getY()) - 5
					: starts[0].getY() - (playerIndex == 1 ? 25 : 10);
			int[][] homeBounds = {
					{68, 270, 100, 90},
					{560, 360, 100, 90},
					{360, 65, 90, 100},
					{270, 545, 90, 100}
			};

			startLabels[playerIndex] = createAreaLabel("Start", color);
			startLabels[playerIndex].setBounds(
					startX, startY, verticalArea ? 90 : 100, verticalArea ? 100 : 90);
			GeneralGraph.add(startLabels[playerIndex], JLayeredPane.MODAL_LAYER);

			homeLabels[playerIndex] = createAreaLabel("Home", color);
			homeLabels[playerIndex].setBounds(
					homeBounds[playerIndex][0], homeBounds[playerIndex][1],
					homeBounds[playerIndex][2], homeBounds[playerIndex][3]);
			GeneralGraph.add(homeLabels[playerIndex], JLayeredPane.MODAL_LAYER);
			addHomeSpaces(homes);

			for (int pawnIndex = 0; pawnIndex < 2; pawnIndex++) {
				pawnIcons[playerIndex][pawnIndex] = createPawnIcon(
						playerIndex, pawnIndex, false);
				rotatedStartPawnIcons[playerIndex][pawnIndex] = createPawnIcon(
						playerIndex, pawnIndex, true);
				pawnLabels[playerIndex][pawnIndex] =
						new JLabel(playerIndex >= 2
								? rotatedStartPawnIcons[playerIndex][pawnIndex]
								: pawnIcons[playerIndex][pawnIndex]);
				pawnLabels[playerIndex][pawnIndex].setHorizontalAlignment(SwingConstants.CENTER);
				pawnLabels[playerIndex][pawnIndex].setVerticalAlignment(SwingConstants.CENTER);
				pawnLabels[playerIndex][pawnIndex].setBounds(
						starts[pawnIndex].getX(), starts[pawnIndex].getY(), 45, 45);
				GeneralGraph.add(
						pawnLabels[playerIndex][pawnIndex], JLayeredPane.DRAG_LAYER);
			}

			model.Square.SafetyTrack track = control.getSafetyTrack(playerIndex);
			for (int squareIndex = 0; squareIndex < track.getLength(); squareIndex++) {
				model.Square.Safety safety = track.getSquare(squareIndex);
				JLabel safetyLabel = new JLabel();
				safetyLabel.setOpaque(true);
				safetyLabel.setBackground(color);
				safetyLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
				safetyLabel.setBounds(safety.getX(), safety.getY(), 45, 45);
				safetyLabels[playerIndex][squareIndex] = safetyLabel;
				GeneralGraph.add(safetyLabel, JLayeredPane.POPUP_LAYER);
			}

			addAreaText("Start", startLabels[playerIndex], playerIndex);
			addAreaText("Home", homeLabels[playerIndex], playerIndex);
		}
	}

	private JLabel createAreaLabel(String text, Color color) {
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.BOLD, 14));
		label.setOpaque(true);
		label.setBackground(Color.WHITE);
		label.setBorder(BorderFactory.createLineBorder(color, 3));
		return label;
	}

	private void addAreaText(String text, JLabel area, int playerIndex) {
		if (playerIndex < 2) {
			area.setText(text);
			area.setVerticalAlignment(
					playerIndex == 0 ? SwingConstants.BOTTOM : SwingConstants.TOP);
			return;
		}

		area.setText("");
		Font font = new Font("Arial", Font.BOLD, 14);
		BufferedImage textImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D measureGraphics = textImage.createGraphics();
		measureGraphics.setFont(font);
		FontMetrics metrics = measureGraphics.getFontMetrics();
		int textWidth = metrics.stringWidth(text);
		int textHeight = metrics.getHeight();
		measureGraphics.dispose();

		textImage = new BufferedImage(textWidth + 4, textHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D textGraphics = textImage.createGraphics();
		textGraphics.setFont(font);
		textGraphics.setColor(Color.BLACK);
		FontMetrics textMetrics = textGraphics.getFontMetrics();
		textGraphics.drawString(text, 2, textMetrics.getAscent());
		textGraphics.dispose();

		BufferedImage rotatedImage = rotatePawnImage(textImage, playerIndex == 2);
		JLabel rotatedText = new JLabel(new ImageIcon(rotatedImage));
		int textX = playerIndex == 2
				? area.getX() + 3
				: area.getX() + area.getWidth() - rotatedImage.getWidth() - 3;
		int textY = area.getY() + (area.getHeight() - rotatedImage.getHeight()) / 2;
		rotatedText.setBounds(textX, textY, rotatedImage.getWidth(), rotatedImage.getHeight());
		rotatedText.setOpaque(false);
		GeneralGraph.add(rotatedText, JLayeredPane.DRAG_LAYER, 0);
	}

	private ImageIcon createPawnIcon(int playerIndex, int pawnIndex, boolean rotateForStart) {
		String fileName = playerIndex == 1
				? "/image/yellowPawn" + (pawnIndex + 1) + ".png"
				: "/image/redPawn" + (pawnIndex + 1) + ".png";
		try {
			BufferedImage pawnImage = ImageIO.read(getClass().getResource(fileName));
			if (playerIndex == 2 || playerIndex == 3) {
				int targetRgb = getPlayerColor(playerIndex).getRGB() & 0x00ffffff;
				for (int y = 0; y < pawnImage.getHeight(); y++) {
					for (int x = 0; x < pawnImage.getWidth(); x++) {
						int argb = pawnImage.getRGB(x, y);
						Color pixel = new Color(argb, true);
						if (pixel.getRed() > 150 && pixel.getGreen() < 100
								&& pixel.getBlue() < 100) {
							pawnImage.setRGB(x, y, (argb & 0xff000000) | targetRgb);
						}
					}
				}
				if (rotateForStart) {
					pawnImage = rotatePawnImage(pawnImage, playerIndex == 2);
				}
			}
			Image scaledImage = pawnImage.getScaledInstance(
					PAWN_ICON_SIZE, PAWN_ICON_SIZE, Image.SCALE_SMOOTH);
			return new ImageIcon(scaledImage);
		} catch (IOException | IllegalArgumentException exception) {
			throw new IllegalStateException("Could not load pawn image: " + fileName, exception);
		}
	}

	private BufferedImage rotatePawnImage(BufferedImage source, boolean counterClockwise) {
		int width = source.getWidth();
		int height = source.getHeight();
		BufferedImage rotated = new BufferedImage(
				height, width, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rotatedX = counterClockwise ? y : height - 1 - y;
				int rotatedY = counterClockwise ? width - 1 - x : x;
				rotated.setRGB(rotatedX, rotatedY, source.getRGB(x, y));
			}
		}
		return rotated;
	}

	private void addHomeSpaces(model.Square.Home[] homeSquares) {
		for (model.Square.Home homeSquare : homeSquares) {
			JLabel homeSpace = new JLabel();
			homeSpace.setOpaque(false);
			homeSpace.setBounds(homeSquare.getX(), homeSquare.getY(), 45, 45);
			GeneralGraph.add(homeSpace, JLayeredPane.POPUP_LAYER);
		}
	}



	/**

	 * <b>Transformer(mutative)</b>:sets some buttons and labels <br />

	 * <p>

	 * <b>Postcondition:</b> sets some buttons and labels

	 * </p>

	 *

	 */

	public void initButtons() {

		ReceiveCard = new JLabel("Draw pile");

		ReceiveCard.setFont(new Font("Arial", Font.BOLD, 14));

		ReceiveCard.setBounds(750, 365, 120, 25);

		CurrentCard = new JLabel("Current card");

		CurrentCard.setFont(new Font("Arial", Font.BOLD, 14));

		CurrentCard.setBounds(930, 365, 120, 25);

		FoldButton = new JButton("Fold");
		FoldButton.setUI(new BasicButtonUI());
		FoldButton.setBackground(Color.RED);
		FoldButton.setForeground(Color.WHITE);
		FoldButton.setOpaque(true);
		FoldButton.setContentAreaFilled(true);
		FoldButton.setFocusPainted(false);
		setFoldButtonEnabled(false);

		FoldButton.setBounds(750, 400, 290, 50);

		imageURL = getClass().getResource("/image/backCard.png");

		image = new ImageIcon(imageURL).getImage();

		Cards = new JButton(new ImageIcon(image));
		Cards.setToolTipText("Draw a card");
		Cards.getAccessibleContext().setAccessibleName("Draw a card");

		Cards.setBounds(750, 200, 108, 158);

		GeneralGraph.add(Cards, JLayeredPane.MODAL_LAYER);

		GeneralGraph.add(ReceiveCard, JLayeredPane.MODAL_LAYER);

		GeneralGraph.add(CurrentCard, JLayeredPane.MODAL_LAYER);

		GeneralGraph.add(FoldButton, JLayeredPane.MODAL_LAYER);

	}



	/**

	 * <b>Observer</b>:find the right panel accordingly the card<br />

	 * <p>

	 * <b>Postcondition:</b>shows the panel accordingly the card

	 * </p>

	 *

	 */

	public void game() {

		if (turn == true) {
			if (!control.hasLegalMove()) {
				showNoLegalMoveDialog();
				return;
			}

			switch (pickCard.getCard()) {

			case 1:

			case 4:

			case 6:

			case 10:

				choosePawnDialog();

				break;

			case 3:

			case 5:

				MovePawnDialog();

				break;

			case 7:

			case 8:

			case 11:

			case 12:

				chooseBetween();

				break;

			case 2:

				choosePawnDialog();

				break;

			}

		}

	}



	/**

	 * <b>Observer</b>:shows the dialog panel accordingly the card 8,11,12<br />

	 * <p>

	 * <b>Postcondition:</b>shows the panel accordingly the card

	 * </p>

	 *

	 */

	public void chooseBetween() {

		JDialog choose = new JDialog(Sorry, "Choose Between", true);

		JPanel panel = new JPanel();

		JButton ch1 = new JButton("Move Pawn");

		ch1.setSize(25, 20);

		switch (pickCard.getCard()) {

		case 7:
			JButton spl = new JButton("Split 7");

			spl.setSize(25, 20);

			spl.addActionListener(new ActionListener() {

				@Override

				public void actionPerformed(ActionEvent e) {

					choose.setVisible(false);

					if (!chooseSevenSplitDialog()) {
						choose.setVisible(true);
					}

				}

			});
			spl.setEnabled(control.canSplitSeven());

			panel.add(spl);

			break;

		case 8:

		case 12:

			JButton retake = new JButton("Draw Another Card");
			retake.addActionListener(event -> {
				choose.dispose();
				turn = false;
				turnManager.finishTurn(true);
				Cards.setEnabled(true);
				setFoldButtonEnabled(false);
			});
			panel.add(retake);
			break;

		case 11:

			JButton changeP = new JButton("Swap Pawns");
			changeP.setEnabled(control.canSwapWithAnyEnemy(0)
					|| control.canSwapWithAnyEnemy(1));

			changeP.setSize(25, 20);

			changeP.addActionListener(new ActionListener() {

				@Override

				public void actionPerformed(ActionEvent e) {

					choose.dispose();

					choosePawnDialog(true, false, true);

				}

			});

			panel.add(changeP);

			break;

		}

		ch1.addActionListener(new ActionListener() {

			@Override

			public void actionPerformed(ActionEvent e) {

				choose.dispose();

				if (pickCard.getCard() == 7) {
					choosePawnDialog(false, true, true);
				} else if (pickCard.getCard() == 11) {
					choosePawnDialog(false, false, true);
				} else {
					choosePawnDialog(false, false, true);
				}

			}

		});

		ch1.setEnabled(hasSinglePawnMove());
		panel.add(ch1);

		choose.add(panel);
		configureDialogClose(choose);

		choose.pack();
		choose.setMinimumSize(new Dimension(360, 120));

		choose.setLocationRelativeTo(Sorry);

		choose.setVisible(true);



	}



	/**

	 * <b>Observer</b>:shows the dialog panel for choose the selectable pawn<br />

	 * <p>

	 * <b>Postcondition:</b>shows the dialog panel for choose the selectable pawn

	 * </p>

	 *

	 */

	public void choosePawnDialog() {
		choosePawnDialog(false, false);
	}

	private void choosePawnDialog(boolean swapping) {
		choosePawnDialog(swapping, false);
	}

	private void choosePawnDialog(boolean swapping, boolean moveSevenImmediately) {
		choosePawnDialog(swapping, moveSevenImmediately, false);
	}

	private void choosePawnDialog(boolean swapping, boolean moveSevenImmediately,
			boolean canReturnToCardChoice) {
		JDialog choose = new JDialog(Sorry, "Choose Pawn", true);

		JPanel panel = new JPanel();

		JButton pawn1 = new JButton("Pawn 1");

		JButton pawn2 = new JButton("Pawn 2");

		pawn1.setEnabled(canSelectPawn(0, swapping));
		pawn2.setEnabled(canSelectPawn(1, swapping));

		pawn1.setSize(25, 20);

		pawn2.setSize(25, 20);

		pawn1.addActionListener(new ActionListener() {

			@Override

			public void actionPerformed(ActionEvent e) {

				control.setSelectPawn(0);

				if (moveSevenImmediately) {
					moveSelectedSevenPawn(choose);
				} else {
					choose.dispose();
				}

				if (swapping && turn) {
					chooseEnemyPawnDialog(true, canReturnToCardChoice);
				} else if (!moveSevenImmediately && pickCard.getCard() == 11 && turn) {
					MovePawnDialog(true, canReturnToCardChoice);
				} else if (!moveSevenImmediately && pickCard.getCard() == 6 && turn) {

					chooseEnemyPawnDialog(false, false);

				} else if (!moveSevenImmediately && pickCard.getCard() != 11 && turn) {

					MovePawnDialog(true, canReturnToCardChoice);

				}

			}

		});



		pawn2.addActionListener(new ActionListener() {

			@Override

			public void actionPerformed(ActionEvent e) {

				control.setSelectPawn(1);

				if (moveSevenImmediately) {
					moveSelectedSevenPawn(choose);
				} else {
					choose.dispose();
				}

				if (swapping && turn) {
					chooseEnemyPawnDialog(true, canReturnToCardChoice);
				} else if (!moveSevenImmediately && pickCard.getCard() == 11 && turn) {
					MovePawnDialog(true, canReturnToCardChoice);
				} else if (!moveSevenImmediately && pickCard.getCard() == 6 && turn) {

					chooseEnemyPawnDialog(false, false);

				} else if (!moveSevenImmediately && pickCard.getCard() != 11 && turn) {

					MovePawnDialog(true, canReturnToCardChoice);

				}

			}

		});

		panel.add(pawn1);

		panel.add(pawn2);

		if (canReturnToCardChoice) {
			JButton backButton = new JButton("\u2190 Back");
			backButton.addActionListener(event -> {
				choose.dispose();
				chooseBetween();
			});
			panel.add(backButton);
		}

		choose.add(panel);
		configureDialogClose(choose);

		choose.pack();
		choose.setMinimumSize(new Dimension(260, 120));

		choose.setLocationRelativeTo(Sorry);

		choose.setVisible(true);

	}



	/**

	 * <b>Observer</b>:shows the dialog panel for choose the enemy selectable pawn

	 * for SorryCard and 11<br />

	 * <p>

	 * <b>Postcondition:</b>shows the dialog panel for choose the enemy selectable

	 * pawn for SorryCard and 11

	 * </p>

	 *

	 */

	public void chooseEnemyPawnDialog() {
		chooseEnemyPawnDialog(pickCard.getCard() == 11, false);
	}

	private void chooseEnemyPawnDialog(boolean swapping, boolean canReturnToCardChoice) {
		chooseEnemyPlayer(swapping, canReturnToCardChoice);
	}

	private void showEnemyPawnDialog(
			int enemyPlayerIndex, boolean swapping, boolean canReturnToCardChoice) {
		JDialog choose = new JDialog(Sorry, "Choose Enemy Pawn", true);

		JPanel panel = new JPanel();

		JButton pawn1 = new JButton("Pawn 1");

		JButton pawn2 = new JButton("Pawn 2");

		pawn1.setText("Player " + (enemyPlayerIndex + 1) + " Pawn 1");
		pawn2.setText("Player " + (enemyPlayerIndex + 1) + " Pawn 2");
		pawn1.setEnabled(control.canSwapWithEnemy(
				control.getSelectPawn(), enemyPlayerIndex, 0));
		pawn2.setEnabled(control.canSwapWithEnemy(
				control.getSelectPawn(), enemyPlayerIndex, 1));

		pawn1.setSize(25, 20);

		pawn2.setSize(25, 20);

		pawn1.addActionListener(new ActionListener() {

			@Override

			public void actionPerformed(ActionEvent e) {

				moveAgainstEnemy(choose, enemyPlayerIndex, 0);

			}

		});



		pawn2.addActionListener(new ActionListener() {

			@Override

			public void actionPerformed(ActionEvent e) {

				moveAgainstEnemy(choose, enemyPlayerIndex, 1);

			}

		});

		panel.add(pawn1);

		panel.add(pawn2);

		JButton backButton = new JButton("\u2190 Back");
		backButton.addActionListener(event -> {
			choose.dispose();
			chooseEnemyPlayer(swapping, canReturnToCardChoice);
		});
		panel.add(backButton);

		choose.add(panel);
		configureDialogClose(choose);

		choose.pack();
		choose.setMinimumSize(new Dimension(360, 120));

		choose.setLocationRelativeTo(Sorry);

		choose.setVisible(true);

	}

	private void chooseEnemyPlayer(boolean swapping, boolean canReturnToCardChoice) {
		List<Integer> enemyIndexes = new ArrayList<>();
		for (int playerIndex = 0; playerIndex < control.getPlayerCount(); playerIndex++) {
			if (playerIndex != control.getSelectPlayer()
					&& (control.canSwapWithEnemy(
							control.getSelectPawn(), playerIndex, 0)
							|| control.canSwapWithEnemy(
									control.getSelectPawn(), playerIndex, 1))) {
				enemyIndexes.add(playerIndex);
			}
		}
		JDialog choose = new JDialog(Sorry, "Choose Opponent", true);
		JPanel panel = new JPanel();
		for (int enemyPlayerIndex : enemyIndexes) {
			JButton opponent = new JButton("Player " + (enemyPlayerIndex + 1)
					+ " (" + getPlayerColorName(enemyPlayerIndex) + ")");
			opponent.addActionListener(event -> {
				choose.dispose();
				showEnemyPawnDialog(enemyPlayerIndex, swapping, canReturnToCardChoice);
			});
			panel.add(opponent);
		}
		JButton backButton = new JButton("\u2190 Back");
		backButton.addActionListener(event -> {
			choose.dispose();
			choosePawnDialog(swapping, false, canReturnToCardChoice);
		});
		panel.add(backButton);
		choose.add(panel);
		configureDialogClose(choose);
		choose.pack();
		choose.setMinimumSize(new Dimension(320, 120));
		choose.setLocationRelativeTo(Sorry);
		choose.setVisible(true);
	}

	private void moveAgainstEnemy(
			JDialog dialog, int enemyPlayerIndex, int enemyPawnIndex) {
		int made = control.makeMove(
				control.getSelectPawn(), enemyPlayerIndex, enemyPawnIndex);
		if (made != 1) {
			JOptionPane.showMessageDialog(dialog, "That move is no longer legal.");
			return;
		}

		dialog.dispose();
		updatePawnLabels();
		finishSuccessfulMove();
	}


	public boolean chooseSevenSplitDialog() {
		while (true) {
			Object[] pawnOptions = {"Pawn 1", "Pawn 2"};
			Object selectedPawn = JOptionPane.showInputDialog(
					Sorry, "Choose the pawn that moves the first part of the 7:",
					"Split 7", JOptionPane.QUESTION_MESSAGE, null, pawnOptions, pawnOptions[0]);
			if (selectedPawn == null) {
				return false;
			}
			int firstPawnIndex = selectedPawn.equals(pawnOptions[0]) ? 0 : 1;

			String input = JOptionPane.showInputDialog(
					Sorry, "How many spaces should Pawn " + (firstPawnIndex + 1) + " move? (1-6)");
			if (input == null) {
				return false;
			}
			try {
				int firstSteps = Integer.parseInt(input.trim());
				if (firstSteps < 1 || firstSteps > 6) {
					JOptionPane.showMessageDialog(Sorry, "Enter a number from 1 to 6.");
					continue;
				}

				int made = control.makeSevenSplit(firstPawnIndex, firstSteps);
				if (made == 1) {
					updatePawnLabels();
					finishSuccessfulMove();

					return true;
				}

				JOptionPane.showMessageDialog(Sorry,
						"That split is not legal. Choose another pawn or split amount.");
			} catch (NumberFormatException exception) {
				JOptionPane.showMessageDialog(Sorry, "Enter a whole number from 1 to 6.");
			}
		}
	}



	/**

	 * <b>Observer</b>:shows the dialog panel for choose forward or backward

	 * accordingly the card<br />

	 * <p>

	 * <b>Postcondition:</b>shows the dialog panel for choose forward or backward

	 * accordingly the card

	 * </p>

	 *

	 */

	public void MovePawnDialog() {
		MovePawnDialog(false, false);
	}

	private void MovePawnDialog(boolean canGoBackToPawnSelection) {
		MovePawnDialog(canGoBackToPawnSelection, false);
	}

	private void MovePawnDialog(boolean canGoBackToPawnSelection, boolean pawnSelectionCanReturnToCardChoice) {

		JDialog Moves = new JDialog(Sorry, "Move Pawn", true);

		JPanel panel = new JPanel();

		JButton forwardButton = new JButton("Forward");

		JButton backwardButton = new JButton("Backward");

		switch (pickCard.getCard()) {

		case 1:

		case 2:

		case 3:

		case 5:

		case 7:

		case 8:

		case 11:

		case 12:

			forwardButton.setVisible(true);

			backwardButton.setVisible(false);

			break;

		case 4:

			forwardButton.setVisible(false);

			backwardButton.setVisible(true);

			break;

		case 10:

			forwardButton.setVisible(true);

			backwardButton.setVisible(true);

			break;

		}

		forwardButton.setSize(25, 20);

		backwardButton.setSize(25, 20);

		forwardButton.setEnabled(canMoveInDirection(false));
		backwardButton.setEnabled(canMoveInDirection(true));

		forwardButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				int made;
				if (pickCard.getCard() == 3 || pickCard.getCard() == 5) {
					made = control.makeMove(true, 0);
					Moves.dispose();
					if (made == 1) {
						updatePawnLabels();
						finishSuccessfulMove();
					} else {
					}
					return;
				}

				switch (pickCard.getCard()) {

				case 1:

				case 2:

				case 7:

				case 8:

				case 10:

				case 11:

				case 12:

					int selectPawn = control.getSelectPawn();

					made = control.makeMove(false, selectPawn);

					if (made == 1) {
						updatePawnLabels();

						Moves.dispose();

						finishSuccessfulMove();

					} else {

						count++;

						Moves.dispose();

						if (count < 2) {

							choosePawnDialog(false, false, pawnSelectionCanReturnToCardChoice);

						} else {

							count = 0;


						}

					}

					break;

				}

				Moves.dispose();

			}

		});



		backwardButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				int made;

				int selectPawn = control.getSelectPawn();

				if (pickCard.getCard() == 10) {

					made = control.makeMove(false, selectPawn, MoveDirection.BACKWARD);

				} else {

					made = control.makeMove(false, selectPawn);

				}

				if (made == 1) {
					updatePawnLabels();

					Moves.dispose();

					finishSuccessfulMove();

				} else {

					count++;

					Moves.dispose();

					if (count < 2) {

						choosePawnDialog(false, false, pawnSelectionCanReturnToCardChoice);

					} else {

						count = 0;


					}

				}

			}

		});



		panel.add(forwardButton);

		panel.add(backwardButton);

		if (canGoBackToPawnSelection) {
			JButton backButton = new JButton("\u2190 Back");
			backButton.addActionListener(event -> {
				Moves.dispose();
				choosePawnDialog(false, false, pawnSelectionCanReturnToCardChoice);
			});
			panel.add(backButton);
		}

		Moves.add(panel);
		configureDialogClose(Moves);

		Moves.pack();
		Moves.setMinimumSize(new Dimension(260, 100));
		Moves.setLocationRelativeTo(Sorry);

		Moves.setVisible(true);

	}



	private class FoldListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (turn && !currentCardHasLegalMove) {
				foldCurrentCard(null);
			}

		}



	}



	private class cardsListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if (turn == false || control.getFold() == 1) {

				turn = true;
				count = 0;
				currentCardHasLegalMove = false;
				Cards.setEnabled(false);
				setFoldButtonEnabled(false);

				control.setFold(0);

				if (control.tableIsEmpty()) {

					control.resetDeck();

				}
				pickCard = control.pickedCard();

				if (drawnCardImage != null) {

					GeneralGraph.remove(drawnCardImage);

				}

				if (pickCard.getCard() == 6) {

					imageURL = getClass().getResource("/image/cardSorry.png");

				} else {

					String strNum = String.valueOf(pickCard.getCard());

					imageURL = getClass().getResource("/image/card" + strNum + ".png");

				}

				image = new ImageIcon(imageURL).getImage();

				drawnCardImage = new JLabel(new ImageIcon(image));

				drawnCardImage.setBounds(930, 200, 108, 158);

				GeneralGraph.add(drawnCardImage, JLayeredPane.MODAL_LAYER);

				changeName();
				currentCardHasLegalMove = control.hasLegalMove();
				setFoldButtonEnabled(!currentCardHasLegalMove);

				if (pickCard.getDescription() != null) {
					infoBox.append("\n\nCard: " + pickCard.getDescription());
				}
				Sorry.repaint();

				game();

				control.getDeck().removeCard();

			}



		}

	}

}
