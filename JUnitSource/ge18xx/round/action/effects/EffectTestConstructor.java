package ge18xx.round.action.effects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.game.TestFactory;

@DisplayName ("Effect Constructor Tests")
class EffectTestConstructor {
	ToEffect effectAlpha;
	ToEffect effectBeta;
	Player actorBeta;
	Player actorDelta;
	GameManager gameManager;
	PlayerManager playerManager;
	private String GENERIC_EFFECT = "Generic";
	TestFactory testFactory;
	
	@BeforeEach
	void setUp () throws Exception {
		String tClientName, tPlayer2Name;
		
		tClientName = "TFBuster";
		tPlayer2Name = "EffectTesterBeta";
		testFactory = new TestFactory ();
		gameManager =  testFactory.buildGameManager (tClientName);
		playerManager = new PlayerManager (gameManager);
		effectAlpha = new ToEffect ();
		actorBeta = new Player (tPlayer2Name, false, false, false, false, playerManager, 0);
		actorDelta = new Player (tClientName, false, false, false, false, playerManager, 0);
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
	@Disabled ("Don't understand why XML To String doesn't generate output here")
	@DisplayName ("Test XML Generation")
	void testXMLGeneration () {
		XMLDocument tXMLDocument;
		AttributeName tAN_TEST = new AttributeName ("anTest");
		ElementName tEN_TEST = new ElementName ("EnTest");
		XMLElement tXMLElement, tFullElement;
		String tXMLFormatted;
		
		tXMLDocument = new XMLDocument ();
		tFullElement = tXMLDocument.createElement (tEN_TEST); 
		tXMLElement = effectBeta.getEffectElement (tXMLDocument, tAN_TEST);
		tFullElement.appendChild (tXMLElement); 
		tXMLFormatted = tXMLDocument.toString ();
		System.out.println ("XML OUT Size [" + tXMLFormatted.length () + "]");
	}
}
