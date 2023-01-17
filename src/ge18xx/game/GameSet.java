package ge18xx.game;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.w3c.dom.NodeList;

import ge18xx.game.variants.Variant;
import ge18xx.game.variants.VariantEffect;

//
//  GameSet.java
//  Game_18XX
//
//  Created by Mark Smith on 12/19/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

import ge18xx.network.JGameClient;
import ge18xx.toplevel.LoadableXMLI;
import ge18xx.toplevel.PlayerInputFrame;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

public class GameSet implements LoadableXMLI, ActionListener {
	final ElementName EN_GAMES = new ElementName ("Games");
	final ElementName EN_NETWORK = new ElementName ("Network");
	public static final int NO_GAME_SELECTED = -1;
	public static final GameSet NO_GAME_SET = null;
	private static final String NO_DESCRIPTION = "<html><body><h3>Game Description</h3><p>NO GAME SELECTED</p></body></html>";
	private static final String NEW_GAME = "New Local Game";
	private static final String NETWORK_GAME = "Online Game";
	private static final String LOAD_GAME = "Load Local Game";
	private static final String REASON_WRONG_PLAYER_COUNT = "Either too many, or too few Players for this game";
	private static final String REASON_NO_NEW_GAME = "Must select Game before you can start";
	private static final String REASON_BAD_PLAYER_LIST = "Bad Player List entered";
	GameInfo gameInfo [];
	PlayerInputFrame playerInputFrame;
	JPanel gameJPanel;
	JPanel gameInfoJPanel;
	JPanel descAndVariantsJPanel;
	JPanel listAndButtonJPanel;
	JPanel gameVariants [];
	ButtonGroup gameButtons;
	JRadioButton gameRadioButtons [];
	JScrollPane variantsScrollPane;
	JButton newGameButton;
	JButton networkGameButton;
	JButton loadGameButton;
	JLabel gameInfoLabel;
	JLabel gameDescriptionLabel;
	int selectedGameIndex;
	int gameIndex;

	public GameSet (PlayerInputFrame aPlayerInputFrame) {
		gameInfo = GameInfo.NO_GAMES;
		setSelectedGame (NO_GAME_SELECTED);
		gameInfoJPanel = new JPanel ();
		setPlayerInputFrame (aPlayerInputFrame);
	}

	public void setSelectedGameIndex (int aSelectedGameIndex) {
		gameRadioButtons [aSelectedGameIndex].setSelected (true);
		setSelectedGame (aSelectedGameIndex);
	}

	public void setSelectedGame (int aSelectedGameIndex) {
		selectedGameIndex = aSelectedGameIndex;
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		String tActionName;
		int tGameIndex;

		if (gameInfo != GameInfo.NO_GAMES) {
			tActionName = e.getActionCommand ();
			tGameIndex = getSelectedGameIndex ();
			setSelectedGame (tGameIndex);
			if (LOAD_GAME.equals (tActionName)) {
				playerInputFrame.loadGame ();
			} else if (NETWORK_GAME.equals (tActionName)) {
				handleNetworkGameConnect ();
			} else if (NEW_GAME.equals (tActionName)) {
				playerInputFrame.handleHotseatGameStart (gameInfo [selectedGameIndex]);
			} else {
				handleGameSelection (tGameIndex, true);
			}
		}
	}

	public void handleGameSelection (int aGameIndex, boolean aNotify) {
		JGameClient tJGameClient;

		setSelectedGame (aGameIndex);
		gameRadioButtons [aGameIndex].setSelected (true);
		showDescriptionAndVariants (aGameIndex);
		setGameRadioButtons (playerInputFrame.getPlayerCount (), playerInputFrame.getBadPlayerList ());
		if ((playerInputFrame.isNetworkGame () && aNotify)) {
			tJGameClient = playerInputFrame.getNetworkJGameClient ();
			tJGameClient.handleGameSelection (aGameIndex, gameInfo [aGameIndex].getName ());
		}
		playerInputFrame.pack ();
	}

