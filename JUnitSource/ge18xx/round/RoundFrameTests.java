package ge18xx.round;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.swing.JLabel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ge18xx.bank.Bank;
import ge18xx.bank.BankTestFactory;
import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;
import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.player.PlayerManager;
import ge18xx.player.PlayerTestFactory;
import ge18xx.toplevel.FrameTestFactory;
import ge18xx.toplevel.MinorCompaniesFrame;
import ge18xx.toplevel.PrivatesFrame;
import ge18xx.toplevel.ShareCompaniesFrame;

@DisplayName ("Round Frame Tests")
class RoundFrameTests {
	GameTestFactory gameTestFactory;
	RoundTestFactory roundTestFactory;
	FrameTestFactory frameTestFactory;
	BankTestFactory bankTestFactory;
	RoundFrame roundFrame;
	GameManager mGameManager;
	RoundManager mRoundManager;
	
	@BeforeEach
	void setUp () throws Exception {
		PrivatesFrame tPrivatesFrame;
		MinorCompaniesFrame tMinorCompaniesFrame;
		ShareCompaniesFrame tShareCompaniesFrame;
		PlayerTestFactory tPlayerTestFactory;
		PlayerManager tPlayerManager;
		PhaseManager mPhaseManager;
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

		tCurrentPhaseInfo = gameTestFactory.buildPhaseInfoMock ();
		Mockito.when (mPhaseManager.getCurrentPhaseInfo ()).thenReturn (tCurrentPhaseInfo);
		Mockito.when (mRoundManager.getPhaseManager ()).thenReturn (mPhaseManager);
//		tPlayerTestFactory = new PlayerTestFactory (mGameManager);
//		tPlayerManager = tPlayerTestFactory.buildPlayerManager ();
//		tPhaseManager = new PhaseManager ();
		Mockito.when (mRoundManager.getBank ()).thenReturn (mBank);
		tBankCashLabel = new JLabel ("Cash from mBank");
		Mockito.when (mBank.getBankCashLabel ()).thenReturn (tBankCashLabel);

		frameTestFactory = new FrameTestFactory (mGameManager, mRoundManager);
//		tPrivatesFrame = frameTestFactory.buildPrivatesFrame ("Privates Test Frame");
//		tMinorCompaniesFrame = frameTestFactory.buildMinorCompaniesFrame ("Minor Companies Test Frame");
//		tShareCompaniesFrame = frameTestFactory.buildShareCompaniesFrame ("Share Companies Test Frame");
//		
//		mGameManager.setPrivatesFrame (tPrivatesFrame);
//		mGameManager.setMinorCompaniesFrame (tMinorCompaniesFrame);
//		mGameManager.setShareCompaniesFrame (tShareCompaniesFrame);
//		mGameManager.setPlayerManager (tPlayerManager);
//		mGameManager.setPhaseManager (tPhaseManager);
		
//		roundFrame = roundTestFactory.buildRoundFrame (mGameManager, mRoundManager);
	}

	@Test
	@DisplayName ("Round Frame Tests")
	void roundFrameTests () {
//		String tFrameTitle;
//		
//		tFrameTitle = roundFrame.getTitle ();
//		assertEquals ("DUMMY", tFrameTitle);
	}

}
