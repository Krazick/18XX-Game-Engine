package ge18xx.company.special;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import ge18xx.bank.BankPool;
import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.GUI;

public class LoanRepayment extends TriggerClass implements ActionListener {
	public static final String DONE = "Done";
	public static final String NOT_ACTING_PRESIDENT = "You are not the Acting President";
	public static final String TIME_TO_REPAY = "Time to repay company outstanding Loans";
	public static final String NO_OUTSTANDING_LOANS = "There are no outstanding Loans to repay. %s will not form.";
	public static final String FRAME_TITLE = "Loan Repayment";
	public static final String CONTINUE = "Continue";
	public static final String FOLD = "Fold";
	
	XMLFrame allLoanRepaymentFrame;
	GameManager gameManager;
	int currentPlayerIndex;
	int shareFoldCount;
	boolean currentPlayerDone;
	ActionStates formationState;
	JPanel allLoanRepaymentJPanel;
	JPanel bottomJPanel;
	JButton continueButton;
	String notificationText;
	ShareCompany formingShareCompany;
	
	public LoanRepayment (GameManager aGameManager) {
		String tFullFrameTitle;
		gameManager = aGameManager;
		tFullFrameTitle = setFormationState (ActorI.ActionStates.LoanRepayment);
		
		setNotificationText (TIME_TO_REPAY);
		continueButton = GUI.NO_BUTTON;
		gameManager.setTriggerClass (this);
		setFormingShareCompany ();
		
		buildAllPlayers (tFullFrameTitle);
	}

	public LoanRepayment (GameManager aGameManager, BuyTrainAction aBuyTrainAction) {
		this (aGameManager);
		
		Player tActingPlayer;
		
		if (aBuyTrainAction != Action.NO_ACTION) {
			tActingPlayer = findActingPresident ();
			aBuyTrainAction.addShowSpecialPanelEffect (tActingPlayer, tActingPlayer);
		}
	}

	public String setFormationState (ActionStates aFormationState) {
		String tFullFrameTitle;
		
		formationState = aFormationState;
		tFullFrameTitle = gameManager.createFrameTitle (formationState.toString ());
		if (allLoanRepaymentFrame != XMLFrame.NO_XML_FRAME) {
			setFrameTitle (tFullFrameTitle);
		}
		
		return tFullFrameTitle;
	}

	public void setFormingShareCompany () {
		int tFormingCompanyID;
		Corporation tFormingCompany;
		tFormingCompanyID = gameManager.getFormingCompanyId ();
		tFormingCompany = gameManager.getCorporationByID (tFormingCompanyID);
		if (tFormingCompany.isAShareCompany ()) {
			formingShareCompany = (ShareCompany) tFormingCompany;
		}
	}

	public String getFormingCompanyAbbrev () {
		return formingShareCompany.getAbbrev ();
	}
	
	public void buildContinueButton (String aActionCommand) {
		String tToolTip;
		
		tToolTip = GUI.EMPTY_STRING;
		continueButton = buildSpecialButton (CONTINUE, aActionCommand, tToolTip, this);
	}
	
	public JButton buildSpecialButton (String aTitle, String aActionCommand, String aToolTip, ActionListener aActionListener) {
		JButton tSpecialButton;
		boolean tEnabled;
		
		tEnabled = getEnabled (aToolTip);
		tSpecialButton = new JButton (aTitle);
		tSpecialButton.setActionCommand (aActionCommand);
		tSpecialButton.setEnabled (tEnabled);
		tSpecialButton.setToolTipText (aToolTip);
		tSpecialButton.addActionListener (aActionListener);
		
		return tSpecialButton;
	}

	public boolean getEnabled (String aToolTip) {
		boolean tEnabled;
		
		if (GUI.EMPTY_STRING.equals (aToolTip)) {
			tEnabled = true;
		} else {
			tEnabled = false;
		}
		
		return tEnabled;
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

	public void setFrameTitle (String aFrameTitle) {
		allLoanRepaymentFrame.setTitle (aFrameTitle);
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
		String tFormingAbbrev;
		String tNotification;
		
		tFormingAbbrev = formingShareCompany.getAbbrev ();
		if (haveSharesToFold ()) {
			System.out.println ("There are " + getShareFoldCount () + " Shares to fold into " + tFormingAbbrev);
			buildContinueButton (FOLD);
		} else {
			tNotification = String.format (NO_OUTSTANDING_LOANS, tFormingAbbrev);
			setNotificationText (tNotification);
			buildContinueButton (CONTINUE);
			System.out.println ("No Shares are folding into " + tFormingAbbrev);
		}
		rebuildSpecialPanel (currentPlayerIndex);
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
		if (continueButton != GUI.NO_BUTTON) {
			tBottomJPanel.add (Box.createHorizontalStrut (10));
			tBottomJPanel.add (continueButton);
		}
		
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
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		if (tActionCommand.equals (FOLD)) {
			handleFoldIntoFormingCompany ();
		} else if (tActionCommand.equals (CONTINUE)) {
			hideSpecialPanel ();
		}
	}
	
	public void handleFoldIntoFormingCompany () {
		setFormationState (ActorI.ActionStates.ShareExchange);
		setupPlayers ();
	}
	
	public String buildFoldNotification (String aFoldingCompanyAbbrev, int aShareFoldCount) {
		String tNotification;
		String tFormingCompanyAbbrev;
		
		tFormingCompanyAbbrev = getFormingCompanyAbbrev ();

		tNotification = aFoldingCompanyAbbrev + " will fold " + aShareFoldCount + " Shares into the " + tFormingCompanyAbbrev +
				". Total New Share Fold Count is " + shareFoldCount;
		
		return tNotification;
	}
	
	@Override
	public void hideSpecialPanel () {
		allLoanRepaymentFrame.hideFrame ();
	}
	
	@Override
	public void showSpecialPanel () {
		allLoanRepaymentFrame.showFrame ();
	}
}
