package ge18xx.toplevel;

import static org.mockito.ArgumentMatchers.anyString;

import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;

public class FrameTestFactory {

	GameManager gameManager;
	RoundManager roundManager;

	/**
	 * Basic Constructor that saves the provided Game Manager
	 *
	 * @param aGameManager The GameManager to save in this Test Factory
	 * @param aRoundManager The RoundManager to save in this Test Factory
	 *
	 */
	public FrameTestFactory (GameManager aGameManager, RoundManager aRoundManager) {
		gameManager = aGameManager;
		roundManager = aRoundManager;
	}

	/**
	 * Build a basic Privates Frame, given the stored Round Manager
	 *
	 * @param a Frame Title
	 * @return a Privates Frame;
	 *
	 */
	public PrivatesFrame buildPrivatesFrame (String aFrameTitle) {
		PrivatesFrame tPrivatesFrame;

		tPrivatesFrame = new PrivatesFrame (aFrameTitle, roundManager);

		return tPrivatesFrame;
	}
	
	/**
	 * Build a basic Minor Companies Frame, given the stored Round Manager
	 *
	 * @param a Frame Title
	 * @return a Minor Companies Frame;
	 *
	 */
	public MinorCompaniesFrame buildMinorCompaniesFrame (String aFrameTitle) {
		MinorCompaniesFrame tMinorCompaniesFrame;

		tMinorCompaniesFrame = new MinorCompaniesFrame (aFrameTitle, roundManager);

		return tMinorCompaniesFrame;
	}

	/**
	 * Build a basic ShareCompanies Frame, given the stored Round Manager
	 *
	 * @param a Frame Title
	 * @return a Share Companies Frame;
	 *
	 */
	public ShareCompaniesFrame buildShareCompaniesFrame (String aFrameTitle) {
		ShareCompaniesFrame tShareCompaniesFrame;
		
		tShareCompaniesFrame = new ShareCompaniesFrame (aFrameTitle, roundManager);

		return tShareCompaniesFrame;
	}
	
	/**
	 * Build a Mock for the ShareCompanies Frame
	 *
	 * @param a Frame Title
	 * @return a Share Companies Frame;
	 *
	 */

	public ShareCompaniesFrame buildShareCompaniesFrameMock (String aFrameTitle) {
		ShareCompaniesFrame mShareCompaniesFrame;
		
		mShareCompaniesFrame = Mockito.mock (ShareCompaniesFrame.class);
		Mockito.when (mShareCompaniesFrame.getName ()).thenReturn (aFrameTitle);
		Mockito.when (mShareCompaniesFrame.getCorporationState (anyString ())).thenReturn (ActorI.ActionStates.NoState);

		return mShareCompaniesFrame;
	}
}
