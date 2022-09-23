package ge18xx.game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ge18xx.network.JGameClient;

public class SavedGame {
	String gameID;
	String gameStatus;
	int lastActionNumber;
	ArrayList<String> players;
	boolean localAutoSaveFound;
	String gameName;

	private final static String GAME_ID = "(\\d\\d\\d\\d-\\d\\d-\\d\\d-\\d\\d\\d\\d)";
	private final static String NSG_WITH_GAME_ID = "<NetworkSaveGame gameID=\"" + GAME_ID
			+ "\" status=\"(.*)\" lastActionNumber=\"(\\d+)\"/?>";
	private final static Pattern NSG_WITH_GAME_ID_PATTERN = Pattern.compile (NSG_WITH_GAME_ID);
	private final static String PLAYER_WITH_NAME = "<Player name=\"(.*)\" status=\"(.*)\"/?>";
	private final static Pattern PLAYER_WITH_NAME_PATTERN = Pattern.compile (PLAYER_WITH_NAME);
	private final static String GAME_WITH_GAME_ID = "<Game gameID=\"" + GAME_ID
			+ "\" lastActionNumber=\"(\\d+)\" players=\"(.*)\" status=\\\"(.*)\\\"/?>";
	private final static Pattern GAME_WITH_GAME_ID_PATTERN = Pattern.compile (GAME_WITH_GAME_ID);
	public final static ArrayList<String> NO_PLAYERS = null;
	public final static String NO_NAME = "NO_NAME";
	public final static String NO_STATUS = "NO_STATUS";
	public final static String TEST_FILE = "JunitTestFile";
	public final static int BAD_ACTION_NUMBER = -1;
	public final static SavedGame NO_SAVED_GAME = null;

	public SavedGame (String aFileName) throws FileNotFoundException {
		setupPlayers ();
		setLocalAutoSaveFound (false);
		if (aFileName != null) {
			readFile (aFileName);
		} else {
			throw (new FileNotFoundException ("Null File Name"));
		}
	}

	public SavedGame () {
		setupPlayers ();
		setupEmptyValues ();
	}

	private void setupPlayers () {
		players = new ArrayList<String> ();
	}

