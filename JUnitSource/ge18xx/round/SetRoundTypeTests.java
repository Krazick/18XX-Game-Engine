package ge18xx.round;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankTestFactory;
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

	@BeforeEach
	void setUp () throws Exception {
		String tClientName;
		PlayerInputFrame mPlayerInputFrame;
		RoundFrame mRoundFrame;

		tClientName = "RMTestBuster";
		gameTestFactory = new GameTestFactory ();
		gameManager = gameTestFactory.buildGameManager (tClientName);
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

		roundManager.setRoundTypeTo (ActorI.ActionStates.FormationRound);
		tCurrentRoundType = roundManager.getCurrentRoundType ();
		assertEquals (ActorI.ActionStates.FormationRound, tCurrentRoundType);

		roundManager.setRoundTypeTo (ActorI.ActionStates.ContractBidRound);
		tCurrentRoundType = roundManager.getCurrentRoundType ();
		assertEquals (ActorI.ActionStates.ContractBidRound, tCurrentRoundType);
	}
	
	@Test
	@DisplayName ("Verifying the getRoundType method")
	void verifyingGetRoundTypes () {
		String tRoundType;
		
		roundManager.setRoundToStockRound ();
		tRoundType = roundManager.getRoundType ();
		assertEquals ("Stock Round", tRoundType);
		
		roundManager.setRoundToOperatingRound ();
		tRoundType = roundManager.getRoundType ();
		assertEquals ("Operating Round", tRoundType);
		
		roundManager.setRoundToAuctionRound ();
		tRoundType = roundManager.getRoundType ();
		assertEquals ("Auction Round", tRoundType);
		
		roundManager.setRoundToFormationRound ();
		tRoundType = roundManager.getRoundType ();
		assertEquals ("Formation Round", tRoundType);
		
		roundManager.setRoundToContractBidRound ();
		tRoundType = roundManager.getRoundType ();
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
		
		Mockito.when (mGameManager.canStartOperatingRound ()).thenReturn (true);
		roundManager.canStartOperatingRound ();
		Mockito.verify (mGameManager, times (1)).canStartOperatingRound ();

		Mockito.when (mOperatingRound.roundIsDone ()).thenReturn (true);
		roundManager.operatingRoundIsDone ();
		Mockito.verify (mOperatingRound, times (1)).roundIsDone ();
	}
}
