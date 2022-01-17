package ge18xx.round;

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
		tGameManager =  tGameTestFactory.buildGameManager (aClientName);
		
		return buildRoundManager (tGameManager);
	}
	
	public RoundManager buildRoundManager (GameManager aGameManager) {
		return buildRoundManager (aGameManager, PlayerManager.NO_PLAYER_MANAGER);
	}
	
	public RoundManager buildRoundManager (GameManager aGameManager, PlayerManager aPlayerManager) {
		return new RoundManager (aGameManager, aPlayerManager);
		
	}
}
