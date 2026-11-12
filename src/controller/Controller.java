package controller;


import java.util.ArrayList;
import java.util.List;

import model.*;

import model.Card.*;

import model.Square.*;

import model.Square.Slide.*;
import model.save.GameSnapshot;
import model.save.LocationType;
import model.save.PawnPosition;


/** Coordinates game rules, player state, movement, captures, and persistence. */
public class Controller {

	private Deck deck = new Deck();

	private int fold, numSelectPlayer, numSelectPawn;

	private Player[] players;

	private Square[] boxes = new Square[60];

	private static final int[] PLAYER_ENTRY_INDEXES = {58, 28, 43, 13};
	private final SafetyTrack[] safetyTracks = new SafetyTrack[4];
	private final Start[][] starts = new Start[4][2];


	private Square createSquare(int i, int x, int y) {
		if (i == 6)  return new StartSlide(x, y, 3, 5);
		if (i == 5 || i == 4 || i == 3) return new InternalSlide(x, y);
		if (i == 2)  return new EndSlide(x, y);
		if (i == 14) return new StartSlide(x, y, 3, 4);
		if (i == 13 || i == 12) return new InternalSlide(x, y);
		if (i == 11) return new EndSlide(x, y);

		// Yellow Slides
		if (i == 21) return new StartSlide(x, y, 1, 5);
		if (i == 20 || i == 19 || i == 18) return new InternalSlide(x, y);
		if (i == 17) return new EndSlide(x, y);
		if (i == 29) return new StartSlide(x, y, 1, 4);
		if (i == 28 || i == 27) return new InternalSlide(x, y);
		if (i == 26) return new EndSlide(x, y);

		// Blue
		if (i == 36) return new StartSlide(x, y, 2, 5);
		if (i == 35 || i == 34 || i == 33) return new InternalSlide(x, y);
		if (i == 32) return new EndSlide(x, y);
		if (i == 44) return new StartSlide(x, y, 2, 4);
		if (i == 43 || i == 42) return new InternalSlide(x, y);
		if (i == 41) return new EndSlide(x, y);

		// Red
		if (i == 51) return new StartSlide(x, y, 0, 5);
		if (i == 50 || i == 49 || i == 48) return new InternalSlide(x, y);
		if (i == 47) return new EndSlide(x, y);
		if (i == 59) return new StartSlide(x, y, 0, 4);
		if (i == 58 || i == 57) return new InternalSlide(x, y);
		if (i == 56) return new EndSlide(x, y);

		// Simple Squares
		return new Simple(x, y);
	}
	/**

	 * <b>constructor</b>:Creates a new Controller and initializes some coordinates

	 * and card <br>

	 */

	public Controller() {
		this(2);
	}

	/**
	 * Creates a complete board and the requested number of players.
	 *
	 * @param playerCount number of players, from 2 through 4
	 */
	public Controller(int playerCount) {
		setPlayer(playerCount);

		int x=0,y=0;

		for(int i=0;i<60;i++){

			if(i<15){

				boxes[i] = createSquare(i, x, y);

				y=y+45;

			}else if(i<15+15){

				boxes[i] = createSquare(i, x, y);

				x=x+45;

			}else if(i<15+15+15){

				boxes[i] = createSquare(i, x, y);

				y=y-45;

			}else{

				boxes[i] = createSquare(i, x, y);

				x=x-45;

			}

		}

		initializePlayerAreas();

		fold=0;

	}

	private void initializePlayerAreas() {
		int[][] safetyOrigins = {
				{90, 45}, {585, 630}, {630, 90}, {45, 585}
		};
		int[][] safetySteps = {
				{0, 45}, {0, -45}, {-45, 0}, {45, 0}
		};
		int[][] homeOrigins = {
				{75, 280}, {565, 385}, {382, 70}, {292, 550}
		};
		int[][] homeSteps = {
				{45, 0}, {45, 0}, {0, 45}, {0, 45}
		};
		int[][] startOrigins = {
				{165, 55}, {475, 610}, {607, 205}, {67, 465}
		};
		int[][] startSteps = {
			{45, 0}, {45, 0}, {0, -45}, {0, 45}
		};
		for (int playerIndex = 0; playerIndex < safetyTracks.length; playerIndex++) {
			int color = playerIndex + 1;
			Safety[] safetySquares = new Safety[5];
			for (int i = 0; i < safetySquares.length; i++) {
				safetySquares[i] = new Safety(
						safetyOrigins[playerIndex][0] + safetySteps[playerIndex][0] * i,
						safetyOrigins[playerIndex][1] + safetySteps[playerIndex][1] * i,
						color);
			}

			Home[] homeSquares = new Home[2];
			for (int i = 0; i < homeSquares.length; i++) {
				homeSquares[i] = new Home(
						homeOrigins[playerIndex][0] + homeSteps[playerIndex][0] * i,
						homeOrigins[playerIndex][1] + homeSteps[playerIndex][1] * i,
						color);
				starts[playerIndex][i] = new Start(
						startOrigins[playerIndex][0] + startSteps[playerIndex][0] * i,
						startOrigins[playerIndex][1] + startSteps[playerIndex][1] * i,
						color);
				if (playerIndex < players.length) {
					players[playerIndex].getPawns()[i]
							.setStartSquare(starts[playerIndex][i]);
				}
			}
			safetyTracks[playerIndex] = new SafetyTrack(
					safetySquares, homeSquares, PLAYER_ENTRY_INDEXES[playerIndex]);
		}
	}



