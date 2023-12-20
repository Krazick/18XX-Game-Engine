package ge18xx.toplevel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.logging.log4j.Logger;

//
//  PlayerInputFrame.java
//  Game_18XX
//
//  Created by Mark Smith on 12/6/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.game.GameSet;
import ge18xx.network.JGameClient;
import ge18xx.network.NetworkPlayer;

import geUtilities.xml.XMLFrame;
import geUtilities.ElementName;
import geUtilities.XMLNode;
import swingDelays.KButton;

public class PlayerInputFrame extends XMLFrame implements ActionListener, FocusListener {
	public static final PlayerInputFrame NO_PLAYER_INPUT_FRAME = null;
	public static final String NO_NAME = "";
	public static final int NO_PLAYER_INDEX = -1;
	public static final int NO_PLAYERS = 0;
	public static final String INVALID_NAME = "INVALID-NAME";
	public static final String DUPLICATE_NAME = "DUPLICATE-NAME";
	public static final ElementName EN_PLAYERS = new ElementName ("Players");
	public static final ElementName EN_PLAYER = new ElementName ("Player");
	private static final String RANDOMIZE_ORDER = "Randomize Order";
	private static final String REASON_NO_RANDOMIZE = "Must have at least two Players entered to Randomize";
	private static final long serialVersionUID = 1L;
	static final int MAX_PLAYERS = 8;
	static final int MAX_GAMES = 5;
	GameManager gameManager;
	GameSet gameSet;
	int playerCount;
	boolean badPlayerList;
	JTextField [] playerNames;
	JLabel labelPlayerCount;
	KButton randomizeButton;
	JPanel centerPanel;
	Logger logger;

