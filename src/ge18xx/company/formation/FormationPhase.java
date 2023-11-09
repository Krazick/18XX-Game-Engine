package ge18xx.company.formation;

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
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import ge18xx.bank.BankPool;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.phase.PhaseInfo;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.round.action.ChangeFormationPhaseStateAction;
import ge18xx.round.action.ChangeStateAction;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.GUI;

public class FormationPhase extends TriggerClass implements ActionListener {
	public static final FormationPhase NO_FORMATION_PHASE = null;
	public static final String DONE = "Done";
	public static final String NOT_ACTING_PRESIDENT = "You are not the Acting President";
	public static final String TIME_TO_REPAY = "Time to repay company outstanding Loans";
	public static final String NOT_CURRENT_PLAYER = "You are not the current President";
	public static final String NO_OUTSTANDING_LOANS = "There are no outstanding Loans to repay. %s will not form.";
	public static final String CONTINUE = "Continue";
	public static final String FOLD = "Fold";
	public static final String TOKEN_EXCHANGE = "TokenExchange";
	public static final String ASSET_COLLECTION = "AssetCollection";
	public static final String STOCK_VALUE_CALCULATION = "StockValueCalculation";
	public static final int SHARES_NEEDED_FOR_2ND_ISSUE = 21;
	
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
	JPanel notificationJPanel;
	JTextArea notiricationArea;

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
	
	public void buildNotificationJPanel () {
		Color tColor;
		
		if (notificationJPanel == null) {
			notificationJPanel = new JPanel ();
			notiricationArea = new JTextArea (5, 80);
			notiricationArea.setFont (new Font ("Courier New", Font.BOLD, 16));
			notiricationArea.setLineWrap (true);
			notiricationArea.setWrapStyleWord (true);
			notificationJPanel.add (notiricationArea);
			tColor = gameManager.getAlertColor ();
			notificationJPanel.setBackground (tColor);

		}
	}