	/**

	 * <b>Accessory:</b>This method returns the array of players

	 * <p>

	 * <b> Postcondition:</b> Must return Player

	 *

	 * @return the array of players

	 */

	public Player[] getPlayer() {

		return players.clone();

	}



	/**

	 * <b>Accessory:</b>This method returns the boxes

	 * <p>

	 * <b> Postcondition:</b> Must return Square

	 *

	 * @return the array of square boxes

	 */

	public Square[] getBoxes() {

		return boxes.clone();

	}



	/**

	 * <b>Accessory:</b>This method returns the SafetyRed boxes

	 * <p>

	 * <b> Postcondition:</b> Must return Safety

	 *

	 * @return the array of safety boxes of red

	 */

	public Safety[] getSafetyRed() {

		return copySafetySquares(safetyTracks[0]);
	}

	/**
	 * Returns Red's complete private route.
	 *
	 * @return Red player's Safety and Home route
	 */
	public SafetyTrack getRedSafetyTrack() {
		return safetyTracks[0];
	}



	/**

	 * <b>Accessory:</b>This method returns the Safety Yellow boxes

	 * <p>

	 * <b> Postcondition:</b> Must return Safety

	 *

	 * @return the array of safety boxes of yellow

	 */

	public Safety[] getSafetyYellow() {

		return copySafetySquares(safetyTracks[1]);
	}

	/**
	 * Returns Yellow's complete private route.
	 *
	 * @return Yellow player's Safety and Home route
	 */
	public SafetyTrack getYellowSafetyTrack() {
		return safetyTracks[1];
	}

	/**
	 * Returns Blue's protected squares.
	 *
	 * @return a copy of Blue player's Safety squares
	 */
	public Safety[] getSafetyBlue() {
		return copySafetySquares(safetyTracks[2]);
	}

	/**
	 * Returns Green's protected squares.
	 *
	 * @return a copy of Green player's Safety squares
	 */
	public Safety[] getSafetyGreen() {
		return copySafetySquares(safetyTracks[3]);
	}

	/**
	 * Returns a player's Safety and Home route.
	 *
	 * @param playerIndex zero-based player index
	 * @return requested route
	 */
	public SafetyTrack getSafetyTrack(int playerIndex) {
		if (playerIndex < 0 || playerIndex >= safetyTracks.length) {
			throw new IllegalArgumentException("Invalid player index");
		}
		return safetyTracks[playerIndex];
	}

	/**
	 * Returns a copy of a player's Start slots.
	 *
	 * @param playerIndex zero-based player index
	 * @return two Start squares
	 */
	public Start[] getStartSquares(int playerIndex) {
		if (playerIndex < 0 || playerIndex >= starts.length) {
			throw new IllegalArgumentException("Invalid player index");
		}
		return starts[playerIndex].clone();
	}

	/**
	 * Returns a copy containing a player's Home slots.
	 *
	 * @param playerIndex zero-based player index
	 * @return two Home squares
	 */
	public Home[] getHomeSquares(int playerIndex) {
		if (playerIndex < 0 || playerIndex >= safetyTracks.length) {
			throw new IllegalArgumentException("Invalid player index");
		}
		return new Home[] {
				safetyTracks[playerIndex].getHomeSquare(0),
				safetyTracks[playerIndex].getHomeSquare(1)
		};
	}

	private Safety[] copySafetySquares(SafetyTrack safetyTrack) {
		Safety[] squares = new Safety[safetyTrack.getLength()];
		for (int i = 0; i < squares.length; i++) {
			squares[i] = safetyTrack.getSquare(i);
		}
		return squares;
	}

