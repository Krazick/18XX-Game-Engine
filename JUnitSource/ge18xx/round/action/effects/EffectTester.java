package ge18xx.round.action.effects;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import ge18xx.company.CompanyTestFactory;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.PlayerTestFactory;
import ge18xx.round.RoundManager;
import ge18xx.round.RoundTestFactory;
import ge18xx.round.StockRound;

class EffectTester {
	GameTestFactory gameTestFactory;
	GameManager mGameManager;
	PlayerTestFactory playerTestFactory;
	PlayerManager mPlayerManager;
	Player mPlayerActorAlpha;
	Player mPlayerActorBeta;
	CompanyTestFactory companyTestFactory;
	ShareCompany mShareCompanyGreen;
	RoundTestFactory roundTestFactory;
	RoundManager mRoundManager;
	StockRound mStockRound;

	@BeforeAll
	void factorySetup () {
		gameTestFactory = new GameTestFactory ();
		roundTestFactory = new RoundTestFactory ();
		companyTestFactory = new CompanyTestFactory (gameTestFactory);
		
		mGameManager = gameTestFactory.buildGameManagerMock ();

	}

	@BeforeEach
	void setUp () throws Exception {
		String tPlayer1Name;
		String tPlayer2Name;

		tPlayer1Name = "FromEffectTesterAlpha";
		tPlayer2Name = "ToEffectTesterBeta";

		playerTestFactory = new PlayerTestFactory (mGameManager);
		mPlayerManager = playerTestFactory.buildPlayerManagerMock (2);
		
		mPlayerActorAlpha = playerTestFactory.buildPlayerMock (tPlayer1Name);
		mPlayerActorBeta = playerTestFactory.buildPlayerMock (tPlayer2Name);

		mRoundManager = roundTestFactory.buildRoundManagerMock ();
		Mockito.when (mRoundManager.getGameManager ()).thenReturn (mGameManager);
		Mockito.when (mGameManager.getPlayerManager ()).thenReturn (mPlayerManager);
		Mockito.when (mGameManager.getRoundManager ()).thenReturn (mRoundManager);
		
		mShareCompanyGreen = companyTestFactory.buildShareCompanyMock ();
		
		mStockRound = roundTestFactory.buildStockRoundMock (mPlayerManager, mRoundManager);
		
	}

}
