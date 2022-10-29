package ge18xx.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;

@DisplayName ("JGameClient XML Tests")
public class JGameClientXMLTests {
	GameTestFactory testFactory;
	JGameClient jGameClient;

	@BeforeEach
	public void setUp () {
		GameManager tGameManager;
		String tClientName;

		testFactory = new GameTestFactory ();
		tClientName = "GMTestBuster";
		tGameManager = testFactory.buildGameManager (tClientName);
		jGameClient = new JGameClient ("JGameClient Testing Frame", tGameManager);
	}

	@Test
	@DisplayName ("Building Game Activity 2 Attributes Tests")
	public void JGameClientGameActivity2XMLTests () {
		int tSelectedGameIndex = 0;
		String tBroadcastMessage = "This is a Broadcast Message";
		String tGameActivity2, tGameActivity3;
		String tExpected1 = "<GA><GameSelection gameIndex=\"0\" z_broadcast=\"This is a Broadcast Message\"/></GA>";

		tGameActivity2 = jGameClient.buildGameActivityXML (JGameClient.EN_GAME_SELECTION, JGameClient.AN_GAME_INDEX,
				tSelectedGameIndex + "", JGameClient.AN_BROADCAST_MESSAGE, tBroadcastMessage);
		assertEquals (tExpected1, tGameActivity2);

		tGameActivity3 = jGameClient.buildGameActivityXML (JGameClient.EN_GAME_SELECTION, JGameClient.AN_GAME_INDEX,
				tSelectedGameIndex, JGameClient.AN_BROADCAST_MESSAGE, tBroadcastMessage);
		assertEquals (tExpected1, tGameActivity3);
	}

	@Test
	@DisplayName ("Building Game XML 1 Attribute Tests")
	public void JGameClientGameActivity1XMLTests () {
		int tSelectedGameIndex = 0;
		String tGameActivity2;
		String tExpected1 = "<GameSelection><NetworkGame gameIndex=\"0\"/></GameSelection>";

		tGameActivity2 = jGameClient.buildGameXML (JGameClient.EN_GAME_SELECTION, JGameClient.EN_NETWORK_GAME, JGameClient.AN_GAME_INDEX,
				tSelectedGameIndex + "");
		assertEquals (tExpected1, tGameActivity2);
	}

	@Test
	@DisplayName ("Building Game Activity 3 Attributes Tests")
	public void JGameClientGameActivity3XMLTests () {
		int tSelectedGameID = 7;
		String tGameID = "1830.2022-06-23-1655";
		String tBroadcastMessage = "This is a Broadcast Message";
		String tGameActivity1;
		String tGameActivity2;
		String tExpected1 = "<GA><PlayerOrder gameID=\"7\" z_broadcast=\"This is a Broadcast Message\"/></GA>";
		String tExpected2 = "<GA><GameSelection gameID=\"1830.2022-06-23-1655\" player=\"AlphaPlayer\" z_broadcast=\"This is a Broadcast Message\"/></GA>";

		tGameActivity1 = jGameClient.buildGameActivityXML (JGameClient.EN_PLAYER_ORDER, JGameClient.AN_GAME_ID,
				tSelectedGameID + "", JGameClient.AN_BROADCAST_MESSAGE, tBroadcastMessage);
		assertEquals (tExpected1, tGameActivity1);

		tGameActivity2 = jGameClient.buildGameActivityXML (JGameClient.EN_GAME_SELECTION, JGameClient.AN_GAME_ID,
				tGameID , JGameClient.AN_BROADCAST_MESSAGE, tBroadcastMessage, JGameClient.AN_PLAYER, "AlphaPlayer");
		assertEquals (tExpected2, tGameActivity2);
	}

	@Test
	@DisplayName ("Building GameID Request")
	public void buildGameIDRequestTest () {
		String tGameID = "1830.2022-06-23-1655";
		String tGameName = "1830";
		int tLastActionNumber = 105;
		String tBuildResult;
		String tExpected;

		tExpected = "Game Support <GS><LoadGameSetup actionNumber=\"105\" gameID=\"1830.2022-06-23-1655\" gameName=\"1830\"/></GS>";
		tBuildResult = jGameClient.buildGameIDRequest (tGameID, tLastActionNumber, tGameName);
		assertEquals (tExpected, tBuildResult);
	}
}