package ge18xx.company.special;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.GUI;

public class FormationPhase extends TriggerClass implements ActionListener {
	public static final String DONE = "Done";
	public static final String NOT_ACTING_PRESIDENT = "You are not the Acting President";
	public static final String TIME_TO_REPAY = "Time to repay company outstanding Loans";
	public static final String NOT_CURRENT_PLAYER = "You are not the current President";
	public static final String NO_OUTSTANDING_LOANS = "There are no outstanding Loans to repay. %s will not form.";
	public static final String CONTINUE = "Continue";
	public static final String FOLD = "Fold";
	
	XMLFrame formationFrame;
	GameManager gameManager;
	int currentPlayerIndex;
	int shareFoldCount;

	boolean currentPlayerDone;
	boolean formingPresidentAssigned;
	boolean allPlayerSharesHandled;
	ActionStates formationState;
	JPanel formationJPanel;
	JPanel bottomJPanel;
	JButton continueButton;
	String notificationText;
	ShareCompany formingShareCompany;
	Player actingPresident;
	
	public FormationPhase (GameManager aGameManager) {
		String tFullFrameTitle;
		
		gameManager = aGameManager;
		tFullFrameTitle = setFormationState (ActorI.ActionStates.LoanRepayment);
		
		setNotificationText (TIME_TO_REPAY);
		continueButton = GUI.NO_BUTTON;
		actingPresident = Player.NO_PLAYER;
		gameManager.setTriggerClass (this);
		setFormingShareCompany ();
		setAllPlayerSharesHandled (false);
		buildAllPlayers (tFullFrameTitle);
	}

	public FormationPhase (GameManager aGameManager, BuyTrainAction aBuyTrainAction) {
		this (aGameManager);
		
		Player tActingPlayer;
		
		if (aBuyTrainAction != Action.NO_ACTION) {
			tActingPlayer = findActingPresident ();
			aBuyTrainAction.addShowSpecialPanelEffect (tActingPlayer, tActingPlayer);
			aBuyTrainAction.addSetFormationStateEffect (tActingPlayer, ActorI.ActionStates.NoState, formationState);
		}
	}

	public void setFormingPresidentAssigned (boolean aformingPresidentAssigned) {
		formingPresidentAssigned = aformingPresidentAssigned;
	}
	
	public boolean getFormingPresidentAssigned () {
		return formingPresidentAssigned;
	}

	public void setAllPlayerSharesHandled (boolean aAllPlayerSharesHandled) {
		allPlayerSharesHandled = aAllPlayerSharesHandled;
	}
	
	public boolean getAllPlayerSharesHandled () {
		return allPlayerSharesHandled;
	}

	@Override
	public String setFormationState (ActionStates aFormationState) {
		String tFullFrameTitle;
		
		formationState = aFormationState;
		tFullFrameTitle = gameManager.createFrameTitle (formationState.toString ());
		if (formationFrame != XMLFrame.NO_XML_FRAME) {
			setFrameTitle (tFullFrameTitle);
		}
		
		return tFullFrameTitle;
	}

	public ActorI.ActionStates getFormationState () {
		return formationState;
	}
	
	public void setFormingShareCompany () {
		int tFormingCompanyID;
		Corporation tFormingCompany;
		
		tFormingCompanyID = gameManager.getFormingCompanyId ();
		tFormingCompany = gameManager.getCorporationByID (tFormingCompanyID);
		if (tFormingCompany.isAShareCompany ()) {
			formingShareCompany = (ShareCompany) tFormingCompany;
		}
		setFormingPresidentAssigned (false);
	}

	public String getFormingCompanyAbbrev () {
		return formingShareCompany.getAbbrev ();
	}
	
	public void buildContinueButton (String aActionCommand) {
		String tToolTip;
		
		tToolTip = GUI.EMPTY_STRING;
		continueButton = buildSpecialButton (CONTINUE, aActionCommand, tToolTip, this);
	}

