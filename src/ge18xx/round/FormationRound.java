package ge18xx.round;

import ge18xx.round.action.ActorI;

public class FormationRound extends InterruptionRound {
	public static final FormationRound NO_FORMATION_ROUND = null;
	public static final String NAME = "Formation Round";

	public FormationRound (RoundManager aRoundManager) {
		super (aRoundManager);
		setName (NAME);
	}
	
	@Override
	public ActorI.ActionStates getRoundType () {
		return ActorI.ActionStates.FormationRound;
	}
	
	@Override
	public boolean isAFormationRound () {
		return true;
	}
}
