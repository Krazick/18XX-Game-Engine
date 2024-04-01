package ge18xx.round.action.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.BankTestFactory;
import ge18xx.company.Certificate;
import ge18xx.company.CertificateTestFactory;
import ge18xx.company.CompanyTestFactory;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.phase.PhaseInfo;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.PlayerTestFactory;
import ge18xx.player.Portfolio;
import ge18xx.round.RoundManager;
import ge18xx.round.RoundTestFactory;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.TransferOwnershipAction;

@DisplayName ("Transfer Ownership Effect Constructor Tests")
class TransferOwnershipEffectTestConstructor {
	TransferOwnershipEffect effectAlpha;
	TransferOwnershipEffect effectBeta;
	ShareCompany companyBeta;
	ShareCompany companyGamma;
	Player mPlayerActorAlpha;
	Player mPlayerActorDelta;
	GameManager mGameManager;
	GameManager gameManager;
	PhaseInfo mPhaseInfo = Mockito.mock (PhaseInfo.class);
	PlayerManager playerManager;
	GameTestFactory gameTestFactory;
	CompanyTestFactory companyTestFactory;
	PlayerTestFactory playerTestFactory;
	CertificateTestFactory certificateTestFactory;
	Certificate certificate;
	Bank bank;
	BankPool bankPool;
	BankTestFactory bankTestFactory;
	RoundTestFactory roundTestFactory;
	RoundManager roundManager;

	/*
	<GA><Action actor="JeffW" chainPrevious="true" class="ge18xx.round.action.TransferOwnershipAction" dateTime="1698871606396" name="Transfer Ownership Action" number="1354" roundID="10.2" roundType="Operating Round" totalCash="12000">
	<Effects>
		<Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CGR" fromActor="Bank IPO" fromName="Bank IPO" isAPrivate="false" name="Transfer Ownership" percentage="10" president="true" toActor="JeffW"/>
		<Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CGR" fromActor="JeffW" fromName="JeffW" isAPrivate="false" name="Transfer Ownership" percentage="5" president="false" toActor="Bank"/>
		<Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CGR" fromActor="JeffW" fromName="JeffW" isAPrivate="false" name="Transfer Ownership" percentage="5" president="false" toActor="Bank"/>
	</Effects></Action></GA>


<GA><Action actor="JeffW" chainPrevious="false" class="ge18xx.round.action.TransferOwnershipAction" dateTime="1698872783625" name="Transfer Ownership Action" number="1355" roundID="10.2" roundType="Operating Round" totalCash="12000">
<Effects>
<Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="BBG" fromActor="JeffW" fromName="JeffW" isAPrivate="false" name="Transfer Ownership" percentage="10" president="false" toActor="Bank Closed"/><Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="BBG" fromActor="JeffW" fromName="JeffW" isAPrivate="false" name="Transfer Ownership" percentage="10" president="false" toActor="Bank Closed"/><Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="BBG" fromActor="JeffW" fromName="JeffW" isAPrivate="false" name="Transfer Ownership" percentage="20" president="true" toActor="Bank Closed"/><Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CV" fromActor="JeffW" fromName="JeffW" isAPrivate="false" name="Transfer Ownership" percentage="10" president="false" toActor="Bank Closed"/><Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CV" fromActor="JeffW" fromName="JeffW" isAPrivate="false" name="Transfer Ownership" percentage="10" president="false" toActor="Bank Closed"/><Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CV" fromActor="JeffW" fromName="JeffW" isAPrivate="false" name="Transfer Ownership" percentage="10" president="false" toActor="Bank Closed"/><Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CV" fromActor="JeffW" fromName="JeffW" isAPrivate="false" name="Transfer Ownership" percentage="10" president="false" toActor="Bank Closed"/><Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CV" fromActor="JeffW" fromName="JeffW" isAPrivate="false" name="Transfer Ownership" percentage="20" president="true" toActor="Bank Closed"/><Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CGR" fromActor="Bank" fromName="Bank IPO" isAPrivate="false" name="Transfer Ownership" percentage="10" president="false" toActor="JeffW"/><Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CGR" fromActor="Bank" fromName="Bank IPO" isAPrivate="false" name="Transfer Ownership" percentage="10" president="false" toActor="JeffW"/><Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CGR" fromActor="Bank" fromName="Bank IPO" isAPrivate="false" name="Transfer Ownership" percentage="10" president="false" toActor="JeffW"/><Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CGR" fromActor="Bank" fromName="Bank IPO" isAPrivate="false" name="Transfer Ownership" percentage="10" president="false" toActor="JeffW"/><Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CGR" fromActor="Bank" fromName="Bank IPO" isAPrivate="false" name="Transfer Ownership" percentage="10" president="false" toActor="JeffW"/></Effects></Action></GA>

<GA><Action actor="JeffW" chainPrevious="true" class="ge18xx.round.action.TransferOwnershipAction" dateTime="1698872783625" name="Transfer Ownership Action" number="1356" roundID="10.2" roundType="Operating Round" totalCash="12000">
<Effects>

<Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CGR" fromActor="Bank" fromName="Bank IPO" isAPrivate="false" name="Transfer Ownership" percentage="20" president="true" toActor="JeffW"/>
<Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CGR" fromActor="JeffW" fromName="JeffW" isAPrivate="false" name="Transfer Ownership" percentage="10" president="false" toActor="Bank"/>
<Effect class="ge18xx.round.action.effects.TransferOwnershipEffect" companyAbbrev="CGR" fromActor="JeffW" fromName="JeffW" isAPrivate="false" name="Transfer Ownership" percentage="10" president="false" toActor="Bank"/>
</Effects></Action></GA>

	*/
	
