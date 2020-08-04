package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.phase.PhaseManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class PhaseChangeEffect extends Effect {
	public final static String NAME = "Phase Change";
	final static AttributeName AN_PREVIOUS_PHASE = new AttributeName ("previousPhase");
	final static AttributeName AN_NEW_PHASE = new AttributeName ("newPhase");
	int previousPhaseIndex;
	int newPhaseIndex;

	public PhaseChangeEffect() {
		super ();
		setName (NAME);
		setPreviousPhase (PhaseManager.NO_PHASE);
		setNewPhase (PhaseManager.NO_PHASE);
	}

	public PhaseChangeEffect (ActorI aActor, int aPreviousStateIndex, int aNewStateIndex) {
		super (NAME, aActor);
		setPreviousPhase (aPreviousStateIndex);
		setNewPhase (aNewStateIndex);
	}

	public PhaseChangeEffect(XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		int tPreviousPhaseIndex, tNewPhaseIndex;
		
		tPreviousPhaseIndex = aEffectNode.getThisIntAttribute (AN_PREVIOUS_PHASE);
		tNewPhaseIndex = aEffectNode.getThisIntAttribute (AN_NEW_PHASE);
		setPreviousPhase (tPreviousPhaseIndex);
		setNewPhase (tNewPhaseIndex);
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_PREVIOUS_PHASE, previousPhaseIndex);
		tEffectElement.setAttribute (AN_NEW_PHASE, newPhaseIndex);
	
		return tEffectElement;
	}

	public int getNewPhaseIndex () {
		return newPhaseIndex;
	}
	
	public int getPreviousPhaseIndex () {
		return previousPhaseIndex;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " by " + actor.getName () + " from " + previousPhaseIndex + 
				" to " + newPhaseIndex + ".");
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setPreviousPhase (int aPreviousPhaseIndex) {
		previousPhaseIndex = aPreviousPhaseIndex;
	}
	
	public void setNewPhase (int aNewPhaseIndex) {
		newPhaseIndex = aNewPhaseIndex;
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		PhaseManager tPhaseManager;
		
		tEffectApplied = false;
		tPhaseManager = aRoundManager.getPhaseManager ();
		tPhaseManager.setCurrentPhase (newPhaseIndex);
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		PhaseManager tPhaseManager;
		
		tEffectUndone = false;
		tPhaseManager = aRoundManager.getPhaseManager ();
		tPhaseManager.setCurrentPhase (previousPhaseIndex);
		tEffectUndone = true;

		return tEffectUndone;
	}
}
