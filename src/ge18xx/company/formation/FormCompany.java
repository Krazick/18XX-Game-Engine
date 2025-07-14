package ge18xx.company.formation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import ge18xx.bank.BankPool;
import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.player.Portfolio;
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
	GameManager gameManager;
	int currentPlayerIndex;
	boolean currentPlayerDone;
	boolean formingPresidentAssigned;
	XMLFrame formationFrame;
	Corporation operatingCompany;
	Corporation triggeringCompany;
	ShareCompany formingShareCompany;
	protected ActionStates formationState;
	
	public FormCompany () {
		
	}
	
	public FormCompany (GameManager aGameManager) {
		gameManager = aGameManager;
		setFormingShareCompany ();
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

	protected int getPlayerCount () {
		PlayerManager tPlayerManager;
		List<Player> tPlayers;
		int tPlayerCount;
		
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		tPlayerCount = tPlayers.size ();
		
		return tPlayerCount;
	}

	protected int panelHeight () {
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
}
