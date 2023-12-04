package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.XMLNode;

public class ClearExchangePrezShareEffect extends ExchangePrezShareEffect {
	public final static String NAME = "Clear Exchange President Share";
	final static String AN_CLEAR_EXCHANGE_PREZ = "ClearExchangedPrez";

	public ClearExchangePrezShareEffect () {
		super ();
		setName (NAME);
	}

	public ClearExchangePrezShareEffect (ActorI aToActor, String aCorporationAbbrev) {
		super (NAME, aToActor, aCorporationAbbrev);
	}

	public ClearExchangePrezShareEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Player tPlayer;

		tEffectApplied = false;
		tPlayer = (Player) actor;
		tPlayer.setExchangedPrezShare (null);
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;

		tEffectUndone = false;
		tPlayer = (Player) actor;
		tPlayer.setExchangedPrezShare (corporationAbbrev);
		tEffectUndone = true;

		return tEffectUndone;
	}
}