	private PawnPosition createPawnPosition(int playerIndex, int pawnIndex) {
		Pawn pawn = players[playerIndex].getPawns()[pawnIndex];
		Square square = pawn.getCurrentSquare();

		if (pawn.isStart()) {
			return new PawnPosition(
					playerIndex, pawnIndex, LocationType.START, pawnIndex);
		}

		SafetyTrack safetyTrack = safetyTracks[playerIndex];

		int safetyIndex = safetyTrack.getIndexOf(square);
		if (safetyIndex >= 0) {
			return new PawnPosition(
					playerIndex, pawnIndex, LocationType.SAFETY, safetyIndex);
		}

		for(int i=0; i<2; i++){
			if(square==safetyTrack.getHomeSquare(i)){
				return new PawnPosition(
						playerIndex, pawnIndex, LocationType.HOME, i);
			}
		}
		Square[] boxes=getBoxes();
		for(int i = 0; i < 60; i++){
			if (boxes[i]==square) {
				return new PawnPosition(
						playerIndex, pawnIndex, LocationType.OUTER, i);
			}
		}

		throw new IllegalStateException("Pawn is not on a known board square");
	}

	private Square resolveSquare(PawnPosition position) {
		if (position == null) {
			throw new IllegalArgumentException("Pawn position cannot be null");
		}

		int playerIndex = position.getPlayerIndex();
		if (playerIndex >= players.length) {
			throw new IllegalArgumentException(
					"Snapshot contains a player not supported by this game");
		}

		SafetyTrack safetyTrack = safetyTracks[playerIndex];
		switch (position.getLocationType()) {
			case START:
				if (position.getLocationIndex() != position.getPawnIndex()) {
					throw new IllegalArgumentException(
							"Start location index must match the pawn index");
				}
				return starts[playerIndex][position.getPawnIndex()];
			case OUTER:
				return boxes[position.getLocationIndex()];
			case SAFETY:
				return safetyTrack.getSquare(position.getLocationIndex());
			case HOME:
				return safetyTrack.getHomeSquare(position.getLocationIndex());
			default:
				throw new IllegalArgumentException(
						"Unsupported pawn location: " + position.getLocationType());
		}
	}

	/**
	 * Validates and restores all model state from a snapshot.
	 *
	 * @param snapshot saved state matching this game's player count
	 * @throws IllegalArgumentException if the snapshot is inconsistent
	 */
	public void restoreSnapshot(GameSnapshot snapshot) {
		if (snapshot == null) {
			throw new IllegalArgumentException("Snapshot cannot be null");
		}
		if (snapshot.getPlayerCount() != players.length) {
			throw new IllegalArgumentException(
					"Snapshot player count does not match this game");
		}
		if (snapshot.getNextPlayerIndex() < 0
				|| snapshot.getNextPlayerIndex() >= players.length) {
			throw new IllegalArgumentException(
					"Snapshot next player is not supported by this game");
		}

		List<PawnPosition> positions = snapshot.getPawnPositions();
		int pawnCount = 0;
		for (Player player : players) {
			pawnCount += player.getPawns().length;
		}
		if (positions.size() != pawnCount) {
			throw new IllegalArgumentException(
					"Snapshot must contain exactly one position for every pawn");
		}

		boolean[][] seenPawns = new boolean[players.length][2];
		Square[] destinations = new Square[positions.size()];
		for (int i = 0; i < positions.size(); i++) {
			PawnPosition position = positions.get(i);
			if (position == null) {
				throw new IllegalArgumentException(
						"Snapshot pawn positions cannot contain null");
			}

			int playerIndex = position.getPlayerIndex();
			int pawnIndex = position.getPawnIndex();
			if (playerIndex >= players.length) {
				throw new IllegalArgumentException(
						"Snapshot contains a player not supported by this game");
			}
			if (seenPawns[playerIndex][pawnIndex]) {
				throw new IllegalArgumentException(
						"Snapshot contains a duplicate pawn");
			}
			seenPawns[playerIndex][pawnIndex] = true;

			Square destination = resolveSquare(position);
			for (int previous = 0; previous < i; previous++) {
				if (destinations[previous] == destination) {
					throw new IllegalArgumentException(
							"Snapshot assigns multiple pawns to the same square");
				}
			}
			destinations[i] = destination;
		}

		for (int playerIndex = 0; playerIndex < seenPawns.length; playerIndex++) {
			for (int pawnIndex = 0; pawnIndex < seenPawns[playerIndex].length; pawnIndex++) {
				if (!seenPawns[playerIndex][pawnIndex]) {
					throw new IllegalArgumentException(
							"Snapshot is missing a pawn position");
				}
			}
		}

		Deck restoredDeck = new Deck(
				snapshot.getDeckOrder(), snapshot.getNextCardIndex());

		for (int i = 0; i < positions.size(); i++) {
			PawnPosition position = positions.get(i);
			players[position.getPlayerIndex()].getPawns()[position.getPawnIndex()]
					.moveTo(destinations[i]);
		}
		deck = restoredDeck;
		numSelectPlayer = snapshot.getNextPlayerIndex();
		numSelectPawn = 0;
	}

