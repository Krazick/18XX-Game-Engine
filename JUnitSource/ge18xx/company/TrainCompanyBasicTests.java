package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.CorporateBank;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI;

@TestInstance (Lifecycle.PER_CLASS)
@DisplayName ("Train Company Basic Tests")
class TrainCompanyBasicTests extends CorporationTester {
	GameManager mGameManager;
	Bank bank;
	CorporateBank corporateBank;
	TrainCompany trainCompany1;
	TrainCompany trainCompany3;
	TrainCompany mTrainCompany;

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
		
		trainCompany1 = companyTestFactory.buildATrainCompany (1);
		trainCompany3 = companyTestFactory.buildATrainCompany (3);
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
		trainCompany1.setCorporationList (mCorporationList);
		
		Mockito.when (mCorporationList.getBank ()).thenReturn (tBank);
		Mockito.when (mCorporationList.getBankPool ()).thenReturn (tBankPool);
		assertEquals (0, trainCompany1.getSelectedTrainCount ());
		
		mBank = bankTestFactory.buildBankMock (mGameManager);
		mBankPool = bankTestFactory.buildBankPoolMock (mGameManager);

		Mockito.when (mBank.getSelectedTrainCount ()).thenReturn (1);
		Mockito.when (mCorporationList.getBank ()).thenReturn (mBank);
		assertEquals (0, trainCompany1.getSelectedTrainCount ());
		
		Mockito.when (mCorporationList.getBank ()).thenReturn (tBank);
		Mockito.when (mBankPool.getSelectedTrainCount ()).thenReturn (0);
		Mockito.when (mCorporationList.getBankPool ()).thenReturn (mBankPool);
		assertEquals (0, trainCompany1.getSelectedTrainCount ());
		
		Mockito.when (mBank.getSelectedTrainCount ()).thenReturn (1);
		Mockito.when (mCorporationList.getBank ()).thenReturn (mBank);
		Mockito.when (mBankPool.getSelectedTrainCount ()).thenReturn (0);
		Mockito.when (mCorporationList.getBankPool ()).thenReturn (mBankPool);
		assertEquals (1, trainCompany1.getSelectedTrainCount ());
		
		Mockito.when (mBank.getSelectedTrainCount ()).thenReturn (0);
		Mockito.when (mCorporationList.getBank ()).thenReturn (mBank);
		Mockito.when (mBankPool.getSelectedTrainCount ()).thenReturn (1);
		Mockito.when (mCorporationList.getBankPool ()).thenReturn (mBankPool);
		assertEquals (1, trainCompany1.getSelectedTrainCount ());
		
		Mockito.when (mBank.getSelectedTrainCount ()).thenReturn (1);
		Mockito.when (mCorporationList.getBank ()).thenReturn (mBank);
		Mockito.when (mBankPool.getSelectedTrainCount ()).thenReturn (1);
		Mockito.when (mCorporationList.getBankPool ()).thenReturn (mBankPool);
		assertEquals (2, trainCompany1.getSelectedTrainCount ());
		
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
		
		tPortLicense = trainCompany1.getPortLicense ();
		assertNull (tPortLicense);

		tPrice = 50;
		tBenefitValue = 10;
		tNewLicense = new License (License.LicenseTypes.BRIDGE, tPrice, tBenefitValue);
		trainCompany1.addLicense (tNewLicense);
		
		tPortLicense = trainCompany1.getPortLicense ();
		assertNull (tPortLicense);
		
		tNewPortLicense = new License (License.LicenseTypes.PORT, tPrice, tBenefitValue);
		trainCompany1.addLicense (tNewPortLicense);
		
		tPortLicense = trainCompany1.getPortLicense ();
		assertEquals (tNewPortLicense, tPortLicense);
		
		assertTrue (trainCompany1.hasLicense ("Port"));
		assertFalse (trainCompany1.hasLicense ("Tunnel"));
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
		trainCompany1.addLicense (tNewLicense);
		tNewPortLicense = new License (License.LicenseTypes.PORT, tPrice, tBenefitValue);
		trainCompany1.addLicense (tNewPortLicense);

		tPortLicense = trainCompany1.getPortLicense ();
		assertEquals (tNewPortLicense, tPortLicense);
		
		assertTrue (trainCompany1.removeLicense (tNewPortLicense));
		tPortLicense = trainCompany1.getPortLicense ();
		assertNull (tPortLicense);
		
