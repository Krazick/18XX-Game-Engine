package ge18xx.game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName ("GE18XX Saved Games Tests")
class SavedGamesTests {
	SavedGames savedGames;
	SavedGames noSavedGames;
	
	@BeforeEach
	void setUp() throws Exception {
		String tEmptyXMLData = "";
		String tXMLData = "<SavedGames name=\"Mark\">" +
				"<Game gameID=\"2021-04-08-1923\" lastActionNumber=\"100\" players=\"Dave, Mark\" status=\"ACTIVE\">" + 
				"<Game gameID=\"2021-04-09-1541\" lastActionNumber=\"105\" players=\"Mark, Dave\" status=\"ACTIVE\">" + 
				"<Game gameID=\"2021-04-07-1748\" lastActionNumber=\"101\" players=\"Mark, Jeff\" status=\"ACTIVE\">" + 
				"<Game gameID=\"2021-04-13-1214\" lastActionNumber=\"105\" players=\"Dave, Mark\" status=\"ACTIVE\">" + 
				"</SavedGames>";

		savedGames = new SavedGames (tXMLData);
		noSavedGames = new SavedGames (tEmptyXMLData);
	}

	@Test
	@DisplayName ("Constructor and getting Count")
	void SavedGameConstructorTest () {
		assertEquals (4, savedGames.getSavedGameCount ());
	}

	@Nested
	@DisplayName ("Getting GameID at an Index")
	class SavedGameGetGameIDTests {
	
		@Test
		@DisplayName ("With no Saved Games")
		void SavedGameGetGameIDatNoGamesTests () {
			assertEquals (0, noSavedGames.getSavedGameCount ());
			
			assertEquals ("NOID", noSavedGames.getGameIDat (0));
			assertEquals ("NOID", noSavedGames.getGameIDat (1));
			assertEquals ("NOID", noSavedGames.getGameIDat (-31));
		}
		
		@Test
		@DisplayName ("With 4 Saved Games")
		void SaveGameGetGameIDatTests () {
			assertEquals (4, savedGames.getSavedGameCount ());
			
			assertEquals ("2021-04-08-1923", savedGames.getGameIDat (0));
			assertEquals ("2021-04-09-1541", savedGames.getGameIDat (1));
			assertEquals ("NOID", savedGames.getGameIDat (6));
			assertEquals ("NOID", savedGames.getGameIDat (-56));
		}
	}
	
	@Nested
	@DisplayName ("SavedGames seeking specific GameID")
	class SaveGameSpecificGameIDTests {
		@Test
		@DisplayName ("SavedGames has specific GameID")
		void SavedGamesHasGameIDTest ( ) {
			assertTrue (savedGames.hasGameID ("2021-04-08-1923"));
			assertTrue (savedGames.hasGameID ("2021-04-13-1214"));
			assertFalse (savedGames.hasGameID (null));
			assertFalse (savedGames.hasGameID (""));
			assertFalse (savedGames.hasGameID ("NOID"));
			assertFalse (savedGames.hasGameID ("2021-04-13-1215"));
			assertFalse (noSavedGames.hasGameID("021-04-08-1923"));
		}
		
		@Test
		@DisplayName ("SavedGames getting Index for specific GameID")
		void SavedGamesGetIndexForGameIDTest ( ) {
			assertEquals (0, savedGames.getIndexForGameID ("2021-04-08-1923"));
			assertEquals (3, savedGames.getIndexForGameID ("2021-04-13-1214"));
			assertEquals (-1, savedGames.getIndexForGameID (null));
			assertEquals (-1, savedGames.getIndexForGameID (""));
			assertEquals (-1, savedGames.getIndexForGameID ("NOID"));
			assertEquals (-1, savedGames.getIndexForGameID ("2021-04-13-1215"));
			assertEquals (-1, noSavedGames.getIndexForGameID("021-04-08-1923"));
		}
	}
	
	@Test
	@DisplayName ("Retrieve Specific SavedGame At")
	void GetSavedGameAtTest () {
		SavedGame tSavedGame;
		SavedGame tBadSavedGame;
		
		tSavedGame = savedGames.getSavedGameAt (3);
		assertEquals ("2021-04-13-1214", tSavedGame.getGameID ());
		tBadSavedGame = savedGames.getSavedGameAt (12);
		assertNull (tBadSavedGame);
		tBadSavedGame = savedGames.getSavedGameAt (-3);
		assertNull (tBadSavedGame);
		tBadSavedGame = noSavedGames.getSavedGameAt (0);
		assertNull (tBadSavedGame);
	}
}
