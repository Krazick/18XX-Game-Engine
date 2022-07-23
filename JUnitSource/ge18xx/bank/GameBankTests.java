package ge18xx.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.company.Certificate;
import ge18xx.company.CertificateTestFactory;
import ge18xx.company.CompanyTestFactory;
import ge18xx.company.Corporation;
import ge18xx.company.LoadedCertificate;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderLoaderI;
import ge18xx.train.Train;
import ge18xx.train.TrainPortfolio;

@DisplayName ("Game Bank Tests")
class GameBankTests {
	private BankTestFactory bankTestFactory;
	private GameTestFactory gameTestFactory;
	private CompanyTestFactory companyTestFactory;
	private CertificateTestFactory certificateTestFactory;
	private GameManager mGameManager;
	private Portfolio mPortfolio;
	private TrainPortfolio mTrainPortfolio;
	private GameBank gameBank;
	
	@BeforeEach
	void setUp () throws Exception {
		
		bankTestFactory = new BankTestFactory ();
		gameTestFactory = new GameTestFactory ();
		companyTestFactory = new CompanyTestFactory (gameTestFactory);
		certificateTestFactory = new CertificateTestFactory ();
		
		mGameManager = gameTestFactory.buildGameManagerMock ();
		gameBank = bankTestFactory.buildGameBank (mGameManager);
		 
		mPortfolio = Mockito.mock (Portfolio.class);
		Mockito.when (mPortfolio.getName ()).thenReturn ("Portfolio Mock Name");
		mTrainPortfolio = Mockito.mock (TrainPortfolio.class);
		Mockito.when (mTrainPortfolio.getName ()).thenReturn ("Train Portfolio Mock Name");
		
		gameBank.setPortfolio (mPortfolio);
		gameBank.setTrainPortfolio (mTrainPortfolio);
	}

	@DisplayName ("Constructor Tests")
	@Test
	void gameBankConstructorTests () {
		GameBank tGameBank;
		Portfolio tPortfolio;
		TrainPortfolio tTrainPortfolio;
		
		tGameBank = bankTestFactory.buildGameBank (mGameManager);
		assertEquals ("Test Game Bank", tGameBank.getName ());
		tGameBank.setPortfolio (mPortfolio);
		tGameBank.setTrainPortfolio (mTrainPortfolio);
		
		tPortfolio = tGameBank.getPortfolio ();
		tTrainPortfolio = tGameBank.getTrainPortfolio ();
		
		assertEquals ("Portfolio Mock Name", tPortfolio.getName ());
		assertEquals ("Train Portfolio Mock Name", tTrainPortfolio.getName ());
		assertEquals ("Fixed", tGameBank.getStateName ());
	}

	@DisplayName ("Add Certificate Test")
	@Test
	void addCertificateTest () {
		Certificate mCertificate;
		
		mCertificate  = certificateTestFactory.buildCertificateMock ();
		Mockito.doNothing ().when (mPortfolio).addCertificate (any (Certificate.class));
		
		gameBank.addCertificate (mCertificate);
		Mockito.verify (mPortfolio, times (1)).addCertificate (any (Certificate.class));
	}
	
	@DisplayName ("Add Train Test")
	@Test
	void addTrainTest () {
		Train mTrain;
		
		mTrain  = Mockito.mock (Train.class);
		Mockito.doNothing ().when (mTrainPortfolio).addTrain (any (Train.class));
		
		gameBank.addTrain (mTrain);
		Mockito.verify (mTrainPortfolio, times (1)).addTrain (any (Train.class));
	}
	
	@DisplayName ("Clear Selections Test")
	@Test
	void clearSelectionsTest () {
		Mockito.doNothing ().when (mTrainPortfolio).clearSelections ();
		Mockito.doNothing ().when (mPortfolio).clearSelections ();
		
		gameBank.clearSelections ();
		Mockito.verify (mPortfolio, times (1)).clearSelections ();
		Mockito.verify (mTrainPortfolio, times (1)).clearSelections ();
	}
	
	@DisplayName ("Get Certificate")
	@Nested
	class getCertificateTests {
		@DisplayName ("getCertificatePercentageFor Test")
		@Test
		void getCertificatePercentageForTest () {
			ShareCompany tShareCompany;
			int tShareCompanyPercentage;
			
			Mockito.when (mPortfolio.getCertificatePercentageFor (any (Corporation.class))).thenReturn (30);
			tShareCompany = companyTestFactory.buildAShareCompany (1);
			tShareCompanyPercentage = gameBank.getCertificatePercentageFor (tShareCompany);
			assertEquals (30, tShareCompanyPercentage);
			Mockito.verify (mPortfolio, times (1)).getCertificatePercentageFor (tShareCompany);
		}

