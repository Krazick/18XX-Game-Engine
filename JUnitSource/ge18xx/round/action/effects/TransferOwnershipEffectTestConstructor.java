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
		
		tGameInfo = gameTestFactory.buildGameInfo ();
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
		TransferOwnershipAction tTransferOwnershipAction;
		String tOperatingRoundID;
		String tFromName;
		
		tOperatingRoundID = "1.1";
		tFromName = "Alpha Tester";
		tTransferOwnershipAction = new TransferOwnershipAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, mPlayerActorAlpha);

		tTransferOwnershipAction.addTransferOwnershipEffect (mPlayerActorAlpha, tFromName, certificate, bank, Bank.CLOSED);
		tTransferOwnershipAction.printActionReport (roundManager);
	}
}
