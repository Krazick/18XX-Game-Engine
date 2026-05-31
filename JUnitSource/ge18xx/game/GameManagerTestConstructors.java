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
	FrameTestFactory frameTestFactory;
	RoundTestFactory roundTestFactory;
	GameManager gameManager;
	RoundManager roundManager;

	@BeforeEach
	void setUp () throws Exception {
		String tClientName;
		
		tClientName = "TGIBuster";
		
		gameTestFactory = new GameTestFactory ();
		roundTestFactory = new RoundTestFactory ();
		gameManager = gameTestFactory.buildGameManager (tClientName);
		roundManager = roundTestFactory.buildRoundManager (gameManager);
		frameTestFactory = new FrameTestFactory (gameManager, roundManager);
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
		ShareCompaniesFrame mShareCompaniesFrame;

		mShareCompaniesFrame = frameTestFactory.buildShareCompaniesFrameMock ("Share Company Frame Mock");
		gameManager.setShareCompaniesFrame (mShareCompaniesFrame);
		assertEquals (ActorI.ActionStates.NoState, gameManager.getCorporationState ("TGIBuster"));
	}
	
	@Test
	@DisplayName ("Test Game Initiation")
	void gameInitiationTest () {
		GameInfo tGameInfo;
		PlayerInputFrame mPlayerInputFrame;

		tGameInfo = gameTestFactory.buildGameInfo (1);

		assertEquals ("<NONE>", gameManager.getGameName ());
		assertEquals ("", gameManager.getActiveGameName ());
		assertEquals ("1830TEST", tGameInfo.getName ());
		assertEquals ("<NONE>", gameManager.getFileName ("Market"));
		assertFalse (gameManager.gameIsStarted ());

		mPlayerInputFrame = gameTestFactory.buildPIFMock ();
		gameManager.setPlayerInputFrame (mPlayerInputFrame);
		gameManager.initiateGame (tGameInfo);

		assertTrue (gameManager.gameIsStarted ());
		assertEquals ("1830TEST", gameManager.getGameName ());
		assertEquals ("1830TEST", gameManager.getActiveGameName ());
		assertEquals (0, gameManager.getCountOfMinors ());
		assertEquals (6, gameManager.getCountOfPrivates ());
		assertEquals (6, gameManager.getCountOfOpenPrivates ());
		assertEquals ("1830TEST XML Data/1830TEST Map.xml", gameManager.getMapFileName ());
		assertEquals ("1830TEST XML Data/1830TEST Map.xml", gameManager.getFileName ("map"));
		assertEquals ("1830TEST XML Data/1830TEST Market.xml", gameManager.getMarketFileName ());
		assertEquals ("1830TEST XML Data/1830TEST Cities.xml", gameManager.getCitiesFileName ());
		assertEquals ("1830TEST XML Data/1830TEST Companies.xml", gameManager.getCompaniesFileName ());
		assertEquals ("1830TEST XML Data/1830TEST TileSet.xml", gameManager.getTileSetFileName ());

		assertNull (gameManager.getGameFrameConfig ());
	}
}
