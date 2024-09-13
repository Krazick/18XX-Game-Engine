package ge18xx.round;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

import org.mockito.Mockito;

import ge18xx.company.CorporationList;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.PlayerManager;

public class RoundTestFactory {
	GameManager gameManager;
	GameTestFactory gameTestFactory;
	String clientName;

	public RoundTestFactory () {

	}

	public RoundManager buildRoundManager () {
		clientName = "RTFBuster";

		return buildRoundManager (clientName);
	}

	public RoundManager buildRoundManager (String aClientName) {
		gameTestFactory = new GameTestFactory ();
		gameManager = gameTestFactory.buildGameManager (aClientName);

		return buildRoundManager (gameManager);
	}

	public RoundManager buildRoundManager (GameManager aGameManager) {
		return buildRoundManager (aGameManager, PlayerManager.NO_PLAYER_MANAGER);
	}

	public RoundManager buildRoundManager (GameManager aGameManager, PlayerManager aPlayerManager) {
		RoundManager roundManager;
		
		roundManager = new RoundManager (aGameManager, aPlayerManager);
		
		return roundManager;
	}

	public RoundManager buildRoundManagerMock () {
		RoundManager mRoundManager = Mockito.mock (RoundManager.class);

		return mRoundManager;
	}

	public StockRound buildStockRound (PlayerManager aPlayerManager, RoundManager aRoundManager) {
		StockRound tStockRound;

		tStockRound = new StockRound (aPlayerManager, aRoundManager);

		return tStockRound;
	}
	
	public StockRound buildStockRoundMock (PlayerManager aPlayerManager, RoundManager aRoundManager) {
		StockRound mStockRound;

		mStockRound = Mockito.mock (StockRound.class);
		Mockito.when (mStockRound.getType ()).thenReturn ("Stock Round Mock");
		Mockito.when (mStockRound.getID ()).thenReturn ("1");

		return mStockRound;
	}

	public OperatingRound buildOperatingRound (RoundManager aRoundManager) {
		OperatingRound tOperatingRound;
		CorporationList tPrivatesNull, tMinorsNull, tSharesNull;

		tPrivatesNull = CorporationList.NO_CORPORATION_LIST;
		tMinorsNull = CorporationList.NO_CORPORATION_LIST;
		tSharesNull = CorporationList.NO_CORPORATION_LIST;

		tOperatingRound = new OperatingRound (aRoundManager, tPrivatesNull, tMinorsNull, tSharesNull);

		return tOperatingRound;
	}
	
	public OperatingRound buildOperatingRoundMock (PlayerManager aPlayerManager, RoundManager aRoundManager) {
		OperatingRound mOperatingRound;

		mOperatingRound = Mockito.mock (OperatingRound.class);
		Mockito.when (mOperatingRound.getType ()).thenReturn ("Operating Round Mock");

		return mOperatingRound;
	}

	public AuctionRound buildAuctionRound (RoundManager aRoundManager) {
		AuctionRound tAuctionRound;

		tAuctionRound = new AuctionRound (aRoundManager);

		return tAuctionRound;
	}
	
	public AuctionRound buildAuctionRoundMock (PlayerManager aPlayerManager, RoundManager aRoundManager) {
		AuctionRound mAuctionRound;

		mAuctionRound = Mockito.mock (AuctionRound.class);
		Mockito.when (mAuctionRound.getType ()).thenReturn ("Auction Round Mock");

		return mAuctionRound;
	}

	public RoundFrame buildRoundFrame () {
		RoundManager tRoundManager;
		RoundFrame tRoundFrame;
		String tTitle;
		
		gameTestFactory = new GameTestFactory ();
		gameManager = gameTestFactory.buildGameManager ();
		tTitle = "RoundTestFactory Title";
		tRoundManager = buildRoundManager ();
		
		tRoundFrame = new RoundFrame (tTitle, tRoundManager, gameManager);
		
		return tRoundFrame;
	}
	
	public RoundFrame buildRoundFrameMock () {
		RoundFrame mRoundFrame;
		
		mRoundFrame = Mockito.mock (RoundFrame.class);
		doNothing ().when (mRoundFrame).setStockRoundInfo (anyString (), anyInt ());

		return mRoundFrame;
	}
}
