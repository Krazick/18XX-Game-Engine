package ge18xx.game;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.logging.log4j.Logger;

import ge18xx.network.JGameClient;

//
//Game_18XX.java
//Game_18XX
//
//Created by Mark Smith on 8/25/07.
//Copyright (c) 2007 __MyCompanyName__. All rights reserved.
//
//For information on setting Java configuration information, including
//setting Java properties, refer to the documentation at
//	http://developer.apple.com/techpubs/java/java.html
//

import ge18xx.network.NetworkPlayer;
import ge18xx.toplevel.AboutBox;
import ge18xx.toplevel.PlayerInputFrame;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.GUI;
import ge18xx.utilities.Sound;
import ge18xx.utilities.XMLDocument;
import log4j2.plugins.LoggerLookup;

// TODO -- Create an abstract Super Class that is "GameEngineFrame" that has non-specific objects
// the Game_18XX Class that extends the GameEngineFrame,
public class Game_18XX extends XMLFrame {
	private static final long serialVersionUID = 1L;

	// Generic Game Engine Fields
	private final String ENTER_USER_NAME = "Must Enter User Name";
	private final String OK_TEXT = "OK";
	private final String QUIT_TEXT = "Quit";
	public final String DATA_URL_BASE = "DataURLBase";
	protected ResourceBundle resbundle;
	protected AboutBox aboutBox;
	protected JMenu fileMenu, gameMenu;
	protected Action newAction, openAction, closeAction, saveAction, saveAsAction, saveConfigAction;
	protected Action exitAction, undoAction, cutAction, copyAction;
	protected Action pasteAction, clearAction, selectAllAction, userPreferencesAction;
	protected Action selectGameAction, showActionReportFrameAction, showPlayerInputAction;

	// Game18XX Specific Menu Actions
	protected Action showMapAction, showMarketAction, showCitiesAction, showPrivatesAction;
	protected Action showTileTrayAction, showMinorCompaniesAction;
	protected Action showChatClientAction, showRoundFrameAction, showShareCompaniesAction;
	protected Action showAuditFrameAction;

	// More Generic Game Engine Fields
	JMenuBar mainMenuBar;
	JMenuItem gameMenuItems[];
	JMenuItem userPreferencesMenuItem;
	JMenuItem newMenuItem;
	JMenuItem openMenuItem;
	JMenuItem closeMenuItem;
	JMenuItem saveMenuItem;
	JMenuItem saveAsMenuItem;
	JMenuItem saveConfigMenuItem;
	JMenuItem frameInfoMenuItem;
	JMenuItem exitMenuItem;
	private JTextField clientUserName;
	private JButton newGameButton;
	private JButton quitButton;
	private JButton disconnectButton;
	
	GameManager gameManager;
	PlayerInputFrame playerInputFrame;
	LoggerLookup loggerLookup = new LoggerLookup ();
	String userDir = System.getProperty ("user.dir");
	Image iconImage;

	public Game_18XX () {
		this (true);
	}

	public Game_18XX (boolean aVisible) {
		super ("");

		// The ResourceBundle below contains all of the strings used in this
		// application. ResourceBundles are useful for localizing applications.
		// New localities can be added by adding additional properties files.
		loadResourceBundle ("ge18xx");

		setApplicationIcon ();

		createActions ();
		addMenus ();

		setSize (360, 300);
		setLocation (100, 100);
		setFrameContents ();
		setupFrameActions ();
		toFront ();
		setVisible (aVisible);

		playWhistle ();

		addWindowListener (new WindowAdapter () {
			@Override
			public void windowClosing (WindowEvent evt) {
				onExit ();
			}
		});
		updateDisconnectButton ();
	}

	private void loadResourceBundle (String aGameEngineName) {
		String tTitle;

		if (aGameEngineName.length () > 0) {
			resbundle = ResourceBundle.getBundle (aGameEngineName + ".game.MyResources", Locale.getDefault ());
			tTitle = resbundle.getString ("frameTitle");
		} else {
			tTitle = "Generic Game Engine";
		}
		setTitle (tTitle);
	}

