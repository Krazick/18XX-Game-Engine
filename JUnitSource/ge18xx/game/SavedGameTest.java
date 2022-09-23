package ge18xx.game;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName ("GE18XX Saved Game Tests")
class SavedGameTest {
	SavedGame savedGame;

	@BeforeEach
	void setUp () {
		try {
			savedGame = new SavedGame (SavedGame.TEST_FILE);
		} catch (FileNotFoundException tException) {
			tException.printStackTrace ();
		}
	}

	@Test
	@DisplayName ("Saved Game XML Generation Test")
	void SaveGameXMLGenerationTest () {
		savedGame.setGameID ("2021-04-12-1315");
		savedGame.setGameStatus ("Active");
		savedGame.setLastActionNumber (101);
		savedGame.addPlayer ("SGPlayerAlpha");
		savedGame.addPlayer ("SGPlayerBeta");
		savedGame.addPlayer ("SGPlayerGamma");

		assertEquals (
				"<Game gameID=\"2021-04-12-1315\" lastActionNumber=\"101\" players=\"SGPlayerAlpha, SGPlayerBeta, SGPlayerGamma\" status=\"Active\">",
				savedGame.getSavedGameXML ());
	}

	@Nested
	@DisplayName ("Constructor and Setters Tests")
	class constuctorTests {
		@Test
		@DisplayName ("Basic Constructor Test")
		void SavedGameBasicConstructorTest () {
			assertEquals ("", savedGame.getGameID ());
			assertEquals ("", savedGame.getGameStatus ());
			assertEquals (0, savedGame.getLastActionNumber ());
			assertEquals (0, savedGame.getPlayerCount ());
		}

		@Test
		@DisplayName ("No Arguments Constructor Test")
		void SavedGameNoArgsConstructorTest () {
			SavedGame tSavedGame;

			tSavedGame = new SavedGame ();
			assertEquals ("", tSavedGame.getGameID ());
			assertEquals ("", tSavedGame.getGameStatus ());
			assertEquals (0, tSavedGame.getLastActionNumber ());
			assertEquals (0, tSavedGame.getPlayerCount ());
			assertFalse (tSavedGame.localAutoSaveFound ());
		}

		@Test
		@DisplayName ("Setup Players from List Test")
		void SavedGameSetViaPlayersListTest () {
			String tPlayerNameList = "SGPlayerAlpha,SGPlayerBeta,SGPlayerGamma";

			savedGame.setPlayers (tPlayerNameList);
			assertEquals (3, savedGame.getPlayerCount ());
			assertTrue (savedGame.hasPlayer ("SGPlayerBeta"));
			assertFalse (savedGame.hasPlayer ("SGPlayerDelta"));
			assertFalse (savedGame.localAutoSaveFound ());
		}

		@Test
		@DisplayName ("Setter Tests")
		void SavedGameSettersTest () {
			savedGame.setGameID ("2021-04-12-1313");
			savedGame.setGameStatus ("Prepared");
			savedGame.setLastActionNumber (100);
			savedGame.setGameName ("GameName Test Setter and Getter");

			assertEquals ("2021-04-12-1313", savedGame.getGameID ());
			assertEquals ("Prepared", savedGame.getGameStatus ());
			assertEquals (100, savedGame.getLastActionNumber ());
			assertEquals (0, savedGame.getPlayerCount ());
			assertEquals ("", savedGame.getPlayers ());
			assertFalse (savedGame.localAutoSaveFound ());
			assertEquals ("GameName Test Setter and Getter", savedGame.getGameName ());
		}

		@Test
		@DisplayName ("Add Players Tests")
		void SavedGameAddPlayersTest () {
			savedGame.setGameID ("2021-04-12-1315");
			savedGame.setGameStatus ("Active");
			savedGame.setLastActionNumber (101);
			savedGame.addPlayer ("SGPlayerAlpha");
			savedGame.addPlayer ("SGPlayerBeta");
			savedGame.addPlayer ("SGPlayerGamma");

			assertEquals ("2021-04-12-1315", savedGame.getGameID ());
			assertEquals ("Active", savedGame.getGameStatus ());
			assertEquals (101, savedGame.getLastActionNumber ());
			assertEquals (3, savedGame.getPlayerCount ());
			assertEquals ("SGPlayerAlpha, SGPlayerBeta, SGPlayerGamma", savedGame.getPlayers ());
		}
	}

	@Nested
	@DisplayName ("Parsing Data from input")
	class parsingDataFromInputLineTests {

