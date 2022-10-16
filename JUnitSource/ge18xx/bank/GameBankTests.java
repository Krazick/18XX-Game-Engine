package ge18xx.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
import ge18xx.player.PortfolioTestFactory;
import ge18xx.train.Train;
import ge18xx.train.TrainPortfolio;
import ge18xx.train.TrainTestFactory;
import ge18xx.utilities.UtilitiesTestFactory;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

@DisplayName ("Game Bank Tests")
class GameBankTests {
	private BankTestFactory bankTestFactory;
	private GameTestFactory gameTestFactory;
	private CompanyTestFactory companyTestFactory;
	private CertificateTestFactory certificateTestFactory;
	private TrainTestFactory trainTestFactory;
	private UtilitiesTestFactory utilitiesTestFactory;
	private PortfolioTestFactory portfolioTestFactory;
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
		trainTestFactory = new TrainTestFactory ();
		utilitiesTestFactory = new UtilitiesTestFactory ();
		portfolioTestFactory = new PortfolioTestFactory (bankTestFactory);
		
		mGameManager = gameTestFactory.buildGameManagerMock ();
		gameBank = bankTestFactory.buildGameBank (mGameManager);
		
		mPortfolio = portfolioTestFactory.buildPortfolioMock (gameBank);
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
		assertEquals ("GameBank", tGameBank.getAbbrev ());
		
		assertEquals (0, tGameBank.getTrainLimit ());
		assertTrue (tGameBank.isABank ());
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
	
	@DisplayName ("Get Cash Holder Test")
	@Test
	void getCashHolderTest () {
		Bank mBank;
		
		mBank = bankTestFactory.buildBankMock (mGameManager);
		Mockito.when (mGameManager.getBank ()).thenReturn (mBank);
		assertEquals (mBank, gameBank.getCashHolder ());
		
		assertEquals (mBank, gameBank.getBank ());
	}
	
	@DisplayName ("Get Porfolio Holder Test")
	@Test
	void getPortfolioHolderTest () {
		assertEquals (gameBank, gameBank.getPortfolioHolder ());
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
			Certificate tCertificateRemoved;
			Certificate tCertificateNotRemoved;
			Certificate mGeneratedCertificate;
			
			mGeneratedCertificate = certificateTestFactory.buildCertificateMock ();

			Mockito.when (mPortfolio.getCertificateFor (any (Corporation.class))).thenReturn (mGeneratedCertificate);
			Mockito.when (mPortfolio.getCertificateFor (any (Corporation.class), anyBoolean ())).thenReturn (mGeneratedCertificate);
			tShareCompany = companyTestFactory.buildAShareCompany (1);
			tCertificateRemoved = gameBank.getCertificateFromCorp (tShareCompany);
			assertEquals (mGeneratedCertificate, tCertificateRemoved);
			Mockito.verify (mPortfolio, times (1)).getCertificateFor (tShareCompany);
			
			tCertificateNotRemoved = gameBank.getCertificateFromCorp (tShareCompany, false);
			assertEquals (mGeneratedCertificate, tCertificateNotRemoved);
			Mockito.verify (mPortfolio, times (1)).getCertificateFor (tShareCompany, false);

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

		@DisplayName ("getCurrentHolder from GameManager Test")
		@Test
		void getCurrentHolderGMTest () {
			LoadedCertificate mLoadedCertificate;
			PortfolioHolderLoaderI mPortfolioHolderLoaderI;
			PortfolioHolderLoaderI mFoundPortfolioHolderLoaderI;
			
			mLoadedCertificate = certificateTestFactory.buildLoadedCertificateMock ();
			mPortfolioHolderLoaderI = Mockito.mock (PortfolioHolderLoaderI.class);
			Mockito.when (mGameManager.getCurrentHolder (mLoadedCertificate)).thenReturn (mPortfolioHolderLoaderI);
			mFoundPortfolioHolderLoaderI = gameBank.getCurrentHolderGM (mLoadedCertificate);
			assertEquals (mPortfolioHolderLoaderI, mFoundPortfolioHolderLoaderI);
			Mockito.verify (mGameManager, times (1)).getCurrentHolder (mLoadedCertificate);
		}
	}
	
	@DisplayName ("Train Portfolio interaction")
	@Nested
	class trainPortfolioInteractionTests {
		@DisplayName ("getCheapestTrain Test")
		@Test
		void getCheapestTrainTest () {
			Train tCheapestTrain;
			Train mGeneratedCheapestTrain;
			
			mGeneratedCheapestTrain = trainTestFactory.buildTrainMock ();

			Mockito.when (mTrainPortfolio.getCheapestTrain ()).thenReturn (mGeneratedCheapestTrain);
			tCheapestTrain = gameBank.getCheapestTrain ();
			assertEquals (mGeneratedCheapestTrain, tCheapestTrain);
			Mockito.verify (mTrainPortfolio, times (1)).getCheapestTrain ();
		}
		
