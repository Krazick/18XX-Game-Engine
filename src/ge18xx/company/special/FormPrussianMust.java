package ge18xx.company.special;

import ge18xx.game.GameManager;
import ge18xx.round.action.Action;

public class FormPrussianMust extends TriggerClass {

	public FormPrussianMust (GameManager aGameManager, Action aAction) {
		System.out.println ("Initiate Form Prussian Must Game: " + aGameManager.getActiveGameName ());
	}

}