		@DisplayName ("getCertificateCountFor Test")
		@Test
		void getCertificateCountForTest () {
			ShareCompany tShareCompany;
			int tShareCompanyCount;
			
			Mockito.when (mPortfolio.getCertificateCountFor (any (Corporation.class))).thenReturn (3);
			tShareCompany = companyTestFactory.buildAShareCompany (1);
			tShareCompanyCount = gameBank.getCertificateCountFor (tShareCompany);
			assertEquals (3, tShareCompanyCount);
			Mockito.verify (mPortfolio, times (1)).getCertificateCountFor (tShareCompany);
		}
		
		@DisplayName ("getCertificateFromCorp Test")
		@Test
		void getCertificateFromCorpTest () {
			ShareCompany tShareCompany;
			Certificate tCertificate;
			Certificate mGeneratedCertificate;
			
			mGeneratedCertificate = certificateTestFactory.buildCertificateMock ();

			Mockito.when (mPortfolio.getCertificateFor (any (Corporation.class))).thenReturn (mGeneratedCertificate);
			tShareCompany = companyTestFactory.buildAShareCompany (1);
			tCertificate = gameBank.getCertificateFromCorp (tShareCompany);
			assertEquals (mGeneratedCertificate, tCertificate);
			Mockito.verify (mPortfolio, times (1)).getCertificateFor (tShareCompany);
		}

		@DisplayName ("getCertificateToBidOn Test")
		@Test
		void getCertificateToBidOnTest () {
			Certificate tCertificate;
			Certificate mGeneratedCertificate;
			
			mGeneratedCertificate = certificateTestFactory.buildCertificateMock ();

			Mockito.when (mPortfolio.getCertificateToBidOn ()).thenReturn (mGeneratedCertificate);
			tCertificate = gameBank.getCertificateToBidOn ();
			assertEquals (mGeneratedCertificate, tCertificate);
			Mockito.verify (mPortfolio, times (1)).getCertificateToBidOn ();
		}

		@DisplayName ("getCertificateToBuy Test")
		@Test
		void getCertificateToBuyTest () {
			Certificate tCertificate;
			Certificate mGeneratedCertificate;
			
			mGeneratedCertificate = certificateTestFactory.buildCertificateMock ();

			Mockito.when (mPortfolio.getCertificateToBuy ()).thenReturn (mGeneratedCertificate);
			tCertificate = gameBank.getCertificateToBuy ();
			assertEquals (mGeneratedCertificate, tCertificate);
			Mockito.verify (mPortfolio, times (1)).getCertificateToBuy ();
		}

		@DisplayName ("getCertificatesToBuy Test")
		@Test
		void getCertificatesToBuyTest () {
			List<Certificate> tCertificates;
			List<Certificate> mGeneratedCertificates;
			
			mGeneratedCertificates = certificateTestFactory.buildListCertificatesMock ();

			Mockito.when (mPortfolio.getCertificatesToBuy ()).thenReturn (mGeneratedCertificates);
			tCertificates = gameBank.getCertificatesToBuy ();
			assertEquals (mGeneratedCertificates, tCertificates);
			Mockito.verify (mPortfolio, times (1)).getCertificatesToBuy ();
		}
	}
	
	@DisplayName ("Get PortfolioHolderLoaderI")
	@Nested
	class getPortfolioHolderLoaderITests {
		@DisplayName ("getCurrentHolder Test")
		@Test
		void getCurrentHolderTest () {
			LoadedCertificate mLoadedCertificate;
			PortfolioHolderLoaderI mPortfolioHolderLoaderI;
			PortfolioHolderLoaderI mFoundPortfolioHolderLoaderI;
			
			mLoadedCertificate = certificateTestFactory.buildLoadedCertificateMock ();
			mPortfolioHolderLoaderI = Mockito.mock (PortfolioHolderLoaderI.class);
			Mockito.when (mPortfolio.getCurrentHolder (any (LoadedCertificate.class))).thenReturn (mPortfolioHolderLoaderI);
			mFoundPortfolioHolderLoaderI = gameBank.getCurrentHolder (mLoadedCertificate);
			assertEquals (mPortfolioHolderLoaderI, mFoundPortfolioHolderLoaderI);
			Mockito.verify (mPortfolio, times (1)).getCurrentHolder (mLoadedCertificate);
		}
	}
}
