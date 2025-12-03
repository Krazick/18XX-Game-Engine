package ge18xx.round.plan.condition;

import ge18xx.round.RoundManager;

public class RoundIs extends Condition {
	public static final String NAME = "Round is";
	String roundName;
	RoundManager roundManager;
	
	public RoundIs (String aRoundName, RoundManager aRoundManager) {
		super (NAME);
		setRoundName (aRoundName);
		setRoundManager (aRoundManager);
	}

	public void setRoundName (String aRoundName) {
		roundName = aRoundName;
	}
	
	public String getRoundName () {
		return roundName;
	}

	public void setRoundManager (RoundManager aRoundManager) {
		roundManager = aRoundManager;
	}
	
	public RoundManager getRoundManager() {
		return roundManager;
	}
	
	@Override
	public boolean meets () {
		boolean tMeets;
		
		if (roundManager.getRoundName ().equals (roundName)) {
			tMeets = MEETS;
		} else {
			tMeets = FAILS;
		}
		
		return tMeets;
	}
}
