package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.round.action.ActorI;

@DisplayName ("Token Company Tests")
class TokenCompanyTests {
	CompanyTestFactory companyTestFactory;
	TokenCompany tokenCompany;

	@BeforeEach
	void setUp () throws Exception {
		companyTestFactory = new CompanyTestFactory ();
		tokenCompany = companyTestFactory.buildTokenCompanyConcrete (Corporation.NO_ID, "Token Test Company, No ID");
	}

	@AfterEach
	void tearDown () throws Exception {
	}

	
	@Test
	@DisplayName ("Test fetching a Market Token for this Company")
	void fetchMarketTokenTest () {
		
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
		MapToken tMapToken;

		tMapToken = new MapToken ();
		tokenCompany.setAbbrev ("TRC");
		tokenCompany.setName ("Token Railway Company");

		tokenCompany.resetStatus (ActorI.ActionStates.StartedOperations);
		assertTrue (tokenCompany.canLayToken ());

		tokenCompany.addMapToken (tMapToken);
		tokenCompany.addMapToken (tMapToken);
		assertEquals ("Token Count: 3", tokenCompany.getTokenLabel ());

		assertEquals (3, tokenCompany.getTokenCount ());
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
