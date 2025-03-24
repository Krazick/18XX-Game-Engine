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
import ge18xx.game.GameTestFactory;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioTestFactory;
import ge18xx.round.action.ActorI;

@DisplayName ("Share Company Class Tests")
class ShareCompanyTests {
	BankTestFactory bankTestFactory;
	GameTestFactory gameTestFactory;
	CompanyTestFactory companyTestFactory;
	PortfolioTestFactory portfolioTestFactory;
	ShareCompany noDestinationShareCompany;
	ShareCompany destinationShareCompany;
	ShareCompany unformedShareCompany;
	Portfolio mNoDestinationPortfolio;
	Portfolio mDestinationPortfolio;
	Portfolio mUnformedPortfolio;
	Bank bank;
	
	@BeforeEach
	void setUp () throws Exception {
		gameTestFactory = new GameTestFactory ();
		bankTestFactory = new BankTestFactory ();
		bank = bankTestFactory.buildBank ();
		companyTestFactory = new CompanyTestFactory (gameTestFactory);
		portfolioTestFactory = new PortfolioTestFactory ();
		
		noDestinationShareCompany = companyTestFactory.buildAShareCompany (1);
		mNoDestinationPortfolio = portfolioTestFactory.buildPortfolioMock (noDestinationShareCompany);
		noDestinationShareCompany.setCorporationCertificates (mNoDestinationPortfolio);
		
		destinationShareCompany = companyTestFactory.buildAShareCompany (3);
		mDestinationPortfolio = portfolioTestFactory.buildPortfolioMock (destinationShareCompany);
		destinationShareCompany.setCorporationCertificates (mDestinationPortfolio);
		
		unformedShareCompany = companyTestFactory.buildAShareCompany (4);
		mUnformedPortfolio = portfolioTestFactory.buildPortfolioMock (unformedShareCompany);
		unformedShareCompany.setCorporationCertificates (mUnformedPortfolio);
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
	
	@Test
	@DisplayName ("Get Capitalization Level for Non-Destination Company Tests")
	void getNDShareCompanyCapitalizationLevelTests () {
		CorporationList mCorporationList;
		
		mCorporationList = companyTestFactory.getCorporationListMock ();
		Mockito.when (mCorporationList.getCapitalizationLevel (6)).thenReturn (10);
		noDestinationShareCompany.setCorporationList (mCorporationList);
		Mockito.when (mNoDestinationPortfolio.getPlayerOrCorpOwnedPercentageFor (noDestinationShareCompany)).thenReturn (60);
		assertEquals (10, noDestinationShareCompany.getCapitalizationLevel ());
		
		Mockito.when (mCorporationList.getCapitalizationLevel (3)).thenReturn (0);
		Mockito.when (mNoDestinationPortfolio.getPlayerOrCorpOwnedPercentageFor (noDestinationShareCompany)).thenReturn (30);
		assertEquals (0, noDestinationShareCompany.getCapitalizationLevel ());
	}
	
	@Test
	@DisplayName ("Get Capitalization Level for Destination Company Tests")
	void getDShareCompanyCapitalizationLevelTests () {
		CorporationList mDCorporationList;
		
		mDCorporationList = companyTestFactory.getCorporationListMock ();
		Mockito.when (mDCorporationList.getCapitalizationLevel (6)).thenReturn (10);
		destinationShareCompany.setCorporationList (mDCorporationList);
		Mockito.when (mDestinationPortfolio.getPlayerOrCorpOwnedPercentageFor (destinationShareCompany)).thenReturn (60);
		assertEquals (10, destinationShareCompany.getCapitalizationLevel ());
		
		Mockito.when (mDCorporationList.getCapitalizationLevel (2)).thenReturn (2);
		Mockito.when (mDestinationPortfolio.getPlayerOrCorpOwnedPercentageFor (destinationShareCompany)).thenReturn (20);
		assertEquals (2, destinationShareCompany.getCapitalizationLevel ());
		
		Mockito.when (mDCorporationList.getCapitalizationLevel (3)).thenReturn (3);
		Mockito.when (mDestinationPortfolio.getPlayerOrCorpOwnedPercentageFor (destinationShareCompany)).thenReturn (30);
		assertEquals (3, destinationShareCompany.getCapitalizationLevel ());

		Mockito.when (mDCorporationList.getCapitalizationLevel (4)).thenReturn (4);
		Mockito.when (mDestinationPortfolio.getPlayerOrCorpOwnedPercentageFor (destinationShareCompany)).thenReturn (40);
		assertEquals (4, destinationShareCompany.getCapitalizationLevel ());
		
		Mockito.when (mDCorporationList.getCapitalizationLevel (5)).thenReturn (5);
		Mockito.when (mDestinationPortfolio.getPlayerOrCorpOwnedPercentageFor (destinationShareCompany)).thenReturn (50);
		assertEquals (5, destinationShareCompany.getCapitalizationLevel ());
	}
	
	@Test
	@DisplayName ("Calculating Starting Treasury Tests")
	void calculatingNDShareCompanyTreasuryTests () {
		CorporationList mCorporationList;
		
		mCorporationList = companyTestFactory.getCorporationListMock ();
		Mockito.when (mCorporationList.getCapitalizationLevel (6)).thenReturn (10);
		noDestinationShareCompany.setCorporationList (mCorporationList);
		noDestinationShareCompany.setParPrice (90);
		Mockito.when (mNoDestinationPortfolio.getPlayerOrCorpOwnedPercentageFor (noDestinationShareCompany)).thenReturn (60);
		assertEquals (900, noDestinationShareCompany.calculateStartingTreasury ());
		
		Mockito.when (mCorporationList.getCapitalizationLevel (3)).thenReturn (0);
		noDestinationShareCompany.setParPrice (82);
		Mockito.when (mNoDestinationPortfolio.getPlayerOrCorpOwnedPercentageFor (noDestinationShareCompany)).thenReturn (30);
		assertEquals (0, noDestinationShareCompany.calculateStartingTreasury ());
	}
	
	@Test
	@DisplayName ("Calculating Starting Treasry for Destination Company Tests")
	void calculatingDShareCompanyStartingTreasuryTests () {
		CorporationList mDCorporationList;
		
		mDCorporationList = companyTestFactory.getCorporationListMock ();
		Mockito.when (mDCorporationList.getCapitalizationLevel (6)).thenReturn (10);
		destinationShareCompany.setCorporationList (mDCorporationList);
		destinationShareCompany.setParPrice (100);
		Mockito.when (mDestinationPortfolio.getPlayerOrCorpOwnedPercentageFor (destinationShareCompany)).thenReturn (60);
		assertEquals (0, destinationShareCompany.calculateStartingTreasury ());
		destinationShareCompany.setDestinationCapitalizationLevel (5);
		assertEquals (500, destinationShareCompany.calculateStartingTreasury ());
		
		Mockito.when (mDCorporationList.getCapitalizationLevel (2)).thenReturn (2);
		destinationShareCompany.setDestinationCapitalizationLevel (2);
		destinationShareCompany.setParPrice (75);
		Mockito.when (mDestinationPortfolio.getPlayerOrCorpOwnedPercentageFor (destinationShareCompany)).thenReturn (20);
		assertEquals (150, destinationShareCompany.calculateStartingTreasury ());
	}
	
	@Test
	@DisplayName ("Share Company Float Tests")
	void shareCompanyFloatedTests () {
		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Unowned);
		assertFalse (noDestinationShareCompany.willFloat ());
		assertFalse (noDestinationShareCompany.hasFloated ());
		
		assertFalse (unformedShareCompany.willFloat ());
		assertFalse (unformedShareCompany.hasFloated ());
		
		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Owned);
		assertFalse (noDestinationShareCompany.willFloat ());
		assertFalse (noDestinationShareCompany.hasFloated ());
		
