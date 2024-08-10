package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.StockRound;
import ge18xx.round.action.ActorI;
import geUtilities.xml.XMLNode;

public class NewCurrentPlayerEffect extends ChangePlayerEffect {
	public final static String NAME = "Change Current Player";

	public NewCurrentPlayerEffect (ActorI aActor, int aPreviousPlayer, int aNewPlayer) {
		super (aActor, aPreviousPlayer, aNewPlayer);
		setName (NAME);
	}

	public NewCurrentPlayerEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		StockRound tStockRound;

		tEffectUndone = true;
		tStockRound = aRoundManager.getStockRound ();
		tStockRound.setCurrentPlayerIndex (newPlayerIndex);

		return tEffectUndone;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		StockRound tStockRound;
		Player tPlayer;

		tEffectUndone = true;
		tStockRound = aRoundManager.getStockRound ();
		tStockRound.setCurrentPlayerIndex (previousPlayerIndex);
		tPlayer = tStockRound.getCurrentPlayer ();
		tPlayer.showPlayerFrame ();

		return tEffectUndone;
	}
}
