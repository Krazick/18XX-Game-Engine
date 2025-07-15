package ge18xx.company.formation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;

import ge18xx.bank.BankPool;
import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.GenericActor;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.FormationRoundAction;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;

public class FormCompany extends TriggerClass {
	public static final ElementName EN_FORM_COMPANY = new ElementName ("FormCompany");
	public static final AttributeName AN_CLASS = new AttributeName ("class");
	public static final AttributeName AN_CURRENT_PLAYER_INDEX = new AttributeName ("currentPlayerIndex");
	public static final AttributeName AN_CURRENT_PLAYER_DONE = new AttributeName ("currentPlayerDone");
	public static final AttributeName AN_FORMING_PRESIDENT_ASSIGNED = new AttributeName ("formingPresidentAssigned");
	public static final AttributeName AN_TRIGGERING_COMPANY = new AttributeName ("triggeringCompany");
	public static final FormCompany NO_FORM_COMPANY = null;
	public static final AttributeName AN_FORMATION_STATE = new AttributeName ("formationState");
	public static final AttributeName AN_ACTING_PRESIDENT = new AttributeName ("actingPresident");
	public static final String NOT_ACTING_PRESIDENT = "You are not the Acting President";
	GameManager gameManager;
	int currentPlayerIndex;
	boolean currentPlayerDone;
	boolean formingPresidentAssigned;
	XMLFrame formationFrame;
	Corporation operatingCompany;
	Corporation triggeringCompany;
	ShareCompany formingShareCompany;
	protected ActionStates formationState;
	protected Player actingPresident;
	protected JPanel formationJPanel;
	
	public FormCompany () {
		
	}
	
	public FormCompany (GameManager aGameManager) {
		gameManager = aGameManager;
		setFormingShareCompany ();
		setActingPresident (Player.NO_PLAYER);
	}
	
	public FormCompany (XMLNode aXMLNode, GameManager aGameManager) {
		this (aGameManager);
		parseXML (aXMLNode);
	}

	public void parseXML (XMLNode aXMLNode) {
		String tTriggeringCompanyAbbrev;
		Corporation tTriggeringCompany;
		int tCurrentPlayerIndex;
		String tState;
		boolean tCurrentPlayerDone;
		boolean tFormingPresidentAssigned;
		ActorI.ActionStates tFormationState;
		GenericActor tGenericActor;
		Player tPlayer;
		String tPlayerName;
 		
		tCurrentPlayerIndex = aXMLNode.getThisIntAttribute (AN_CURRENT_PLAYER_INDEX);
		tCurrentPlayerDone = aXMLNode.getThisBooleanAttribute (AN_CURRENT_PLAYER_DONE);
		tFormingPresidentAssigned = aXMLNode.getThisBooleanAttribute (AN_FORMING_PRESIDENT_ASSIGNED);
		tState = aXMLNode.getThisAttribute (AN_FORMATION_STATE);
  		tGenericActor = new GenericActor ();
		tFormationState = tGenericActor.getPlayerFormationState (tState);

		setCurrentPlayerIndex (tCurrentPlayerIndex);
		setCurrentPlayerDone (tCurrentPlayerDone);
		setFormingPresidentAssigned (tFormingPresidentAssigned);
		setFormationState (tFormationState);

		tTriggeringCompanyAbbrev = aXMLNode.getThisAttribute (AN_TRIGGERING_COMPANY);
		tTriggeringCompany = gameManager.getShareCompany (tTriggeringCompanyAbbrev);
		setTriggeringCompany (tTriggeringCompany);
		
		tPlayerName = aXMLNode.getThisAttribute (AN_ACTING_PRESIDENT);
		tPlayer = (Player) gameManager.getActor (tPlayerName);
		if (tPlayer != Player.NO_PLAYER) {
			setActingPresident (tPlayer);
		}
	}
	
	@Override
	public XMLElement addElements (XMLDocument aXMLDocument, ElementName aElementName) {
		XMLElement tXMLElement;
		String tTriggeringAbbrev;
		
		tXMLElement = aXMLDocument.createElement (aElementName);
		
		tXMLElement.setAttribute (AN_CLASS, this.getClass ().getName ());
		tXMLElement.setAttribute (AN_FORMATION_STATE, formationState.toString ());
		tXMLElement.setAttribute (AN_CURRENT_PLAYER_INDEX, currentPlayerIndex);
		tXMLElement.setAttribute (AN_CURRENT_PLAYER_DONE, currentPlayerDone);
		tXMLElement.setAttribute (AN_FORMING_PRESIDENT_ASSIGNED, formingPresidentAssigned);
		if (triggeringCompany == ShareCompany.NO_SHARE_COMPANY) {
			tTriggeringAbbrev = GUI.EMPTY_STRING;
		} else {
			tTriggeringAbbrev = triggeringCompany.getAbbrev ();
		}
		tXMLElement.setAttribute (AN_TRIGGERING_COMPANY, tTriggeringAbbrev);

		return tXMLElement;
	}
	
	public static FormCompany getConstructor (GameManager aGameManager, XMLNode aEffectNode, String aClassName) 
				throws ClassNotFoundException, InstantiationException, IllegalAccessException,
						InvocationTargetException {
		FormCompany tFormCompany;
		Class<?> tFormCompanyToLoad;
		Constructor<?> tFormCompanyConstructor;

		tFormCompanyToLoad = Class.forName (aClassName);
		tFormCompany = FormCompany.NO_FORM_COMPANY;
		try {
			tFormCompanyConstructor = tFormCompanyToLoad.getConstructor (aEffectNode.getClass (),
					aGameManager.getClass ());
			tFormCompany = (FormCompany) tFormCompanyConstructor.newInstance (aEffectNode, aGameManager);
		} catch (NoSuchMethodException tNoSuchMethodException) {
			System.err.println ("Caught Exception with message ");
			System.err.println ("Class name " + aClassName);
			tNoSuchMethodException.printStackTrace ();
		}

		return tFormCompany;
	}