	/**
	 * Captures the current logical state between turns.
	 *
	 * @param nextPlayerIndex zero-based player who will draw next
	 * @return immutable validated snapshot
	 */
	public GameSnapshot createSnapshot(int nextPlayerIndex) {
		List<PawnPosition> positions = new ArrayList<>();

		for (int playerIndex = 0; playerIndex < players.length; playerIndex++) {
			for (int pawnIndex = 0;
				pawnIndex < players[playerIndex].getPawns().length;
				pawnIndex++) {
				positions.add(createPawnPosition(playerIndex, pawnIndex));
			}
		}

		return new GameSnapshot(
				positions,
				deck.getDrawOrder(),
				deck.getNextCardIndex(),
				nextPlayerIndex,
				players.length);
	}
	/**

	 * <b>Accessory:</b>This method returns the Home Red boxes

	 * <p>

	 * <b> Postcondition:</b> Must return Home

	 *

	 * @return the array of home boxes of red

	 */

	public Home[] getHomeRed() {

		return getHomeSquares(0);

	}



	/**

	 * <b>Accessory:</b>This method returns the Home Yellow boxes

	 * <p>

	 * <b> Postcondition:</b> Must return Home

	 *

	 * @return the array of home boxes of yellow

	 */

	public Home[] getHomeYellow() {

		return getHomeSquares(1);

	}



	/**

	 * <b>Accessory:</b>This method returns the Start Red boxes

	 * <p>

	 * <b> Postcondition:</b> Must return Start

	 *

	 * @return the array of start boxes of red

	 */

	public Start[] getStartRed() {

		return getStartSquares(0);

	}



	/**

	 * <b>Accessory:</b>This method returns the Start Yellow boxes

	 * <p>

	 * <b> Postcondition:</b> Must return Start

	 *

	 * @return the array of start boxes of yellow

	 */

	public Start[] getStartYellow() {

		return getStartSquares(1);

	}



	/**

	 * <b>Accessory:</b>This method returns the deck

	 * <p>

	 * <b> Postcondition:</b> Must return Deck

	 *

	 * @return the deck

	 */

	public Deck getDeck() {

		return deck;

	}



	/**

	 * <b>Accessory:</b>This method returns the fold

	 * <p>

	 * <b> Postcondition:</b> Must return int

	 *

	 * @return the fold

	 */

	public int getFold() {

		return fold;

	}



	/**

	 * <b>Accessory:</b>This method returns the chosen pawn

	 * <p>

	 * <b> Postcondition:</b> Must return Pawn

	 *

	 * @return the chosen pawn

	 * @param num zero-based pawn index

	 * @param numPlayer zero-based player index

	 */

	public Pawn getChoosePawn(int num, int numPlayer) {

		return players[numPlayer].getPawns()[num];

	}



	/**

	 * <b>Observer:</b>This method reset the Deck

	 */

	public void resetDeck() {

		deck=new Deck();

	}



	/**

	 * <b>Transformer:</b>This method sets the players array

	 * <p>

	 * <b> Postcondition:</b> Must return Player array

	 */

	public void setPlayer() {
		setPlayer(2);
	}

	/**
	 * Recreates the player array with two pawns per player.
	 *
	 * @param playerCount number of players, from 2 through 4
	 */
	public void setPlayer(int playerCount) {
		if (playerCount < 2 || playerCount > 4) {
			throw new IllegalArgumentException("Player count must be between 2 and 4");
		}
		players = new Player[playerCount];
		for (int playerIndex = 0; playerIndex < playerCount; playerIndex++) {
			players[playerIndex] = new Player(playerIndex);
		}
	}



	/**

	 * <b>Observer</b>:This method check if the table is empty

	 * <p>

	 * <b> Postcondition:</b> Must return boolean

	 *

	 * @return true if size of Deck is zero otherwise false

	 */

	public boolean tableIsEmpty() {

		return deck.sum()==0;

	}

	/**
	 * Returns the configured multiplayer size.
	 *
	 * @return number of active players
	 */
	public int getPlayerCount() {
		return players.length;
	}