	@BeforeEach
	void setUp () throws Exception {
		String tClientName;
		String tPlayer2Name;
		String tPlayer3Name;
		Portfolio mPortfolioAlpha;
		Portfolio mPortfolioDelta;
		GameInfo tGameInfo;

		tClientName = "TFBuster";
		tPlayer2Name = "ToEffectTesterAlpha";
		tPlayer3Name = "ToEffectTesterDelta";
		gameTestFactory = new GameTestFactory ();
		companyTestFactory = new CompanyTestFactory (gameTestFactory);
		certificateTestFactory = new CertificateTestFactory ();
		bankTestFactory = new BankTestFactory ();
		roundTestFactory = new RoundTestFactory ();
		
		gameManager = gameTestFactory.buildGameManager (tClientName);
		mGameManager = gameTestFactory.buildGameManagerMock (tClientName);
		Mockito.when (mGameManager.gameHasPrivates ()).thenReturn (true);
		Mockito.when (mGameManager.gameHasMinors ()).thenReturn (false);
		Mockito.when (mGameManager.gameHasShares ()).thenReturn (true);
		
		tGameInfo = gameTestFactory.buildGameInfo (1);
		gameManager.setGameInfo (tGameInfo);
		bankPool = bankTestFactory.buildBankPool (gameManager);
		gameManager.setBank (100);
		gameManager.setBankPool (bankPool);
		bank = gameManager.getBank ();
		roundManager = roundTestFactory.buildRoundManagerMock ();

		playerTestFactory = new PlayerTestFactory (mGameManager);
		playerManager = playerTestFactory.buildPlayerManager ();

		effectAlpha = new TransferOwnershipEffect ();
		
		mPlayerActorAlpha = playerTestFactory.buildPlayerMock (tPlayer2Name);
		mPlayerActorDelta = playerTestFactory.buildPlayerMock (tPlayer3Name);
		
		mPortfolioAlpha = Mockito.mock (Portfolio.class);
		Mockito.when (mPlayerActorAlpha.getPortfolio ()).thenReturn (mPortfolioAlpha);
		Mockito.when (mPlayerActorAlpha.getName ()).thenReturn (tPlayer2Name);
		Mockito.when (mPlayerActorAlpha.getAbbrev ()).thenReturn (tPlayer2Name);
		Mockito.when (mPortfolioAlpha.transferOneCertificateOwnership (any (Portfolio.class), any (Certificate.class))).thenReturn (true);

		mPortfolioDelta = Mockito.mock (Portfolio.class);
		Mockito.when (mPortfolioDelta.transferOneCertificateOwnership (any (Portfolio.class), any (Certificate.class))).thenReturn (true);
		
		Mockito.when (mPlayerActorDelta.getPortfolio ()).thenReturn (mPortfolioDelta);
		
		companyBeta = companyTestFactory.buildAShareCompany (1);
		Mockito.when (mPhaseInfo.getWillFloatPercent ()).thenReturn (60);
		Mockito.when (companyBeta.getMinSharesToFloat ()).thenReturn (6);
		Mockito.when (companyBeta.getCurrentPhaseInfo ()).thenReturn (mPhaseInfo);

		companyGamma = companyTestFactory.buildAShareCompany (2);

		certificate = certificateTestFactory.buildCertificate (companyBeta, true, 20, mPortfolioAlpha);
		Mockito.when (mPortfolioAlpha.getCertificate (0)).thenReturn (certificate);
		effectBeta = new TransferOwnershipEffect (mPlayerActorAlpha, certificate, mPlayerActorDelta);
	}

