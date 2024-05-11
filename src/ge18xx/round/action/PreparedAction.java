package ge18xx.round.action;

import ge18xx.round.action.ActorI.ActionStates;

public class PreparedAction {
	public static final Action NO_ACTION = null;
	ActorI.ActionStates targetState;
	Action action;
	
	public PreparedAction (ActorI.ActionStates aTargetState, Action aAction) {
		setTargetState (aTargetState);
		setAction (aAction);
	}

	private void setAction (Action aAction) {
		action = aAction;	
	}

	private void setTargetState (ActionStates aTargetState) {
		targetState = aTargetState;
	}

	public Action getAction () {
		return action;
	}
	
	public ActorI.ActionStates getTargetState () {
		return targetState;
	}
}