	private void readFile (String aFileName) {
		FileReader tFile;
		BufferedReader tReader;
		String tLine, tGameID, tPlayerName, tGameStatus;
		int tLastActionNumber;

		if (!TEST_FILE.equals (aFileName)) {
			try {
				tFile = new FileReader (aFileName);
				tReader = new BufferedReader (tFile);
				while ((tLine = tReader.readLine ()) != null) {
					tGameID = getGameIDFromLine (tLine);
					if (tGameID.equals (JGameClient.NO_GAME_ID)) {
						tPlayerName = getPlayerNameFromLine (tLine);
						if (!tPlayerName.equals (NO_NAME)) {
							addPlayer (tPlayerName);
						}
					} else {
						setGameID (tGameID);
						tGameStatus = getGameStatusFromLine (tLine);
						setGameStatus (tGameStatus);
						tLastActionNumber = getLastActionNumberFromLine (tLine);
						setLastActionNumber (tLastActionNumber);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace ();
			} catch (IOException e) {
				e.printStackTrace ();
			}
		} else {
			setupEmptyValues ();
		}
	}

	private void setupEmptyValues () {
		setGameID ("");
		setLastActionNumber (0);
		setGameStatus ("");
		setLocalAutoSaveFound (false);
	}

	public void setLocalAutoSaveFound (boolean aLocalFound) {
		localAutoSaveFound = aLocalFound;
	}

	public boolean localAutoSaveFound () {
		return localAutoSaveFound;
	}

	public void setGameName (String aGameName) {
		gameName = aGameName;
	}

	public String getGameName () {
		return gameName;
	}

	public void setPlayers (String aPlayerList) {
		String [] tPlayerNames = aPlayerList.split (",");

		for (String tPlayerName : tPlayerNames) {
			addPlayer (tPlayerName);
		}
	}

	public void addPlayer (String tPlayerName) {
		players.add (tPlayerName);
	}

	public void setGameID (String aGameID) {
		gameID = aGameID;
	}

	public String getGameID () {
		return gameID;
	}

	public void setGameStatus (String aGameStatus) {
		gameStatus = aGameStatus;
	}

	public String getGameStatus () {
		return gameStatus;
	}

	public void setLastActionNumber (int aLastActionNumber) {
		lastActionNumber = aLastActionNumber;
	}

	public int getLastActionNumber () {
		return lastActionNumber;
	}

	public int getPlayerCount () {
		return players.size ();
	}

	public int getLastActionNumberFromLine (String aRequest) {
		Matcher tMatcher = NSG_WITH_GAME_ID_PATTERN.matcher (aRequest);
		int tLastActionNumber = BAD_ACTION_NUMBER;
		String tLANText = "";

		if (tMatcher.find ()) {
			tLANText = tMatcher.group (3);
			tLastActionNumber = Integer.parseInt (tLANText);
		}

		return tLastActionNumber;
	}

	public String getGameIDFromLine (String aRequest) {
		Matcher tMatcher = NSG_WITH_GAME_ID_PATTERN.matcher (aRequest);
		String tGameID = JGameClient.NO_GAME_ID;

		if (tMatcher.find ()) {
			tGameID = tMatcher.group (1);
		}

		return tGameID;
	}

	public String getGameStatusFromLine (String aRequest) {
		Matcher tMatcher = NSG_WITH_GAME_ID_PATTERN.matcher (aRequest);
		String tGameStatus = NO_STATUS;

		if (tMatcher.find ()) {
			tGameStatus = tMatcher.group (2);
		}

		return tGameStatus;
	}

	public String getPlayerNameFromLine (String aRequest) {
		Matcher tMatcher = PLAYER_WITH_NAME_PATTERN.matcher (aRequest);
		String tPlayerName = NO_NAME;

		if (tMatcher.find ()) {
			tPlayerName = tMatcher.group (1);
		}

		return tPlayerName;
	}

	public String getPlayers () {
		String tPlayers;

		tPlayers = "";
		if (players.size () > 0) {
			for (String tPlayer : players) {
				if (tPlayers.length () > 0) {
					tPlayers += ", ";
				}
				tPlayers += tPlayer;
			}
		}

		return tPlayers;
	}

	public boolean hasPlayer (String aPlayerName) {
		boolean tHasPlayer = false;

		if (players.size () > 0) {
			for (String tPlayer : players) {
				if (tPlayer.equals (aPlayerName)) {
					tHasPlayer = true;
				}
			}
		}

		return tHasPlayer;
	}

	public static SavedGame parseSavedGameXML (String aSavedGameXML) {
		Matcher tMatcher = GAME_WITH_GAME_ID_PATTERN.matcher (aSavedGameXML);
		String tGameID = JGameClient.NO_GAME_ID;
		String tLastActionNumberString;
		int tLastActionNumber;
		String aPlayerList;
		String tStatus;
		SavedGame tSavedGame = new SavedGame ();

		if (tMatcher.find ()) {
			tGameID = tMatcher.group (1);
			tLastActionNumberString = tMatcher.group (2);
			tLastActionNumber = Integer.parseInt (tLastActionNumberString);
			aPlayerList = tMatcher.group (3);
			tStatus = tMatcher.group (4);
			tSavedGame.setGameID (tGameID);
			tSavedGame.setLastActionNumber (tLastActionNumber);
			tSavedGame.setPlayers (aPlayerList);
			tSavedGame.setGameStatus (tStatus);
		}

		return tSavedGame;
	}

	public String getSavedGameXML () {
		String tSavedGameXML = "";
		String tPlayers;

		tPlayers = getPlayers ();
		tSavedGameXML = "<Game gameID=\"" + gameID + "\" lastActionNumber=\"" + lastActionNumber + "\" players=\""
				+ tPlayers + "\" status=\"" + gameStatus + "\">";

		return tSavedGameXML;
	}
}
