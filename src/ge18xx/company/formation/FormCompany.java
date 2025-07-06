package ge18xx.company.formation;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;

public class FormCompany extends TriggerClass {
	GameManager gameManager;
	protected int currentPlayerIndex;
	Corporation operatingCompany;
	Corporation triggeringCompany;
	
	public FormCompany () {
		
	}
	
	public FormCompany (GameManager aGameManager) {
		gameManager = aGameManager;

	}

	@Override
	public void setCurrentPlayerIndex (int aCurrentPlayerIndex) {
		currentPlayerIndex = aCurrentPlayerIndex;
	}

	public int getCurrentPlayerIndex () {
		return currentPlayerIndex;
	}

	public Player getCurrentPlayer () {
		Player tCurrentPlayer;
		PlayerManager tPlayerManager;
		
		tPlayerManager = gameManager.getPlayerManager ();
		tCurrentPlayer = tPlayerManager.getPlayer (currentPlayerIndex);
	
		return tCurrentPlayer;
	}
	
	@Override
	public void setTriggeringCompany (Corporation aTriggeringCompany) {
		triggeringCompany = aTriggeringCompany;
	}
	
	public Corporation getTriggeringCompany (Corporation aTriggeringCompany) {
		return triggeringCompany;
	}

}
