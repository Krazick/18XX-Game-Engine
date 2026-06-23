package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.CorporateBank;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import geUtilities.xml.ElementName;

@TestInstance (Lifecycle.PER_CLASS)
@DisplayName ("Corporation List Tests")
class CorporationListTests extends CorporationTester {
	public ElementName EN_1830_CORPORATIONS = new ElementName ("1830 Share Companies");
	public ElementName EN_1853_CORPORATIONS = new ElementName ("1853 Share Companies");
	GameManager mGameManager;
	RoundManager mRoundManager;
	Bank bank;
	CorporateBank corporateBank;
	TrainCompany trainCompany1;
	TrainCompany trainCompany2;
	TrainCompany trainCompany3;
	CorporationList corporationList1;
	CorporationList corporationList2;
	CorporationList corporationList3;

	@Override
	@BeforeAll
	void factorySetup () {
		super.factorySetup ();
	}

	@BeforeEach
	void setUp () throws Exception {
		mGameManager = companyTestFactory.getGameManagerMock ();
		bank = bankTestFactory.buildBank (mGameManager);
		Mockito.when (mGameManager.getBank ()).thenReturn (bank);
		Mockito.when (mGameManager.getBankNamed ("Bank")).thenReturn (bank);

		corporateBank = bankTestFactory.buildCorporateBank (mGameManager);
		Mockito.when (mGameManager.getCorporateBank ()).thenReturn (corporateBank);
		Mockito.when (mGameManager.getBankNamed ("Test Corporate Bank")).thenReturn (corporateBank);
		
		mRoundManager = roundTestFactory.buildRoundManager ();
		
		trainCompany1 = companyTestFactory.buildATrainCompany (1);
		trainCompany2 = companyTestFactory.buildATrainCompany (2);
		trainCompany3 = companyTestFactory.buildATrainCompany (3);
		corporationList1 = new CorporationList (EN_1830_CORPORATIONS, mRoundManager);
		corporationList3 = new CorporationList (EN_1853_CORPORATIONS, mRoundManager);
	}

	@Test
	@DisplayName ("Corporation List Basic Tests")
	void corporationListBasicTests () {
		assertEquals (0, corporationList1.getCorporationCount ());
		corporationList1.addCorporation (trainCompany1);
		assertEquals (1, corporationList1.getCorporationCount ());
		corporationList1.addCorporation (trainCompany2);
		assertEquals (2, corporationList1.getCorporationCount ());
		
		assertFalse (corporationList1.hasCorporateBank ());
		
		corporationList3.addCorporation (trainCompany3);
		assertEquals (1, corporationList3.getCorporationCount ());
		
		assertTrue (corporationList3.hasCorporateBank ());
	}
}