	@Test
	@DisplayName ("Simple Constructor Tests")
	void TransferOwnershipConstrucorTests () {
		Player tFoundPlayer;
		Player tFoundToPlayer;
		Certificate tCertificate;
		String tReportResult = "--Effect: Transfer Ownership of 20% of TPRR (President Share) from ToEffectTesterAlpha to ToEffectTesterDelta.";
		
		assertEquals ("TPRR", certificate.getCompanyAbbrev ());
		assertFalse (effectAlpha.actorIsSet (), "Actor is Set");
		assertEquals (ActorI.NO_NAME, effectAlpha.getToActorName ());

		assertTrue (effectBeta.actorIsSet (), "Actor is not Set");
		assertEquals ("Transfer Ownership", effectBeta.getName ());
		assertEquals ("ToEffectTesterAlpha", effectBeta.getActorName ());
		assertEquals ("ToEffectTesterDelta", effectBeta.getToActorName ());

		tFoundPlayer = (Player) effectBeta.getActor ();
		tFoundToPlayer = (Player) effectBeta.getToActor ();
		assertEquals ("ToEffectTesterAlpha", tFoundPlayer.getName ());
		assertEquals (tReportResult, effectBeta.getEffectReport (null));
		assertNotNull (effectBeta.getToActorName ());
		assertEquals ("ToEffectTesterDelta", effectBeta.getToActorName ());
		assertEquals ("ToEffectTesterDelta", tFoundToPlayer.getName ());

		tCertificate = effectBeta.getCertificate ();
		assertEquals ("TPRR", tCertificate.getCompanyAbbrev ());
		assertEquals ("TPRR", effectBeta.getCompanyAbbrev ());

		assertTrue (effectBeta.applyEffect (null));
		assertFalse (effectBeta.wasNewStateAuction ());
		assertTrue (effectBeta.undoEffect (null));
	}
	
