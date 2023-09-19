package ge18xx.company.special;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import ge18xx.bank.BankPool;
import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.round.action.Action;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.GUI;

public class LoanRepayment extends TriggerClass implements ActionListener {
	public static final String DONE = "Done";
	public static final String NOT_ACTING_PRESIDENT = "You are not the Acting President";
	public static final String FRAME_TITLE = "Loan Repayment";
	
	XMLFrame allLoanRepaymentFrame;
	GameManager gameManager;
	int currentPlayerIndex;
	int shareFoldCount;
	boolean currentPlayerDone;
	JPanel allLoanRepaymentJPanel;
	JPanel bottomJPanel;
	String notificationText;
	
	public LoanRepayment (GameManager aGameManager) {
		String tFullFrameTitle;
		
		gameManager = aGameManager;
		tFullFrameTitle = gameManager.createFrameTitle (FRAME_TITLE);
		setNotificationText ("Time to repay company outstanding Loans");
		gameManager.setTriggerClass (this);
		buildAllPlayers (tFullFrameTitle);
	}
	
	public LoanRepayment (GameManager aGameManager, BuyTrainAction aBuyTrainAction) {
		String tFullFrameTitle;
		Player tActingPlayer;
		
		gameManager = aGameManager;
		tFullFrameTitle = gameManager.createFrameTitle (FRAME_TITLE);
		setNotificationText ("Time to repay company outstanding Loans");
		gameManager.setTriggerClass (this);
		buildAllPlayers (tFullFrameTitle);
		if (aBuyTrainAction != Action.NO_ACTION) {
			tActingPlayer = findActingPresident ();
			aBuyTrainAction.addShowSpecialPanelEffect (tActingPlayer, tActingPlayer);
		}
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
		allLoanRepaymentFrame.buildScrollPane (allLoanRepaymentJPanel);

		tRoundFrameOffset = gameManager.getOffsetRoundFrame ();
		allLoanRepaymentFrame.setLocation (tRoundFrameOffset);
		gameManager.addNewFrame (allLoanRepaymentFrame);
		
		allLoanRepaymentFrame.showFrame ();
		setShareFoldCount (0);
	}

	public void setShareFoldCount (int aCountToFold) {
		shareFoldCount = aCountToFold;
	}
	
	public void addShareFoldCount (int aShareCountToFold) {
		shareFoldCount += aShareCountToFold;
	}
	
	public int getShareFoldCount () {
		return shareFoldCount;
	}
	
	public boolean haveSharesToFold () {
		return shareFoldCount > 0;
	}
	
	public void setupPlayers () {
		List<Player> tPlayers;
		PlayerManager tPlayerManager;
		Player tActingPresident;
		int tCurrentPlayerIndex;
		
		tActingPresident = findActingPresident ();
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		tCurrentPlayerIndex = tPlayerManager.getPlayerIndex (tActingPresident);
		setCurrentPlayerIndex (tCurrentPlayerIndex);
		updatePlayers (tPlayers, tActingPresident);
	}

	@Override
	public void setCurrentPlayerIndex (int aCurrentPlayerIndex) {
		currentPlayerIndex = aCurrentPlayerIndex;
	}
	
	public int getCurrentPlayerIndex () {
		return currentPlayerIndex;
	}
	
	public JPanel buildPortfolioJPanel (Portfolio aPortfolio) {
		JPanel tPortfolioJPanel;
		JPanel tOwnershipPanel;
		JLabel tTitle;
		JLabel tEmptyOpenMarket;
		
		tPortfolioJPanel = new JPanel ();
		tPortfolioJPanel.setLayout (new BoxLayout (tPortfolioJPanel, BoxLayout.Y_AXIS));
		tTitle = new JLabel ("Open Market Portfolio");
		tPortfolioJPanel.add (tTitle);
		tOwnershipPanel = aPortfolio.buildOwnershipPanel (gameManager);
		if (tOwnershipPanel == GUI.NO_PANEL) {
			tEmptyOpenMarket = new JLabel ("NO CERTIFICATES IN OPEN MARKET");
			tPortfolioJPanel.add (tEmptyOpenMarket);
		} else {
			tPortfolioJPanel.add (tOwnershipPanel);
		}
		
		return tPortfolioJPanel;
	}

