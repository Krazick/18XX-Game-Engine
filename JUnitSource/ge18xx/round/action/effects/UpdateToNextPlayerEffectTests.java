package ge18xx.round.action.effects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.company.formation.TriggerClass;
import ge18xx.player.Player;

class UpdateToNextPlayerEffectTests extends EffectTester {
	UpdateToNextPlayerEffect updateToNextPlayerEffect;
	List<Player> players;
	TriggerClass mTriggerClass;
	
	@Override
	@BeforeEach
	void setUp () throws Exception {
		super.setUp ();
		players = new LinkedList<Player> ();
		players.add (mPlayerActorAlpha);
		players.add (mPlayerActorBeta);
		
		Mockito.when (mPlayerManager.getPlayers ()).thenReturn (players);

		mTriggerClass = Mockito.mock (TriggerClass.class);
		doNothing().when (mTriggerClass).setCurrentPlayerIndex (anyInt ());
		Mockito.when (mTriggerClass.updateToNextPlayer (anyList(), anyBoolean ())).thenReturn (2);

		Mockito.when (mGameManager.getTriggerClass ()).thenReturn (mTriggerClass);
		
		Mockito.when (mRoundManager.getPlayerManager ()).thenReturn (mPlayerManager);
		Mockito.when (mPlayerManager.getPlayerIndex (mPlayerActorAlpha)).thenReturn (0);
		Mockito.when (mPlayerManager.getPlayerIndex (mPlayerActorBeta)).thenReturn (1);
		
		updateToNextPlayerEffect = new UpdateToNextPlayerEffect (mPlayerActorAlpha, mPlayerActorBeta);
	}

	@Test
	void testGetEffectReport () {
		assertEquals ("FromEffectTesterAlpha", mPlayerActorAlpha.getName ());
		assertEquals ("ToEffectTesterBeta", mPlayerActorBeta.getName ());
		assertEquals ("--Effect: Update to Next Player from FromEffectTesterAlpha to ToEffectTesterBeta.", 
						updateToNextPlayerEffect.getEffectReport (mRoundManager));
	}

	@Test
	void testApplyEffect () {
		Mockito.when (mPlayerActorAlpha.isAShareCompany ()).thenReturn (false);
		assertTrue (updateToNextPlayerEffect.applyEffect (mRoundManager));

		Mockito.when (mStockRound.isAShareCompany ()).thenReturn (false);
		Mockito.when (mStockRound.isAPlayer ()).thenReturn (false);
		updateToNextPlayerEffect.setActor (mStockRound);
		
		assertFalse (updateToNextPlayerEffect.applyEffect (mRoundManager));
		assertEquals ("Actor Stock Round is not a Player.",
					updateToNextPlayerEffect.getApplyFailureReason ());
	}

	@Test
	void testUndoEffect () {
		Mockito.when (mPlayerActorAlpha.isAShareCompany ()).thenReturn (false);
		assertTrue (updateToNextPlayerEffect.undoEffect (mRoundManager));
		
		Mockito.when (mStockRound.isAShareCompany ()).thenReturn (false);
		Mockito.when (mStockRound.isAPlayer ()).thenReturn (false);
		updateToNextPlayerEffect.setActor (mStockRound);
		assertFalse (updateToNextPlayerEffect.undoEffect (mRoundManager));
		assertEquals ("Actor Stock Round is not a Player.",
					updateToNextPlayerEffect.getUndoFailureReason ());
	}

	@Test
	void testUpdateToNextPlayerEffectXMLNodeGameManager () {
//		fail ("Not yet implemented");
	}

	@Test
	void testUpdateToNextPlayer () {
		assertEquals (1, updateToNextPlayerEffect.updateToNextPlayer (mRoundManager, true));

		Mockito.when (mGameManager.getTriggerClass ()).thenReturn (null);
		assertEquals (-1, updateToNextPlayerEffect.updateToNextPlayer (mRoundManager, true));
	}
	
	@Test
	void testUpdateToNextPlayerBad () {
		Mockito.when (mPlayerManager.getPlayerIndex (mPlayerActorAlpha)).thenReturn (0);
		Mockito.when (mPlayerManager.getPlayerIndex (mPlayerActorBeta)).thenReturn (-1);
		assertEquals (2, updateToNextPlayerEffect.updateToNextPlayer (mRoundManager, true));
	}
}
