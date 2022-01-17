package ge18xx.round;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.toplevel.PlayerInputFrame;

@DisplayName ("Round Manager Constructor Tests")
class RoundManagerTestConstructors {
	GameManager gameManager;
	GameTestFactory gameTestFactory;
	RoundTestFactory roundTestFactory;
	RoundManager roundManager;
	
	@BeforeEach
	void setUp () throws Exception {
		String tClientName;
		PlayerInputFrame mPlayerInputFrame;
		
		tClientName = "RMTestBuster";
		gameTestFactory = new GameTestFactory ();
		gameManager = gameTestFactory.buildGameManager (tClientName);
		mPlayerInputFrame = gameTestFactory.buildPIFMock ();
		gameManager.setPlayerInputFrame (mPlayerInputFrame);
		roundTestFactory = new RoundTestFactory ();
		roundManager = roundTestFactory.buildRoundManager (gameManager);
	}

	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Constructor with GameManager and PlayerManager NULL Tests")
	void constructorTwoArgsTest () {
		assertEquals (gameManager, roundManager.getGameManager ());

	}

}
