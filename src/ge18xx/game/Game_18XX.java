package ge18xx.game;

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
import ge18xx.toplevel.PrefPane;
import ge18xx.utilities.Sound;
import log4j2.plugins.LoggerLookup;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Game_18XX extends JFrame {
	private static final long serialVersionUID = 1L;
	private final String ENTER_USER_NAME = "Must Enter User Name";
	private final String NO_TOOL_TIP = "";
	protected ResourceBundle resbundle;
	protected AboutBox aboutBox;
	protected PrefPane prefs;
	protected Action newAction, openAction, closeAction, saveAction, saveAsAction, saveConfigAction, 
					 frameInfoAction, exitAction,
					 undoAction, cutAction, copyAction, pasteAction, clearAction, selectAllAction;
	protected Action selectGameAction, showMapAction, showMarketAction, showCitiesAction, showPrivatesAction;
	protected Action showTileTrayAction, showCoalCompaniesAction, showMinorCompaniesAction;
	protected Action showChatClientAction, showRoundFrameAction, showShareCompaniesAction;
	protected Action showPlayerInputAction, showAuditFrameAction, showActionReportFrameAction;
	static final JMenuBar mainMenuBar = new JMenuBar ();	
	protected JMenu fileMenu, gameMenu;

	GameManager gameManager;
	PlayerInputFrame playerInputFrame;
	JMenuItem gameMenuItems [];
	JMenuItem newMenuItem;
	JMenuItem openMenuItem;
	JMenuItem closeMenuItem;
	JMenuItem saveMenuItem;
	JMenuItem saveAsMenuItem;
	JMenuItem saveConfigMenuItem;
	JMenuItem frameInfoMenuItem;
	JMenuItem exitMenuItem;
	private String OK_TEXT = "OK";
	private String QUIT_TEXT = "Quit";
	private JTextField clientUserName;
	private JButton newGameButton;
//	private JButton loadGameButton;
	private JButton tQuitButton;
	private static Logger logger;
	LoggerLookup loggerLookup;
	String userDir = System.getProperty ("user.dir");

	public Game_18XX () {
		this (true);
	}
	
	public Game_18XX (boolean aVisible) {
		super ("");
		String tTitle;
		
		// The ResourceBundle below contains all of the strings used in this
		// application.  ResourceBundles are useful for localizing applications.
		// New localities can be added by adding additional properties files.
		resbundle = ResourceBundle.getBundle ("ge18xx.game.MyResources", Locale.getDefault ());
		
		tTitle = resbundle.getString ("frameTitle");
		setTitle (tTitle);
		setApplicationIcon ();
		
		createActions ();
		addMenus ();
		
		setSize (385, 260);
		setLocation (100, 100);
		setFrameContents ();
		setupFrameActions ();
		toFront ();
		setVisible (aVisible);
		
		playWhistle();
	}

	private void playWhistle () {
		Sound tSound;
		
		tSound = new Sound ();
		tSound.playSoundClip (tSound.WHISTLE);
	}
	
	public void setupLogger (String aUserName) {
		String tXMLConfigFile;
	    String tJavaVersion = System.getProperty ("java.version");
	    String tOSName = System.getProperty ("os.name");
	    String tOSVersion = System.getProperty( "os.version");
	    
		LoggerLookup.setUserName (aUserName);
	    tXMLConfigFile = "18XX XML Data" + File.separator + "log4j2.xml";
		System.setProperty ("log4j.configurationFile", tXMLConfigFile);
		logger = LogManager.getLogger (Game_18XX.class);
		logger.info ("Game Engine 18XX, Version " + getGEVersion () + 
					" Client " + aUserName);
		logger.info ("Java Version " + tJavaVersion + 
					" OS Name " + tOSName + " OS Version " + tOSVersion);
	}
	
	public String getUserDir () {
		return userDir;
	}
	
	public static Logger getLogger () {
		return logger;
	}
	
	private void setApplicationIcon () {
		// This will set the GE18XX Frame Icon (when it is minimized)
		// For Mac on the Doc even, but not the Application Level Icon.
		Image image = Toolkit.getDefaultToolkit ().getImage ("images/GE18XX.png");
		this.setIconImage (image);
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
	    
	    if (! tDirectory.exists ()){
	    	tDirectory.mkdir ();
	    }
	}

	private void setupNewGameManager () {
		String tCUNText = clientUserName.getText ();

		setupAutoSavesAndLogDirectory ();
		if (NetworkPlayer.validPlayerName (tCUNText)) {
			setupLogger (tCUNText);
			setGameManager (new GameManager (this, tCUNText));
			enableGameStartItems ();
			newGameButton.requestFocusInWindow ();
		} else if (! (tCUNText.equals (""))) {
			clientUserName.setText ("INVALID NAME");
			clientUserName.requestFocusInWindow ();
		}		
	}
	
	private void setupFrameActions () {
		clientUserName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent aEvent) {
				Object tEventObject = aEvent.getSource ();
				
				if (tEventObject instanceof JTextField) {
					setupNewGameManager ();
				}
			}
		});
		
		clientUserName.addKeyListener (new KeyAdapter() {
			@Override
			public void keyReleased (KeyEvent e) {
				enableGameStartItems ();
			}
		});
	
		newGameButton.addActionListener (new ActionListener() {
			public void actionPerformed (ActionEvent aEvent) {
				newGame ();
			}
		});
		
		newGameButton.addKeyListener (new KeyAdapter() {
			@Override
			public void keyReleased (KeyEvent e) {
				if (e.getKeyCode () == KeyEvent.VK_ENTER){
					newGame ();
				 }
			}
		});
		
