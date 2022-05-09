package ge18xx.round;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.PlayerTestFactory;
import ge18xx.player.PlayerManager;
import ge18xx.round.action.ActorI;
import ge18xx.toplevel.PlayerInputFrame;

@DisplayName ("Round Manager Constructor Tests")
class RoundManagerTestConstructors {
	GameManager gameManager;
	GameManager mGameManager;
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
		mGameManager = gameTestFactory.buildGameManagerMock ();
	}

	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Constructor with GameManager and PlayerManager NULL Tests")
	void constructorTwoArgsTest () {
		assertEquals (gameManager, roundManager.getGameManager ());
	}

	@Test
	@DisplayName ("Test Clear Methods")
	void clearMethodTests () {
		Mockito.doNothing ().when (mGameManager).clearAllPlayerSelections ();
		Mockito.doNothing ().when (mGameManager).clearBankSelections ();
		Mockito.doNothing ().when (mGameManager).clearAllAuctionStates ();

		roundManager.setGameManager (mGameManager);
		roundManager.clearAllPlayerSelections ();
		Mockito.verify (mGameManager, times (1)).clearAllPlayerSelections ();
		roundManager.clearBankSelections ();
		Mockito.verify (mGameManager, times (1)).clearBankSelections ();
		roundManager.clearAllAuctionStates ();
		Mockito.verify (mGameManager, times (1)).clearAllAuctionStates ();
	}

	@Test
	@DisplayName ("Test Boolean Methods")
	void booleanMethodTests () {
		Mockito.when (mGameManager.canPayHalfDividend ()).thenReturn (false);
		Mockito.when (mGameManager.isPlaceTileMode ()).thenReturn (false);
		Mockito.when (mGameManager.isPlaceTokenMode ()).thenReturn (false);

		roundManager.setGameManager (mGameManager);
		assertFalse (roundManager.isPlaceTileMode ());
		assertFalse (roundManager.isPlaceTokenMode ());
		assertFalse (roundManager.canPayHalfDividend ());
		assertFalse (roundManager.isAuctionRound ());
		assertFalse (roundManager.isStockRound ());
		assertFalse (roundManager.isOperatingRound ());
	}

	@Test
	@DisplayName ("Test Set and Get Methods")
	void setAndGetMethodTests () {
		roundManager.setRoundType (ActorI.ActionStates.StockRound);
		assertEquals ("Stock Round", roundManager.getCurrentRoundType ().toString ());
		assertTrue (roundManager.isStockRound ());
		roundManager.setRoundType (ActorI.ActionStates.AuctionRound);
		assertEquals ("Auction Round", roundManager.getCurrentRoundType ().toString ());
		assertTrue (roundManager.isAuctionRound ());
		roundManager.setRoundType (ActorI.ActionStates.OperatingRound);
		assertEquals ("Operating Round", roundManager.getCurrentRoundType ().toString ());
		assertTrue (roundManager.isOperatingRound ());
	}

	@Test
	@DisplayName ("Test getActor Method")
	void getActorMethodTests () {
		ActorI tActor;
		PlayerTestFactory tPlayerTestFactory;
		PlayerManager tPlayerManager;
		StockRound tStockRound;
		OperatingRound tOperatingRound;
		AuctionRound tAuctionRound;

		tPlayerTestFactory = new PlayerTestFactory (gameManager);
		tPlayerManager = tPlayerTestFactory.buildPlayerManager ();
		tStockRound = roundTestFactory.buildStockRound (tPlayerManager, roundManager);
		roundManager.setStockRound (tStockRound);
		tAuctionRound = roundTestFactory.buildAuctionRound (roundManager);
		roundManager.setAuctionRound (tAuctionRound);
		tOperatingRound = roundTestFactory.buildOperatingRound (roundManager);
		roundManager.setOperatingRound (tOperatingRound);

		roundManager.setRoundType (ActorI.ActionStates.StockRound);
		tActor = roundManager.getActor ("Stock Round");
		assertTrue (tActor.isAStockRound ());

		roundManager.setRoundType (ActorI.ActionStates.AuctionRound);
		tActor = roundManager.getActor ("Auction Round");
		assertEquals (tAuctionRound, tActor);

		roundManager.setRoundType (ActorI.ActionStates.OperatingRound);
		tActor = roundManager.getActor ("Operating Round");
		assertTrue (tActor.isAOperatingRound ());

		tActor = roundManager.getActor ("RoundManager Tester");
		assertNull (tActor);
	}
}
