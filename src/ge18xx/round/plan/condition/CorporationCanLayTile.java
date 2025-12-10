package ge18xx.round.plan.condition;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationFrame;
import geUtilities.GUI;
import swingTweaks.KButton;

public class CorporationCanLayTile extends CorporationIsActive {
	public static final String NAME = "Corporation can Lay Tile";

	public CorporationCanLayTile (Corporation aCorporation) {
		this (NAME, aCorporation);
	}

	public CorporationCanLayTile (String aName, Corporation aCorporation) {
		super (aName, aCorporation);
	}

	@Override
	public boolean meets () {
		boolean tMeets;
		CorporationFrame tCorporationFrame;
		KButton tDummyTileButton;
		String tFailsReason;
		
		tFailsReason = GUI.EMPTY_STRING;
		if (super.meets ()) {
			tCorporationFrame = corporation.getCorporationFrame ();
			tDummyTileButton = new KButton ("DummyTileButton");
			if (tCorporationFrame.updateTileButton (tDummyTileButton)) {
				tMeets = MEETS;
			} else {
				tMeets = FAILS;
				tFailsReason = tDummyTileButton.getToolTipText ();
			}
		} else {
			tMeets = FAILS;
		}
		
		setFailsReason (tFailsReason);
		
		return tMeets;
	}

}