	public void buildAllPlayers (String aFrameName) {
		Border tMargin;
		Point tRoundFrameOffset;
		int tHeight;
		int tWidth;
		
		formationFrame = new XMLFrame (aFrameName, gameManager);
		formationFrame.setSize (800, 600);
		
		formationJPanel = new JPanel ();
		tMargin = new EmptyBorder (10,10,10,10);
		formationJPanel.setBorder (tMargin);
		
		formationJPanel.setLayout (new BoxLayout (formationJPanel, BoxLayout.Y_AXIS));

		setupPlayers ();
		formationFrame.buildScrollPane (formationJPanel);

		tRoundFrameOffset = gameManager.getOffsetRoundFrame ();
		formationFrame.setLocation (tRoundFrameOffset);
		gameManager.addNewFrame (formationFrame);
		
		tWidth = 1140;
		tHeight = panelHeight ();
		System.out.println ("Width " + tWidth + " Height " + tHeight + " Shares Open " + gameManager.getCountOfCanOperate ());
		formationFrame.setSize (tWidth,  tHeight);
		formationFrame.showFrame ();
		
		setShareFoldCount (0);
	}

	private int panelHeight () {
		int tPanelHeight;
		int tPlayerHeight;
		int tPlayerCount;
		int tCompanyCount;
		int tCompanyHeight;
		int tOpenMarketCompanyCount;
		int tOpenMarketHeight;
		BankPool tOpenMarket;
		Portfolio tOpenMarketPortfolio;
		
		tPlayerCount = getPlayerCount ();
		tPlayerHeight = 50 * tPlayerCount;
		
		tCompanyCount = gameManager.getCountOfCanOperate ();
		tCompanyHeight = 85 * tCompanyCount;
		
		tOpenMarket = gameManager.getBankPool ();
		tOpenMarketPortfolio = tOpenMarket.getPortfolio ();
		tOpenMarketCompanyCount = tOpenMarketPortfolio.getUniqueCompanyCount ();
		tOpenMarketHeight = 20 * (tOpenMarketCompanyCount + 1);
		
		tPanelHeight = tPlayerHeight + tCompanyHeight + tOpenMarketHeight + 40;
		
		return tPanelHeight;
	}
	
