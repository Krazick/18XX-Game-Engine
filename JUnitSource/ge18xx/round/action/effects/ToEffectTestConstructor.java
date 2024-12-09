package ge18xx.round.action.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankTestFactory;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;

@DisplayName ("ToEffect Constructor Tests")
public class ToEffectTestConstructor {
	ToEffect effectAlpha;
	ToEffect effectBeta;
	Player actorBeta;
	Player actorGamma;
	Player actorDelta;
	GameManager mGameManager;
	PlayerManager playerManager;
	Bank bank;
	GameInfo mGameInfo;
	private String GENERIC_TO_EFFECT = "GenericTo";
	GameTestFactory gameTestFactory;
	BankTestFactory bankTestFactory;

	@BeforeEach
	void setUp () throws Exception {
		String tClientName, tPlayer2Name, tPlayer3Name;

		tClientName = "TFBuster";
		tPlayer2Name = "ToEffectTesterBeta";
		tPlayer3Name = "ToEffectTesterGamma";
		gameTestFactory = new GameTestFactory ();
		bankTestFactory = new BankTestFactory ();

		mGameManager = gameTestFactory.buildGameManagerMock (tClientName);
		Mockito.when (mGameManager.gameHasPrivates ()).thenReturn (true);
		Mockito.when (mGameManager.gameHasMinors ()).thenReturn (false);
		Mockito.when (mGameManager.gameHasShares ()).thenReturn (true);
		
		mGameInfo = gameTestFactory.buildGameInfoMock ();
		Mockito.when (mGameInfo.hasAuctionRound ()).thenReturn (true);
		Mockito.when (mGameManager.getActiveGame ()).thenReturn (mGameInfo);

		bank = bankTestFactory.buildBank ();
		playerManager = new PlayerManager (mGameManager);
		effectAlpha = new ToEffect ();
		actorDelta = new Player (tClientName, playerManager, PlayerManager.CERTIFICATE_LIMIT_ZERO);
		actorBeta = new Player (tPlayer2Name, playerManager, PlayerManager.CERTIFICATE_LIMIT_ZERO);
		actorGamma = new Player (tPlayer3Name, playerManager, PlayerManager.CERTIFICATE_LIMIT_ZERO);
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
