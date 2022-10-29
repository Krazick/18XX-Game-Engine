package ge18xx.round.action.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;

@DisplayName ("CashTransferEffect Constructor Tests")
class CashTransferEffectTestConstructor {
	CashTransferEffect effectAlpha;
	CashTransferEffect effectBeta;
	Player actorBeta;
	Player actorGamma;
	GameManager mGameManager;
	PlayerManager playerManager;
	GameTestFactory testFactory;
	int cashAmount;

	@BeforeEach
	void setUp () throws Exception {
		String tClientName, tPlayer2Name, tPlayer3Name;

		tClientName = "TFBuster";
		tPlayer2Name = "ToEffectTesterBeta";
		tPlayer3Name = "ToEffectTesterGamma";
		testFactory = new GameTestFactory ();
		mGameManager = testFactory.buildGameManagerMock (tClientName);
		Mockito.when (mGameManager.gameHasPrivates ()).thenReturn (true);
		Mockito.when (mGameManager.gameHasMinors ()).thenReturn (false);
		Mockito.when (mGameManager.gameHasShares ()).thenReturn (true);
		playerManager = new PlayerManager (mGameManager);
		effectAlpha = new CashTransferEffect ();
		cashAmount = 100;
		actorBeta = new Player (tPlayer2Name, playerManager, 0);
		actorGamma = new Player (tPlayer3Name, playerManager, 0);
		effectBeta = new CashTransferEffect (actorBeta, actorGamma, cashAmount);
	}

	@Test
	@DisplayName ("Simple CashTransferEffect Tests")
	void simpleConstructorTests () {
		Player tFoundPlayer;
		Player tFoundToPlayer;
		String tReportResult = "--Effect: Cash Transfer of 100 from ToEffectTesterBeta to ToEffectTesterGamma.";

		assertFalse (effectAlpha.actorIsSet (), "Actor is Set");
		assertTrue (effectBeta.actorIsSet (), "Actor is not Set");
		assertEquals ("Cash Transfer", effectBeta.getName ());
		assertEquals ("ToEffectTesterBeta", effectBeta.getActorName ());
		assertEquals ("ToEffectTesterGamma", effectBeta.getToActorName ());

		tFoundPlayer = (Player) effectBeta.getActor ();
		tFoundToPlayer = (Player) effectBeta.getToActor ();
		assertEquals ("ToEffectTesterBeta", tFoundPlayer.getName ());
		assertEquals (tReportResult, effectBeta.getEffectReport (null));
		assertNotNull (effectBeta.getToActorName ());
		assertEquals ("ToEffectTesterGamma", effectBeta.getToActorName ());
		assertEquals ("ToEffectTesterGamma", tFoundToPlayer.getName ());
		assertEquals (100, effectBeta.getCash ());

		assertTrue (effectBeta.undoEffect (null));
		assertFalse (effectBeta.wasNewStateAuction ());
		assertTrue (effectBeta.applyEffect (null));
	}

	@Test
	@DisplayName ("CashTransferEffect Apply and Undo Tests")
	void simpleApplyUndoTests () {
		effectBeta.undoEffect (null);
		assertEquals (100, actorBeta.getCash ());
		assertEquals (-100, actorGamma.getCash ());

		effectBeta.applyEffect (null);
		assertEquals (0, actorBeta.getCash ());
		assertEquals (0, actorGamma.getCash ());

		effectBeta.applyEffect (null);
		assertEquals (-100, actorBeta.getCash ());
		assertEquals (100, actorGamma.getCash ());
	}

	@Test
	@DisplayName ("CashTransferEffect Test Credit and Debit")
	void simpleCreditDebitTests () {
		assertEquals (100, effectBeta.getEffectDebit (actorBeta.getName ()));
		assertEquals (100, effectBeta.getEffectCredit (actorGamma.getName ()));
		assertEquals (0, effectBeta.getEffectDebit (actorGamma.getName ()));
		assertEquals (0, effectBeta.getEffectCredit (actorBeta.getName ()));
	}
}
