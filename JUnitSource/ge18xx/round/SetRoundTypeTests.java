package ge18xx.round;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.round.action.ActorI;
import ge18xx.toplevel.PlayerInputFrame;

@DisplayName ("Set Round Type Tests")
class SetRoundTypeTests {
	GameManager gameManager;
	GameManager mGameManager;
	GameTestFactory gameTestFactory;
	RoundTestFactory roundTestFactory;
	RoundManager roundManager;
	OperatingRound operatingRound;

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
		mGameManager = gameTestFactory.buildGameManagerMock ();
		operatingRound = roundTestFactory.buildOperatingRound (roundManager);
	}

	@Test
	@DisplayName ("Setting the Round to an Different Types")
	void SetRoundManagerToTypesTest () {
		ActorI.ActionStates tCurrentRoundType;
		
		roundManager.setRoundToOperatingRound ();
		tCurrentRoundType = roundManager.getCurrentRoundType ();
		assertEquals (ActorI.ActionStates.OperatingRound, tCurrentRoundType);

		roundManager.setRoundToStockRound ();
		tCurrentRoundType = roundManager.getCurrentRoundType ();
		assertEquals (ActorI.ActionStates.StockRound, tCurrentRoundType);

		roundManager.setRoundToAuctionRound ();
		tCurrentRoundType = roundManager.getCurrentRoundType ();
		assertEquals (ActorI.ActionStates.AuctionRound, tCurrentRoundType);

		roundManager.setRoundTypeTo (ActorI.ActionStates.OperatingRound);
		tCurrentRoundType = roundManager.getCurrentRoundType ();
		assertEquals (ActorI.ActionStates.OperatingRound, tCurrentRoundType);

		roundManager.setRoundTypeTo (ActorI.ActionStates.StockRound);
		tCurrentRoundType = roundManager.getCurrentRoundType ();
		assertEquals (ActorI.ActionStates.StockRound, tCurrentRoundType);

		roundManager.setRoundTypeTo (ActorI.ActionStates.AuctionRound);
		tCurrentRoundType = roundManager.getCurrentRoundType ();
		assertEquals (ActorI.ActionStates.AuctionRound, tCurrentRoundType);
	}
}
