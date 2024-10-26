package ge18xx.round;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankTestFactory;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.PlayerManager;
import ge18xx.player.PlayerTestFactory;
import ge18xx.round.action.ActorI;
import ge18xx.toplevel.PlayerInputFrame;

@DisplayName ("Set Round Type Tests")
public class SetRoundTypeTests {
	GameManager gameManager;
	GameManager mGameManager;
	GameTestFactory gameTestFactory;
	BankTestFactory bankTestFactory;
	Bank mBank;
	RoundTestFactory roundTestFactory;
	RoundManager roundManager;
	OperatingRound mOperatingRound;
	OperatingRound operatingRound;
	StockRound stockRound;
	AuctionRound auctionRound;
	FormationRound formationRound;
	ContractBidRound contractBidRound;
	PlayerTestFactory playerTestFactory;
	PlayerManager mPlayerManager;
	GameInfo gameInfo;

	@BeforeEach
	void setUp () throws Exception {
		String tClientName;
		PlayerInputFrame mPlayerInputFrame;
		RoundFrame mRoundFrame;

		tClientName = "RMTestBuster";
		gameTestFactory = new GameTestFactory ();
		gameManager = gameTestFactory.buildGameManager (tClientName);
		gameInfo = gameTestFactory.buildGameInfo (1);
		gameManager.setGameInfo (gameInfo);
		bankTestFactory = new BankTestFactory ();
		mBank = bankTestFactory.buildBankMock ();
		Mockito.when (mBank.firstCertificateHasBidders ()).thenReturn (false);
		mPlayerInputFrame = gameTestFactory.buildPIFMock ();
		gameManager.setPlayerInputFrame (mPlayerInputFrame);
		roundTestFactory = new RoundTestFactory ();
		roundManager = roundTestFactory.buildRoundManager (gameManager);
		gameManager.setBank (mBank);
		
		mGameManager = gameTestFactory.buildGameManagerMock ();
		Mockito.when (mGameManager.getBank ()).thenReturn (mBank);
		
		playerTestFactory = new PlayerTestFactory (mGameManager);
		mPlayerManager = playerTestFactory.buildPlayerManagerMock (3);
		
		mOperatingRound = roundTestFactory.buildOperatingRoundMock (mPlayerManager,  roundManager);
		Mockito.when (mOperatingRound.getID ()).thenReturn ("1.1");
		roundManager.setOperatingRound (mOperatingRound);

		operatingRound = roundTestFactory.buildOperatingRound (roundManager);

		stockRound = roundTestFactory.buildStockRound (roundManager, mPlayerManager);
		roundManager.setStockRound (stockRound);
		
		auctionRound = roundTestFactory.buildAuctionRound (roundManager);
		roundManager.setAuctionRound (auctionRound);
		
		formationRound = roundTestFactory.buildFormationRound (roundManager);
		roundManager.setFormationRound (formationRound);
		
		contractBidRound = roundTestFactory.buildContractBidRound (roundManager);
		roundManager.setContractBidRound (contractBidRound);
	
		mRoundFrame = roundTestFactory.buildRoundFrameMock ();
		roundManager.setRoundFrame (mRoundFrame);
	}

	@Test
	@DisplayName ("Setting the Round to an Different States")
	void SetRoundManagerToTest () {
		ActorI.ActionStates tCurrentRoundState;
		OperatingRound tOperatingRound;
		AuctionRound tAuctionRound;
		FormationRound tFormationRound;
		ContractBidRound tContractBidRound;
		
		tOperatingRound = roundTestFactory.buildOperatingRound (roundManager);
		roundManager.setOperatingRound (tOperatingRound);
		roundManager.setCurrentRound (tOperatingRound);

		roundManager.setRoundToOperatingRound ();
		tCurrentRoundState = roundManager.getCurrentRoundState ();
		assertEquals (ActorI.ActionStates.OperatingRound, tCurrentRoundState);

		stockRound.setIDPart1 (1);
		stockRound.setIDPart2 (0);
		
		roundManager.setStockRound (stockRound);
		roundManager.setCurrentRound (stockRound);

		stockRound.setRoundToStockRound ();
		tCurrentRoundState = roundManager.getCurrentRoundState ();
		assertEquals (ActorI.ActionStates.StockRound, tCurrentRoundState);

		tAuctionRound = roundTestFactory.buildAuctionRound (roundManager);
		roundManager.setAuctionRound (tAuctionRound);
		roundManager.setCurrentRound (tAuctionRound);

		roundManager.setRoundToAuctionRound ();
		tCurrentRoundState = roundManager.getCurrentRoundState ();
		assertEquals (ActorI.ActionStates.AuctionRound, tCurrentRoundState);

		roundManager.setOperatingRound (tOperatingRound);
		roundManager.setCurrentRound (tOperatingRound);

		roundManager.setRoundTypeTo (ActorI.ActionStates.OperatingRound);
		tCurrentRoundState = roundManager.getCurrentRoundState ();
		assertEquals (ActorI.ActionStates.OperatingRound, tCurrentRoundState);

		roundManager.setStockRound (stockRound);
		roundManager.setCurrentRound (stockRound);

		roundManager.setRoundTypeTo (ActorI.ActionStates.StockRound);
		tCurrentRoundState = roundManager.getCurrentRoundState ();
		assertEquals (ActorI.ActionStates.StockRound, tCurrentRoundState);

		roundManager.setAuctionRound (tAuctionRound);
		roundManager.setCurrentRound (tAuctionRound);

		roundManager.setRoundTypeTo (ActorI.ActionStates.AuctionRound);
		tCurrentRoundState = roundManager.getCurrentRoundState ();
		assertEquals (ActorI.ActionStates.AuctionRound, tCurrentRoundState);
		
		tFormationRound = roundTestFactory.buildFormationRound (roundManager);
		roundManager.setFormationRound (tFormationRound);
		roundManager.setCurrentRound (tFormationRound);

		roundManager.setRoundTypeTo (ActorI.ActionStates.FormationRound);
		tCurrentRoundState = roundManager.getCurrentRoundState ();
		assertEquals (ActorI.ActionStates.FormationRound, tCurrentRoundState);
		
		tContractBidRound = roundTestFactory.buildContractBidRound (roundManager);
		roundManager.setContractBidRound (tContractBidRound);
		roundManager.setCurrentRound (tContractBidRound);

		roundManager.setRoundTypeTo (ActorI.ActionStates.ContractBidRound);
		tCurrentRoundState = roundManager.getCurrentRoundState ();
		assertEquals (ActorI.ActionStates.ContractBidRound, tCurrentRoundState);
	}
	
