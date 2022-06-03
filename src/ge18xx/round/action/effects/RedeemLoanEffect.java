package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.XMLNode;

/**
 * The RedeemLoanEffect class does the same as the GetLoanEffect Class, but in reverse (a Loan is Redeemed, AKA Paid Back)
 * There is no limit on how many loans can be paid back per OR, so no change to the LoanTaken Flag. Other than the name of the Effect
 * being set, there is nothing else for this Effect to do, or undo.
 *
 */
public class RedeemLoanEffect extends GetLoanEffect {
	public final static String NAME = "Redeem Loan";

	public RedeemLoanEffect () {
		super (NAME);
	}

	public RedeemLoanEffect (ActorI aActor) {
		super (NAME, aActor);
	}

	public RedeemLoanEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		return true;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		return true;
	}
}
