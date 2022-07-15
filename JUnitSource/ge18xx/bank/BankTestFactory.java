package ge18xx.bank;

import org.mockito.Mockito;

import ge18xx.game.GameManager;

public class BankTestFactory {

	public BankTestFactory () {
		// TODO Auto-generated constructor stub
	}

	public Bank buildBank (GameManager aGameManager) {
		Bank tBank;
		
		tBank = new Bank (0, aGameManager);
		
		return tBank;
	}
	
	public Bank buildBankMock (GameManager mGameManager) {
		Bank mBank = Mockito.mock (Bank.class);

		Mockito.when (mBank.getAbbrev ()).thenReturn ("Bank Mock");

		return mBank;
	}

}
