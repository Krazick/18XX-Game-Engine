package ge18xx.network;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.ConnectException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.logging.log4j.Logger;

import ge18xx.game.GameInfo;
// TODO Work out ways to remove imports that refer to ge18xx Packages
// Intention here is to break out ge18xx.utilities to it's own JAR File first
// And then have the ge18xx.network to it's own JAR File that requires the utilities JAR.
import ge18xx.game.GameManager;
import ge18xx.game.GameSet;
import ge18xx.game.SavedGame;
import ge18xx.game.SavedGames;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.FileUtils;
import ge18xx.utilities.GUI;
import ge18xx.utilities.Validators;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;

public class JGameClient extends XMLFrame {
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_SERVER_PORT = 18300;
	private static final String DEFAULT_REMOTE_SERVER_IP = "108.31.172.56";
	private static final String CONNECT_ACTION = "CONNECT";
	private final String ALREADY_CONNECTED = "You are already connected";
	private final String NOT_CONNECTED = "You are not connected yet";
	private final String WAITING_FOR_GAME = "Waiting for Game Selection";
	private final String GAME_SELECTED = "Game has been Selected, hit the button when ready to play";
	private final String WAITING_FOR_ALL = "Waiting for ALL players to be Ready";
	private final String GAME_ALREADY_STARTED = "Game has already started, don't need to Start again";

	private ChatServerHandler serverHandler;
	private GameSupportHandler gameSupportHandler;

	// Static Strings used by Client Handler - Should replace with XML Utilities
	// handling
	public static final String NO_GAME_ID = "NOID";
	public static final String EMPTY_GAME_ID = "";
	public static final String GAME_ACTIVITY_TAG = "GA";
	public static final String GAME_ACTIVITY_PREFIX = "Game Activity";
	public static final String GAME_SUPPORT_TAG = "GS";
	public static final String GAME_SUPPORT_PREFIX = "Game Support";
	public static final String GA_XML_START = "<" + GAME_ACTIVITY_TAG + ">";
	public static final String GA_XML_END = "</" + GAME_ACTIVITY_TAG + ">";
	public static final String GS_XML_START = "<" + GAME_SUPPORT_TAG + ">";
	public static final String GS_XML_END = "</" + GAME_SUPPORT_TAG + ">";
	public static final String REQUEST_ACTION_NUMBER = "<ActionNumber requestNew=\"TRUE\">";

	// XML Utilities Element Names and Attribute Names
	public static final ElementName EN_NETWORK_GAME = new ElementName ("NetworkGame");
	public static final ElementName EN_GAME_ACTIVITY = new ElementName (GAME_ACTIVITY_TAG);
	public static final ElementName EN_GAME_SUPPORT = new ElementName (GAME_SUPPORT_TAG);
	public static final ElementName EN_GAME_SELECTION = new ElementName ("GameSelection");
	public static final ElementName EN_LOAD_GAME_SETUP = new ElementName ("LoadGameSetup");
	public static final ElementName EN_PLAYER_ORDER = new ElementName ("PlayerOrder");
	public static final ElementName EN_REQUEST_SAVED_GAMES = new ElementName ("RequestSavedGames");
	public static final AttributeName AN_SERVER_IP = new AttributeName ("serverIP");
	public static final AttributeName AN_SERVER_PORT = new AttributeName ("serverPort");
	public static final AttributeName AN_GAME_INDEX = new AttributeName ("gameIndex");
	public static final AttributeName AN_GAME_VARIANTS = new AttributeName ("gameOptions");
	public static final AttributeName AN_BROADCAST_MESSAGE = new AttributeName ("z_broadcast");
	public static final AttributeName AN_GAME_ID = new AttributeName ("gameID");
	public static final AttributeName AN_ACTION_NUMBER = new AttributeName ("actionNumber");
	public static final AttributeName AN_GAME_NAME = new AttributeName ("gameName");

	public static final AttributeName AN_PLAYER_ORDER = new AttributeName ("players");
	public static final AttributeName AN_PLAYER = new AttributeName ("player");
	public static final AttributeName AN_REQUEST_ACTION_NUMBER = new AttributeName ("requestActionNumber");
	public static JGameClient NO_JGAME_CLIENT = null;
	public static final String REQUEST_LAST_ACTION_COMPLETE = "<LastAction isComplete=\"TRUE\">";
	public static final String REQUEST_LAST_ACTION = "<ActionNumber requestLast=\"TRUE\">";
	public static final String DISCONNECT = "DISCONNECT";
	private final String SHOW_SAVED_GAMES = "SHOW SAVED GAMES";
	private final String SHOW_ALL_GAMES = "SHOW ALL GAMES";
	private final String SELECT_GAME = "SELECT GAME";
	private final String READY_TO_PLAY = "READY";
//	private final String ACTIVE = "ACTIVE";
	private final String REFRESH = "REFRESH";
	private final String AFK = "AFK";
	private final String SEND = "SEND";
	private final String PLAY_GAME = "PLAY GAME";
	private final String START_GAME = "START";
	private final String NO_SELECTED_GAME = null;
	// Static Labels
	JLabel nameLabel = new JLabel ("Name:");
	JLabel serverIPLabel = new JLabel ("Server IP:");
	JLabel playersLabel = new JLabel ("Players");
	JLabel messageLabel = new JLabel ("Message:");

	// Java Swing Objects
	private JTextPane chatText;
	private JTextPane gameActivity;
	private JTextField playerName;
	private JTextField messageField;
	private JButton connectButton;
	private JButton sendMessageButton;
	private JButton awayFromKeyboardAFKButton;
	private JButton disconnectButton;
	private JButton refreshPlayersButton;
	private JButton startReadyButton;
	private JButton showSavedGames;
	private JTextField serverIPField;
	private JScrollPane spChatText;
	private JScrollPane spGameActivity;
	private JSplitPane splitPane;
	private JPanel gameActivityPanel;
	private JPanel gamePanel;
	private JPanel gameInfoPanel;
	private JPanel networkSavedGamesPanel;
	private JPanel primaryPanel;
	private JPanel headerPanel;
	private JPanel activityPanel;
	private JPanel playersPanel;
	private JPanel messagePanel;
	private JPanel bottomPanel;
	private JList<NetworkPlayer> playerList;
	private JList<String> savedGamesList;
	private DefaultListModel<String> savedGamesListModel;

	private HeartbeatThread heartbeatThread;
	private Thread hbeatThread;
	private Thread serverThread;
	private SimpleAttributeSet normal = new SimpleAttributeSet ();
	private SimpleAttributeSet iSaid = new SimpleAttributeSet ();

