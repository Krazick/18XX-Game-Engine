package ge18xx.toplevel;

//
//  PlayerInputFrame.java
//  Game_18XX
//
//  Created by Mark Smith on 12/6/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

import ge18xx.game.GameManager;
import ge18xx.game.GameSet;
import ge18xx.game.GameInfo;
import ge18xx.game.Game_18XX;
import ge18xx.network.JGameClient;
import ge18xx.network.NetworkPlayer;
import ge18xx.utilities.ElementName;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.*;

import java.util.*;
import java.util.stream.Collectors;

import javax.swing.*;

public class PlayerInputFrame extends XMLFrame implements ActionListener, FocusListener {
	public static final String NO_NAME = "";
	public static final int NO_PLAYER_INDEX = -1;
	public static final int NO_PLAYERS = 0;
	public static final String INVALID_NAME = "INVALID-NAME";
	public static final ElementName EN_PLAYERS = new ElementName ("Players");
	public static final ElementName EN_PLAYER = new ElementName ("Player");
	private static final String RANDOMIZE_ORDER = "Randomize Order";
	private static final String REASON_NO_RANDOMIZE = "Must have at least two Players entered to Randomize";
	private static final long serialVersionUID = 1L;
	static final int MAX_PLAYERS = 8;
	static final int MAX_GAMES = 5;
	GameManager gameManager;
	GameSet gameSet;
	JTextField [] tfPlayerNames;
	JLabel labelPlayerCount;
	JButton randomizeButton;
	int playerCount;
	JPanel centerComponents;
	JPanel westComponents;
	Game_18XX parentFrame;
	
