package ge18xx.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import ge18xx.company.Certificate;

@DisplayName ("Escrow")
@ExtendWith (MockitoExtension.class)
class EscrowTests {

	@Mock Certificate mCertificate;
	Escrow primaryEscrow;
	
	@BeforeEach
	void setUp() throws Exception {
		primaryEscrow = new Escrow (mCertificate, 120);
	}

	@Nested
	@DisplayName ("Constructor Tests")
	class constructorFunctionalityTests {
		@Test
		@DisplayName ("No Args Test")
		void escrowConstructor0Test () {
			Escrow tEscrow;
			
			tEscrow = new Escrow ();
			assertEquals (0, tEscrow.getCash ());
			assertEquals (null, tEscrow.getCompanyAbbrev ());
		}
	
		@Test
		@DisplayName ("Certificate Args Test")
		void escrowConstructor1Test () {
			Escrow tEscrow;
			
			tEscrow = new Escrow (mCertificate);
			assertEquals (0, tEscrow.getCash ());
			assertNull (tEscrow.getName ());
			assertNull (tEscrow.getAbbrev ());
		}
	
		@Test
		@DisplayName ("Further Certificate Args Test")
		void escrowConstructor1ATest () {
			Mockito.doReturn ("EscrowCo").when (mCertificate).getCompanyAbbrev ();
			
			assertEquals ("No Action", primaryEscrow.getStateName ());
			assertEquals ("EscrowCo", primaryEscrow.getCompanyAbbrev ());
		}
	
	
		@Test
		@DisplayName ("Certificate and Cash Args Test")
		void escrowConstructor2Test () {
			assertEquals (120, primaryEscrow.getCash ());
		}
	}
	
	@Test
	@DisplayName ("Name Tests") 
	void nameTests () {
		assertEquals (null, primaryEscrow.getName ());
		assertEquals (") Escrow for EscrowTester", Escrow.getUnindexedName ("EscrowTester"));
		primaryEscrow.setName ("Escrow Test Name");
		assertEquals ("Escrow Test Name", primaryEscrow.getName ());
		primaryEscrow.setName ("Escrow Baker", 0);
		assertEquals ("0) Escrow for Escrow Baker", primaryEscrow.getName ());
	}
	
	@Test
	@DisplayName ("Adding Cash Test")
	void addCashTest () {
		primaryEscrow.addCash (20);
		assertEquals (140, primaryEscrow.getCash ());
	}
	
	@Nested
	@DisplayName ("CashHolder Interface Boolean Method Tests")
	class cashHolderIBooleanTests {
		@Test
		@DisplayName ("is a Private Company Test") 
		void escrowPrivateTest () {
			assertFalse (primaryEscrow.isAPrivateCompany ());
		}

		@Test
		@DisplayName ("is a Corporation Test") 
		void escrowCorporationTest () {
			assertFalse (primaryEscrow.isACorporation ());
		}

		@Test
		@DisplayName ("is a Bank Test") 
		void escrowBankTest () {
			assertFalse (primaryEscrow.isABank ());
		}

		@Test
		@DisplayName ("is a Player Test") 
		void escrowPlayerTest () {
			assertFalse (primaryEscrow.isAPlayer ());
		}

		@Test
		@DisplayName ("is a Stock Round Test") 
		void escrowStockRoundTest () {
			assertFalse (primaryEscrow.isAStockRound ());
		}

		@Test
		@DisplayName ("is a Operating Round Test") 
		void escrowOperatingRoundTest () {
			assertFalse (primaryEscrow.isAOperatingRound ());
		}
	}
}
