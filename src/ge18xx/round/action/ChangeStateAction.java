package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.effects.ChangeCorporationStatusEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.StateChangeEffect;
import ge18xx.utilities.XMLNode;

public class ChangeStateAction extends ChangePlayerAction {
	public final static String NAME = "Change State";
	public final static ChangeStateAction NO_CHANGE_STATE_ACTION = null;
	
	public ChangeStateAction () {
		this (NAME);
	}
	
	public ChangeStateAction (String aName) {
		super (NAME);
	}
	
	public ChangeStateAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}
	
	public ChangeStateAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addStateChangeEffect (ActorI aActor, ActorI.ActionStates aOldState, 
			ActorI.ActionStates aNewState) {
		StateChangeEffect tStateChangeEffect;

		if (actor.isACorporation ()) {
			addChangeCorporationStatusEffect (aActor, aOldState, aNewState);
		} else {
			tStateChangeEffect = new StateChangeEffect (aActor, aOldState, aNewState);
			addEffect (tStateChangeEffect);
		}
	}
	
	public void addChangeCorporationStatusEffect (ActorI aActor, ActorI.ActionStates aPreviousState, ActorI.ActionStates aNewState) {
		ChangeCorporationStatusEffect tChangeCorporationStatusEffect;

		tChangeCorporationStatusEffect = new ChangeCorporationStatusEffect (aActor, aPreviousState, aNewState);
		addEffect (tChangeCorporationStatusEffect);
	}

	public boolean wasLastActionStartAuction () {
		boolean tWasNewStateAuction = false;
		
		for (Effect tEffect: effects) {
			if (tEffect.wasNewStateAuction ()) {
				tWasNewStateAuction = true;
			}
		}
		
		return tWasNewStateAuction;
	}
	
	public String getOldState () {
		String tOldState = "";
		
		for (Effect tEffect : effects) {
			if (tOldState.equals ("")) {
				if (tEffect instanceof StateChangeEffect) {
					tOldState = ((StateChangeEffect) tEffect).getPreviousState ().toString ();
				}
			}
		}
		
		return tOldState;
	}

	public String getNewState () {
		String tOldState = "";
		
		for (Effect tEffect : effects) {
			if (tOldState.equals ("")) {
				if (tEffect instanceof StateChangeEffect) {
					tOldState = ((StateChangeEffect) tEffect).getNewState ().toString ();
				}
			}
		}
		
		return tOldState;
	}

	public String getActorNames () {
		String tActorNames = "";
		String tActorName;
		
		for (Effect tEffect : effects) {
			if (tEffect instanceof StateChangeEffect) {
				tActorName = ((StateChangeEffect) tEffect).getActorName ();
				if (! tActorNames.contains (tActorName)) {
					tActorNames += tActorName + ", ";
				}
			}
		}
		if (tActorNames.equals ("")) {
			tActorNames = "NONE";
		} else {
			tActorNames = tActorNames.substring (0, tActorNames.length () - 2);
		}
		
		return tActorNames;
	}
	
	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";
		String tOldState, tNewState;
		
		tOldState = getOldState ();
		tNewState = getNewState ();
		if (! tNewState.equals (tOldState)) {
			tSimpleActionReport = actor.getName () + " changed state of " + getActorNames () + " from  " + getOldState () + 
				" to " + getNewState () + ".";
		} else {
			tSimpleActionReport = actor.getName () + " state for remains [" + tOldState + "]";
		}
		
		return tSimpleActionReport;
	}
}
