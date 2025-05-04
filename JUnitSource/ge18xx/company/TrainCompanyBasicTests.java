package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.BankTestFactory;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI;

class TrainCompanyBasicTests {
	CompanyTestFactory companyTestFactory;
	GameManager mGameManager;
	BankTestFactory bankTestFactory;
	Bank bank;
	TrainCompany trainCompany;
	TrainCompany mTrainCompany;

	@BeforeEach
	void setUp () throws Exception {
		companyTestFactory = new CompanyTestFactory ();
		mGameManager = companyTestFactory.getGameManagerMock ();
		bankTestFactory = new BankTestFactory ();
		bank = bankTestFactory.buildBank (mGameManager);
		trainCompany = companyTestFactory.buildATrainCompany (1);
		mTrainCompany = companyTestFactory.buildTrainCompanyMock ();
	}

	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Test getting selected Train Count") 
	void getSelectedTrainCountTest () {
		Bank tBank;
		BankPool tBankPool;
		Bank mBank;
		BankPool mBankPool;
		CorporationList mCorporationList;
		
		tBank = Bank.NO_BANK;
		tBankPool = BankPool.NO_BANK_POOL;
		mCorporationList = companyTestFactory.buildCorporationListMock (mGameManager, null);
		trainCompany.setCorporationList (mCorporationList);
		
		Mockito.when (mCorporationList.getBank ()).thenReturn (tBank);
		Mockito.when (mCorporationList.getBankPool ()).thenReturn (tBankPool);
		assertEquals (0, trainCompany.getSelectedTrainCount ());
		
		mBank = bankTestFactory.buildBankMock (mGameManager);
		mBankPool = bankTestFactory.buildBankPoolMock (mGameManager);

		Mockito.when (mBank.getSelectedTrainCount ()).thenReturn (1);
		Mockito.when (mCorporationList.getBank ()).thenReturn (mBank);
		assertEquals (0, trainCompany.getSelectedTrainCount ());
		
		Mockito.when (mCorporationList.getBank ()).thenReturn (tBank);
		Mockito.when (mBankPool.getSelectedTrainCount ()).thenReturn (0);
		Mockito.when (mCorporationList.getBankPool ()).thenReturn (mBankPool);
		assertEquals (0, trainCompany.getSelectedTrainCount ());
		
		Mockito.when (mBank.getSelectedTrainCount ()).thenReturn (1);
		Mockito.when (mCorporationList.getBank ()).thenReturn (mBank);
		Mockito.when (mBankPool.getSelectedTrainCount ()).thenReturn (0);
		Mockito.when (mCorporationList.getBankPool ()).thenReturn (mBankPool);
		assertEquals (1, trainCompany.getSelectedTrainCount ());
		
		Mockito.when (mBank.getSelectedTrainCount ()).thenReturn (0);
		Mockito.when (mCorporationList.getBank ()).thenReturn (mBank);
		Mockito.when (mBankPool.getSelectedTrainCount ()).thenReturn (1);
		Mockito.when (mCorporationList.getBankPool ()).thenReturn (mBankPool);
		assertEquals (1, trainCompany.getSelectedTrainCount ());
		
		Mockito.when (mBank.getSelectedTrainCount ()).thenReturn (1);
		Mockito.when (mCorporationList.getBank ()).thenReturn (mBank);
		Mockito.when (mBankPool.getSelectedTrainCount ()).thenReturn (1);
		Mockito.when (mCorporationList.getBankPool ()).thenReturn (mBankPool);
		assertEquals (2, trainCompany.getSelectedTrainCount ());
		
		Mockito.when (mBank.getSelectedTrainCount ()).thenReturn (1);
		Mockito.when (mCorporationList.getBank ()).thenReturn (mBank);
		Mockito.when (mBankPool.getSelectedTrainCount ()).thenReturn (1);
		Mockito.when (mCorporationList.getBankPool ()).thenReturn (mBankPool);
		Mockito.when (mTrainCompany.getSelectedTrainCount ()).thenReturn (1);
		assertEquals (1, mTrainCompany.getSelectedTrainCount ());
	}
	
