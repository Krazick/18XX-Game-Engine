package ge18xx.player;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.bank.BankTestFactory;
import ge18xx.bank.GameBank;
import ge18xx.company.Certificate;
import ge18xx.company.CertificateTestFactory;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;

class PortfolioTests {
	private BankTestFactory bankTestFactory;
	private GameTestFactory gameTestFactory;
	private PortfolioTestFactory portfolioTestFactory;
	private CertificateTestFactory certificateTestFactory;
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
		certificateTestFactory = new CertificateTestFactory ();
		mCertificate = certificateTestFactory.buildCertificateMock ();
	}

	@Test
	@DisplayName ("Methods using certificates List Tests")
	void certificateListTests () {

		portfolio.clearSelections ();
		assertEquals (0, portfolio.getCertificateTotalCount ());
		portfolio.addCertificate (mCertificate);
		assertEquals (1, portfolio.getCertificateTotalCount ());
		portfolio.clearSelections ();
	}

}
