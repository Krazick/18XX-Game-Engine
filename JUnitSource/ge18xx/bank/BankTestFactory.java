package ge18xx.bank;

import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;

public class BankTestFactory {
	GameTestFactory gameTestFactory;
	GameManager gameManager;
	
	public BankTestFactory () {
		// Nothing really to construct here.
	}

	public Bank buildBank () {
		Bank tBank;
		
		buildGameManager ();
		tBank = buildBank (gameManager);
		
		return tBank;
	}
	
	public void buildGameManager () {
		GameManager tGameManager;
		
		if (gameTestFactory == null) {
			gameTestFactory = new GameTestFactory ();
			tGameManager = gameTestFactory.buildGameManager ();
			setGameManager (tGameManager);
		}
	}
	
	public void setGameManager (GameManager aGameManager) {
		gameManager = aGameManager;
	}
	
	public GameManager getGameManager () {
		return gameManager;
	}
	
	public Bank buildBank (GameManager aGameManager) {
		Bank tBank;

		setGameManager (aGameManager);
		tBank = new Bank (0, gameManager);

		return tBank;
	}

	public Bank buildBankMock () {
		Bank mBank;
		
		buildGameManager ();
		mBank = buildBankMock (gameManager);
		
		return mBank;
	}
	
	public Bank buildBankMock (GameManager aGameManager) {
		Bank mBank;

		setGameManager (aGameManager);
		mBank = Mockito.mock (Bank.class);
		Mockito.when (mBank.getAbbrev ()).thenReturn ("Bank Mock");
		aGameManager.setBank (mBank);
		
		return mBank;
	}
	
	public Bank buildBankMock (GameManager aGameManager, String aBankName) {
		Bank mBank;

		mBank = Mockito.mock (Bank.class);
		Mockito.when (mBank.getAbbrev ()).thenReturn ("Bank Mock");
		Mockito.when (mBank.getName ()).thenReturn (aBankName);
		Mockito.when (mBank.isABank ()).thenReturn (true);
		Mockito.when (mBank.isABankPool ()).thenReturn (false);
		aGameManager.setBank (mBank);
		
		return mBank;
	}
	
	public BankPool buildBankPoolMock (GameManager aGameManager, String aBankPoolName) {
		BankPool mBankPool;

		mBankPool = Mockito.mock (BankPool.class);
		Mockito.when (mBankPool.getAbbrev ()).thenReturn ("Bank Pool Mock");
		Mockito.when (mBankPool.getName ()).thenReturn (aBankPoolName);
		Mockito.when (mBankPool.isABank ()).thenReturn (true);
		Mockito.when (mBankPool.isABankPool ()).thenReturn (true);
		
		aGameManager.setBankPool (mBankPool);
		
		return mBankPool;
	}

	public GameBank buildGameBank (GameManager aGameManager) {
		return buildGameBank ("Test Game Bank", aGameManager);
	}

	public GameBank buildGameBank (String aGameBankName, GameManager aGameManager) {
		GameBank tGameBank;

		tGameBank = new GameBank (aGameBankName, aGameManager);

		return tGameBank;
	}

	public GameBank buildGameBankMock (GameManager mGameManager) {
		return buildGameBankMock ("Mock Game Bank", mGameManager);
	}

	public GameBank buildGameBankMock (String aGameBankNameMock, GameManager mGameManager) {
		GameBank mGameBank;

		mGameBank = Mockito.mock (Bank.class);
		Mockito.when (mGameBank.getAbbrev ()).thenReturn ("Bank Mock");

		return mGameBank;
	}

	public BankPool buildBankPool (GameManager aGameManager) {
		BankPool tBankPool;

		tBankPool = new BankPool (aGameManager);

		return tBankPool;
	}

	public BankPool buildBankPoolMock (GameManager mGameManager) {
		BankPool mBankPool;

		mBankPool = Mockito.mock (BankPool.class);
		Mockito.when (mBankPool.getAbbrev ()).thenReturn ("Bank Pool Mock");
		
		return mBankPool;
	}
}
