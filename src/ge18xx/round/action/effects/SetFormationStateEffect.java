package ge18xx.round.action.effects;

import ge18xx.company.special.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class SetFormationStateEffect extends ChangeCorporationStatusEffect {
	public final static String NAME = "formation State";

	public SetFormationStateEffect () {
		super ();
		setName (NAME);
		setPreviousState (ActorI.ActionStates.NoState);
		setNewState (ActorI.ActionStates.NoState);
	}

	public SetFormationStateEffect (ActorI aActor, ActorI.ActionStates aPreviousState,
			ActorI.ActionStates aNewState) {
		super (aActor, aPreviousState, aNewState);
		setName (NAME);
	}

	public SetFormationStateEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		GameManager tGameManager;
		TriggerClass tTriggerClass;
		
		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			tGameManager = aRoundManager.getGameManager ();
			tTriggerClass = tGameManager.getTriggerClass ();
			tTriggerClass.setFormationState (newState);
			tEffectApplied = true;
		} else {
			setApplyFailureReason ("Actor " + actor.getName () + " is not a Player.");
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		GameManager tGameManager;
		TriggerClass tTriggerClass;

		tEffectUndone = false;
		if (actor.isAPlayer ()) {
			tGameManager = aRoundManager.getGameManager ();
			tTriggerClass = tGameManager.getTriggerClass ();
			tTriggerClass.setFormationState (previousState);
			tEffectUndone = true;
		} else {
			setUndoFailureReason ("Actor " + actor.getName () + " is not a Player.");
		}

		return tEffectUndone;
	}

}
