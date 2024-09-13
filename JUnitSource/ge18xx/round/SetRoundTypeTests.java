package ge18xx.round;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.PlayerManager;
import ge18xx.player.PlayerTestFactory;
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
	OperatingRound mOperatingRound;
	StockRound stockRound;
	PlayerTestFactory playerTestFactory;
	PlayerManager mPlayerManager;

	@BeforeEach
	void setUp () throws Exception {
		String tClientName;
		PlayerInputFrame mPlayerInputFrame;
		RoundFrame mRoundFrame;

		tClientName = "RMTestBuster";
		gameTestFactory = new GameTestFactory ();
		gameManager = gameTestFactory.buildGameManager (tClientName);
		mPlayerInputFrame = gameTestFactory.buildPIFMock ();
		gameManager.setPlayerInputFrame (mPlayerInputFrame);
		roundTestFactory = new RoundTestFactory ();
		roundManager = roundTestFactory.buildRoundManager (gameManager);
		mGameManager = gameTestFactory.buildGameManagerMock ();
		operatingRound = roundTestFactory.buildOperatingRound (roundManager);
		playerTestFactory = new PlayerTestFactory (mGameManager);
		mPlayerManager = playerTestFactory.buildPlayerManagerMock (3);
		mOperatingRound = roundTestFactory.buildOperatingRoundMock (mPlayerManager,  roundManager);
		Mockito.when (mOperatingRound.getID ()).thenReturn ("1.1");
		roundManager.setOperatingRound (mOperatingRound);
		stockRound = roundTestFactory.buildStockRound (mPlayerManager, roundManager);
		roundManager.setStockRound (stockRound);
		mRoundFrame = roundTestFactory.buildRoundFrameMock ();
		roundManager.setRoundFrame (mRoundFrame);
	}

	@Test
	@DisplayName ("Setting the Round to an Different Types")
	void SetRoundManagerToTypesTest () {
		ActorI.ActionStates tCurrentRoundType;
		
		roundManager.setRoundToOperatingRound ();
		tCurrentRoundType = roundManager.getCurrentRoundType ();
		assertEquals (ActorI.ActionStates.OperatingRound, tCurrentRoundType);

		stockRound.setIDPart1 (1);
		stockRound.setIDPart2 (0);
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
	
	@Test
	@DisplayName ("Testing the calls to gameManager boolean tests")
	void verifyingGameManagerBooleanTests () {
		roundManager.setGameManager (mGameManager);
		roundManager.setOperatingRound (mOperatingRound);
		
		Mockito.when (mGameManager.applyingAction ()).thenReturn (true);
		roundManager.applyingAction ();
		Mockito.verify (mGameManager, times (1)).applyingAction ();
		
		Mockito.when (mGameManager.canStartOperatingRound ()).thenReturn (true);
		roundManager.canStartOperatingRound ();
		Mockito.verify (mGameManager, times (1)).canStartOperatingRound ();

		Mockito.when (mOperatingRound.roundIsDone ()).thenReturn (true);
		roundManager.operatingRoundIsDone ();
		Mockito.verify (mOperatingRound, times (1)).roundIsDone ();
	}
}
