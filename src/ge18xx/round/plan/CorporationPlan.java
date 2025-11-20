package ge18xx.round.plan;

import ge18xx.company.Corporation;

public class CorporationPlan extends Plan {
	Corporation corporation;
	
	public CorporationPlan (String aGameName, String aName) {
		this (aGameName, aName, Corporation.NO_CORPORATION);
	}

	public CorporationPlan (String aGameName, String aName, Corporation aCorporation) {
		super (aGameName, aName);
		setCorporation (aCorporation);
	}
	
	public void setCorporation (Corporation aCorporation) {
		corporation = aCorporation;
	}
	
	public Corporation getCorporation () {
		return corporation;
	}
}
