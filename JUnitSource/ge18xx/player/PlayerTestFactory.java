package ge18xx.player;

import org.mockito.Mockito;

import ge18xx.game.GameManager;

public class PlayerTestFactory {
	GameManager gameManager;

	/**
	 * Basic Constructor that saves the provided Game Manager
	 * 
	 * @param aGameManager The GameManager to save in this Test Factory
	 * 
	 */
	public PlayerTestFactory (GameManager aGameManager) {
		gameManager = aGameManager;
	}

	/**
	 * Build a basic Player Manager, given the stored Game Manager
	 * 
	 * @return a PlayerManager
	 * 
	 */
	public PlayerManager buildPlayerManager () {
		PlayerManager tPlayerManager;

		tPlayerManager = new PlayerManager (gameManager);

		return tPlayerManager;
	}

	/**
	 * Guild a basic PlayerManager Mock that will mock the getPlayerCount and return 3
	 * 
	 * @return the Mocked PlayerManager
	 */
	public PlayerManager buildPlayerManagerMock (int aPlayerCount) {
		PlayerManager mPlayerManager = Mockito.mock (PlayerManager.class);;
		
		Mockito.when (mPlayerManager.getPlayerCount ()).thenReturn (aPlayerCount);
		
		return mPlayerManager;
	}
	
	/**
	 * Build a basic Player Mock with the Provided Name mocked when 'getName' method called
	 * 
	 * @param aName Name to return when 'getName' method is called by mocked object
	 * 
	 * @return the constructed Player Mock
	 * 
	 */
	public Player buildPlayerMock (String aName) {
		Player mPlayer = Mockito.mock (Player.class);

		Mockito.when (mPlayer.getName ()).thenReturn (aName);

		return mPlayer;
	}

}
