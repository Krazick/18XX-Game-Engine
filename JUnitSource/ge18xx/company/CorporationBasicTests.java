package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ItemListener;

import javax.swing.JPanel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ge18xx.round.action.ActorI;

@DisplayName ("Abstract Corporation Class Tests via Concrete extension")
class CorporationBasicTests {

	class CorporationConcrete extends Corporation {

		@Override
		public boolean atTrainLimit () {
			return false;
		}

		@Override
		public void completeBenefitInUse () {

		}

		@Override
		public JPanel buildPrivateCertJPanel (ItemListener aItemListener, int aAvailableCash) {
			return null;
		}

		@Override
		protected boolean choiceForBaseToken () {
			return false;
		}

		@Override
		public int calculateStartingTreasury () {
			return 0;
		}
	}

	CorporationConcrete corporation;

	@BeforeEach
	void setUp () throws Exception {
		corporation = new CorporationConcrete ();
	}

	@AfterEach
	void tearDown () throws Exception {
	}

//	ActorI.ActionStates -- CorporationStates
//
//	Unowned, Owned, Closed, MayFloat, WillFloat, NotOperated,
//	StartedOperations, TileLaid, Tile2Laid, TileUpgraded, 
//	StationLaid, TileAndStationLaid, OperatedTrain, HoldDividend, 
//	HalfDividend, FullDividend, BoughtTrain, Operated

	@Test
	@DisplayName ("Verify basic Corporation Settings, and simple Status Updates work")
	void testBasicSetNames () {
		corporation.setAbbrev ("BRC");
		corporation.setName ("Basic Railway Company");
		corporation.setStatus (ActorI.ActionStates.Unowned);

		assertEquals ("BRC", corporation.getAbbrev ());
		assertEquals ("Basic Railway Company", corporation.getName ());
		assertFalse (corporation.isClosed ());
		assertFalse (corporation.canOperate ());
		assertTrue (corporation.isUnowned ());

		corporation.resetStatus (ActorI.ActionStates.Owned);
		assertFalse (corporation.canOperate ());
		assertFalse (corporation.isUnowned ());

		corporation.resetStatus (ActorI.ActionStates.Operated);
		assertTrue (corporation.canOperate ());

		corporation.resetPrimaryActionState (ActorI.ActionStates.Closed);
		assertTrue (corporation.isClosed ());
		assertFalse (corporation.canOperate ());
	}

	@Nested
	@DisplayName ("Test with Different Corporation States")
	class TestDifferentStates {
		@Test
		@DisplayName ("Valid for IsOperating Method")
		void testIsOperating () {
			// (status == ActorI.ActionStates.Closed) ||
			// (status == ActorI.ActionStates.Unowned) ||
			// (status == ActorI.ActionStates.Owned) ||
			// (status == ActorI.ActionStates.Operated) ||
			// (status == ActorI.ActionStates.NotOperated)
			corporation.setAbbrev ("BRC");
			corporation.setName ("Basic Railway Company");

			corporation.setStatus (ActorI.ActionStates.Unowned);
			assertFalse (corporation.isOperating ());

			corporation.resetStatus (ActorI.ActionStates.FullDividend);
			assertTrue (corporation.isOperating ());

			corporation.resetStatus (ActorI.ActionStates.Owned);
			assertFalse (corporation.isOperating ());

			corporation.resetStatus (ActorI.ActionStates.BoughtTrain);
			assertTrue (corporation.isOperating ());

			corporation.resetStatus (ActorI.ActionStates.Operated);
			assertFalse (corporation.isOperating ());

			corporation.resetStatus (ActorI.ActionStates.TileLaid);
			assertTrue (corporation.isOperating ());

			corporation.resetStatus (ActorI.ActionStates.NotOperated);
			assertFalse (corporation.isOperating ());

			corporation.resetStatus (ActorI.ActionStates.StationLaid);
			assertTrue (corporation.isOperating ());

			corporation.resetStatus (ActorI.ActionStates.Closed);
			assertFalse (corporation.isOperating ());
		}

