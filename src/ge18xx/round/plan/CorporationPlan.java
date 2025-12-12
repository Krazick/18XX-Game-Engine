package ge18xx.round.plan;

import ge18xx.company.Corporation;

public class CorporationPlan extends Plan {
	Corporation corporation;

	public CorporationPlan (String aPlayerName, String aGameName, String aName) {
		this (aPlayerName, aGameName, aName, Corporation.NO_CORPORATION);
	}

	public CorporationPlan (String aPlayerName, String aGameName, String aName, Corporation aCorporation) {
		super (aPlayerName, aGameName, aName);
		setCorporation (aCorporation);
	}

	public void setCorporation (Corporation aCorporation) {
		corporation = aCorporation;
	}

	public Corporation getCorporation () {
		return corporation;
	}
	
	public String getCorporationName () {
		String tCorporationName;
		
		if (corporation == Corporation.NO_CORPORATION) {
			tCorporationName = Corporation.NO_NAME;
		} else {
			tCorporationName = corporation.getName ();
		}
		
		return tCorporationName;
	}
}
