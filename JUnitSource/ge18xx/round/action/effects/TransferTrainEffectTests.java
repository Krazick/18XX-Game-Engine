package ge18xx.round.action.effects;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.game.TestFactory;
import ge18xx.player.PlayerManager;
import ge18xx.round.action.ActorI;
import ge18xx.train.Train;

@DisplayName ("Transfer Train Effect Constructor Tests")
class TransferTrainEffectTests {
	TransferTrainEffect effectAlpha;
	TransferTrainEffect effectBeta;
	ShareCompany actorBeta;
	ShareCompany actorGamma;
	ShareCompany actorZeta;
	GameManager gameManager;
	PlayerManager playerManager;
	TestFactory testFactory;
	Train train;

	@BeforeEach
	void setUp() throws Exception {
		String tClientName;
		
		tClientName = "TFBuster";
		testFactory = new TestFactory ();
		gameManager =  testFactory.buildGameManager (tClientName);
		playerManager = new PlayerManager (gameManager);
		effectAlpha = new TransferTrainEffect ();
		actorBeta = testFactory.buildAShareCompany (1);
		actorGamma = testFactory.buildAShareCompany (2);
		actorZeta = testFactory.buildAShareCompany (33);
		train = new Train ("Test2", 0, 1, 2, 80);
		effectBeta = new TransferTrainEffect (actorBeta, train, actorGamma);
	}

	@Test
	@DisplayName ("Simple Constructor Tests")
	void test() {
		String tReportResult = "--Effect: Transfer Train named Test2 from TestPennsylvania to Test Baltimore and Ohio.";
		ShareCompany tShareActor;
		ShareCompany tToShareActor;

		assertFalse (effectAlpha.actorIsSet (), "Actor is Set");
		assertEquals (ActorI.NO_NAME, effectAlpha.getToActorName ());
		
		assertTrue (effectBeta.actorIsSet (), "Actor is not Set");
		assertEquals ("Transfer Train", effectBeta.getName ());
		assertEquals ("TestPennsylvania", effectBeta.getActorName ());
		assertEquals ("Test Baltimore and Ohio", effectBeta.getToActorName ());

		tShareActor = (ShareCompany) effectBeta.getActor ();
		tToShareActor = (ShareCompany) effectBeta.getToActor ();
		assertEquals ("TestPennsylvania", tShareActor.getName ());
		assertEquals (tReportResult, effectBeta.getEffectReport (null));
		assertNotNull (effectBeta.getToActorName ());
		assertEquals ("Test Baltimore and Ohio", effectBeta.getToActorName ());
		assertEquals ("Test Baltimore and Ohio", tToShareActor.getName ());
		
		assertNull (actorZeta);
	}

}
