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
import ge18xx.company.CertificateTestFactory;
import ge18xx.company.CompanyTestFactory;
import ge18xx.company.Coupon;
import ge18xx.company.MinorCompany;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.game.Capitalization;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.round.RoundManager;
import ge18xx.round.RoundTestFactory;
import ge18xx.round.StockRound;
import ge18xx.round.action.ActorI;

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
	GameManager mGameManager;
	RoundManager roundManager;
	Bank mBank;
	BankPool mBankPool;
	StockRound mStockRound;
	String bankName;
	Portfolio bankPoolPortfolio;
	Portfolio bankPortfolio;
	GameInfo gameInfo;

	@BeforeEach
	void setUp () throws Exception {
		bankName = "PM Mock Bank";
		gameTestFactory = new GameTestFactory ();
		mGameManager = gameTestFactory.buildGameManagerMock ();
		gameInfo = gameTestFactory.buildGameInfo (2);
		mGameManager.setGameInfo (gameInfo);
		
		playerTestFactory = new PlayerTestFactory (mGameManager);
		bankTestFactory = new BankTestFactory ();
		roundTestFactory = new RoundTestFactory ();
		companyTestFactory = new CompanyTestFactory (gameTestFactory);
		certificateTestFactory = new CertificateTestFactory ();
		
		mBank = bankTestFactory.buildBankMock (mGameManager, bankName);
		bankPortfolio = new Portfolio (mBank);
		Mockito.when (mBank.getPortfolio ()).thenReturn (bankPortfolio);
		
		mBankPool = bankTestFactory.buildBankPoolMock (mGameManager, bankName);
		
		bankPoolPortfolio = new Portfolio (mBankPool);
		Mockito.when (mBankPool.getPortfolio ()).thenReturn (bankPoolPortfolio);

		mPrivateCompany = companyTestFactory.buildPrivateCompanyMock ("GETester");
		
		mCertificate = certificateTestFactory.buildCertificateMock ();
		playerManager = playerTestFactory.buildPlayerManager ();
		roundManager = roundTestFactory.buildRoundManager (mGameManager, playerManager);
		mStockRound = roundTestFactory.buildStockRoundMock (playerManager, roundManager);
		Mockito.when (mStockRound.getBank ()).thenReturn (mBank);
		playerManager.setStockRound (mStockRound);
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

	@Test
	@DisplayName ("For Operated Non-Destinated Share Company Pay Cash To Test")
	void forNoDestinationShareGetPayCashToTest () {
		CashHolderI tCashHolder;
		Portfolio tSourcePortfolio;
		ShareCompany tShareCompany;
		Certificate tShareCertificate;
		Bank tBank;
		
		tBank = playerManager.getBank ();
		tSourcePortfolio = tBank.getPortfolio ();
		
		tShareCompany = companyTestFactory.buildAShareCompany (2);
		tShareCompany.resetStatus (ActorI.ActionStates.Operated);
		tShareCertificate = certificateTestFactory.buildCertificate (tShareCompany, true, 100,
							tSourcePortfolio);
		
		tCashHolder = playerManager.getPayCashTo (tBank, tShareCertificate, tSourcePortfolio);
		assertEquals ("PM Mock Bank", tCashHolder.getName ());		
	}
	
	@Test
	@DisplayName ("For Operated Non-Destinated Share Company Pay Cash To Test")
	void forNonDestinationedShareGetPayCashToTest () {
		CashHolderI tCashHolder;
		Portfolio tSourcePortfolio;
		ShareCompany tShareCompany;
		Certificate tShareCertificate;
		Coupon mNextTrain;
		
		mNextTrain = Mockito.mock (Coupon.class);
		Mockito.when (mNextTrain.getName ()).thenReturn ("4");

		Mockito.when (mBank.getNextAvailableTrain ()).thenReturn (mNextTrain);

		tSourcePortfolio = mBank.getPortfolio ();
		
		tShareCompany = companyTestFactory.buildAShareCompany (3);
		tShareCompany.resetStatus (ActorI.ActionStates.Operated);
		tShareCompany.setReachedDestination (false);
		tShareCertificate = certificateTestFactory.buildCertificate (tShareCompany, true, 100,
							tSourcePortfolio);
		
		tCashHolder = playerManager.getPayCashTo (mBank, tShareCertificate, tSourcePortfolio);
		assertEquals ("PM Mock Bank", tCashHolder.getName ());		
	}
	
	@Test
	@DisplayName ("For Operated Destinated Share Company Pay Cash To Test")
	void forDestinationedShareGetPayCashToTest () {
		CashHolderI tCashHolder;
		Portfolio tSourcePortfolio;
		ShareCompany tShareCompany;
		Certificate tShareCertificate;
		String tResult = "Buffalo, Brantford & Goderich Railway";
		Coupon mNextTrain;

		mNextTrain = Mockito.mock (Coupon.class);
		Mockito.when (mNextTrain.getName ()).thenReturn ("4");

		Mockito.when (mBank.getNextAvailableTrain ()).thenReturn (mNextTrain);

		tSourcePortfolio = mBank.getPortfolio ();
		
		tShareCompany = companyTestFactory.buildAShareCompany (3);
		tShareCompany.resetStatus (ActorI.ActionStates.Operated);
		tShareCompany.setReachedDestination (true);
		tShareCertificate = certificateTestFactory.buildCertificate (tShareCompany, true, 100,
							tSourcePortfolio);
		
		tCashHolder = playerManager.getPayCashTo (mBank, tShareCertificate, tSourcePortfolio);
		assertEquals (tResult, tCashHolder.getName ());		
	}

	@Test
	@DisplayName ("For Operated Destinated Share Company with Full Capitalization Pay Cash To Test")
	void forDestinationedFullCapShareGetPayCashToTest () {
		CashHolderI tCashHolder;
		Portfolio tSourcePortfolio;
		ShareCompany tShareCompany;
		Certificate tShareCertificate;
		String tResult = "PM Mock Bank";
		Coupon mNextTrain;

		mNextTrain = Mockito.mock (Coupon.class);
		Mockito.when (mNextTrain.getName ()).thenReturn ("4");

		Mockito.when (mBank.getNextAvailableTrain ()).thenReturn (mNextTrain);

		tSourcePortfolio = mBank.getPortfolio ();
		
		tShareCompany = companyTestFactory.buildAShareCompany (3);
		tShareCompany.resetStatus (ActorI.ActionStates.Operated);
		tShareCompany.setReachedDestination (true);
		tShareCertificate = certificateTestFactory.buildCertificate (tShareCompany, true, 100,
							tSourcePortfolio);
		Mockito.when (mGameManager.getCapitalizationLevel (Mockito.anyInt ())).thenReturn (Capitalization.INCREMENTAL_10_MAX);
		tCashHolder = playerManager.getPayCashTo (mBank, tShareCertificate, tSourcePortfolio);
		assertEquals (tResult, tCashHolder.getName ());		
	}

	@Test
	@DisplayName ("For Operated Non-Destinated Share Company with Full Capitalizaiton Pay Cash To Test")
	void forNonDestinationedFullCapShareGetPayCashToTest () {
		CashHolderI tCashHolder;
		Portfolio tSourcePortfolio;
		ShareCompany tShareCompany;
		Certificate tShareCertificate;
		String tResult = "PM Mock Bank";
		Coupon mNextTrain;

		mNextTrain = Mockito.mock (Coupon.class);
		Mockito.when (mNextTrain.getName ()).thenReturn ("4");

		Mockito.when (mBank.getNextAvailableTrain ()).thenReturn (mNextTrain);

		tSourcePortfolio = mBank.getPortfolio ();
		
		tShareCompany = companyTestFactory.buildAShareCompany (3);
		tShareCompany.resetStatus (ActorI.ActionStates.Operated);
		tShareCompany.setReachedDestination (false);
		tShareCertificate = certificateTestFactory.buildCertificate (tShareCompany, true, 100,
							tSourcePortfolio);
		Mockito.when (mGameManager.getCapitalizationLevel (Mockito.anyInt ())).thenReturn (Capitalization.INCREMENTAL_10_MAX);
		tCashHolder = playerManager.getPayCashTo (mBank, tShareCertificate, tSourcePortfolio);
		assertEquals (tResult, tCashHolder.getName ());		
	}
}