	@Override
	public void setCurrentPlayerIndex (int aCurrentPlayerIndex) {
		currentPlayerIndex = aCurrentPlayerIndex;
	}

	public int getCurrentPlayerIndex () {
		return currentPlayerIndex;
	}

	public Player getCurrentPlayer () {
		Player tCurrentPlayer;
		PlayerManager tPlayerManager;
		
		tPlayerManager = gameManager.getPlayerManager ();
		tCurrentPlayer = tPlayerManager.getPlayer (currentPlayerIndex);
	
		return tCurrentPlayer;
	}
	
	@Override
	public void setTriggeringCompany (Corporation aTriggeringCompany) {
		triggeringCompany = aTriggeringCompany;
	}
	
	public Corporation getTriggeringCompany (Corporation aTriggeringCompany) {
		return triggeringCompany;
	}

	public void setCurrentPlayerDone (Boolean aCurrentPlayerDone) {
		currentPlayerDone = aCurrentPlayerDone;
	}

	public boolean getCurrentPlayerDone () {
		return currentPlayerDone;
	}
	
	public void setFormingPresidentAssigned (boolean aformingPresidentAssigned) {
		formingPresidentAssigned = aformingPresidentAssigned;
	}
	
	public boolean getFormingPresidentAssigned () {
		return formingPresidentAssigned;
	}
	
	@Override
	public void triggeringHandleDone () {
		if (triggeringCompany != ShareCompany.NO_SHARE_COMPANY) {
			triggeringCompany.corporationListDoneAction ();
		} else {
			System.err.println ("Trying to Trigger Handle Done, but don't have a Triggering Share Company set.");
		}
	}

	@Override
	public void hideFormationPanel () {
		formationFrame.hideFrame ();
	}

	@Override
	public void showFormationFrame () {
		formationFrame.showFrame ();
	}

	public void refreshPanel () {
		formationFrame.repaint ();
	}

	@Override
	public XMLFrame getFormationFrame () {
		return formationFrame;
	}

	public boolean isFormationFrameVisible () {
		return formationFrame.isVisible ();
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

	public ActorI.ActionStates getFormationState () {
		return formationState;
	}

	public void setFrameTitle (String aFrameTitle) {
		formationFrame.setTitle (aFrameTitle);
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

	public void setFormationState (FormationRoundAction aFormationRoundAction, 
					ActorI.ActionStates aNewFormationState) {
		ActorI.ActionStates tOldFormationState;
		ActorI.ActionStates tNewFormationState;
		ActorI tPrimaryActor;
		
		tPrimaryActor = aFormationRoundAction.getActor ();
		
		tOldFormationState = getFormationState ();
		setFormationState (aNewFormationState);
		tNewFormationState = getFormationState ();
		
		aFormationRoundAction.addSetFormationStateEffect (tPrimaryActor, tOldFormationState, tNewFormationState);
	}

	int getPlayerCount () {
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
	
	public void updatePlayers (List<Player> aPlayers, Player aActingPresident) {
		PlayerFormationPanel tPlayerJPanel;
		
		currentPlayerDone = false;
		formationJPanel.removeAll ();
		for (Player tPlayer : aPlayers) {
			tPlayerJPanel = buildPlayerPanel (tPlayer, aActingPresident);
			formationJPanel.add (tPlayerJPanel);
			formationJPanel.add (Box.createVerticalStrut (10));
		}
//		buildNotificationJPanel ();
//		buildBottomJPanel ();
//		formationJPanel.add (bottomJPanel);
//		formationJPanel.repaint ();
	}

	int panelHeight () {
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

	public Player getFormingPresident () {
		Player tFormingPresident;
		
		tFormingPresident = (Player) formingShareCompany.getPresident ();
	
		return tFormingPresident;
	}

	@Override
	public void setActingPresident (Player aActingPresident) {
		actingPresident = aActingPresident;
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
	
	public PlayerFormationPanel buildPlayerPanel (Player aPlayer, Player aActingPresident) {
		PlayerFormationPanel tPlayerFormationPanel;
		String tClassName;
		Class<?> tFormCompanyClass;
		Class<?> tPlayerClass;
		Class<?> tClassToLoad;
		Constructor<?> tClassConstructor;

		tPlayerFormationPanel = PlayerFormationPanel.NO_PLAYER_FORMATION_PANEL;
		tClassName = "ge18xx.company.formation." + formationState.toNoSpaceString ();
		System.out.println ("Find Constructor for " + tClassName);
		try {
			// Calls the Constructor for the Next Step in the Formation List to call
			tClassToLoad = Class.forName (tClassName);
			tFormCompanyClass = this.getClass ().getSuperclass ();
			tPlayerClass = aPlayer.getClass ();
			
			tClassConstructor = tClassToLoad.getConstructor (gameManager.getClass (), tFormCompanyClass, 
						tPlayerClass, tPlayerClass);
			tPlayerFormationPanel = (PlayerFormationPanel) tClassConstructor.newInstance (gameManager, this,
					aPlayer, aActingPresident);
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

		return tPlayerFormationPanel;
	}
}