	public void onExit () {
		int tConfirm;
		Logger tLogger;
		String tQuestion;

		tQuestion = "Are You Sure to Exit the " + getTitle () + "?";

		tConfirm = JOptionPane.showConfirmDialog (this, tQuestion, "Exit Confirmation", JOptionPane.YES_NO_OPTION);
		if (tConfirm == 0) {
			tLogger = loggerLookup.getLogger ();
			if (tLogger != null) {
				tLogger.info ("EXITING Game Engine");
			}
			disconnect ();
			System.exit (0);
		}
	}

	private void playWhistle () {
		Sound tSound;

		tSound = new Sound ();
		tSound.playSoundClip (tSound.WHISTLE);
	}

	public void setupLogger (String aUserName, String aAppName) {
		String tAppVersion;
		String tXMLConfigFileDir;

		if (getLogger () == null) {
			tAppVersion = getGEVersion ();
			tXMLConfigFileDir = resbundle.getString ("configDir");
			loggerLookup.setupLogger (aUserName, aAppName, tAppVersion, tXMLConfigFileDir, ge18xx.game.Game_18XX.class);
		}
	}

	public String getGEVersion () {
		return resbundle.getString ("version");
	}

	public String getUserDir () {
		return userDir;
	}

	public Logger getLogger () {
		return loggerLookup.getLogger ();
	}

	public static Logger getLoggerX () {
		return LoggerLookup.getLoggerX ();
	}

	private void setApplicationIcon () {
		// This will set the GE18XX Frame Icon (when it is minimized)
		// For Mac on the Dock even, but not the Application Level Icon.
		iconImage = getIconImage ();
		setIconImage (iconImage);
	}

	@Override
	public Image getIconImage () {
		String tIconPath;
		ImageIcon tIcon;

		tIconPath = resbundle.getString ("iconImage");
		tIcon = new ImageIcon (tIconPath);
		iconImage = tIcon.getImage ();

		return iconImage;
	}

	public void setGameManager (GameManager aGameManager) {
		gameManager = aGameManager;
	}

	private void setupAutoSavesAndLogDirectory () {
		String tAutoSavesDirName;
		String tAutoSavesLogDirName;
		String tAutoSavesNetwork;

		tAutoSavesDirName = "autoSaves";
		createDirectory (tAutoSavesDirName);
		tAutoSavesLogDirName = tAutoSavesDirName + File.separator + "logs";
		createDirectory (tAutoSavesLogDirName);
		tAutoSavesNetwork = tAutoSavesDirName + File.separator + "network";
		createDirectory (tAutoSavesNetwork);
	}

	private void createDirectory (String tDirectoryName) {
		File tDirectory = new File (tDirectoryName);

		if (!tDirectory.exists ()) {
			tDirectory.mkdir ();
		}
	}

	private void setupNewGameManager () {
		String tClientName = clientUserName.getText ();

		setupAutoSavesAndLogDirectory ();
		if (NetworkPlayer.validPlayerName (tClientName)) {
			setupLogger (tClientName, "GE18XX");
			setGameManager (new GameManager (this, tClientName));
			enableGameStartItems ();
			newGameButton.requestFocusInWindow ();
		} else if (!(tClientName.equals (""))) {
			clientUserName.setText ("INVALID NAME");
			clientUserName.requestFocusInWindow ();
		}
	}

