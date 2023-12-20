package ge18xx.network;

import java.awt.Point;

import org.apache.logging.log4j.Logger;

//import ge18xx.game.GameInfo;
//import ge18xx.game.GameSet;
//import ge18xx.toplevel.PlayerInputFrame;
import geUtilities.xml.XMLFrame;
import geUtilities.FileUtils;
import geUtilities.XMLElement;

public interface NetworkGameSupport {
	public final String NO_NAME = "";
	public static final String NO_GAME_ID = "NOID";
	public static final String EMPTY_GAME_ID = "";
	public final static int WAIT_TIME = 10;

	public void updatePlayerCountLabel ();
	public void addNetworkPlayer (String aPlayerName);
	public void removeNetworkPlayer (String aPlayerName);
	public void removeAllNetworkPlayers ();
	public void handleGameActivity (String aGameActivity);
	public String getPlayersInOrder ();
	public void randomizePlayerOrder ();
	public void initiateNetworkGame ();
	public boolean gameStarted ();
	public Point getOffsetGEFrame ();
	public String getClientUserName ();
	public void parseNetworkSavedGames (String aResponse);
	public void loadAutoSavedGame (String aAutoSaveFileName);
	public void updateDisconnectButton ();
	public FileUtils getFileUtils ();
	
	public int getSelectedGameIndex ();
	public void setSelectedGameIndex (int aGameIndex);

	public void addNewFrame (XMLFrame aJGameClient);

	// Needs Log4J Imports
	public Logger getLogger ();
	public String getXMLBaseDirectory ();
	public XMLElement getGameVariantsXMLElement ();
//	public GameInfo getSelectedGame ();
	
	public void setGameID (String aGameID);
	public String getGameID ();
	public void resetGameID (String aGameID);

	public String getGEVersion ();
	public String getEnvironmentVersionInfo ();

//	public JGameClient getNetworkJGameClient ();
//	public PlayerInputFrame getPlayerInputFrame ();
//	public GameSet getGameSet ();
}
