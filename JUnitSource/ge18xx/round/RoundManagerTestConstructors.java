package ge18xx.round;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.PlayerManager;
import ge18xx.player.PlayerTestFactory;
import ge18xx.round.action.ActorI;
import ge18xx.toplevel.PlayerInputFrame;

@DisplayName ("Round Manager Constructor Tests")
public class RoundManagerTestConstructors {
	GameManager gameManager;
	GameManager mGameManager;
	GameTestFactory gameTestFactory;
	PlayerTestFactory playerTestFactory;
	PlayerManager playerManager;
	RoundTestFactory roundTestFactory;
	RoundManager roundManager;
	GameInfo gameInfo;

	@BeforeEach
	void setUp () throws Exception {
		String tClientName;
		PlayerInputFrame mPlayerInputFrame;

		tClientName = "RMTestBuster";
		gameTestFactory = new GameTestFactory ();
		gameManager = gameTestFactory.buildGameManager (tClientName);
		gameInfo = gameTestFactory.buildGameInfo (1);
		gameManager.setGameInfo (gameInfo);
		playerTestFactory = new PlayerTestFactory (gameManager);
		mPlayerInputFrame = gameTestFactory.buildPIFMock ();
		gameManager.setPlayerInputFrame (mPlayerInputFrame);
		roundTestFactory = new RoundTestFactory ();
		roundManager = roundTestFactory.buildRoundManager (gameManager);
		mGameManager = gameTestFactory.buildGameManagerMock ();
		playerManager = playerTestFactory.buildPlayerManager ();
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
		StockRound tStockRound;

		Mockito.when (mGameManager.canPayHalfDividend ()).thenReturn (false);
		Mockito.when (mGameManager.isPlaceTileMode ()).thenReturn (false);
		Mockito.when (mGameManager.isPlaceTokenMode ()).thenReturn (false);
		Mockito.when (mGameManager.getActiveGame ()).thenReturn (gameInfo);

		roundManager.setGameManager (mGameManager);
		tStockRound = roundTestFactory.buildStockRound (roundManager, playerManager);
		roundManager.setCurrentRound (tStockRound);
		assertFalse (roundManager.isPlaceTileMode ());
		assertFalse (roundManager.isPlaceTokenMode ());
		assertFalse (roundManager.canPayHalfDividend ());
		assertFalse (roundManager.isAAuctionRound ());
		assertTrue (roundManager.isAStockRound ());
		assertFalse (roundManager.isAOperatingRound ());
	}

	@Test
	@DisplayName ("Test Set and Get Methods")
	void setAndGetMethodTests () {
		StockRound tStockRound;
		AuctionRound tAuctionRound;
		OperatingRound tOperatingRound;

		tStockRound = roundTestFactory.buildStockRound (roundManager, playerManager);
		roundManager.setCurrentRound (tStockRound);

		assertEquals ("Stock Round", roundManager.getCurrentRoundState ().toString ());
		assertTrue (roundManager.isAStockRound ());
		
		tAuctionRound = roundTestFactory.buildAuctionRound (roundManager);
		roundManager.setCurrentRound (tAuctionRound);

		assertEquals ("Auction Round", roundManager.getCurrentRoundState ().toString ());
		assertTrue (roundManager.isAAuctionRound ());
	
		tOperatingRound = roundTestFactory.buildOperatingRound (roundManager);
		roundManager.setCurrentRound (tOperatingRound);

		assertEquals ("Operating Round", roundManager.getCurrentRoundState ().toString ());
		assertTrue (roundManager.isAOperatingRound ());
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
		FormationRound tFormationRound;
		ContractBidRound tContractBidRound;

		tPlayerTestFactory = new PlayerTestFactory (gameManager);
		tPlayerManager = tPlayerTestFactory.buildPlayerManager ();
		
		tStockRound = roundTestFactory.buildStockRound (roundManager, tPlayerManager);
		roundManager.setStockRound (tStockRound);
		
		tAuctionRound = roundTestFactory.buildAuctionRound (roundManager);
		roundManager.setAuctionRound (tAuctionRound);
		
		tOperatingRound = roundTestFactory.buildOperatingRound (roundManager);
		roundManager.setOperatingRound (tOperatingRound);
		
		tFormationRound = roundTestFactory.buildFormationRound (roundManager);
		roundManager.setFormationRound (tFormationRound);
		
		tContractBidRound = roundTestFactory.buildContractBidRound (roundManager);
		roundManager.setContractBidRound (tContractBidRound);

//		roundManager.setCurrentRoundState (ActorI.ActionStates.StockRound);
		tActor = roundManager.getActor ("Stock Round");
		assertTrue (tActor.isAStockRound ());

//		roundManager.setCurrentRoundState (ActorI.ActionStates.AuctionRound);
		tActor = roundManager.getActor ("Auction Round");
		assertEquals (tAuctionRound, tActor);

//		roundManager.setCurrentRoundState (ActorI.ActionStates.OperatingRound);
		tActor = roundManager.getActor ("Operating Round");
		assertTrue (tActor.isAOperatingRound ());

//		roundManager.setCurrentRoundState (ActorI.ActionStates.FormationRound);
		tActor = roundManager.getActor ("Formation Round");
		assertTrue (tActor.isAFormationRound ());

//		roundManager.setCurrentRoundState (ActorI.ActionStates.ContractBidRound);
		tActor = roundManager.getActor ("Contract Bid Round");
		assertTrue (tActor.isAContractBidRound ());

		tActor = roundManager.getActor ("RoundManager Tester");
		assertNull (tActor);
	}
}
