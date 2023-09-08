package ge18xx.company.special;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.GUI;

public class LoanRepayment extends TriggerClass implements ActionListener {
	public static final String DONE = "Done";
	public static final String NOT_ACTING_PRESIDENT = "You are not the Acting President";
	public static final String FRAME_TITLE = "Loan Repayment Frame";
	
	XMLFrame allLoanRepaymentFrame;
	GameManager gameManager;
	int currentPlayerIndex;
	boolean currentPlayerDone;
	JPanel allLoanRepaymentJPanel;
	
	public LoanRepayment (GameManager aGameManager) {
		String tFullFrameTitle;
		gameManager = aGameManager;
		tFullFrameTitle = gameManager.createFrameTitle (FRAME_TITLE);
		buildAllPlayers (tFullFrameTitle);
	}

	public void buildAllPlayers (String aFrameName) {
		Border tMargin;
		Point tRoundFrameOffset;

		allLoanRepaymentFrame = new XMLFrame (aFrameName, gameManager);
		allLoanRepaymentFrame.setSize (800, 600);
		
		allLoanRepaymentJPanel = new JPanel ();
		tMargin = new EmptyBorder (10,10,10,10);
		allLoanRepaymentJPanel.setBorder (tMargin);
		
		allLoanRepaymentJPanel.setLayout (new BoxLayout (allLoanRepaymentJPanel, BoxLayout.Y_AXIS));

		setupPlayers ();
		
		allLoanRepaymentFrame.add (allLoanRepaymentJPanel);
		
		tRoundFrameOffset = gameManager.getOffsetRoundFrame ();
		allLoanRepaymentFrame.setLocation (tRoundFrameOffset);
		gameManager.addNewFrame (allLoanRepaymentFrame);
		allLoanRepaymentFrame.showFrame ();
	}

	public void setupPlayers () {
		List<Player> tPlayers;
		PlayerManager tPlayerManager;
		Player tActingPresident;
		
		tActingPresident = findActingPresident ();
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		currentPlayerIndex = tPlayerManager.getPlayerIndex (tActingPresident);
		updatePlayers (tPlayers, tActingPresident);
	}

	public boolean updateToNextPlayer (List<Player> aPlayers) {
		Player tActingPresident;
		Player tFirstPresident;
		PlayerManager tPlayerManager;
		int tNextPlayerIndex;
		boolean tFoundNextPlayer;
		
		tPlayerManager = gameManager.getPlayerManager ();
		tNextPlayerIndex = tPlayerManager.getNextPlayerIndex (currentPlayerIndex);
		tActingPresident = tPlayerManager.getPlayer (tNextPlayerIndex);
		tFirstPresident = findActingPresident ();
		if (tActingPresident == tFirstPresident) {
			tFoundNextPlayer = false;
			allRepaymentsDone ();
		} else {
			tFoundNextPlayer = true;
			currentPlayerIndex = tNextPlayerIndex;
			System.out.println ("Updated to Next Player " + tActingPresident.getName () + " (" + tNextPlayerIndex + ")");
			updatePlayers (aPlayers, tActingPresident);
		}
		
		return tFoundNextPlayer;
	}
	
	public void allRepaymentsDone () {
		System.out.println ("All Players processed");
		allLoanRepaymentFrame.hideFrame ();
	}
	
	public void updatePlayers (List<Player> aPlayers, Player aActingPresident) {
		Color tBackgroundColor;
		boolean tActingPlayer;
		PlayerLoanRepaymentJPanel tPlayerLoanRepaymentPanel;
		
		currentPlayerDone = false;
		allLoanRepaymentJPanel.removeAll ();
		for (Player tPlayer : aPlayers) {
			if (aActingPresident == tPlayer) {
				if (gameManager.getClientUserName ().equals (aActingPresident.getName ())) {
					tBackgroundColor = Color.ORANGE;
					tActingPlayer = true;
				} else {
					tBackgroundColor = GUI.defaultColor;
					tActingPlayer = false;
				}
			} else {
				tBackgroundColor = GUI.defaultColor;
				tActingPlayer = false;
			}

			tPlayerLoanRepaymentPanel = new PlayerLoanRepaymentJPanel (gameManager, this, tPlayer, tActingPlayer);
			tPlayerLoanRepaymentPanel.setBackground (tBackgroundColor);
			allLoanRepaymentJPanel.add (tPlayerLoanRepaymentPanel);
			allLoanRepaymentJPanel.add (Box.createVerticalStrut (10));
		}
		allLoanRepaymentJPanel.revalidate ();
	}

	public Player findActingPresident () {
		Corporation tActingCorporation;
		Player tActingPlayer;
		
		tActingCorporation = gameManager.getOperatingCompany ();
		tActingPlayer = (Player) tActingCorporation.getPresident ();
		
		return tActingPlayer;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		System.out.println ("Repayment Frame Action Triggered");
	}
}