	public void setFrameTitle (String aFrameTitle) {
		formationFrame.setTitle (aFrameTitle);
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
	
	private int getPlayerCount () {
		PlayerManager tPlayerManager;
		List<Player> tPlayers;
		int tPlayerCount;
		
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		tPlayerCount = tPlayers.size ();
		
		return tPlayerCount;
	}
	
	public void setupPlayers () {
		List<Player> tPlayers;
		PlayerManager tPlayerManager;
		int tCurrentPlayerIndex;
		
		findActingPresident ();
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		tCurrentPlayerIndex = tPlayerManager.getPlayerIndex (actingPresident);
		setCurrentPlayerIndex (tCurrentPlayerIndex);
		updatePlayers (tPlayers, actingPresident);
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
		tTitle = new JLabel (BankPool.NAME + " Portfolio");
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
		setCurrentPlayerIndex (tNextPlayerIndex);
		if (tActingPresident == tFirstPresident) {
			allPlayersHandled ();
		} else {
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
	
	public void allPlayersHandled () {
		String tFormingAbbrev;
		String tNotification;
		
		tFormingAbbrev = formingShareCompany.getAbbrev ();
		if (formationState == ActorI.ActionStates.LoanRepayment) {
			if (haveSharesToFold ()) {
				System.out.println ("There are " + getShareFoldCount () + " Shares to fold into " + tFormingAbbrev);
				buildContinueButton (FOLD);
			} else {
				tNotification = String.format (NO_OUTSTANDING_LOANS, tFormingAbbrev);
				setNotificationText (tNotification);
				buildContinueButton (CONTINUE);
				System.out.println ("No Shares are folding into " + tFormingAbbrev);
			}
		} else if (formationState == ActorI.ActionStates.ShareExchange) {
			System.out.println ("All Players Shares have been Exchanged");
			setAllPlayerSharesHandled (true);
		}
		rebuildSpecialPanel (currentPlayerIndex);
	}
	
	public void rebuildSpecialPanel () {
		rebuildSpecialPanel (getCurrentPlayerIndex ());
	}
	
	@Override
	public void rebuildSpecialPanel (int aCurrentPlayerIndex) {
		List<Player> tPlayers;
		PlayerManager tPlayerManager;
		Player tActingPlayer;
		
		showSpecialPanel ();
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		tActingPlayer = tPlayers.get (aCurrentPlayerIndex);
		updatePlayers (tPlayers, tActingPlayer);
	}
	
	public void updateContinueButton () {
		Player tCurrentPlayer;
		
		if (continueButton != GUI.NO_BUTTON) {
			tCurrentPlayer = getCurrentPlayer ();
			if (tCurrentPlayer == actingPresident) {
				continueButton.setEnabled (true);
				continueButton.setToolTipText (GUI.EMPTY_STRING);
			} else {
				continueButton.setEnabled (false);
				continueButton.setToolTipText (NOT_CURRENT_PLAYER);

			}
		}
	}
	
	public void updatePlayers (List<Player> aPlayers, Player aActingPresident) {
		PlayerFormationPhase tPlayerJPanel;
		
		currentPlayerDone = false;
		formationJPanel.removeAll ();
		for (Player tPlayer : aPlayers) {

			tPlayerJPanel = buildPlayerPanel (tPlayer, aActingPresident);
			formationJPanel.add (tPlayerJPanel);
			formationJPanel.add (Box.createVerticalStrut (10));
		}
		bottomJPanel = buildBottomJPanel ();
		formationJPanel.add (bottomJPanel);
		updateContinueButton ();
		formationJPanel.repaint ();
		formationJPanel.revalidate ();
	}

	public PlayerFormationPhase buildPlayerPanel (Player aPlayer, Player aActingPresident) {
		PlayerFormationPhase tPlayerFormationPhase;
		String tClassName;
		Class<?> tPhaseToLoad;
		Constructor<?> tPhaseConstructor;

		tPlayerFormationPhase = PlayerFormationPhase.NO_PLAYER_FORMATION_PHASE;
		tClassName = "ge18xx.company.special." + formationState.toNoSpaceString ();
		try {
			tPhaseToLoad = Class.forName (tClassName);
			tPhaseConstructor = tPhaseToLoad.getConstructor (gameManager.getClass (), this.getClass (), 
						aPlayer.getClass (), aPlayer.getClass ());
			tPlayerFormationPhase = (PlayerFormationPhase) tPhaseConstructor.newInstance (gameManager, this, aPlayer, aActingPresident);
		} catch (NoSuchMethodException | SecurityException e) {
			System.err.println ("Error trying to get Constructor");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return tPlayerFormationPhase;
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
		PortfolioHolderI tPresident;
		
		if (actingPresident == Player.NO_PLAYER) {
			tActingCorporation = gameManager.getOperatingCompany ();
			tPresident = tActingCorporation.getPresident ();
			tActingPlayer = (Player) tPresident;
			actingPresident = tActingPlayer;
		} else {
			tActingPlayer = actingPresident;
		}
		
		return tActingPlayer;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		if (tActionCommand.equals (FOLD)) {
			handleFoldIntoFormingCompany ();
		} else if (tActionCommand.equals (CONTINUE)) {
			System.out.println ("Formation Phase - Action Performing HIDE Special Panel");
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

		tNotification = aFoldingCompanyAbbrev + " will fold " + aShareFoldCount + 
				" Shares into the " + tFormingCompanyAbbrev +
				". Total New Share Fold Count is " + shareFoldCount;
		
		return tNotification;
	}
	
	@Override
	public void hideSpecialPanel () {
		System.out.println ("Formation Phase - Hiding Special Panel");
		formationFrame.hideFrame ();
	}
	
	@Override
	public void showSpecialPanel () {
		formationFrame.showFrame ();
	}
}
