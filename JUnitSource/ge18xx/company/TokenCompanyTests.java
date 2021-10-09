package ge18xx.company;

import java.awt.event.ItemListener;

import javax.swing.JPanel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName ("Token Company Tests")
class TokenCompanyTests {
	class TokenCompanyConcrete extends TokenCompany {

		@Override
		public JPanel buildPrivateCertJPanel (ItemListener aItemListener, int aAvailableCash) {
			// TODO Auto-generated method stub
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
}
