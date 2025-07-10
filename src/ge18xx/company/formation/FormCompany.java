package ge18xx.company.formation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class FormCompany extends TriggerClass {
	public static final ElementName EN_FORM_COMPANY = new ElementName ("FormCompany");
	public static final AttributeName AN_CLASS = new AttributeName ("class");
	public static final AttributeName AN_CURRENT_PLAYER_INDEX = new AttributeName ("currentPlayerIndex");
	public static final AttributeName AN_CURRENT_PLAYER_DONE = new AttributeName ("currentPlayerDone");
	public static final AttributeName AN_FORMING_PRESIDENT_ASSIGNED = new AttributeName ("formingPresidentAssigned");
	public static final AttributeName AN_TRIGGERING_COMPANY = new AttributeName ("triggeringCompany");
	public static final FormCompany NO_FORM_COMPANY = null;
	GameManager gameManager;
	int currentPlayerIndex;
	boolean currentPlayerDone;
	boolean formingPresidentAssigned;
	Corporation operatingCompany;
	Corporation triggeringCompany;
	
	public FormCompany () {
		
	}
	
	public FormCompany (GameManager aGameManager) {
		gameManager = aGameManager;
	}
	
	public FormCompany (XMLNode aXMLNode, GameManager aGameManager) {
		this (aGameManager);
		parseXML (aXMLNode);
	}

	public void parseXML (XMLNode aXMLNode) {
		String tTriggeringCompanyAbbrev;
		Corporation tTriggeringCompany;
		int tCurrentPlayerIndex;
		boolean tCurrentPlayerDone;
		boolean tFormingPresidentAssigned;
		
		tCurrentPlayerIndex = aXMLNode.getThisIntAttribute (AN_CURRENT_PLAYER_INDEX);
		tCurrentPlayerDone = aXMLNode.getThisBooleanAttribute (AN_CURRENT_PLAYER_DONE);
		tFormingPresidentAssigned = aXMLNode.getThisBooleanAttribute (AN_FORMING_PRESIDENT_ASSIGNED);

		setCurrentPlayerIndex (tCurrentPlayerIndex);
		setCurrentPlayerDone (tCurrentPlayerDone);
		setFormingPresidentAssigned (tFormingPresidentAssigned);

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

	public boolean getCurrentPlauerDone () {
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
}