	/**

	 * <b>Transformer:</b>This method sets if the fold

	 * <p>

	 * <b> Precondition:</b>the fold must be int

	 *

	 * @param fold this valuable is true if the player goes fold otherwise

	 */

	public void setFold(int fold) {
		if (fold != 0 && fold != 1) {
			throw new IllegalArgumentException("Fold state must be 0 or 1");
		}
		this.fold=fold;

	}



	/**

	 * <b>Observer</b>:This method check if they have winner

	 * <p>

	 * <b> Postcondition:</b> Must return int

	 *

	 * @return if we have winner

	 */

	public int win() {
		for (int playerIndex = 0; playerIndex < players.length; playerIndex++) {
			Pawn[] pawns = players[playerIndex].getPawns();
			if (pawns[0].isHome() && pawns[1].isHome()) {
				return playerIndex + 1;
			}
		}
		return 0;

	}



	/**

	 * <b>Observer</b>:This method find the picked card

	 * <p>

	 * <b> Postcondition:</b> Must return card

	 *

	 * @return the picked card

	 */

	public Card pickedCard(){

		Card card=deck.getCard();

		return card;

	}



	/**

	 * <b>Observer</b>:This method check if the player can kill the enemy pawn

	 * <p>

	 * <b> Precondition:</b>the numPawn must be int between 0,1

	 * <p>

	 * <b> Precondition:</b>the numPlayer must be int between 0,1

	 * <p>

	 * <b> Postcondition:</b> Must change the coordinates of pawns if its the same

	 * box

	 *

	 * @param numPawn   the number of selectable pawn

	 * @param numPlayer the number of selectable player

	 */

	public void checkKill(int numPawn, int numPlayer){
		if (numPlayer < 0 || numPlayer >= players.length
				|| numPawn < 0 || numPawn >= players[numPlayer].getPawns().length) {
			throw new IllegalArgumentException("Invalid pawn or player index");
		}

		Pawn pawn = getChoosePawn(numPawn, numPlayer);
		for (int playerIndex = 0; playerIndex < players.length; playerIndex++) {
			if (playerIndex == numPlayer) {
				continue;
			}
			for (Pawn enemyPawn : players[playerIndex].getPawns()) {
				if (pawn.getCurrentSquare() == enemyPawn.getCurrentSquare()) {
					enemyPawn.returnToStart();
					return;
				}
			}
		}
	}



	/**

	 * <b>Transformer:</b> sets the number of selectable player

	 * <p>

	 * <b> Precondition:</b>the num must be int

	 *

	 * @param num number of selectable player

	 */

	public void setSelectPlayer(int num) {
		if (num < 0 || num >= players.length) {
			throw new IllegalArgumentException("Invalid player index");
		}
		this.numSelectPlayer=num;

	}



	/**

	 * <b>Accessory:</b>This method returns the number of selectable player

	 * <p>

	 * <b> Postcondition:</b> Must return int

	 *

	 * @return numSelectPlayer

	 */

	public int getSelectPlayer() {

		return this.numSelectPlayer;

	}



	/**

	 * <b>Transformer:</b> sets the number of selectable pawn

	 * <p>

	 * <b> Precondition:</b>the num must be int

	 *

	 * @param num number of selectable pawn

	 */

	public void setSelectPawn(int num) {
		if (num < 0 || num >= 2) {
			throw new IllegalArgumentException("Invalid pawn index");
		}
		this.numSelectPawn=num;

	}



	/**

	 * <b>Accessory:</b>This method returns the number of selectable pawn

	 * <p>

	 * <b> Postcondition:</b> Must return int

	 *

	 * @return numSelectPawn

	 */

	public int getSelectPawn() {

		return this.numSelectPawn;

	}


	/**
	 * <b>Observer</b>: Checks if pawn landed on a StartSlide and triggers slide action
	 */
	/**
	 * Activates a slide when the pawn occupies an opponent-colored entrance.
	 *
	 * @param p pawn whose destination should be checked
	 */
	public void checkSlide(Pawn p) {
		for (int i = 0; i < 60; i++) {
			if (boxes[i] instanceof StartSlide && boxes[i] == p.getCurrentSquare()) {
				((StartSlide) boxes[i]).Act(players, p, boxes);
				break;
			}
		}
	}

