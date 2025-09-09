package ge18xx.round.action.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import ge18xx.company.formation.TriggerClass;

@TestInstance (Lifecycle.PER_CLASS)
class ToFormationPanelEffectTests extends EffectTester {
	ToFormationPanelEffect toFormationPanelEffect;
	TriggerClass mTriggerClass;

	@Override
	@BeforeAll
	void factorySetup () {
		super.factorySetup ();
	}
	
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
	void testApplyEffect () {
		Mockito.when (mPlayerActorAlpha.isAShareCompany ()).thenReturn (false);
		assertFalse (toFormationPanelEffect.applyEffect (mRoundManager));
		assertEquals ("Actor FromEffectTesterAlpha is not a Share Company.",
						toFormationPanelEffect.getApplyFailureReason ());

		Mockito.when (mShareCompanyGreen.getPresident ()).thenReturn (mPlayerActorAlpha);
		toFormationPanelEffect.setActor (mShareCompanyGreen);
		
		assertTrue (toFormationPanelEffect.applyEffect (mRoundManager));
	}

	@Test
	void testUndoEffect () {
		assertTrue (toFormationPanelEffect.undoEffect (mRoundManager));

		Mockito.when (mShareCompanyGreen.getPresident ()).thenReturn (mPlayerActorAlpha);
		toFormationPanelEffect.setActor (mShareCompanyGreen);
		
		assertTrue (toFormationPanelEffect.undoEffect (mRoundManager));
		
		Mockito.when (mStockRound.isAShareCompany ()).thenReturn (false);
		Mockito.when (mStockRound.isAPlayer ()).thenReturn (false);
		toFormationPanelEffect.setActor (mStockRound);
		assertFalse (toFormationPanelEffect.undoEffect (mRoundManager));
		assertEquals ("Actor Stock Round is not a Share Company, nor a Player.",
				toFormationPanelEffect.getUndoFailureReason ());
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
