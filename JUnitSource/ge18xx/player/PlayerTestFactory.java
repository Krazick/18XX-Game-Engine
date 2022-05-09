package ge18xx.player;

import ge18xx.game.GameManager;

public class PlayerTestFactory {
	GameManager gameManager;

	public PlayerTestFactory (GameManager aGameManager) {
		gameManager = aGameManager;
	}

	public PlayerManager buildPlayerManager () {
		PlayerManager tPlayerManager;

		tPlayerManager = new PlayerManager (gameManager);

		return tPlayerManager;
	}

}
