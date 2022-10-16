package ge18xx.player;

import org.mockito.Mockito;

import ge18xx.bank.BankTestFactory;
import ge18xx.game.GameManager;

public class PortfolioTestFactory {
	BankTestFactory bankTestFactory;

	public PortfolioTestFactory () {
		bankTestFactory = new BankTestFactory ();
	}

	public PortfolioTestFactory (BankTestFactory aBankTestFactory) {
		bankTestFactory = aBankTestFactory;
	}
	
	public PortfolioHolderI buildBankPortfolioHolder (GameManager aGameManager) {
		PortfolioHolderI tBankPortfolioHolder;
		
		tBankPortfolioHolder = bankTestFactory.buildBank (aGameManager);
		
		return tBankPortfolioHolder;
	}
	
	public PortfolioHolderI buildBankPortfolioHolderMock (GameManager aGameManager) {
		PortfolioHolderI mBankPortfolioHolder;

		mBankPortfolioHolder = bankTestFactory.buildBankMock (aGameManager);

		return mBankPortfolioHolder;
	}

	public Portfolio buildPortfolio (PortfolioHolderI aHolder) {
		Portfolio tPortfolio;
		
		tPortfolio = new Portfolio (aHolder);
		
		return tPortfolio;
	}

	public Portfolio buildPortfolioMock (PortfolioHolderI aHolder) {
		Portfolio mPortfolio;

		mPortfolio = Mockito.mock (Portfolio.class);
		Mockito.when (mPortfolio.getHolder ()).thenReturn (aHolder);
		Mockito.when (mPortfolio.getName ()).thenReturn ("Portfolio Mock Name");
		
		return mPortfolio;
	}
}