	private void handleNetworkGameConnect () {
		GameManager tGameManager;
		JGameClient tNetworkGameJClient;
		String tPlayerName;
		String tChatTitle;

		tGameManager = playerInputFrame.getGameManager ();
		tGameManager.createUserPreferencesFrame ();
		tPlayerName = tGameManager.getClientUserName ();
		playerInputFrame.clearOtherPlayers (tPlayerName);
		tChatTitle = tGameManager.createFrameTitle (JGameClient.BASE_TITLE);
		tNetworkGameJClient = new JGameClient (tChatTitle, tGameManager);
		tGameManager.setNetworkJGameClient (tNetworkGameJClient);
		tGameManager.setNotifyNetwork (true);
		tNetworkGameJClient.addLocalPlayer (tPlayerName, false);
		removeGamePanelButtons ();
		tNetworkGameJClient.addGamePanel (gameJPanel);
		tNetworkGameJClient.clearGameSelection ();
		tNetworkGameJClient.swapToGamePanel ();
		playerInputFrame.setVisible (false);
	}

	public void addGameInfo (JPanel aGamePanel) {
		JPanel tGamesJPanel;
		int tGameCount;

		if (gameInfo != GameInfo.NO_GAMES) {
			gameInfoJPanel = new JPanel ();
			gameJPanel = aGamePanel;
			tGameCount = gameInfo.length;
			gameRadioButtons = new JRadioButton [tGameCount];

			tGamesJPanel = new JPanel ();
			tGamesJPanel.setLayout (new BoxLayout (tGamesJPanel, BoxLayout.Y_AXIS));
			buildGameButtons (tGamesJPanel, tGameCount);

			listAndButtonJPanel = new JPanel ();
			listAndButtonJPanel.setLayout (new BoxLayout (listAndButtonJPanel, BoxLayout.Y_AXIS));
			listAndButtonJPanel.add (tGamesJPanel);

			networkGameButton = new JButton ();
			setupButton (networkGameButton, NETWORK_GAME);

			newGameButton = new JButton ();
			setupButton (newGameButton, NEW_GAME);
			setEnabledGameButtons (false, REASON_NO_NEW_GAME);

			loadGameButton = new JButton ();
			setupButton (loadGameButton, LOAD_GAME);

			gameJPanel.add (listAndButtonJPanel);
			gameJPanel.add (Box.createVerticalStrut (10));

			showDescriptionAndVariants (NO_GAME_SELECTED);
		}
	}

	private void buildGameButtons (JPanel aBoxOfGamesJPanel, int aGameCount) {
		int tIndex;
		String tGameName;

		gameButtons = new ButtonGroup ();
		for (tIndex = 0; tIndex < aGameCount; tIndex++) {
			tGameName = "<html><body>" + gameInfo [tIndex].getName () + " <i>(" + gameInfo [tIndex].getMinPlayers ()
					+ "-" + gameInfo [tIndex].getMaxPlayers () + " Players)</i></body></html>";
			gameRadioButtons [tIndex] = new JRadioButton (tGameName);
			gameRadioButtons [tIndex].setEnabled (false);
			gameRadioButtons [tIndex].setToolTipText (REASON_WRONG_PLAYER_COUNT);
			gameRadioButtons [tIndex].setActionCommand (gameInfo [tIndex].getName ());
			gameRadioButtons [tIndex].addActionListener (this);
			gameButtons.add (gameRadioButtons [tIndex]);
			aBoxOfGamesJPanel.add (gameRadioButtons [tIndex]);
			aBoxOfGamesJPanel.add (Box.createVerticalStrut (10));
		}
	}

	private void setupButton (JButton aButton, String aName) {
		aButton.setText (aName);
		aButton.setActionCommand (aName);
		aButton.addActionListener (this);
		listAndButtonJPanel.add (aButton);
	}

	public void removeGamePanelButtons () {
		listAndButtonJPanel.remove (networkGameButton);
		listAndButtonJPanel.remove (newGameButton);
		listAndButtonJPanel.remove (loadGameButton);
	}

	public boolean gameIsSelected () {
		boolean tGameIsSelected;

		tGameIsSelected = false;
		if (selectedGameIndex != NO_GAME_SELECTED) {
			tGameIsSelected = true;
		}

		return tGameIsSelected;
	}