	public FormationPhase (GameManager aGameManager, BuyTrainAction aBuyTrainAction) {
		this (aGameManager);
		
		Player tActingPlayer;
		
		if (aBuyTrainAction != Action.NO_ACTION) {
			tActingPlayer = findActingPresident ();
			aBuyTrainAction.addShowFormationPanelEffect (tActingPlayer);
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

	public void updateDoneButton () {
		
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
		List<Player> tPlayers;
		PlayerManager tPlayerManager;
		
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		formationFrame = new XMLFrame (aFrameName, gameManager);
		formationFrame.setSize (800, 600);
		
		formationJPanel = new JPanel ();
		tMargin = new EmptyBorder (10,10,10,10);
		formationJPanel.setBorder (tMargin);
		
		formationJPanel.setLayout (new BoxLayout (formationJPanel, BoxLayout.Y_AXIS));
		

		for (Player tPlayer : tPlayers) {
			tPlayer.setPrimaryActionState (ActorI.ActionStates.CompanyFormation);
		}

		setupPlayers (tPlayerManager, tPlayers);
		formationFrame.buildScrollPane (formationJPanel);

		tRoundFrameOffset = gameManager.getOffsetRoundFrame ();
		formationFrame.setLocation (tRoundFrameOffset);
		gameManager.addNewFrame (formationFrame);
		
		tWidth = 1140;
		tHeight = panelHeight ();
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
	
	public boolean haveSharesToFold () {
		return shareFoldCount > 0;
	}
	
	public boolean hasAssetsToCollect () {
		boolean tHasAssetsToCollect;
		
		tHasAssetsToCollect = true;
		
		return tHasAssetsToCollect;
	}
	
	public boolean hasStockValueToCalculate () {
		boolean tHasStockValueToCalculate;
		
		tHasStockValueToCalculate = true;
		
		return tHasStockValueToCalculate;
	}
	
	public boolean hasTokensToExchange () {
		boolean tHasTokensToExchange;
		int tCompanyTokensToExchange;
		int tCompanyIndex;
		int tCompanyCount;
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;
		
		tHasTokensToExchange = false;
		tCompanyTokensToExchange = 0;
		if (formationState == ActorI.ActionStates.ShareExchange) {
			tShareCompanies = gameManager.getShareCompanies ();
			tCompanyCount = tShareCompanies.getRowCount ();
			for (tCompanyIndex = 0; tCompanyIndex < tCompanyCount; tCompanyIndex++) {
				tShareCompany = (ShareCompany) tShareCompanies.getCorporation (tCompanyIndex);
				if (tShareCompany.willFold ()) {
					if (tShareCompany.sharesFolded ()) {
						tCompanyTokensToExchange++;
					}
				}
			}
		}
		
		if (tCompanyTokensToExchange > 0) {
			tHasTokensToExchange = true;
		}
		
		return tHasTokensToExchange;
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
		
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		setupPlayers (tPlayerManager, tPlayers);
	}
	
	public void setupPlayers (PlayerManager aPlayerManager, List<Player> aPlayers) {
		int tCurrentPlayerIndex;
		
		findActingPresident ();
		tCurrentPlayerIndex = aPlayerManager.getPlayerIndex (actingPresident);
		setCurrentPlayerIndex (tCurrentPlayerIndex);
		updatePlayers (aPlayers, actingPresident);
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
		BankPool tBankPool;
		
		tPortfolioJPanel = new JPanel ();
		tBankPool = gameManager.getBankPool ();
		tPortfolioJPanel.setLayout (new BoxLayout (tPortfolioJPanel, BoxLayout.Y_AXIS));
		tTitle = new JLabel (tBankPool.getName ());
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
		Player tNextPlayer;
		Player tFirstPresident;
		Player tCurrentPlayer;
		PlayerManager tPlayerManager;
		int tNextPlayerIndex;
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		ChangeStateAction tChangeStateAction;
		
		tPlayerManager = gameManager.getPlayerManager ();
		
		tCurrentPlayer = aPlayers.get (currentPlayerIndex);
		tOldState = tCurrentPlayer.getPrimaryActionState ();
		tCurrentPlayer.setPrimaryActionState (formationState);
		tNewState = tCurrentPlayer.getPrimaryActionState ();;
		tChangeStateAction = new ChangeStateAction (ActorI.ActionStates.FormationRound, "1", tCurrentPlayer);
		tChangeStateAction.addStateChangeEffect (tCurrentPlayer, tOldState, tNewState);
		rebuildFormationPanel (currentPlayerIndex);

		tNextPlayerIndex = tPlayerManager.getNextPlayerIndex (currentPlayerIndex);
		tNextPlayer = tPlayerManager.getPlayer (tNextPlayerIndex);
		tFirstPresident = findActingPresident ();
		
		tChangeStateAction.addUpdateToNextPlayerEffect (tCurrentPlayer, tCurrentPlayer, tNextPlayer);
		gameManager.addAction (tChangeStateAction);
		
		setCurrentPlayerIndex (tNextPlayerIndex);
		if (tNextPlayer == tFirstPresident) {
			allPlayersHandled ();
		} else {
			updatePlayers (aPlayers, tNextPlayer);
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
				buildContinueButton (FOLD);
			} else {
				tNotification = String.format (NO_OUTSTANDING_LOANS, tFormingAbbrev);
				setNotificationText (tNotification);
				buildContinueButton (CONTINUE);
			}
		} else if (formationState == ActorI.ActionStates.ShareExchange) {
			setAllPlayerSharesHandled (true);
			if (hasTokensToExchange ()) {
				buildContinueButton (TOKEN_EXCHANGE);
			}
		} else if (formationState == ActorI.ActionStates.TokenExchange) {
			System.out.println ("All Folded Companies have had Tokens Exchanged");
			if (hasAssetsToCollect ()) {
				System.out.println ("Ready to do " + ASSET_COLLECTION);
				buildContinueButton (ASSET_COLLECTION);
			}
		} else if (formationState == ActorI.ActionStates.StockValueCalculation) {
			System.out.println ("All Folded Companies have had Assets Collected");
			if (hasStockValueToCalculate ()) {
				System.out.println ("Ready to do " + STOCK_VALUE_CALCULATION);
				buildContinueButton (STOCK_VALUE_CALCULATION);
			}
		}

		rebuildFormationPanel (currentPlayerIndex);
	}
	
	public void allPlayerSharesExchanged () {
		if (formationState == ActorI.ActionStates.ShareExchange) {
			setAllPlayerSharesHandled (true);
			if (hasTokensToExchange ()) {
				buildContinueButton (TOKEN_EXCHANGE);
			}
		}
		rebuildFormationPanel (currentPlayerIndex);
	}
	
	public void rebuildFormationPanel () {
		rebuildFormationPanel (getCurrentPlayerIndex ());
	}
	
	@Override
	public void rebuildFormationPanel (int aCurrentPlayerIndex) {
		List<Player> tPlayers;
		PlayerManager tPlayerManager;
		Player tActingPlayer;
		
		showFormationPanel ();
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
		buildNotificationJPanel ();
		buildBottomJPanel ();
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
		tClassName = "ge18xx.company.formation." + formationState.toNoSpaceString ();
		try {
			tPhaseToLoad = Class.forName (tClassName);
			tPhaseConstructor = tPhaseToLoad.getConstructor (gameManager.getClass (), this.getClass (), 
						aPlayer.getClass (), aPlayer.getClass ());
			tPlayerFormationPhase = (PlayerFormationPhase) tPhaseConstructor.newInstance (gameManager, this, aPlayer,
					aActingPresident);
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
	
	public String getNotificationText () {
		return notificationText;
	}
	
	public void buildBottomJPanel () {
		JPanel tOpenMarketJPanel;
		
		notiricationArea.setText (notificationText);
		
		if (bottomJPanel == null) {
			bottomJPanel = new JPanel ();
	
			bottomJPanel.setLayout (new BoxLayout (bottomJPanel, BoxLayout.X_AXIS));
			bottomJPanel.add (Box.createHorizontalGlue ());
			bottomJPanel.add (notificationJPanel);
			bottomJPanel.add (Box.createHorizontalStrut (20));
			
			tOpenMarketJPanel = buildOpenMarketPortfolio ();
			bottomJPanel.add (tOpenMarketJPanel);
		
			bottomJPanel.add (Box.createHorizontalGlue ());

		}
		
		if (continueButton != GUI.NO_BUTTON) {
			bottomJPanel.add (Box.createHorizontalStrut (10));
			bottomJPanel.add (continueButton);
		}
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
			hideFormationPanel ();
		} else if (tActionCommand.equals (TOKEN_EXCHANGE)) {
			handleTokenExchange ();
		} else if (tActionCommand.equals (ASSET_COLLECTION)) {
			handleTokenExchange ();
		} else if (tActionCommand.equals (STOCK_VALUE_CALCULATION)) {
			handleTokenExchange ();
		}
	}
	
	public void handleFormationStateChange (ActorI.ActionStates aNewFormationState) {
		ChangeFormationPhaseStateAction tChangeFormationPhaseStateAction;
		String tOperatingRoundID;
		ActorI.ActionStates tOldFormationState;
		ActorI.ActionStates tNewFormationState;
		
		System.out.println ("Formation Phase - " + aNewFormationState.toString ());
		tOldFormationState = getFormationState ();
		setFormationState (aNewFormationState);
		setupPlayers ();
		tOperatingRoundID = gameManager.getOperatingRoundID ();
		tNewFormationState = getFormationState ();

		tChangeFormationPhaseStateAction = new ChangeFormationPhaseStateAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, actingPresident);
		tChangeFormationPhaseStateAction.addSetFormationStateEffect (actingPresident, tOldFormationState, tNewFormationState);
		gameManager.addAction (tChangeFormationPhaseStateAction);

	}

	public void handleTokenExchange () {
		handleFormationStateChange (ActorI.ActionStates.TokenExchange);
	}
	
	public void handleFoldIntoFormingCompany () {
		handleFormationStateChange (ActorI.ActionStates.ShareExchange);
	}

	public void handleAssetCollection () {
		handleFormationStateChange (ActorI.ActionStates.AssetCollection);
	}
	
	public void handleStockValueCalculation () {
		handleFormationStateChange (ActorI.ActionStates.StockValueCalculation);
	}
	
	public int getSharesReceived (int aSharesExchanged) {
		int tSharesReceived;
		
		tSharesReceived = aSharesExchanged/2;
		
		return tSharesReceived;
	}
	
	public int getPercentageForExchange () {
		int tPercentage;
		
		if (shareFoldCount > SHARES_NEEDED_FOR_2ND_ISSUE) {
			tPercentage = PhaseInfo.STANDARD_SHARE_SIZE/2;
		} else {
			tPercentage = PhaseInfo.STANDARD_SHARE_SIZE;
		}
		
		return tPercentage;
	}
	
	public int getShareFoldCount () {
		return shareFoldCount;
	}

	public String buildFoldNotification (ShareCompany aFoldingCompany, int aShareFoldCount) {
		String tNotification;
		String tFormingCompanyAbbrev;
		String tFoldingCompanyAbbrev;
		String tTotalSharesFolded;
		String tPresidentName;
		int tNewShareCount;
		int tSharePercentage;
		
		tFormingCompanyAbbrev = getFormingCompanyAbbrev ();
		tFoldingCompanyAbbrev = aFoldingCompany.getAbbrev ();
		tPresidentName = aFoldingCompany.getPresidentName ();
		tNotification = tFoldingCompanyAbbrev + " will fold " + aShareFoldCount + 
				" Shares into the " + tFormingCompanyAbbrev + ".";
		
		tNewShareCount = getSharesReceived (aShareFoldCount);
		
		tTotalSharesFolded = " A total of " + shareFoldCount + " Shares will be folded into " + tFormingCompanyAbbrev + ".";
		
		tSharePercentage = getPercentageForExchange ();
		tNotification += " " + tFormingCompanyAbbrev + " will issue " + tNewShareCount + " shares at " + tSharePercentage + 
				"% per Share to " + tPresidentName + " of the First ";
		if (tSharePercentage != PhaseInfo.STANDARD_SHARE_SIZE) {
			tNotification += "and Second Issues.";
		} else {
			tNotification += "Issue.";
		}
		tNotification += tTotalSharesFolded;
		
		return tNotification;
	}
	
	@Override
	public void hideFormationPanel () {
		formationFrame.hideFrame ();
	}
	
	@Override
	public void showFormationPanel () {
		formationFrame.showFrame ();
	}
	
	public void refreshPanel () {
		formationFrame.repaint ();
	}
}
