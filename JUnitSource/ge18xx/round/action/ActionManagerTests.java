package ge18xx.round.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import ge18xx.round.RoundManager;

@TestInstance (Lifecycle.PER_CLASS)
@DisplayName ("Action Manager Tests")
class ActionManagerTests extends ActionTester {

	RoundManager roundManager;
	ActionManager actionManager;
	
	@Override
	@BeforeAll
	void factorySetUp () {
		super.factorySetUp ();
	}

	@BeforeEach
	void setup () {
		roundManager = roundTestFactory.buildRoundManager (gameManager);
		actionManager = actionEffectsFactory.buildActionManager (roundManager);
	}
	
	@Test
	@DisplayName ("Initial Build Tests")
	void actionManagerTestInitialBuild () {
		Action tAction;
		
		assertEquals (gameManager, actionManager.getGameManager ());
		assertEquals (0, actionManager.getActionCount ());
		assertEquals (0, actionManager.getActionNumber ());
		assertFalse (gameManager.isNetworkGame ());
		assertFalse (gameManager.getNotifyNetwork ());
		
		tAction = actionEffectsFactory.getTestActionAt (2);
		assertEquals ("Start Stock Action", tAction.getName ());
		assertEquals (1, tAction.getNumber ());
		actionManager.sendActionToNetwork (tAction);
	}
}