	public GameInfo getGameByName (String aName) {
		int tIndex, tGameCount;
		GameInfo tFoundGame;

		tGameCount = gameInfo.length;
		tFoundGame = GameInfo.NO_GAME_INFO;
		if ((tGameCount > 0) && (aName != null)) {
			for (tIndex = 0; tIndex < tGameCount; tIndex++) {
				if (aName.equals (gameInfo [tIndex].getName ())) {
					tFoundGame = gameInfo [tIndex];
				}
			}
		}

		return tFoundGame;
	}

	public int getSelectedGameIndex () {
		int tIndex, tGameCount, tFoundGame;

		tGameCount = gameInfo.length;
		tFoundGame = NO_GAME_SELECTED;
		for (tIndex = 0; tIndex < tGameCount; tIndex++) {
			if (gameRadioButtons [tIndex].isSelected ()) {
				tFoundGame = tIndex;
			}
		}

		return tFoundGame;
	}

	public GameInfo getSelectedGame () {
		GameInfo tGameInfo;
		int tIndex;

		if (gameIsSelected ()) {
			tIndex = getSelectedGameIndex ();
			tGameInfo = gameInfo [tIndex];
		} else {
			tGameInfo = GameInfo.NO_GAME_INFO;
		}

		return tGameInfo;
	}

	public String getSelectedGameName () {
		int tIndex;
		String tGameName = "";

		if (gameIsSelected ()) {
			tIndex = getSelectedGameIndex ();
			tGameName = gameInfo [tIndex].getName ();
		}

		return tGameName;
	}

	@Override
	public String getTypeName () {
		return "Game Set";
	}

	public void clearAllSelectedGames () {
		gameButtons.clearSelection ();
	}

	@Override
	public void loadXML (XMLDocument aXMLDocument) throws IOException {
		XMLNode tXMLGameSetRoot;

		tXMLGameSetRoot = aXMLDocument.getDocumentNode ();
		ParseGameConfig (tXMLGameSetRoot);
	}