	// GE18XX Specific Objects
	private NetworkGameSupport gameManager;
	private NetworkPlayers networkPlayers;
	private GameSet gameSet;
	private NetworkMessages networkMessage;

	// Standard Java Objects
	private String serverIP;
	private int serverPort;
	private int selectedGameIndex;
	private String selectedGameName;
	private String autoSaveFileName;
	private Logger logger;
	private boolean gameStarted = false;

	public JGameClient (String aTitle, NetworkGameSupport aGameManager) {
		this (aTitle, aGameManager, DEFAULT_REMOTE_SERVER_IP, DEFAULT_SERVER_PORT);
	}

	public JGameClient (String aTitle, NetworkGameSupport aGameManager, String aServerIP, int aServerPort) {
		super (aTitle);
		Point tNewPoint;

		gameManager = aGameManager;
		networkPlayers = new NetworkPlayers (gameManager);
		gameSupportHandler = new GameSupportHandler (this);
		networkMessage = new NetworkMessages ();
		setupJFrame ();
		setupActions ();
		setServerIP (aServerIP);
		setServerPort (aServerPort);
		if (gameManager != GameManager.NO_GAME_MANAGER) {
			gameManager.addNewFrame (this);
			tNewPoint = gameManager.getOffsetGEFrame ();
			setLocation (tNewPoint);
			setVisible (true);
			logger = gameManager.getLogger ();
		}
	}

	private boolean setupServerInfo () {
		String tServerIPEntered;
		boolean tGoodServer;

		tServerIPEntered = serverIPField.getText ();
		if (Validators.isValidIP (tServerIPEntered)) {
			setServerIP (tServerIPEntered);
			tGoodServer = true;
		} else {
			tServerIPEntered = "BAD " + tServerIPEntered;
			serverIPField.setText (tServerIPEntered);
			tGoodServer = false;
		}
		setServerPort (DEFAULT_SERVER_PORT);

		return tGoodServer;
	}

	private boolean setupNewPlayer (String aAction) {
		boolean tValidNewPlayer = false;
		String tPlayerName = playerName.getText ();

		tValidNewPlayer = NetworkPlayer.validPlayerName (tPlayerName);
		if (tValidNewPlayer) {
			try {
				connect (aAction);
				serverIPField.setEnabled (false);
				serverIPField.setToolTipText ("Cannot change Server while Connected");
			} catch (Exception eSocket) {
				setForUnconnected ();
			}
		} else {
			appendToChat ("Player Name [" + tPlayerName + "] is not valid");
		}

		return tValidNewPlayer;
	}

	private void setupServerAndPlayer (String aAction) {
		if (setupServerInfo ()) {
			setupNewPlayer (aAction);
		}
	}

