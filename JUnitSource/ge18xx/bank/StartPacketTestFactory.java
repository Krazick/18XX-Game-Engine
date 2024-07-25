package ge18xx.bank;

import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.player.Portfolio;

public class StartPacketTestFactory {
	private GameManager gameManager;
	private Bank bank;
	
	public StartPacketTestFactory (GameManager aGameManager, Bank aBank) {
		gameManager = aGameManager;
		bank = aBank;
	}

	public StartPacketFrame buildStartPacketFrame (String aFrameName) {
		StartPacketFrame tStartPacketFrame;
		
		tStartPacketFrame = new StartPacketFrame (aFrameName, gameManager);
		
		return tStartPacketFrame;
	}
	
	public StartPacketFrame buildStartPacketFrameMock (String aFrameName) {
		StartPacketFrame mStartPacketFrame;
		Portfolio tPortfolio;

		mStartPacketFrame = Mockito.mock (StartPacketFrame.class);
		tPortfolio = bank.getPortfolio ();
		Mockito.when (mStartPacketFrame.loadStartPacketWithCertificates (tPortfolio)).thenReturn (true);

		return mStartPacketFrame;
	}
}
