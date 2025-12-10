package ge18xx.round.plan.condition;

import ge18xx.company.Corporation;
import geUtilities.GUI;

public class CorporationIsInStatus extends CorporationExists {
	public static final String NAME = "Corporation is in Status";
	String statusList;

	public CorporationIsInStatus (Corporation aCorporation, String aStatusList) {
		this (NAME, aCorporation, aStatusList);
	}
	
	public CorporationIsInStatus (String aName, Corporation aCorporation, String aStatusList) {
		super (aName, aCorporation);
		setStatusList (aStatusList);
	}

	public void setStatusList (String aStatusList) {
		statusList = aStatusList;
	}
	
	public String getStatusList () {
		return statusList;
	}
	
	@Override
	public boolean meets () {
		boolean tMeets;
		String tStatus;
		String tFailsReason;
		
		tFailsReason = GUI.EMPTY_STRING;
		if (super.meets ()) {
			tStatus = corporation.getStatusName ();
			if (statusList.contains (tStatus)) {
				tMeets = MEETS;
			} else {
				tMeets = FAILS;
				tFailsReason = "Current Status of " + tStatus + " is not one of " + statusList;
			}
		} else {
			tMeets = FAILS;
			tFailsReason = failsReason;
		}
		setFailsReason (tFailsReason);
		
		return tMeets;
	}
}
