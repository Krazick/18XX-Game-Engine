package ge18xx.round;

import org.mockito.Mockito;

import ge18xx.company.CorporationList;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.PlayerManager;

public class RoundTestFactory {

	public RoundTestFactory () {

	}

	public RoundManager buildRoundManager () {
		String tClientName;

		tClientName = "RTFBuster";

		return buildRoundManager (tClientName);
	}

	public RoundManager buildRoundManager (String aClientName) {
		GameManager tGameManager;
		GameTestFactory tGameTestFactory;

		tGameTestFactory = new GameTestFactory ();
		tGameManager = tGameTestFactory.buildGameManager (aClientName);

		return buildRoundManager (tGameManager);
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

}
