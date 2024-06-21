package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class ChangeCorporationStatusEffect extends Effect {
	public static final String NAME = "Change Corporation Status";
	public static final AttributeName AN_PREVIOUS_STATE = new AttributeName ("previousState");
	public static final AttributeName AN_NEW_STATE = new AttributeName ("newState");
	ActorI.ActionStates previousState;
	ActorI.ActionStates newState;

	public ChangeCorporationStatusEffect () {
		super ();
		setName (NAME);
		setPreviousState (ActorI.ActionStates.Unowned);
		setNewState (ActorI.ActionStates.Unowned);
	}

	public ChangeCorporationStatusEffect (ActorI aActor, ActorI.ActionStates aPreviousState,
			ActorI.ActionStates aNewState) {
		super (NAME, aActor);
		setPreviousState (aPreviousState);
		setNewState (aNewState);
	}

	public ChangeCorporationStatusEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		String tPreviousStateName;
		String tNewStateName;
		ActorI.ActionStates tPreviousState;
		ActorI.ActionStates tNewState;

		tPreviousStateName = aEffectNode.getThisAttribute (AN_PREVIOUS_STATE);
		tNewStateName = aEffectNode.getThisAttribute (AN_NEW_STATE);
		tPreviousState = aGameManager.getCorporationState (tPreviousStateName);
		tNewState = aGameManager.getCorporationState (tNewStateName);
		setPreviousState (tPreviousState);
		setNewState (tNewState);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_PREVIOUS_STATE, previousState.toString ());
		tEffectElement.setAttribute (AN_NEW_STATE, newState.toString ());

		return tEffectElement;
	}

	public ActorI.ActionStates getNewState () {
		return newState;
	}

	public ActorI.ActionStates getPreviousState () {
		return previousState;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tActorFullName;

		tActorFullName = actor.getAbbrev ();
		if (actor.isAPrivateCompany ()) {
			tActorFullName = tActorFullName + " (Private)";
		}
		return (REPORT_PREFIX + name + " for " + tActorFullName + " from " + previousState + " to " + newState + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setNewState (ActorI.ActionStates aNewState) {
		newState = aNewState;
	}

	public void setPreviousState (ActorI.ActionStates aPreviousState) {
		previousState = aPreviousState;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Corporation tCorporation;

		tEffectApplied = false;
		if (actor.isACorporation ()) {
			tCorporation = (Corporation) actor;
			if (newState.equals (ActorI.ActionStates.Operated)) {
				tCorporation.clearAllTrainsFromMap (false);
			}
			tCorporation.resetStatus (newState);
			tEffectApplied = true;
		} else {
			setApplyFailureReason ("Actor " + actor.getName () + " is not a Corporation.");
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Corporation tCorporation;

		tEffectUndone = false;
		if (actor.isACorporation ()) {
			tCorporation = (Corporation) actor;
			tCorporation.resetStatus (previousState);
			tEffectUndone = true;
		} else {
			setUndoFailureReason ("Actor " + actor.getName () + " is not a Corporation.");
		}

		return tEffectUndone;
	}
}
