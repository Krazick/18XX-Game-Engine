package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.company.formation.FormCompany;
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
 	Corporation triggeringCompany;
	
	public StartFormationEffect (ActorI aActor, Corporation aFormingCorporation, Corporation aTriggeringCompany) {
		super (NAME, aActor);
		setFormingCorporation (aFormingCorporation);
		setTriggeringCompany (aTriggeringCompany);
	}

	public StartFormationEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		setName (NAME);
		int tFormingCompanyID;
		int tTriggeringCompanyID;
		Corporation tFormingCorporation;
	 	Corporation tTriggeringCompany;
	 	TriggerClass tTriggerFormationClass;
		
		tTriggeringCompany = Corporation.NO_CORPORATION;
		tFormingCompanyID = aEffectNode.getThisIntAttribute (AN_FORMING_COMPANY_ID);
		tFormingCorporation = aGameManager.getCorporationByID (tFormingCompanyID);
		setFormingCorporation (tFormingCorporation);
		if (triggeringCompany != Corporation.NO_CORPORATION) {
			tTriggeringCompanyID = aEffectNode.getThisIntAttribute (AN_TRIGGERING_COMPANY_ID);
			tTriggeringCompany = aGameManager.getShareCompanyByID (tTriggeringCompanyID);
			setTriggeringCompany (tTriggeringCompany);
		} else {
			tTriggeringCompanyID = aEffectNode.getThisIntAttribute (AN_TRIGGERING_COMPANY_ID);
			tTriggeringCompany = aGameManager.getCorporationByID (tTriggeringCompanyID);
			setTriggeringCompany (tTriggeringCompany);
		}
		tTriggerFormationClass = aGameManager.getTriggerFormation ();
		if (tTriggerFormationClass != TriggerClass.NO_TRIGGER_CLASS) {
			tTriggerFormationClass.setTriggeringCompany (tTriggeringCompany);
		}
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_FORMING_COMPANY_ID, formingCorporation.getID ());
		tEffectElement.setAttribute (AN_TRIGGERING_COMPANY_ID, triggeringCompany.getID ());
	
		return tEffectElement;
	}

	public void setFormingCorporation (Corporation aFormingCorporation) {
		formingCorporation = aFormingCorporation;
	}

	public void setTriggeringCompany (Corporation aTriggeringCompany) {
		triggeringCompany = aTriggeringCompany;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tReport;
		String tFormingCompanyAbbrev;
		String tTriggeringCompanyAbbrev;
		String tActorName;
		
		if (formingCorporation == Corporation.NO_CORPORATION) {
			tFormingCompanyAbbrev = Corporation.NO_ABBREV;
		} else {
			tFormingCompanyAbbrev = formingCorporation.getAbbrev ();
		}
		if (triggeringCompany == Corporation.NO_CORPORATION) {
			tTriggeringCompanyAbbrev = Corporation.NO_ABBREV;
		} else {
			tTriggeringCompanyAbbrev = triggeringCompany.getAbbrev ();
		} 
		tActorName = actor.getName ();
		tReport = REPORT_PREFIX + name + " for " + tFormingCompanyAbbrev + " by " + tActorName + " triggered by " + 
				tTriggeringCompanyAbbrev + ".";
		
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
			tGameManager.setTriggerFormation (FormCompany.NO_FORM_COMPANY);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