	@Test
	@DisplayName ("Verifying the getRoundType method")
	void verifyingGetRoundTypes () {
		String tRoundType;
		StockRound tStockRound;
		OperatingRound tOperatingRound;
		AuctionRound tAuctionRound;
		FormationRound tFormationRound;
		ContractBidRound tContractBidRound;
		
		tStockRound = roundTestFactory.buildStockRound (roundManager, mPlayerManager);
		roundManager.setStockRound (tStockRound);
		roundManager.setCurrentRound (tStockRound);

		tStockRound.setRoundToStockRound ();
		tRoundType = roundManager.getRoundName ();
		assertEquals ("Stock Round", tRoundType);

		tOperatingRound = roundTestFactory.buildOperatingRound (roundManager);
		roundManager.setOperatingRound (tOperatingRound);
		roundManager.setCurrentRound (tOperatingRound);

		roundManager.setRoundToOperatingRound ();
		tRoundType = roundManager.getRoundName ();
		assertEquals ("Operating Round", tRoundType);

		tAuctionRound = roundTestFactory.buildAuctionRound (roundManager);
		roundManager.setAuctionRound (tAuctionRound);
		roundManager.setCurrentRound (tAuctionRound);
		
		roundManager.setRoundToAuctionRound ();
		tRoundType = roundManager.getRoundName ();
		assertEquals ("Auction Round", tRoundType);
		
		tFormationRound = roundTestFactory.buildFormationRound (roundManager);
		roundManager.setFormationRound (tFormationRound);
		roundManager.setCurrentRound (tFormationRound);
		
		roundManager.setRoundToFormationRound ();
		tRoundType = roundManager.getRoundName ();
		assertEquals ("Formation Round", tRoundType);
		
		tContractBidRound = roundTestFactory.buildContractBidRound (roundManager);
		roundManager.setContractBidRound (tContractBidRound);
		roundManager.setCurrentRound (tContractBidRound);
		
		roundManager.setRoundToContractBidRound ();
		tRoundType = roundManager.getRoundName ();
		assertEquals ("Contract Bid Round", tRoundType);
	}
	
	@Test
	@DisplayName ("Verifying the getStateName method")
	void verifyingGetStateNames () {
		String tStateName;
		
		tStateName = stockRound.getStateName ();
		assertEquals ("Stock Round", tStateName);
		
		tStateName = operatingRound.getStateName ();
		assertEquals ("Operating Round", tStateName);
		
		tStateName = auctionRound.getStateName ();
		assertEquals ("Auction Round", tStateName);
		
		tStateName = formationRound.getStateName ();
		assertEquals ("Formation Round", tStateName);
		
		tStateName = contractBidRound.getStateName ();
		assertEquals ("Contract Bid Round", tStateName);
	}

	@Test
	@DisplayName ("Testing the calls to gameManager boolean tests")
	void verifyingGameManagerBooleanTests () {
		roundManager.setGameManager (mGameManager);
		roundManager.setOperatingRound (mOperatingRound);
		
		Mockito.when (mGameManager.applyingAction ()).thenReturn (true);
		roundManager.applyingAction ();
		Mockito.verify (mGameManager, times (1)).applyingAction ();
		
		Mockito.when (mBank.canStartOperatingRound ()).thenReturn (true);
		mBank.canStartOperatingRound ();
		Mockito.verify (mBank, times (1)).canStartOperatingRound ();
	}
}
