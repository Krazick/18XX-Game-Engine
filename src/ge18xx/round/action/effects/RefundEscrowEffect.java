package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.XMLNode;

public class RefundEscrowEffect extends CashTransferEffect {
	public final static String NAME = "Refund Escrow";

	public RefundEscrowEffect () {
		super ();
		setName (NAME);
	}

	public RefundEscrowEffect (ActorI aFromActor, ActorI aToActor, int aCashAmount) {
		super (aFromActor, aToActor, aCashAmount);
		setName (NAME);
	}

	public RefundEscrowEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;

		tEffectApplied = super.applyEffect (aRoundManager);

		return tEffectApplied;
	}
}