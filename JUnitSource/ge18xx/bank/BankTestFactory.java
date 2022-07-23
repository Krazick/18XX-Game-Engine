package ge18xx.bank;

import org.mockito.Mockito;

import ge18xx.game.GameManager;

public class BankTestFactory {

	public BankTestFactory () {
		// Nothing really to construct here.
	}

	public Bank buildBank (GameManager aGameManager) {
		Bank tBank;
		
		tBank = new Bank (0, aGameManager);
		
		return tBank;
	}
	
	public Bank buildBankMock (GameManager mGameManager) {
		Bank mBank;

		mBank = Mockito.mock (Bank.class);
		Mockito.when (mBank.getAbbrev ()).thenReturn ("Bank Mock");

		return mBank;
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