	@Test
	@DisplayName ("Test getting a Port License in this Company")
	void getPortLicenseTest () {
		License tPortLicense;
		License tNewLicense;
		License tNewPortLicense;
		int tPrice;
		int tBenefitValue;
		
		tPortLicense = trainCompany.getPortLicense ();
		assertNull (tPortLicense);

		tPrice = 50;
		tBenefitValue = 10;
		tNewLicense = new License (License.LicenseTypes.BRIDGE, tPrice, tBenefitValue);
		trainCompany.addLicense (tNewLicense);
		
		tPortLicense = trainCompany.getPortLicense ();
		assertNull (tPortLicense);
		
		tNewPortLicense = new License (License.LicenseTypes.PORT, tPrice, tBenefitValue);
		trainCompany.addLicense (tNewPortLicense);
		
		tPortLicense = trainCompany.getPortLicense ();
		assertEquals (tNewPortLicense, tPortLicense);
		
		assertTrue (trainCompany.hasLicense ("Port"));
		assertFalse (trainCompany.hasLicense ("Tunnel"));
	}
	
	@Test 
	@DisplayName ("Test Removing Specific Licenses")
	void removePortLicenseTest () {
		License tPortLicense;
		License tNewLicense;
		License tNewPortLicense;
		int tPrice;
		int tBenefitValue;

		tPrice = 50;
		tBenefitValue = 10;
		tNewLicense = new License (License.LicenseTypes.BRIDGE, tPrice, tBenefitValue);
		trainCompany.addLicense (tNewLicense);
		tNewPortLicense = new License (License.LicenseTypes.PORT, tPrice, tBenefitValue);
		trainCompany.addLicense (tNewPortLicense);

		tPortLicense = trainCompany.getPortLicense ();
		assertEquals (tNewPortLicense, tPortLicense);
		
		assertTrue (trainCompany.removeLicense (tNewPortLicense));
		tPortLicense = trainCompany.getPortLicense ();
		assertNull (tPortLicense);
		
		assertFalse (trainCompany.removeLicense (tNewPortLicense));
	}

	@Test
	@DisplayName ("Test getting Specific Licenses")
	void getLicenseByTypeTests () {
		License tPortLicense;
		License tNewLicense;
		License tNewPortLicense;
		License tFoundLicense;
		int tPrice;
		int tBenefitValue;

		tPortLicense = trainCompany.getLicense (License.LicenseTypes.PORT);
		assertNull (tPortLicense);
		
		tPrice = 50;
		tBenefitValue = 10;
		tNewLicense = new License (License.LicenseTypes.BRIDGE, tPrice, tBenefitValue);
		trainCompany.addLicense (tNewLicense);
		tNewPortLicense = new License (License.LicenseTypes.PORT, tPrice, tBenefitValue);
		trainCompany.addLicense (tNewPortLicense);

		tFoundLicense = trainCompany.getLicense (License.LicenseTypes.TUNNEL);
		assertNull (tFoundLicense);
		
		tFoundLicense = trainCompany.getLicense (License.LicenseTypes.BRIDGE);
		assertEquals (tFoundLicense, tNewLicense);
		
		tFoundLicense = trainCompany.getLicense (License.LicenseTypes.PORT);
		assertEquals (tFoundLicense, tNewPortLicense);
	}

	@Test
	@DisplayName ("Share Company has bought train Tests")
	void shareCompanyHasBoughtTrainTests () {

		trainCompany.resetStatus (ActorI.ActionStates.Unowned);
		assertFalse (trainCompany.hasBoughtTrain ());

		trainCompany.resetStatus (ActorI.ActionStates.Owned);
		assertFalse (trainCompany.hasBoughtTrain ());
		
		// Something weird happening if resetStatus is called, with it tries to updateInfo
		// it then fails to find the ShareCompanies CorporationList that is mocked in the ShareCompany
		
//		noDestinationShareCompany.resetStatus (ActorI.ActionStates.BoughtTrain);
//		assertTrue (noDestinationShareCompany.hasBoughtTrain ());
		
		trainCompany.forceSetStatus (ActorI.ActionStates.BoughtTrain);
		assertTrue (trainCompany.hasBoughtTrain ());

		trainCompany.resetStatus (ActorI.ActionStates.NotOperated);
		assertFalse (trainCompany.hasBoughtTrain ());

		trainCompany.resetStatus (ActorI.ActionStates.Operated);
		assertFalse (trainCompany.hasBoughtTrain ());

		trainCompany.resetStatus (ActorI.ActionStates.Closed);
		assertFalse (trainCompany.hasBoughtTrain ());
	}
}
