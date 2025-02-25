package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.company.formation.FormCGR;
import ge18xx.company.formation.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class StartFormationEffect extends Effect {
	public static final String NAME = "Start Formation";
	public static final AttributeName AN_FORMING_COMPANY_ID = new AttributeName ("formingCompanyID");
	public static final AttributeName AN_TRIGGERING_COMPANY_ID = new AttributeName ("triggeringCompanyID");
	Corporation formingCorporation;
 	ShareCompany triggeringShareCompany;
	
	public StartFormationEffect (ActorI aActor, Corporation aFormingCorporation, ShareCompany aTriggeringShareCompany) {
		super (NAME, aActor);
		setFormingCorporation (aFormingCorporation);
		setTriggeringShareCompany (aTriggeringShareCompany);
	}

	public StartFormationEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		setName (NAME);
		int tFormingCompanyID;
		int tTriggeringShareCompanyID;
		Corporation tFormingCorporation;
	 	ShareCompany tTriggeringShareCompany;
	 	TriggerClass tTriggerFormationClass;
		
		tFormingCompanyID = aEffectNode.getThisIntAttribute (AN_FORMING_COMPANY_ID);
		tFormingCorporation = aGameManager.getCorporationByID (tFormingCompanyID);
		setFormingCorporation (tFormingCorporation);
		tTriggeringShareCompanyID = aEffectNode.getThisIntAttribute (AN_TRIGGERING_COMPANY_ID);
		tTriggeringShareCompany = (ShareCompany) aGameManager.getShareCompanyByID (tTriggeringShareCompanyID);
		setTriggeringShareCompany (tTriggeringShareCompany);
		tTriggerFormationClass = aGameManager.getTriggerFormation ();
		if (tTriggerFormationClass != TriggerClass.NO_TRIGGER_CLASS) {
			tTriggerFormationClass.setTriggeringShareCompany (tTriggeringShareCompany);
		}
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_FORMING_COMPANY_ID, formingCorporation.getID ());
		tEffectElement.setAttribute (AN_TRIGGERING_COMPANY_ID, triggeringShareCompany.getID ());
	
		return tEffectElement;
	}

	public void setFormingCorporation (Corporation aFormingCorporation) {
		formingCorporation = aFormingCorporation;
	}

	public void setTriggeringShareCompany (ShareCompany aTriggeringShareCompany) {
		triggeringShareCompany = aTriggeringShareCompany;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tReport;
		String tFormingCompanyAbbrev;
		String tTriggeringShareCompanyAbbrev;
		String tActorName;
		
		if (formingCorporation == Corporation.NO_CORPORATION) {
			tFormingCompanyAbbrev = "NO Formation Corporation";
		} else {
			tFormingCompanyAbbrev = formingCorporation.getAbbrev ();
		}
		tTriggeringShareCompanyAbbrev = triggeringShareCompany.getAbbrev ();
		tActorName = actor.getName ();
		tReport = REPORT_PREFIX + name + " for " + tFormingCompanyAbbrev + " by " + tActorName + " triggered by " + 
				tTriggeringShareCompanyAbbrev + ".";
		
		return tReport;
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		
		tEffectApplied = true;
		
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		GameManager tGameManager;
		
		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tGameManager = aRoundManager.getGameManager ();
			tGameManager.setTriggerFormation (FormCGR.NO_FORM_CGR);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
