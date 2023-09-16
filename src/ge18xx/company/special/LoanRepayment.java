package ge18xx.company.special;

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
	boolean currentPlayerDone;
	JPanel allLoanRepaymentJPanel;
	
	public LoanRepayment (GameManager aGameManager) {
		String tFullFrameTitle;
		
		gameManager = aGameManager;
		tFullFrameTitle = gameManager.createFrameTitle (FRAME_TITLE);
		gameManager.setTriggerClass (this);
		buildAllPlayers (tFullFrameTitle);
	}
	
	public LoanRepayment (GameManager aGameManager, BuyTrainAction aBuyTrainAction) {
		String tFullFrameTitle;
		Player tActingPlayer;
		
		gameManager = aGameManager;
		tFullFrameTitle = gameManager.createFrameTitle (FRAME_TITLE);
		gameManager.setTriggerClass (this);
		buildAllPlayers (tFullFrameTitle);
		System.out.println ("Calling LoanRepayment Constructor with aBuyTrainAction");
		if (aBuyTrainAction != Action.NO_ACTION) {
			tActingPlayer = findActingPresident ();
			aBuyTrainAction.addShowSpecialPanelEffect (tActingPlayer, tActingPlayer);
			// Add a SHOW FRAME Effect for the Special Panel
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
		addSpecialPanelRefresh (tActingPresident);
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
			addSpecialPanelRefresh (tActingPresident);
		}
		
		return tFoundNextPlayer;
	}
	
	public Player getCurrentPlayer () {
		Player tCurrentPlayer;
		PlayerManager tPlayerManager;
		
		tPlayerManager = gameManager.getPlayerManager ();
		tCurrentPlayer = tPlayerManager.getPlayer (currentPlayerIndex);

		return tCurrentPlayer;
	}
	
	public void allRepaymentsDone () {
		System.out.println ("All Players processed");
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
		JPanel tOpenMarketJPanel;
		
		currentPlayerDone = false;
		allLoanRepaymentJPanel.removeAll ();
		for (Player tPlayer : aPlayers) {

			tPlayerLoanRepaymentPanel = new PlayerLoanRepaymentJPanel (gameManager, this, tPlayer, aActingPresident);
			allLoanRepaymentJPanel.add (tPlayerLoanRepaymentPanel);
			allLoanRepaymentJPanel.add (Box.createVerticalStrut (10));
		}
		tOpenMarketJPanel = buildOpenMarketPortfolio ();
		allLoanRepaymentJPanel.add (tOpenMarketJPanel);
		
		allLoanRepaymentJPanel.repaint ();
		allLoanRepaymentJPanel.revalidate ();
	}

	public void addSpecialPanelRefresh (Player aActingPresident) {
//		OperatingRound tOperatingRound;
//		RoundManager tRoundManager;
//		SpecialPanelAction tSpecialPanelAction;
//		BankPool tBankPool;
//		
//		tRoundManager = gameManager.getRoundManager ();
//		tOperatingRound = tRoundManager.getOperatingRound ();
//		tSpecialPanelAction = new SpecialPanelAction (tOperatingRound.getRoundType (), tOperatingRound.getID (), aActingPresident);
//		tSpecialPanelAction.setChainToPrevious (true);
//		tBankPool = gameManager.getBankPool ();
//		tSpecialPanelAction.addSpecialPanelEffect (aActingPresident, tBankPool);
//		gameManager.addAction (tSpecialPanelAction);
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
	
	@Override
	public void hideSpecialPanel () {
		allLoanRepaymentFrame.hideFrame ();
	}
}
