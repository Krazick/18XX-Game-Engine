package ge18xx.game;

import java.util.ArrayList;

public class SavedGames {
	private ArrayList<SavedGame> games;
	public static int NO_INDEX = -1;
	
	public SavedGames (String aSavedGamesXML) {
		String tSavedGamesXML;
		String [] tSavedGamesParsed;
		
		games = new ArrayList<SavedGame> ();
		tSavedGamesXML = aSavedGamesXML.replaceAll ("><", ">\r<");
		tSavedGamesParsed = tSavedGamesXML.split ("\r");
		for (String tLine : tSavedGamesParsed) {
			if (tLine.startsWith("<Game ")) {
				addSavedGame (tLine);
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
	
	public String getGameIDat (int aIndex) {
		SavedGame tSavedGame;
		String tGameIDat = SavedGame.NO_GAME_ID;
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
			if (! aGameID.equals (SavedGame.NO_GAME_ID)) {
				if (getSavedGameCount () > 0) {
					for (SavedGame tSavedGame : games) {
						if (! tHasGameID) {
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
			if (! aGameID.equals (SavedGame.NO_GAME_ID)) {
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
			if ((aIndex >=0) && (aIndex < tGameCount)) {
				tSavedGame = games.get (aIndex);
			}
		}
		
		return tSavedGame;
	}
}
