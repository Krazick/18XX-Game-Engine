package ge18xx.player;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.game.Config;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.round.action.ActorI;

class PlayerActionStateTests {

	Player player;
	GameManager gameManager;
	GameTestFactory gameTestFactory;
	PlayerTestFactory playerTestFactory;
	PlayerManager playerManager;
	
	@BeforeEach
	void setUp () throws Exception {
		String tName;
		int tCash;
		GameInfo tGameInfo;
		Config tConfigData;
		
		gameTestFactory = new GameTestFactory ();
		gameManager = gameTestFactory.buildGameManager ();
		tGameInfo = gameTestFactory.buildGameInfo ();
		gameManager.setGame (tGameInfo);
		tConfigData = new Config (gameManager);
		gameManager.setConfigData (tConfigData);
		
		playerTestFactory = new PlayerTestFactory (gameManager);
		playerManager = playerTestFactory.buildPlayerManager ();
		tName = "AlphaTester";
		tCash = 1000;
		player = playerTestFactory.buildPlayer (tName, playerManager, tCash);
	}

	@Test
	@DisplayName ("Basic State Tests")
	void basicStateTests () {
		assertEquals (player.getStateName (), "No Action");
		assertTrue (player.primaryActionState.canChangeState (ActorI.ActionStates.Pass));
		player.setPrimaryActionState (ActorI.ActionStates.Pass);
		
		assertEquals (player.getStateName (), "Passed");
		assertFalse (player.primaryActionState.canChangeState (ActorI.ActionStates.Pass));
		assertTrue (player.primaryActionState.canChangeState (ActorI.ActionStates.NoAction));

	}

}
