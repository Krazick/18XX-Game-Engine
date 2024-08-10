package ge18xx.round.action.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.company.CompanyTestFactory;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.PlayerManager;
import ge18xx.train.Train;

@DisplayName ("Transfer Train Effect Constructor Tests")
class TransferTrainEffectTests {
	TransferTrainEffect effectBeta;
	ShareCompany actorBeta;
	ShareCompany actorGamma;
	ShareCompany actorZeta;
	GameManager gameManager;
	PlayerManager playerManager;
	GameTestFactory testFactory;
	CompanyTestFactory companyTestFactory;
	Train train;

	@BeforeEach
	void setUp () throws Exception {
		String tClientName;

		tClientName = "TFBuster";
		testFactory = new GameTestFactory ();
		companyTestFactory = new CompanyTestFactory (testFactory);
		gameManager = testFactory.buildGameManager (tClientName);
		playerManager = new PlayerManager (gameManager);
		actorBeta = companyTestFactory.buildAShareCompany (1);
		actorGamma = companyTestFactory.buildAShareCompany (2);
		actorZeta = companyTestFactory.buildAShareCompany (companyTestFactory.NO_COMPANY_INDEX);
		train = new Train ("Test2", 0, 1, 2, 80);
		effectBeta = new TransferTrainEffect (actorBeta, train, actorGamma);
	}

	@Test
	@DisplayName ("Simple Constructor Tests")
	void simpleConstructorTests () {
		String tReportResult = "--Effect: Transfer Train named Test2 from Test Pennsylvania to Test Baltimore and Ohio.";
		ShareCompany tShareActor;
		ShareCompany tToShareActor;

		assertTrue (effectBeta.actorIsSet (), "Actor is not Set");
		assertEquals ("Transfer Train", effectBeta.getName ());
		assertEquals ("Test Pennsylvania", effectBeta.getActorName ());
		assertEquals ("Test Baltimore and Ohio", effectBeta.getToActorName ());

		tShareActor = (ShareCompany) effectBeta.getActor ();
		tToShareActor = (ShareCompany) effectBeta.getToActor ();
		assertEquals ("Test Pennsylvania", tShareActor.getName ());
		assertEquals (tReportResult, effectBeta.getEffectReport (null));
		assertNotNull (effectBeta.getToActorName ());
		assertEquals ("Test Baltimore and Ohio", effectBeta.getToActorName ());
		assertEquals ("Test Baltimore and Ohio", tToShareActor.getName ());

		assertNull (actorZeta);
	}
}
