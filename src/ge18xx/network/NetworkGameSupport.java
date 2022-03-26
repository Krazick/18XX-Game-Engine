package ge18xx.network;

import java.awt.Point;

import org.apache.logging.log4j.Logger;

import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.toplevel.XMLFrame;

public interface NetworkGameSupport {
	public final String NO_NAME = "";

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

	// Needs specific ge18xx Player, RoundManager and XMLFrame Imports
	
	public void addNewFrame (XMLFrame jGameClient);
	public Player getClientPlayer ();
	public RoundManager getRoundManager ();
 
	// Needs Log4J Imports
	public Logger getLogger ();
}
