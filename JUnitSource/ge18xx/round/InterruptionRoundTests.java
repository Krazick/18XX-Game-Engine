package ge18xx.round;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.BankTestFactory;
import ge18xx.bank.StartPacketFrame;
import ge18xx.bank.StartPacketTestFactory;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.PlayerManager;
import ge18xx.player.PlayerTestFactory;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActionEffectsFactory;
import ge18xx.round.action.ActorI;
import geUtilities.utilites.xml.UtilitiesTestFactory;

class InterruptionRoundTests {
	GameTestFactory gameTestFactory;
	RoundTestFactory roundTestFactory;
	PlayerTestFactory playerTestFactory;
	UtilitiesTestFactory utilitiesTestFactory;
	BankTestFactory bankTestFactory;
	GameManager gameManager;
	PlayerManager mPlayerManager;
	RoundManager roundManager;
	OperatingRound mOperatingRound;
	ActionEffectsFactory actionEffectsFactory;
	StartPacketTestFactory startPacketTestFactory;
	StartPacketFrame mStartPacketFrame;
	BankPool mBankPool;
	Bank mBank;
	InterruptionRound mInterruptionRound;
	
	@BeforeEach
	void setup () {
		gameTestFactory = new GameTestFactory ();
		gameManager = gameTestFactory.buildGameManager ();
		bankTestFactory = new BankTestFactory ();
		mBankPool = bankTestFactory.buildBankPoolMock (gameManager, "Bank Pool");
		mBank = bankTestFactory.buildBankMock (gameManager, "Bank");
		startPacketTestFactory = new StartPacketTestFactory (gameManager, mBank);
		mStartPacketFrame = startPacketTestFactory.buildStartPacketFrameMock ("SPF Mock");
		Mockito.when (mBank.getStartPacketFrame ()).thenReturn (mStartPacketFrame);

		roundTestFactory = new RoundTestFactory ();
		playerTestFactory = new PlayerTestFactory (gameManager);
		utilitiesTestFactory = new UtilitiesTestFactory ();
		roundManager = roundTestFactory.buildRoundManager (gameManager);
		mPlayerManager = playerTestFactory.buildPlayerManagerMock (4);
		mOperatingRound = roundTestFactory.buildOperatingRoundMock (mPlayerManager, roundManager);
		actionEffectsFactory = new ActionEffectsFactory (gameManager, utilitiesTestFactory);
		
		mInterruptionRound = roundTestFactory.buildFormationRoundMock (roundManager);
		roundManager.setCurrentRound (mInterruptionRound);
	}
	
	@Test
	@DisplayName ("Test Interruption Round Triggering if Null")
	void testInterruptionRoundTriggeringNull () {
		Action tAction;
		RoundType mRoundType;
		
		tAction = actionEffectsFactory.getTestActionAt (1);
		assertEquals ("Change Round Action", tAction.getName ());
		
		mRoundType = roundTestFactory.buildRoundTypeMock ();
		Mockito.when (mRoundType.getInterruptionRound ()).thenReturn (null);
		Mockito.when (mInterruptionRound.getRoundType ()).thenReturn (mRoundType);
		assertFalse (roundManager.checkAndHandleInterruption (tAction));
	}

	@Test
	@DisplayName ("Test Interruption Round Triggering")
	void testInterruptionRoundTriggering () {
		Action tAction;
		RoundType tRoundType;
		OperatingRound tOperatingRound;
		FormationRound tFormationRound;
		GameInfo mGameInfo;
		
		tAction = actionEffectsFactory.getTestActionAt (1);
		assertEquals ("Change Round Action", tAction.getName ());
		
		tRoundType = roundTestFactory.buildRoundTypeAt (1);
		mGameInfo = gameTestFactory.buildGameInfoMock ();
		Mockito.when (mGameInfo.getRoundType (anyString ())).thenReturn (tRoundType);
		
		gameManager.setGameInfo (mGameInfo);
		tOperatingRound = roundTestFactory.buildOperatingRound (roundManager);
		roundManager.setOperatingRound (tOperatingRound);
		roundManager.setCurrentRound (tOperatingRound);
		
		tFormationRound = roundTestFactory.buildFormationRound (roundManager);
		tFormationRound.setRoundType ();
		roundManager.setFormationRound (tFormationRound);
		assertFalse (roundManager.checkAndHandleInterruption (tAction));
	}