		@DisplayName ("getSelectedTrain Test")
		@Test
		void getSelectedTrainTest () {
			Train tSelectedTrain;
			Train mGeneratedSelectedTrain;
			
			mGeneratedSelectedTrain = trainTestFactory.buildTrainMock ();

			Mockito.when (mTrainPortfolio.getSelectedTrain ()).thenReturn (mGeneratedSelectedTrain);
			tSelectedTrain = gameBank.getSelectedTrain ();
			assertEquals (mGeneratedSelectedTrain, tSelectedTrain);
			Mockito.verify (mTrainPortfolio, times (1)).getSelectedTrain ();
		}
		
		@DisplayName ("getSelectedTrainCount Test")
		@Test
		void getSelectedTrainCountTest () {
			int tSelectedTrainCount;
			
			Mockito.when (mTrainPortfolio.getSelectedCount ()).thenReturn (2);
			tSelectedTrainCount = gameBank.getSelectedTrainCount ();
			assertEquals (2, tSelectedTrainCount);
			Mockito.verify (mTrainPortfolio, times (1)).getSelectedCount ();
			
			tSelectedTrainCount = gameBank.getLocalSelectedTrainCount ();
			assertEquals (2, tSelectedTrainCount);
			Mockito.verify (mTrainPortfolio, times (2)).getSelectedCount ();
		}
		
		@DisplayName ("getTrain Test")
		@Test
		void getTrainTest () {
			Train tSelectedTrain;
			Train mGeneratedSelectedTrain;
			
			mGeneratedSelectedTrain = trainTestFactory.buildTrainMock ();

			Mockito.when (mTrainPortfolio.getTrain ("3")).thenReturn (mGeneratedSelectedTrain);
			tSelectedTrain = gameBank.getTrain ("3");
			assertEquals (mGeneratedSelectedTrain, tSelectedTrain);
			Mockito.verify (mTrainPortfolio, times (1)).getTrain ("3");
		}
		
		@DisplayName ("getTrainQuantity Test")
		@Test
		void getTrainQuantityTest () {
			int tTrainQuantity;
			
			Mockito.when (mTrainPortfolio.getTrainQuantity ("3")).thenReturn (5);
			tTrainQuantity = gameBank.getTrainQuantity ("3");
			assertEquals (5, tTrainQuantity);
			Mockito.verify (mTrainPortfolio, times (1)).getTrainQuantity ("3");
		}
		
		@DisplayName ("getTrainNameAndQty Test")
		@Test
		void getTrainNameAndQtyTest () {
			String tNameAndQuantity;
			
			Mockito.when (mTrainPortfolio.getTrainNameAndQty ("AVAILABLE")).thenReturn ("3 (5)");
			tNameAndQuantity = gameBank.getTrainNameAndQty ("AVAILABLE");
			assertEquals ("3 (5)", tNameAndQuantity);
			Mockito.verify (mTrainPortfolio, times (1)).getTrainNameAndQty ("AVAILABLE");
		}
		
		@DisplayName ("hasTrainNamed Test")
		@Test
		void hasTrainNamedTest () {
			boolean tHasTrainNamed;
			
			Mockito.when (mTrainPortfolio.hasTrainNamed ("3")).thenReturn (true);
			Mockito.when (mTrainPortfolio.hasTrainNamed ("4")).thenReturn (false);
			tHasTrainNamed = gameBank.hasTrainNamed ("3");
			assertTrue (tHasTrainNamed);
			Mockito.verify (mTrainPortfolio, times (1)).hasTrainNamed ("3");
			tHasTrainNamed = gameBank.hasTrainNamed ("5");
			assertFalse (tHasTrainNamed);
			Mockito.verify (mTrainPortfolio, times (1)).hasTrainNamed ("5");
		}
		
		@DisplayName ("removeSelectedTrain Test")
		@Test
		void removeSelectedTrainTest () {
			boolean tHasTrainNamed;
			
			Mockito.when (mTrainPortfolio.removeSelectedTrain ()).thenReturn (true);
			tHasTrainNamed = gameBank.removeSelectedTrain ();
			assertTrue (tHasTrainNamed);
			Mockito.verify (mTrainPortfolio, times (1)).removeSelectedTrain ();
		}
		
		@DisplayName ("removeTrain Test")
		@Test
		void removeTrainTest () {
			boolean tTrainRemoved;
			
			Mockito.when (mTrainPortfolio.removeTrain ("3")).thenReturn (true);
			Mockito.when (mTrainPortfolio.hasTrainNamed ("4")).thenReturn (false);
			tTrainRemoved = gameBank.removeTrain ("3");
			assertTrue (tTrainRemoved);
			Mockito.verify (mTrainPortfolio, times (1)).removeTrain ("3");
		}
		
