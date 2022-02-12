package ge18xx.company;

import java.awt.event.ItemListener;

import javax.swing.JPanel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.round.action.ActorI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName ("Token Company Tests")
class TokenCompanyTests {
	class TokenCompanyConcrete extends TokenCompany {

		@Override
		public JPanel buildPrivateCertJPanel (ItemListener aItemListener, int aAvailableCash) {
			return null;
		}
	}

	TokenCompanyConcrete tokenCompany;
	
	@BeforeEach
	void setUp () throws Exception {
		tokenCompany = new TokenCompanyConcrete ();
	}

	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Test various 'isA<something>' method")
	void testCorporationIsAMethods () {
		assertFalse (tokenCompany.isAPlayer ());
		assertFalse (tokenCompany.isACoalCompany ());
		assertFalse (tokenCompany.isAPrivateCompany ());
		assertTrue (tokenCompany.isATrainCompany ());
		assertTrue (tokenCompany.isATokenCompany ());
		assertFalse (tokenCompany.isMinorCompany ());
		assertFalse (tokenCompany.isGovtRailway ());
		assertFalse (tokenCompany.isShareCompany ());
		assertFalse (tokenCompany.isBank ());
		assertFalse (tokenCompany.isBankPool ());
	}
	
	@Test
	@DisplayName ("Valid for CanLayToken Method")
	void testCanLayToken () {	
		MapToken tMapToken;
		
		tMapToken = new MapToken ();
		tokenCompany.setAbbrev ("TRC");
		tokenCompany.setName ("Token Railway Company");
		
		tokenCompany.resetStatus (ActorI.ActionStates.StartedOperations);
		assertFalse (tokenCompany.canLayToken ());
		
		tokenCompany.addMapToken (tMapToken);
		tokenCompany.addMapToken (tMapToken);
		assertEquals ("Token Count: 2", tokenCompany.getTokenLabel ());

		assertEquals (2, tokenCompany.getTokenCount ());
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
