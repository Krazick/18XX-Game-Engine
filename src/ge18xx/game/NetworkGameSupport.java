package ge18xx.game;

import java.awt.Point;

import ge18xx.network.JGameClient;
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
	public void addNewFrame (XMLFrame jGameClient);
	public Player getClientPlayer ();
	public RoundManager getRoundManager ();
	public Point getOffsetGEFrame ();
	public String getGameID ();
	public void resetGameID (String tGameID);
	public String getXMLBaseDirectory ();
	public void parseNetworkSavedGames (String tResponse);
	public String getClientUserName ();
	public void loadAutoSavedGame (String autoSaveFileName);
	public String getGEVersion ();
}