	@Override
	public int updateToNextPlayer (List<Player> aPlayers) {
		Player tActingPresident;
		Player tFirstPresident;
		PlayerManager tPlayerManager;
		int tNextPlayerIndex;

		
		tPlayerManager = gameManager.getPlayerManager ();
		tNextPlayerIndex = tPlayerManager.getNextPlayerIndex (currentPlayerIndex);
		tActingPresident = tPlayerManager.getPlayer (tNextPlayerIndex);
		tFirstPresident = findActingPresident ();
		if (tActingPresident == tFirstPresident) {
			allRepaymentsDone ();
		} else {
			setCurrentPlayerIndex (tNextPlayerIndex);
			updatePlayers (aPlayers, tActingPresident);
		}
		
		return tNextPlayerIndex;
	}
	
	public Player getCurrentPlayer () {
		Player tCurrentPlayer;
		PlayerManager tPlayerManager;
		
		tPlayerManager = gameManager.getPlayerManager ();
		tCurrentPlayer = tPlayerManager.getPlayer (currentPlayerIndex);

		return tCurrentPlayer;
	}
	
	public void allRepaymentsDone () {
		if (haveSharesToFold ()) {
			System.out.println ("There are " + getShareFoldCount () + " Shares to fold into CGR");
		} else {
			System.out.println ("No Shares into CGR");
		}
		allLoanRepaymentFrame.hideFrame ();
	}
	
	@Override
	public void rebuildSpecialPanel (int aCurrentPlayerIndex) {
		List<Player> tPlayers;
		PlayerManager tPlayerManager;
		Player tActingPlayer;
		
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		tActingPlayer = tPlayers.get (aCurrentPlayerIndex);
		updatePlayers (tPlayers, tActingPlayer);
	}
	
	public void updatePlayers (List<Player> aPlayers, Player aActingPresident) {
		PlayerLoanRepaymentJPanel tPlayerLoanRepaymentPanel;
		
		currentPlayerDone = false;
		allLoanRepaymentJPanel.removeAll ();
		for (Player tPlayer : aPlayers) {

			tPlayerLoanRepaymentPanel = new PlayerLoanRepaymentJPanel (gameManager, this, tPlayer, aActingPresident);
			allLoanRepaymentJPanel.add (tPlayerLoanRepaymentPanel);
			allLoanRepaymentJPanel.add (Box.createVerticalStrut (10));
		}
		bottomJPanel = buildBottomJPanel ();
		allLoanRepaymentJPanel.add (bottomJPanel);
		
		allLoanRepaymentJPanel.repaint ();
		allLoanRepaymentJPanel.revalidate ();
	}

	public void setNotificationText (String aNotificationText) {
		notificationText = aNotificationText;
	}
	
	public JPanel buildBottomJPanel () {
		JPanel tBottomJPanel;
		JPanel tOpenMarketJPanel;
		JPanel tNotificationJPanel;
		JLabel tNotificationLabel;
		Color tColor;
		
		tColor = gameManager.getAlertColor ();
		tNotificationJPanel = new JPanel ();
		tNotificationLabel = new JLabel (notificationText);
		tNotificationLabel.setFont (new Font ("Courier New", Font.BOLD, 16));
		
		tNotificationJPanel.add (tNotificationLabel);
		tNotificationJPanel.setBackground (tColor);
		
		tBottomJPanel = new JPanel ();
		tBottomJPanel.setLayout (new BoxLayout (tBottomJPanel, BoxLayout.X_AXIS));
		tBottomJPanel.add (Box.createHorizontalGlue ());
		tBottomJPanel.add (tNotificationJPanel);
		tBottomJPanel.add (Box.createHorizontalStrut (20));
		
		tOpenMarketJPanel = buildOpenMarketPortfolio ();
		tBottomJPanel.add (tOpenMarketJPanel);
		
		tBottomJPanel.add (Box.createHorizontalGlue ());

		return tBottomJPanel;
	}
	
	public JPanel buildOpenMarketPortfolio () {
		JPanel tOpenMarketJPanel;
		BankPool tOpenMarket;
		Portfolio tOpenMarketPortfolio;
		
		tOpenMarketJPanel = new JPanel ();
		tOpenMarket = gameManager.getBankPool ();
		
		tOpenMarketPortfolio = tOpenMarket.getPortfolio ();
		tOpenMarketJPanel = buildPortfolioJPanel (tOpenMarketPortfolio);

		return tOpenMarketJPanel;
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
	}
	
	@Override
	public void hideSpecialPanel () {
		allLoanRepaymentFrame.hideFrame ();
	}
}