	public PlayerInputFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager);
		
		String tClientUserName;
		
		gameManager = (GameManager) gameEngineManager;
		logger = gameManager.getLogger ();

		buildWestPanel ();
		buildCenterPanel ();

		gameSet = new GameSet (this);

		tClientUserName = gameManager.getClientUserName ();
		addPlayer (tClientUserName);
		setBadPlayerList (false);
		lockClientPlayer ();
		pack ();
		playerNames [0].transferFocus ();
	}

	public void setBadPlayerList (boolean aBadPlayerList) {
		badPlayerList = aBadPlayerList;
	}
	
	public boolean getBadPlayerList () {
		return badPlayerList;
	}
	
	private JPanel buildPlayersPanel () {
		int tIndex;
		JPanel tPlayersPanel;

		tPlayersPanel = new JPanel ();
		tPlayersPanel.setLayout (new BoxLayout (tPlayersPanel, BoxLayout.Y_AXIS));
		tPlayersPanel.add (Box.createVerticalStrut (5));
		playerNames = new JTextField [MAX_PLAYERS + 1];

		for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
			playerNames [tIndex] = new JTextField (10);
			playerNames [tIndex].addActionListener (this);
			playerNames [tIndex].addFocusListener (this);
			playerNames [tIndex].setMaximumSize (playerNames [tIndex].getMinimumSize ());
			buildOnePlayerPanel (tIndex, tPlayersPanel);
			tPlayersPanel.add (Box.createVerticalStrut (5));
		}
		// Randomize Player Order Button
		randomizeButton = setupButton (RANDOMIZE_ORDER, RANDOMIZE_ORDER, this, Component.CENTER_ALIGNMENT);
		tPlayersPanel.add (randomizeButton);
		tPlayersPanel.add (Box.createVerticalStrut (10));
		labelPlayerCount = new JLabel ();
		setPlayerCount ();

		return tPlayersPanel;
	}

	private void buildCenterPanel () {
		centerPanel = new JPanel ();
		centerPanel.setLayout (new BoxLayout (centerPanel, BoxLayout.PAGE_AXIS));
		centerPanel.add (Box.createVerticalStrut (10));
		centerPanel.add (labelPlayerCount);
		add (centerPanel, BorderLayout.CENTER);
	}

	private void buildWestPanel () {
		JPanel tWestPanel;
		JPanel tPlayersPanel;

		tPlayersPanel = buildPlayersPanel ();

		tWestPanel = new JPanel ();
		tWestPanel.setLayout (new BoxLayout (tWestPanel, BoxLayout.X_AXIS));
		tWestPanel.add (Box.createHorizontalStrut (5));
		tWestPanel.add (tPlayersPanel);
		tWestPanel.add (Box.createHorizontalStrut (5));
		add (tWestPanel, BorderLayout.WEST);
	}

	private void buildOnePlayerPanel (int aIndex, JPanel aPlayersBox) {
		JPanel tOnePlayerPanel;
		JLabel tLabel;

		tLabel = new JLabel (" Player " + (aIndex + 1) + ": ");

		tOnePlayerPanel = new JPanel ();
		tOnePlayerPanel.setLayout (new BoxLayout (tOnePlayerPanel, BoxLayout.X_AXIS));
		tOnePlayerPanel.add (Box.createHorizontalStrut (10));
		tOnePlayerPanel.add (tLabel);
		tOnePlayerPanel.add (Box.createHorizontalGlue ());
		tOnePlayerPanel.add (playerNames [aIndex]);
		tOnePlayerPanel.add (Box.createHorizontalStrut (10));
		aPlayersBox.add (tOnePlayerPanel);
	}

	public void lockClientPlayer () {
		String tClientUserName;
		int tIndex;

		tClientUserName = gameManager.getClientUserName ();
		for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
			if (tClientUserName.equals (playerNames [tIndex].getText ())) {
				playerNames [tIndex].setEditable (false);
			} else {
				playerNames [tIndex].setEditable (true);
			}
		}
	}

	public void addGameInfo () {
		gameSet.addGameInfo (centerPanel);
	}

	public void handleHotseatGameStart (GameInfo aGameInfo) {
		setVisible (false);
		initiateGame (aGameInfo);
		logger.info ("Start new Game [" + aGameInfo.getGameName () + "] with Players [" + getPlayersInOrder () + "]");
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		if (RANDOMIZE_ORDER.equals (aEvent.getActionCommand ())) {
			randomizePlayerOrder ();
		}
	}

	public String getPlayersInOrder () {
		String tPlayersInOrder = "";
		List<String> tPlayerNames;

		tPlayerNames = getPlayerNames ();
		tPlayersInOrder = tPlayerNames.stream ().collect (Collectors.joining (","));

		return tPlayersInOrder;
	}

	public List<String> getPlayerNames () {
		List<String> tPlayerNames;
		int tIndex;
		String tName;

		tPlayerNames = new ArrayList<> ();
		for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
			tName = playerNames [tIndex].getText ();
			if (!(tName.equals (NO_NAME))) {
				tPlayerNames.add (tName);
			}
		}

		return tPlayerNames;
	}

	public void handleResetPlayerOrder (String aPlayerOrder, String aBroadcast) {
		int tIndex;
		JGameClient tJGameClient;
		String [] tPlayerNames;

		tPlayerNames = aPlayerOrder.split (",");
		for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
			if (tIndex < tPlayerNames.length) {
				playerNames [tIndex].setText (tPlayerNames [tIndex]);
			} else {
				playerNames [tIndex].setText (NO_NAME);
			}
		}
		lockClientPlayer ();
		tJGameClient = gameManager.getNetworkJGameClient ();
		tJGameClient.appendToChat (aBroadcast);
	}

	public void randomizePlayerOrder () {
		int tIndex;
		int tFoundCount;
		List<String> tPlayerNames;
		boolean tShouldRandomize;

		tShouldRandomize = gameManager.shouldRandomize ();
		if (tShouldRandomize) {
			tPlayerNames = getPlayerNames ();
			tFoundCount = tPlayerNames.size ();
			Random tGenerator = new Random ();
			Collections.shuffle (tPlayerNames, tGenerator);
			for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
				if (tIndex < tFoundCount) {
					playerNames [tIndex].setText (tPlayerNames.get (tIndex));
				} else {
					playerNames [tIndex].setText (NO_NAME);
				}
			}
			lockClientPlayer ();
		}
	}

	@Override
	public void focusGained (FocusEvent aEvent) {
	}

	@Override
	public void focusLost (FocusEvent aEvent) {
		Object tEventObject = aEvent.getSource ();

		if (tEventObject instanceof JTextField) {
			setPlayerCount ();
			if (playerCount > 1) {
				if (getBadPlayerList ()) {
					randomizeButton.setEnabled (false);
					randomizeButton.setToolTipText ("Bad Player List Entered");
				} else {
					randomizeButton.setEnabled (true);
					randomizeButton.setToolTipText ("");
				}
			} else {
				randomizeButton.setEnabled (false);
				randomizeButton.setToolTipText (REASON_NO_RANDOMIZE);
			}
		}
	}

	public String getFirstPlayerName () {
		int tIndex;
		String tFirstPlayerName = "";
		String tName;

		for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
			tName = playerNames [tIndex].getText ();
			if (tFirstPlayerName.equals (NO_NAME)) {
				if (tName != null) {
					if (!(tName.equals (NO_NAME))) {
						tFirstPlayerName = tName;
					}
				}
			}
		}

		return tFirstPlayerName;
	}

	public void clearOtherPlayers (String aPlayerName) {
		int tIndex;
		
		playerNames [0].setText (aPlayerName);
		for (tIndex = 1; tIndex < MAX_PLAYERS; tIndex++) {
			playerNames [tIndex].setText (NO_NAME);
		}
		setPlayerCount ();
	}

	public int getTFPlayerCount () {
		String tName;
		int tIndex;
		int tPlayerCount;
		boolean tDuplicateName;

		tPlayerCount = 0;
		setBadPlayerList (false);
		for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
			tName = playerNames [tIndex].getText ();
			if (tName != null) {
				if (!(tName.equals (NO_NAME))) {
					if (NetworkPlayer.validPlayerName (tName)) {
						tPlayerCount++;
					} else {
						playerNames [tIndex].setText (INVALID_NAME);
						setBadPlayerList (true);
					}
				}
			}
		}
		tDuplicateName = isDuplicate (tPlayerCount);
		if (tDuplicateName) {
			playerNames [tPlayerCount - 1].setText (DUPLICATE_NAME);
			tPlayerCount--;
			setBadPlayerList (true);
		}
		
		return tPlayerCount;
	}

	public void updatePlayerCountLabel () {
		int tActualCount;
		String tPrefix;
		String tLabel;

		if (playerCount == NO_PLAYERS) {
			tActualCount = 0;
		} else {
			tActualCount = playerCount;
		}

		if (gameManager.isNetworkGame ()) {
			tPrefix = "Players Connected: ";
		} else {
			tPrefix = "Players Entered: ";
		}
		if (getBadPlayerList ()) {
			tLabel = "Bad Player List Entered";
		} else {
			tLabel = tPrefix + tActualCount;
		}
		labelPlayerCount.setText (tLabel);
	}

	public GameSet getGameSet () {
		return gameSet;
	}

	public String getPlayerName (int tIndex) {
		String tPlayerName;

		tPlayerName = NO_NAME;
		if ((tIndex >= 0) && (tIndex < playerCount)) {
			tPlayerName = playerNames [tIndex].getText ();
		}

		return tPlayerName;
	}

	public int getIndexOfPlayer (String aPlayerName) {
		int tIndex, tPlayerIndex = NO_PLAYER_INDEX;

		if ((aPlayerName != null) && (aPlayerName != "")) {
			for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
				if (aPlayerName.equals (playerNames [tIndex].getText ())) {
					tPlayerIndex = tIndex;
				}
			}
		}

		return tPlayerIndex;
	}

	/**
	 * Check all other player names entered so far for a duplicate. Ignore the CurrentIndex Player
	 * 
	 * @param aPlayerCount The Total PlayerCount to this point
	 * @return True if the same player name is any other player name, otherwise false.
	 * 
	 */
	public boolean isDuplicate (int aPlayerCount) {
		boolean tIsDuplicate;
		int tIndex;
		String tPlayerName;
		
		tIsDuplicate = false;
		if (aPlayerCount >= 2) {
			tPlayerName = playerNames [aPlayerCount - 1].getText ();
			if ((tPlayerName != null) && (!tPlayerName.equals (NO_NAME))) {
				for (tIndex = 0; tIndex < (aPlayerCount - 1); tIndex++) {
					if (tPlayerName.equals (playerNames [tIndex].getText ())) {
						tIsDuplicate = true;				
					}
				}
			}
		}
		
		return tIsDuplicate;
	}
	
	public boolean isAlreadyPresent (String aPlayerName) {
		int tIndex;
		boolean tIsAlreadyPresent = false;

		if ((aPlayerName != null) && (!aPlayerName.equals (NO_NAME))) {
			for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
				if (aPlayerName.equals (playerNames [tIndex].getText ())) {
					tIsAlreadyPresent = true;
				}
			}
		}

		return tIsAlreadyPresent;
	}

	public void addPlayer (String aPlayerName) {
		int tPlayerCount;

		tPlayerCount = getTFPlayerCount ();
		if (!isAlreadyPresent (aPlayerName)) {
			playerNames [tPlayerCount++].setText (aPlayerName);
			logNewPlayer (aPlayerName);
		}
		setPlayerCount (tPlayerCount);
	}

	private void logNewPlayer (String aPlayerName) {
		if (isNetworkGame ()) {
			logger.info ("New Network Player " + aPlayerName + " added to the Player List.");
		} else {
			logger.info ("New Player " + aPlayerName + " added to the Player List.");
		}
	}

	public void addNetworkPlayer (String aPlayerName) {
		addPlayer (aPlayerName);
	}

	public void removeAllPlayers () {
		int tIndex;

		if (playerCount > NO_PLAYERS) {
			for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
				playerNames [tIndex].setText (NO_NAME);
			}
		}
		setPlayerCount (NO_PLAYERS);
	}

	public void removeNetworkPlayer (String aPlayerName) {
		int tPlayerIndex, tIndex;

		tPlayerIndex = getIndexOfPlayer (aPlayerName);

		if (tPlayerIndex != NO_PLAYER_INDEX) {
			for (tIndex = tPlayerIndex; tIndex < (MAX_PLAYERS - 1); tIndex++) {
				playerNames [tIndex].setText (getPlayerName (tIndex + 1));
			}
			playerNames [MAX_PLAYERS - 1].setText (NO_NAME);
			logger.info ("Network Player " + aPlayerName + " removed from the Player List.");
		}
		setPlayerCount ();
	}

	public String [] getPlayers () {
		String [] tPlayers;
		int tIndex, tPlayersIndex;
		String tPlayerName;

		tPlayers = new String [playerCount];
		tPlayersIndex = 0;
		for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
			tPlayerName = playerNames [tIndex].getText ();
			if (!tPlayerName.equals (NO_NAME)) {
				tPlayers [tPlayersIndex++] = tPlayerName;
			}
		}

		return tPlayers;
	}

	public void setPlayerCount () {
		setPlayerCount (getTFPlayerCount ());
	}

	public void setPlayerCount (int aPlayerCount) {
		playerCount = aPlayerCount;
		updatePlayerCountLabel ();
		if (gameSet != null) {
			gameSet.setGameRadioButtons (aPlayerCount, getBadPlayerList ());
		}
	}

	public int getPlayerCount () {
		return playerCount;
	}

	public GameInfo getSelectedGame () {
		return gameSet.getSelectedGame ();
	}

	public void initiateGame (GameInfo aGameInfo) {
		gameManager.initiateGame (aGameInfo);
	}

	public void loadGame () {
		setVisible (false);
		gameManager.setPlayerInputFrame (this);
		gameManager.loadSavedGame ();
	}

	public boolean isNetworkGame () {
		return gameManager.isNetworkGame ();
	}

	public JGameClient getNetworkJGameClient () {
		return gameManager.getNetworkJGameClient ();
	}

	public void addGameInfoPanel (JPanel aGameInfoPanel) {
		if (isNetworkGame ()) {
			JGameClient tJGameClient = getNetworkJGameClient ();
			tJGameClient.addGameInfoPanel (aGameInfoPanel, gameSet);
		} else {
			add (aGameInfoPanel, BorderLayout.EAST);
			pack ();
		}
	}

	public void handleGameSelection (int aGameIndex, XMLNode aVariantEffectsNode, String aBroadcast) {
		JGameClient tJGameClient;
		String tName;

		gameSet.handleGameSelection (aGameIndex, false);
		gameSet.handleGameVariants (aVariantEffectsNode);
		tJGameClient = gameManager.getNetworkJGameClient ();
		tJGameClient.appendToChat (aBroadcast);
		tName = aBroadcast.substring (0, aBroadcast.indexOf (" "));
		tJGameClient.playerReady (tName);
	}

	public int getSelectedGameIndex () {
		return gameSet.getSelectedGameIndex ();
	}

	public void setSelectedGameIndex (int aGameIndex) {
		gameSet.setSelectedGameIndex (aGameIndex);
	}
}