		assertFalse (trainCompany1.removeLicense (tNewPortLicense));
	}

	@Test
	@DisplayName ("Test getting Specific Licenses")
	void getLicenseByTypeTests () {
		License tPortLicense;
		License tNewBridgeLicense;
		License tNewPortLicense;
		License tFoundLicense;
		int tPrice;
		int tBenefitValue;

		tPortLicense = trainCompany1.getLicense (License.LicenseTypes.PORT);
		assertNull (tPortLicense);

		assertEquals (0, trainCompany1.getLicenseCount ());
		assertEquals (" ", trainCompany1.getLicenses ());

		tPrice = 50;
		tBenefitValue = 10;
		tNewBridgeLicense = new License (License.LicenseTypes.BRIDGE, tPrice, tBenefitValue);
		
		assertFalse (trainCompany1.hasLicense (tNewBridgeLicense));
		trainCompany1.addLicense (tNewBridgeLicense);
		
		assertEquals (1, trainCompany1.getLicenseCount ());
		assertEquals ("Bridge", trainCompany1.getLicenses ());

		tNewPortLicense = new License (License.LicenseTypes.PORT, tPrice, tBenefitValue);
		trainCompany1.addLicense (tNewPortLicense);
		
		assertEquals (2, trainCompany1.getLicenseCount ());
		assertEquals ("Bridge, Port", trainCompany1.getLicenses ());

		tFoundLicense = trainCompany1.getLicense (License.LicenseTypes.TUNNEL);
		assertNull (tFoundLicense);
		
		tFoundLicense = trainCompany1.getLicense (License.LicenseTypes.BRIDGE);
		assertEquals (tFoundLicense, tNewBridgeLicense);
		
		tFoundLicense = trainCompany1.getLicense (License.LicenseTypes.PORT);
		assertEquals (tFoundLicense, tNewPortLicense);
		
		assertEquals (tNewPortLicense, trainCompany1.getLicenseAt (1));
		assertEquals (tNewBridgeLicense, trainCompany1.getLicenseAt (0));
		
		assertTrue (trainCompany1.hasLicense (tNewPortLicense));
	}

	@Test
	@DisplayName ("Share Company has bought train Tests")
	void shareCompanyHasBoughtTrainTests () {

		trainCompany1.resetStatus (ActorI.ActionStates.Unowned);
		assertFalse (trainCompany1.hasBoughtTrain ());

		trainCompany1.resetStatus (ActorI.ActionStates.Owned);
		assertFalse (trainCompany1.hasBoughtTrain ());
		
		// Something weird happening if resetStatus is called, with it tries to updateInfo
		// it then fails to find the ShareCompanies CorporationList that is mocked in the ShareCompany
		
//		noDestinationShareCompany.resetStatus (ActorI.ActionStates.BoughtTrain);
//		assertTrue (noDestinationShareCompany.hasBoughtTrain ());
		
		trainCompany1.forceSetStatus (ActorI.ActionStates.BoughtTrain);
		assertTrue (trainCompany1.hasBoughtTrain ());

		trainCompany1.resetStatus (ActorI.ActionStates.NotOperated);
		assertFalse (trainCompany1.hasBoughtTrain ());

		trainCompany1.resetStatus (ActorI.ActionStates.Operated);
		assertFalse (trainCompany1.hasBoughtTrain ());

		trainCompany1.resetStatus (ActorI.ActionStates.Closed);
		assertFalse (trainCompany1.hasBoughtTrain ());
	}
	
	
	@Test
	@DisplayName ("TrainCompany Actors Bank Tests")
	void TrainCompanyActorsBankTests () {
		Bank tActorsBank;
		Bank tActorsCorporateBank;
		
		tActorsBank = trainCompany1.getActorsBank ();
		assertEquals ("Bank", tActorsBank.getName ());
		assertEquals ("Test Train Pennsylvania", trainCompany1.getName ());
		assertFalse (tActorsBank.isCorporateBank ());
		
		tActorsCorporateBank = trainCompany3.getActorsBank ();
		assertEquals ("Test Corporate Bank", tActorsCorporateBank.getName ());
		assertTrue (tActorsCorporateBank.isCorporateBank ());

		assertEquals ("Test East Indian Railway", trainCompany3.getName ());
		
		assertFalse (trainCompany1.isCorporateBank ());
		assertTrue (trainCompany3.isCorporateBank ());
	}

}
