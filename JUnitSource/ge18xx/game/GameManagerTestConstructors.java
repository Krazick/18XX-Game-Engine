package ge18xx.game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ge18xx.toplevel.PlayerInputFrame;

@DisplayName ("Game Manager Constructor Tests")
class GameManagerTestConstructors {
	TestFactory testFactory;
	
	@BeforeEach
	void setUp () throws Exception {
		testFactory = new TestFactory ();
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
		String tClientName, tConfigFileName;
		
		tClientName = "GMTestBuster";
		tConfigFileName = "ge18xx." + tClientName + ".cfg.xml";
		tGameManager = testFactory.buildGameManager (tClientName);
		
		assertEquals (tClientName, tGameManager.getClientUserName ());
		assertNull (tGameManager.getRoundManager ());
		assertNull (tGameManager.getPlayerManager ());
		assertNull (tGameManager.getPhaseManager ());
		assertNull (tGameManager.getMapFrame ());
		assertNull (tGameManager.getBankPool ());
		
		assertNotNull (tGameManager.getBank ());
		
		assertEquals (false, tGameManager.isNetworkGame ());
		
		assertEquals (tConfigFileName, tGameManager.getConfigFileName ());
	}
	
	@Test
	@DisplayName ("Test Game Initiation") 
	void gameInitiationTest () {
		GameManager tGameManager;
		String tClientName;
		GameInfo tGameInfo;
		PlayerInputFrame mPlayerInputFrame;
		
		tClientName = "TGIBuster";

		tGameManager = testFactory.buildGameManager (tClientName);
		tGameInfo = testFactory.buildGameInfo ();
		
		assertEquals ("<NONE>", tGameManager.getGameName ());
		assertEquals ("<NONE>", tGameManager.getActiveGameName ());
		assertEquals ("1830TEST", tGameInfo.getName ());
		assertEquals ("<NONE>", tGameManager.getFileName ("Market"));
		assertFalse (tGameManager.gameIsStarted ());
		
		mPlayerInputFrame = testFactory.buildPIFMock ();
		tGameManager.setPlayerInputFrame (mPlayerInputFrame);
		tGameManager.initiateGame (tGameInfo);
		
		assertTrue (tGameManager.gameIsStarted ());
		assertEquals ("1830TEST", tGameManager.getGameName ());
		assertEquals ("1830TEST", tGameManager.getActiveGameName ());
		assertEquals (0, tGameManager.getCountOfMinors ());
		assertEquals (0, tGameManager.getCountOfCoals ());
		assertEquals (6, tGameManager.getCountOfPrivates ());
		assertEquals (6, tGameManager.getCountOfOpenPrivates ());
		assertEquals ("1830 XML Data/1830 Map.xml", tGameManager.getMapFileName ());
		assertEquals ("1830 XML Data/1830 Map.xml", tGameManager.getFileName ("map"));
		
		assertNull (tGameManager.getGameFrameConfig ());
	}
}
