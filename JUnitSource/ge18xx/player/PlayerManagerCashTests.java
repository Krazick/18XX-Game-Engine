package ge18xx.player;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.BankTestFactory;
import ge18xx.company.Certificate;
import ge18xx.company.CertificateHolderI;
import ge18xx.company.CertificateTestFactory;
import ge18xx.company.CompanyTestFactory;
import ge18xx.company.Corporation;
import ge18xx.company.MinorCompany;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.round.RoundManager;
import ge18xx.round.RoundTestFactory;
import ge18xx.round.StockRound;
import ge18xx.round.action.ActorI;
import geUtilities.utilites.UtilitiesTestFactory;

class PlayerManagerCashTests {
	GameTestFactory gameTestFactory;
	PlayerTestFactory playerTestFactory;
	CertificateTestFactory certificateTestFactory;
	BankTestFactory bankTestFactory;
	RoundTestFactory roundTestFactory;
	CompanyTestFactory companyTestFactory;
	
	PlayerManager playerManager;
	PrivateCompany mPrivateCompany;
	Certificate mCertificate;
	GameManager gameManager;
	RoundManager roundManager;
	Bank mBank;
	BankPool mBankPool;
	StockRound stockRound;
	String bankName;
	Portfolio bankPoolPortfolio;
	Portfolio bankPortfolio;

	@BeforeEach
	void setUp () throws Exception {
		bankName = "PM Mock Bank";
		gameTestFactory = new GameTestFactory ();
		gameManager = gameTestFactory.buildGameManager ();
		
		playerTestFactory = new PlayerTestFactory (gameManager);
		bankTestFactory = new BankTestFactory ();
		roundTestFactory = new RoundTestFactory ();
		companyTestFactory = new CompanyTestFactory (gameTestFactory);
		certificateTestFactory = new CertificateTestFactory ();
		
		mBank = bankTestFactory.buildBankMock (gameManager, bankName);
		bankPortfolio = new Portfolio (mBank);
		Mockito.when (mBank.getPortfolio ()).thenReturn (bankPortfolio);
		
		mBankPool = bankTestFactory.buildBankPoolMock (gameManager, bankName);
		
		bankPoolPortfolio = new Portfolio (mBankPool);
		Mockito.when (mBankPool.getPortfolio ()).thenReturn (bankPoolPortfolio);

		mPrivateCompany = companyTestFactory.buildPrivateCompanyMock ("GETester");
		
		mCertificate = certificateTestFactory.buildCertificateMock ();
		playerManager = playerTestFactory.buildPlayerManager ();
		roundManager = roundTestFactory.buildRoundManager (gameManager, playerManager);
		stockRound = roundTestFactory.buildStockRound (playerManager, roundManager);
	}

	@Test
	@DisplayName ("Basic Getting the Bank")
	void getBankTest () {
		Bank tBank;
		
		tBank = playerManager.getBank ();
		assertEquals (mBank, tBank);
		assertEquals ("PM Mock Bank", tBank.getName ());
	}

	@Test
	@DisplayName ("Buy From Bank Pool Pay Cash To Test")
	void getPayCashToTest () {
		CashHolderI tCashHolder;
		Portfolio tSourcePortfolio;
		Bank tBank;
		
		tBank = playerManager.getBank ();
		tSourcePortfolio = mBankPool.getPortfolio ();
		Mockito.when (mCertificate.getShareCompany ()).thenReturn (ShareCompany.NO_SHARE_COMPANY);
		
		tCashHolder = playerManager.getPayCashTo (tBank, mCertificate, tSourcePortfolio);
		assertEquals ("PM Mock Bank", tCashHolder.getName ());		
	}
	
	@Test
	@DisplayName ("For Private and Minor Certificates Pay Cash To Test")
	void forNonShareGetPayCashToTest () {
		CashHolderI tCashHolder;
		Portfolio tSourcePortfolio;
		PrivateCompany tPrivateCompany;
		MinorCompany tMinorCompany;
		Certificate tPrivateCertificate;
		Certificate tMinorCertificate;
		Bank tBank;
		
		tBank = playerManager.getBank ();
		tSourcePortfolio = tBank.getPortfolio ();
		
		tPrivateCompany = companyTestFactory.buildAPrivateCompany (1);
		tPrivateCertificate = certificateTestFactory.buildCertificate (tPrivateCompany, true, 100,
							tSourcePortfolio);
		
		tCashHolder = playerManager.getPayCashTo (tBank, tPrivateCertificate, tSourcePortfolio);
		assertEquals ("PM Mock Bank", tCashHolder.getName ());		

		tMinorCompany = companyTestFactory.buildAMinorCompany (1);
		tMinorCertificate = certificateTestFactory.buildCertificate (tMinorCompany, true, 100,
							tSourcePortfolio);
		
		tCashHolder = playerManager.getPayCashTo (tBank, tMinorCertificate, tSourcePortfolio);
		assertEquals ("PM Mock Bank", tCashHolder.getName ());		
	}

	@Test
	@DisplayName ("For Non-Floated Share Company Pay Cash To Test")
	void forNonFloatedShareGetPayCashToTest () {
		CashHolderI tCashHolder;
		Portfolio tSourcePortfolio;
		ShareCompany tShareCompany;
		Certificate tShareCertificate;
		Bank tBank;
		
		tBank = playerManager.getBank ();
		tSourcePortfolio = tBank.getPortfolio ();
		
		tShareCompany = companyTestFactory.buildAShareCompany (1);
		tShareCompany.resetStatus (ActorI.ActionStates.Owned);
		tShareCertificate = certificateTestFactory.buildCertificate (tShareCompany, true, 100,
							tSourcePortfolio);
		
		tCashHolder = playerManager.getPayCashTo (tBank, tShareCertificate, tSourcePortfolio);
		assertEquals ("PM Mock Bank", tCashHolder.getName ());		
	}
}