		noDestinationShareCompany.resetStatus (ActorI.ActionStates.MayFloat);
		assertFalse (noDestinationShareCompany.willFloat ());
		assertFalse (noDestinationShareCompany.hasFloated ());

		noDestinationShareCompany.resetStatus (ActorI.ActionStates.WillFloat);
		assertTrue (noDestinationShareCompany.willFloat ());
		assertFalse (noDestinationShareCompany.hasFloated ());

		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Operated);
		assertFalse (noDestinationShareCompany.willFloat ());
		assertTrue (noDestinationShareCompany.hasFloated ());

		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Closed);
		assertFalse (noDestinationShareCompany.willFloat ());
		assertFalse (noDestinationShareCompany.hasFloated ());
	}
	
	@Test
	@DisplayName ("Share Company Is Operational Tests")
	void shareCompanyIsOperationalTests () {
		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Unowned);
		assertFalse (noDestinationShareCompany.isOperational ());
		
		assertFalse (unformedShareCompany.isOperational ());
		
		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Owned);
		assertFalse (noDestinationShareCompany.isOperational ());
		
		noDestinationShareCompany.resetStatus (ActorI.ActionStates.MayFloat);
		assertFalse (noDestinationShareCompany.isOperational ());

		noDestinationShareCompany.resetStatus (ActorI.ActionStates.WillFloat);
		assertFalse (noDestinationShareCompany.isOperational ());

		noDestinationShareCompany.resetStatus (ActorI.ActionStates.NotOperated);
		assertTrue (noDestinationShareCompany.isOperational ());

		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Operated);
		assertTrue (noDestinationShareCompany.isOperational ());

		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Closed);
		assertFalse (noDestinationShareCompany.isOperational ());
	}
	
	@Test
	@DisplayName ("Share Company can Operate Tests")
	void shareCompanyCanOperateTests () {
		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Unowned);
		assertFalse (noDestinationShareCompany.canOperate ());
		
		assertFalse (unformedShareCompany.canOperate ());
		
		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Owned);
		assertFalse (noDestinationShareCompany.canOperate ());
		
		noDestinationShareCompany.resetStatus (ActorI.ActionStates.MayFloat);
		assertTrue (noDestinationShareCompany.canOperate ());

		noDestinationShareCompany.resetStatus (ActorI.ActionStates.WillFloat);
		assertTrue (noDestinationShareCompany.canOperate ());

		noDestinationShareCompany.resetStatus (ActorI.ActionStates.NotOperated);
		assertTrue (noDestinationShareCompany.canOperate ());

		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Operated);
		assertTrue (noDestinationShareCompany.canOperate ());

		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Closed);
		assertFalse (noDestinationShareCompany.canOperate ());
	}
	
	@Test
	@DisplayName ("Share Company has Operated Tests")
	void shareCompanyHasOperatedTests () {
		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Unowned);
		assertFalse (noDestinationShareCompany.hasOperated ());
		
		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Owned);
		assertFalse (noDestinationShareCompany.hasOperated ());
		
		noDestinationShareCompany.resetStatus (ActorI.ActionStates.MayFloat);
		assertFalse (noDestinationShareCompany.hasOperated ());

		noDestinationShareCompany.resetStatus (ActorI.ActionStates.WillFloat);
		assertFalse (noDestinationShareCompany.hasOperated ());

		noDestinationShareCompany.resetStatus (ActorI.ActionStates.NotOperated);
		assertFalse (noDestinationShareCompany.hasOperated ());

		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Operated);
		assertTrue (noDestinationShareCompany.hasOperated ());

		noDestinationShareCompany.resetStatus (ActorI.ActionStates.Closed);
		assertFalse (noDestinationShareCompany.hasOperated ());
	}
}
