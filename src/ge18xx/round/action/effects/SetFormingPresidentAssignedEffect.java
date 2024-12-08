package ge18xx.round.action.effects;

import ge18xx.company.formation.FormCGR;
import ge18xx.company.formation.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.round.FormationRound;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class SetFormingPresidentAssignedEffect extends Effect {
	public final static String NAME = "Set Forming President Assigned";
	final static AttributeName AN_PRESIDENT_ASSIGNED = new AttributeName ("presidentAssigned");
	boolean presidentAssigned;

	public SetFormingPresidentAssignedEffect (ActorI aActor, boolean aPresidentAssigned) {
		super (NAME, aActor);
		setPresidentAssigned (aPresidentAssigned);
	}

	public SetFormingPresidentAssignedEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		boolean tRepaymentHandled;
		
		tRepaymentHandled = aEffectNode.getThisBooleanAttribute (AN_PRESIDENT_ASSIGNED);
		setPresidentAssigned (tRepaymentHandled);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_PRESIDENT_ASSIGNED, presidentAssigned);

		return tEffectElement;
	}

	public void setPresidentAssigned (boolean aPresidentAssigned) {
		presidentAssigned = aPresidentAssigned;
	}
	
	public boolean getPresidentAssigned () {
		return presidentAssigned;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName () + " to TRUE.");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		FormationRound tFormationRound;
		TriggerClass tTriggerClass;
		FormCGR tFormCGR;
		
		tEffectApplied = false;
		tFormationRound = aRoundManager.getFormationRound ();
		tTriggerClass = tFormationRound.getTriggerFormationClass ();
		if (tTriggerClass instanceof FormCGR) {
			tFormCGR = (FormCGR) tTriggerClass;
			tFormCGR.setFormingPresidentAssigned (presidentAssigned);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		FormationRound tFormationRound;
		TriggerClass tTriggerClass;
		FormCGR tFormCGR;
		
		tEffectUndone = false;
		tFormationRound = aRoundManager.getFormationRound ();
		tTriggerClass = tFormationRound.getTriggerFormationClass ();
		if (tTriggerClass instanceof FormCGR) {
			tFormCGR = (FormCGR) tTriggerClass;
			tFormCGR.setFormingPresidentAssigned (! presidentAssigned);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}

}
