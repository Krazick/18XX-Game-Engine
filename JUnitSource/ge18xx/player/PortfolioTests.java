package ge18xx.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ge18xx.bank.BankTestFactory;
import ge18xx.bank.GameBank;
import ge18xx.company.Certificate;
import ge18xx.company.CertificateTestFactory;
import ge18xx.company.CompanyTestFactory;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;

class PortfolioTests {
	private BankTestFactory bankTestFactory;
	private GameTestFactory gameTestFactory;
	private PortfolioTestFactory portfolioTestFactory;
	private CertificateTestFactory certificateTestFactory;
	private CompanyTestFactory companyTestFactory;
	private GameManager mGameManager;
	private Portfolio portfolio;
	private GameBank gameBank;
	private Certificate mCertificate;

	@BeforeEach
	void setUp () throws Exception {
		bankTestFactory = new BankTestFactory ();
		gameTestFactory = new GameTestFactory ();
		mGameManager = gameTestFactory.buildGameManagerMock ();
		gameBank = bankTestFactory.buildGameBank (mGameManager);
		portfolioTestFactory = new PortfolioTestFactory (bankTestFactory);
		portfolio = portfolioTestFactory.buildPortfolio (gameBank);
		companyTestFactory = new CompanyTestFactory ();
		certificateTestFactory = new CertificateTestFactory ();
		mCertificate = certificateTestFactory.buildCertificateMock ();
	}

	@Nested
	@DisplayName ("Methods using certificates List")
	class CertificateListTests {
		@Test
		@DisplayName ("Count Tests")
		void certificateListTests () {
			portfolio.clearSelections ();
			assertEquals (0, portfolio.getCertificateTotalCount ());
			portfolio.addCertificate (mCertificate);
			assertEquals (1, portfolio.getCertificateTotalCount ());
			portfolio.clearSelections ();
		}
		
		@Test
		@DisplayName ("Certificate Sort Tests")
		void certificateSortTests () {
			ShareCompany tShareCompany;
			Portfolio tPortfolio;
			
			tShareCompany = companyTestFactory.buildAShareCompany (6);
			tPortfolio = tShareCompany.getCorporationCertificates ();
			assertEquals (12, tPortfolio.getCertificateTotalCount ());
			tPortfolio.sortByOwners ();
			System.out.println (tPortfolio.getCertificatePercentList (tShareCompany));
		}

		@Test
		@DisplayName ("Get Certificate by Index Tests")
		void getCertificateByIndexTest () {
			Certificate mCertificate1;
			Certificate tFoundCertificate;

			mCertificate1 = certificateTestFactory.buildCertificateMock ();
			tFoundCertificate = portfolio.getCertificate (0);
			assertNull (tFoundCertificate);

			portfolio.addCertificate (mCertificate);
			tFoundCertificate = portfolio.getCertificate (1);
			assertNull (tFoundCertificate);

			tFoundCertificate = portfolio.getCertificate (0);
			assertEquals (mCertificate, tFoundCertificate);

			portfolio.addCertificate (mCertificate1);
			tFoundCertificate = portfolio.getCertificate (5);
			assertNull (tFoundCertificate);

			tFoundCertificate = portfolio.getCertificate (-31);
			assertNull (tFoundCertificate);
		}
	}
}
