package ge18xx.round.action.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankTestFactory;
import ge18xx.company.Certificate;
import ge18xx.company.CertificateTestFactory;
import ge18xx.company.CompanyTestFactory;
import ge18xx.company.ShareCompany;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.PlayerTestFactory;
import ge18xx.player.Portfolio;
import ge18xx.round.RoundTestFactory;
import ge18xx.round.action.ActorI;

@DisplayName ("Response to Offer Effect Constructor Tests")
public class ResponseOfferEffectTestConstructor {
	ResponseOfferEffect effectAlpha;
	ResponseOfferEffect effectBeta;
	ResponseOfferEffect effectChi;
	ResponseOfferEffect effectEpsilon;
	ShareCompany companyBeta;
	TrainCompany companyGamma;
	Player mPlayerActorAlpha;
	Player mPlayerActorDelta;
	Player playerActorGamma;
	GameManager mGameManager;
	PlayerManager playerManager;
	GameTestFactory gameTestFactory;
	PlayerTestFactory playerTestFactory;
	CompanyTestFactory companyTestFactory;
	RoundTestFactory roundTestFactory;
	CertificateTestFactory certificateTestFactory;
	Certificate certificate;
	GameInfo mGameInfo;
	Bank bank;
	BankTestFactory bankTestFactory;

	@BeforeEach
	void setUp () throws Exception {
		String tClientName;
		String tPlayer1Name;
		String tPlayer2Name;
		String tPlayer3Name;
		Portfolio mPortfolioAlpha;
		Portfolio tGammaPortfolio;
		boolean tResponse;
		String tItemType;
		String tItemName;

		tClientName = "TFBuster";
		tPlayer1Name = "ToEffectTesterGamma";
		tPlayer2Name = "ToEffectTesterAlpha";
		tPlayer3Name = "ToEffectTesterDelta";
		gameTestFactory = new GameTestFactory ();
		companyTestFactory = new CompanyTestFactory (gameTestFactory);
		certificateTestFactory = new CertificateTestFactory ();
		roundTestFactory = new RoundTestFactory ();
		bankTestFactory = new BankTestFactory ();
		bank = bankTestFactory.buildBank ();
		
		mGameManager = gameTestFactory.buildGameManagerMock (tClientName);
		Mockito.when (mGameManager.gameHasPrivates ()).thenReturn (true);
		Mockito.when (mGameManager.gameHasMinors ()).thenReturn (false);
		Mockito.when (mGameManager.gameHasShares ()).thenReturn (true);
		
		mGameInfo = gameTestFactory.buildGameInfoMock ();
		Mockito.when (mGameInfo.hasAuctionRound ()).thenReturn (true);
		Mockito.when (mGameManager.getActiveGame ()).thenReturn (mGameInfo);

		playerTestFactory = new PlayerTestFactory (mGameManager);
		playerManager = playerTestFactory.buildPlayerManager ();

		effectAlpha = new ResponseOfferEffect ();
		
		mPlayerActorAlpha = playerTestFactory.buildPlayerMock (tPlayer2Name);
		mPlayerActorDelta = playerTestFactory.buildPlayerMock (tPlayer3Name);
		playerActorGamma = playerTestFactory.buildPlayer (tPlayer1Name, playerManager, 
								PlayerManager.CERTIFICATE_LIMIT_ZERO);
		mPortfolioAlpha = Mockito.mock (Portfolio.class);
		Mockito.when (mPlayerActorAlpha.getPortfolio ()).thenReturn (mPortfolioAlpha);
		
		companyBeta = companyTestFactory.buildAShareCompany (1);
		companyGamma = companyTestFactory.buildAShareCompany (2);
		tGammaPortfolio = playerActorGamma.getPortfolio ();
		certificate = companyBeta.getCertificate (20, true);
		tGammaPortfolio.addCertificate (certificate);

		effectAlpha = new ResponseOfferEffect ();
		tResponse = true;
		tItemType = "Private";
		tItemName = certificate.getCompanyName ();
		effectBeta = new ResponseOfferEffect (mPlayerActorAlpha, mPlayerActorDelta, tResponse, tItemType, tItemName);
		tResponse = false;
		effectChi = new ResponseOfferEffect (mPlayerActorAlpha, mPlayerActorDelta, tResponse, tItemType, tItemName);
		effectEpsilon = new ResponseOfferEffect (companyBeta, mPlayerActorDelta, tResponse, tItemType, tItemName);
	}

	@Test
	@DisplayName ("Simple Response to Offer Tests")
	void simpleResponseToOfferTests () {
		Player tFoundPlayer;

		assertFalse (effectAlpha.actorIsSet (), "Actor is Set");
		assertTrue (effectBeta.actorIsSet (), "Actor is not Set");
		assertEquals ("Response To Offer", effectBeta.getName ());
		assertEquals ("ToEffectTesterAlpha", effectBeta.getActorName ());
		assertEquals ("ToEffectTesterDelta", effectBeta.getToActorName ());

		assertTrue (effectBeta.getResponse ());
		assertEquals ("Private", effectBeta.getItemType ());
		assertEquals ("Test Pennsylvania", effectBeta.getItemName ());

		tFoundPlayer = (Player) effectBeta.getToActor ();
		assertEquals ("ToEffectTesterDelta", tFoundPlayer.getName ());
	}

	@Test
	@DisplayName ("Reports from Response to Offer Tests")
	void reportsFromResponseToOfferTests () {
		String tReportResponseTrue = "--Effect:  The offer from ToEffectTesterDelta to buy Test Pennsylvania Private sent to ToEffectTesterAlpha was Accepted";
		String tReportResponseFalse = "--Effect:  The offer from ToEffectTesterDelta to buy Test Pennsylvania Private sent to ToEffectTesterAlpha was Rejected";
		String tReportResponseNoTO = "--Effect:  The offer from NULL to buy Test Pennsylvania Private sent to ToEffectTesterAlpha was Rejected";
		String tReportResponseEpsilon = "--Effect:  The offer from ToEffectTesterDelta to buy Test Pennsylvania Private sent to President of Test Pennsylvania (ToEffectTesterGamma) was Rejected";

		assertEquals (tReportResponseTrue, effectBeta.getEffectReport (null));
		assertEquals (tReportResponseFalse, effectChi.getEffectReport (null));

		effectChi.setToActor (ActorI.NO_ACTOR);
		assertEquals (tReportResponseNoTO, effectChi.getEffectReport (null));

		assertEquals (tReportResponseEpsilon, effectEpsilon.getEffectReport (null));
	}

}
