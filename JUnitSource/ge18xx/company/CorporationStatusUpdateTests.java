package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import ge18xx.bank.Bank;
import ge18xx.round.action.ActorI;
import ge18xx.tiles.Tile;

@DisplayName ("Corporation Status Update Tests")
@TestInstance (Lifecycle.PER_CLASS)
class CorporationStatusUpdateTests extends CorporationTester {
	ShareCompany alphaShareCompany;
	ShareCompany betaShareCompany;
	ShareCompany limaShareCompany;
	Corporation gammaPrivateCompany;
	MinorCompany deltaMinorCompany;
	Bank bank;

	@Override
	@BeforeAll
	void factorySetup () {
		super.factorySetup ();
	}
	
	@BeforeEach
	void setUp () throws Exception {
		bank = bankTestFactory.buildBank ();
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
	
	@Test
	@DisplayName ("Update Status from TileAndStationLaid to next State")
	void testUpdatingTileAndStationLaidStatus () {
		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileAndStationLaid);
		assertEquals ("Tile and Station Laid", betaShareCompany.getStatusName ());
		betaShareCompany.updateStatus (ActorI.ActionStates.StationLaid);
		assertEquals ("Tile and Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileAndStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.TilesAndStationLaid);
		assertEquals ("Tiles and Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileAndStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.OperatedTrain);
		assertEquals ("Operated Train", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileAndStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HandledLoanInterest);
		assertEquals ("Handled Loan Interest", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileAndStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HoldDividend);
		assertEquals ("No Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileAndStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HalfDividend);
		assertEquals ("Half Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileAndStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.FullDividend);
		assertEquals ("Full Dividend Paid", betaShareCompany.getStatusName ());
		
		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileAndStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.StartedOperations);
		assertEquals ("Tile and Station Laid", betaShareCompany.getStatusName ());
	}
	
	@Test
	@DisplayName ("Update Status from TilesAndStationLaid to next State")
	void testUpdatingTilesAndStationLaidStatus () {
		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesAndStationLaid);
		assertEquals ("Tiles and Station Laid", betaShareCompany.getStatusName ());
		betaShareCompany.updateStatus (ActorI.ActionStates.StationLaid);
		assertEquals ("Tiles and Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesAndStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.TilesAndStationLaid);
		assertEquals ("Tiles and Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesAndStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.OperatedTrain);
		assertEquals ("Operated Train", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesAndStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HandledLoanInterest);
		assertEquals ("Handled Loan Interest", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesAndStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HoldDividend);
		assertEquals ("No Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesAndStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HalfDividend);
		assertEquals ("Half Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesAndStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.FullDividend);
		assertEquals ("Full Dividend Paid", betaShareCompany.getStatusName ());
		
		betaShareCompany.forceSetStatus (ActorI.ActionStates.TilesAndStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.StartedOperations);
		assertEquals ("Tiles and Station Laid", betaShareCompany.getStatusName ());
	}

	@Test
	@DisplayName ("Update Status from StationLaid to next State")
	void testUpdatingStationLaidStatus () {
		betaShareCompany.forceSetStatus (ActorI.ActionStates.StationLaid);
		assertEquals ("Station Laid", betaShareCompany.getStatusName ());
		betaShareCompany.updateStatus (ActorI.ActionStates.TileLaid);
		assertEquals ("Tile and Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.StationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.StationLaid);
		assertEquals ("Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.StationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.TilesLaid);
		assertEquals ("Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.StationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.TileAndStationLaid);
		assertEquals ("Tile and Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.StationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.OperatedTrain);
		assertEquals ("Operated Train", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.StationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HandledLoanInterest);
		assertEquals ("Handled Loan Interest", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.StationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HoldDividend);
		assertEquals ("No Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.StationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HalfDividend);
		assertEquals ("Half Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.StationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.FullDividend);
		assertEquals ("Full Dividend Paid", betaShareCompany.getStatusName ());
		
		betaShareCompany.forceSetStatus (ActorI.ActionStates.StationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.StartedOperations);
		assertEquals ("Station Laid", betaShareCompany.getStatusName ());
	}
	
	@Test
	@DisplayName ("Update Status from TileUpgraded to next State")
	void testUpdatingTileUpgradedStatus () {
		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgraded);
		assertEquals ("Tile Upgraded", betaShareCompany.getStatusName ());
		betaShareCompany.updateStatus (ActorI.ActionStates.TileLaid);
		assertEquals ("Tile Upgraded", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgraded);
		betaShareCompany.updateStatus (ActorI.ActionStates.TilesLaid);
		assertEquals ("Tile Upgraded", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgraded);
		betaShareCompany.updateStatus (ActorI.ActionStates.TileAndStationLaid);
		assertEquals ("Tile Upgraded", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgraded);
		betaShareCompany.updateStatus (ActorI.ActionStates.TilesAndStationLaid);
		assertEquals ("Tile Upgraded", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgraded);
		betaShareCompany.updateStatus (ActorI.ActionStates.StationLaid);
		assertEquals ("Tile Upgraded Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgraded);
		betaShareCompany.updateStatus (ActorI.ActionStates.TileUpgradedStationLaid);
		assertEquals ("Tile Upgraded Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgraded);
		betaShareCompany.updateStatus (ActorI.ActionStates.OperatedTrain);
		assertEquals ("Operated Train", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgraded);
		betaShareCompany.updateStatus (ActorI.ActionStates.HandledLoanInterest);
		assertEquals ("Handled Loan Interest", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgraded);
		betaShareCompany.updateStatus (ActorI.ActionStates.HoldDividend);
		assertEquals ("No Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgraded);
		betaShareCompany.updateStatus (ActorI.ActionStates.HalfDividend);
		assertEquals ("Half Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgraded);
		betaShareCompany.updateStatus (ActorI.ActionStates.FullDividend);
		assertEquals ("Full Dividend Paid", betaShareCompany.getStatusName ());
		
		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgraded);
		betaShareCompany.updateStatus (ActorI.ActionStates.StartedOperations);
		assertEquals ("Tile Upgraded", betaShareCompany.getStatusName ());
	}
	
	@Test
	@DisplayName ("Update Status from TileUpgradedStationLaid to next State")
	void testUpdatingTileUpgradedStationLaidStatus () {
		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgradedStationLaid);
		assertEquals ("Tile Upgraded Station Laid", betaShareCompany.getStatusName ());
		betaShareCompany.updateStatus (ActorI.ActionStates.TileLaid);
		assertEquals ("Tile Upgraded Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgradedStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.TilesLaid);
		assertEquals ("Tile Upgraded Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgradedStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.TileAndStationLaid);
		assertEquals ("Tile Upgraded Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgradedStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.TilesAndStationLaid);
		assertEquals ("Tile Upgraded Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgradedStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.StationLaid);
		assertEquals ("Tile Upgraded Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgradedStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.TileUpgradedStationLaid);
		assertEquals ("Tile Upgraded Station Laid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgradedStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.OperatedTrain);
		assertEquals ("Operated Train", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgradedStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HandledLoanInterest);
		assertEquals ("Handled Loan Interest", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgradedStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HoldDividend);
		assertEquals ("No Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgradedStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.HalfDividend);
		assertEquals ("Half Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgradedStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.FullDividend);
		assertEquals ("Full Dividend Paid", betaShareCompany.getStatusName ());
		
		betaShareCompany.forceSetStatus (ActorI.ActionStates.TileUpgradedStationLaid);
		betaShareCompany.updateStatus (ActorI.ActionStates.StartedOperations);
		assertEquals ("Tile Upgraded Station Laid", betaShareCompany.getStatusName ());
	}
	
	@Test
	@DisplayName ("Update Status from HandledLoanInterest to next State")
	void testUpdatingHandledLoanInterestStatus () {
		betaShareCompany.forceSetStatus (ActorI.ActionStates.HandledLoanInterest);
		assertEquals ("Handled Loan Interest", betaShareCompany.getStatusName ());
		betaShareCompany.updateStatus (ActorI.ActionStates.HoldDividend);
		assertEquals ("No Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.HandledLoanInterest);
		betaShareCompany.updateStatus (ActorI.ActionStates.HalfDividend);
		assertEquals ("Half Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.HandledLoanInterest);
		betaShareCompany.updateStatus (ActorI.ActionStates.FullDividend);
		assertEquals ("Full Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.HandledLoanInterest);
		betaShareCompany.updateStatus (ActorI.ActionStates.StartedOperations);
		assertEquals ("Handled Loan Interest", betaShareCompany.getStatusName ());
	}
	
	@Test
	@DisplayName ("Update Status from Dividend Paid States to next State")
	void testUpdatingDividendPaidStatesStatus () {
		betaShareCompany.forceSetStatus (ActorI.ActionStates.HoldDividend);
		assertEquals ("No Dividend Paid", betaShareCompany.getStatusName ());
		betaShareCompany.updateStatus (ActorI.ActionStates.BoughtTrain);
		assertEquals ("Bought Train", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.HoldDividend);
		betaShareCompany.updateStatus (ActorI.ActionStates.Operated);
		assertEquals ("Operated", betaShareCompany.getStatusName ());
		
		betaShareCompany.forceSetStatus (ActorI.ActionStates.HoldDividend);
		betaShareCompany.updateStatus (ActorI.ActionStates.StartedOperations);
		assertEquals ("No Dividend Paid", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.HalfDividend);
		betaShareCompany.updateStatus (ActorI.ActionStates.BoughtTrain);
		assertEquals ("Bought Train", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.HalfDividend);
		betaShareCompany.updateStatus (ActorI.ActionStates.Operated);
		assertEquals ("Operated", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.HalfDividend);
		betaShareCompany.updateStatus (ActorI.ActionStates.StartedOperations);
		assertEquals ("Half Dividend Paid", betaShareCompany.getStatusName ());
		
		betaShareCompany.forceSetStatus (ActorI.ActionStates.FullDividend);
		betaShareCompany.updateStatus (ActorI.ActionStates.BoughtTrain);
		assertEquals ("Bought Train", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.FullDividend);
		betaShareCompany.updateStatus (ActorI.ActionStates.Operated);
		assertEquals ("Operated", betaShareCompany.getStatusName ());

		betaShareCompany.forceSetStatus (ActorI.ActionStates.FullDividend);
		betaShareCompany.updateStatus (ActorI.ActionStates.StartedOperations);
		assertEquals ("Full Dividend Paid", betaShareCompany.getStatusName ());
	}
	
	@Test
	@DisplayName ("Update Status TrainCompany to next State")
	void testUpdatingTrainCompanyStatus () {
		Tile tNoPreviousTile;
		Tile tThePreviousTile;
		
		tNoPreviousTile = Tile.NO_TILE;
		limaShareCompany.forceSetStatus (ActorI.ActionStates.StationLaid);
		assertEquals ("Station Laid", limaShareCompany.getStatusName ());
		limaShareCompany.updateStatusWithTile (tNoPreviousTile);
		assertEquals ("Tile and Station Laid", limaShareCompany.getStatusName ());

		limaShareCompany.forceSetStatus (ActorI.ActionStates.TileLaid);
		limaShareCompany.updateStatusWithTile (tNoPreviousTile);
		assertEquals ("Tiles Laid", limaShareCompany.getStatusName ());

		limaShareCompany.forceSetStatus (ActorI.ActionStates.TileAndStationLaid);
		limaShareCompany.updateStatusWithTile (tNoPreviousTile);
		assertEquals ("Tiles and Station Laid", limaShareCompany.getStatusName ());

		limaShareCompany.forceSetStatus (ActorI.ActionStates.StartedOperations);
		limaShareCompany.updateStatusWithTile (tNoPreviousTile);
		assertEquals ("Tile Laid", limaShareCompany.getStatusName ());

		limaShareCompany.forceSetStatus (ActorI.ActionStates.HoldDividend);
		limaShareCompany.updateStatusWithTile (tNoPreviousTile);
		assertEquals ("No Dividend Paid", limaShareCompany.getStatusName ());

		tThePreviousTile = new Tile (7, "TEST", 1);
		limaShareCompany.forceSetStatus (ActorI.ActionStates.StartedOperations);
		limaShareCompany.updateStatusWithTile (tThePreviousTile);
		assertEquals ("Tile Upgraded", limaShareCompany.getStatusName ());

		limaShareCompany.forceSetStatus (ActorI.ActionStates.StationLaid);
		limaShareCompany.updateStatusWithTile (tThePreviousTile);
		assertEquals ("Tile Upgraded Station Laid", limaShareCompany.getStatusName ());

		limaShareCompany.forceSetStatus (ActorI.ActionStates.FullDividend);
		limaShareCompany.updateStatusWithTile (tThePreviousTile);
		assertEquals ("Full Dividend Paid", limaShareCompany.getStatusName ());
	}
}