//		loadGameButton.addActionListener (new ActionListener() {
//			public void actionPerformed (ActionEvent aEvent) {
//				loadGame ();
//			}
//		});
//		
//		loadGameButton.addKeyListener (new KeyAdapter() {
//			@Override
//			public void keyReleased (KeyEvent e) {
//				if (e.getKeyCode () == KeyEvent.VK_ENTER){
//					loadGame ();
//				 }
//			}
//		});
		
		tQuitButton.addActionListener (new ActionListener() {
			public void actionPerformed (ActionEvent aEvent) {
				System.exit (0);
			}
		});
	}
	
	public String getGEVersion () {
		return resbundle.getString ("version");
	}
	
	private void setFrameContents () {
		JLabel tGameEngineTitle = new JLabel ("Game Engine Title");
		tGameEngineTitle.setText (resbundle.getString ("message"));
		tGameEngineTitle.setFont (new Font ("Lucida Grande", Font.BOLD, 24));
		tGameEngineTitle.setHorizontalAlignment (SwingConstants.CENTER);
		
		JLabel tGameEngineVersion = new JLabel ("Version: X.X");
		tGameEngineVersion.setText (getGEVersion ());
		tGameEngineVersion.setHorizontalAlignment (SwingConstants.CENTER);
		tGameEngineVersion.setFont (new Font ("Lucida Grande", Font.PLAIN, 20));
		
		JLabel tClientLabel = new JLabel ("Client User Name:");
		
		clientUserName = new JTextField ();
		clientUserName.setColumns (10);
		clientUserName.setEnabled (true);
		
		newGameButton = new JButton (OK_TEXT);
//		loadGameButton = new JButton ("Load Game...");
		tQuitButton = new JButton(QUIT_TEXT);
		
		GroupLayout groupLayout = new GroupLayout (getContentPane ());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(114)
							.addComponent(tGameEngineVersion))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(55)
							.addComponent(tGameEngineTitle))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(57)
							.addComponent(tClientLabel)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(clientUserName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(60)
							.addComponent(newGameButton)
							.addGap(100)
//							.addComponent(loadGameButton)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(tQuitButton)))
					.addContainerGap(11, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(34)
					.addComponent(tGameEngineTitle)
					.addGap(18)
					.addComponent(tGameEngineVersion)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(tClientLabel)
						.addComponent(clientUserName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(newGameButton)
//						.addComponent(loadGameButton)
						.addComponent(tQuitButton))
					.addContainerGap(12, Short.MAX_VALUE))
		);
		disableGameButtons ();
		getContentPane ().setLayout (groupLayout);
	}

	public void paint (Graphics g) {
		super.paint (g);
	}

	/*
	public void about(ApplicationEvent e) {
		aboutBox.setResizable(false);
		aboutBox.setVisible(true);
	}

	public void preferences(ApplicationEvent e) {
		prefs.setResizable(false);
		prefs.setVisible(true);
	}

	public void quit(ApplicationEvent e) {	
		System.exit(0);
	}
*/
	
	public void addMenus() {
		int tMenuItemCount, tMenuItemIndex;
		int tMenuIndex;
		
		fileMenu = new JMenu("File");
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
		frameInfoMenuItem = new JMenuItem (frameInfoAction);
		disableFrameInfoMenuItem ();
		fileMenu.add (frameInfoMenuItem);
		exitMenuItem = new JMenuItem (exitAction);
		fileMenu.add (exitMenuItem);
		mainMenuBar.add (fileMenu);

		gameMenu = new JMenu ("Game");
		tMenuItemCount = 10;
		gameMenuItems = new JMenuItem [tMenuItemCount];
		tMenuIndex = 0;
		tMenuIndex = addGameMenu (tMenuIndex, showMapAction);
		tMenuIndex = addGameMenu (tMenuIndex, showMarketAction);
		tMenuIndex = addGameMenu (tMenuIndex, showCitiesAction);
		tMenuIndex = addGameMenu (tMenuIndex, showPrivatesAction);
//		tMenuIndex = addGameMenu (tMenuIndex, showCoalCompaniesAction);
//		tMenuIndex = addGameMenu (tMenuIndex, showMinorCompaniesAction);
		tMenuIndex = addGameMenu (tMenuIndex, showShareCompaniesAction);
		tMenuIndex = addGameMenu (tMenuIndex, showTileTrayAction);
		tMenuIndex = addGameMenu (tMenuIndex, showChatClientAction);
		tMenuIndex = addGameMenu (tMenuIndex, showRoundFrameAction);
		tMenuIndex = addGameMenu (tMenuIndex, showAuditFrameAction);
		tMenuIndex = addGameMenu (tMenuIndex, showActionReportFrameAction);
//		gameMenuItems [0] = new JMenuItem (showMapAction);
//		gameMenuItems [1] = new JMenuItem (showMarketAction);
//		gameMenuItems [2] = new JMenuItem (showCitiesAction);
//		gameMenuItems [3] = new JMenuItem (showPrivatesAction);
//		gameMenuItems [4] = new JMenuItem (showCoalCompaniesAction);
//		gameMenuItems [5] = new JMenuItem (showMinorCompaniesAction);
//		gameMenuItems [6] = new JMenuItem (showShareCompaniesAction);
//		gameMenuItems [7] = new JMenuItem (showTileTrayAction);
//		gameMenuItems [8] = new JMenuItem (showChatClientAction);
//		gameMenuItems [9] = new JMenuItem (showRoundFrameAction);
//		gameMenuItems [10] = new JMenuItem (showAuditFrameAction);
//		gameMenuItems [11] = new JMenuItem (showActionReportFrameAction);
		
		for (tMenuItemIndex = 0; tMenuItemIndex < tMenuItemCount; tMenuItemIndex++) {
			gameMenuItems [tMenuItemIndex].setEnabled (false);
			gameMenu.add (gameMenuItems [tMenuItemIndex]);
		}
		mainMenuBar.add (gameMenu);
		
		setJMenuBar (mainMenuBar);
	}
	
	private int addGameMenu (int aMenuIndex, Action aMenuAction) {
		gameMenuItems [aMenuIndex] = new JMenuItem (aMenuAction);
		
		return (aMenuIndex + 1);
	}
	
	public void createActions () {
		int shortcutKeyMask = Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask ();

		//Create actions that can be used by menus, buttons, toolbars, etc.
		newAction = new newActionClass (resbundle.getString ("newItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_N, shortcutKeyMask));
		openAction = new openActionClass (resbundle.getString ("openItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_O, shortcutKeyMask));
		closeAction = new closeActionClass (resbundle.getString ("closeItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_W, shortcutKeyMask));
		saveAction = new saveActionClass (resbundle.getString ("saveItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_S, shortcutKeyMask));
		saveAsAction = new saveAsActionClass (resbundle.getString ("saveAsItem"));
		saveConfigAction = new saveConfigActionClass (resbundle.getString ("saveConfigItem"));
		frameInfoAction = new frameInfoActionClass (resbundle.getString ("frameInfoItem"));
		exitAction = new exitActionClass (resbundle.getString ("exitItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_Q, shortcutKeyMask));
		
		showMapAction = new showMapActionClass (resbundle.getString("showMapItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_M, shortcutKeyMask));
		showMarketAction = new showMarketActionClass (resbundle.getString ("showMarketItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_K, shortcutKeyMask));
		showCitiesAction = new showCitiesActionClass (resbundle.getString ("showCitiesItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_L, shortcutKeyMask));
		showTileTrayAction = new showTileTrayActionClass (resbundle.getString ("showTileTrayItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_T, shortcutKeyMask));
		showPrivatesAction = new showPrivatesActionClass (resbundle.getString ("showPrivatesItem"),
				KeyStroke.getKeyStroke (KeyEvent.VK_P, shortcutKeyMask));
//		showCoalCompaniesAction = new showMinorsActionClass (resbundle.getString ("showCoalsItem"), null);
//		showMinorCompaniesAction = new showMinorsActionClass (resbundle.getString ("showMinorsItem"), null);
		showShareCompaniesAction = new showShareCompaniesActionClass (resbundle.getString ("showShareCompaniesItem"), null);
		showChatClientAction = new showChatClientActionClass (resbundle.getString ("showChatClientItem"), null);
		showRoundFrameAction = new showRoundFrameActionClass (resbundle.getString ("showRoundFrameItem"), null);
		showAuditFrameAction = new showAuditFrameActionClass (resbundle.getString ("showAuditFrameItem"), null);
		showActionReportFrameAction = new showActionReportFrameActionClass (resbundle.getString ("showActionReportFrameItem"), null);
	}
	
	public void createGameSet () {
		if (loadGameSet ()) {
			playerInputFrame.addGameInfo ();
		}
	}

	public void createPlayerInputFrame () {
		Point tNewPoint;
		
		if (playerInputFrame == null) {
			playerInputFrame = new PlayerInputFrame ("Enter Player Information", gameManager);
			playerInputFrame.setParentFrame (this);
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
	
	public void disableFrameInfoMenuItem () {
		frameInfoMenuItem.setEnabled (false);
	}
	
	public void disableGameButtons () {
		newGameButton.setEnabled (false);
		newGameButton.setToolTipText (ENTER_USER_NAME);
//		loadGameButton.setEnabled (false);
//		loadGameButton.setToolTipText (ENTER_USER_NAME);
	}
	
	public void disableGameStartItems () {
		disableNewMenuItem ();
		disableOpenMenuItem ();
		disableGameButtons ();
		clientUserName.setEnabled (false);
	}
	
	public void enableGameStartItems () {
		enableNewMenuItem ();
		enableOpenMenuItem ();
		newGameButton.setEnabled (true);
		newGameButton.setToolTipText (NO_TOOL_TIP);
//		loadGameButton.setEnabled (true);
//		loadGameButton.setToolTipText (NO_TOOL_TIP);
		clientUserName.setEnabled (true);
	}
	
	public void enableGameMenuItems () {
		int tMenuItemIndex, tMenuItemCount;
		String tCoalMenuText = resbundle.getString ("showCoalsItem");
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
			} else if (tMenuText.equals (tCoalMenuText)) {
				if (gameManager.getCountOfCoals () > 0) {
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

	public void enableFrameInfoMenuItem () {
		frameInfoMenuItem.setEnabled (true);
	}

	public void enableSaveMenuItems () {
		enableSaveMenuItem ();
		enableSaveAsMenuItem ();
		enableSaveConfigMenuItem ();
		enableFrameInfoMenuItem ();
	}
	
	public void initiateGame () {
		disableGameStartItems ();
		enableGameMenuItems ();
		enableSaveMenuItems ();
	}
	
	public boolean loadGameSet () {
		String tFileName = gameManager.getXMLBaseDirectory () + "18xx Games.xml";
		boolean tLoadedGameSet;
		
		try {
			createPlayerInputFrame ();
			playerInputFrame.loadXML (tFileName, playerInputFrame.getGameSet ());
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
		
		public void actionPerformed (ActionEvent e) {
			gameManager.showActionReportFrame ();
		}
	}
	
	public class newActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		public newActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}
		
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
		public void actionPerformed (ActionEvent e) {
			System.exit (0);
		}
	}
	
	public class saveActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public saveActionClass (String text, KeyStroke shortcut) {
			super (text);
			putValue (ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed (ActionEvent e) {
			gameManager.saveAGame (true);
		}
	}
	
	public class saveAsActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public saveAsActionClass (String text) {
			super (text);
		}
		public void actionPerformed (ActionEvent e) {
			gameManager.saveAGame (false);
		}
	}
	
	public class saveConfigActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public saveConfigActionClass (String text) {
			super (text);
		}
		public void actionPerformed (ActionEvent e) {
			gameManager.saveConfig (false);
		}
	}
	
	public class frameInfoActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public frameInfoActionClass (String text) {
			super (text);
		}
		public void actionPerformed (ActionEvent e) {
			gameManager.showFrameInfo ();
		}
	}
	
	 public static void main (String aArgs []) {
		new Game_18XX ();
	 }
}