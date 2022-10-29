package ge18xx.round.action.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.company.Certificate;
import ge18xx.company.CompanyTestFactory;
import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;

@DisplayName ("Response to Offer Effect Constructor Tests")
class ResponseOfferEffectTestConstructor {
	ResponseOfferEffect effectAlpha;
	ResponseOfferEffect effectBeta;
	ResponseOfferEffect effectChi;
	ResponseOfferEffect effectEpsilon;
	ShareCompany companyBeta;
	ShareCompany companyGamma;
	Player playerActorAlpha;
	Player playerActorDelta;
	GameManager mGameManager;
	PlayerManager playerManager;
	GameTestFactory testFactory;
	CompanyTestFactory companyTestFactory;
	Certificate certificate;

	@BeforeEach
	void setUp () throws Exception {
		String tClientName, tPlayer2Name, tPlayer3Name;
		Portfolio tPortfolioAlpha;
		boolean tResponse;
		String tItemType;
		String tItemName;

		tClientName = "TFBuster";
		tPlayer2Name = "ToEffectTesterAlpha";
		tPlayer3Name = "ToEffectTesterDelta";
		testFactory = new GameTestFactory ();
		companyTestFactory = new CompanyTestFactory (testFactory);
		mGameManager = testFactory.buildGameManagerMock (tClientName);
		Mockito.when (mGameManager.gameHasPrivates ()).thenReturn (true);
		Mockito.when (mGameManager.gameHasMinors ()).thenReturn (false);
		Mockito.when (mGameManager.gameHasShares ()).thenReturn (true);
		playerManager = new PlayerManager (mGameManager);
		effectAlpha = new ResponseOfferEffect ();
		playerActorAlpha = new Player (tPlayer2Name, playerManager, 0);
		playerActorDelta = new Player (tPlayer3Name, playerManager, 0);
		companyBeta = companyTestFactory.buildAShareCompany (1);
		companyGamma = companyTestFactory.buildAShareCompany (2);
		tPortfolioAlpha = playerActorAlpha.getPortfolio ();

		certificate = new Certificate (companyBeta, true, 20, tPortfolioAlpha);
		tPortfolioAlpha.addCertificate (certificate);
		effectAlpha = new ResponseOfferEffect ();
//		public Certificate (Corporation aCorporation, boolean aIsPresidentShare, int aPercentage, CertificateHolderI aOwner) {
		tResponse = true;
		tItemType = "Private";
		tItemName = certificate.getCompanyName ();
		effectBeta = new ResponseOfferEffect (playerActorAlpha, playerActorDelta, tResponse, tItemType, tItemName);
		tResponse = false;
		effectChi = new ResponseOfferEffect (playerActorAlpha, playerActorDelta, tResponse, tItemType, tItemName);
		effectEpsilon = new ResponseOfferEffect (companyBeta, playerActorDelta, tResponse, tItemType, tItemName);
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
		assertEquals ("TestPennsylvania", effectBeta.getItemName ());

		tFoundPlayer = (Player) effectBeta.getToActor ();
		assertEquals ("ToEffectTesterDelta", tFoundPlayer.getName ());
	}

	@Test
	@DisplayName ("Reports from Response to Offer Tests")
	void reportsFromResponseToOfferTests () {
		String tReportResponseTrue = "--Effect:  The offer from ToEffectTesterDelta to buy TestPennsylvania Private sent to ToEffectTesterAlpha was Accepted";
		String tReportResponseFalse = "--Effect:  The offer from ToEffectTesterDelta to buy TestPennsylvania Private sent to ToEffectTesterAlpha was Rejected";
		String tReportResponseNoTO = "--Effect:  The offer from NULL to buy TestPennsylvania Private sent to ToEffectTesterAlpha was Rejected";
		String tReportResponseEpsilon = "--Effect:  The offer from ToEffectTesterDelta to buy TestPennsylvania Private sent to  President of TestPennsylvania (TPRR) was Rejected";

		assertEquals (tReportResponseTrue, effectBeta.getEffectReport (null));
		assertEquals (tReportResponseFalse, effectChi.getEffectReport (null));

		effectChi.setToActor (Corporation.NO_ACTOR);
		assertEquals (tReportResponseNoTO, effectChi.getEffectReport (null));

		assertEquals (tReportResponseEpsilon, effectEpsilon.getEffectReport (null));
	}

}
