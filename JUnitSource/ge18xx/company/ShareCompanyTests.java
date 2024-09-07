package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankTestFactory;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioTestFactory;

@DisplayName ("Share Company Class Tests")
class ShareCompanyTests {
	BankTestFactory bankTestFactory;
	CompanyTestFactory companyTestFactory;
	PortfolioTestFactory portfolioTestFactory;
	ShareCompany shareCompany;
	Portfolio mPortfolio;
	Bank bank;
	
	@BeforeEach
	void setUp () throws Exception {
		bankTestFactory = new BankTestFactory ();
		bank = bankTestFactory.buildBank ();
		companyTestFactory = new CompanyTestFactory ();
		portfolioTestFactory = new PortfolioTestFactory ();
		shareCompany = companyTestFactory.buildAShareCompany (1);
		mPortfolio = portfolioTestFactory.buildPortfolioMock (shareCompany);
		shareCompany.setCorporationCertificates (mPortfolio);
	}

	@Test
	@DisplayName ("Get Percent Owned Tests")
	void getPercentOwnedTests () {
		Mockito.when (mPortfolio.getPercentOwned ()).thenReturn (20);
		Mockito.when (mPortfolio.getPlayerOrCorpOwnedPercentageFor (any (Corporation.class))).thenReturn (30);
		assertEquals (20, shareCompany.getPercentOwned ());
		assertEquals (2, shareCompany.getSharesOwned ());
		assertEquals (30, shareCompany.getPlayerOrCorpOwnedPercentage ());
		assertEquals (3, shareCompany.getSharesSold ());
		
		
		
		Mockito.when (mPortfolio.getPercentOwned ()).thenReturn (60);
		assertEquals (60, shareCompany.getPercentOwned ());
		assertEquals (6, shareCompany.getSharesOwned ());
	}
}
