package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.effects.ApplyDiscountEffect;
import ge18xx.utilities.XMLNode;

public class PassAction extends ChangeStateAction {
	public final static String NAME = "Pass";

	public PassAction () {
		super ();
		setName (NAME);
	}

	public PassAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public PassAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addApplyDiscountEffect (ActorI aPlayer, String aCertificateName, int aOldDiscount, int aNewDiscount) {
		ApplyDiscountEffect tApplyDiscountEffect;

		tApplyDiscountEffect = new ApplyDiscountEffect (aPlayer, aCertificateName, aOldDiscount, aNewDiscount);
		addEffect (tApplyDiscountEffect);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " Passed.";

		return tSimpleActionReport;
	}
}