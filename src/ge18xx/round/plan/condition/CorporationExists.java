package ge18xx.round.plan.condition;

import ge18xx.company.Corporation;
import geUtilities.GUI;

public class CorporationExists extends Condition {
	public static final String NAME = "Corporation Exists";
	Corporation corporation;

	public CorporationExists (Corporation aCorporation) {
		this (NAME, aCorporation);
	}
	
	public CorporationExists (String aName, Corporation aCorporation) {
		super (aName);
		setCorporation (aCorporation);
	}

	public void setCorporation (Corporation aCorporation) {
		corporation = aCorporation;
	}
	
	public Corporation getCorporation () {
		return corporation;
	}
	
	@Override
	public boolean meets () {
		boolean tMeets;
		String tFailsReason;
		
		tFailsReason = GUI.EMPTY_STRING;
		if (corporation == Corporation.NO_CORPORATION) {
			tMeets = FAILS;
			tFailsReason = "Corporation is not set";
		} else {
			tMeets = MEETS;
		}
		setFailsReason (tFailsReason);
		
		return tMeets;
	}

	@Override
	public String getReport () {
		String tReport;
		
		tReport = super.getReport () + " (" + corporation.getAbbrev () + ")";
		tReport = appendStatus (tReport);
		
		return tReport;
	}
}
