package ge18xx.company.formation;

import java.awt.event.ActionListener;
import java.util.List;

import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.action.StartFormationAction;
import ge18xx.round.action.ActorI.ActionStates;

import geUtilities.GUI;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLSaveGameI;
import swingTweaks.KButton;

public class TriggerClass implements XMLSaveGameI {
	public static final TriggerClass NO_TRIGGER_CLASS = null;
	
	public TriggerClass () {
		
	}
	
	public TriggerClass (GameManager aGameManager) {
		
	}
	
	public void setTriggeringShareCompany (ShareCompany aTriggeringShareCompany) {

	}
	
	public void setTriggeringCompany (Corporation aTriggeringCompany) {

	}

	public void setActingPresident (Player aActingPresident) {

	}

	public void prepareFormation (StartFormationAction aStartFormationAction) {

	}
	
	public void rebuildFormationPanel (int aCurrentPlayerIndex) {
	
	}
	
	public void rebuildFormationPanel () {

	}

	public void hideFormationPanel () {
		
	}
	
	public void showFormationFrame () {
		
	}
	
	public void triggeringHandleDone () {
		
	}
	
	public int updateToNextPlayer (List<Player> aPlayers, boolean aAddAction) {
		return 0;
	}
	
	public void setCurrentPlayerIndex (int aCurrentPlayerIndex) {
		
	}
	
	public boolean ends () {
		return false;
	}
	
	public XMLFrame getFormationFrame () {
		return XMLFrame.NO_XML_FRAME;
	}
	
	public String setFormationState (ActionStates aFormationState) {
		return GUI.EMPTY_STRING;
	}
	
	public KButton buildSpecialButton (String aText, String aActionCommand, String aToolTip, 
						ActionListener aActionListener) {
		KButton tSpecialButton;
		boolean tEnabled;
		
		tEnabled = getEnabled (aToolTip);
		tSpecialButton = new KButton (aText);
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
}
