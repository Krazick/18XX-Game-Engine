package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.XMLNode;

public class CashInfoEffect extends CashTransferEffect {
	public final static String NAME = "Cash Info";

	public CashInfoEffect () {
		this (NAME);
	}

	public CashInfoEffect (String aName) {
		super (aName);
	}

	public CashInfoEffect (ActorI aFromActor, ActorI aToActor, int aCashAmount) {
		super (aFromActor, aToActor, aCashAmount);
		setName (NAME);
	}

	public CashInfoEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	// Don't need to actually Apply or Undo this because this is more just to Hold
	// the Cash and Target Actor for the Proposed offer.
	// Therefore override the CashTransfer Apply and Undo Effect Methods

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		return true;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		return true;
	}
}