	/**
	 * Tests whether the current card has a legal single-pawn action.
	 *
	 * @param pawnIndex zero-based current-player pawn index
	 * @return {@code true} when that pawn can legally act
	 */
	public boolean canMovePawn(int pawnIndex) {
		if (numSelectPlayer < 0 || numSelectPlayer >= players.length
				|| pawnIndex < 0 || pawnIndex >= players[numSelectPlayer].getPawns().length) {
			return false;
		}

		Pawn pawn = getChoosePawn(pawnIndex, numSelectPlayer);
		Pawn[] ownPawns = players[numSelectPlayer].getPawns();
		Card card = deck.getCard();
		SafetyTrack safetyTrack = safetyTracks[numSelectPlayer];
		int cardNumber = card.getCard();

		if (cardNumber == 1 || cardNumber == 2) {
			if (pawn.isStart()) {
				int exitIndex = pawn.getStartExitIndex();
				Pawn occupant = boxes[exitIndex].getOccupant();
				return occupant == null || occupant.getColor() != pawn.getColor();
			}
			return card.findBox(pawn, boxes, cardNumber, safetyTrack) != null;
		}
		if (cardNumber == 4) {
			return card.findBox(pawn, boxes, -4, safetyTrack) != null;
		}
		if (cardNumber == 10) {
			return card.findBox(pawn, boxes, 10, safetyTrack) != null
					|| card.findBox(pawn, boxes, -1, safetyTrack) != null;
		}
		if (cardNumber == 6) {
			if (!pawn.isStart()) {
				return false;
			}
			for (int enemyPlayer = 0; enemyPlayer < players.length; enemyPlayer++) {
				if (enemyPlayer == numSelectPlayer) {
					continue;
				}
				for (Pawn enemy : players[enemyPlayer].getPawns()) {
					if (isOuterTrackSquare(enemy.getCurrentSquare())) {
						return true;
					}
				}
			}
			return false;
		}
		if (cardNumber == 11) {
			return card.findBox(pawn, boxes, 11, safetyTrack) != null;
		}
		if (cardNumber == 7) {
			return card.findBox(pawn, boxes, 7, safetyTrack) != null;
		}
		return card.findBox(pawn, boxes, cardNumber, safetyTrack) != null;
	}

	/**
	 * Tests a two-player card 11 or Sorry! interaction.
	 *
	 * @param ownPawnIndex current player's pawn index
	 * @param enemyPawnIndex opponent pawn index
	 * @return whether the selected interaction is legal
	 */
	public boolean canSwapWithEnemy(int ownPawnIndex, int enemyPawnIndex) {
		if (players.length != 2) {
			return false;
		}
		return canSwapWithEnemy(
				ownPawnIndex, (numSelectPlayer + 1) % players.length, enemyPawnIndex);
	}

	/**
	 * Tests a card 11 swap or Sorry! replacement against a specific opponent.
	 *
	 * @param ownPawnIndex current player's pawn index
	 * @param enemyPlayerIndex opponent player index
	 * @param enemyPawnIndex opponent pawn index
	 * @return whether the selected interaction is legal
	 */
	public boolean canSwapWithEnemy(
			int ownPawnIndex, int enemyPlayerIndex, int enemyPawnIndex) {
		if (numSelectPlayer < 0 || numSelectPlayer >= players.length
				|| enemyPlayerIndex < 0 || enemyPlayerIndex >= players.length
				|| enemyPlayerIndex == numSelectPlayer
				|| ownPawnIndex < 0 || ownPawnIndex >= 2
				|| enemyPawnIndex < 0 || enemyPawnIndex >= 2) {
			return false;
		}
		Card card = deck.getCard();
		if (card.getCard() != 6 && card.getCard() != 11) {
			return false;
		}
		Pawn ownPawn = getChoosePawn(ownPawnIndex, numSelectPlayer);
		Pawn enemyPawn = getChoosePawn(enemyPawnIndex, enemyPlayerIndex);
		if (!isOuterTrackSquare(enemyPawn.getCurrentSquare())) {
			return false;
		}
		if (card.getCard() == 6) {
			return ownPawn.isStart();
		}
		return isOuterTrackSquare(ownPawn.getCurrentSquare());
	}