	public void ParseGameConfig (XMLNode aCellNode) {
		XMLNodeList tXMLNodeList;
		NodeList tChildren;
		XMLNode tChildNode;
		String tChildName;
		int tChildrenCount, tIndex;

		tChildren = aCellNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (EN_GAMES.equals (tChildName)) {
				int tGameCount;
				tXMLNodeList = new XMLNodeList (gameInfoParsingRoutine);

				tGameCount = tXMLNodeList.getChildCount (tChildNode, GameInfo.EN_GAME_INFO);
				gameInfo = new GameInfo [tGameCount];
				gameIndex = 0;
				tXMLNodeList.parseXMLNodeList (tChildNode, GameInfo.EN_GAME_INFO);
			}
		}
	}

	ParsingRoutineI gameInfoParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			gameInfo [gameIndex] = new GameInfo (aChildNode);
			gameIndex++;
		}
	};

	@Override
	public void foundItemMatchKey1 (XMLNode aChildNode) {
	}

	public void handleGameVariants (XMLNode aVariantEffectsNode) {
		int tSelectedGameIndex;

		tSelectedGameIndex = getSelectedGameIndex ();
		if (aVariantEffectsNode == VariantEffect.NO_VARIANT_EFFECTS_NODE) {
			System.err.println ("No VariantEffects Node provided");
		} else {
			gameInfo [tSelectedGameIndex].loadAllVariantEffects (aVariantEffectsNode);
			gameInfo [tSelectedGameIndex].selectActiveVariantEffects ();
		}
	}

	private void setEnabledGameButtons (boolean aState, String aToolTipText) {
		newGameButton.setEnabled (aState);
		newGameButton.setToolTipText (aToolTipText);
	}

	public void setPlayerInputFrame (PlayerInputFrame aPlayerInputFrame) {
		playerInputFrame = aPlayerInputFrame;
	}

	public void setGameRadioButtons (int aCurrentPlayerCount, boolean aBadPlayerList) {
		int tIndex;
		int tGameCount;

		if (gameInfo != GameInfo.NO_GAMES) {
			tGameCount = gameInfo.length;
			for (tIndex = 0; tIndex < tGameCount; tIndex++) {
				if (aBadPlayerList) {
					gameRadioButtons [tIndex].setEnabled (false);
					gameRadioButtons [tIndex].setToolTipText (REASON_BAD_PLAYER_LIST);
					setEnabledGameButtons (false, REASON_BAD_PLAYER_LIST);
				} else if (gameInfo [tIndex].canPlayWithXPlayers (aCurrentPlayerCount)) {
					gameRadioButtons [tIndex].setEnabled (true);
					gameRadioButtons [tIndex].setToolTipText (GUI.NO_TOOL_TIP);
					if (gameRadioButtons [tIndex].isSelected ()) {
						selectedGameIndex = tIndex;
						setEnabledGameButtons (true, GUI.NO_TOOL_TIP);
					}
				} else {
					if (selectedGameIndex == tIndex) {
						selectedGameIndex = NO_GAME_SELECTED;
						setEnabledGameButtons (false, REASON_NO_NEW_GAME);
						gameButtons.clearSelection ();
					}
					gameRadioButtons [tIndex].setEnabled (false);
					gameRadioButtons [tIndex].setToolTipText (REASON_WRONG_PLAYER_COUNT);
				}
			}
		}
	}

	private void showDescriptionAndVariants (int aIndex) {
		if (descAndVariantsJPanel == GUI.NO_PANEL) {
			descAndVariantsJPanel = new JPanel ();
			descAndVariantsJPanel.setLayout (new BoxLayout (descAndVariantsJPanel, BoxLayout.Y_AXIS));
		}
		descAndVariantsJPanel.removeAll ();
		variantsScrollPane = new JScrollPane (descAndVariantsJPanel);
		variantsScrollPane.setPreferredSize (new Dimension (500, 350));
		if (aIndex == NO_GAME_SELECTED) {
			if (gameDescriptionLabel == GUI.NO_LABEL) {
				gameDescriptionLabel = new JLabel (NO_DESCRIPTION);
			} else {
				gameDescriptionLabel.setText (NO_DESCRIPTION);
			}
			descAndVariantsJPanel.add (gameDescriptionLabel);
		} else {
			buildGameDescription (aIndex);
		}

		gameInfoJPanel.removeAll ();
		gameInfoJPanel.add (variantsScrollPane);
		gameInfoJPanel.setPreferredSize (gameInfoJPanel.getPreferredSize ());
		playerInputFrame.addGameInfoPanel (gameInfoJPanel);
	}

	public void buildGameDescription (int aIndex) {
		String tDescription;
		int tVariantCount;
		int tVariantIndex;
		int tAddedCount;
		Variant tVariant;
		GameManager tGameManager;

		tDescription = gameInfo [aIndex].getHTMLDescription ();
		gameDescriptionLabel.setText (tDescription);
		gameDescriptionLabel.setBorder (BorderFactory.createEmptyBorder (0, 10, 0, 10));
		descAndVariantsJPanel.add (gameDescriptionLabel);
		tVariantCount = gameInfo [aIndex].getVariantCount ();

		gameVariants = new JPanel [tVariantCount];
		tGameManager = playerInputFrame.getGameManager ();
		tAddedCount = 0;
		for (tVariantIndex = 0; tVariantIndex < tVariantCount; tVariantIndex++) {
			tVariant = gameInfo [aIndex].getVariantIndex (tVariantIndex);
			if (tGameManager.isNetworkGame ()) {
				if (! tVariant.hotSeatOnly ()) {
					tAddedCount = addVariantToList (tAddedCount, tVariant);
				}
			} else {
				tAddedCount = addVariantToList (tAddedCount, tVariant);
			}
		}
	}

	private int addVariantToList (int aIndex, Variant aVariant) {
		gameVariants [aIndex] = aVariant.buildVariantDescription ();
		descAndVariantsJPanel.add (gameVariants [aIndex]);

		return (aIndex + 1);
	}
}
