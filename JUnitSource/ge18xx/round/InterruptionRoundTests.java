package ge18xx.round;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.BankTestFactory;
import ge18xx.bank.StartPacketTestFactory;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.PlayerManager;
import ge18xx.player.PlayerTestFactory;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActionEffectsFactory;
import geUtilities.utilites.xml.UtilitiesTestFactory;

class InterruptionRoundTests {
	GameTestFactory gameTestFactory;
	RoundTestFactory roundTestFactory;
	PlayerTestFactory playerTestFactory;
	UtilitiesTestFactory utilitiesTestFactory;
	BankTestFactory bankTestFactory;
	GameManager gameManager;
	PlayerManager mPlayerManager;
	RoundManager roundManager;
	OperatingRound mOperatingRound;
	ActionEffectsFactory actionEffectsFactory;
	StartPacketTestFactory startPacketTestFactory;
	BankPool mBankPool;
	Bank mBank;
	
	@BeforeEach
	void setup () {
		gameTestFactory = new GameTestFactory ();
		gameManager = gameTestFactory.buildGameManager ();
		bankTestFactory = new BankTestFactory ();
		mBankPool = bankTestFactory.buildBankPoolMock (gameManager, "Bank Pool");
		mBank = bankTestFactory.buildBankMock (gameManager, "Bank");
		startPacketTestFactory = new StartPacketTestFactory (gameManager, mBank);
		roundTestFactory = new RoundTestFactory ();
		playerTestFactory = new PlayerTestFactory (gameManager);
		utilitiesTestFactory = new UtilitiesTestFactory ();
		roundManager = roundTestFactory.buildRoundManager ();
		mPlayerManager = playerTestFactory.buildPlayerManagerMock (4);
		mOperatingRound = roundTestFactory.buildOperatingRoundMock (mPlayerManager, roundManager);
		actionEffectsFactory = new ActionEffectsFactory (gameManager, utilitiesTestFactory);
	}
	
	@Test
	@DisplayName ("Test Interruption Round Triggering")
	void testInterruptionRoundTriggering () {
		Action tAction;
		
		tAction = actionEffectsFactory.getTestActionAt (1);
		assertEquals ("Change Round Action", tAction.getName ());
	}

}
