package ge18xx.round.plan.condition;

import geUtilities.GUI;

public class Condition {
	public static final boolean FAILS = false;
	public static final boolean MEETS = true;
	public static final String FAILS_STRING = "FAILS";
	public static final String MEETS_STRING = "MEETS";
	public static final String NAME = "Condition";
	String name;
	String failsReason;
	
	public Condition (String aName) {
		setName (aName + " " + NAME);
		setFailsReason (GUI.EMPTY_STRING);
	}

	public void setName (String aName) {
		name = aName;
	}
	
	public String getName () {
		return name;
	}
	
	public boolean meets () {
		return FAILS;
	}
	
	public void setFailsReason (String aFailsReason) {
		failsReason = aFailsReason;
	}
	
	public String getFailsReason () {
		return failsReason;
	}
	
	public String getReport () {
		String tReport;
		
		tReport = name;
		
		return tReport;
	}

	protected String appendStatus (String aReport) {
		if (meets ()) {
			aReport += " " + MEETS_STRING;
		} else {
			aReport += " " + FAILS_STRING + " " + failsReason;
		}
		
		return aReport;
	}
}
