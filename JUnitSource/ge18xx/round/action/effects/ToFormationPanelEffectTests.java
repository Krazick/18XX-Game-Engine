package ge18xx.round.action.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.company.formation.TriggerClass;

class ToFormationPanelEffectTests extends EffectTester {
	ToFormationPanelEffect toFormationPanelEffect;
	TriggerClass mTriggerClass;

	@Override
	@BeforeEach
	void setUp () throws Exception {
		super.setUp ();
		toFormationPanelEffect = new ToFormationPanelEffect (mPlayerActorAlpha, mPlayerActorBeta);
		
		Mockito.when (mRoundManager.getPlayerManager ()).thenReturn (mPlayerManager);
		Mockito.when (mPlayerManager.getPlayerIndex (mPlayerActorAlpha)).thenReturn (0);
		Mockito.when (mPlayerManager.getPlayerIndex (mPlayerActorBeta)).thenReturn (1);

		mTriggerClass = Mockito.mock (TriggerClass.class);
		Mockito.when (mGameManager.getTriggerClass ()).thenReturn (mTriggerClass);
		doNothing().when (mTriggerClass).rebuildFormationPanel (anyInt ());
	}

	@Test
	void testGetEffectReport () {
		assertEquals ("--Effect: To Formation Panel for FromEffectTesterAlpha is rebuilt.", 
				toFormationPanelEffect.getEffectReport (mRoundManager));
	}

	@Test
	void testUndoEffect () {
//		fail ("Not yet implemented");
	}

	@Test
	void testApplyEffect () {
//		fail ("Not yet implemented");
	}

	@Test
	void testHideFormationPanel () {
//		fail ("Not yet implemented");
	}

	@Test
	void testRebuildFormationPanel () {
		toFormationPanelEffect.rebuildFormationPanel (mRoundManager, 0);
		verify (mTriggerClass, times (1)).rebuildFormationPanel (0);
		
		Mockito.when (mGameManager.getTriggerClass ()).thenReturn (null);
		toFormationPanelEffect.rebuildFormationPanel (mRoundManager, 0);
	}

	@Test
	void testGetPlayerIndex () {

		assertEquals (0, toFormationPanelEffect.getPlayerIndex (mRoundManager, mPlayerActorAlpha));
		assertEquals (1, toFormationPanelEffect.getPlayerIndex (mRoundManager, mPlayerActorBeta));
	}

}
