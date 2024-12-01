package ge18xx.round.action;

import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.SetFormationStateEffect;
import ge18xx.round.action.effects.ShowFormationPanelEffect;
import ge18xx.round.action.effects.StartFormationEffect;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLNode;

/* Currently there is no code that calls this. 
 * This Action is stored as a "Prepared Action" in the 18xx-Games.xml File for when the 
 * CGR Share Company is formed at the Start of Phase 5, that is triggered by the Purchase 
 * of the first 6 Train, and the buy Company Completes the DONE Action.
 * 
 */
public class StartFormationAction extends ChangeRoundAction {
	public final static AttributeName AN_TRIGGERING_CORPORATION = new AttributeName ("triggeringCorporation");
	public final static String NAME = "Start Formation";
	public final static StartFormationAction NO_START_FORMATION_ACTION = null;
	ShareCompany triggeringShareCompany;

	public StartFormationAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public StartFormationAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
		
		String tTriggeringCorporationAbbrev;
		
		tTriggeringCorporationAbbrev = aActionNode.getThisAttribute (AN_NAME);
		setTriggeringShareCompany (tTriggeringCorporationAbbrev, aGameManager);
	}
	
	public void setTriggeringShareCompany (ShareCompany aTriggeringShareCompany) {
		triggeringShareCompany = aTriggeringShareCompany;
	}
	
	public ShareCompany getTriggeringShareCompany () {
		return triggeringShareCompany;
	}
	
	public void setTriggeringShareCompany (String aTriggeringShareCompanyAbbrev, GameManager aGameManager) {
		ShareCompany tTriggeringShareCompany;
		
		tTriggeringShareCompany = aGameManager.getShareCompany (aTriggeringShareCompanyAbbrev);
		setTriggeringShareCompany (tTriggeringShareCompany);
	}
	
	public void addShowFormationPanelEffect (ActorI aFromActor) {
		ShowFormationPanelEffect tShowFormationPanelEffect;
		
		tShowFormationPanelEffect = new ShowFormationPanelEffect (aFromActor);
		addEffect (tShowFormationPanelEffect);
	}
	
	public void addSetFormationStateEffect (ActorI aFromActor, ActorI.ActionStates aOldFormationState,
			ActorI.ActionStates aNewFormationState) {
		SetFormationStateEffect tSetFormationStateEffect;
		
		tSetFormationStateEffect = new SetFormationStateEffect (aFromActor, aOldFormationState, aNewFormationState);
		addEffect (tSetFormationStateEffect);
	}

	public void addStartFormationEffect (ActorI aActor, Corporation aFormingCorporation, 
					ShareCompany aTriggeringShareCompany) {
		StartFormationEffect tStartFormationEffect;
		
		tStartFormationEffect = new StartFormationEffect (aActor, aFormingCorporation, 
			aTriggeringShareCompany);
		addEffect (tStartFormationEffect);
	}
	
	public void setTriggeringShareCompanyToPrepared (ShareCompany tTriggeringShareCompany) {
		StartFormationEffect tStartFormationEffect;
		
		for (Effect tEffect : effects) {
			if (tEffect instanceof StartFormationEffect) {
				tStartFormationEffect = (StartFormationEffect) tEffect;
				tStartFormationEffect.setTriggeringShareCompany (tTriggeringShareCompany);
			}
		}
	}
}
