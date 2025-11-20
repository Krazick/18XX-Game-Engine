package ge18xx.round.plan;

import ge18xx.company.Corporation;

public class CorporationPlan extends Plan {
	Corporation corporation;
	
	public CorporationPlan (Corporation aCorporation, String aGameName, String aName) {
		super (aGameName, aName);
		setCorporation (aCorporation);
	}

	private void setCorporation (Corporation aCorporation) {
		corporation = aCorporation;
	}

	public Corporation getCorporation () {
		return corporation;
	}
	
	public String getCorporationName () {
		return corporation.getName ();
	}
}
