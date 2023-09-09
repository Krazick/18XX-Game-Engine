package ge18xx.round.action;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.SetRepaymentFinishedEffect;
import ge18xx.utilities.XMLNode;

public class RepaymentHandledAction extends ChangeStateAction {
	public final static String NAME = "RepayementHandledAction";

	public RepaymentHandledAction () {
		this (NAME);
	}

	public RepaymentHandledAction (String aName) {
		super (aName);
	}

	public RepaymentHandledAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public RepaymentHandledAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	public void addSetRepaymentHandledEffect (ActorI aActor, boolean aRepaymentHandled) {
		SetRepaymentFinishedEffect tSetRepaymentHandledEffect;
		
		if (actor.isACorporation ()) {
			tSetRepaymentHandledEffect = new SetRepaymentFinishedEffect (aActor, aRepaymentHandled);
			addEffect (tSetRepaymentHandledEffect);
		}
	}

	@Override
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " has handled Loan Repayment.";

		return tSimpleActionReport;
	}
}
