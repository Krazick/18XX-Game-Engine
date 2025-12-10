package ge18xx.round.plan.condition;

import ge18xx.round.RoundManager;
import geUtilities.GUI;

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
		String tFailsReason;
		
		tFailsReason = GUI.EMPTY_STRING;
		if (roundManager.getRoundName ().equals (roundName)) {
			tMeets = MEETS;
		} else {
			tMeets = FAILS;
			tFailsReason = "Current Round is not " + roundName;
		}
		setFailsReason (tFailsReason);
		
		return tMeets;
	}

	@Override
	public String getReport () {
		String tReport;
		
		tReport = super.getReport () + " (" + roundName + ")";
		tReport = appendStatus (tReport);
		
		return tReport;
	}
}
