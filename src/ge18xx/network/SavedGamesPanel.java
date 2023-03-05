package ge18xx.network;

import java.awt.LayoutManager;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ge18xx.game.SavedGame;
import ge18xx.game.SavedGames;
import ge18xx.utilities.FileUtils;
import ge18xx.utilities.GUI;

public class SavedGamesPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JList<String> savedGamesList;
	private DefaultListModel<String> savedGamesListModel;
	private JButton chooseGameButton;
	NetworkGameSupport gameManager;
	JGameClient jGameClient;
	
	public SavedGamesPanel (NetworkGameSupport aGameManager, JGameClient aJGameClient, SavedGames aNetworkSavedGames) {
		super ();
		setGameManager (aGameManager);
		setJGameClient (aJGameClient);
		setChooseGameButton ();
		buildNetworkSGPanel (aNetworkSavedGames);
	}

	public SavedGamesPanel (LayoutManager layout, NetworkGameSupport aGameManager, JGameClient aJGameClient, SavedGames aNetworkSavedGames) {
		super (layout);
		setGameManager (aGameManager);
		setJGameClient (aJGameClient);
		setChooseGameButton ();
		buildNetworkSGPanel (aNetworkSavedGames);
	}

	public SavedGamesPanel (boolean isDoubleBuffered, NetworkGameSupport aGameManager, JGameClient aJGameClient, SavedGames aNetworkSavedGames) {
		super (isDoubleBuffered);
		setGameManager (aGameManager);
		setJGameClient (aJGameClient);
		setChooseGameButton ();
		buildNetworkSGPanel (aNetworkSavedGames);
	}

	public SavedGamesPanel (LayoutManager layout, boolean isDoubleBuffered, NetworkGameSupport aGameManager, JGameClient aJGameClient, SavedGames aNetworkSavedGames) {
		super (layout, isDoubleBuffered);
		setGameManager (aGameManager);
		setJGameClient (aJGameClient);
		setChooseGameButton ();
		buildNetworkSGPanel (aNetworkSavedGames);
	}

	public void setGameManager (NetworkGameSupport aGameManager) {
		gameManager = aGameManager;
	}
	
	public void setJGameClient (JGameClient aJGameClient) {
		jGameClient = aJGameClient;
	}
	
	public void setChooseGameButton () {
		chooseGameButton = jGameClient.getChooseGameButton ();
	}
	
	public void buildNetworkSGPanel (SavedGames aNetworkSavedGames) {
		JLabel tPanelTitle;
		int tSavedGameCount;
		String tTitle;
		Box tNetworkSavedGamesBox;
		
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
			chooseGameButton.setToolTipText ("Show " + tTitle);
			updateShowSavedGamesButton ();
		} else {
			chooseGameButton.setEnabled (false);
			chooseGameButton.setToolTipText (tTitle);
		}

		add (tNetworkSavedGamesBox);
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
		savedGamesList = new JList<String> (savedGamesListModel);
		savedGamesList.setSelectionMode (ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		savedGamesList.setLayoutOrientation (JList.VERTICAL);
		savedGamesList.addListSelectionListener (new ListSelectionListener () {
			@Override
			public void valueChanged (ListSelectionEvent aEvent) {
				savedGameSelected (aEvent);
			}
		});
		

	}

	private void updateShowSavedGamesButton () {
		chooseGameButton.setEnabled (true);
		chooseGameButton.setToolTipText (GUI.NO_TOOL_TIP);
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

	private void savedGameSelected (ListSelectionEvent aEvent) {
		String tSelectedGame;
		String [] tSelectedParts;
		String tNewSaveGameFile;
		FileUtils tFileUtils;
		String tAutoSaveFileName;

		tFileUtils = gameManager.getFileUtils ();
		tSelectedGame = savedGamesList.getSelectedValue ();
		tSelectedParts = tSelectedGame.split (" : ");
		tNewSaveGameFile = tSelectedParts [1] + "." + tSelectedParts [2] + "." + gameManager.getClientUserName ()
				+ ".save" + tFileUtils.xml;
		tAutoSaveFileName = jGameClient.getAutoSaveFileName ();
		if (!tNewSaveGameFile.equals (tAutoSaveFileName)) {
//			System.out.println ("Selected Saved Game is [" + tSelectedGame + "]");
//			System.out.println (" Part 0 [" + tSelectedParts [0] + "]" + " Part 1 [" + tSelectedParts [1] + "]"
//					+ " Part 2 [" + tSelectedParts [2] + "]" + " Part 3 [" + tSelectedParts [3] + "]");
//			System.out.println ("New Auto Save File Name: " + tNewSaveGameFile);
			jGameClient.setAutoSaveFileName (tNewSaveGameFile);
			jGameClient.updateReadyButton (JGameClient.PLAY_GAME, true, GUI.NO_TOOL_TIP);
		}
	}

}
