package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.company.TokenInfo.TokenType;
import ge18xx.round.action.ActorI;

@DisplayName ("Token Company Tests")
class TokenCompanyTests {
	CompanyTestFactory companyTestFactory;
	TokenCompany tokenCompany;
	TokenCompany tokenCompany2;

	@BeforeEach
	void setUp () throws Exception {
		companyTestFactory = new CompanyTestFactory ();
		tokenCompany = companyTestFactory.buildATokenCompany (1);
		tokenCompany2 = companyTestFactory.buildATokenCompany (2);
	}

	@AfterEach
	void tearDown () throws Exception {
	}

	
	@Test
	@DisplayName ("Test fetching a Market Token for this Company")
	void fetchMarketTokenTest () {
		Token tMarketToken;

		assertEquals ("Token Count: 4", tokenCompany.getTokenLabel ());
		tMarketToken = tokenCompany.getMarketToken ();
		assertEquals (tMarketToken.getCorporationID (), 991);
		assertFalse (tMarketToken instanceof MapToken);
		assertTrue (tMarketToken instanceof Token);
	}
	
	@Test
	@DisplayName ("Test adding a Map Token for this Company")
	void addingMapTokenTest () {
		Token tFetchedToken;
		
		assertEquals (4, tokenCompany.getTokenCount ());
		assertEquals ("Token Count: 4", tokenCompany.getTokenLabel ());
		
		tFetchedToken = tokenCompany.getMapToken ();
		assertTrue (tFetchedToken.isAMapToken ());
		assertTrue (tFetchedToken instanceof MapToken);
	}
	
	@Test
	@DisplayName ("Test all Tokens are good")
	void validateAllTokensTest () {
		int tTokenCount;
		int tTokenIndex;
		Token tFetchedToken;
		
		tTokenCount = tokenCompany2.getTokenCount ();
		assertEquals (9, tTokenCount);
		for (tTokenIndex = 0; tTokenIndex < tTokenCount; tTokenIndex++) {
			tFetchedToken = tokenCompany2.getTokenAt (tTokenIndex);
			if (tTokenIndex == 0) {
				assertEquals (TokenType.MARKET, tFetchedToken.getTokenType ());
				assertEquals (tTokenIndex, tFetchedToken.getTokenIndex ());
				assertEquals ("TTBNO", tFetchedToken.getCorporationAbbrev ());
			} else if (tTokenIndex == 1){
				assertEquals (TokenType.HOME1, tFetchedToken.getTokenType ());
				assertEquals (tTokenIndex, tFetchedToken.getTokenIndex ());
				assertEquals ("TTBNO", tFetchedToken.getCorporationAbbrev ());
			} else if (tTokenIndex > 1){
				assertEquals (TokenType.FIXED_COST, tFetchedToken.getTokenType ());
				assertEquals (tTokenIndex, tFetchedToken.getTokenIndex ());
				assertEquals ("TTBNO", tFetchedToken.getCorporationAbbrev ());
			} 
		}
	}
	
	@Test
	@DisplayName ("Test various 'isA<something>' method")
	void corporationIsAMethodTests () {
		assertFalse (tokenCompany.isAPlayer ());
		assertFalse (tokenCompany.isAPrivateCompany ());
		assertTrue (tokenCompany.isATrainCompany ());
		assertTrue (tokenCompany.isATokenCompany ());
		assertFalse (tokenCompany.isAMinorCompany ());
		assertFalse (tokenCompany.isGovtRailway ());
		assertFalse (tokenCompany.isAShareCompany ());
		assertFalse (tokenCompany.isABank ());
		assertFalse (tokenCompany.isABankPool ());
	}

	@Test
	@DisplayName ("Valid for CanLayToken Method")
	void canLayTokenTests () {
		tokenCompany.setAbbrev ("TRC");
		tokenCompany.setName ("Token Railway Company");

		tokenCompany.resetStatus (ActorI.ActionStates.StartedOperations);
		assertTrue (tokenCompany.canLayToken ());

		tokenCompany.resetStatus (ActorI.ActionStates.Unowned);
		assertFalse (tokenCompany.canLayToken ());

		tokenCompany.resetStatus (ActorI.ActionStates.StartedOperations);
		assertTrue (tokenCompany.canLayToken ());

		tokenCompany.resetStatus (ActorI.ActionStates.Operated);
		assertFalse (tokenCompany.canLayToken ());

		tokenCompany.resetStatus (ActorI.ActionStates.StationLaid);
		assertTrue (tokenCompany.canLayToken ());

		tokenCompany.resetStatus (ActorI.ActionStates.TileLaid);
		assertTrue (tokenCompany.canLayToken ());

		tokenCompany.resetStatus (ActorI.ActionStates.Tile2Laid);
		assertTrue (tokenCompany.canLayToken ());

		tokenCompany.resetStatus (ActorI.ActionStates.TileUpgraded);
		assertTrue (tokenCompany.canLayToken ());

		tokenCompany.resetStatus (ActorI.ActionStates.TileAndStationLaid);
		assertTrue (tokenCompany.canLayToken ());

		tokenCompany.resetStatus (ActorI.ActionStates.OperatedTrain);
		assertFalse (tokenCompany.canLayToken ());

		tokenCompany.resetStatus (ActorI.ActionStates.HoldDividend);
		assertFalse (tokenCompany.canLayToken ());

		tokenCompany.resetStatus (ActorI.ActionStates.HalfDividend);
		assertFalse (tokenCompany.canLayToken ());

		tokenCompany.resetStatus (ActorI.ActionStates.FullDividend);
		assertFalse (tokenCompany.canLayToken ());

		tokenCompany.resetStatus (ActorI.ActionStates.BoughtTrain);
		assertFalse (tokenCompany.canLayToken ());

		tokenCompany.resetStatus (ActorI.ActionStates.Closed);
		assertFalse (tokenCompany.canLayToken ());
	}
}
