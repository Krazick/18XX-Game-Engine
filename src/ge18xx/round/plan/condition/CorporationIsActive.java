package ge18xx.round.plan.condition;

import ge18xx.company.Corporation;
import geUtilities.GUI;

public class CorporationIsActive extends CorporationExists {
	public static final String NAME = "Corporation is Active";

	public CorporationIsActive (Corporation aCorporation) {
		this (NAME, aCorporation);
	}

	public CorporationIsActive (String aName, Corporation aCorporation) {
		super (aName, aCorporation);
	}

	@Override
	public boolean meets () {
		boolean tMeets;
		String tFailsReason;
		
		tFailsReason = GUI.EMPTY_STRING;
		if (super.meets ()) {
			if (corporation.isActive ()) {
				tMeets = MEETS;
			} else {
				tMeets = FAILS;
				tFailsReason = "Corporation is not Active";
			}
		} else {
			tMeets = FAILS;
		}
		setFailsReason (tFailsReason);
		
		return tMeets;
	}
}
