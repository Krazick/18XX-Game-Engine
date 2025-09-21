package ge18xx.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.round.RoundManager;
import ge18xx.round.RoundTestFactory;
import ge18xx.round.action.ActorI;
import ge18xx.toplevel.FrameTestFactory;
import ge18xx.toplevel.PlayerInputFrame;
import ge18xx.toplevel.ShareCompaniesFrame;

@DisplayName ("Game Manager Constructor Tests")
class GameManagerTestConstructors {
	GameTestFactory gameTestFactory;

	@BeforeEach
	void setUp () throws Exception {
		gameTestFactory = new GameTestFactory ();
	}

	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Test Constructor with no Args")
	void constructorNoArgsTest () {
		GameManager tGameManager;

		tGameManager = new GameManager ();
		assertNotNull (tGameManager);
	}

	@Test
	@DisplayName ("Test Constructor with Parent Frame and Client name")
	void constructorTwoArgsTest () {
		GameManager tGameManager;
		String tClientName;

		tClientName = "GMTestBuster";
		tGameManager = gameTestFactory.buildGameManager (tClientName);

		assertEquals (tClientName, tGameManager.getClientUserName ());
		assertNull (tGameManager.getRoundManager ());
		assertNull (tGameManager.getPlayerManager ());
		assertNull (tGameManager.getPhaseManager ());
		assertNull (tGameManager.getMapFrame ());
		assertNull (tGameManager.getBankPool ());

		assertNotNull (tGameManager.getBank ());

		assertEquals (false, tGameManager.isNetworkGame ());
	}

	@Test
	@DisplayName ("Test getCorporationState via Share Companies Frame") 
	void testGetCorporationState () {
		GameManager tGameManager;
		String tClientName;
		ShareCompaniesFrame mShareCompaniesFrame;
		FrameTestFactory frameTestFactory;
		RoundTestFactory roundTestFactory;
		RoundManager tRoundManager;
		
		tClientName = "TGIBuster";

		tGameManager = gameTestFactory.buildGameManager (tClientName);
		roundTestFactory = new RoundTestFactory ();
		tRoundManager = roundTestFactory.buildRoundManager (tGameManager);
		frameTestFactory = new FrameTestFactory (tGameManager, tRoundManager);
		mShareCompaniesFrame = frameTestFactory.buildShareCompaniesFrameMock ("Share Company Frame Mock");
		tGameManager.setShareCompaniesFrame (mShareCompaniesFrame);
		assertEquals ( ActorI.ActionStates.NoState, tGameManager.getCorporationState (tClientName));
	}
	
	@Test
	@DisplayName ("Test Game Initiation")
	void gameInitiationTest () {
		GameManager tGameManager;
		String tClientName;
		GameInfo tGameInfo;
		PlayerInputFrame mPlayerInputFrame;

		tClientName = "TGIBuster";

		tGameManager = gameTestFactory.buildGameManager (tClientName);
		tGameInfo = gameTestFactory.buildGameInfo (1);

		assertEquals ("<NONE>", tGameManager.getGameName ());
		assertEquals ("", tGameManager.getActiveGameName ());
		assertEquals ("1830TEST", tGameInfo.getName ());
		assertEquals ("<NONE>", tGameManager.getFileName ("Market"));
		assertFalse (tGameManager.gameIsStarted ());

		mPlayerInputFrame = gameTestFactory.buildPIFMock ();
		tGameManager.setPlayerInputFrame (mPlayerInputFrame);
		tGameManager.initiateGame (tGameInfo);

		assertTrue (tGameManager.gameIsStarted ());
		assertEquals ("1830TEST", tGameManager.getGameName ());
		assertEquals ("1830TEST", tGameManager.getActiveGameName ());
		assertEquals (0, tGameManager.getCountOfMinors ());
		assertEquals (6, tGameManager.getCountOfPrivates ());
		assertEquals (6, tGameManager.getCountOfOpenPrivates ());
		assertEquals ("1830TEST XML Data/1830TEST Map.xml", tGameManager.getMapFileName ());
		assertEquals ("1830TEST XML Data/1830TEST Map.xml", tGameManager.getFileName ("map"));
		assertEquals ("1830TEST XML Data/1830TEST Market.xml", tGameManager.getMarketFileName ());
		assertEquals ("1830TEST XML Data/1830TEST Cities.xml", tGameManager.getCitiesFileName ());
		assertEquals ("1830TEST XML Data/1830TEST Companies.xml", tGameManager.getCompaniesFileName ());
		assertEquals ("1830TEST XML Data/1830TEST TileSet.xml", tGameManager.getTileSetFileName ());

		assertNull (tGameManager.getGameFrameConfig ());
	}
}
