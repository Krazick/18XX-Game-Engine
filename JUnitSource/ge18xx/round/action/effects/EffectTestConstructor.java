package ge18xx.round.action.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.PlayerTestFactory;
import ge18xx.round.RoundManager;
import ge18xx.round.RoundTestFactory;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;

@DisplayName ("Effect Constructor Tests")
class EffectTestConstructor {
	ToEffect effectAlpha;
	ToEffect effectBeta;
	Player actorBeta;
	Player actorDelta;
	GameManager mGameManager;
	PlayerManager playerManager;
	private String GENERIC_EFFECT = "Generic";
	GameTestFactory gameTestFactory;
	PlayerTestFactory playerTestFactory;

	@BeforeEach
	void setUp () throws Exception {
		String tClientName, tPlayer2Name;

		tClientName = "TFBuster";
		tPlayer2Name = "EffectTesterBeta";
		gameTestFactory = new GameTestFactory ();
		mGameManager = gameTestFactory.buildGameManagerMock (tClientName);
		Mockito.when (mGameManager.gameHasPrivates ()).thenReturn (true);
		Mockito.when (mGameManager.gameHasMinors ()).thenReturn (false);
		Mockito.when (mGameManager.gameHasShares ()).thenReturn (true);
		playerTestFactory = new PlayerTestFactory (mGameManager);
		playerManager = playerTestFactory.buildPlayerManager ();
		effectAlpha = new ToEffect ();
		actorBeta = playerTestFactory.buildPlayerMock (tPlayer2Name);
		actorDelta = playerTestFactory.buildPlayerMock (tClientName);
		effectBeta = new ToEffect (GENERIC_EFFECT, actorBeta, actorDelta);
	}

	@Test
	@DisplayName ("Simple Effect Tests")
	void simpleConstructorTests () {
		Player tFoundPlayer;

		assertFalse (effectAlpha.actorIsSet (), "Actor is Set");
		assertTrue (effectBeta.actorIsSet (), "Actor is not Set");
		assertEquals (GENERIC_EFFECT, effectBeta.getName ());
		assertEquals ("EffectTesterBeta", effectBeta.getActorName ());

		tFoundPlayer = (Player) effectBeta.getActor ();
		assertEquals ("EffectTesterBeta", tFoundPlayer.getName ());
		assertEquals ("--Effect: Generic for EffectTesterBeta to TFBuster.", effectBeta.getEffectReport (null));
		assertEquals (effectBeta.getToActorName (), "TFBuster");

		assertTrue (effectBeta.undoEffect (RoundManager.NO_ROUND_MANAGER));
		assertFalse (effectBeta.wasNewStateAuction ());
		assertFalse (effectBeta.applyEffect (RoundManager.NO_ROUND_MANAGER));
	}

	@Test
	@DisplayName ("Test XML Generation")
	void testXMLGeneration () {
		XMLDocument tXMLDocument;
		AttributeName tAN_TEST = new AttributeName ("anTest");
		ElementName tEN_TEST = new ElementName ("EnTest");
		XMLElement tXMLElement, tFullElement;
		String tXMLFormatted;
		String tExpected = "";

		tXMLDocument = new XMLDocument ();
		tFullElement = tXMLDocument.createElement (tEN_TEST);
		tXMLElement = effectBeta.getEffectElement (tXMLDocument, tAN_TEST);
		tFullElement.appendChild (tXMLElement);
		tXMLDocument.appendChild (tFullElement);
		tXMLFormatted = tXMLDocument.toString ();
		tExpected += "<EnTest>\n";
		tExpected += "<Effect class=\"ge18xx.round.action.effects.ToEffect\" fromActor=\"EffectTesterBeta\" fromName=\"\" isAPrivate=\"false\" name=\"Generic\" toActor=\"TFBuster\"/>\n";
		tExpected += "</EnTest>\n";

		assertEquals (tExpected, tXMLFormatted);
	}

	@Test
	@DisplayName ("Test Benefit Aspects")
	void testBenefitAspects () {
		assertEquals ("", effectBeta.getBenefitEffectReport ());
		effectBeta.setBenefitName ("Test Benefit");
		effectBeta.setBenefitPrivateAbbrev ("TPA");
		effectBeta.setBenefitUsed (false);

		assertEquals ("Test Benefit", effectBeta.getBenefitName ());
		assertEquals ("TPA", effectBeta.getBenefitPrivateAbbrev ());
		assertEquals (" Test Benefit Benefit from TPA.", effectBeta.getBenefitEffectReport ());
		effectBeta.setBenefitUsed (true);
		assertEquals (" Used Test Benefit Benefit from TPA.", effectBeta.getBenefitEffectReport ());
	}

	@Test
	@DisplayName ("Using RoundManager Test")
	void useOfRoundManagerTests () {
		RoundManager tRoundManager;
		RoundTestFactory tRoundTestFactory;
		String tEffectReport;

		tRoundTestFactory = new RoundTestFactory ();
		tRoundManager = tRoundTestFactory.buildRoundManager ();
		tEffectReport = effectBeta.getEffectReport (tRoundManager);
		assertEquals ("--Effect: Generic for EffectTesterBeta to TFBuster.", tEffectReport);
		assertTrue (effectBeta.undoEffect (tRoundManager));
		assertFalse (effectBeta.applyEffect (tRoundManager));
	}

}
