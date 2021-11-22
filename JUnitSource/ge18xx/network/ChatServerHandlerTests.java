package ge18xx.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.game.GameManager;
import ge18xx.game.TestFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatServerHandlerTests {
	ChatServerHandler chatServerHandler;
	TestFactory testFactory;

	@BeforeEach
	void setUp () throws Exception {
		GameManager tGameManager;
		String tClientName;
		
		testFactory = new TestFactory ();
		tClientName = "GMTestBuster";
		tGameManager = testFactory.buildGameManager (tClientName);
		String tHost = "96.240.138.230";
		int tPort = 18300;
		
		chatServerHandler = new ChatServerHandler (tHost, tPort, tGameManager);
	}

	@Test
	@DisplayName ("Test Valid Game ID")
	void testBuildGameSupport () {
		String tGameID_NO_ID = "NOID";
		String tGameID_EMPTY_GAME_ID = "";
		String tGameID_VALID_ID = "2021-11-22-1433";
		String tXMLChild = "<XMLData>";
		
		assertEquals ("Game Support <GS><XMLData></GS>", chatServerHandler.buildGameSupportXML (tGameID_EMPTY_GAME_ID, tXMLChild));
		assertEquals ("Game Support <GS><XMLData></GS>", chatServerHandler.buildGameSupportXML (tGameID_NO_ID, tXMLChild));
		assertEquals ("Game Support <GS gameID=\"2021-11-22-1433\"><XMLData></GS>", chatServerHandler.buildGameSupportXML (tGameID_VALID_ID, tXMLChild));

	}
	@Test
	@DisplayName ("Test Valid Game ID")
	void testValidGameID () {
		String tGameID_NO_ID = "NOID";
		String tGameID_EMPTY_GAME_ID = "";
		String tGameID_VALID_ID = "2021-11-22-1433";
		
		assertFalse (chatServerHandler.isValidGameID (tGameID_EMPTY_GAME_ID));
		assertFalse (chatServerHandler.isValidGameID (tGameID_NO_ID));
		assertTrue (chatServerHandler.isValidGameID (tGameID_VALID_ID));
	}


}