	private void setupActions () {
		connectButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent aActionEvent) {
				String tAction = aActionEvent.getActionCommand ();
				setupServerAndPlayer (tAction);
			}
		});

		connectButton.addKeyListener (new KeyAdapter () {
			@Override
			public void keyReleased (KeyEvent aActionEvent) {
				if (aActionEvent.getKeyCode () == KeyEvent.VK_ENTER) {
					String tAction = connectButton.getActionCommand ();
					setupServerAndPlayer (tAction);
				}
			}
		});

		sendMessageButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent aActionEvent) {
				sendMessage (aActionEvent);
			}
		});

		sendMessageButton.addKeyListener (new KeyAdapter () {
			@Override
			public void keyReleased (KeyEvent aActionEvent) {
				if (aActionEvent.getKeyCode () == KeyEvent.VK_ENTER) {
					String tAction = sendMessageButton.getActionCommand ();
					sendMessage (tAction);
				}
			}
		});

		refreshPlayersButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent aActionEvent) {
				String tAction = aActionEvent.getActionCommand ();

				if (REFRESH.equals (tAction)) {
					refreshPlayers ();
				}
			}
		});

		messageField.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent aActionEvent) {
				sendMessage (aActionEvent);
			}
		});

		awayFromKeyboardAFKButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent aActionEvent) {
				String tAction = aActionEvent.getActionCommand ();

				if (AFK.equals (tAction)) {
					serverHandler.sendUserIsAFK ();
					networkPlayers.setPlayerAFK (playerName.getText (), true);
					awayFromKeyboardAFKButton.setEnabled (false);
				}
			}
		});

		showSavedGames.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent aActionEvent) {

				if (showSavedGames.getText ().equals (SHOW_SAVED_GAMES)) {
					showSavedGames.setText (SHOW_ALL_GAMES);
					swapToNSGPanel ();
					updateReadyButton (PLAY_GAME, false, WAITING_FOR_GAME);

				} else if (showSavedGames.getText ().equals (SHOW_ALL_GAMES)) {
					showSavedGames.setText (SHOW_SAVED_GAMES);
					clearGameSelection ();
					swapToGIPanel ();
					updateReadyButton (SELECT_GAME, false, WAITING_FOR_GAME);
				}
			}
		});

		startReadyButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent aActionEvent) {
				String tAction = aActionEvent.getActionCommand ();

				if (PLAY_GAME.equals (tAction)) {
					updateReadyButton (PLAY_GAME, false, GAME_ALREADY_STARTED);
					updateButtonGameStarted (showSavedGames);
					loadAndStartGame ();
				} else {
					if (getGameID ().equals ("")) {
						setGameIDFromNetwork ();
					}
					if (SELECT_GAME.equals (tAction)) {
						sendGameSelection ();
					} else if (READY_TO_PLAY.equals (tAction)) {
						sendPlayerReady ();
					}
					if (START_GAME.equals (tAction)) {
						handleStartGame ();
					}
				}
			}

		});

		disconnectButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent aActionEvent) {
				String tAction = aActionEvent.getActionCommand ();

				if (DISCONNECT.equals (tAction)) {
					disconnect ();
				}
			}
		});
	}

	public void disconnect () {
		if (!gameStarted) {
			// Send to all Players command to unselect game, clear all Ready Flags
		}
		if (serverHandler != null) {
			serverHandler.shutdown ();
			networkPlayers.removeAllPlayers ();
			setForUnconnected ();
		}
	}

	public void printButtonStatus (JButton aButton) {
		System.out.println (aButton.getActionCommand () + " Button is Enabled [" + aButton.isEnabled () + "]");
	}

	public void setForUnconnected () {
		stopHeartbeatDelivery ();

		sendMessageButton.setEnabled (false);
		sendMessageButton.setToolTipText (NOT_CONNECTED);
		showSavedGames.setEnabled (false);
		showSavedGames.setToolTipText (NOT_CONNECTED);
		disconnectButton.setEnabled (false);
		disconnectButton.setToolTipText (NOT_CONNECTED);
		refreshPlayersButton.setEnabled (false);
		refreshPlayersButton.setToolTipText (NOT_CONNECTED);

		awayFromKeyboardAFKButton.setEnabled (false);
		awayFromKeyboardAFKButton.setToolTipText (NOT_CONNECTED);
		connectButton.setEnabled (true);
		connectButton.setToolTipText (GUI.NO_TOOL_TIP);
		connectButton.requestFocusInWindow ();
		updateReadyButton (SELECT_GAME, false, NOT_CONNECTED);

		messageField.setEnabled (false);
		messageField.setFocusable (false);
		serverIPField.setEnabled (true);
		serverIPField.setToolTipText (GUI.NO_TOOL_TIP);

		if (!gameManager.gameStarted ()) {
			showSavedGames.setText (SHOW_SAVED_GAMES);
			removeNSGPanel ();
			removeGamePanel ();
			clearGameSelection ();
			addGamePanel ();
		}
		gameManager.updateDisconnectButton ();
	}

	public void setForConnected () {
		startHeartbeat ();

//		Debugging for Saved Game setup in Network
//		stopHeartbeatDelivery ();

		connectButton.setEnabled (false);
		connectButton.setToolTipText (ALREADY_CONNECTED);
		sendMessageButton.setEnabled (true);
		sendMessageButton.setToolTipText (GUI.NO_TOOL_TIP);
		disconnectButton.setEnabled (true);
		disconnectButton.setToolTipText ("For Debugging Purposes ONLY");
		awayFromKeyboardAFKButton.setEnabled (true);
		awayFromKeyboardAFKButton.setToolTipText (GUI.NO_TOOL_TIP);
		refreshPlayersButton.setEnabled (true);
		refreshPlayersButton.setToolTipText (GUI.NO_TOOL_TIP);
		updateReadyButton (SELECT_GAME, false, WAITING_FOR_GAME);

		playerName.setFocusable (false);
		playerName.setEnabled (false);
		playerName.setEditable (false);
		requestSavedGames ();
		messageField.setEnabled (true);
		messageField.setFocusable (true);
		messageField.requestFocusInWindow ();
		gameManager.updateDisconnectButton ();
	}

	private void updateShowSavedGamesButton () {
		showSavedGames.setEnabled (true);
		showSavedGames.setToolTipText (GUI.NO_TOOL_TIP);
	}

	public void requestSavedGames () {
		String tRequestSavedGames;
		String tFullRequest;
		String tResponse;
		String tGSResponseRegEx = "<GSResponse>(.*)</GSResponse>";
		Pattern tGSResponsePattern = Pattern.compile (tGSResponseRegEx);
		Matcher tMatcher;
		String tSavedGamesXML;

		tRequestSavedGames = buildGameSupportXML (EN_REQUEST_SAVED_GAMES, AN_PLAYER, playerName.getText ());
		tFullRequest = GAME_SUPPORT_PREFIX + " " + tRequestSavedGames;
		tResponse = gameSupportHandler.requestGameSupport (tFullRequest);

		tMatcher = tGSResponsePattern.matcher (tResponse);
		if (tMatcher.find ()) {
			tSavedGamesXML = tMatcher.group (1);

			gameManager.parseNetworkSavedGames (tSavedGamesXML);
		}
	}

	public void startHeartbeat () {
		heartbeatThread = new HeartbeatThread (this);
		hbeatThread = new Thread (heartbeatThread);
		heartbeatThread.setContinueRunning (true);
		hbeatThread.start ();
	}
	
	private void buildPanels () {
		primaryPanel = new JPanel ();
		primaryPanel.setLayout (new BoxLayout (primaryPanel, BoxLayout.Y_AXIS));
		buildHeaderPanel ();
		buildActityAndPlayersPanel ();
		buildMessagePanel ();
		buildBottomPanel ();
		
		primaryPanel.add (Box.createVerticalStrut (10));
		primaryPanel.add (headerPanel);
		primaryPanel.add (Box.createVerticalGlue ());
		primaryPanel.add (activityPanel);
		primaryPanel.add (Box.createVerticalStrut (10));
		primaryPanel.add (Box.createVerticalGlue ());
		primaryPanel.add (messagePanel);
		primaryPanel.add (Box.createVerticalStrut (10));
		primaryPanel.add (Box.createVerticalGlue ());
		primaryPanel.add (bottomPanel);
		primaryPanel.add (Box.createVerticalStrut (10));
		add (primaryPanel);
	}

	private void buildBottomPanel () {
		bottomPanel = new JPanel ();
		bottomPanel.setLayout (new BoxLayout (bottomPanel, BoxLayout.X_AXIS));
		bottomPanel.add (Box.createHorizontalStrut (10));
		bottomPanel.add (sendMessageButton);
		bottomPanel.add (Box.createHorizontalGlue ());
		bottomPanel.add (awayFromKeyboardAFKButton);
		bottomPanel.add (Box.createHorizontalGlue ());
		bottomPanel.add (disconnectButton);
		bottomPanel.add (Box.createHorizontalStrut (10));
	}

	private void buildActityAndPlayersPanel () {
		playersPanel = new JPanel ();
		playersPanel.setLayout (new BoxLayout (playersPanel, BoxLayout.Y_AXIS));
		playersPanel.add (Box.createVerticalStrut (10));
		playersPanel.add (playersLabel);
		playersPanel.add (Box.createVerticalStrut (10));
		playerList.setFixedCellWidth (150);
		playersPanel.add (playerList);
		playersPanel.add (Box.createVerticalGlue ());
		playersPanel.add (refreshPlayersButton);
		playersPanel.add (Box.createVerticalStrut (10));
		
		activityPanel = new JPanel ();
		activityPanel.setLayout (new BoxLayout (activityPanel, BoxLayout.X_AXIS));
		activityPanel.add (Box.createHorizontalStrut (10));
		splitPane.setAlignmentY (Component.TOP_ALIGNMENT);
		activityPanel.add (splitPane);
		activityPanel.add (Box.createHorizontalStrut (10));
		playersPanel.setAlignmentY (Component.TOP_ALIGNMENT);
		activityPanel.add (playersPanel);
		activityPanel.add (Box.createHorizontalStrut (10));
	}

	private void buildMessagePanel () {
		Dimension tDimensionPref;
		
		messagePanel = new JPanel ();
		messagePanel.setLayout (new BoxLayout (messagePanel, BoxLayout.X_AXIS));
		messagePanel.add (Box.createHorizontalStrut (10));
		messagePanel.add (messageLabel);
		messagePanel.add (Box.createHorizontalStrut (5));
		messageField.setColumns (200);
		tDimensionPref = messageField.getPreferredSize ();
		messageField.setMaximumSize (tDimensionPref);
		messagePanel.add (messageField);
		messagePanel.add (Box.createHorizontalStrut (10));
	}

	private void buildHeaderPanel () {
		headerPanel = new JPanel ();
		headerPanel.setLayout (new BoxLayout (headerPanel, BoxLayout.X_AXIS));
		headerPanel.add (Box.createHorizontalStrut (10));
		headerPanel.add (nameLabel);
		playerName.setColumns (10);
		playerName.setMaximumSize (playerName.getMinimumSize ());
		headerPanel.add (playerName);
		headerPanel.add (Box.createHorizontalGlue ());
		headerPanel.add (connectButton);
		headerPanel.add (Box.createHorizontalGlue ());
		headerPanel.add (showSavedGames);
		headerPanel.add (Box.createHorizontalGlue ());
		headerPanel.add (startReadyButton);
		headerPanel.add (Box.createHorizontalGlue ());
		headerPanel.add (serverIPLabel);
		serverIPField.setColumns (10);
		serverIPField.setMaximumSize (serverIPField.getMinimumSize ());
		headerPanel.add (serverIPField);
		headerPanel.add (Box.createHorizontalStrut (10));
	}

	private void setupJFrame () {
		nameLabel.setLabelFor (playerName);
		messageLabel.setLabelFor (messageField);

		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		buildFrameComponents ();
		buildPanels ();
		
		setForUnconnected ();
		setSize (1000, 520);
	}

	private void buildFrameComponents () {
		// Text Fields
		playerName = new JTextField ();
		playerName.setColumns (10);
		messageField = new JTextField (200);
		messageField.setActionCommand (SEND);

		serverIPField = new JTextField (DEFAULT_REMOTE_SERVER_IP, 10);
		serverIPField.setHorizontalAlignment (SwingConstants.CENTER);

		// Action Buttons
		connectButton = new JButton (CONNECT_ACTION);
		sendMessageButton = new JButton (SEND);
		awayFromKeyboardAFKButton = new JButton (AFK);
		refreshPlayersButton = new JButton (REFRESH);
		disconnectButton = new JButton (DISCONNECT);
		startReadyButton = new JButton (SELECT_GAME);
		showSavedGames = new JButton (SHOW_SAVED_GAMES);

		// Text Panes and Scroll Panes
		chatText = new JTextPane ();
		chatText.setText ("Player Chat Area");
		chatText.setFocusable (true);
		chatText.setFocusTraversalKeysEnabled (false);
		chatText.setFocusCycleRoot (false);
		chatText.setEditable (false);

		spChatText = new JScrollPane ();
		spChatText.setAutoscrolls (true);
		spChatText.setViewportBorder (null);
		spChatText.setViewportView (chatText);

		gameActivityPanel = new JPanel ();
		gameActivityPanel.setBorder (new LineBorder (Color.GRAY, 1, true));
		gameActivityPanel.setPreferredSize (new Dimension (100, 100));

		gameActivity = new JTextPane ();
		gameActivity.setText ("Game Activity Area");
		gameActivity.setFocusable (true);
		gameActivity.setFocusTraversalKeysEnabled (false);
		gameActivity.setFocusCycleRoot (false);
		gameActivity.setEditable (false);

		spGameActivity = new JScrollPane ();
		spGameActivity.setAutoscrolls (true);
		spGameActivity.setViewportView (gameActivity);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				gameActivityPanel, spChatText);

		playerList = new JList<> (networkPlayers.getPlayerList ());
		playerList.setFocusable (false);
		playerList.setFocusTraversalKeysEnabled (false);
		playerList.setEnabled (false);
	}

	private void handleStartGame () {
		String tGameID;

		tGameID = getGameID ();
		if (serverHandler != ServerHandler.NO_SERVER_HANDLER) {
			serverHandler.sendUserStart (tGameID);
		}
		startsGame ();
		updatePlayersAndButtons ();
	}

	public void startsGame () {
		swapToGameActivity ();
		gameStarted = true;
		gameManager.initiateNetworkGame ();
		updatePlayersAndButtons ();
	}

	private void updatePlayersAndButtons () {
		refreshPlayers ();
		updateButtonGameStarted (startReadyButton);
		updateButtonGameStarted (showSavedGames);
	}

	private void updateButtonGameStarted (JButton aButton) {
		aButton.setEnabled (false);
		aButton.setToolTipText (GAME_ALREADY_STARTED);
	}

	private void swapToGameActivity () {
		removeGamePanel ();
		removeNSGPanel ();
		addSPGameActivity ();
	}

	private void swapToNSGPanel () {
		removeGamePanel ();
		addNSGPanel ();
	}

	private void swapToGIPanel () {
		removeNSGPanel ();
		addGamePanel ();
	}

	public void removeGamePanel () {
		if (gamePanel != GUI.NO_PANEL) {
			gameActivityPanel.remove (gamePanel);
		}
		if (gameInfoPanel != GUI.NO_PANEL) {
			gameActivityPanel.remove (gameInfoPanel);
		}
		revalidate ();
	}

	public void addGamePanel () {
		if (gamePanel != GUI.NO_PANEL) {
			gameActivityPanel.add (gamePanel, BorderLayout.WEST);
		}
	}

	public void addGamePanel (JPanel aGamePanel) {
		gamePanel = aGamePanel;
		addGamePanel ();
		revalidate ();
	}

	public void addGameInfoPanel (JPanel aGameInfoPanel, GameSet aGameSet) {
		gameSet = aGameSet;
		gameInfoPanel = aGameInfoPanel;
		gameActivityPanel.add (aGameInfoPanel, BorderLayout.EAST);
		revalidate ();
	}

	public void removeSPGameActivity () {
		gameActivityPanel.remove (spGameActivity);
		revalidate ();
	}

	public void addSPGameActivity () {
		gameActivityPanel.add (spGameActivity);
		revalidate ();
	}

	public void addNSGPanel () {
		gameActivityPanel.add (networkSavedGamesPanel);
		revalidate ();
	}

	public void removeNSGPanel () {
		if (networkSavedGamesPanel != GUI.NO_PANEL) {
			gameActivityPanel.remove (networkSavedGamesPanel);
		}
		revalidate ();
	}

	public void log (String aMessage) {
		log (aMessage, null);
	}

	private void log (String aMessage, Exception aException) {
		System.err.println (aMessage);
		if (aException != null) {
			aException.printStackTrace ();
		}
	}

	// Server Handler Connection Routines ---
	public void setServerHandler (ChatServerHandler aServerHandler) {
		serverHandler = aServerHandler;
	}

	private boolean connectToServer (String aPlayerName) throws Exception {
		boolean tSuccess = false;
		ChatServerHandler tChatServerHandler;

		try {
			tChatServerHandler = new ChatServerHandler (serverIP, serverPort, gameManager);
			if (tChatServerHandler != ServerHandler.NO_SERVER_HANDLER) {
				setServerHandler (tChatServerHandler);
				if (serverHandler.isConnected ()) {
					serverThread = new Thread (serverHandler);
					serverThread.start ();
					serverHandler.initializeChat (this);
					// If initializeChat method caused the Server Handler to abort
					// for example: same name used by another client, it will stop
					// the Handler, so do NOT add the player, and return Failure here
					if (serverHandler.isConnected ()) {
						tSuccess = true;
					}
				}
			}
		} catch (ConnectException tException) {
			appendToChat ("Server Connection Failed - confirm Server is up and operational");
			throw tException;
		} catch (Exception tException) {
			appendToChat ("Server Connection Timed Out - confirm Server is up and operational");
			throw tException;
		}

		return tSuccess;
	}

	private void connect (String aAction) throws Exception {

		String tPlayerName;
		boolean tSuccess;

		if (CONNECT_ACTION.equals (aAction)) {
			tPlayerName = playerName.getText ();
			tSuccess = connectToServer (tPlayerName);
			if (tSuccess) {
				if (serverHandler != ServerHandler.NO_SERVER_HANDLER) {
					if (serverHandler.isConnected ()) {
						serverHandler.sendGEVersion (gameManager.getGEVersion ());
						setForConnected ();
						serverHandler.requestUserNameList ();
					}
				}
			}
		}
	}

	public boolean isConnected () {
		boolean tIsConnected = false;

		if (serverHandler != ServerHandler.NO_SERVER_HANDLER) {
			tIsConnected = serverHandler.isConnected ();
		}

		return tIsConnected;
	}

	// --- End Server Handler Connection Routines

	// Message and Chat Management ---

	public void refreshPlayers () {
		backFromAFK ();
		networkPlayers.removeAllPlayers ();
		// Add myself to the list
		networkPlayers.addPlayer (playerName.getText ());
		// Request from the ServerHandler to add all of the other Players
		serverHandler.requestUserNameList ();
	}

	public void sendGameActivity (String aGameActivity) {
		serverHandler.sendGameActivity (GAME_ACTIVITY_PREFIX + " " + aGameActivity);
	}

	public void clearGameSelection () {
		if (gameSet != GameSet.NO_GAME_SET) {
			gameSet.clearAllSelectedGames ();
		}
		selectedGameIndex = GameSet.NO_GAME_SELECTED;
		selectedGameName = NO_SELECTED_GAME;
		updateReadyButton (SELECT_GAME, false, WAITING_FOR_GAME);
	}

	public void handleGameSelection (int aGameIndex, String aGameName) {
		selectedGameIndex = aGameIndex;
		selectedGameName = aGameName;
		updateReadyButton (SELECT_GAME, true, GAME_SELECTED);
	}

	public void sendPlayerReady () {
		String tGameID;

		tGameID = getGameID ();
		showSavedGames.setEnabled (false);
		showSavedGames.setToolTipText ("Ready to play New Game");
		serverHandler.sendUserReady (tGameID);
		sendPlayerOrder ();
	}

	public NetworkGameSupport getGameManager () {
		return gameManager;
	}

	public void sendPlayerOrder () {
		String tBroadcastMessage, tGameActivity;
		String tPlayerOrder;

		gameManager.randomizePlayerOrder ();
		tPlayerOrder = gameManager.getPlayersInOrder ();
		tBroadcastMessage = getName () + " has sent a new Player Order [" + tPlayerOrder + "]";
		tGameActivity = buildGameActivityXML (EN_PLAYER_ORDER, AN_PLAYER_ORDER, tPlayerOrder, AN_BROADCAST_MESSAGE,
				tBroadcastMessage);
		sendGameActivity (tGameActivity);

		updateReadyButton ("WAITING", false, WAITING_FOR_ALL);
		playerReady ();
		appendToChat ("You have sent a new Player Order [" + tPlayerOrder + "]");
	}

	public void sendGameSelection () {
		String tBroadcastMessage, tGameActivity;
		String tGameID;
		XMLElement tVariantEffects;
		GameInfo tGameInfo;
		XMLDocument tXMLDocument;

		tGameInfo = gameManager.getSelectedGame ();
		tGameInfo.setupVariants ();
		tXMLDocument = networkMessage.getXMLDocument ();
		tVariantEffects = tGameInfo.getGameVariantsXMLElement (tXMLDocument);
		tBroadcastMessage = getName () + " has Selected [" + selectedGameName + "] Are you ready to Play?";
		tGameID = getGameID ();
		tGameActivity = buildGameActivityXML (EN_GAME_SELECTION, tVariantEffects,
					AN_GAME_INDEX, selectedGameIndex + "",
					AN_BROADCAST_MESSAGE, tBroadcastMessage,
					AN_GAME_ID, tGameID);
		showSavedGames.setEnabled (false);
		showSavedGames.setToolTipText ("Ready to play New Game");
		sendGameActivity (tGameActivity);
		sendPlayerOrder ();
	}

	public void updateReadyButton (String aAction, boolean aEnabled, String aToolTip) {
		startReadyButton.setActionCommand (aAction);
		startReadyButton.setText (aAction);
		startReadyButton.setToolTipText (aToolTip);
		if (aEnabled) {
			if (gameManager.gameStarted ()) {
				updateButtonGameStarted (startReadyButton);
				updateButtonGameStarted (showSavedGames);
			} else {
				startReadyButton.setEnabled (true);
			}
		} else {
			startReadyButton.setEnabled (false);
		}
	}

	public void setGameIDFromNetwork () {
		String tGameID;

		tGameID = gameSupportHandler.retrieveGameID ();

		gameManager.resetGameID (tGameID);
	}

	public void setGameIDonServer (String aGameID, int aLastActionNumber, String aGameName) {
		String tGameIDRequest;
		String tResponse;

		tGameIDRequest = buildGameIDRequest (aGameID, aLastActionNumber, aGameName);
		tResponse = gameSupportHandler.requestGameSupport (tGameIDRequest);
		logger.info ("Request sent is [" + tGameIDRequest + "]");
		logger.info ("Response is [" + tResponse + "]");
	}

	public String buildGameIDRequest (String aGameID, int aLastActionNumber, String aGameName) {
		String tGameSupport = "";

		networkMessage.buildGameXML (EN_GAME_SUPPORT, EN_LOAD_GAME_SETUP);
		networkMessage.addAttribute (EN_GAME_SUPPORT, EN_LOAD_GAME_SETUP, AN_GAME_ID, aGameID);
		networkMessage.addAttribute (EN_GAME_SUPPORT, EN_LOAD_GAME_SETUP, AN_ACTION_NUMBER, aLastActionNumber);
		networkMessage.addAttribute (EN_GAME_SUPPORT, EN_LOAD_GAME_SETUP, AN_GAME_NAME, aGameName);
		tGameSupport = GAME_SUPPORT_PREFIX + " " + networkMessage.toString ();

		return tGameSupport;
	}

	public String buildGameActivityXML (ElementName aElementName,
						AttributeName aAttributeName1, int aAttributeValue1,
						AttributeName aAttributeName2, String aAttributeValue2) {
		return buildGameActivityXML (aElementName, aAttributeName1, aAttributeValue1 + "", aAttributeName2,
				aAttributeValue2);
	}

	public String buildGameActivityXML (ElementName aElementName,
						AttributeName aAttributeName1, String aAttributeValue1,
						AttributeName aAttributeName2, String aAttributeValue2) {
		String tGameActivity = "";

		tGameActivity = buildGameXML (EN_GAME_ACTIVITY, aElementName, aAttributeName1, aAttributeValue1,
				aAttributeName2, aAttributeValue2);

		return tGameActivity;
	}

	public String buildGameActivityXML (ElementName aElementName, XMLElement aChildElement,
						AttributeName aAttributeName1, String aAttributeValue1,
						AttributeName aAttributeName2, String aAttributeValue2,
						AttributeName aAttributeName3, String aAttributeValue3) {
		String tGameActivity = "";

		tGameActivity = buildGameXML (EN_GAME_ACTIVITY, aElementName, aChildElement, aAttributeName1, aAttributeValue1,
				aAttributeName2, aAttributeValue2, aAttributeName3, aAttributeValue3);

		return tGameActivity;
	}

	public String buildGameActivityXML (ElementName aElementName,
						AttributeName aAttributeName1, String aAttributeValue1,
						AttributeName aAttributeName2, String aAttributeValue2,
						AttributeName aAttributeName3, String aAttributeValue3) {
		String tGameActivity = "";

		tGameActivity = buildGameXML (EN_GAME_ACTIVITY, aElementName, aAttributeName1, aAttributeValue1,
				aAttributeName2, aAttributeValue2, aAttributeName3, aAttributeValue3);

		return tGameActivity;
	}

	public String buildGameSupportXML (ElementName aElementName,
						AttributeName aAttributeName1, String aAttributeValue1) {
		String tGameSupport = "";

		networkMessage.buildGameXML (EN_GAME_SUPPORT, aElementName);
		networkMessage.addAttribute (EN_GAME_SUPPORT, aElementName, aAttributeName1, aAttributeValue1);
		tGameSupport = networkMessage.toString ();

		return tGameSupport;
	}

	public String buildGameXML (ElementName aPrimaryEN, ElementName aSecondaryEN,
						AttributeName aAttributeName1, String aAttributeValue1) {
		String tGameSupport;

		networkMessage.buildGameXML (aPrimaryEN, aSecondaryEN);
		networkMessage.addAttribute (aPrimaryEN, aSecondaryEN, aAttributeName1, aAttributeValue1);
		tGameSupport = networkMessage.toString ();

		return tGameSupport;
	}

	public String buildGameXML (ElementName aPrimaryEN, ElementName aSecondaryEN,
						AttributeName aAttributeName1, String aAttributeValue1,
						AttributeName aAttributeName2, String aAttributeValue2) {
		String tGameSupport;

		networkMessage.buildGameXML (aPrimaryEN, aSecondaryEN);
		networkMessage.addAttribute (aPrimaryEN, aSecondaryEN, aAttributeName1, aAttributeValue1);
		networkMessage.addAttribute (aPrimaryEN, aSecondaryEN, aAttributeName2, aAttributeValue2);
		tGameSupport = networkMessage.toString ();

		return tGameSupport;
	}

	public String buildGameXML (ElementName aPrimaryEN, ElementName aSecondaryEN, XMLElement aChildElement,
			AttributeName aAttributeName1, String aAttributeValue1,
			AttributeName aAttributeName2, String aAttributeValue2,
			AttributeName aAttributeName3, String aAttributeValue3) {
		String tGameSupport;

		networkMessage.buildGameXML (aPrimaryEN, aSecondaryEN);
		networkMessage.appendChild (aPrimaryEN, aSecondaryEN, aChildElement);
		networkMessage.addAttribute (aPrimaryEN, aSecondaryEN, aAttributeName1, aAttributeValue1);
		networkMessage.addAttribute (aPrimaryEN, aSecondaryEN, aAttributeName2, aAttributeValue2);
		networkMessage.addAttribute (aPrimaryEN, aSecondaryEN, aAttributeName3, aAttributeValue3);
		tGameSupport = networkMessage.toString ();

		return tGameSupport;
	}

	public String buildGameXML (ElementName aPrimaryEN, ElementName aSecondaryEN,
						AttributeName aAttributeName1, String aAttributeValue1,
						AttributeName aAttributeName2, String aAttributeValue2,
						AttributeName aAttributeName3, String aAttributeValue3) {
		String tGameSupport;

		networkMessage.buildGameXML (aPrimaryEN, aSecondaryEN);
		networkMessage.addAttribute (aPrimaryEN, aSecondaryEN, aAttributeName1, aAttributeValue1);
		networkMessage.addAttribute (aPrimaryEN, aSecondaryEN, aAttributeName2, aAttributeValue2);
		networkMessage.addAttribute (aPrimaryEN, aSecondaryEN, aAttributeName3, aAttributeValue3);
		tGameSupport = networkMessage.toString ();

		return tGameSupport;
	}

	public String wrapWithGA (String aXMLText) {
		String tXMLWrapped;

		tXMLWrapped = GA_XML_START + aXMLText + GA_XML_END;

		return tXMLWrapped;
	}

	private void sendMessage (ActionEvent aActionEvent) {
		String tAction = aActionEvent.getActionCommand ();
		sendMessage (tAction);
	}

	private void sendMessage (String aAction) {
		if (SEND.equals (aAction)) {
			String tMessage = messageField.getText ();

			// De-activate AFK, if it has been set
			backFromAFK ();

			// Send the Message to the Server
			if (tMessage.length () > 0) {
				if (tMessage.startsWith (GA_XML_START) && tMessage.endsWith (GA_XML_END)) {
					serverHandler.sendGameActivity (tMessage);
				} else if (tMessage.startsWith (GA_XML_START) && tMessage.endsWith (GA_XML_END)) {
					serverHandler.sendGameSupport (tMessage);
				} else {
					serverHandler.sendMessage (tMessage);
					StyleConstants.setItalic (iSaid, true);
					appendToChat ("I said: " + tMessage, iSaid);
				}
			}
			messageField.setText ("");
		}
	}

	public void appendToGameActivity (String aGameActivity) {
		try {
			Document doc = gameActivity.getDocument ();
			doc.insertString (doc.getLength (), "\n" + aGameActivity, normal);
			scroll (spGameActivity, ScrollDirection.DOWN);
		} catch (BadLocationException exc) {
			exc.printStackTrace ();
		}
	}

	public void handleGameActivity (String aGameActivity) {
		String tGameActivity;

		tGameActivity = aGameActivity.substring (GAME_ACTIVITY_PREFIX.length () + 1);
		gameManager.handleGameActivity (tGameActivity);
	}

	public void handleServerMessage (String tMessage) {
		String tPatternStart, tPatternEnd;

		tPatternStart = GAME_ACTIVITY_PREFIX + " " + GA_XML_START;
		tPatternEnd = GA_XML_END;
		if (tMessage.startsWith (tPatternStart) && tMessage.endsWith (tPatternEnd)) {
			handleGameActivity (tMessage);
		} else {
			appendToChat (tMessage);
		}
	}

	public void appendToChat (String aString) {
		appendToChat (aString, normal);
	}

	public void appendToChat (String aString, boolean aISent) {
		if (aISent) {
			StyleConstants.setItalic (iSaid, true);
			appendToChat (aString, iSaid);
		}
	}

	public void appendToChat (String aString, SimpleAttributeSet aStyle) {
		try {
			Document doc = chatText.getDocument ();
			doc.insertString (doc.getLength (), "\n" + aString, aStyle);
			scroll (spChatText, ScrollDirection.DOWN);
		} catch (BadLocationException exc) {
			exc.printStackTrace ();
		}
	}

	public JTextPane getChatText () {
		return chatText;
	}

	// --- End Message and Chat Management

	// Client Player Management ---

	public void playerReady () {
		backFromAFK ();
		playerReady (getName ());
	}

	public void playerReady (String aPlayerName) {
		int tIndex = aPlayerName.indexOf (aPlayerName);
		String tPlayerName = aPlayerName;

		if (tIndex > 0) {
			tPlayerName = aPlayerName.substring (0, tIndex);
		}

		networkPlayers.setPlayerReady (tPlayerName, true);
		if (networkPlayers.allPlayersAreReady ()) {
			updateReadyButton (START_GAME, true, "Hit to Start Game");
		}
	}

	public void playerActive (String aPlayerName) {
		networkPlayers.setPlayerActive (aPlayerName, true);
	}

	public void addLocalPlayer (String aLocalPlayer, boolean aAutoConnect) {
		if (NetworkPlayer.validPlayerName (aLocalPlayer)) {
			playerName.setText (aLocalPlayer);
			if (aAutoConnect) {
				setupNewPlayer (CONNECT_ACTION);
				gameManager.updatePlayerCountLabel ();
			}
		}
	}

	private void backFromAFK () {
		if (isPlayerAFK ()) {
			serverHandler.sendUserIsNotAFK ();
		}
		resetPlayerFromeAFK ();
		awayFromKeyboardAFKButton.setEnabled (true);
	}

	public boolean isPlayerAFK () {
		return networkPlayers.playerIsAFK (getName ());
	}

	@Override
	public String getName () {
		return getPlayerName ();
	}

	public String getPlayerName () {
		return playerName.getText ();
	}

	public JTextField getPlayerNameJTF () {
		return playerName;
	}

	// --- End Client Player Management ---

	// Player List Management ---

	public void addPlayer (String aPlayerName) {
		networkPlayers.addPlayer (aPlayerName);
	}

	public void setPlayerAsAFK (String aPlayerName) {
		networkPlayers.setPlayerAFK (aPlayerName, true);
	}

	public void resetPlayerFromeAFK () {
		resetPlayerFromAFK (playerName.getText ());
	}

	public void resetPlayerFromAFK (String aPlayerName) {
		networkPlayers.setPlayerAFK (aPlayerName, false);
	}

	public void removePlayer (String aPlayerName) {
		networkPlayers.removePlayer (aPlayerName);
	}

	public void rejectedConnect () {
		networkPlayers.removeAllPlayers ();
		setForUnconnected ();
	}

	// --- End Player List Management

	/**
	 * Scrolls a {@code scrollPane} all the way up or down.
	 *
	 * @param aScrollPane the scrollPane that we want to scroll up or down
	 * @param aDirection  we scroll up if this is {@link ScrollDirection#UP}, or
	 *                    down if it's {@link ScrollDirection#DOWN}
	 */

	public static void scroll (JScrollPane aScrollPane, ScrollDirection aDirection) {
		final JScrollBar tVerticalBar = aScrollPane.getVerticalScrollBar ();

		// If we want to scroll to the top, set this value to the minimum,
		// else to the maximum
		final int tTopOrBottom = aDirection == ScrollDirection.UP ? tVerticalBar.getMinimum ()
				: tVerticalBar.getMaximum ();

		AdjustmentListener scroller = new AdjustmentListener () {
			@Override
			public void adjustmentValueChanged (AdjustmentEvent tEvent) {
				Adjustable adjustable = tEvent.getAdjustable ();
				adjustable.setValue (tTopOrBottom);
				// We have to remove the listener, otherwise the
				// user would be unable to scroll afterwards
				tVerticalBar.removeAdjustmentListener (this);
			}
		};
		tVerticalBar.addAdjustmentListener (scroller);
	}

	public enum ScrollDirection {
		UP, DOWN
	}

	public XMLElement getNetworkElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_NETWORK_GAME);
		tXMLElement.setAttribute (AN_SERVER_IP, getServerIP ());
		tXMLElement.setAttribute (AN_SERVER_PORT, getServerPort ());

		return tXMLElement;
	}

	public void setServerIP (String aServerIP) {
		serverIP = aServerIP;
	}

	public void setServerPort (int aServerPort) {
		serverPort = aServerPort;
	}

	public String getServerIP () {
		return serverIP;
	}

	public int getServerPort () {
		return serverPort;
	}

	// Need to make a Request for Game Support, then block until we get a response
	// Send the response back

	public String requestGameSupport (String aGameID, String aRequestGameSupport) {
		String tGSResponse;
		String tFullGSRequest;

		tFullGSRequest = serverHandler.buildGameSupportXML (aGameID, aRequestGameSupport);
		tGSResponse = gameSupportHandler.requestGameSupport (tFullGSRequest);

		return tGSResponse;
	}

	public void handleGSResponse (String aGSResponse) {
		gameSupportHandler.handleGSResponse (aGSResponse);
	}

	public ServerHandler getServerHandler () {
		return serverHandler;
	}

	public String getXMLBaseDirectory () {
		return gameManager.getXMLBaseDirectory ();
	}

	public String getGameID () {
		return gameManager.getGameID ();
	}

	public void loadSavedGameJList (SavedGames aNetworkSavedGames) {
		SavedGame tSavedGame;
		String tSavedGameInfo;

		savedGamesListModel = new DefaultListModel<> ();
		for (int tIndex = 0; tIndex < aNetworkSavedGames.getSavedGameCount (); tIndex++) {
			tSavedGame = aNetworkSavedGames.getSavedGameAt (tIndex);
			if (tSavedGame.localAutoSaveFound ()) {
				tSavedGameInfo = "   : " + tSavedGame.getGameName () + " : " + tSavedGame.getGameID () + " : Players ("
						+ tSavedGame.getPlayers () + ") : " + " Last Action Number : "
						+ tSavedGame.getLastActionNumber ();
				savedGamesListModel.addElement (tSavedGameInfo);
			}
		}
		savedGamesList = new JList<> (savedGamesListModel);
		savedGamesList.setSelectionMode (ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		savedGamesList.setLayoutOrientation (JList.VERTICAL);
		savedGamesList.addListSelectionListener (new ListSelectionListener () {
			@Override
			public void valueChanged (ListSelectionEvent aEvent) {
				savedGameSelected (aEvent);
			}
		});
	}

	private void savedGameSelected (ListSelectionEvent aEvent) {
		String tSelectedGame;
		String [] tSelectedParts;
		String tNewSaveGameFile;
		FileUtils tFileUtils;

		tFileUtils = gameManager.getFileUtils ();
		tSelectedGame = savedGamesList.getSelectedValue ();
		tSelectedParts = tSelectedGame.split (" : ");
		tNewSaveGameFile = tSelectedParts [1] + "." + tSelectedParts [2] + "." + gameManager.getClientUserName ()
				+ ".save" + tFileUtils.xml;
		if (!tNewSaveGameFile.equals (autoSaveFileName)) {
			System.out.println ("Selected Saved Game is [" + tSelectedGame + "]");
			System.out.println (" Part 0 [" + tSelectedParts [0] + "]" + " Part 1 [" + tSelectedParts [1] + "]"
					+ " Part 2 [" + tSelectedParts [2] + "]" + " Part 3 [" + tSelectedParts [3] + "]");
			System.out.println ("New Auto Save File Name: " + tNewSaveGameFile);
			autoSaveFileName = tNewSaveGameFile;
			updateReadyButton (PLAY_GAME, true, GUI.NO_TOOL_TIP);
		}
	}

	public void loadAndStartGame () {
		gameManager.loadAutoSavedGame (autoSaveFileName);
		serverHandler.sendUserActive (getGameID ());
		refreshPlayers ();
		System.out.println ("Should have Game Manager Load the Network Game, and Start Playing " + "with Game ID ["
				+ getGameID () + "]");
		swapToGameActivity ();
	}

	public int getAutoSavedLastAction () {
		int tLastAction = 99;
		String tSelectedGame;
		String [] tSelectedParts;

		tSelectedGame = savedGamesList.getSelectedValue ();
		tSelectedParts = tSelectedGame.split (" : ");
		tLastAction = Integer.parseInt (tSelectedParts [5]);

		return tLastAction;
	}

	// TODO Extract the method building the Saved Game Panel to separate Class,
	// into the ge18xx.game Package
	// Take along Subroutines with it.
	// Leave new method to set the networkSavedGames JPanel
	public void buildNetworkSGPanel (SavedGames aNetworkSavedGames) {
		JLabel tPanelTitle;
		int tSavedGameCount;
		String tTitle;
		Box tNetworkSavedGamesBox;

		networkSavedGamesPanel = new JPanel ();
		tNetworkSavedGamesBox = Box.createVerticalBox ();
		tSavedGameCount = aNetworkSavedGames.getMatchedSavedGameCount ();

		if (tSavedGameCount == 1) {
			tTitle = " Your Saved Game ";
		} else if (tSavedGameCount > 1) {
			tTitle = "Your " + tSavedGameCount + " Saved Games";
		} else {
			tTitle = "You have no Saved Games";
		}

		tPanelTitle = new JLabel (tTitle);
		tNetworkSavedGamesBox.add (Box.createVerticalStrut (10));
		tNetworkSavedGamesBox.add (tPanelTitle);

		if (tSavedGameCount > 0) {
			loadSavedGameJList (aNetworkSavedGames);
			tNetworkSavedGamesBox.add (Box.createVerticalStrut (20));
			tNetworkSavedGamesBox.add (savedGamesList);
			showSavedGames.setToolTipText ("Show " + tTitle);
			updateShowSavedGamesButton ();
		} else {
			showSavedGames.setEnabled (false);
			showSavedGames.setToolTipText (tTitle);
		}

		networkSavedGamesPanel.add (tNetworkSavedGamesBox);
	}

	public String fetchActionWithNumber (int tActionNumber, String aGameID) {
		String tActionFound;
		String tRequestAction;
		String tResponse;

		tRequestAction = "<RequestAction actionNumber=\"" + tActionNumber + "\">";
		tResponse = requestGameSupport (aGameID, tRequestAction);

		tActionFound = "Network Provided Action Number " + tActionNumber;

		tActionFound = tResponse;

		return tActionFound;
	}

	public void stopHeartbeatDelivery () {
		if (heartbeatThread != null) {
			heartbeatThread.stopHeartbeatDelivery ();
		}
	}

	public void startHeartbeatDelivery () {
		heartbeatThread.startHeartbeatDelivery ();
	}
}