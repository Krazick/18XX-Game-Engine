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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.game.XMLSaveGameI;
import ge18xx.phase.PhaseInfo;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ActorI.ActionStates;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;
import ge18xx.round.action.BuyTrainAction;
import ge18xx.round.action.ChangeFormationPhaseStateAction;
import ge18xx.round.action.ChangeStateAction;
import ge18xx.round.action.FormationPhaseAction;
import ge18xx.round.action.GenericActor;
import ge18xx.round.action.StartFormationAction;
import geUtilities.GUI;

public class FormationPhase extends TriggerClass implements ActionListener, XMLSaveGameI {
	public static final ElementName EN_FORMATION_PHASE = new ElementName ("FormationPhase");
	public static final AttributeName AN_CURRENT_PLAYER_INDEX = new AttributeName ("currentPlayerIndex");
	public static final AttributeName AN_SHARE_FOLD_COUNT = new AttributeName ("shareFoldCount");
	public static final AttributeName AN_CURRENT_PLAYER_DONE = new AttributeName ("currentPlayerDone");
	public static final AttributeName AN_FORMING_PRESIDENT_ASSIGNED = new AttributeName ("formingPresidentAssigned");
	public static final AttributeName AN_ALL_PLAYER_SHARES_HANDLED = new AttributeName ("allPlayerSharesHandled");
	public static final AttributeName AN_FORMATION_STATE = new AttributeName ("formationState");
	public static final AttributeName AN_TRIGGERING_COMPANY = new AttributeName ("triggeringCompany");
	public static final AttributeName AN_NOTITIFCATION_TEXT = new AttributeName ("notificationText");
	public static final AttributeName AN_ACTING_PRESIDENT = new AttributeName ("actingPresident");
	public static final AttributeName AN_HOME_TOKENS_EXCHANGED = new AttributeName ("homeTokensExchanged");
	public static final AttributeName AN_NON_HOME_TOKENS_EXCHANGED = new AttributeName ("nonHomeTokensExchanged");
	public static final FormationPhase NO_FORMATION_PHASE = null;
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
	boolean homeTokensExchanged;
	boolean nonHomeTokensExchanged;

	ActionStates formationState;
	JPanel formationJPanel;
	JPanel bottomJPanel;
	JPanel openMarketJPanel;
	JPanel ipoJPanel;
	String notificationText;
	JPanel notificationJPanel;
	JTextArea notiricationArea;

	StartFormationAction startFormationAction;
	ShareCompany triggeringShareCompany;
	ShareCompany formingShareCompany;
	Player actingPresident;
	
	public FormationPhase (GameManager aGameManager) {
		String tFullFrameTitle;
		
		gameManager = aGameManager;
		tFullFrameTitle = setFormationState (ActorI.ActionStates.LoanRepayment);
		
		setNotificationText (TIME_TO_REPAY);
		setActingPresident (Player.NO_PLAYER);
		setFormingShareCompany ();
		setAllPlayerSharesHandled (false);
		setHomeTokensExchanged (false);
		setNonHomeTokensExchanged (false);

		buildAllPlayers (tFullFrameTitle);
		gameManager.setTriggerClass (this);
		gameManager.setFormationPhase (this);
	}

	public FormationPhase (GameManager aGameManager, BuyTrainAction aBuyTrainAction) {
		this (aGameManager);
		
		Player tActingPlayer;
		List<Player> tPlayers;
		PlayerManager tPlayerManager;
		ShareCompany tTriggeringShareCompany;

		if (aBuyTrainAction != Action.NO_ACTION) {
			tActingPlayer = findActingPresident ();
			tTriggeringShareCompany = (ShareCompany) gameManager.getOperatingCompany ();

			aBuyTrainAction.addShowFormationPanelEffect (tActingPlayer);
			aBuyTrainAction.addSetFormationStateEffect (tActingPlayer, ActorI.ActionStates.NoState, formationState);
			aBuyTrainAction.addStartFormationEffect (tActingPlayer, formingShareCompany, tTriggeringShareCompany);
			tPlayerManager = gameManager.getPlayerManager ();
			tPlayers = tPlayerManager.getPlayers ();

			updatePlayersState (tPlayers, aBuyTrainAction);
		}
	}

