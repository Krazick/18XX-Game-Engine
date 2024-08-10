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
import ge18xx.bank.BankPool;
import ge18xx.bank.BankTestFactory;
import ge18xx.bank.StartPacketFrame;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.PlayerTestFactory;
import ge18xx.round.RoundManager;
import ge18xx.round.RoundTestFactory;
import geUtilities.utilites.xml.UtilitiesTestFactory;
import geUtilities.xml.XMLNode;

@DisplayName ("CashTransferEffect Constructor Tests")
class CashTransferEffectTestConstructor {
	CashTransferEffect effectAlpha;
	CashTransferEffect effectBeta;
	Player actorBeta;
	Player actorGamma;
	GameManager mGameManager;
	GameManager gameManager;
	RoundManager mRoundManager;
	Bank bank;
	BankPool bankPool;
	PlayerManager playerManager;
	PlayerTestFactory playerTestFactory;
	GameTestFactory gameTestFactory;
	RoundTestFactory roundTestFactory;
	BankTestFactory bankTestFactory;
	private UtilitiesTestFactory utilitiesTestFactory;
	int cashAmount;

	@BeforeEach
	void setUp () throws Exception {
		String tClientName;
		String tPlayer2Name;
		String tPlayer3Name;
		StartPacketFrame tStartPacketFrame;
		GameInfo tGameInfo;

		tClientName = "TFBuster";
		tPlayer2Name = "ToEffectTesterBeta";
		tPlayer3Name = "ToEffectTesterGamma";
		gameTestFactory = new GameTestFactory ();
		utilitiesTestFactory = gameTestFactory.getUtilitiesTestFactory ();
		roundTestFactory = new RoundTestFactory ();
		bankTestFactory = new BankTestFactory ();
		
		gameManager = gameTestFactory.buildGameManager (tClientName);
		mGameManager = gameTestFactory.buildGameManagerMock (tClientName);
		Mockito.when (mGameManager.gameHasPrivates ()).thenReturn (true);
		Mockito.when (mGameManager.gameHasMinors ()).thenReturn (false);
		Mockito.when (mGameManager.gameHasShares ()).thenReturn (true);

		tGameInfo = gameTestFactory.buildGameInfo (1);
		gameManager.setGameInfo (tGameInfo);

		mRoundManager = roundTestFactory.buildRoundManagerMock ();
		bankPool = bankTestFactory.buildBankPool (gameManager);
		gameManager.setBank (100);
		gameManager.setBankPool (bankPool);
		bank = gameManager.getBank ();
		tStartPacketFrame = new StartPacketFrame ("Test CashTransfer Frame", mGameManager);
		bank.setStartPacketFrame (tStartPacketFrame);
		
		playerTestFactory = new PlayerTestFactory (mGameManager);
		playerManager = playerTestFactory.buildPlayerManager ();
		gameManager.setPlayerManager (playerManager);
		
		effectAlpha = new CashTransferEffect ();
		cashAmount = 100;
		actorBeta = playerTestFactory.buildPlayer (tPlayer2Name, playerManager, 0);
		actorGamma = playerTestFactory.buildPlayer (tPlayer3Name, playerManager, 0);
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
	
	private XMLNode buildEffectXMLNode (String aEffectTextXML) {
		XMLNode tEffectXMLNode;
		
		tEffectXMLNode = utilitiesTestFactory.buildXMLNode (aEffectTextXML);

		return tEffectXMLNode;
	}

	@Test 
	@DisplayName ("CashTransfer Display Report Test")
	void cashTransferReportTest () {
		XMLNode tEffectXMLNode;
		String tEffectReport;
		CashTransferEffect tEffectGamma;
		
		String tCashTransferReportXML = "<Effect cash=\"250\" class=\"ge18xx.round.action.effects.CashTransferEffect\" fromActor=\"Bank\" isAPrivate=\"false\" name=\"Cash Transfer\" toActor=\"ToEffectTesterGamma\"/>";
		
		tEffectXMLNode = buildEffectXMLNode (tCashTransferReportXML);
		
		tEffectGamma = new CashTransferEffect (tEffectXMLNode, gameManager);
		tEffectReport = tEffectGamma.getEffectReport (mRoundManager);
		assertEquals ("--Effect: Cash Transfer of 250 from Bank to ToEffectTesterGamma.", tEffectReport);
//		System.out.println (tEffectReport);
	}
}
