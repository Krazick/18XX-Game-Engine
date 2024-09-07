package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
	ShareCompany noDestinationShareCompany;
	ShareCompany destinationShareCompany;
	Portfolio mNoDestinationPortfolio;
	Portfolio mDestinationPortfolio;
	Bank bank;
	
	@BeforeEach
	void setUp () throws Exception {
		bankTestFactory = new BankTestFactory ();
		bank = bankTestFactory.buildBank ();
		companyTestFactory = new CompanyTestFactory ();
		portfolioTestFactory = new PortfolioTestFactory ();
		noDestinationShareCompany = companyTestFactory.buildAShareCompany (1);
		mNoDestinationPortfolio = portfolioTestFactory.buildPortfolioMock (noDestinationShareCompany);
		noDestinationShareCompany.setCorporationCertificates (mNoDestinationPortfolio);
		
		destinationShareCompany = companyTestFactory.buildAShareCompany (3);
		mDestinationPortfolio = portfolioTestFactory.buildPortfolioMock (destinationShareCompany);
		destinationShareCompany.setCorporationCertificates (mDestinationPortfolio);
	}

	@Test
	@DisplayName ("Get Share Company with no Destination attribute Tests")
	void getShareCompanyNDAttributeTests () {
		Mockito.when (mNoDestinationPortfolio.getPercentOwned ()).thenReturn (50);
		Mockito.when (mNoDestinationPortfolio.getPlayerOrCorpOwnedPercentageFor (any (Corporation.class))).thenReturn (70);
		Mockito.when (mNoDestinationPortfolio.getBankPoolPercentage (any (Corporation.class))).thenReturn (0);
		
		assertEquals (50, noDestinationShareCompany.getPercentOwned ());
		assertEquals (5, noDestinationShareCompany.getSharesOwned ());
		assertEquals (70, noDestinationShareCompany.getPlayerOrCorpOwnedPercentage ());
		assertEquals (7, noDestinationShareCompany.getSharesSold ());
		assertEquals (0, noDestinationShareCompany.getSharesInBankPool ());

		Mockito.when (mNoDestinationPortfolio.getBankPoolPercentage (any (Corporation.class))).thenReturn (10);
		assertEquals (1, noDestinationShareCompany.getSharesInBankPool ());

		assertEquals (7, noDestinationShareCompany.getSharesOwnedByPlayerOrCorp ());
		
		assertFalse (noDestinationShareCompany.hasDestination ());
		assertEquals ("NO DESTINATION", noDestinationShareCompany.getDestinationLabel ());
	}
	
	@Test
	@DisplayName ("Get Share Company with Destination attribute Tests")
	void getShareCompanyDAttributeTests () {
		Mockito.when (mDestinationPortfolio.getPercentOwned ()).thenReturn (50);
		Mockito.when (mDestinationPortfolio.getPlayerOrCorpOwnedPercentageFor (any (Corporation.class))).thenReturn (70);
		Mockito.when (mDestinationPortfolio.getBankPoolPercentage (any (Corporation.class))).thenReturn (10);
		Mockito.when (mDestinationPortfolio.getPercentOwned ()).thenReturn (20);
		
		assertEquals (20, destinationShareCompany.getPercentOwned ());
		assertEquals (2, destinationShareCompany.getSharesOwned ());
		assertTrue (destinationShareCompany.hasDestination ());
		assertEquals ("N17", destinationShareCompany.getDestinationLabel ());
	}
}