	public FormationPhase (XMLNode aXMLNode, GameManager aGameManager) {
		this (aGameManager);

		int tCurrentPlayerIndex;
		int tShareFoldCount;
		boolean tCurrentPlayerDone;
		boolean tFormingPresidentAssigned;
		boolean tAllPlayerSharesHandled;
		boolean tHomeTokensExchanged;
		boolean tNonHomeTokensExchanged;
		String tState;
		String tNotificationText;
		String tTriggeringCompanyAbbrev;
		Player tPlayer;
		String tPlayerName;
		GenericActor tGenericActor;
		ActorI.ActionStates tFormationState;
		ShareCompany tTriggeringShareCompany;
		
		tCurrentPlayerIndex = aXMLNode.getThisIntAttribute (AN_CURRENT_PLAYER_INDEX);
		tShareFoldCount = aXMLNode.getThisIntAttribute (AN_SHARE_FOLD_COUNT);
		tCurrentPlayerDone = aXMLNode.getThisBooleanAttribute (AN_CURRENT_PLAYER_DONE);
		tFormingPresidentAssigned = aXMLNode.getThisBooleanAttribute (AN_FORMING_PRESIDENT_ASSIGNED);
		tHomeTokensExchanged = aXMLNode.getThisBooleanAttribute (AN_HOME_TOKENS_EXCHANGED);
		tNonHomeTokensExchanged = aXMLNode.getThisBooleanAttribute (AN_NON_HOME_TOKENS_EXCHANGED);
		tAllPlayerSharesHandled = aXMLNode.getThisBooleanAttribute (AN_ALL_PLAYER_SHARES_HANDLED);
		
		tState = aXMLNode.getThisAttribute (AN_FORMATION_STATE);
		tTriggeringCompanyAbbrev = aXMLNode.getThisAttribute (AN_TRIGGERING_COMPANY);
		tTriggeringShareCompany = aGameManager.getShareCompany (tTriggeringCompanyAbbrev);
		setTriggeringShareCompany (tTriggeringShareCompany);
		tGenericActor = new GenericActor ();
		tFormationState = tGenericActor.getPlayerFormationState (tState);
		tNotificationText = aXMLNode.getThisAttribute (AN_NOTITIFCATION_TEXT);
		tPlayerName = aXMLNode.getThisAttribute (AN_ACTING_PRESIDENT);
		
		setCurrentPlayerIndex (tCurrentPlayerIndex);
		setShareFoldCount (tShareFoldCount);
		setCurrentPlayerDone (tCurrentPlayerDone);
		setFormingPresidentAssigned (tFormingPresidentAssigned);
		setAllPlayerSharesHandled (tAllPlayerSharesHandled);
		setHomeTokensExchanged (tHomeTokensExchanged);
		setNonHomeTokensExchanged (tNonHomeTokensExchanged);
		setFormationState (tFormationState);
		setNotificationText (tNotificationText);
		
		tPlayer = (Player) gameManager.getActor (tPlayerName);
		if (tPlayer != Player.NO_PLAYER) {
			setActingPresident (tPlayer);
		}
	}
	
	public XMLElement addElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		String tTriggeringAbbrev;
		
		tXMLElement = aXMLDocument.createElement (EN_FORMATION_PHASE);
		tXMLElement.setAttribute (AN_CURRENT_PLAYER_INDEX, currentPlayerIndex);
		tXMLElement.setAttribute (AN_SHARE_FOLD_COUNT, shareFoldCount);
		tXMLElement.setAttribute (AN_CURRENT_PLAYER_DONE, currentPlayerDone);
		tXMLElement.setAttribute (AN_FORMING_PRESIDENT_ASSIGNED, formingPresidentAssigned);
		tXMLElement.setAttribute (AN_ALL_PLAYER_SHARES_HANDLED, allPlayerSharesHandled);
		tXMLElement.setAttribute (AN_HOME_TOKENS_EXCHANGED, homeTokensExchanged);
		tXMLElement.setAttribute (AN_NON_HOME_TOKENS_EXCHANGED, nonHomeTokensExchanged);
		tXMLElement.setAttribute (AN_FORMATION_STATE, formationState.toString ());
		tXMLElement.setAttribute (AN_NOTITIFCATION_TEXT, notificationText);
		if (triggeringShareCompany == ShareCompany.NO_SHARE_COMPANY) {
			tTriggeringAbbrev = GUI.EMPTY_STRING;
		} else {
			tTriggeringAbbrev = triggeringShareCompany.getAbbrev ();
		}
		tXMLElement.setAttribute (AN_TRIGGERING_COMPANY, tTriggeringAbbrev);
		if (actingPresident != ActorI.NO_ACTOR) {
			tXMLElement.setAttribute (AN_ACTING_PRESIDENT, actingPresident.getName ());
		}

