package ge18xx.network;

import java.awt.Point;

import org.apache.logging.log4j.Logger;

import ge18xx.game.GameInfo;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.FileUtils;
import ge18xx.utilities.XMLElement;

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

	public JGameClient getNetworkJGameClient ();

	public int getSelectedGameIndex ();

	public void setSelectedGameIndex (int aGameIndex);

	public String getPlayersInOrder ();

	public void randomizePlayerOrder ();

	public void initiateNetworkGame ();

	public boolean gameStarted ();

	public Point getOffsetGEFrame ();

	public String getGameID ();

	public void resetGameID (String tGameID);

	public String getXMLBaseDirectory ();

	public void parseNetworkSavedGames (String tResponse);

	public String getClientUserName ();

	public void loadAutoSavedGame (String autoSaveFileName);

	public String getGEVersion ();

	public void updateDisconnectButton ();

	public FileUtils getFileUtils ();

	// Needs specific ge18xx XMLFrame Imports

	public void addNewFrame (XMLFrame jGameClient);

	// Needs Log4J Imports
	public Logger getLogger ();

	public XMLElement getGameVariantsXMLElement ();
	
	public GameInfo getSelectedGame ();
}
