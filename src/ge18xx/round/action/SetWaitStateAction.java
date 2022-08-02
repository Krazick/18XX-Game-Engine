package ge18xx.round.action;

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
	}
	
	public SetWaitStateAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public SetWaitStateAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
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
	
	public void resetPlayerStatesAfterWait (Action tWaitedAction) {
		Effect tEffect;
		SetWaitStateEffect tSetWaitStateEffect;
		String tEffectName;
		ActorI.ActionStates tOldState;
		ActorI.ActionStates tNewState;
		ActorI tToActor;
		ActorI tActor;
		
		tEffectName = SetWaitStateEffect.NAME;
		tEffect = tWaitedAction.getEffectNamed (tEffectName);
		if (tEffect != Effect.NO_EFFECT) {
			tSetWaitStateEffect = (SetWaitStateEffect) tEffect;
			// Need to reset the Player State so reverse the New and Old
			tNewState = tSetWaitStateEffect.getPreviousState ();
			tOldState = tSetWaitStateEffect.getNewState ();
			tActor = tSetWaitStateEffect.getActor ();
			tToActor = tSetWaitStateEffect.getToActor ();
			addSetWaitStateEffect (tActor, tToActor, tOldState, tNewState);
		} else {
			System.err.println ("Could not find Effect named " + tEffectName + " in Action named " + 
								tWaitedAction.getName ());
		}
	}

}
