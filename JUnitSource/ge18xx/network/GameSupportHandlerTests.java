package ge18xx.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.toplevel.PlayerInputFrame;

@DisplayName ("Game Support Handler Tests")
@ExtendWith (MockitoExtension.class)
class GameSupportHandlerTests {

	GameSupportHandler gameSupportHandler;
	GameTestFactory testFactory;
	NetworkTestFactory networkTestFactory;
	PlayerInputFrame mPlayerInputFrame;
	JGameClient jGameClient;
	String clientName;
	ChatServerHandler mChatServerHandler;

	@BeforeEach
	void setUp () throws Exception {
		GameManager tGameManager;

		testFactory = new GameTestFactory ();
		clientName = "GMTestBuster";
		tGameManager = testFactory.buildGameManager (clientName);
		mPlayerInputFrame = testFactory.buildPIFMockWithGameSet ();
		tGameManager.setPlayerInputFrame (mPlayerInputFrame);
		jGameClient = new JGameClient ("JGameClient Testing Frame", tGameManager);

		gameSupportHandler = new GameSupportHandler (jGameClient);
		networkTestFactory = new NetworkTestFactory (testFactory);
		mChatServerHandler = networkTestFactory.buildMockChatServerHandler ();
	}

	@Test
	@DisplayName ("Test Retrieving Game ID from Request")
	void getGameIDFromRequestTest () {
		String tGoodRequest = "<GS gameID=\"2021-07-31-2005\"><ActionNumber requestNew=\"TRUE\"></GS>";
		String tBadRequest = "<GS><LastActionNumber requestNew=\"TRUE\"></GS>";
		String tFoundGameID;

		tFoundGameID = gameSupportHandler.getGameIDFromRequest (tGoodRequest);
		assertEquals ("2021-07-31-2005", tFoundGameID);
		tFoundGameID = gameSupportHandler.getGameIDFromRequest (tBadRequest);
		assertEquals ("NOID", tFoundGameID);
	}

	@Test
	@DisplayName ("Test Retrieving Game ID from Response")
	void getGameIDFromResponseTest () {
		String tGoodResponse = "<GSResponse gameID=\"2021-07-31-2005\"><LastActionNumber requestNew=\"TRUE\"></GSResponse>";
		String tBadResponse = "<GSResponse><LastActionNumber requestNew=\"TRUE\"></GSResponse>";
		String tFoundGameID;

		tFoundGameID = gameSupportHandler.getGameIDFromNetworkResponse (tGoodResponse);
		assertEquals ("2021-07-31-2005", tFoundGameID);
		
		tFoundGameID = gameSupportHandler.getGameIDFromNetworkResponse (tBadResponse);
		assertEquals ("NOID", tFoundGameID);
	}

	@Test
	@DisplayName ("Test GameSupport to Mocked ServerHandler")
	void requestGameSupportTest () {
		String tGoodRequest = "<GS gameID=\"2021-07-31-2005\"><ActionNumber requestNew=\"TRUE\"></GS>";
		String tGoodResponse = "<GSResponse gameID=\"2021-07-31-2005\"><LastActionNumber requestNew=\"TRUE\"></GSResponse>";

		Mockito.doReturn (false).when (mChatServerHandler).sendGameSupport (tGoodRequest);
		jGameClient.setServerHandler (mChatServerHandler);
		gameSupportHandler.setResponse (tGoodResponse);

		assertEquals (tGoodResponse, gameSupportHandler.requestGameSupport (tGoodRequest));

	}

	@Test
	@DisplayName ("Test GameSupport to Mocked ServerHandler")
	void retrieveGameIDviaGameSupportTest () {
//		String tGoodRequest = "<GS gameID=\"2021-07-31-2005\"><ActionNumber requestNew=\"TRUE\"></GS>";
		String tGoodResponse = "<GSResponse gameID=\"2021-07-31-2005\"><LastActionNumber requestNew=\"TRUE\"></GSResponse>";
		String tGameIDRequest = "Game Support <GS><GameIDRequest></GS>";

		Mockito.doReturn (false).when (mChatServerHandler).sendGameSupport (tGameIDRequest);
		jGameClient.setServerHandler (mChatServerHandler);
		gameSupportHandler.setResponse (tGoodResponse);

		assertEquals ("2021-07-31-2005", gameSupportHandler.retrieveGameID ());

	}
}