	@Test
	@DisplayName ("Test Interruption Round Triggering2")
	void testInterruptionRoundTriggering2 () {
		Action tAction;
		RoundType tRoundType;
		RoundType tInterruptionRoundType;
		OperatingRound tOperatingRound;
		FormationRound tFormationRound;
		GameInfo mGameInfo;
		
		tAction = actionEffectsFactory.getTestActionAt (1);
		assertEquals ("Change Round Action", tAction.getName ());
		
		tRoundType = roundTestFactory.buildRoundTypeAt (1);
		mGameInfo = gameTestFactory.buildGameInfoMock ();
		Mockito.when (mGameInfo.getRoundType (anyString ())).thenReturn (tRoundType);
		
		gameManager.setGameInfo (mGameInfo);
		tOperatingRound = roundTestFactory.buildOperatingRound (roundManager);
		roundManager.setOperatingRound (tOperatingRound);
		roundManager.setCurrentRound (tOperatingRound);
		
		tInterruptionRoundType = roundTestFactory.buildRoundTypeAt (2);
		tFormationRound = roundTestFactory.buildFormationRound (roundManager);
		tFormationRound.setRoundType (tInterruptionRoundType);
		roundManager.setFormationRound (tFormationRound);
		assertFalse (roundManager.checkAndHandleInterruption (tAction));
	}

	@Test
	@DisplayName ("Test getting Actor Round by Name")
	void testGetActorByName () {
		GameInfo mGameInfo;
		StockRound tStockRound;
		OperatingRound tOperatingRound;
		AuctionRound tAuctionRound;
		FormationRound tFormationRound;
		ContractBidRound tContractBidRound;
		RoundType tRoundType;
		ActorI tActor;
		
		tRoundType = roundTestFactory.buildRoundTypeAt (0);
		mGameInfo = gameTestFactory.buildGameInfoMock ();
		Mockito.when (mGameInfo.getRoundType (anyString ())).thenReturn (tRoundType);
		
		gameManager.setGameInfo (mGameInfo);
		
		tStockRound = roundTestFactory.buildStockRound (roundManager, mPlayerManager);
		
		assertNull (roundManager.getActor ("Stock Round"));
		
		roundManager.setStockRound (tStockRound);
		tActor = roundManager.getActor ("Stock Round");
		assertEquals (tActor, tStockRound);

		tOperatingRound = roundTestFactory.buildOperatingRound (roundManager);
		roundManager.setOperatingRound (tOperatingRound);
		tActor = roundManager.getActor ("Operating Round");
		assertEquals (tActor, tOperatingRound);
		
		tAuctionRound = roundTestFactory.buildAuctionRound (roundManager);
		roundManager.setAuctionRound (tAuctionRound);
		tActor = roundManager.getActor ("Auction Round");
		assertEquals (tActor, tAuctionRound);

		tFormationRound = roundTestFactory.buildFormationRound (roundManager);
		roundManager.setFormationRound (tFormationRound);
		tActor = roundManager.getActor ("Formation Round");
		assertEquals (tActor, tFormationRound);

		tContractBidRound = roundTestFactory.buildContractBidRound (roundManager);
		roundManager.setContractBidRound (tContractBidRound);
		tActor = roundManager.getActor ("Contract Bid Round");
		assertEquals (tActor, tContractBidRound);
	}
	
	@Test
	@DisplayName ("Test is Round Actor by Name")
	void testisRoundActor () {
		GameInfo mGameInfo;
		RoundType tRoundType;
		Round tRoundA;
		Round tRoundNull;

		tRoundType = roundTestFactory.buildRoundTypeAt (0);
		mGameInfo = gameTestFactory.buildGameInfoMock ();
		Mockito.when (mGameInfo.getRoundType (anyString ())).thenReturn (tRoundType);
		
		gameManager.setGameInfo (mGameInfo);
		
		tRoundA = roundTestFactory.buildStockRound (roundManager, mPlayerManager);
		assertTrue (roundManager.isRoundActor (tRoundA, "Stock Round"));
		assertFalse (roundManager.isRoundActor (tRoundA, "Operating Round"));
		
		tRoundNull = null;
		assertFalse (roundManager.isRoundActor (tRoundNull, "Stock Round"));
	}

}