	/**
	 * Tests every distribution of seven steps between the current player's pawns.
	 *
	 * @return whether any legal split-card-7 combination exists
	 */
	public boolean canSplitSeven() {
		if (numSelectPlayer < 0 || numSelectPlayer >= players.length
				|| deck.getCard().getCard() != 7) {
			return false;
		}
		Pawn[] pawns = players[numSelectPlayer].getPawns();
		SafetyTrack safetyTrack = safetyTracks[numSelectPlayer];
		Card card = deck.getCard();
		for (int firstSteps = 1; firstSteps <= 6; firstSteps++) {
			Square first = card.findBox(pawns[0], boxes, firstSteps, safetyTrack);
			Square second = card.findBox(pawns[1], boxes, 7 - firstSteps, safetyTrack);
			if (distinctDestinations(first, second, safetyTrack)) {
				return true;
			}
			first = card.findBox(pawns[1], boxes, firstSteps, safetyTrack);
			second = card.findBox(pawns[0], boxes, 7 - firstSteps, safetyTrack);
			if (distinctDestinations(first, second, safetyTrack)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests every action offered by the current card.
	 *
	 * @return whether the current player can legally use the current card
	 */
	public boolean hasLegalMove() {
		if (numSelectPlayer < 0 || numSelectPlayer >= players.length) {
			return false;
		}
		int cardNumber = deck.getCard().getCard();
		Pawn[] pawns = players[numSelectPlayer].getPawns();
		if (cardNumber == 3 || cardNumber == 5) {
			SafetyTrack safetyTrack = safetyTracks[numSelectPlayer];
			return deck.getCard().findBox(pawns[0], boxes, cardNumber, safetyTrack) != null
					|| deck.getCard().findBox(pawns[1], boxes, cardNumber, safetyTrack) != null;
		}
		if (cardNumber == 7) {
			return canMovePawn(0) || canMovePawn(1) || canSplitSeven();
		}
		if (cardNumber == 8 || cardNumber == 12) {
			return true;
		}
		if (cardNumber == 11
				&& (canSwapWithAnyEnemy(0) || canSwapWithAnyEnemy(1))) {
			return true;
		}
		for (int i = 0; i < pawns.length; i++) {
			if (canMovePawn(i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests one current-player pawn against every opponent pawn.
	 *
	 * @param ownPawnIndex current player's pawn index
	 * @return whether at least one legal swap or replacement exists
	 */
	public boolean canSwapWithAnyEnemy(int ownPawnIndex) {
		for (int enemyPlayer = 0; enemyPlayer < players.length; enemyPlayer++) {
			if (enemyPlayer == numSelectPlayer) {
				continue;
			}
			for (int enemyPawn = 0; enemyPawn < 2; enemyPawn++) {
				if (canSwapWithEnemy(ownPawnIndex, enemyPlayer, enemyPawn)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean distinctDestinations(Square first, Square second, SafetyTrack safetyTrack) {
		if (first == null || second == null) {
			return false;
		}
		if (first != second) {
			return true;
		}
		for (int i = 0; i < 2; i++) {
			Square home = safetyTrack.getHomeSquare(i);
			if (first == home && safetyTrack.getHomeSquare(1 - i).getOccupant() == null) {
				return true;
			}
		}
		return false;
	}

	private boolean isOuterTrackSquare(Square square) {
		for (Square box : boxes) {
			if (box == square) {
				return true;
			}
		}
		return false;
	}

	/**

	 * <b>Observer</b>:This method move the pawns in board

	 * <p>

	 * <b> Precondition:</b>the allPawns must be boolean

	 * <p>

	 * <b> Precondition:</b>the numPawn must be int

	 * <p>

	 * <b> Postcondition:</b> Must change the coordinates of pawns and return 1 if

	 * its succeed 0 in failure

	 *

	 * @param allPawns if the move the two pawns of player

	 * @param numPawn  the number of selectable pawn

	 * @return returns 1 if its succeed 0 in failure

	 */

	public int makeMove(boolean allPawns, int numPawn) {
		return makeMove(allPawns, numPawn, MoveDirection.FORWARD);
	}

	/**
	 * Applies the current card in the requested direction.
	 *
	 * @param allPawns whether the card should move both eligible pawns
	 * @param numPawn selected pawn index for a single-pawn action
	 * @param direction requested movement direction
	 * @return {@code 1} when movement succeeded; otherwise {@code 0}
	 */
	public int makeMove(boolean allPawns, int numPawn, MoveDirection direction) {
		if (numSelectPlayer < 0 || numSelectPlayer >= players.length
				|| direction == null
				|| (allPawns && direction != MoveDirection.FORWARD)
				|| (!allPawns && (numPawn < 0
						|| numPawn >= players[numSelectPlayer].getPawns().length))) {
			return 0;
		}

		int made=0;

		Pawn tmpPawn1=null,tmpPawn2=null;

		Card tmpCard=deck.getCard();
		SafetyTrack safetyTrack = safetyTracks[numSelectPlayer];

		if(allPawns==true){

			tmpPawn1=getChoosePawn(0, numSelectPlayer);

			tmpPawn2=getChoosePawn(1, numSelectPlayer);

			made=tmpCard.Act(tmpPawn1, tmpPawn2, boxes, safetyTrack);

		}else{

			tmpPawn1=getChoosePawn(numPawn, numSelectPlayer);

			if (!(tmpCard instanceof NumberCard)) {
				return 0;
			}
			made=((NumberCard) tmpCard).Act(tmpPawn1, boxes, safetyTrack, direction);

		}

		if(made==1){

			if(allPawns){
				checkSlide(tmpPawn1);

				checkKill(0, numSelectPlayer);

				checkSlide(tmpPawn2);

				checkKill(1, numSelectPlayer);

			}else{

				checkSlide(tmpPawn1);

				checkKill(numPawn, numSelectPlayer);

			}

			return 1;

		}else{

			return 0;

		}

	}

	/**
	 * Applies a split card 7 and performs resulting slides and captures.
	 *
	 * @param firstPawnIndex pawn receiving the first share
	 * @param firstSteps steps assigned to that pawn, from 1 through 6
	 * @return {@code 1} when both moves succeeded; otherwise {@code 0}
	 */
	public int makeSevenSplit(int firstPawnIndex, int firstSteps) {
		if (numSelectPlayer < 0 || numSelectPlayer >= players.length) {
			return 0;
		}
		Card currentCard = deck.getCard();
		Pawn[] currentPawns = players[numSelectPlayer].getPawns();
		if (!(currentCard instanceof NumberSeven) || currentPawns.length != 2
				|| firstPawnIndex < 0 || firstPawnIndex >= currentPawns.length
				|| firstSteps < 1 || firstSteps > 6) {
			return 0;
		}

		int secondPawnIndex = (firstPawnIndex + 1) % currentPawns.length;
		Pawn firstPawn = currentPawns[firstPawnIndex];
		Pawn secondPawn = currentPawns[secondPawnIndex];
		SafetyTrack safetyTrack = safetyTracks[numSelectPlayer];
		int made = ((NumberSeven) currentCard).splitMove(
				firstPawn, secondPawn, firstSteps, boxes, safetyTrack);

		if (made == 1) {
			checkSlide(firstPawn);
			checkKill(firstPawnIndex, numSelectPlayer);
			checkSlide(secondPawn);
			checkKill(secondPawnIndex, numSelectPlayer);
		}
		return made;
	}



	/**

	 * <b>Observer</b>:This method move the pawns in board and specifically for

	 * cards Sorry and 11

	 * <p>

	 * <b> Precondition:</b>the numPawn must be int

	 * <p>

	 * <b> Precondition:</b>the numEnemyPawn must be int

	 * <p>

	 * <b> Postcondition:</b> Must change the coordinates of pawns and return 1 if

	 * its succeed 0 in failure

	 *

	 * @param numPawn      the number of selectable pawn

	 * @param numEnemyPawn the number of enemy pawn

	 * @return returns 1 if its succeed 0 in failure

	 */

	public int makeMove(int numPawn, int numEnemyPawn) {
		if (players.length != 2) {
			return 0;
		}
		return makeMove(
				numPawn, (numSelectPlayer + 1) % players.length, numEnemyPawn);
	}

	/**
	 * Applies card 11 or Sorry! against a selected opponent pawn.
	 *
	 * @param numPawn current player's pawn index
	 * @param enemyPlayerIndex selected opponent index
	 * @param numEnemyPawn selected opponent pawn index
	 * @return {@code 1} when the action succeeded; otherwise {@code 0}
	 */
	public int makeMove(int numPawn, int enemyPlayerIndex, int numEnemyPawn) {
		if (numSelectPlayer < 0 || numSelectPlayer >= players.length
				|| enemyPlayerIndex < 0 || enemyPlayerIndex >= players.length
				|| enemyPlayerIndex == numSelectPlayer
				|| numPawn < 0 || numPawn >= 2
				|| numEnemyPawn < 0 || numEnemyPawn >= 2) {
			return 0;
		}

		int made=0;

		Pawn tmpPawn1;

		Card tmpCard=deck.getCard();
		SafetyTrack safetyTrack = safetyTracks[numSelectPlayer];
		tmpPawn1 = getChoosePawn(numPawn, numSelectPlayer);
		Pawn tmpPawn2 = getChoosePawn(numEnemyPawn, enemyPlayerIndex);
		made = tmpCard.Act(tmpPawn1, tmpPawn2, boxes, safetyTrack);

		if (made == 1) {
			if (tmpCard.getCard() == 6) {
				tmpPawn2.returnToStart();
				checkSlide(tmpPawn1);
			} else if (tmpCard.getCard() == 11) {
				checkSlide(tmpPawn1);
				checkSlide(tmpPawn2);
			}
		}

		return made == 1 ? 1 : 0;

	}

}
