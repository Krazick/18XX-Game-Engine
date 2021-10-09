package ge18xx.round;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ge18xx.game.GameManager;
import ge18xx.game.TestFactory;
import ge18xx.toplevel.PlayerInputFrame;

@DisplayName ("Round Manager Constructor Tests")
class RoundManagerTestConstructors {
	GameManager gameManager;
	TestFactory testFactory;
	
	@BeforeEach
	void setUp () throws Exception {
		String tClientName;
		PlayerInputFrame mPlayerInputFrame;
		
		testFactory = new TestFactory ();
		tClientName = "RMTestBuster";
		gameManager = testFactory.buildGameManager (tClientName);
		mPlayerInputFrame = testFactory.buildPIFMock ();
		gameManager.setPlayerInputFrame (mPlayerInputFrame);
	}

	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Constructor with GameManager and PlayerManager NULL Tests")
	void constructorTwoArgsTest () {
		RoundManager tRoundManager;
		
		tRoundManager = new RoundManager (gameManager, StockRound.NO_PLAYER_MANAGER);
		assertEquals (gameManager, tRoundManager.getGameManager ());
		
//		fail ("Not yet implemented");
	}

}
