package ge18xx.round.plan.condition;

import ge18xx.company.Corporation;

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
		
		if (corporation == Corporation.NO_CORPORATION) {
			tMeets = FAILS;
		} else {
			tMeets = MEETS;
		}
		
		return tMeets;
	}
}
