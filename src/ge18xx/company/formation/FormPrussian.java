package ge18xx.company.formation;

import ge18xx.game.GameManager;
import ge18xx.round.action.StartFormationAction;
import geUtilities.xml.XMLFrame;

public class FormPrussian extends TriggerClass {
	XMLFrame formationFrame;
	int currentPlayerIndex;

	public FormPrussian (GameManager aGameManager) {
		System.out.println ("Initiate Form Prussian Option Game: " + aGameManager.getActiveGameName ());
	}
	
	@Override
	public void prepareFormation (StartFormationAction aStartFormationAction) {

	}
	
	@Override
	public void showFormationFrame () {
		formationFrame.showFrame ();
	}
	
	@Override
	public void rebuildFormationPanel () {
		int tCurrentPlayerIndex;
		
		tCurrentPlayerIndex = currentPlayerIndex;
		if (tCurrentPlayerIndex >= 0) {
			rebuildFormationPanel (tCurrentPlayerIndex);
		}

	}

}
