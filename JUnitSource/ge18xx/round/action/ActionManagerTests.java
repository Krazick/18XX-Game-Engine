package ge18xx.round.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.StartPacketFrame;
import ge18xx.bank.StartPacketItem;
import ge18xx.bank.StartPacketTestFactory;
import ge18xx.company.Certificate;
import ge18xx.round.RoundManager;
import ge18xx.toplevel.FrameTestFactory;
import ge18xx.toplevel.PrivatesFrame;

@TestInstance (Lifecycle.PER_CLASS)
@DisplayName ("Action Manager Tests")
class ActionManagerTests extends ActionTester {

	RoundManager roundManager;
	ActionManager actionManager;
	FrameTestFactory frameTestFactory;
	PrivatesFrame mPrivatesFrame;
	Certificate mCertificate;
	
	@Override
	@BeforeAll
	void factorySetUp () {
		super.factorySetUp ();
	}

	@BeforeEach
	void setup () {
		Certificate mPrivateCertificate;
		Bank tBank;
		StartPacketItem mStartPacketItem;
		StartPacketFrame mStartPacketFrame;
		StartPacketTestFactory startPacketTestFactory;
		
		roundManager = roundTestFactory.buildRoundManager (gameManager);
		actionManager = actionEffectsFactory.buildActionManager (roundManager);
		tBank = bankTestFactory.buildBank ();
		frameTestFactory = new FrameTestFactory (gameManager, roundManager);
		startPacketTestFactory = new StartPacketTestFactory (gameManager, tBank);
		mStartPacketFrame = startPacketTestFactory.buildStartPacketFrameMock ("Start Packet Frame Mock");
		mStartPacketItem = startPacketTestFactory.buildStartPacketItemMock ();
		Mockito.when (mStartPacketFrame.getStartPacketItem (anyInt ())).thenReturn (mStartPacketItem);

		mPrivateCertificate = certificateTestFactory.buildCertificateMock ();
		mPrivatesFrame = frameTestFactory.buildPrivatesFrameMock ("Privates Test Frame Mock");		
		Mockito.when (mPrivatesFrame.getCertificate (anyString (), anyInt (), 
							anyBoolean ())).thenReturn (mPrivateCertificate);
		tBank.setStartPacketFrame (mStartPacketFrame);
		gameManager.setPrivatesFrame (mPrivatesFrame);
	}
	
	@Test
	@DisplayName ("Initial Build Tests")
	void actionManagerTestInitialBuild () {
		assertEquals (gameManager, actionManager.getGameManager ());
		assertEquals (0, actionManager.getActionCount ());
		assertEquals (0, actionManager.getActionNumber ());
		assertFalse (gameManager.isNetworkGame ());
		assertFalse (gameManager.getNotifyNetwork ());
		
		addGameStartingActions ();
	}

	protected void addGameStartingActions () {
		Action tAction;
		tAction = actionEffectsFactory.getTestActionAt (2);
		assertEquals ("Start Stock Action", tAction.getName ());
		assertEquals (1, tAction.getNumber ());
		actionManager.sendActionToNetwork (tAction);
		assertEquals (0, actionManager.getActionCount ());
		actionManager.addTheAction (tAction);
		assertEquals (1, actionManager.getActionCount ());
	
		tAction = actionEffectsFactory.getTestActionAt (3);
		assertEquals ("Buy Stock Action", tAction.getName ());
		assertEquals (2, tAction.getNumber ());
		actionManager.sendActionToNetwork (tAction);
		actionManager.addTheAction (tAction);
		assertEquals (2, actionManager.getActionCount ());

		tAction = actionEffectsFactory.getTestActionAt (4);
		assertEquals ("Done Player Action", tAction.getName ());
		assertEquals (3, tAction.getNumber ());
		assertFalse (actionManager.sendActionToNetwork (tAction));
		actionManager.addTheAction (tAction);
		assertEquals (3, actionManager.getActionCount ());
	}
	
	@Test
	@DisplayName ("Action Number Testing")
	void actionNumberTesting () {
		int tActionNumber;
		
		tActionNumber = actionManager.generateNewActionNumber (false);
		assertEquals (1, tActionNumber);

		tActionNumber = actionManager.generateNewActionNumber (false);
		assertEquals (2, tActionNumber);
	}
	
	@Test
	@DisplayName ("Action Index Testing")
	void actionIndexTesting () {
		int tActionIndex;
		int tActionIndexOffset;
		Action tAction;
		
		assertEquals (0, actionManager.getActionCount ());
		tActionIndexOffset = 0;
		tActionIndex = actionManager.getLastActionIndex (tActionIndexOffset);
		assertEquals (0, tActionIndex);

		tActionIndex = actionManager.getLastActionIndex ();
		assertEquals (-1, tActionIndex);
		
		tAction = actionManager.getActionAt (0);
		assertEquals (Action.NO_ACTION, tAction);
		
		assertFalse (actionManager.hasActionsToUndo ());
		addGameStartingActions ();
		
		tActionIndexOffset = 1;
		tActionIndex = actionManager.getLastActionIndex (tActionIndexOffset);
		assertEquals (2, tActionIndex);
		
		assertTrue (actionManager.hasActionsToUndo ());
		
		tActionIndex = actionManager.getLastActionIndex ();
		assertEquals (2, tActionIndex);

		tAction = actionManager.getActionAt (0);
		assertEquals ("Start Stock Action", tAction.getName ());
		assertEquals (1, tAction.getNumber ());

		tAction = actionManager.getActionAt (3);
		assertEquals (Action.NO_ACTION, tAction);

		tAction = actionManager.getActionAt (-1);
		assertEquals (Action.NO_ACTION, tAction);

	}

}
