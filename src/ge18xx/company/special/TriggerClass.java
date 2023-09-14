package ge18xx.company.special;

import java.util.List;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.action.BuyTrainAction;

public class TriggerClass {
	public static final TriggerClass NO_TRIGGER_CLASS = null;
	
	public TriggerClass () {
		
	}
	
	public TriggerClass (GameManager aGameManager, BuyTrainAction aBuyTrainAction) {
		
	}

	public void rebuildSpecialPanel (Player aActingPlayer) {
	
	}
	
	public void hideSpecialPanel () {
		
	}
	
	public boolean updateToNextPlayer (List<Player> aPlayers) {
		return false;
	}
}
