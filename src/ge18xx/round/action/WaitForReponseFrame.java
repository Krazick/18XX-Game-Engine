package ge18xx.round.action;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JLabel;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;

public class WaitForReponseFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	PlayerManager playerManager;
	Player waitingForPlayer;
	Player askingPlayer;
	
	public WaitForReponseFrame (String aTitle, Player aWaitingForPlayer, Player aAskingPlayer) throws HeadlessException {
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
	
	private void buildWaitFrame (String aPlayerName) {
		Point tRoundPoint;
		JLabel tWaitMessage;
		
		tWaitMessage = new JLabel ("Waiting for Response from " + aPlayerName);
		setLayout (new FlowLayout (FlowLayout.CENTER));
		setSize (400, 400);
		add (tWaitMessage);
		setBackground (Color.PINK);
		
		tRoundPoint = playerManager.getOffsetRoundFramePoint ();
		setSize (400, 200);
		setLocation (tRoundPoint);
		setAlwaysOnTop (true);
//		setDefaultCloseOperation (JDialog.DO_NOTHING_ON_CLOSE);
	}
	
	/**
	 * When the Corporation has need to wait for a Response from a Network Player, State is ActorI.ActionStates.WaitingResponse
	 * Put this thread to sleep, in 2 second chunks
	 * 
	 */
	public void waitForResponse () {
		int tWaitTime;
		
		tWaitTime = 2000; // Wait for 2 Seconds before testing if a Response came back
		
		setVisible (true);
		while (isWaitingForResponse ()) {
			try {
				Thread.sleep (tWaitTime);
			} catch (InterruptedException eException) {
				System.err.println ("Waiting for the Response to Clear - Exception");
				eException.printStackTrace ();
			}
		}
		setVisible (false);
	}
}