	private void setupFrameActions () {
		clientUserName.addFocusListener (new FocusAdapter () {
			@Override
			public void focusLost (FocusEvent aEvent) {
				Object tEventObject = aEvent.getSource ();

				if (tEventObject instanceof JTextField) {
					setupNewGameManager ();
				}
			}
		});

		clientUserName.addKeyListener (new KeyAdapter () {
			@Override
			public void keyReleased (KeyEvent e) {
				enableGameStartItems ();
			}
		});

		newGameButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent aEvent) {
				newGame ();
			}
		});

		newGameButton.addKeyListener (new KeyAdapter () {
			@Override
			public void keyReleased (KeyEvent e) {
				if (e.getKeyCode () == KeyEvent.VK_ENTER) {
					newGame ();
				}
			}
		});

		quitButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent aEvent) {
				onExit ();
			}
		});

		disconnectButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent aActionEvent) {
				String tAction = aActionEvent.getActionCommand ();

				if (JGameClient.DISCONNECT.equals (tAction)) {
					disconnect ();
				}
			}
		});

	}

	private void disconnect () {
		if (gameManager != GameManager.NO_GAME_MANAGER) {
			gameManager.disconnect ();
		}
	}

	private void setFrameContents () {
		String tJavaVersion;
		JLabel tJavaLabel;
		JLabel tGameEngineTitle;
		JLabel tGameEngineVersion;
		JLabel tClientLabel;
		JPanel tPrimaryPanel;
		JPanel tClientPanel;
		JPanel tButtonPanel;

		tPrimaryPanel = new JPanel ();
		tPrimaryPanel.setLayout (new BoxLayout (tPrimaryPanel, BoxLayout.Y_AXIS));
		
		tGameEngineTitle = new JLabel ("Game Engine Title");
		tGameEngineTitle.setText (resbundle.getString ("message"));
		tGameEngineTitle.setFont (new Font ("Lucida Grande", Font.BOLD, 20));
		tGameEngineTitle.setAlignmentX (Component.CENTER_ALIGNMENT);

		tJavaVersion = System.getProperty ("java.version");
		tJavaLabel = new JLabel ("Java Version " + tJavaVersion);
		tJavaLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		tJavaLabel.setFont (new Font ("Lucida Grande", Font.PLAIN, 14));

		tGameEngineVersion = new JLabel ("Version: X.X");
		tGameEngineVersion.setText ("V " + getGEVersion ());
		tGameEngineVersion.setAlignmentX (Component.CENTER_ALIGNMENT);
		tGameEngineVersion.setFont (new Font ("Lucida Grande", Font.PLAIN, 16));

		tClientLabel = new JLabel ("Client User Name:");

		clientUserName = new JTextField ();
		clientUserName.setColumns (10);
		clientUserName.setEnabled (true);
		
		tClientPanel = new JPanel ();
		tClientPanel.setLayout (new BoxLayout (tClientPanel, BoxLayout.X_AXIS));
		tClientPanel.add (Box.createHorizontalStrut (10));
		tClientPanel.add (tClientLabel);
		tClientPanel.add (Box.createHorizontalStrut (10));
		tClientPanel.add (clientUserName);
		tClientPanel.add (Box.createHorizontalStrut (10));
		tClientPanel.setMaximumSize (new Dimension (320, 35));
		
		tPrimaryPanel.add (Box.createVerticalStrut (10));
		tPrimaryPanel.add (tGameEngineTitle);
		tPrimaryPanel.add (Box.createVerticalStrut (10));
		tPrimaryPanel.add (tJavaLabel);
		tPrimaryPanel.add (Box.createVerticalStrut (10));
		tPrimaryPanel.add (tGameEngineVersion);
		tPrimaryPanel.add (Box.createVerticalStrut (10));
		tPrimaryPanel.add (Box.createVerticalGlue ());
		tPrimaryPanel.add (tClientPanel);
		tPrimaryPanel.add (Box.createVerticalGlue ());
		tPrimaryPanel.add (Box.createVerticalStrut (10));

		tButtonPanel = new JPanel ();
		tButtonPanel.setLayout (new BoxLayout (tButtonPanel, BoxLayout.X_AXIS));
		newGameButton = new JButton (OK_TEXT);
		quitButton = new JButton (QUIT_TEXT);
		disconnectButton = new JButton (JGameClient.DISCONNECT);
		tButtonPanel.add (Box.createHorizontalStrut (10));
		tButtonPanel.add (newGameButton);
		tButtonPanel.add (Box.createHorizontalStrut (10));
		tButtonPanel.add (quitButton);
		tButtonPanel.add (Box.createHorizontalStrut (10));
		tButtonPanel.add (disconnectButton);
		tButtonPanel.add (Box.createHorizontalStrut (10));

		tButtonPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		tPrimaryPanel.add (tButtonPanel);
		tPrimaryPanel.add (Box.createVerticalStrut (10));
		add (tPrimaryPanel);
		pack ();
		disableGameButtons ();
	}

	@Override
	public void paint (Graphics g) {
		super.paint (g);
	}

	/*
	 * public void about (ApplicationEvent e) { aboutBox.setResizable (false);
	 * aboutBox.setVisible (true); }
	 * 
	 */

	private void addMenus () {
		mainMenuBar = new JMenuBar ();
		setupFileMenu ();
		setupGameMenu ();

		setJMenuBar (mainMenuBar);
	}

	private void setupFileMenu () {
		fileMenu = new JMenu ("File");
		userPreferencesMenuItem = new JMenuItem (userPreferencesAction);
		disableUserPreferencesMenuItem ();
		fileMenu.add (userPreferencesMenuItem);
		newMenuItem = new JMenuItem (newAction);
		disableNewMenuItem ();
		fileMenu.add (newMenuItem);
		openMenuItem = new JMenuItem (openAction);
		disableOpenMenuItem ();
		fileMenu.add (openMenuItem);
		closeMenuItem = new JMenuItem (closeAction);
		fileMenu.add (closeMenuItem);
		saveMenuItem = new JMenuItem (saveAction);
		disableSaveMenuItem ();
		fileMenu.add (saveMenuItem);
		saveAsMenuItem = new JMenuItem (saveAsAction);
		disableSaveAsMenuItem ();
		fileMenu.add (saveAsMenuItem);
		saveConfigMenuItem = new JMenuItem (saveConfigAction);
		disableSaveConfigMenuItem ();
		fileMenu.add (saveConfigMenuItem);
		exitMenuItem = new JMenuItem (exitAction);
		fileMenu.add (exitMenuItem);
		mainMenuBar.add (fileMenu);
	}

	private void setupGameMenu () {
		int tMenuItemCount;
		int tMenuItemIndex;
		int tMenuIndex;

		gameMenu = new JMenu ("Game");
		tMenuItemCount = 10;
		gameMenuItems = new JMenuItem [tMenuItemCount];
		tMenuIndex = 0;
		tMenuIndex = addGameMenu (tMenuIndex, showMapAction);
		tMenuIndex = addGameMenu (tMenuIndex, showMarketAction);
		tMenuIndex = addGameMenu (tMenuIndex, showCitiesAction);
		tMenuIndex = addGameMenu (tMenuIndex, showPrivatesAction);
		tMenuIndex = addGameMenu (tMenuIndex, showShareCompaniesAction);
		tMenuIndex = addGameMenu (tMenuIndex, showTileTrayAction);
		tMenuIndex = addGameMenu (tMenuIndex, showChatClientAction);
		tMenuIndex = addGameMenu (tMenuIndex, showRoundFrameAction);
		tMenuIndex = addGameMenu (tMenuIndex, showAuditFrameAction);
		tMenuIndex = addGameMenu (tMenuIndex, showActionReportFrameAction);

		for (tMenuItemIndex = 0; tMenuItemIndex < tMenuItemCount; tMenuItemIndex++) {
			gameMenuItems [tMenuItemIndex].setEnabled (false);
			gameMenu.add (gameMenuItems [tMenuItemIndex]);
		}
		mainMenuBar.add (gameMenu);
	}

	private int addGameMenu (int aMenuIndex, Action aMenuAction) {
		gameMenuItems [aMenuIndex] = new JMenuItem (aMenuAction);

		return (aMenuIndex + 1);
	}

	private void createActions () {
		int tShortcutKeyMask = Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask ();

		setupGenericActions (tShortcutKeyMask);
		setupGameSpecificActions (tShortcutKeyMask);
	}

	private void setupGameSpecificActions (int aShortcutKeyMask) {
		showMapAction = new showMapActionClass (resbundle.getString ("showMapItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_M, aShortcutKeyMask));
		showMarketAction = new showMarketActionClass (resbundle.getString ("showMarketItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_K, aShortcutKeyMask));
		showCitiesAction = new showCitiesActionClass (resbundle.getString ("showCitiesItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_L, aShortcutKeyMask));
		showTileTrayAction = new showTileTrayActionClass (resbundle.getString ("showTileTrayItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_T, aShortcutKeyMask));
		showPrivatesAction = new showPrivatesActionClass (resbundle.getString ("showPrivatesItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_P, aShortcutKeyMask));
		showShareCompaniesAction = new showShareCompaniesActionClass (resbundle.getString ("showShareCompaniesItem"),
				null);
		showChatClientAction = new showChatClientActionClass (resbundle.getString ("showChatClientItem"), null);
		showRoundFrameAction = new showRoundFrameActionClass (resbundle.getString ("showRoundFrameItem"), null);
		showAuditFrameAction = new showAuditFrameActionClass (resbundle.getString ("showAuditFrameItem"), null);
		showActionReportFrameAction = new showActionReportFrameActionClass (
				resbundle.getString ("showActionReportFrameItem"), null);
	}

	private void setupGenericActions (int aShortcutKeyMask) {
		// Create actions that can be used by menus, buttons, toolbars, etc.
		userPreferencesAction = new userPreferencesActionClass (resbundle.getString ("preferencesItem"), null);
		newAction = new newActionClass (resbundle.getString ("newItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_N, aShortcutKeyMask));
		openAction = new openActionClass (resbundle.getString ("openItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_O, aShortcutKeyMask));
		closeAction = new closeActionClass (resbundle.getString ("closeItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_W, aShortcutKeyMask));
		saveAction = new saveActionClass (resbundle.getString ("saveItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_S, aShortcutKeyMask));
		saveAsAction = new saveAsActionClass (resbundle.getString ("saveAsItem"));
		saveConfigAction = new saveConfigActionClass (resbundle.getString ("saveConfigItem"));
		exitAction = new exitActionClass (resbundle.getString ("exitItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_Q, aShortcutKeyMask));
	}

	public void createGameSet () {
		if (loadGameSet ()) {
			playerInputFrame.addGameInfo ();
		}
	}

	public void createPlayerInputFrame () {
		Point tNewPoint;

		if (playerInputFrame == XMLFrame.NO_XML_FRAME) {
			playerInputFrame = new PlayerInputFrame ("Enter Player Information", gameManager);
			tNewPoint = getOffsetGEFrame ();
			playerInputFrame.setLocation (tNewPoint);
			createGameSet ();
		}
	}

	public Point getOffsetGEFrame () {
		Point tGEFramePoint, tNewPoint;
		double tX, tY;
		int tNewX, tNewY;

		tGEFramePoint = getLocation ();
		tX = tGEFramePoint.getX ();
		tY = tGEFramePoint.getY ();
		tNewX = (int) tX + 100;
		tNewY = (int) tY + 100;
		tNewPoint = new Point (tNewX, tNewY);

		return tNewPoint;
	}

	public void disableUserPreferencesMenuItem () {
		userPreferencesMenuItem.setEnabled (false);
	}

	public void disableNewMenuItem () {
		newMenuItem.setEnabled (false);
	}

	public void disableOpenMenuItem () {
		openMenuItem.setEnabled (false);
	}

	public void disableCloseMenuItem () {
		closeMenuItem.setEnabled (false);
	}

	public void disableSaveMenuItem () {
		saveMenuItem.setEnabled (false);
	}

	public void disableSaveAsMenuItem () {
		saveAsMenuItem.setEnabled (false);
	}

	public void disableSaveConfigMenuItem () {
		saveConfigMenuItem.setEnabled (false);
	}

	public void updateDisconnectButton () {
		if (gameManager == GameManager.NO_GAME_MANAGER) {
			disconnectButton.setEnabled (false);
			disconnectButton.setToolTipText ("No Game Initialized yet");
		} else if (!gameManager.isNetworkGame ()) {
			disconnectButton.setEnabled (false);
			disconnectButton.setToolTipText ("Not a Network Game");
		} else if (gameManager.isConnected ()) {
			disconnectButton.setEnabled (true);
			disconnectButton.setToolTipText ("Will Disconnect from Network Connect");
		} else {
			disconnectButton.setEnabled (false);
			disconnectButton.setToolTipText ("Not Connected to a Network Game");
		}
	}

	public void disableGameButtons () {
		newGameButton.setEnabled (false);
		newGameButton.setToolTipText (ENTER_USER_NAME);
	}

	public void disableGameStartItems () {
		disableNewMenuItem ();
		disableOpenMenuItem ();
		disableCloseMenuItem ();
		disableGameButtons ();
		clientUserName.setEnabled (false);
	}

	public void enableGameStartItems () {
		enableNewMenuItem ();
		enableOpenMenuItem ();
		newGameButton.setEnabled (true);
		newGameButton.setToolTipText (GUI.NO_TOOL_TIP);
		clientUserName.setEnabled (true);
	}

	public void enableGameMenuItems () {
		int tMenuItemIndex, tMenuItemCount;
		String tMinorMenuText = resbundle.getString ("showMinorsItem");
		String tPrivateMenuText = resbundle.getString ("showPrivatesItem");
		String tChatClientText = resbundle.getString ("showChatClientItem");
		String tMenuText;
		boolean tEnableMenuItem;

		tMenuItemCount = gameMenuItems.length;
		for (tMenuItemIndex = 0; tMenuItemIndex < tMenuItemCount; tMenuItemIndex++) {
			tMenuText = gameMenuItems [tMenuItemIndex].getText ();
			if (tMenuText.equals (tMinorMenuText)) {
				if (gameManager.getCountOfMinors () > 0) {
					tEnableMenuItem = true;
				} else {
					tEnableMenuItem = false;
				}
			} else if (tMenuText.equals (tPrivateMenuText)) {
				if (gameManager.getCountOfPrivates () > 0) {
					tEnableMenuItem = true;
				} else {
					tEnableMenuItem = false;
				}
			} else if (tMenuText.equals (tChatClientText)) {
				if (gameManager.isNetworkGame ()) {
					tEnableMenuItem = true;
				} else {
					tEnableMenuItem = false;
				}
			} else {
				tEnableMenuItem = true;
			}
			gameMenuItems [tMenuItemIndex].setEnabled (tEnableMenuItem);
		}
	}

	public void enableUserPreferencesMenuItem () {
		userPreferencesMenuItem.setEnabled (true);
	}

	public void enableNewMenuItem () {
		newMenuItem.setEnabled (true);
	}

	public void enableOpenMenuItem () {
		openMenuItem.setEnabled (true);
	}

	public void enableCloseMenuItem () {
		closeMenuItem.setEnabled (true);
	}

	public void enableSaveMenuItem () {
		saveMenuItem.setEnabled (true);
	}

	public void enableSaveAsMenuItem () {
		saveAsMenuItem.setEnabled (true);
	}

	public void enableSaveConfigMenuItem () {
		saveConfigMenuItem.setEnabled (true);
	}

	public void enableSaveMenuItems () {
		enableSaveMenuItem ();
		enableSaveAsMenuItem ();
		enableSaveConfigMenuItem ();
	}

	public void initiateGame () {
		disableGameStartItems ();
		enableGameMenuItems ();
		enableSaveMenuItems ();
	}

	public String getURLValue (String aResourceName) {
		String tURLBase;
		
		tURLBase = resbundle.getString (aResourceName);
		
		return tURLBase;
	}
	
	public String getURLBase () {
		return getURLValue (DATA_URL_BASE);
	}
	
	public boolean loadGameSet () {
		String tURLBase;
		String tGameURLFileName;
		String tFullURL;
		boolean tLoadedGameSet;
		GameSet tGameSet;
		XMLDocument tURLDocument;

		tURLBase = getURLBase ();
		tGameURLFileName = getURLValue ("GameSetURLFile");
		tFullURL = tURLBase + tGameURLFileName;
		tURLDocument = new XMLDocument (tFullURL);
		
		try {
			createPlayerInputFrame ();
			tGameSet = playerInputFrame.getGameSet ();
			playerInputFrame.loadXML (tURLDocument, tGameSet);
			tLoadedGameSet = true;
		} catch (IOException tException) {
			System.err.println ("Caught Exception " + tException);
			tLoadedGameSet = false;
		}

		return tLoadedGameSet;
	}

	public class showMapActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public showMapActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			gameManager.showMap ();
		}
	}

	public class showMarketActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public showMarketActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			gameManager.showMarket ();
		}
	}

	public class showCitiesActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public showCitiesActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			gameManager.showCities ();
		}
	}

	public class showTileTrayActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public showTileTrayActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			gameManager.showTileTray ();
		}
	}

	public class showPrivatesActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public showPrivatesActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			gameManager.showPrivateCompanies ();
		}
	}

	public class showMinorsActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public showMinorsActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			gameManager.showMinorCompanies ();
		}
	}

	public class showShareCompaniesActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public showShareCompaniesActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			gameManager.showShareCompanies ();
		}
	}

	public class showChatClientActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public showChatClientActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			gameManager.showChatClient ();
		}
	}

	public class showRoundFrameActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public showRoundFrameActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			gameManager.showRoundFrame ();
		}
	}

	public class showAuditFrameActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public showAuditFrameActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			gameManager.showAuditFrame ();
		}
	}

	public class showActionReportFrameActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public showActionReportFrameActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			gameManager.showActionReportFrame ();
		}
	}

	public class userPreferencesActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		public userPreferencesActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			gameManager.showUserPreferencesFrame ();
		}
	}

	public class newActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public newActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			newGame ();
		}
	}

	public void newGame () {
		createPlayerInputFrame ();
		playerInputFrame.setVisible (true);
	}

	public void loadGame () {
		createPlayerInputFrame ();
		gameManager.loadSavedGame ();
	}

	public class openActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public openActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			loadGame ();
		}
	}

	public class closeActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public closeActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			System.out.println ("Close...");
		}
	}

	public class exitActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public exitActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			System.out.println ("EXITING THE APP");
			onExit ();
		}
	}

	public class saveActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public saveActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			gameManager.saveAGame (true);
		}
	}

	public class saveAsActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public saveAsActionClass (String text) {
			super (text);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			gameManager.saveAGame (false);
		}
	}

	public class saveConfigActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public saveConfigActionClass (String text) {
			super (text);
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			gameManager.saveConfig (false);
		}
	}

	private static void setupForMac () {
		boolean tIsMacOS = false;

		String tOSName = System.getProperty ("os.name");
		tIsMacOS = tOSName.contains ("Mac OS");

		if (tIsMacOS) {
			// These calls must come before any AWT or Swing code is called,
			// otherwise the Mac menu bar will use the class name as the application name.
			System.setProperty ("apple.laf.useScreenMenuBar", "true");
			System.setProperty ("com.apple.mrj.application.apple.menu.about.name", "GE18XX Test");
		}
	}

	public static void main (String aArgs[]) {
		setupForMac ();

		new Game_18XX ();
	}
}