		@Test
		@DisplayName ("Extracting the Last Action Number")
		void LastActionNumberExtractionTest () {
			String tNSG_XML_StringGood = "<NetworkSaveGame gameID=\"2021-04-12-1353\" status=\"Active\" lastActionNumber=\"103\">";
			String tNSG_XML_StringBad = "<NetworkSaveGame gameID=\"2021-04-12-1353\" status=\"Active\" lastActionNumber=\"ABLE\">";

			assertEquals (103, savedGame.getLastActionNumberFromLine (tNSG_XML_StringGood));
			assertEquals (-1, savedGame.getLastActionNumberFromLine (tNSG_XML_StringBad));
		}

		@Test
		@DisplayName ("Extracting the GameID")
		void GameIDExtractionTest () {
			String tNSG_XML_StringGood = "<NetworkSaveGame gameID=\"2021-04-12-1353\" status=\"Active\" lastActionNumber=\"103\">";
			String tNSG_XML_StringBad = "<NetworkSaveGame gameID=\"2021-04-12-1353333\" status=\"Active\" lastActionNumber=\"ABLE\">";

			assertEquals ("2021-04-12-1353", savedGame.getGameIDFromLine (tNSG_XML_StringGood));
			assertEquals ("NOID", savedGame.getGameIDFromLine (tNSG_XML_StringBad));
		}

		@Test
		@DisplayName ("Extracting the Game Status")
		void GameStatusExtractionTest () {
			String tNSG_XML_StringGood = "<NetworkSaveGame gameID=\"2021-04-12-1353\" status=\"Active\" lastActionNumber=\"103\">";
			String tNSG_XML_StringBad = "<NetworkSaveGame gameID=\"2021-04-12-1353\" gameStatus=\"Active\" lastActionNumber=\"ABLE\">";

			assertEquals ("Active", savedGame.getGameStatusFromLine (tNSG_XML_StringGood));
			assertEquals ("NO_STATUS", savedGame.getGameStatusFromLine (tNSG_XML_StringBad));
		}

		@Test
		@DisplayName ("Extracting the Player Name")
		void PlayerNameExtractionTest () {
			String tClient_XML_StringGood = "<Player name=\"SGTesterAlpha\" status=\"Active\">";
			String tClient_XML_StringBad = "<Player playerName=\"SGTesterBeta\" status=\"Active\">";

			assertEquals ("SGTesterAlpha", savedGame.getPlayerNameFromLine (tClient_XML_StringGood));
			assertEquals ("NO_NAME", savedGame.getPlayerNameFromLine (tClient_XML_StringBad));
		}

		@Test
		@DisplayName ("Extracting from XML Data Line")
		void ParseSavedGameXML () {
			SavedGame tSavedGame;
			SavedGame tSavedGameBad;
			String tSavedGameXML = "<Game gameID=\"2021-04-12-1353\" lastActionNumber=\"103\" players=\"SGPlayerAlpha,SGPlayerBeta,SGPlayerGamma\" status=\"Active\">";
			String tBadSavedGameXML = "<Game gameID=\"2021-AA-BBBB\" lastActionNumber=\"103\" players=\"SGPlayerAlpha,SGPlayerBeta,SGPlayerGamma\" status=\"Active\">";

			tSavedGame = SavedGame.parseSavedGameXML (tSavedGameXML);
			assertEquals (3, tSavedGame.getPlayerCount ());
			assertEquals ("2021-04-12-1353", tSavedGame.getGameID ());
			assertEquals (103, tSavedGame.getLastActionNumber ());

			tSavedGameBad = SavedGame.parseSavedGameXML (tBadSavedGameXML);
			assertEquals (0, tSavedGameBad.getPlayerCount ());
			assertEquals (0, tSavedGameBad.getLastActionNumber ());
		}
	}

	@Nested
	@DisplayName ("Players in Games")
	class PlayersInGameTests {

		@Test
		@DisplayName ("Player is in Game")
		void PlayerIsInGameTest () {
			savedGame.addPlayer ("SGPlayerAlpha");
			savedGame.addPlayer ("SGPlayerBeta");
			savedGame.addPlayer ("SGPlayerGamma");

			assertTrue (savedGame.hasPlayer ("SGPlayerBeta"));
			assertTrue (savedGame.hasPlayer ("SGPlayerAlpha"));
			assertTrue (savedGame.hasPlayer ("SGPlayerGamma"));
			assertFalse (savedGame.hasPlayer ("SGPlayerDelta"));
		}

		@Test
		@DisplayName ("Player is in Game")
		void NoPlayersInGameTest () {
			assertFalse (savedGame.hasPlayer ("SGPlayerBeta"));
		}
	}
}
