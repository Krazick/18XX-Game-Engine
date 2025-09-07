package ge18xx.round.action.effects;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.PlayerTestFactory;
import ge18xx.round.RoundManager;
import ge18xx.round.RoundTestFactory;

class EffectTester {
	GameTestFactory gameTestFactory;
	GameManager mGameManager;
	PlayerTestFactory playerTestFactory;
	PlayerManager mPlayerManager;
	Player mPlayerActorAlpha;
	Player mPlayerActorBeta;
	RoundTestFactory roundTestFactory;
	RoundManager mRoundManager;

	@BeforeEach
	void setUp () throws Exception {
		String tPlayer1Name;
		String tPlayer2Name;

		gameTestFactory = new GameTestFactory ();
		mGameManager = gameTestFactory.buildGameManagerMock ();
		tPlayer1Name = "FromEffectTesterAlpha";
		tPlayer2Name = "ToEffectTesterBeta";

		playerTestFactory = new PlayerTestFactory (mGameManager);
		mPlayerManager = playerTestFactory.buildPlayerManagerMock (2);
		
		mPlayerActorAlpha = playerTestFactory.buildPlayerMock (tPlayer1Name);
		mPlayerActorBeta = playerTestFactory.buildPlayerMock (tPlayer2Name);

		roundTestFactory = new RoundTestFactory ();
		mRoundManager = roundTestFactory.buildRoundManagerMock ();
		Mockito.when (mRoundManager.getGameManager ()).thenReturn (mGameManager);
	
		Mockito.when (mGameManager.getPlayerManager ()).thenReturn (mPlayerManager);
		Mockito.when (mGameManager.getRoundManager ()).thenReturn (mRoundManager);
	}

}