	@Test
	@DisplayName ("Test with Action Created")
	void TransferOwnershipWithActionCreation () {
		TransferOwnershipAction tTransferOwnershipAction1;
		TransferOwnershipAction tTransferOwnershipAction2;
		TransferOwnershipAction tTransferOwnershipAction3;
		String tOperatingRoundID;
		String tFromName;
		String tToName;
		String tExpectedActionReport1;
		String tExpectedActionReport2;
		String tExpectedActionReport3;
		String tExpectedEffectReport1;
		String tExpectedEffectReport2;
		String tExpectedEffectReport3;
		
		tOperatingRoundID = "1.1";
		tFromName = "Alpha Tester";
		tToName = "Alpha Tester";
		
		tExpectedEffectReport1 = "--Effect: Transfer Ownership of 20% of TPRR (President Share) from Alpha Tester to Bank Closed.";
		tExpectedEffectReport2 = "--Effect: Transfer Ownership of 20% of TPRR (President Share) from Bank IPO to Alpha Tester.";
		tExpectedEffectReport3 = "--Effect: Transfer Ownership of 20% of TPRR (President Share) from Bank to ToEffectTesterAlpha.";
		tExpectedActionReport1 = "0. Operating Round 1.1: ToEffectTesterAlpha performed Transfer Ownership Action Chain to Previous [false]\n"
				+ tExpectedEffectReport1;
		tExpectedActionReport2 = "0. Operating Round 1.1: ToEffectTesterAlpha performed Transfer Ownership Action Chain to Previous [false]\n"
				+ tExpectedEffectReport2;
		tExpectedActionReport3 = "0. Operating Round 1.1: ToEffectTesterAlpha performed Transfer Ownership Action Chain to Previous [false]\n"
				+ tExpectedEffectReport3;
	
		tTransferOwnershipAction1 = new TransferOwnershipAction (ActorI.ActionStates.OperatingRound, 
						tOperatingRoundID, mPlayerActorAlpha);

		transferOwnershipTest (tTransferOwnershipAction1, mPlayerActorAlpha, tFromName, bank, Bank.CLOSED, 
					tExpectedActionReport1, tExpectedEffectReport1);
	
		tTransferOwnershipAction2 = new TransferOwnershipAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, mPlayerActorAlpha);
		transferOwnershipTest (tTransferOwnershipAction2, bank, Bank.IPO, mPlayerActorAlpha, tToName, 
				tExpectedActionReport2, tExpectedEffectReport2);

		tTransferOwnershipAction3 = new TransferOwnershipAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, mPlayerActorAlpha);
		transferOwnershipTest (tTransferOwnershipAction3, bank, mPlayerActorAlpha,  
				tExpectedActionReport3, tExpectedEffectReport3);
		//--Effect: Transfer Ownership of 10% of CV from JeffW to Bank.
	}

	public void transferOwnershipTest (TransferOwnershipAction aTransferOwnershipAction, ActorI aFrom, String aFromName, 
				ActorI aTo, String aToName, String aExpectedActionReport, String aExpectedEffectReport) {
		TransferOwnershipEffect tTransferOwnershipEffect;
		String tActionReport;
		String tEffectReport;
		
		aTransferOwnershipAction.addTransferOwnershipEffect (aFrom, aFromName, certificate, aTo, aToName);
		
		tTransferOwnershipEffect = (TransferOwnershipEffect) aTransferOwnershipAction.getEffect (0);
		
		tActionReport = aTransferOwnershipAction.getActionReport (roundManager);
		tEffectReport = tTransferOwnershipEffect.getEffectReport (roundManager);
	
		assertEquals (aExpectedActionReport, tActionReport);
		assertEquals (aExpectedEffectReport, tEffectReport);
	}
	
	public void transferOwnershipTest (TransferOwnershipAction aTransferOwnershipAction, ActorI aFrom, 
			ActorI aTo, String aExpectedActionReport, String aExpectedEffectReport) {
		TransferOwnershipEffect tTransferOwnershipEffect;
		String tActionReport;
		String tEffectReport;
		
		aTransferOwnershipAction.addTransferOwnershipEffect (aFrom, certificate, aTo);
		
		tTransferOwnershipEffect = (TransferOwnershipEffect) aTransferOwnershipAction.getEffect (0);
		
		tActionReport = aTransferOwnershipAction.getActionReport (roundManager);
		tEffectReport = tTransferOwnershipEffect.getEffectReport (roundManager);
	
		assertEquals (aExpectedActionReport, tActionReport);
		assertEquals (aExpectedEffectReport, tEffectReport);
	}
}
