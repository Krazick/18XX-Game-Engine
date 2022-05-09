package ge18xx.round.action.effects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;

@DisplayName ("ToEffect Constructor Tests")
class ToEffectTestConstructor {
	ToEffect effectAlpha;
	ToEffect effectBeta;
	Player actorBeta;
	Player actorGamma;
	Player actorDelta;
	GameManager mGameManager;
	PlayerManager playerManager;
	private String GENERIC_TO_EFFECT = "GenericTo";
	GameTestFactory testFactory;

	@BeforeEach
	void setUp () throws Exception {
		String tClientName, tPlayer2Name, tPlayer3Name;

		tClientName = "TFBuster";
		tPlayer2Name = "ToEffectTesterBeta";
		tPlayer3Name = "ToEffectTesterGamma";
		testFactory = new GameTestFactory ();
		mGameManager = testFactory.buildGameManagerMock (tClientName);
		Mockito.when (mGameManager.gameHasPrivates ()).thenReturn (true);
		Mockito.when (mGameManager.gameHasCoals ()).thenReturn (false);
		Mockito.when (mGameManager.gameHasMinors ()).thenReturn (false);
		Mockito.when (mGameManager.gameHasShares ()).thenReturn (true);
		playerManager = new PlayerManager (mGameManager);
		effectAlpha = new ToEffect ();
		actorDelta = new Player (tClientName, playerManager, 0);
		actorBeta = new Player (tPlayer2Name, playerManager, 0);
		actorGamma = new Player (tPlayer3Name, playerManager, 0);
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
		assertEquals ("--Effect: " + GENERIC_TO_EFFECT + " for ToEffectTesterBeta to ToEffectTesterGamma.",
				effectBeta.getEffectReport (null));
		assertNotNull (effectBeta.getToActorName ());
		assertEquals ("ToEffectTesterGamma", effectBeta.getToActorName ());
		assertEquals ("ToEffectTesterGamma", tFoundToPlayer.getName ());

		assertFalse (effectBeta.isToActor ("ToEffectTesterBeta"));
		assertTrue (effectBeta.isToActor ("ToEffectTesterGamma"));

		assertTrue (effectBeta.undoEffect (RoundManager.NO_ROUND_MANAGER));
		assertFalse (effectBeta.wasNewStateAuction ());
		assertFalse (effectBeta.applyEffect (RoundManager.NO_ROUND_MANAGER));
	}

	@Test
	@DisplayName ("ToEffect Tests for toActor")
	void toActorTests () {
		assertEquals (effectBeta.getToActor (), actorGamma);

	}

}
