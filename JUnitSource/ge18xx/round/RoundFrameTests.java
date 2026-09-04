package ge18xx.round;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.mockito.ArgumentMatchers.any;
import javax.swing.JLabel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankTestFactory;
import ge18xx.company.CorporationList;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.player.PlayerTestFactory;
import ge18xx.player.Player;
import ge18xx.player.PlayerFrame;
import ge18xx.player.PlayerManager;
//import ge18xx.player.PlayerTestFactory;
import ge18xx.toplevel.FrameTestFactory;
//import ge18xx.toplevel.MinorCompaniesFrame;
//import ge18xx.toplevel.PrivatesFrame;
//import ge18xx.toplevel.ShareCompaniesFrame;

@DisplayName ("Round Frame Tests")
class RoundFrameTests {
	GameTestFactory gameTestFactory;
	RoundTestFactory roundTestFactory;
	FrameTestFactory frameTestFactory;
	BankTestFactory bankTestFactory;
	PlayerTestFactory playerTestFactory;
	RoundFrame roundFrame;
	GameManager mGameManager;
	RoundManager mRoundManager;
	Round mCurrentRound;
	
	@BeforeEach
	void setUp () throws Exception {
//		PrivatesFrame tPrivatesFrame;
//		MinorCompaniesFrame tMinorCompaniesFrame;
//		ShareCompaniesFrame tShareCompaniesFrame;
		PlayerTestFactory tPlayerTestFactory;
		PlayerManager mPlayerManager;
		CorporationList mShareCompanies;
		PhaseManager mPhaseManager;
		PlayerFrame mPlayerFrame;
		Player mPlayer;
		Bank mBank;
		JLabel tBankCashLabel;
		PhaseInfo tCurrentPhaseInfo;

		gameTestFactory = new GameTestFactory ();
		mGameManager = gameTestFactory.buildGameManagerMock ();
		bankTestFactory = new BankTestFactory ();
		mBank = bankTestFactory.buildBankMock ();
		mPhaseManager = gameTestFactory.buildPhaseManagerMock ();
		roundTestFactory = new RoundTestFactory ();
		mRoundManager = roundTestFactory.buildRoundManagerMock ();
		tPlayerTestFactory = new PlayerTestFactory (mGameManager);
		mPlayer = tPlayerTestFactory.buildPlayerMock ("Buster");
		
		mPlayerManager = tPlayerTestFactory.buildPlayerManagerMock (3);
		mPlayerFrame = tPlayerTestFactory.buildPlayerFrameMock ();
		
		Mockito.when (mPlayerFrame.hasMustBuyCertificate ()).thenReturn (false);
		Mockito.when (mPlayerFrame.mustSellStock ()).thenReturn (false);
		Mockito.when (mPlayerFrame.isVisible ()).thenReturn (false);
		Mockito.when (mGameManager.getCurrentPlayer ()).thenReturn (mPlayer);
		Mockito.when (mGameManager.getCurrentPlayerFrame ()).thenReturn (mPlayerFrame);

		mCurrentRound = roundTestFactory.buildStockRoundMock (mPlayerManager, mRoundManager);
		tCurrentPhaseInfo = gameTestFactory.buildPhaseInfoMock ();
		Mockito.when (mGameManager.getBank ()).thenReturn (mBank);

		Mockito.when (mPhaseManager.getCurrentPhaseInfo ()).thenReturn (tCurrentPhaseInfo);
		Mockito.when (mRoundManager.getPhaseManager ()).thenReturn (mPhaseManager);
		Mockito.when (mRoundManager.getBank ()).thenReturn (mBank);
		Mockito.when (mRoundManager.getGameManager ()).thenReturn (mGameManager);
		
		mShareCompanies = Mockito.mock (CorporationList.class);
		Mockito.when (mGameManager.getShareCompanies ()).thenReturn (mShareCompanies);
		Mockito.when (mShareCompanies.addListeners (any (ListenerPanel.class))).thenReturn (true);
		Mockito.when (mShareCompanies.getCorporationCount ()).thenReturn (3);
		Mockito.when (mRoundManager.getShareCompanies ()).thenReturn (mShareCompanies);

		tBankCashLabel = new JLabel ("Cash from mBank");
		Mockito.when (mBank.getBankCashLabel ()).thenReturn (tBankCashLabel);

//		frameTestFactory = new FrameTestFactory (mGameManager, mRoundManager);
//		tPrivatesFrame = frameTestFactory.buildPrivatesFrame ("Privates Test Frame");
//		tMinorCompaniesFrame = frameTestFactory.buildMinorCompaniesFrame ("Minor Companies Test Frame");
//		tShareCompaniesFrame = frameTestFactory.buildShareCompaniesFrame ("Share Companies Test Frame");
//		
//		mGameManager.setPrivatesFrame (tPrivatesFrame);
//		mGameManager.setMinorCompaniesFrame (tMinorCompaniesFrame);
//		mGameManager.setShareCompaniesFrame (tShareCompaniesFrame);
//		mGameManager.setPlayerManager (tPlayerManager);
//		mGameManager.setPhaseManager (tPhaseManager);
		
		roundFrame = roundTestFactory.buildRoundFrameButtons (mGameManager, mRoundManager);
	}

	@Test
	@DisplayName ("Round Frame Initial Button Tests")
	void roundFrameButtonTests () {
		String tDoButtonText;
//		String tPassButtonText;
		String tPlayerName;
		boolean tEnableButton;
		
		tDoButtonText = "Player will operate Baltimore & Ohio";
		assertNull (roundFrame.doButton);
		roundFrame.updateDoButtonText (tDoButtonText);
		assertNull (roundFrame.doButton);		
		
		roundFrame.buildButtonsJPanel ();
		assertNotNull (roundFrame.buttonsJPanel);
		assertNotNull (roundFrame.doButton);
		assertEquals ("No Player Action", roundFrame.doButton.getText ());
		
		roundFrame.updateDoButtonText (tDoButtonText);
		assertEquals ("Player will operate Baltimore & Ohio", roundFrame.doButton.getText ());

		tEnableButton = false;
		roundFrame.enableDoButton (tEnableButton);
		assertFalse (roundFrame.doButton.isEnabled ());
		assertEquals ("You are not the President of the Company", roundFrame.doButton.getToolTipText ());
		
		tEnableButton = true;
		roundFrame.enableDoButton (tEnableButton);
		assertTrue (roundFrame.doButton.isEnabled ());
		assertEquals ("", roundFrame.doButton.getToolTipText ());
		
		assertNotNull (roundFrame.passButton);
		tPlayerName = "Buster";
		Mockito.when (mRoundManager.getCurrentRound ()).thenReturn (mCurrentRound);
		roundFrame.setCurrentPlayerText (tPlayerName);
		assertEquals ("Buster Pass in Stock Round", roundFrame.passButton.getText ());
	}
}