		return tXMLElement;
	}

	public void setTriggeringShareCompany (ShareCompany aTriggeringShareCompany) {
		triggeringShareCompany = aTriggeringShareCompany;
	}
	
	public void setHomeTokensExchanged (boolean aHomeTokenExchanged) {
		homeTokensExchanged = aHomeTokenExchanged;
	}

	public void setNonHomeTokensExchanged (boolean aNonHomeTokenExchanged) {
		nonHomeTokensExchanged = aNonHomeTokenExchanged;
	}
	
	public boolean getHomeTokensExchanged () {
		return homeTokensExchanged;
	}
	
	public boolean getNonHomeTokensExchanged () {
		return nonHomeTokensExchanged;
	}
	
	@Override
	public void triggeringHandleDone () {
		if (triggeringShareCompany != ShareCompany.NO_SHARE_COMPANY) {
			triggeringShareCompany.corporationListDoneAction ();
		} else {
			System.err.println ("Trying to Trigger Handle Done, but don't have a Triggering Share Company set.");
		}
	}
	
	public void buildNotificationJPanel () {
		Color tColor;
		
		if (notificationJPanel == GUI.NO_PANEL) {
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

	public void setFormationState (FormationPhaseAction aFormationPhaseAction, ActorI.ActionStates aNewFormationState) {
		ActorI.ActionStates tOldFormationState;
		ActorI.ActionStates tNewFormationState;
		ActorI tPrimaryActor;
		
		tPrimaryActor = aFormationPhaseAction.getActor ();
		
		tOldFormationState = getFormationState ();
		setFormationState (aNewFormationState);
		tNewFormationState = getFormationState ();
		
		aFormationPhaseAction.addSetFormationStateEffect (tPrimaryActor, tOldFormationState, tNewFormationState);
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

	public ShareCompany getFormingCompany () {
		return formingShareCompany;
	}

	public String getFormingCompanyAbbrev () {
		return formingShareCompany.getAbbrev ();
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

		setupPlayers (tPlayerManager, tPlayers);
		formationFrame.buildScrollPane (formationJPanel);

		tRoundFrameOffset = gameManager.getOffsetRoundFrame ();
		formationFrame.setLocation (tRoundFrameOffset);
		gameManager.addNewFrame (formationFrame);
		
		tWidth = 1140;
		tHeight = panelHeight ();
		formationFrame.setSize (tWidth,  tHeight);
		showFormationFrame ();
		
		setShareFoldCount (0);
	}

	public boolean isFormationFrameVisible () {
		return formationFrame.isVisible ();
	}
	
	public void showFormationFrame () {
		formationFrame.showFrame ();
	}

	public void updatePlayersState (List<Player> tPlayers, BuyTrainAction aBuyTrainAction) {
		GenericActor tGenericActor;
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		
		tGenericActor = new GenericActor ();
		for (Player tPlayer : tPlayers) {
			tOldState = tPlayer.getPrimaryActionState ();
			if (! tGenericActor.isFormationRound (tOldState)) {
				tPlayer.setPrimaryActionState (ActorI.ActionStates.CompanyFormation);
				tNewState = tPlayer.getPrimaryActionState ();
				aBuyTrainAction.addStateChangeEffect (tPlayer, tOldState, tNewState);
			}
		}
	}

	public void addStartFormationAction () {
		gameManager.addAction (startFormationAction);
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
		int tCompanyIndex;
		int tCompanyCount;
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;
		
		tHasTokensToExchange = false;
		if (formationState == ActorI.ActionStates.ShareExchange) {
			tShareCompanies = gameManager.getShareCompanies ();
			tCompanyCount = tShareCompanies.getRowCount ();
			for (tCompanyIndex = 0; tCompanyIndex < tCompanyCount; tCompanyIndex++) {
				tShareCompany = (ShareCompany) tShareCompanies.getCorporation (tCompanyIndex);
				if (tShareCompany.willFold ()) {
					tHasTokensToExchange = true;
				}
			}
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

	public void setActingPresident (Player aActingPresident) {
		actingPresident = aActingPresident;
	}
	
	@Override
	public void setCurrentPlayerIndex (int aCurrentPlayerIndex) {
		currentPlayerIndex = aCurrentPlayerIndex;
	}
	
	public int getCurrentPlayerIndex () {
		return currentPlayerIndex;
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
		
		updateToPlayer (aPlayers, tNextPlayer, tFirstPresident, tNextPlayerIndex);
		
		return tNextPlayerIndex;
	}

	private void updateToPlayer (List<Player> aPlayers, Player aNextPlayer, Player aFirstPresident,
			int aNextPlayerIndex) {
		setCurrentPlayerIndex (aNextPlayerIndex);
		if (aNextPlayer == aFirstPresident) {
			allPlayersHandled ();
		} else {
			updatePlayers (aPlayers, aNextPlayer);
		}
	}
	
	public Player getFormingPresident () {
		Player tFormingPresident;
		
		tFormingPresident = (Player) formingShareCompany.getPresident ();

		return tFormingPresident;
	}
	
	public void updateToFormingPresident (ActorI.ActionStates aFormationState) {
		PlayerManager tPlayerManager;
		Player tCurrentPlayer;
		Player tFormingPresident;
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		ActorI.ActionStates tPrezOldState;
		ActorI.ActionStates tPrezNewState;
		List<Player> tPlayers;
		ChangeStateAction tChangeStateAction;
		int tPresidentIndex;

		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		tFormingPresident = getFormingPresident ();
		tPresidentIndex = tPlayerManager.getPlayerIndex (tFormingPresident);
		setActingPresident (tFormingPresident);

		if (currentPlayerIndex < 0) {
			tCurrentPlayer = tFormingPresident;
			setCurrentPlayerIndex (tPresidentIndex);
		} else {
			tCurrentPlayer = tPlayers.get (currentPlayerIndex);
		}
		tOldState = tCurrentPlayer.getPrimaryActionState ();
		tCurrentPlayer.setPrimaryActionState (formationState);
		tNewState = tCurrentPlayer.getPrimaryActionState ();;
		tChangeStateAction = new ChangeStateAction (ActorI.ActionStates.FormationRound, "3", tCurrentPlayer);
		tChangeStateAction.addStateChangeEffect (tCurrentPlayer, tOldState, tNewState);
		
		if (tFormingPresident != tCurrentPlayer) {
			tPrezOldState = tFormingPresident.getPrimaryActionState ();
			tFormingPresident.setPrimaryActionState (aFormationState);
			tPrezNewState = tFormingPresident.getPrimaryActionState ();
			tChangeStateAction.addStateChangeEffect (tFormingPresident, tPrezOldState, tPrezNewState);
			setCurrentPlayerIndex (tPresidentIndex);
			tChangeStateAction.addUpdateToNextPlayerEffect (tCurrentPlayer, tCurrentPlayer, tFormingPresident);
			gameManager.addAction (tChangeStateAction);
			rebuildFormationPanel (tPresidentIndex);
		}
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
			if (! haveSharesToFold ()) {
				tNotification = String.format (NO_OUTSTANDING_LOANS, tFormingAbbrev);
				setNotificationText (tNotification);
			}
		} else if (formationState == ActorI.ActionStates.ShareExchange) {
			setAllPlayerSharesHandled (true);
		} else if (formationState == ActorI.ActionStates.TokenExchange) {
			if (hasAssetsToCollect ()) {
				System.out.println ("Ready to do " + ASSET_COLLECTION);
			}
		} else if (formationState == ActorI.ActionStates.StockValueCalculation) {
			System.out.println ("All Folded Companies have had Assets Collected");
			if (hasStockValueToCalculate ()) {
				System.out.println ("Ready to do " + STOCK_VALUE_CALCULATION);
			}
		}

		rebuildFormationPanel (currentPlayerIndex);
	}
	
	public void rebuildFormationPanel () {
		int tCurrentPlayerIndex;
		
		tCurrentPlayerIndex = getCurrentPlayerIndex ();
		if (tCurrentPlayerIndex >= 0) {
			rebuildFormationPanel (tCurrentPlayerIndex);
		}
	}
	
	@Override
	public void rebuildFormationPanel (int aCurrentPlayerIndex) {
		List<Player> tPlayers;
		PlayerManager tPlayerManager;
		Player tActingPlayer;
		
		showFormationPanel ();
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		if (aCurrentPlayerIndex >= 0) {
			tActingPlayer = tPlayers.get (aCurrentPlayerIndex);
			updatePlayers (tPlayers, tActingPlayer);
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
		formationJPanel.repaint ();
		formationJPanel.revalidate ();
	}

	public void setCurrentPlayerDone (Boolean aCurrentPlayerDone) {
		currentPlayerDone = aCurrentPlayerDone;
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
		notiricationArea.setText (notificationText);
		
		if (bottomJPanel == null) {
			bottomJPanel = new JPanel ();
			bottomJPanel.setLayout (new BoxLayout (bottomJPanel, BoxLayout.X_AXIS));
			
			bottomJPanel.add (Box.createHorizontalGlue ());
			bottomJPanel.add (notificationJPanel);
			bottomJPanel.add (Box.createHorizontalStrut (20));
		}
		
		buildOpenMarketPortfolio ();
	}
	
	public void buildOpenMarketPortfolio () {
		BankPool tOpenMarket;
		Bank tBank;
		Portfolio tOpenMarketPortfolio;
		Portfolio tIPOPortfolio;
		
		if (openMarketJPanel == GUI.NO_PANEL) {
			openMarketJPanel = new JPanel ();
			bottomJPanel.add (openMarketJPanel);
		}
		tOpenMarket = gameManager.getBankPool ();
		
		tOpenMarketPortfolio = tOpenMarket.getPortfolio ();
		
		if (ipoJPanel == GUI.NO_PANEL) {
			ipoJPanel = new JPanel ();
			bottomJPanel.add (ipoJPanel);
		}
		tBank = gameManager.getBank ();
		tIPOPortfolio = tBank.getPortfolio ();
		buildPortfolioJPanel (tOpenMarketPortfolio, tIPOPortfolio);
		bottomJPanel.add (Box.createHorizontalGlue ());
	}

	public void buildPortfolioJPanel (Portfolio aOpenMarketPortfolio, Portfolio aIPOPortfolio) {
		JPanel tOwnershipPanel;
		JPanel tIPOPanel;
		JLabel tTitle;
		JLabel tEmptyOpenMarket;
		BankPool tBankPool;
		
		tBankPool = gameManager.getBankPool ();
		openMarketJPanel.removeAll ();
		openMarketJPanel.setLayout (new BoxLayout (openMarketJPanel, BoxLayout.Y_AXIS));
		tTitle = new JLabel (tBankPool.getName ());
		openMarketJPanel.add (tTitle);
		tOwnershipPanel = aOpenMarketPortfolio.buildOwnershipPanel (gameManager);
		if (tOwnershipPanel == GUI.NO_PANEL) {
			tEmptyOpenMarket = new JLabel ("NO CERTIFICATES IN OPEN MARKET");
			openMarketJPanel.add (tEmptyOpenMarket);
		} else {
			openMarketJPanel.add (tOwnershipPanel);
		}
		
		ipoJPanel.removeAll ();
		ipoJPanel.setLayout (new BoxLayout (ipoJPanel, BoxLayout.Y_AXIS));
		tTitle = new JLabel ("IPO Holdings");
		tIPOPanel = aIPOPortfolio.buildOwnershipPanel (gameManager);
		ipoJPanel.add (tTitle);
		ipoJPanel.add (tIPOPanel);
	}

	public Player findActingPresident () {
		Corporation tActingCorporation;
		Player tActingPlayer;
		PortfolioHolderI tPresident;
		
		if (actingPresident == Player.NO_PLAYER) {
			tActingCorporation = gameManager.getOperatingCompany ();
			if (tActingCorporation != Corporation.NO_CORPORATION) {
				tPresident = tActingCorporation.getPresident ();
				if (tPresident.isAPlayer ()) {
					tActingPlayer = (Player) tPresident;
					setActingPresident (tActingPlayer);
				} else {
					setActingPresident (Player.NO_PLAYER);
				}
			}
		}
		tActingPlayer = actingPresident;
	
		return tActingPlayer;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
	
		tActionCommand = aEvent.getActionCommand ();
		applyCommand (tActionCommand);
	}
	
	public void applyCommand (String aActionCommand) {
		if (aActionCommand.equals (CONTINUE)) {
			hideFormationPanel ();
		} else if (aActionCommand.equals (FOLD)) {
			handleFoldIntoFormingCompany ();
		} else if (aActionCommand.equals (TOKEN_EXCHANGE)) {
			handleTokenExchange ();
		} else if (aActionCommand.equals (ASSET_COLLECTION)) {
			handleAssetCollection ();
		} else if (aActionCommand.equals (STOCK_VALUE_CALCULATION)) {
			handleStockValueCalculation ();
		}
	}
	
	public void handleFormationStateChange (ActorI.ActionStates aNewFormationState) {
		ChangeFormationPhaseStateAction tChangeFormationPhaseStateAction;
		PlayerManager tPlayerManager;
		Player tFormingPresident;
		String tOperatingRoundID;
		ActorI.ActionStates tNewFormationState;
		
		System.out.println ("Formation Phase - " + aNewFormationState.toString ());
		tOperatingRoundID = gameManager.getOperatingRoundID ();
		if (actingPresident == ActorI.NO_ACTOR) {
			tFormingPresident = getFormingPresident ();
		} else {
			tFormingPresident = actingPresident;
		}
		tChangeFormationPhaseStateAction = new ChangeFormationPhaseStateAction (ActorI.ActionStates.OperatingRound, 
				tOperatingRoundID, tFormingPresident);
		tChangeFormationPhaseStateAction.setChainToPrevious (true);

		setFormationState (tChangeFormationPhaseStateAction, aNewFormationState);

		tNewFormationState = getFormationState ();
		if (tNewFormationState == ActorI.ActionStates.FormationComplete) {
			hideFormationPanel ();
			tChangeFormationPhaseStateAction.addHideFormationPanelEffect (tFormingPresident);
			tPlayerManager = gameManager.getPlayerManager ();
			tPlayerManager.updateCertificateLimit (tChangeFormationPhaseStateAction);
			triggeringHandleDone ();
		} else {
			if ((tNewFormationState == ActorI.ActionStates.TokenExchange) ||
				(tNewFormationState == ActorI.ActionStates.AssetCollection) ||
				(tNewFormationState == ActorI.ActionStates.StockValueCalculation)) {
				updateToFormingPresident (tNewFormationState);
			}
			setupPlayers ();
		}
		gameManager.addAction (tChangeFormationPhaseStateAction);
	}

	public void handleFoldIntoFormingCompany () {
		handleFormationStateChange (ActorI.ActionStates.ShareExchange);
	}

	public void handleTokenExchange () {
		handleFormationStateChange (ActorI.ActionStates.TokenExchange);
	}

	public void handleAssetCollection () {
		handleFormationStateChange (ActorI.ActionStates.AssetCollection);
	}
	
	public void handleStockValueCalculation () {
		handleFormationStateChange (ActorI.ActionStates.StockValueCalculation);
	}
	
	public void handleFormationComplete () {
		handleFormationStateChange (ActorI.ActionStates.FormationComplete);
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
	
	public int getPercentageNotForExchange () {
		int tPercentage;
		
		if (shareFoldCount > SHARES_NEEDED_FOR_2ND_ISSUE) {
			tPercentage = PhaseInfo.STANDARD_SHARE_SIZE;
		} else {
			tPercentage = PhaseInfo.STANDARD_SHARE_SIZE/2;
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
		showFormationFrame ();
	}
	
	public void refreshPanel () {
		formationFrame.repaint ();
	}
}
