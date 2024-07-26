package ge18xx.bank;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.company.CompanyTestFactory;
import ge18xx.company.CorporationList;
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

}