		@DisplayName ("getAvailableTrains Test")
		@Test
		void getAvailableTrainsTest () {
			Train [] tAvailableTrains;
			Train [] tGeneratedAvailableTrains;
			Train mGeneratedTrain;
			Train tFoundTrain;
			
			tGeneratedAvailableTrains = new Train [10];
			mGeneratedTrain = trainTestFactory.buildTrainMock ();
			tGeneratedAvailableTrains [0] = mGeneratedTrain;
			
			Mockito.when (mTrainPortfolio.getAvailableTrains ()).thenReturn (tGeneratedAvailableTrains);
			tAvailableTrains = gameBank.getAvailableTrains ();
			tFoundTrain = tAvailableTrains [0];
			assertEquals (mGeneratedTrain, tFoundTrain);
			Mockito.verify (mTrainPortfolio, times (1)).getAvailableTrains ();
		}
		
		@DisplayName ("getTrainSummary Test")
		@Test
		void getTrainSummaryTest () {
			String tTrainSummary;
			
			Mockito.when (mTrainPortfolio.getTrainSummary ()).thenReturn ("Game Bank Train Summary");
			tTrainSummary = gameBank.getTrainSummary ();
			assertEquals ("Game Bank Train Summary", tTrainSummary);
			Mockito.verify (mTrainPortfolio, times (1)).getTrainSummary ();
		}
		
		@DisplayName ("loadTrainPortfolio Test")
		@Test
		void loadTrainPortfolioTest () {
			XMLNode mXMLNodeTrainPortfolio;
			Bank mBank;
			
			mBank = bankTestFactory.buildBankMock (mGameManager);
			Mockito.when (mGameManager.getBank ()).thenReturn (mBank);
			
			mXMLNodeTrainPortfolio = utilitiesTestFactory.buildXMLNodeMock ();
			Mockito.doNothing ().when (mTrainPortfolio).loadTrainPortfolioFromBank (mXMLNodeTrainPortfolio, mBank);
			gameBank.loadTrainPortfolio (mXMLNodeTrainPortfolio);
			Mockito.verify (mTrainPortfolio, times (1)).loadTrainPortfolioFromBank (mXMLNodeTrainPortfolio, mBank);
		}
		
		@DisplayName ("hasAnyTrains Test")
		@Test
		void hasAnyTrainsTest () {
			Mockito.when (mTrainPortfolio.getTrainCount ()).thenReturn (1);
			assertTrue (gameBank.hasAnyTrains ());
			Mockito.verify (mTrainPortfolio, times (1)).getTrainCount ();

			Mockito.when (mTrainPortfolio.getTrainCount ()).thenReturn (0);
			assertFalse (gameBank.hasAnyTrains ());
			Mockito.verify (mTrainPortfolio, times (2)).getTrainCount ();
		}
		
		@DisplayName ("getTrainPortfolioElements Test")
		@Test
		void getTrainPortfolioElementsTest () {
			XMLElement mXMLElement;
			XMLDocument mXMLDocument;
			
			mXMLDocument = utilitiesTestFactory.buildXMLDocumentMock ();
			mXMLElement = utilitiesTestFactory.buildXMLElementMock ();
			Mockito.when (mTrainPortfolio.getElements (mXMLDocument)).thenReturn (mXMLElement);
			
			gameBank.getTrainPortfolioElements (mXMLDocument);
			Mockito.verify (mTrainPortfolio, times (1)).getElements (mXMLDocument);
		}
	}
	
	@DisplayName ("Portfolio Interaction")
	@Nested
	class portfolioInteractionTests {
		@DisplayName ("getPortfolioElements Test")
		@Test
		void getPortfolioElementsTest () {
			XMLElement mXMLElement;
			XMLDocument mXMLDocument;
			
			mXMLDocument = utilitiesTestFactory.buildXMLDocumentMock ();
			mXMLElement = utilitiesTestFactory.buildXMLElementMock ();
			Mockito.when (mPortfolio.getElements (mXMLDocument)).thenReturn (mXMLElement);
			
			gameBank.getPortfolioElements (mXMLDocument);
			Mockito.verify (mPortfolio, times (1)).getElements (mXMLDocument);
		}
		
		@DisplayName ("loadPortfolio Test")
		@Test
		void loadPortfolioTest () {
			XMLNode mXMLNodePortfolio;
			
			mXMLNodePortfolio = utilitiesTestFactory.buildXMLNodeMock ();
			Mockito.doNothing ().when (mPortfolio).loadPortfolio (mXMLNodePortfolio);
			gameBank.loadPortfolio (mXMLNodePortfolio);
			Mockito.verify (mPortfolio, times (1)).loadPortfolio (mXMLNodePortfolio);
		}
	}
}
