package ge18xx.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.BankTestFactory;
import ge18xx.company.Certificate;
import ge18xx.company.CertificateTestFactory;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.round.action.ActorI;

class PlayerTests {
	GameManager mGameManager;
	GameTestFactory gameTestFactory;
	BankTestFactory bankTestFactory;
	Bank bank;
	BankPool bankPool;
	CertificateTestFactory certificateTestFactory;
	PlayerTestFactory playerTestFactory;
	PlayerManager mPlayerManager;

	GameInfo mGameInfo;
	int playerCount;
	int certificateLimit;
	String playerName;
	Player player;
	Portfolio playerPortfolio;
	
	@BeforeEach
	void setUp () throws Exception {
		GameInfo tGameInfo;
		
		gameTestFactory = new GameTestFactory ();

		tGameInfo = gameTestFactory.buildGameInfo (1);
		mGameManager = gameTestFactory.buildGameManagerMock ();
		Mockito.when (mGameManager.getMaxRounds ()).thenReturn (1);
		Mockito.when (mGameManager.getActiveGame ()).thenReturn (tGameInfo);
		
		mGameInfo = gameTestFactory.buildGameInfoMock ();
		Mockito.when (mGameInfo.hasAuctionRound ()).thenReturn (true);
		Mockito.when (mGameManager.getActiveGame ()).thenReturn (mGameInfo);

		bankTestFactory = new BankTestFactory ();
		bank = bankTestFactory.buildBank ();
		bankPool = bankTestFactory.buildBankPool (mGameManager);
		certificateTestFactory = new CertificateTestFactory ();
		playerTestFactory = new PlayerTestFactory (mGameManager);
		
		playerCount = 4;
		certificateLimit = 16;
		playerName = "BusterPlayer";
		mPlayerManager = playerTestFactory.buildPlayerManagerMock (playerCount);
		Mockito.when (mPlayerManager.getBank ()).thenReturn (bank);
		Mockito.when (mPlayerManager.getBankPool ()).thenReturn (bankPool);
		player = playerTestFactory.buildPlayer (playerName, mPlayerManager, certificateLimit);
		playerPortfolio = new Portfolio (player);
		player.setPortfolio (playerPortfolio);
	}

	@Test
	@DisplayName ("Player get routine Tests")
	void playerGetRoutineTests () {
		Bank tBank;
		BankPool tBankPool;
		GameManager tGameManager;
		Portfolio tPortfolio;
		PortfolioHolderI tPortfolioHolder;
		
		tGameManager = player.getGameManager ();
		assertEquals (mGameManager, tGameManager);
		
		tBank = player.getBank ();
		assertEquals (bank, tBank);
		
		tBankPool = player.getBankPool ();
		assertEquals (bankPool, tBankPool);
		
		tPortfolio = player.getPortfolio ();
		assertEquals (playerPortfolio, tPortfolio);
		
		tPortfolioHolder = player.getPortfolioHolder ();
		assertEquals (player, tPortfolioHolder);
		
		assertFalse (player.gameHasMinors ());
		player.setGameHasMinors (true);
		assertTrue (player.gameHasMinors ());
		
		assertFalse (player.gameHasPrivates ());
		player.setGameHasPrivates (true);
		assertTrue (player.gameHasPrivates ());
		
		assertFalse (player.gameHasShares ());
		player.setGameHasShares (true);
		assertTrue (player.gameHasShares ());
		
		assertEquals (ActorI.ActionStates.NoAction, player.getAuctionActionState ());
		player.setAuctionActionState (ActorI.ActionStates.AuctionRaised);
		assertEquals (ActorI.ActionStates.AuctionRaised, player.getAuctionActionState ());
	}
	
	@Test
	@DisplayName ("Player Certificate Tests")
	void playerCertificateTests () {
		Certificate mCertificate1;
		Certificate mCertificate2;
		
		assertEquals (16, player.getCertificateLimit ());
		
		mCertificate1 = certificateTestFactory.buildCertificateMock ();
		Mockito.when (mCertificate1.countsAgainstCertificateLimit ()).thenReturn (true);
		player.addCertificate (mCertificate1);
		assertEquals (1, player.getCertificateCount ());
		assertEquals (1, player.getCertificateTotalCount ());
		
		mCertificate2 = certificateTestFactory.buildCertificateMock ();
		Mockito.when (mCertificate2.countsAgainstCertificateLimit ()).thenReturn (false);
		player.addCertificate (mCertificate2);
		assertEquals (1, player.getCertificateCount ());
		assertEquals (2, player.getCertificateTotalCount ());
		
		player.setPortfolio (Portfolio.NO_PORTFOLIO);
		assertEquals (0, player.getCertificateCount ());
		assertEquals (0, player.getCertificateTotalCount ());
	}
	
	@Test
	@DisplayName ("Player at Certificate Limit Tests")
	void playerAtCertLimitTests () {
		Certificate mCertificate1;
		Certificate mCertificate2;
		Certificate mCertificate3;
		Certificate mCertificate4;
		
		player.setCertificateLimit (3);
		assertEquals (3, player.getCertificateLimit ());
		
		mCertificate1 = certificateTestFactory.buildCertificateMock ();
		Mockito.when (mCertificate1.countsAgainstCertificateLimit ()).thenReturn (true);
		player.addCertificate (mCertificate1);
		
		mCertificate2 = certificateTestFactory.buildCertificateMock ();
		Mockito.when (mCertificate2.countsAgainstCertificateLimit ()).thenReturn (true);
		player.addCertificate (mCertificate2);

		assertEquals (2, player.getCertificateTotalCount ());
		assertFalse (player.atCertLimit ());
		assertEquals (-1, player.exceedsCertificateLimitBy ());
		
		mCertificate3 = certificateTestFactory.buildCertificateMock ();
		Mockito.when (mCertificate3.countsAgainstCertificateLimit ()).thenReturn (true);
		player.addCertificate (mCertificate3);

		assertEquals (3, player.getCertificateTotalCount ());
		assertTrue (player.atCertLimit ());
		assertEquals (0, player.exceedsCertificateLimitBy ());

		mCertificate4 = certificateTestFactory.buildCertificateMock ();
		Mockito.when (mCertificate4.countsAgainstCertificateLimit ()).thenReturn (true);
		player.addCertificate (mCertificate4);

		assertEquals (4, player.getCertificateTotalCount ());
		assertTrue (player.atCertLimit ());
		assertEquals (1, player.exceedsCertificateLimitBy ());
	}
}
