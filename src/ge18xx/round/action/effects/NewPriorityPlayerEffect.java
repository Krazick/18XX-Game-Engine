package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.StockRound;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

public class NewPriorityPlayerEffect extends ChangePlayerEffect {
	public final static String NAME = "Change Priority Player";
	
	public NewPriorityPlayerEffect (ActorI aActor, int aPreviousPlayer, int aNewPlayer) {
		super (aActor, aPreviousPlayer, aNewPlayer);
		setName (NAME);
	}

	public NewPriorityPlayerEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPreviousPlayer;
		Player tNewPlayer;
		StockRound tStockRound;
		
		tEffectApplied = true;
		tStockRound = aRoundManager.getStockRound ();
		tPreviousPlayer = tStockRound.getPlayerAtIndex (previousPlayerIndex);
		tNewPlayer = tStockRound.getPlayerAtIndex (newPlayerIndex);
		tStockRound.setPriorityPlayer (newPlayerIndex);
		tStockRound.updateRFPlayerLabel (tPreviousPlayer);
		tStockRound.updateRFPlayerLabel (tNewPlayer);
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPreviousPlayer;
		Player tNewPlayer;
		StockRound tStockRound;
		
		tEffectUndone = true;
		tStockRound = aRoundManager.getStockRound ();
		tPreviousPlayer = tStockRound.getPlayerAtIndex (previousPlayerIndex);
		tNewPlayer = tStockRound.getPlayerAtIndex (newPlayerIndex);
		tStockRound.setPriorityPlayer (previousPlayerIndex);
		tStockRound.updateRFPlayerLabel (tPreviousPlayer);
		tStockRound.updateRFPlayerLabel (tNewPlayer);
		
		return tEffectUndone;
	}
}
