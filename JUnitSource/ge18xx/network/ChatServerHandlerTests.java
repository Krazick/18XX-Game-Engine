package ge18xx.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatServerHandlerTests {
	ChatServerHandler mChatServerHandler;
	NetworkTestFactory networkTestFactory;
	
	@BeforeEach
	void setUp () throws Exception {
		networkTestFactory = new NetworkTestFactory ();
		mChatServerHandler = networkTestFactory.buildChatServerHandler ();
	}

	@Test
	@DisplayName ("Test building Game Support")
	void testBuildGameSupport () {
		String tGameID_NO_ID = "NOID";
		String tGameID_EMPTY_GAME_ID = "";
		String tGameID_VALID_ID = "2021-11-22-1433";
		String tXMLChild = "<XMLData>";
		
		assertEquals ("Game Support <GS><XMLData></GS>", mChatServerHandler.buildGameSupportXML (tGameID_EMPTY_GAME_ID, tXMLChild));
		assertEquals ("Game Support <GS><XMLData></GS>", mChatServerHandler.buildGameSupportXML (tGameID_NO_ID, tXMLChild));
		assertEquals ("Game Support <GS gameID=\"2021-11-22-1433\"><XMLData></GS>", mChatServerHandler.buildGameSupportXML (tGameID_VALID_ID, tXMLChild));

	}
	@Test
	@DisplayName ("Test Valid Game ID")
	void testValidGameID () {
		String tGameID_NO_ID = "NOID";
		String tGameID_EMPTY_GAME_ID = "";
		String tGameID_VALID_ID = "2021-11-22-1433";
		
		assertFalse (mChatServerHandler.isValidGameID (tGameID_EMPTY_GAME_ID));
		assertFalse (mChatServerHandler.isValidGameID (tGameID_NO_ID));
		assertTrue (mChatServerHandler.isValidGameID (tGameID_VALID_ID));
	}


}
