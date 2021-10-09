package ge18xx.round.action.effects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ge18xx.company.Certificate;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.game.TestFactory;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.round.action.ActorI;

@DisplayName ("Transfer Ownership Effect Constructor Tests")
class TransferOwnershipEffectTestConstructor {
	TransferOwnershipEffect effectAlpha;
	TransferOwnershipEffect effectBeta;
	ShareCompany actorBeta;
	ShareCompany actorGamma;
	Player playerActorAlpha;
	Player playerActorDelta;
	GameManager gameManager;
	PlayerManager playerManager;
	TestFactory testFactory;
	Certificate certificate;

	@BeforeEach
	void setUp() throws Exception {
		String tClientName, tPlayer2Name, tPlayer3Name;
		Portfolio tPortfolioAlpha;
		
		tClientName = "TFBuster";
		tPlayer2Name = "ToEffectTesterAlpha";
		tPlayer3Name = "ToEffectTesterDelta";
		testFactory = new TestFactory ();
		gameManager =  testFactory.buildGameManager (tClientName);
		playerManager = new PlayerManager (gameManager);
		effectAlpha = new TransferOwnershipEffect ();
		playerActorAlpha = new Player (tPlayer2Name, false, false, false, false, playerManager, 0);
		playerActorDelta = new Player (tPlayer3Name, false, false, false, false, playerManager, 0);
		actorBeta = testFactory.buildAShareCompany (1);
		actorGamma = testFactory.buildAShareCompany (2);
		tPortfolioAlpha = playerActorAlpha.getPortfolio ();
		
		certificate = new Certificate (actorBeta, true, 20, tPortfolioAlpha);
		tPortfolioAlpha.addCertificate (certificate);
//		public Certificate (Corporation aCorporation, boolean aIsPresidentShare, int aPercentage, CertificateHolderI aOwner) {
		effectBeta = new TransferOwnershipEffect (playerActorAlpha, certificate, playerActorDelta);
	}

	@Test
	@DisplayName ("Simple Constructor Tests")
	void test() {
		Player tFoundPlayer;
		Player tFoundToPlayer;
		Certificate tCertificate;
		String tReportResult = "--Effect: Transfer Ownership of 20% of TPRR from ToEffectTesterAlpha to ToEffectTesterDelta.";
		
		assertEquals ("TPRR", certificate.getCompanyAbbrev ());
		assertFalse (effectAlpha.actorIsSet (), "Actor is Set");
		assertEquals (ActorI.NO_NAME, effectAlpha.getToActorName ());
		
		assertTrue (effectBeta.actorIsSet (), "Actor is not Set");
		assertEquals ("Transfer Ownership", effectBeta.getName ());
		assertEquals ("ToEffectTesterAlpha", effectBeta.getActorName ());
		assertEquals ("ToEffectTesterDelta", effectBeta.getToActorName ());
		
		tFoundPlayer = (Player) effectBeta.getActor ();
		tFoundToPlayer = (Player) effectBeta.getToActor ();
		assertEquals ("ToEffectTesterAlpha", tFoundPlayer.getName ());
		assertEquals (tReportResult, effectBeta.getEffectReport (null));
		assertNotNull (effectBeta.getToActorName ());
		assertEquals ("ToEffectTesterDelta", effectBeta.getToActorName ());
		assertEquals ("ToEffectTesterDelta", tFoundToPlayer.getName ());
	
		tCertificate = effectBeta.getCertificate ();
		assertEquals ("TPRR", tCertificate.getCompanyAbbrev ());
		assertEquals ("TPRR", effectBeta.getCompanyAbbrev ());
		
		assertTrue (effectBeta.applyEffect (null));
		assertFalse (effectBeta.wasNewStateAuction ());
		assertTrue (effectBeta.undoEffect (null));

	}

}
