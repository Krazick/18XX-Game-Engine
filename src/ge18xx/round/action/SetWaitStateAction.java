package ge18xx.round.action;

import java.util.List;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.SetWaitStateEffect;
import ge18xx.utilities.XMLNode;

public class SetWaitStateAction extends Action {
	public final static String NAME = "Set Wait State";

	public SetWaitStateAction () {
		super ();
		setName (NAME);
	}

	public SetWaitStateAction (Action aAction) {
		super (aAction);
		setName (NAME);
	}

	public SetWaitStateAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public SetWaitStateAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public SetWaitStateAction (String aName) {
		super (aName);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " requested all players WAIT.";

		return tSimpleActionReport;
	}

	public void addSetWaitStateEffect (ActorI aFromActor, ActorI aToActor, ActionStates aOldState,
						ActionStates aNewState) {
		SetWaitStateEffect tSetWaitStateEffect;

		tSetWaitStateEffect = new SetWaitStateEffect (aFromActor, aToActor,
						aOldState, aNewState);
		addEffect (tSetWaitStateEffect);
	}

	public void resetPlayerStatesAfterWait (Action aWaitedAction) {
		SetWaitStateEffect tSetWaitStateEffect;
		String tEffectName;
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		ActorI tToActor;
		ActorI tActor;
		List<Effect> tEffects;

		tEffects = aWaitedAction.getEffects ();
		for (Effect tEffect : tEffects) {
			tEffectName = tEffect.getName ();
			if (tEffectName.equals (SetWaitStateEffect.NAME)) {
				tSetWaitStateEffect = (SetWaitStateEffect) tEffect;
				// Need to reset the Player State so reverse the New and Old
				tNewState = tSetWaitStateEffect.getPreviousState ();
				tOldState = tSetWaitStateEffect.getNewState ();
				tActor = tSetWaitStateEffect.getActor ();
				tToActor = tSetWaitStateEffect.getToActor ();
				addSetWaitStateEffect (tActor, tToActor, tOldState, tNewState);
			} else {
				System.err.println ("Could not find Effect named " + tEffectName + " in Action named " +
									aWaitedAction.getName ());
			}
		}
	}

}