		@Test
		@DisplayName ("Valid for shouldOperate Method")
		void testShouldOperate () {
			// (status == ActorI.ActionStates.Closed) ||
			// (status == ActorI.ActionStates.Unowned) ||
			// (status == ActorI.ActionStates.Owned) ||
			// (status == ActorI.ActionStates.Operated)

			corporation.setAbbrev ("BRC");
			corporation.setName ("Basic Railway Company");

			corporation.setStatus (ActorI.ActionStates.Unowned);
			assertFalse (corporation.shouldOperate ());

			corporation.resetStatus (ActorI.ActionStates.FullDividend);
			assertTrue (corporation.shouldOperate ());

			corporation.resetStatus (ActorI.ActionStates.Owned);
			assertFalse (corporation.shouldOperate ());

			corporation.resetStatus (ActorI.ActionStates.BoughtTrain);
			assertTrue (corporation.shouldOperate ());

			corporation.resetStatus (ActorI.ActionStates.Operated);
			assertFalse (corporation.shouldOperate ());

			corporation.resetStatus (ActorI.ActionStates.TileLaid);
			assertTrue (corporation.shouldOperate ());

			corporation.resetStatus (ActorI.ActionStates.NotOperated);
			assertTrue (corporation.shouldOperate ());

			corporation.resetStatus (ActorI.ActionStates.StationLaid);
			assertTrue (corporation.shouldOperate ());

			corporation.resetStatus (ActorI.ActionStates.Closed);
			assertFalse (corporation.shouldOperate ());
		}

		@Test
		@DisplayName ("Valid for didOperate Method")
		void testDidOperate () {
			// (status == ActorI.ActionStates.Operated)

			corporation.setAbbrev ("BRC");
			corporation.setName ("Basic Railway Company");

			corporation.setStatus (ActorI.ActionStates.Unowned);
			assertFalse (corporation.didOperate ());

			corporation.resetStatus (ActorI.ActionStates.Operated);
			assertTrue (corporation.didOperate ());
		}

		@Test
		@DisplayName ("Valid for didPartiallyOperate Method")
		void testDidPartiallyOperate () {
			corporation.setAbbrev ("BRC");
			corporation.setName ("Basic Railway Company");

			corporation.setStatus (ActorI.ActionStates.Unowned);
			assertFalse (corporation.didPartiallyOperate ());

			corporation.resetStatus (ActorI.ActionStates.Operated);
			assertFalse (corporation.didPartiallyOperate ());

			corporation.resetStatus (ActorI.ActionStates.StationLaid);
			assertTrue (corporation.didPartiallyOperate ());

			corporation.resetStatus (ActorI.ActionStates.TileLaid);
			assertTrue (corporation.didPartiallyOperate ());

			corporation.resetStatus (ActorI.ActionStates.Tile2Laid);
			assertTrue (corporation.didPartiallyOperate ());

			corporation.resetStatus (ActorI.ActionStates.TileUpgraded);
			assertTrue (corporation.didPartiallyOperate ());

			corporation.resetStatus (ActorI.ActionStates.TileAndStationLaid);
			assertTrue (corporation.didPartiallyOperate ());

			corporation.resetStatus (ActorI.ActionStates.OperatedTrain);
			assertTrue (corporation.didPartiallyOperate ());

			corporation.resetStatus (ActorI.ActionStates.HoldDividend);
			assertTrue (corporation.didPartiallyOperate ());

			corporation.resetStatus (ActorI.ActionStates.HalfDividend);
			assertTrue (corporation.didPartiallyOperate ());

			corporation.resetStatus (ActorI.ActionStates.FullDividend);
			assertTrue (corporation.didPartiallyOperate ());

			corporation.resetStatus (ActorI.ActionStates.BoughtTrain);
			assertTrue (corporation.didPartiallyOperate ());

			corporation.resetStatus (ActorI.ActionStates.Closed);
			assertFalse (corporation.didPartiallyOperate ());
		}
	}

	// Corporation Class Status Methods to Test with Boolean returns:
	// isActive
	// isPlayerOwned -- Looks at Certificate Ownership

	@Test
	@DisplayName ("Test various 'isA<something>' method")
	void testCorporationIsAMethods () {
		assertFalse (corporation.isAPlayer ());
		assertFalse (corporation.isAPrivateCompany ());
		assertFalse (corporation.isATrainCompany ());
		assertFalse (corporation.isATokenCompany ());
		assertFalse (corporation.isAMinorCompany ());
		assertFalse (corporation.isGovtRailway ());
		assertFalse (corporation.isAShareCompany ());
		assertFalse (corporation.isABank ());
		assertFalse (corporation.isABankPool ());
	}

	@Test
	@DisplayName ("Test base Get Methods")
	void testCorporationGetMethods () {
		assertEquals (-1, corporation.getCurrentValue ());
		assertEquals ("<NONE> will operate null", corporation.getDoLabel ());
		assertEquals ("<NONE> is operating null", corporation.getOperatingLabel ());
		assertEquals ("Corporation", corporation.getElementName ().toString ());
	}
}
