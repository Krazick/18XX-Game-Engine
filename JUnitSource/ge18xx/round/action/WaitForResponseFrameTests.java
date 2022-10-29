package ge18xx.round.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.PlayerTestFactory;

class WaitForResponseFrameTests {
	PlayerTestFactory playerTestFactory;
	GameTestFactory gameTestFactory;
	GameManager mGameManager;
	PlayerManager mPlayerManager;
	Player mPlayerAlpha;
	Player mPlayerBeta;
	WaitForReponseFrame waitForReponseFrame;
	
	@BeforeEach
	void setUp () throws Exception {
		Point tTestPoint;
		ActorI.ActionStates tWaitState;
		ActorI.ActionStates tNoWaitState;
		
		gameTestFactory = new GameTestFactory ();
		mGameManager = gameTestFactory.buildGameManagerMock ();
		playerTestFactory = new PlayerTestFactory (mGameManager);
		mPlayerManager = playerTestFactory.buildPlayerManagerMock (3);
		Mockito.when (mGameManager.getPlayerManager ()).thenReturn (mPlayerManager);

		tTestPoint = new Point (200, 200);
		Mockito.when (mPlayerManager.getOffsetRoundFramePoint ()).thenReturn (tTestPoint);
		
		mPlayerAlpha = playerTestFactory.buildPlayerMock ("Alpha Asking");
		mPlayerBeta = playerTestFactory.buildPlayerMock ("Beta Responding");
		Mockito.when (mPlayerAlpha.getGameManager ()).thenReturn (mGameManager);
		
		tWaitState = ActorI.ActionStates.WaitingResponse;
		tNoWaitState = ActorI.ActionStates.Pass;
		
		Mockito.when (mPlayerAlpha.getPrimaryActionState ()).thenReturn (tWaitState);
		Mockito.when (mPlayerBeta.getGameManager ()).thenReturn (mGameManager);
		Mockito.when (mPlayerBeta.getPrimaryActionState ()).thenReturn (tNoWaitState);
		
		waitForReponseFrame = new WaitForReponseFrame ("Test Waiting for Response Frame ", mPlayerBeta, mPlayerAlpha);
		System.out.println ("Wait For Response Frame setup");
	}

	@Test
	@DisplayName ("Show Waiting for Response Frame Test - 1 Second")
	void showWaitingForResponseFrameTest () throws InterruptedException {
		Point tFoundPoint;
		
		tFoundPoint = new Point (200, 200);
		assertEquals (tFoundPoint, mPlayerManager.getOffsetRoundFramePoint ());
		System.out.println ("Running in Test");
		waitForReponseFrame.showFrame ();
		Thread.sleep (1000);
		waitForReponseFrame.hideFrame ();
	}
	
	@Test
	@DisplayName ("Test is Player Waiting for a Response")
	void isWaitingResponseTest () {
		assertTrue (waitForReponseFrame.isWaitingForResponse ());
	}
}
