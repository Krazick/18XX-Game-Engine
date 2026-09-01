package ge18xx.round.action;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Point;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;

public class WaitForReponseFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	PlayerManager playerManager;
	Player waitingForPlayer;
	Player askingPlayer;

	public WaitForReponseFrame (String aTitle, Player aWaitingForPlayer, 
						Player aAskingPlayer) throws HeadlessException {
		super (aTitle);

		GameManager tGameManager;
		String tPlayerName;

		waitingForPlayer = aWaitingForPlayer;
		askingPlayer = aAskingPlayer;
		tGameManager = aWaitingForPlayer.getGameManager ();
		playerManager = tGameManager.getPlayerManager ();
		tPlayerName = aWaitingForPlayer.getName ();
		buildWaitFrame (tPlayerName);
	}

	private void buildWaitFrame (String aPlayerName) {
		Point tRoundPoint;
		JLabel tWaitMessage;
		JPanel tMessagePanel;

		tMessagePanel = new JPanel ();
		tMessagePanel.setLayout (new BoxLayout (tMessagePanel, BoxLayout.Y_AXIS));
		tMessagePanel.setAlignmentY (Component.CENTER_ALIGNMENT);
		tMessagePanel.add (Box.createVerticalStrut (10));	
		
		tWaitMessage = new JLabel ("Waiting for Response from " + aPlayerName);
		tWaitMessage.setAlignmentX (CENTER_ALIGNMENT);
		tWaitMessage.setVisible (true);
		tMessagePanel.add (tWaitMessage);
		add (tMessagePanel);
		setSize (400, 100);
		
		tRoundPoint = playerManager.getOffsetRoundFramePoint ();
		setLocation (tRoundPoint);
		setAlwaysOnTop (true);
	}

	public boolean isWaitingForResponse () {
		boolean tIsWaitingForResponse;
		ActorI.ActionStates tPrimaryActionState;

		tPrimaryActionState = askingPlayer.getPrimaryActionState ();
		tIsWaitingForResponse = false;
		if (tPrimaryActionState.equals (ActorI.ActionStates.WaitingResponse)) {
			tIsWaitingForResponse = true;
		}

		return tIsWaitingForResponse;
	}

	/**
	 * When the Corporation needs to wait for a Response from a Network Player, 
	 * State is ActorI.ActionStates.WaitingResponse
	 * Put this thread to sleep, in X second chunks
	 *
	 */
	public void waitForResponse () {
		waitForResponse (20);
	}

	public void waitForResponse (int aWaitTime) {
		showFrame ();
		while (isWaitingForResponse ()) {
			try {
				Thread.sleep (aWaitTime);
				askingPlayer.setPrimaryActionState (ActorI.ActionStates.Pass);
				// Added above to abort out of the loop. 
				// Need to check if a Response for the Wait State Arrived. 
				// Should Ask
			} catch (InterruptedException eException) {
				System.err.println ("Waiting for the Response to Clear - Exception");
				eException.printStackTrace ();
			}
		}
		hideFrame ();
	}

	public void showFrame () {
		setVisible (true);
		toFront ();
	}

	public void hideFrame () {
		setVisible (false);
	}
}
