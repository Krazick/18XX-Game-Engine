package ge18xx.round.action.effects;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.game.GameManager;
import ge18xx.game.TestFactory;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;

@DisplayName ("ToEffect Constructor Tests")
class ToEffectTestConstructor {
	ToEffect effectAlpha;
	ToEffect effectBeta;
	Player actorBeta;
	Player actorGamma;
	GameManager gameManager;
	PlayerManager playerManager;
	private String GENERIC_TO_EFFECT = "GenericTo";
	TestFactory testFactory;

	@BeforeEach
	void setUp() throws Exception {
		String tClientName, tPlayer2Name, tPlayer3Name;
		
		tClientName = "TFBuster";
		tPlayer2Name = "ToEffectTesterBeta";
		tPlayer3Name = "ToEffectTesterGamma";
		testFactory = new TestFactory ();
		gameManager =  testFactory.buildGameManager (tClientName);
		playerManager = new PlayerManager (gameManager);
		effectAlpha = new ToEffect ();
		actorBeta = new Player (tPlayer2Name, false, false, false, false, playerManager, 0);
		actorGamma = new Player (tPlayer3Name, false, false, false, false, playerManager, 0);
		effectBeta = new ToEffect (GENERIC_TO_EFFECT, actorBeta, actorGamma);
	}

	@Test
	@DisplayName ("Simple ToEffect Tests")
	void simpleConstructorTests () {
		Player tFoundPlayer;
		Player tFoundToPlayer;
		
		assertFalse (effectAlpha.actorIsSet (), "Actor is Set");
		assertTrue (effectBeta.actorIsSet (), "Actor is not Set");
		assertEquals (GENERIC_TO_EFFECT, effectBeta.getName ());
		assertEquals ("ToEffectTesterBeta", effectBeta.getActorName ());
		assertEquals ("ToEffectTesterGamma", effectBeta.getToActorName ());
		
		tFoundPlayer = (Player) effectBeta.getActor ();
		tFoundToPlayer = (Player) effectBeta.getToActor ();
		assertEquals ("ToEffectTesterBeta", tFoundPlayer.getName ());
		assertEquals ("--Effect: " + GENERIC_TO_EFFECT +" for ToEffectTesterBeta to ToEffectTesterGamma.", effectBeta.getEffectReport (null));
		assertNotNull (effectBeta.getToActorName ());
		assertEquals ("ToEffectTesterGamma", effectBeta.getToActorName ());
		assertEquals ("ToEffectTesterGamma", tFoundToPlayer.getName ());
	
		assertFalse (effectBeta.undoEffect (null));
		assertFalse (effectBeta.wasNewStateAuction ());
		assertFalse (effectBeta.applyEffect (null));
	}

}
