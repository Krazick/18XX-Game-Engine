package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.bank.Bank;
import ge18xx.bank.BankTestFactory;
import ge18xx.round.action.ActorI;

@DisplayName ("Corporation Status Update Tests")
class CorporationStatusUpdateTests {
	ShareCompany alphaShareCompany;
	ShareCompany betaShareCompany;
	ShareCompany limaShareCompany;
	PrivateCompany gammaPrivateCompany;
	MinorCompany deltaMinorCompany;
	CompanyTestFactory companyTestFactory;
	BankTestFactory bankTestFactory;
	Bank bank;

	@BeforeEach
	void setUp () throws Exception {
		bankTestFactory = new BankTestFactory ();
		bank = bankTestFactory.buildBank ();
		companyTestFactory = new CompanyTestFactory ();
		alphaShareCompany = companyTestFactory.buildAShareCompany (1);
		betaShareCompany = companyTestFactory.buildAShareCompany (2);
		limaShareCompany = companyTestFactory.buildAShareCompany (3);
		gammaPrivateCompany = companyTestFactory.buildAPrivateCompany (1);
		deltaMinorCompany = companyTestFactory.buildAMinorCompany (1);
	}

	@Test
	@DisplayName ("Update Status from Unowned to next State")
	void testUpdatingUnownedStatus () {
		assertEquals ("Unowned", alphaShareCompany.getStatusName ());
		alphaShareCompany.setStatus (ActorI.ActionStates.Owned);

		assertEquals ("Owned", alphaShareCompany.getStatusName ());
		betaShareCompany.setStatus (ActorI.ActionStates.Unformed);
		assertEquals ("Unformed", betaShareCompany.getStatusName ());

		assertEquals ("Unowned", gammaPrivateCompany.getStatusName ());
		gammaPrivateCompany.setStatus (ActorI.ActionStates.Owned);
		assertEquals ("Owned", gammaPrivateCompany.getStatusName ());
	}

	@Test
	@DisplayName ("Update Status from Owned to next State")
	void testUpdatingOwnedStatus () {
		alphaShareCompany.setStatus (ActorI.ActionStates.Owned);
		alphaShareCompany.setStatus (ActorI.ActionStates.WaitingResponse);
		assertEquals ("Waiting for Response", alphaShareCompany.getStatusName ());
		betaShareCompany.setStatus (ActorI.ActionStates.Owned);
		betaShareCompany.setStatus (ActorI.ActionStates.MayFloat);
		assertEquals ("May Float", betaShareCompany.getStatusName ());

		alphaShareCompany.forceSetStatus (ActorI.ActionStates.Owned);
		alphaShareCompany.setStatus (ActorI.ActionStates.WillFloat);
		assertEquals ("Will Float", alphaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.Owned);
		betaShareCompany.setStatus (ActorI.ActionStates.Closed);
		assertEquals ("Closed", betaShareCompany.getStatusName ());

		alphaShareCompany.forceSetStatus (ActorI.ActionStates.Owned);
		alphaShareCompany.setStatus (ActorI.ActionStates.NotOperated);
		assertNotEquals ("Not Operated", alphaShareCompany.getStatusName ());

		gammaPrivateCompany.setStatus (ActorI.ActionStates.Owned);
		gammaPrivateCompany.setStatus (ActorI.ActionStates.Closed);
		assertEquals ("Closed", gammaPrivateCompany.getStatusName ());

		deltaMinorCompany.setStatus (ActorI.ActionStates.Owned);
		deltaMinorCompany.setStatus (ActorI.ActionStates.Closed);
		assertEquals ("Closed", deltaMinorCompany.getStatusName ());

		deltaMinorCompany.forceSetStatus (ActorI.ActionStates.Owned);
		deltaMinorCompany.setStatus (ActorI.ActionStates.WillFloat);
		assertEquals ("Will Float", deltaMinorCompany.getStatusName ());

		deltaMinorCompany.forceSetStatus (ActorI.ActionStates.Owned);
		deltaMinorCompany.setStatus (ActorI.ActionStates.NotOperated);
		assertEquals ("Not Operated", deltaMinorCompany.getStatusName ());
	}

	@Test
	@DisplayName ("Update Status from MayFloat to next State")
	void testUpdatingMayFloatStatus () {
		limaShareCompany.forceSetStatus (ActorI.ActionStates.MayFloat);
		limaShareCompany.setStatus (ActorI.ActionStates.Owned);
		assertEquals ("Owned", limaShareCompany.getStatusName ());
		
		limaShareCompany.forceSetStatus (ActorI.ActionStates.MayFloat);
		limaShareCompany.setStatus (ActorI.ActionStates.WillFloat);
		assertEquals ("Will Float", limaShareCompany.getStatusName ());
		
		limaShareCompany.forceSetStatus (ActorI.ActionStates.MayFloat);
		limaShareCompany.setStatus (ActorI.ActionStates.NotOperated);
		assertEquals ("Not Operated", limaShareCompany.getStatusName ());

		limaShareCompany.forceSetStatus (ActorI.ActionStates.MayFloat);
		limaShareCompany.setStatus (ActorI.ActionStates.StartedOperations);
		assertNotEquals ("Started Operations", limaShareCompany.getStatusName ());
	}
	
	@Test
	@DisplayName ("Update Status from TileLaid to next State")
	void testUpdatingTileLaidStatus () {
		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileLaid);
		assertEquals ("Tile Laid", betaShareCompany.getStatusName ());
		betaShareCompany.updateStatus (ActorI.ActionStates.StationLaid);
		assertEquals ("Tile and Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.TileAndStationLaid);
		assertEquals ("Tile and Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.TilesLaid);
		assertEquals ("Tiles Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.OperatedTrain);
		assertEquals ("Operated Train", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HandledLoanInterest);
		assertEquals ("Handled Loan Interest", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HoldDividend);
		assertEquals ("No Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HalfDividend);
		assertEquals ("Half Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.FullDividend);
		assertEquals ("Full Dividend Paid", betaShareCompany.getStatusName ());
		
		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.StartedOperations);
		assertEquals ("Tile Laid", betaShareCompany.getStatusName ());
	}
	
	@Test
	@DisplayName ("Update Status from TilesLaid to next State")
	void testUpdatingTilesLaidStatus () {
		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesLaid);
		assertEquals ("Tiles Laid", betaShareCompany.getStatusName ());
		betaShareCompany.updateStatus (ActorI.ActionStates.StationLaid);
		assertEquals ("Tiles and Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.TilesAndStationLaid);
		assertEquals ("Tiles and Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.TilesLaid);
		assertEquals ("Tiles Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.OperatedTrain);
		assertEquals ("Operated Train", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HandledLoanInterest);
		assertEquals ("Handled Loan Interest", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HoldDividend);
		assertEquals ("No Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HalfDividend);
		assertEquals ("Half Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.FullDividend);
		assertEquals ("Full Dividend Paid", betaShareCompany.getStatusName ());
		
		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.StartedOperations);
		assertEquals ("Tiles Laid", betaShareCompany.getStatusName ());
	}
}