	public PlayerInputFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, "Player Input Frame");
		setGameManager (aGameManager);
		int tIndex;
		westComponents = new JPanel ();
		centerComponents = new JPanel ();
		JLabel tLabel;
		Container tOnePlayerBox;
		Container tPlayersBox = Box.createVerticalBox ();
		Container tWestBox = Box.createHorizontalBox ();
		String tClientUserName;
		tPlayersBox.add (Box.createVerticalStrut (5));
		
		tfPlayerNames = new JTextField [MAX_PLAYERS + 1];
		BoxLayout tCenterLayout = new BoxLayout (centerComponents, BoxLayout.PAGE_AXIS);
		centerComponents.setLayout (tCenterLayout);
		BoxLayout tWestLayout = new BoxLayout (westComponents, BoxLayout.PAGE_AXIS);
		westComponents.setLayout (tWestLayout);
		
		for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
			tLabel = new JLabel (" Player " + (tIndex + 1) + ": ");
			tfPlayerNames [tIndex] = new JTextField (10);
			tfPlayerNames [tIndex].addActionListener (this);
			tfPlayerNames [tIndex].addFocusListener (this);
			
			tOnePlayerBox = Box.createHorizontalBox ();
			tOnePlayerBox.add (Box.createHorizontalStrut (10));
			tOnePlayerBox.add (tLabel);
			tOnePlayerBox.add (Box.createHorizontalGlue ());
			tOnePlayerBox.add (tfPlayerNames [tIndex]);
			tOnePlayerBox.add (Box.createHorizontalStrut (10));
			tPlayersBox.add (tOnePlayerBox);
			tPlayersBox.add (Box.createVerticalStrut (5));
		}
		randomizeButton = new JButton (RANDOMIZE_ORDER);
		randomizeButton.setActionCommand (RANDOMIZE_ORDER);
		randomizeButton.addActionListener (this);
		randomizeButton.setEnabled (false);
		randomizeButton.setToolTipText (REASON_NO_RANDOMIZE);
		tPlayersBox.add (randomizeButton);
		tPlayersBox.add (Box.createVerticalStrut (10));
		labelPlayerCount = new JLabel ();
		setPlayerCount ();
		centerComponents.add (Box.createVerticalStrut (10));
		centerComponents.add (labelPlayerCount);
		tWestBox.add (Box.createHorizontalStrut (5));
		tWestBox.add (tPlayersBox);
		tWestBox.add (Box.createHorizontalStrut (5));
		westComponents.add (tWestBox);
		add (centerComponents, BorderLayout.CENTER);
		add (westComponents, BorderLayout.WEST);
		
		gameSet = new GameSet (this);
		
		tClientUserName = gameManager.getClientUserName ();
		addPlayer (tClientUserName);
		fixClientPlayer ();
		pack ();
		tfPlayerNames [0].transferFocus ();
	}
	
	public void fixClientPlayer () {
		String tClientUserName;
		int tIndex;
		
		tClientUserName = gameManager.getClientUserName ();
		for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
			if (tClientUserName.equals (tfPlayerNames [tIndex].getText ())) {
				tfPlayerNames [tIndex].setEditable (false);
			} else {
				tfPlayerNames [tIndex].setEditable (true);
			}
		}
	}
	
	public void addGameInfo () {
		gameSet.addGameInfo (centerComponents);
	}
	
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
	
		tPlayerNames = new ArrayList<String> ();
		for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
			tName = tfPlayerNames [tIndex].getText ();
			if (! (tName.equals (NO_NAME))) {
				tPlayerNames.add (tName);
			}
		}

		return tPlayerNames;
    }
    
    public void handleResetPlayerOrder (String aPlayerOrder, String aBroadcast) {
		int tIndex;
		JGameClient tJGameClient;
		String [] tPlayerNames;
		
		System.out.println ("Names: " + aPlayerOrder);
		tPlayerNames = aPlayerOrder.split (",");
		for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
			if (tIndex < tPlayerNames.length) {
				tfPlayerNames [tIndex].setText (tPlayerNames [tIndex]);
			} else {
				tfPlayerNames [tIndex].setText (NO_NAME);
			}
		}
		fixClientPlayer ();
		tJGameClient = gameManager.getNetworkJGameClient ();
		tJGameClient.appendToChat (aBroadcast);
    }
    
	public void randomizePlayerOrder () {
		int tIndex;
		int tFoundCount;
		List<String> tPlayerNames;
		
		tPlayerNames = getPlayerNames ();
		tFoundCount = tPlayerNames.size ();
		Random tGenerator = new Random ();
		Collections.shuffle (tPlayerNames, tGenerator);
		for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
			if (tIndex < tFoundCount) {
				tfPlayerNames [tIndex].setText (tPlayerNames.get (tIndex));
			} else {
				tfPlayerNames [tIndex].setText (NO_NAME);
			}
		}
		fixClientPlayer ();
	}

	public void focusGained (FocusEvent aEvent) {
    }
	
    public void focusLost (FocusEvent aEvent) {
		Object tEventObject = aEvent.getSource ();
		
		if (tEventObject instanceof JTextField) {
			setPlayerCount (getTFPlayerCount ());
			gameSet.setGameRadioButtons (playerCount);
			if (playerCount > 1) {
				randomizeButton.setEnabled (true);
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
			tName = tfPlayerNames [tIndex].getText ();
			if (tFirstPlayerName.equals (NO_NAME)) {
				if (tName != null) {
					if (! (tName.equals (NO_NAME))) {
						tFirstPlayerName = tName;
					}
				}				
			}
		}
    	
		return tFirstPlayerName;
    }
    
    public void clearOtherPlayers (String aPlayerName) {
    	int tIndex;
    	
    	tfPlayerNames [0].setText (aPlayerName);
		for (tIndex = 1; tIndex < MAX_PLAYERS; tIndex++) {
			tfPlayerNames [tIndex].setText (NO_NAME);
		}
		setPlayerCount (getTFPlayerCount ());
		updatePlayerCountLabel ();
    }
    
    public int getTFPlayerCount () {
    	String tName;
    	int tIndex, tPlayerCount = 0;
    	
		for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
			tName = tfPlayerNames [tIndex].getText ();
			if (tName != null) {
				if (! (tName.equals (NO_NAME))) {
					if (NetworkPlayer.validPlayerName (tName)) {
						tPlayerCount++;
					} else {
						tfPlayerNames [tIndex].setText (INVALID_NAME);
					}
				}
			}
		}

    	return tPlayerCount;
    }
    
    public void updatePlayerCountLabel () {
    	int tActualCount;
    	String tPrefix;
    	
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
    	labelPlayerCount.setText (tPrefix + tActualCount);
    }
    
    public GameManager getGameManager () {
        return gameManager;
    }
    
	public GameSet getGameSet () {
		return gameSet;
	}
	
	public String getPlayerName (int tIndex) {
		String tPlayerName;
		
		tPlayerName = NO_NAME;
		if ((tIndex >= 0) && (tIndex < playerCount)) {
			tPlayerName = tfPlayerNames [tIndex].getText ();
		}
		
		return tPlayerName;
	}
	
	public int getIndexOfPlayer (String aPlayerName) {
		int tIndex, tPlayerIndex = NO_PLAYER_INDEX;
		
		if ((aPlayerName != null) && (aPlayerName != "")) {
			for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
				if (aPlayerName.equals (tfPlayerNames [tIndex].getText ())) {
					tPlayerIndex = tIndex;
				}
			}
		}
		
		return tPlayerIndex;
	}
	
	public boolean isAlreadyPresent (String aPlayerName) {
		int tIndex;
		boolean tIsAlreadyPresent = false;
		
		if ((aPlayerName != null) && (! aPlayerName.equals (NO_NAME))) {
			for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
				if (aPlayerName.equals (tfPlayerNames [tIndex].getText ())) {
					tIsAlreadyPresent = true;
				}
			}
		}
		
		return tIsAlreadyPresent;
	}
	
	public void addPlayer (String aPlayerName) {
		int tPlayerCount;

		tPlayerCount = getTFPlayerCount ();
		if (! isAlreadyPresent (aPlayerName)) {
			tfPlayerNames [tPlayerCount++].setText (aPlayerName);
		}
		setPlayerCount (tPlayerCount);
		gameSet.setGameRadioButtons (tPlayerCount);		
	}
	
	public void addNetworkPlayer (String aPlayerName) {
		addPlayer (aPlayerName);
	}
	
	public void removeAllPlayers () {
		int tIndex;
		
		if (playerCount > NO_PLAYERS) {
			for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
				tfPlayerNames [tIndex].setText (NO_NAME);
			}
		}
		
		setPlayerCount (NO_PLAYERS);
		gameSet.setGameRadioButtons (NO_PLAYERS);
	}
	
	public void removeNetworkPlayer (String aPlayerName) {
		int tPlayerIndex, tIndex, tPlayerCount;
		
		tPlayerIndex = getIndexOfPlayer (aPlayerName);
		
		if (tPlayerIndex != NO_PLAYER_INDEX) {
			for (tIndex = tPlayerIndex; tIndex < (MAX_PLAYERS - 1); tIndex++) {
				tfPlayerNames [tIndex].setText (getPlayerName (tIndex + 1));
			}
			tfPlayerNames [MAX_PLAYERS - 1].setText (NO_NAME);
		}
		tPlayerCount = getTFPlayerCount ();
		setPlayerCount (tPlayerCount);
		gameSet.setGameRadioButtons (tPlayerCount);
	}
	
	public String [] getPlayers () {
		String [] tPlayers;
		int tIndex, tPlayersIndex;
		String tPlayerName;
		
		tPlayers = new String [playerCount];
		tPlayersIndex = 0;
		for (tIndex = 0; tIndex < MAX_PLAYERS; tIndex++) {
			tPlayerName = tfPlayerNames [tIndex].getText ();
			if (! tPlayerName.equals(NO_NAME) ) {
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
	
	public void setGameManager (GameManager aGameManager) {
		gameManager = aGameManager;
		gameManager.setPlayerInputFrame (this);
	}

	public void setParentFrame (Game_18XX a18xxFrame) {
		parentFrame = a18xxFrame;
	}

	public boolean isNetworkGame () {
		return gameManager.isNetworkGame ();
	}
	
	public JGameClient getNetworkJGameClient () {
		return gameManager.getNetworkJGameClient ();
	}
	
	public void addGameInfoPanel (JPanel gameInfoPanel) {
		if (isNetworkGame ()) {
			JGameClient tJGameClient = getNetworkJGameClient ();
			tJGameClient.addGameInfoPanel (gameInfoPanel);
		} else {
			add (gameInfoPanel, BorderLayout.EAST);
			pack ();
		}
	}

	public void handleGameSelection (int aGameIndex, String aOptions, String aBroadcast) {
		JGameClient tJGameClient;
		String tName;
		
		gameSet.handleGameSelection (aGameIndex, false);
		gameSet.handleGameOptions (aOptions);
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