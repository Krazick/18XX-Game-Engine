package ge18xx.game;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import ge18xx.network.JGameClient;

public class SavedGames implements Comparator<SavedGame> {
	public static int NO_INDEX = -1;
	private ArrayList<SavedGame> games;
	GameManager gameManager;

	public SavedGames (String aSavedGamesXML, GameManager aGameManager) {
		String tSavedGamesXML;
		String [] tSavedGamesParsed;

		setGameManager (aGameManager);
		games = new ArrayList<SavedGame> ();
		tSavedGamesXML = aSavedGamesXML.replaceAll ("><", ">\r<");
		tSavedGamesParsed = tSavedGamesXML.split ("\r");
		for (String tLine : tSavedGamesParsed) {
			if (tLine.startsWith ("<Game ")) {
				addSavedGame (tLine);
			}
		}
		Collections.sort (games, this);
	}

	public void setGameManager (GameManager aGameManager) {
		gameManager = aGameManager;
	}

	public GameManager getGameManager () {
		return gameManager;
	}

	public void setAllLocalAutoSaveFound (String aAutoSaveDirectory) {
		File tAutoSavesNetDir;
		String [] tAutoSavesNetFiles;
		String [] tFileNameParts;
		String tSavedGameID;
		String tLocalPlayerName;

		if (aAutoSaveDirectory != null) {
			if (!aAutoSaveDirectory.equals ("")) {
				if (games.size () > 0) {
					tAutoSavesNetDir = new File (aAutoSaveDirectory);
					tAutoSavesNetFiles = tAutoSavesNetDir.list ();
					if (tAutoSavesNetFiles != null) {
						tLocalPlayerName = gameManager.getClientUserName ();
						for (SavedGame tSavedGame : games) {
							tSavedGameID = tSavedGame.getGameID ();

							for (String tFileName : tAutoSavesNetFiles) {
								if (tFileName.endsWith (".save18xx.xml")) {
									tFileNameParts = tFileName.split (Pattern.quote ("."));
									if (tSavedGameID.equals (tFileNameParts [1])) {
										if (tLocalPlayerName.equals (tFileNameParts [2])) {
											tSavedGame.setLocalAutoSaveFound (true);
											tSavedGame.setGameName (tFileNameParts [0]);
										}
									}
								}
							}

						}
					}
				}
			}
		}
	}

	public void addSavedGame (String aSavedGameXML) {
		SavedGame tSavedGame;

		tSavedGame = SavedGame.parseSavedGameXML (aSavedGameXML);
		games.add (tSavedGame);
	}

	public int getSavedGameCount () {
		return games.size ();
	}

	public boolean atLeastOneMatchedLocal () {
		boolean tFoundAtLeastOne = false;

		for (SavedGame tSavedGame : games) {
			if (tSavedGame.localAutoSaveFound ()) {
				tFoundAtLeastOne = true;
			}
		}

		return tFoundAtLeastOne;
	}

	public int getMatchedSavedGameCount () {
		int tMatchedCount = 0;

		for (SavedGame tSavedGame : games) {
			if (tSavedGame.localAutoSaveFound ()) {
				tMatchedCount++;
			}
		}

		return tMatchedCount;
	}

	public String getGameIDat (int aIndex) {
		SavedGame tSavedGame;
		String tGameIDat = JGameClient.NO_GAME_ID;
		int tGameCount;

		tGameCount = getSavedGameCount ();
		if (tGameCount > 0) {
			if ((aIndex >= 0) && (aIndex < tGameCount)) {
				tSavedGame = games.get (aIndex);
				tGameIDat = tSavedGame.getGameID ();
			}
		}

		return tGameIDat;
	}

	public boolean hasGameID (String aGameID) {
		boolean tHasGameID = false;
		String tFoundGameID;

		if (aGameID != null) {
			if (!aGameID.equals (JGameClient.NO_GAME_ID)) {
				if (getSavedGameCount () > 0) {
					for (SavedGame tSavedGame : games) {
						if (!tHasGameID) {
							tFoundGameID = tSavedGame.getGameID ();
							if (tFoundGameID.equals (aGameID)) {
								tHasGameID = true;
							}
						}
					}
				}
			}
		}

		return tHasGameID;
	}

	public int getIndexForGameID (String aGameID) {
		int tIndexFor = NO_INDEX;
		String tFoundGameID;
		int tGameCount;
		SavedGame tSavedGame;

		if (aGameID != null) {
			if (!aGameID.equals (JGameClient.NO_GAME_ID)) {
				tGameCount = getSavedGameCount ();
				if (tGameCount > 0) {
					for (int tIndex = 0; tIndex < tGameCount; tIndex++) {
						if (tIndexFor == -1) {
							tSavedGame = games.get (tIndex);
							tFoundGameID = tSavedGame.getGameID ();
							if (tFoundGameID.equals (aGameID)) {
								tIndexFor = tIndex;
							}
						}
					}
				}
			}
		}

		return tIndexFor;
	}

	public SavedGame getSavedGameAt (int aIndex) {
		SavedGame tSavedGame = SavedGame.NO_SAVED_GAME;
		int tGameCount;

		tGameCount = getSavedGameCount ();
		if (tGameCount > 0) {
			if ((aIndex >= 0) && (aIndex < tGameCount)) {
				tSavedGame = games.get (aIndex);
			}
		}

		return tSavedGame;
	}

	@Override
	public int compare (SavedGame aSavedGame1, SavedGame aSavedGame2) {
		int tOrder;
		String tGameID1;
		String tGameID2;
		
		tGameID1 = aSavedGame1.getGameID ();
		tGameID2 = aSavedGame2.getGameID ();
		tOrder = tGameID2.compareTo (tGameID1);
		
		return tOrder;
	}
}
