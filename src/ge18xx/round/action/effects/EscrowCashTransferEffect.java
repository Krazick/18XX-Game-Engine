package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.XMLNode;

public class EscrowCashTransferEffect extends CashTransferEffect {
	public final static String NAME = "Escrow Cash Transfer";

	public EscrowCashTransferEffect () {
		super ();
		setName (NAME);
	}

	public EscrowCashTransferEffect (ActorI aFromActor, ActorI aToActor, int aCashAmount) {
		super (aFromActor, aToActor, aCashAmount);
		setName (NAME);
	}

	public EscrowCashTransferEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;

		// The creation of the Escrow completes the Cash Transfer.
		// Therefore there is no need to have a Network Game apply this effect
		// To the receiving client
		tEffectApplied = true;

		return tEffectApplied;
	}
}
