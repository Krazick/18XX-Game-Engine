package ge18xx.company.special;

import ge18xx.game.GameManager;
import ge18xx.round.action.Action;

public class FormPrussianOption extends TriggerClass {

	public FormPrussianOption () {
		
	}
	
	public FormPrussianOption (GameManager aGameManager, Action aAction) {
		System.out.println ("Initiate Form Prussian Option Game: " + aGameManager.getActiveGameName ());
	}

}
