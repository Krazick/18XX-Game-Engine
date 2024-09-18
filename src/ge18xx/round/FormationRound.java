package ge18xx.round;

public class FormationRound extends Round {
	public static final FormationRound NO_FORMATION_ROUND = null;
	public static final String NAME = "Formation Round";

	public FormationRound (RoundManager aRoundManager) {
		super (aRoundManager);
	}
	
	@Override
	public String getName () {
		return NAME;
	}
	
	@Override
	public boolean isAFormationRound () {
		return true;
	}
}
