package ge18xx.bank;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.company.CompanyTestFactory;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.phase.PhaseInfo;
import geUtilities.GUI;

class BankTests {
	private BankTestFactory bankTestFactory;
	private GameTestFactory gameTestFactory;
	private CompanyTestFactory companyTestFactory;
	private StartPacketTestFactory startPacketTestFactory;
	private GameManager mGameManager;
	private GameInfo gameInfo;
	private PhaseInfo mPhaseInfo;
	private CorporationList mCorporationListPrivates;
	private CorporationList mCorporationListMinors;
	private CorporationList mCorporationListShares;
	private Bank bank;
	private StartPacketFrame startPacketFrame;
	
	@BeforeEach
	void setUp () throws Exception {
		bankTestFactory = new BankTestFactory ();
		gameTestFactory = new GameTestFactory ();
		companyTestFactory = new CompanyTestFactory ();
		mGameManager = gameTestFactory.buildGameManagerMock ();
		gameInfo = gameTestFactory.buildGameInfo (1);
		bank = bankTestFactory.buildBank (mGameManager);
		startPacketTestFactory = new StartPacketTestFactory (mGameManager, bank);
		startPacketFrame = startPacketTestFactory.buildStartPacketFrameMock ("Test Bank Start Packet");
		bank.setStartPacketFrame (startPacketFrame);
		
		mPhaseInfo = gameTestFactory.buildPhaseInfoMock ();
		mCorporationListPrivates = companyTestFactory.buildCorporationListMock (mGameManager, mPhaseInfo);
		mCorporationListMinors = companyTestFactory.buildCorporationListMock (mGameManager, mPhaseInfo);
		mCorporationListShares = companyTestFactory.buildCorporationListMock (mGameManager, mPhaseInfo);
		Mockito.when (mGameManager.getPrivates ()).thenReturn (mCorporationListPrivates);
		Mockito.when (mGameManager.getMinorCompanies ()).thenReturn (mCorporationListMinors);
		Mockito.when (mGameManager.getShareCompanies ()).thenReturn (mCorporationListShares);

		bank.setup (gameInfo);
	}

	@Test
	@DisplayName ("Format Int Cash with Bank")
	void formatIntCashTest () {
		int tCashAmount;
		String tFormattedCash;
		
		tCashAmount = 1203;
		tFormattedCash = Bank.formatCash (tCashAmount);
		assertEquals ("$ 1,203", tFormattedCash);
		
		bank.setFormat (GUI.EMPTY_STRING);
		tFormattedCash = Bank.formatCash (tCashAmount);
		assertEquals ("1203", tFormattedCash);

		bank.setFormat ("£ ###,###");
		tFormattedCash = Bank.formatCash (tCashAmount);
		assertEquals ("£ 1,203", tFormattedCash);
	}
	
	@Test
	@DisplayName ("Format String Cash with Bank")
	void formatStringCashTest () {
		String tCashAmount;
		String tFormattedCash;
		
		tCashAmount = "1203";
		tFormattedCash = Bank.formatCash (tCashAmount);
		assertEquals ("$ 1,203", tFormattedCash);
		
		bank.setFormat (GUI.EMPTY_STRING);
		tFormattedCash = Bank.formatCash (tCashAmount);
		assertEquals ("1203", tFormattedCash);

		bank.setFormat ("£ ###,###");
		tFormattedCash = Bank.formatCash (tCashAmount);
		assertEquals ("£ 1,203", tFormattedCash);
	}

	@Test
	@DisplayName ("Add Cash to the Bank")
	void addCashToBankTest () {
		int tCashAmount;
		
		tCashAmount = 12000;
		assertEquals (0, bank.getCash ());
		bank.addCash (tCashAmount);
		assertEquals (12000, bank.getCash ());
		bank.addCash (0);
		assertEquals (12000, bank.getCash ());
		bank.addCash (-1000);
		assertEquals (11000, bank.getCash ());
		bank.addCash (-10900);
		assertEquals (100, bank.getCash ());
		assertFalse (bank.isBroken ());
		bank.addCash (-200);
		assertEquals (-100, bank.getCash ());
		assertTrue (bank.isBroken ());
	}
	
	@Test
	@DisplayName ("Transfer Cash between CashHolder and Bank")
	void transferCashToTest () {
		ShareCompany tShareCompany;
		int tCashAmount1;
		int tCashAmount2;
		int tTransferAmount;
		
		tShareCompany = companyTestFactory.buildAShareCompany (1);
		tCashAmount1 = 1050;
		bank.addCash (tCashAmount1);
		assertEquals (1050, bank.getCash ());
		tTransferAmount = 250;
		tCashAmount2 = 500;
		tShareCompany.addCash (tCashAmount2);
		assertEquals (500, tShareCompany.getCash ());
		
		bank.transferCashTo (tShareCompany, tTransferAmount);
		assertEquals (800, bank.getCash ());
		assertEquals (750, tShareCompany.getCash ());
		
		tTransferAmount = 75;
		tShareCompany.transferCashTo (bank, tTransferAmount);
		assertEquals (875, bank.getCash ());
		assertEquals (675, tShareCompany.getCash ());
	}
}
