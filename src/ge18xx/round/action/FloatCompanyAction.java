package ge18xx.round.action;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ReachedDestinationEffect;
import geUtilities.XMLNode;

public class FloatCompanyAction extends CashTransferAction {
	public final static String NAME = "Float Company";

	public FloatCompanyAction () {
		super (NAME);
	}

	public FloatCompanyAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public FloatCompanyAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport;

		tSimpleActionReport = actor.getName () + " was floated and obtained " + Bank.formatCash (getCashAmount ())
				+ " in starting Capital from the Bank.";

		return tSimpleActionReport;
	}
	
	public void addReachedDestinationEffect (ActorI aActor, boolean aReached, int aOldCapitalizationLevel, 
			int aNewCapitalizationLevel) {
		ReachedDestinationEffect tReachedDestinationEffect;
	
		if (actor.isACorporation ()) {
			tReachedDestinationEffect = new ReachedDestinationEffect (aActor, aReached, 
					aOldCapitalizationLevel, aNewCapitalizationLevel);
			addEffect (tReachedDestinationEffect);
		}
	}

	
}
